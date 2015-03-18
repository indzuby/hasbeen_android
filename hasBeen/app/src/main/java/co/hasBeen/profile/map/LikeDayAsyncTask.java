package co.hasBeen.profile.map;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import co.hasBeen.model.api.Loved;
import co.hasBeen.model.network.SFSSLSocketFactory;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-27.
 */
public class LikeDayAsyncTask extends AsyncTask<Object,Void,List<Loved>> {
    Handler mHandler;
//    final static String URL = "https://gist.githubusercontent.com/indzuby/c9e87b33ca65eac93065/raw/4000d9c125b1e56c60f77523dc806e4a9cdb303d/NewsFeed";
    final static String URL = Session.DOMAIN+"users/";
    @Override
    protected List<Loved> doInBackground(Object... params) {
        HttpClient client = SFSSLSocketFactory.getHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL+params[1]+"/lovedDays");
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
                List<Loved> posts = JsonConverter.convertJsonLovedList(reader);
                content.close();
                return posts;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Loved> days) {

        Message msg = mHandler.obtainMessage();
        if(days!=null) {
            msg.obj = days;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public LikeDayAsyncTask(Handler handler) {
        mHandler = handler;
    }
}
