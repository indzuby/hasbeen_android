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

import example.test.hasBeen.model.api.DayApi;
import example.test.hasBeen.model.api.PhotoApi;

/**
 * Created by zuby on 2015-01-27.
 */
public class SearchDayAsyncTask extends AsyncTask<Object,Void,List<DayApi>> {
    Handler mHandler;
    final static String URL = "https://gist.githubusercontent.com/indzuby/c9e87b33ca65eac93065/raw/4000d9c125b1e56c60f77523dc806e4a9cdb303d/NewsFeed";
//    final static String URL = Session.DOMAIN+"map?type=days";
    @Override
    protected List<DayApi> doInBackground(Object... params) {
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
                        if(f.getDeclaredClass() == PhotoApi.class){
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
                Type listType = new TypeToken<List<DayApi>>(){}.getType();
                List<DayApi> posts = gson.fromJson(reader, listType);
                content.close();
                return posts;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<DayApi> days) {

        Message msg = mHandler.obtainMessage();
        if(days!=null) {
            msg.obj = days;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public SearchDayAsyncTask(Handler handler) {
        mHandler = handler;
    }
}
