package example.test.hasBeen.day;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import example.test.hasBeen.model.database.Day;
import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-27.
 */
public class DayAsyncTask extends AsyncTask<Object,Void,Day> {
    Handler mHandler;
//    final static String URL = "https://gist.githubusercontent.com/indzuby/a22f8ae73de9c3e3339c/raw/7f07e297c50961151fe4b8a4839a65d7d176fa91/DayView";
    final static String URL = Session.DOMAIN+"days/";

    @Override
    protected Day doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
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
                        if(f.getDeclaredClass() == Photo.class){
                            if(f.getName().equals("day") || f.getName().equals("place"))
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
                Day day = gson.fromJson(reader, Day.class);
                content.close();
                return day;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Day day) {

        Message msg = mHandler.obtainMessage();
        if(day !=null) {
            msg.obj = day;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public DayAsyncTask(Handler handler) {
        mHandler = handler;
    }
}
