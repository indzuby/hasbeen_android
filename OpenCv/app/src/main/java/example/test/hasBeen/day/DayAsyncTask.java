package example.test.hasBeen.day;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

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

import example.test.hasBeen.model.api.DayApi;

/**
 * Created by zuby on 2015-01-27.
 */
public class DayAsyncTask extends AsyncTask<Object,Void,DayApi> {
    Handler mHandler;
    final static String URL = "https://gist.githubusercontent.com/indzuby/a22f8ae73de9c3e3339c/raw/38c230fe9fe0a4b563cd40df6455dd28a7e0c77c/DayView";
    @Override
    protected DayApi doInBackground(Object... params) {
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
                Gson gson = gsonBuilder.create();
                DayApi day = gson.fromJson(reader, DayApi.class);
                content.close();
                return day;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(DayApi day) {

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
