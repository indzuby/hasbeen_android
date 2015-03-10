package co.hasBeen.database;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import co.hasBeen.geolocation.GeoFourSquare;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Place;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.HasBeenOpenCv;

/**
 * Created by zuby on 2015-01-26.
 */
public class CreateDataBase {

    final static int SUCCESS = 0;
    final static int FAILED = -1;
    Context mContext;
    List<Photo> mPhotoByDB;
    DatabaseHelper database;
    ContentResolver resolver;
    boolean flag = true;
    Cursor cursor;
    List<Photo> mPhotoList;
    Photo lastPhoto;

    public CreateDataBase(Context context) throws SQLException {
        mContext = context;
        database = new DatabaseHelper(context);
        resolver = context.getContentResolver();
        mPhotoList = new ArrayList<>();
        lastPhoto = database.getLastPhoto();

    }

    public void takePhoto() {
        String[] proj = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
        };
        final int[] idx = new int[proj.length];
        if (lastPhoto != null)
            cursor = MediaStore.Images.Media.query(resolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj,
                    MediaStore.Images.Media.DATE_TAKEN + ">?",
                    new String[]{"" + lastPhoto.getTakenTime()},
                    MediaStore.MediaColumns.DATE_ADDED);
        else
            cursor = MediaStore.Images.Media.query(resolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj,
                    null,
                    MediaStore.MediaColumns.DATE_ADDED);

        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < idx.length; i++)
                idx[i] = cursor.getColumnIndex(proj[i]);

            do {

                long photoID = cursor.getInt(idx[0]);
                String photoPath = cursor.getString(idx[1]);
                String displayName = cursor.getString(idx[2]);
                long dataTaken = cursor.getLong(idx[3]);
                String format = cursor.getString(idx[4]);
                float lat = cursor.getFloat(idx[5]);
                float lon = cursor.getFloat(idx[6]);
                if (lat == 0 && lon == 0) continue;
                try {
                    if (database.hasPhotoId(photoID)) continue;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isNotJpg(format)) continue;

                if (displayName != null) {
                    Photo photo = new Photo();
                    photo.setTitle("");
                    photo.setCity("");
                    photo.setCountry("");
                    photo.setDescription("");
                    photo.setPlaceName("");
                    photo.setLat(lat);
                    photo.setLon(lon);
                    photo.setTakenTime(dataTaken);
                    photo.setDayId(new Long(0));
                    photo.setPhotoId(new Long(photoID));
                    photo.setPhotoPath(photoPath);
                    mPhotoList.add(photo);
//                    Log.i("Photo Date", photo.getTakenDate().toString());
                }

            } while (cursor.moveToNext());
            insertDB();
        }
    }

    public boolean isNotJpg(String format) {
        if (!format.endsWith("jpg") && !format.endsWith("jpeg")) return true;
        return false;
    }

    public void insertDB() {
        Iterator iterator = mPhotoList.iterator();
        Day day;
        try {
            Photo beforePhoto = database.getLastPhoto();
            while (iterator.hasNext()) {
                Photo photo = (Photo) iterator.next();
                Long id = database.insertPhoto(photo);
                photo.setClearestId(id);
                if (beforePhoto != null && HasBeenDate.isSameDate(beforePhoto, photo)) {
                    photo.setDayId(beforePhoto.getDayId());
                    database.increasePhotoCount(beforePhoto.getDayId());
                } else {
                    day = new Day();
                    day.setTitle("");
                    day.setDescription("");
                    day.setPhotoCount(1);
                    day.setDate(photo.getTakenTime());
                    day.setCountry("");
                    day.setCity("");
                    Long dayId = database.insertDay(day);
                    photo.setDayId(dayId);
                    beforePhoto = photo;
                    Log.i("Day id", dayId + "");
                }
                database.updatePhoto(photo);
//                Log.i("Photo Date",photo.getTakenDate().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Photo bestPhoto = null;
    ArrayList<Photo> photos = null;

    public void buildDay() {
        try {
            mPhotoByDB = database.selectAllPhoto();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterator iterator = mPhotoByDB.iterator();
        try {
            Photo lastPhoto = database.getLastPhoto();
            bestPhoto = null;
            photos = new ArrayList<Photo>();
            flag = true;
            while (iterator.hasNext()) {
                if (!flag) continue;
                final Photo photo = (Photo) iterator.next();
                if (photo.getEdgeCount() != null) {
                    if (photo.getId() == photo.getClearestId())
                        bestPhoto = photo;
                    continue;
                }
                if (true || HasBeenDate.isDateRangeInThree(photo, lastPhoto)) {
                    Integer edge = photo.getEdgeCount();
                    if (edge == null) {
                        edge = HasBeenOpenCv.detectEdge(getThumbnail(photo.getPhotoId()));
                        photo.setEdgeCount(edge);
                    }
                    if (bestPhoto != null && HasBeenDate.isSameDate(bestPhoto, photo)) {
                        if (isSimilary(bestPhoto, photo)) {
                            if (isMaxEdge(edge, bestPhoto)) {
                                photos.add(bestPhoto);
                                photo.setPlaceName(bestPhoto.getPlaceName());
                                photo.setPositionId(bestPhoto.getPositionId());
                                photo.setPlaceId(bestPhoto.getPlaceId());
                                photo.setFourSquare(bestPhoto.getVenueId(), bestPhoto.getCategoryId(), bestPhoto.getCategryName(), bestPhoto.getCategoryIconPrefix(), bestPhoto.getCategoryIconSuffix());
                                bestPhoto = photo;
                                photos.add(photo);
                            } else {
                                photos.add(photo);
                            }
                        } else {
                            updateClearestId(photos, bestPhoto);
                            photos = new ArrayList<Photo>();
                            flag = false;
                            new GeoFourSquare(new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(Message msg) {
                                    try {
                                        switch (msg.what) {
                                            case FAILED: // failed

                                                break;
                                            case SUCCESS: // success
                                                Photo updatedPhoto = (Photo) msg.obj;
//                                                Log.i("place_name",updatedPhoto.getPlaceName());
                                                if (isSamePlace(updatedPhoto, bestPhoto)) {
                                                    updatedPhoto.setPlaceId(bestPhoto.getPlaceId());
                                                    updatedPhoto.setPositionId(bestPhoto.getPositionId());
                                                    database.updatePhoto(updatedPhoto);
                                                    database.updatePositionEndTime(updatedPhoto.getPositionId(), updatedPhoto.getTakenTime());
                                                } else {
                                                    insertNewPosition(updatedPhoto);
                                                }
                                                bestPhoto = updatedPhoto;
                                                photos.add(updatedPhoto);
                                                break;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    flag = true;
                                }
                            }).execute(photo.getLat(), photo.getLon(), photo, 1);

                        }
                    } else {
                        flag = false;
                        updateClearestId(photos, bestPhoto);
                        photos = new ArrayList<Photo>();
                        new GeoFourSquare(new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                try {
                                    switch (msg.what) {
                                        case FAILED: // failed
                                            break;
                                        case SUCCESS: // success
                                            Photo updatedPhoto = (Photo) msg.obj;
                                            insertNewPosition(updatedPhoto);
                                            bestPhoto = updatedPhoto;
                                            photos.add(updatedPhoto);
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                flag = true;
                            }
                        }).execute(photo.getLat(), photo.getLon(), photo, 1);
                    }
                }
            }
            updateClearestId(photos, bestPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    public HasBeenPlace(String venue_id, String category_id, String category_name, String country, String city, String name, float lat, float lon, String categoryIconPrefix,String categoryIconSuffix) {
    protected void insertNewPosition(Photo photo) throws SQLException {
        Place place = new Place();
        Position position = new Position();
        Long placeId;
        Long positionId;
        if (database.hasVenueId(photo.getVenueId())) {
            placeId = database.getPlaceIdByVenueId(photo.getVenueId());
        } else {
            place.setVenueId(photo.getVenueId());
            place.setCategoryId(photo.getCategoryId());
            place.setCategoryName(photo.getCategryName());
            place.setCountry(photo.getCountry());
            place.setCity(photo.getCity());
            place.setName(photo.getPlaceName());
            place.setLat(photo.getLat());
            place.setLon(photo.getLon());
            place.setCategoryIconPrefix(photo.getCategoryIconPrefix());
            place.setCategoryIconSuffix(photo.getCategoryIconSuffix());
            place.setMainPhotoId(photo.getId());
            placeId = database.insertPlace(place);
        }
        position.setDayId(photo.getDayId());
        position.setStartTime(photo.getTakenTime());
        position.setEndTime(photo.getTakenTime());
        position.setMainPhotoId(photo.getId());
        position.setType("PLACE");
        position.setPlaceId(placeId);
        position.setCategoryIconPrefix(photo.getCategoryIconPrefix());
        position.setCategoryIconSuffix(photo.getCategoryIconSuffix());
        positionId = database.insertPosition(position);

        photo.setPlaceId(placeId);
        photo.setPositionId(positionId);
        database.updatePhoto(photo);
    }

    protected boolean isSamePlace(Photo aPhoto, Photo bPhoto) {
        if (aPhoto.getVenueId().equals(bPhoto.getVenueId()))
            return true;
        return false;
    }

    protected void updateClearestId(List<Photo> photos, Photo bestPhoto) throws SQLException {
        if (photos.size() > 0) {
            Iterator photosIter = photos.iterator();
            while (photosIter.hasNext()) {
                Photo badPhoto = (Photo) photosIter.next();

                badPhoto.setClearestId(bestPhoto.getId());
                badPhoto.setPlaceName(bestPhoto.getPlaceName());
                badPhoto.setPositionId(bestPhoto.getPositionId());
                badPhoto.setPlaceId(bestPhoto.getPlaceId());
                badPhoto.setCity(bestPhoto.getCity());
                badPhoto.setCountry(bestPhoto.getCountry());
                badPhoto.setFourSquare(bestPhoto.getVenueId(), bestPhoto.getCategoryId(), bestPhoto.getCategryName(), bestPhoto.getCategoryIconPrefix(), bestPhoto.getCategoryIconSuffix());
                database.updatePhoto(badPhoto);
            }
            if (database.hasMainPhotoIdInDay(bestPhoto.getDayId()))
                database.updateDayMainPhotoId(bestPhoto.getDayId(), bestPhoto.getId());
            if (database.hasMainPhotoIdInPosition(bestPhoto.getPositionId()))
                database.updatePositionMainPhotoId(bestPhoto.getPositionId(), bestPhoto.getId());
            database.updatePositionEndTime(bestPhoto.getPositionId(), photos.get(photos.size() - 1).getTakenTime());
        }

    }

    protected boolean isSimilary(Photo beforePhoto, Photo photo) {
        if (HasBeenDate.isTimeRangeInFive(beforePhoto.getTakenTime(), photo.getTakenTime()))
            return true;

        double hist = HasBeenOpenCv.compareHistogram(getThumbnail(beforePhoto.getPhotoId()), getThumbnail(photo.getPhotoId()));
        if (hist >= 0.8)
            return true;
        return false;
    }

    protected boolean isMaxEdge(int edge, Photo photo) {
        if (photo == null) return true;
        if (edge > photo.getEdgeCount()) return true;
        return false;
    }


    protected Bitmap getThumbnail(long id) {
        return MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
    }
}
