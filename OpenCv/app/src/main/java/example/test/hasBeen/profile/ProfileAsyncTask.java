package example.test.hasBeen.profile;

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
import example.test.hasBeen.model.api.User;

/**
 * Created by zuby on 2015-01-29.
 */
public class ProfileAsyncTask extends AsyncTask<Object,Void,User> {
    Handler mHandler;
    final static String URL = "https://gist.githubusercontent.com/indzuby/de7426b596733baa9ed1/raw/205b50195800f1669786b65fee82986a36b061f0/Profile";
    @Override
    protected User doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL);
            HttpGet get = new HttpGet(uri.toString());
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
                        if(f.getDeclaringClass() == PhotoApi.class)
                        if(f.getName().equals("day") || f.getName().equals("place") ){
                                return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                }).create();
                User user = gson.fromJson(reader, User.class);
                content.close();
                return user;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User  user) {

        Message msg = mHandler.obtainMessage();
        if(user !=null) {
            msg.obj = user;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public ProfileAsyncTask(Handler handler) {
        mHandler = handler;
    }
}
