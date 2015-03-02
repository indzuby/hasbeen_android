package co.hasBeen.database;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import co.hasBeen.geolocation.GeoFourSquare;
import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Photo;
import co.hasBeen.model.database.Place;
import co.hasBeen.model.database.Position;
import co.hasBeen.utils.HasBeenDate;

/**
 * Created by zuby on 2015-01-26.
 */
public class ItemModule {
    Context mContext;
    DatabaseHelper database;
    ContentResolver resolver;
    Cursor cursor;
    public ItemModule(Context context) throws SQLException{
        mContext = context;
        database = new DatabaseHelper(context);
        resolver = context.getContentResolver();
    }
    public void insertDay() throws SQLException{
        String[] proj = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
        };
        int[] idx = new int[proj.length];
        Day lastDay = database.selectLastDay();
        Long lastDayTime =0L;
        Day day = null;
        int photoCount = 0 ;
        if(lastDay != null) {
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
                if (displayName != null && !HasBeenDate.isSameDate(lastDayTime,dataTaken)) {
                    Photo photo = makePhotoData(lat,lon,dataTaken,photoID,photoPath);
                    Long photoId = database.insertPhoto(photo);
                    photo.setId(photoId);
                    if(day!=null) {
                        day.setPhotoCount(photoCount);
                        database.updateDay(day);
                    }
                    day = makeDayData(photo);
                    photoCount = 1;
                    database.insertDay(day);
                    lastDayTime = dataTaken;
                }else if(HasBeenDate.isSameDate(lastDayTime,dataTaken)) {
                    photoCount++;
                }

            } while (cursor.moveToNext());
        }
    }
    public List<Day> bringTenDay(Long date) throws SQLException{
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
    public void getPlace(final Day day, final TextView placeName) throws Exception{
        Photo photo = database.selectPhoto(day.getMainPhotoId());
        day.setMainPhoto(photo);
        final Place place = new Place();
        day.setMainPlace(place);
        new GeoFourSquare(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if(msg.what==0){
                        insertNewPosition(place,day);
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                placeName.setText(place.getName());
                                if(place.getName().length()<=0)
                                    placeName.setVisibility(View.INVISIBLE);
                                else
                                    placeName.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute(photo.getLat(), photo.getLon(),place, 1);
    }
    protected void insertNewPosition(Place place,Day day) throws SQLException {
        Position position = new Position();
        Long placeId;
        Long positionId;
        if (database.hasVenueId(place.getVenueId()))
            placeId = database.getPlaceIdByVenueId(place.getVenueId());
         else
            placeId = database.insertPlace(place);

        database.updateDayMainPlaceId(day.getId(),placeId);

        position.setDayId(day.getId());
        position.setStartTime(day.getDate());
        position.setEndTime(day.getDate());
        position.setMainPhotoId(day.getMainPhotoId());
        position.setType("PLACE");
        position.setPlaceId(placeId);
        position.setCategoryIconPrefix(place.getCategoryIconPrefix());
        position.setCategoryIconSuffix(place.getCategoryIconSuffix());
        positionId = database.insertPosition(position);
        Photo photo = day.getMainPhoto();

        photo.setPlaceId(placeId);
        photo.setPositionId(positionId);
        database.updatePhoto(photo);
    }
}
