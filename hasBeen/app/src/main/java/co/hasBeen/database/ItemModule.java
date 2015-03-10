package co.hasBeen.database;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import co.hasBeen.geolocation.GeoFourSquare;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Place;
import co.hasBeen.model.api.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.HasBeenOpenCv;

/**
 * Created by zuby on 2015-01-26.
 */
public class ItemModule {
    Context mContext;
    DatabaseHelper database;
    ContentResolver resolver;
    Cursor cursor;
    String[] proj = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
    };

    public ItemModule(Context context) throws SQLException {
        mContext = context;
        database = new DatabaseHelper(context);
        resolver = context.getContentResolver();
    }

    public void insertDay() throws SQLException {
        int[] idx = new int[proj.length];
        Day lastDay = database.selectLastDay();
        Long lastDayTime = 0L;
        Day day = lastDay;
        int photoCount = 0;
        if (lastDay != null) {
            photoCount = day.getPhotoCount();
            lastDayTime = lastDay.getDate();
            String date = new Date(lastDayTime).toString();
        }
        cursor = MediaStore.Images.Media.query(resolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                proj,
                MediaStore.Images.Media.DATE_TAKEN + ">?",
                new String[]{"" + lastDayTime},
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
                if (isNotJpg(format)) continue;
                if (displayName != null && !HasBeenDate.isSameDate(lastDayTime, dataTaken)) {
                    Photo photo = makePhotoData(lat, lon, dataTaken, photoID, photoPath);
                    Long photoId = database.insertPhoto(photo);
                    photo.setId(photoId);
                    if (day != null) {
                        day.setPhotoCount(photoCount);
                        database.updateDay(day);
                    }
                    day = makeDayData(photo);
                    photoCount = 1;
                    Long dayId = database.insertDay(day);
                    photo.setDayId(dayId);
                    database.updatePhoto(photo);
                    lastDayTime = dataTaken;
                } else if (HasBeenDate.isSameDate(lastDayTime, dataTaken)) {
                    photoCount++;
                    lastDayTime = dataTaken;
                }
            } while (cursor.moveToNext());
        }
        day.setPhotoCount(photoCount);
        day.setDate(lastDayTime);
        database.updateDay(day);
    }

    public List<Day> bringTenDay(Long date) throws SQLException {
        insertDay();
        return database.selectBeforeTenDay(date);
    }

    public Day makeDayData(Photo photo) {
        Day day = new Day();
        day.setTitle("");
        day.setCountry("");
        day.setCity("");
        day.setDescription("");
        day.setPhotoCount(1);
        day.setDate(photo.getTakenTime());
        day.setMainPhotoId(photo.getId());
        return day;
    }

    public Photo makePhotoData(float lat, float lon, Long dataTaken, Long photoId, String photoPath) {
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
        photo.setPhotoId(photoId);
        photo.setPhotoPath(photoPath);
        return photo;
    }

    public boolean isNotJpg(String format) {
        if (!format.endsWith("jpg") && !format.endsWith("jpeg")) return true;
        return false;
    }
    class PlaceName implements Runnable {
        TextView placeName;
        Day day;
        PlaceName(TextView placeName, Day day) {
            this.placeName = placeName;
            this.day = day;
        }
        @Override
        public void run() {
            while(day.getMainPlace()==null);
            placeName.setText(day.getMainPlace().getName());
            if (day.getMainPlace().getName().length() <= 0)
                placeName.setVisibility(View.INVISIBLE);
            else
                placeName.setVisibility(View.VISIBLE);
        }
    }
    public void getPlace(Day day ,final TextView placeName) throws Exception {
        Photo photo = database.selectPhoto(day.getMainPhotoId());
        day.setMainPhoto(photo);
        Place place = new Place();
        day.setMainPlace(place);
        new GeoFourSquare(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg.what == 0) {
                        Day day = (Day) msg.obj;
                        insertNewPosition(day.getMainPlace(), day.getMainPhoto());
                        day.setMainPlaceId(day.getMainPlace().getId());
                        database.updateDay(day);
                        ((Activity) mContext).runOnUiThread(new PlaceName(placeName, day));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },day).execute(photo.getLat(), photo.getLon(), place, 1);
    }

    protected void insertNewPosition(Place place, Photo photo) throws SQLException {
        Position position = new Position();
        Long placeId;
        Long positionId;
        if (database.hasVenueId(place.getVenueId()))
            placeId = database.getPlaceIdByVenueId(place.getVenueId());
        else
            placeId = database.insertPlace(place);
        place.setId(placeId);

        position.setDayId(photo.getDayId());
        position.setStartTime(photo.getTakenTime());
        position.setEndTime(photo.getTakenTime());
        position.setMainPhotoId(photo.getId());
        position.setType("PLACE");
        position.setPlaceId(placeId);
        position.setCategoryIconPrefix(place.getCategoryIconPrefix());
        position.setCategoryIconSuffix(place.getCategoryIconSuffix());
        positionId = database.insertPosition(position);
        photo.setPlaceId(placeId);
        photo.setPositionId(positionId);
        photo.setClearestId(photo.getId());
        photo.setEdgeCount(HasBeenOpenCv.detectEdge(getThumbnail(photo.getPhotoId())));
        database.updatePhoto(photo);

    }


    public List<Position> getPhotosByDate(Long dayId) throws Exception {
        Day day = database.selectDay(dayId);
        if (day.getCreatedTime()==null) {
            insertPhotos(day.getDate(), dayId);
            insertPosition(dayId);
            day.setCreatedTime(new Date().getTime());
            day.setPhotoCount(database.countPhotoByDayid(dayId));
            database.updateDay(day);
        }
        return database.selectPositionByDayId(dayId);
    }

    public void insertPhotos(Long date, Long dayId) throws Exception {
        int[] idx = new int[proj.length];
        Long startDayTime = HasBeenDate.getBeforeDay(date, 1);
        Long endDayTime = HasBeenDate.getBeforeDay(date, -1);
        cursor = MediaStore.Images.Media.query(resolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                proj,
                MediaStore.Images.Media.DATE_TAKEN + ">? AND " + MediaStore.Images.Media.DATE_TAKEN + "<?",
                new String[]{"" + startDayTime, "" + endDayTime},
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
                if (isNotJpg(format)) continue;
                if (displayName != null && HasBeenDate.isSameDate(date, dataTaken)) {
                    if (database.hasPhotoId(photoID)) continue;
                    Photo photo = new Photo();
                    photo.setTitle("");
                    photo.setCity("");
                    photo.setCountry("");
                    photo.setDescription("");
                    photo.setPlaceName("");
                    photo.setLat(lat);
                    photo.setLon(lon);
                    photo.setTakenTime(dataTaken);
                    photo.setDayId(dayId);
                    photo.setPhotoId(new Long(photoID));
                    photo.setPhotoPath(photoPath);
                    database.insertPhoto(photo);
                }
            } while (cursor.moveToNext());

        }
    }

    boolean isRun=true;
    Photo bestPhoto = null;
    List<Photo> similaryPhotos ;
    public void insertPosition(Long dayId) throws Exception {
        List<Photo> photoList = database.selectPhotosByDayId(dayId);
        similaryPhotos = new ArrayList<>();
        int photoCount = 0;
        Iterator iterator = photoList.iterator();
        while (iterator.hasNext()) {
            if (!isRun) continue;
            final Photo photo = (Photo) iterator.next();
            if (photo.getEdgeCount() != null) {
                if (photo.getId() == photo.getClearestId())
                    bestPhoto = photo;
                continue;
            }
            Integer edge = photo.getEdgeCount();
            if (edge == null) {
                edge = HasBeenOpenCv.detectEdge(getThumbnail(photo.getPhotoId()));
                photo.setEdgeCount(edge);
            }
            if (bestPhoto != null && isSimilary(bestPhoto, photo)) {
                if (isMaxEdge(edge, bestPhoto.getEdgeCount())) {
                    similaryPhotos.add(bestPhoto);
                    photo.setPlaceName(bestPhoto.getPlaceName());
                    photo.setPositionId(bestPhoto.getPositionId());
                    photo.setPlaceId(bestPhoto.getPlaceId());
                    photo.setFourSquare(bestPhoto.getVenueId(), bestPhoto.getCategoryId(), bestPhoto.getCategryName(), bestPhoto.getCategoryIconPrefix(), bestPhoto.getCategoryIconSuffix());
                    bestPhoto = photo;
                    similaryPhotos.add(photo);
                } else {
                    similaryPhotos.add(photo);
                }
            } else if(bestPhoto!=null){
                updateClearestId(similaryPhotos, bestPhoto);
                similaryPhotos = new ArrayList<Photo>();
                isRun = false;
                Place place = new Place();
                new GeoFourSquare(new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        try {
                            if (msg.what == 0) {
                                Place newPlace = (Place) msg.obj;
//                                                Log.i("place_name",updatedPhoto.getPlaceName());
                                Place bestPlace = database.selectPlace(bestPhoto.getPlaceId());
                                if (isSamePlace(newPlace, bestPlace)) {
                                    photo.setPlaceId(bestPhoto.getPlaceId());
                                    photo.setPositionId(bestPhoto.getPositionId());
                                    database.updatePhoto(photo);
                                    database.updatePositionEndTime(photo.getPositionId(), photo.getTakenTime());
                                } else {
                                    insertNewPosition(newPlace,photo);
                                }
                                bestPhoto = photo;
                                similaryPhotos.add(photo);
                                isRun = true;
                            }else {
                                throw new NullPointerException();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }). execute(photo.getLat(),photo.getLon(),place, 1);
            }else
                bestPhoto = photo;
        }
        updateClearestId(similaryPhotos, bestPhoto);

    }
    protected boolean isSamePlace(Place aPlace, Place bPlace) {
        if (aPlace.getVenueId().equals(bPlace.getVenueId()))
            return true;
        return false;
    }
    protected Bitmap getThumbnail(long id) {
        return MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
    }

    protected boolean isSimilary(Photo beforePhoto, Photo photo) {
        if (HasBeenDate.isTimeRangeInFive(beforePhoto.getTakenTime(), photo.getTakenTime()))
            return true;

        double hist = HasBeenOpenCv.compareHistogram(getThumbnail(beforePhoto.getPhotoId()), getThumbnail(photo.getPhotoId()));
        if (hist >= 0.9)
            return true;
        return false;
    }

    protected boolean isMaxEdge(int edge, int compareEdge) {
        if (edge > compareEdge) return true;
        return false;
    }

    protected void updateClearestId(List<Photo> photos, Photo bestPhoto) throws SQLException {
        if (photos.size() > 0) {
            for(Photo badPhoto : photos){
                badPhoto.setClearestId(bestPhoto.getId());
                badPhoto.setPlaceName(bestPhoto.getPlaceName());
                badPhoto.setPositionId(bestPhoto.getPositionId());
                badPhoto.setPlaceId(bestPhoto.getPlaceId());
                database.updatePhoto(badPhoto);
            }
            if (database.hasMainPhotoIdInPosition(bestPhoto.getPositionId()))
                database.updatePositionMainPhotoId(bestPhoto.getPositionId(), bestPhoto.getId());
            database.updatePositionEndTime(bestPhoto.getPositionId(), photos.get(photos.size() - 1).getTakenTime());
        }

    }
}
