package co.hasBeen.database.Photo;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.database.ItemModule;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Place;
import co.hasBeen.model.api.Position;

/**
 * Created by 주현 on 2015-03-30.
 */
public class AddAsyncTask extends AsyncTask<Object,Void,List<Position>> {
    final static String FOURSQAURE_URL = "https://api.foursquare.com/v2/venues/search";
    final static String CLIENT_ID = "C3SQKKLIP04VY4LZNPCFKIXKSUU41NEB1T3NZL45KSE1VJ05";
    final static String CLIENT_SECRET = "LTBZTBAGTOUZSK4DDD5DUMR34EGJNBGTS5L4SLZSY33P1OXJ";
    final static String ID_PARAM = "client_id";
    final static String SECRET_PARAM = "client_secret";
    final static String DAY_PARAM = "v";
    final static String LOCATION_PARAM = "ll";
    final static String QUERY_PARAM = "query";
    final static String LOCALE_PARAM = "locale";
    String ll;
    ItemModule itemModule;
    Context mContext;
    DatabaseHelper database;
    public int photoCount;
    Long dayid;
    Handler handler;
    public AddAsyncTask(Context context,Long dayid,Handler handler) throws SQLException {
        mContext = context;
        database = new DatabaseHelper(context);
        itemModule = new ItemModule(context);
        this.dayid = dayid;
        this.handler = handler;
    }

    @Override
    protected List<Position> doInBackground(Object... params) {
        try {
            Day day = database.selectDay(dayid);
            photoCount = 0;
            itemModule.insertPhotos(day.getDate(), dayid);
            insertPosition(dayid);
            day.setCreatedTime(new Date().getTime());
            day.setPhotoCount(database.countPhotoByDayid(dayid));
            database.updateDay(day);
            return database.selectPositionByDayId(dayid);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    Photo bestPhoto;
    public void insertPosition(Long dayId) throws Exception {
        List<Photo> photoList = database.selectPhotosByDayId(dayId);
        for(Photo photo : photoList) {
            if (photo.getPositionId()!=null) {
                bestPhoto = photo;

            }else {
                Place newPlace = setPlace(photo);
                Place bestPlace = database.selectPlace(bestPhoto.getPlaceId());
                if (itemModule.isSamePlace(newPlace, bestPlace)) {
                    photo.setPlaceId(bestPhoto.getPlaceId());
                    photo.setPositionId(bestPhoto.getPositionId());
                    database.updatePhoto(photo);
                    database.updatePositionEndTime(photo.getPositionId(), photo.getTakenTime());
                } else {
                    itemModule.insertNewPosition(newPlace, photo);
                }
                bestPhoto = photo;
            }
            photoCount++;
        }
    }
    @Override
    protected void onPostExecute(List<Position> positions) {
        Message msg = handler.obtainMessage();
        if(positions!=null) {
            msg.what = 0 ;
            msg.obj = positions;
        }else
            msg.what=-1;
        handler.sendMessage(msg);
    }
    public Place setPlace(Photo photo) throws Exception{
        JSONObject result = getFourSquarePlace(photo);
        JSONObject jsonObject = result.getJSONObject("response");
        JSONArray jsonArray = jsonObject.getJSONArray("venues");
        Place place = new Place();
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                if (item.isNull("categories") || item.getJSONArray("categories").length() <= 0)
                    continue;
                place.setName(item.getString("name"));
                JSONObject category = item.getJSONArray("categories").getJSONObject(0);
                JSONObject icon = category.getJSONObject("icon");
                place.setVenueId(item.getString("id"));
                place.setCategoryId(category.getString("id"));
                place.setCategoryName(category.getString("name"));
                place.setCategoryIconPrefix(icon.getString("prefix"));
                place.setCategoryIconSuffix(icon.getString("suffix"));
                JSONObject location = item.getJSONObject("location");
                if (location.has("city"))
                    place.setCity(location.getString("city"));
                if (location.has("country"))
                    place.setCountry(location.getString("country"));
                place.setLat((float) location.getDouble("lat"));
                place.setLon((float) location.getDouble("lng"));
                break;
            }
        } else {
            place.setName("Not Found");
            place.setVenueId(null);
            place.setCategoryId("-1");
            place.setCategoryName("");
            place.setLon(photo.getLon());
            place.setLat(photo.getLat());
        }
        return place;
    }
    public JSONObject getFourSquarePlace(Photo photo){
        ll = photo.getLat() + "," + photo.getLon();
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = simpleDateFormat.format(new Date());
        try {

            uri = Uri.parse(FOURSQAURE_URL).buildUpon()
                    .appendQueryParameter(ID_PARAM, CLIENT_ID)
                    .appendQueryParameter(SECRET_PARAM, CLIENT_SECRET)
                    .appendQueryParameter(DAY_PARAM, currentDate)
                    .appendQueryParameter(LOCATION_PARAM, ll)
                    .appendQueryParameter(LOCALE_PARAM, "en")
                    .build();
//                Log.e("Url",uri.toString());
            HttpGet get = new HttpGet(uri.toString());
            response = client.execute(get);
            return new JSONObject(EntityUtils.toString(response.getEntity()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
