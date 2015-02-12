package example.test.hasBeen.search;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-29.
 */
public class SearchPhotoAsyncTask extends AsyncTask<Object,Void,List<Photo>> {
    Handler mHandler;
//    final static String URL = "https://gist.githubusercontent.com/indzuby/01dd9766562e90d0af7e/raw/d4aca1859f83a9599dbe15541624b1499aae8ea2/photoNearBy";
    final static String URL = Session.DOMAIN+"hasBeen/photos";
    @Override
    protected List<Photo> doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL);
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
                        if(f.getName().equals("day") || f.getName().equals("place") ){
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
                Type listType = new TypeToken<List<Photo>>(){}.getType();
                List<Photo> photos = gson.fromJson(reader, listType);
                content.close();
                return photos;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Photo>  photos) {

        Message msg = mHandler.obtainMessage();
        if(photos !=null) {
            msg.obj = photos;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public SearchPhotoAsyncTask(Handler handler) {
        mHandler = handler;
    }
}
