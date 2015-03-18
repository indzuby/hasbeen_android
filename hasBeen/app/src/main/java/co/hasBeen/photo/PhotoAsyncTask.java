package co.hasBeen.photo;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Place;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-29.
 */
public class PhotoAsyncTask extends HasBeenAsyncTask<Object,Void,Photo> {
//    final static String URL = "https://gist.githubusercontent.com/indzuby/ca980f3c37f79d0156be/raw/548b25ee536bb5ec5f1941a0cb05bdb9ba845ed1/PhotoView";
    final static String URL = Session.DOMAIN+"photos/";
    @Override
    protected Photo doInBackground(Object... params) {
        try {
            uri = Uri.parse(URL+params[1]);
            HttpGet get = new HttpGet(uri.toString());
            get.addHeader("User-Agent","Android");
            get.addHeader("Content-Type","application/json");
            get.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                //Read the server response and attempt to parse it as JSON
                Reader reader = new InputStreamReader(content);


                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        if(f.getName().equals("mainPhoto") ){
                            if(f.getDeclaringClass() == Day.class || f.getDeclaringClass() == Place.class)
                                return true;
                        }
                        if(f.getName().equals("coverPhoto"))
                            return true;
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                }).create();
                Photo day = gson.fromJson(reader, Photo.class);
                content.close();
                return day;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Photo day) {

        Message msg = mHandler.obtainMessage();
        if(day !=null) {
            msg.obj = day;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public PhotoAsyncTask(Handler handler) {
        super(handler);
    }
}
