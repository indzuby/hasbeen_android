package co.hasBeen.geolocation;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.hasBeen.model.database.Category;
import co.hasBeen.model.database.Photo;

public class GeoFourSquare extends AsyncTask<Object, Void, JSONObject> {
    private Handler mHandler;
    final static String FOURSQAURE_URL = "https://api.foursquare.com/v2/venues/search";
    final static String CLIENT_ID = "C3SQKKLIP04VY4LZNPCFKIXKSUU41NEB1T3NZL45KSE1VJ05";
    final static String CLIENT_SECRET = "LTBZTBAGTOUZSK4DDD5DUMR34EGJNBGTS5L4SLZSY33P1OXJ";
    final static String ID_PARAM = "client_id";
    final static String SECRET_PARAM = "client_secret";
    final static String DAY_PARAM = "v";
    final static String LOCATION_PARAM = "ll";
    final static String QUERY_PARAM = "query";
    String ll;
    Photo photo;
    int placeCount = 25;
    Category mCategory ;

    public GeoFourSquare(Handler handler) {
        mHandler = handler;
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
//            LatLng location = getLocation();
        ll = params[0] + "," + params[1];
        if (params[2] != null)
            photo = (Photo) params[2];
        if (params[3] != null)
            placeCount = (int) params[3];
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
                    .appendQueryParameter(LOCATION_PARAM, ll).build();
//                Log.e("Url",uri.toString());
            HttpGet get = new HttpGet(uri.toString());
            response = client.execute(get);
            return new JSONObject(EntityUtils.toString(response.getEntity()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onPostExecute(JSONObject result) {
        Message msg = mHandler.obtainMessage();
        try {
//                Log.e("object",result.toString());
            Gson gson = new Gson();
            JSONObject jsonObject = result.getJSONObject("response");
            JSONArray jsonArray = jsonObject.getJSONArray("venues");
            if (placeCount == 1) {
                msg.obj = photo;
                if (jsonArray.length() == 0) {
                    photo.setPlaceId(null);
                    photo.setFourSquare(null, null, null, "", "");
                } else {
                    //          Toast.makeText(mContext, jsonArray.getJSONObject(0).get("name") + "", Toast.LENGTH_LONG).show();

                    photo.setPlaceName(jsonArray.getJSONObject(0).getString("name"));
                    JSONObject category = jsonArray.getJSONObject(0).getJSONArray("categories").getJSONObject(0);
                    JSONObject icon = category.getJSONObject("icon");
                    photo.setFourSquare(jsonArray.getJSONObject(0).getString("id"), category.getString("id"), category.getString("name"), icon.getString("prefix"), icon.getString("suffix"));

                }
            } else {
                Log.i("Geo", ll);
                List<Category> categories = new ArrayList<>();
                for (int i = 0; i < jsonArray.length() && i < placeCount; i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    mCategory = new Category();
                    if (item.getJSONArray("categories").length() > 0) {
                        Log.i("place name", item.getString("name"));
                        JSONObject category = item.getJSONArray("categories").getJSONObject(0);
                        JSONObject icon = category.getJSONObject("icon");
                        mCategory.setPlaceName(item.getString("name"));
                        mCategory.setCategryName(category.getString("name"));
                        mCategory.setVenueId(item.getString("id"));
                        mCategory.setCategoryId(category.getString("id"));
                        mCategory.setCategoryIconPrefix(icon.getString("prefix"));
                        mCategory.setCategoryIconSuffix(icon.getString("suffix"));
                        categories.add(mCategory);
                    }
                }
                msg.obj = categories;
            }
            msg.what = 0;
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            msg.what = -1;
            msg.obj = e;
            mHandler.sendMessage(msg);
        }
    }
}