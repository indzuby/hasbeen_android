package co.hasBeen.search;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import co.hasBeen.R;
import co.hasBeen.model.network.PlaceAutocomplete;
import co.hasBeen.utils.HasBeenAsyncTask;

/**
 * Created by 주현 on 2015-04-08.
 */
public class PlaceAutocompleteAsyncTask extends HasBeenAsyncTask<Object,Void,PlaceAutocomplete> {
    final static String URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    final static String INPUT="input";
    final static String KEY = "key";
    final static String TYPE = "types";
    final static String TYPE_PARAM = "geocode";
    String mapKey;
    public PlaceAutocompleteAsyncTask(Handler mHandler,Context context) {
        super(mHandler);
        mapKey = context.getString(R.string.map_id);
    }

    @Override
    protected PlaceAutocomplete doInBackground(Object... params) {
        String keyword = params[0]+"";
        uri = Uri.parse(URL).buildUpon()
                .appendQueryParameter(INPUT,keyword)
                .appendQueryParameter(TYPE,TYPE_PARAM)
                .appendQueryParameter(KEY,mapKey).build();
        HttpGet get = new HttpGet(uri.toString());
        try {
            response = client.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();

            //Read the server response and attempt to parse it as JSON
            Reader reader = new InputStreamReader(content);
            Gson gson = new Gson();
            PlaceAutocomplete places = gson.fromJson(reader, PlaceAutocomplete.class);
            content.close();
            return places;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(PlaceAutocomplete places) {
        super.onPostExecute(places);
        Message msg = mHandler.obtainMessage();
        if(places !=null) {
            msg.obj = places;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

}
