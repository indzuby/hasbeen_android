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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import co.hasBeen.geolocation.GeoFourSquare;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Place;
import co.hasBeen.model.api.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Util;

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
    public int photoCount;
    public ItemModule(Context context) throws SQLException {
        mContext = context;
        database = new DatabaseHelper(context);
        resolver = context.getContentResolver();
    }

    public void insertDay() throws Exception {
        int[] idx = new int[proj.length];
        Day lastDay = database.selectLastDay();
        Long lastDayTime = 0L;
        Day day = lastDay;
        int photoCount = 0;
        if (lastDay != null) {
            photoCount = day.getPhotoCount();
            lastDayTime = lastDay.getDate();
        }
        cursor = MediaStore.Images.Media.query(resolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                proj,
                MediaStore.Images.Media.DATE_TAKEN + ">?",
                new String[]{"" + lastDayTime},
                MediaStore.Images.Media.DATE_TAKEN);
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < idx.length; i++)
                idx[i] = cursor.getColumnIndex(proj[i]);
            do {
                long photoID = cursor.getInt(idx[0]);
                String photoPath = cursor.getString(idx[1]);
                String displayName = cursor.getString(idx[2]);
                String format = cursor.getString(idx[4]);
                float lat = cursor.getFloat(idx[5]);
                float lon = cursor.getFloat(idx[6]);
                if (lat == 0 || lon == 0) continue;
                if (isNotJpg(format)) continue;
                long dataTaken = Util.getDateTime(photoPath);
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
            day.setPhotoCount(photoCount);
            day.setDate(lastDayTime);
            database.updateDay(day);
        }
    }

    public List<Day> bringTenDay(Long date) throws Exception {
        insertDay();
        List<Day> days = database.selectBeforeTenDay(date);
        if(days!=null)
            for(Day day : days)
                if(day.getMainPlaceId()==null)
                    insertNewPlace(day);
        return days;
    }
    protected void insertNewPlace(final Day day) throws Exception{
        final Place place = new Place();
        final Photo photo = database.selectPhoto(day.getMainPhotoId());
        new GeoFourSquare(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg.what == 0) {
                        Place newPlace = (Place) msg.obj;
                        insertNewPosition(newPlace,photo);
                        day.setMainPlaceId(newPlace.getId());
                        database.updateDayMainPlaceId(day.getId(),newPlace.getId());
                    }else {
                        throw new NullPointerException();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }). execute(photo,place);
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

    public Photo makePhotoData(float lat, float lon, Long dataTaken, Long photoId, String photoPath) throws Exception{
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
        },day).execute(photo, place);
    }

    public void insertNewPosition(Place place, Photo photo) throws SQLException {
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
        database.updatePhoto(photo);

    }


    public List<Position> getPhotosByDate(Long dayId) throws Exception {
        Day day = database.selectDay(dayId);
        photoCount = 0 ;
        insertPhotos(day.getDate(), dayId);
        insertPosition(dayId);
        day.setCreatedTime(new Date().getTime());
        day.setPhotoCount(database.countPhotoByDayid(dayId));
        database.updateDay(day);
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
                MediaStore.Images.Media.DATE_TAKEN);
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < idx.length; i++)
                idx[i] = cursor.getColumnIndex(proj[i]);
            do {
                long photoID = cursor.getInt(idx[0]);
                String photoPath = cursor.getString(idx[1]);
                String displayName = cursor.getString(idx[2]);
                String format = cursor.getString(idx[4]);
                float lat = cursor.getFloat(idx[5]);
                float lon = cursor.getFloat(idx[6]);
                if (lat == 0 || lon == 0) continue;
                if (isNotJpg(format)) continue;
                long dataTaken = Util.getDateTime(photoPath);
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
    public void insertPosition(Long dayId) throws Exception {
        List<Photo> photoList = database.selectPhotosByDayId(dayId);
        Iterator iterator = photoList.iterator();
        while (iterator.hasNext()) {
            if (!isRun) continue;
            final Photo photo = (Photo) iterator.next();
            if (photo.getPositionId() != null) {
                photoCount++;
                bestPhoto = photo;
                continue;
            }
            if(bestPhoto!=null){
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
                                isRun = true;
                                photoCount++;
                            }else {
                                throw new NullPointerException();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }). execute(photo,place);
            }
        }
        while(!isRun);
    }
    public boolean isSamePlace(Place aPlace, Place bPlace) {
        if (aPlace.getVenueId()!=null && bPlace.getVenueId()!=null && aPlace.getVenueId().equals(bPlace.getVenueId()))
            return true;
        return false;
    }
    public Bitmap getThumbnail(long id) {
        return MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
    }
}
