package example.test.hasBeen.newsfeed;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

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

import example.test.hasBeen.model.api.NewsFeedApi;

/**
 * Created by zuby on 2015-01-27.
 */
public class NewsFeedAsyncTask extends AsyncTask<Object,Void,List<NewsFeedApi>> {
    Handler mHandler;
    final static String URL = "https://gist.githubusercontent.com/indzuby/c9e87b33ca65eac93065/raw/33d576b53438eca307f9d4d6a354d3528d40a7c3/NewsFeed";
    @Override
    protected List<NewsFeedApi> doInBackground(Object... params) {
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
                Type listType = new TypeToken<List<NewsFeedApi>>(){}.getType();
                List<NewsFeedApi> posts = gson.fromJson(reader, listType);
                content.close();
                return posts;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<NewsFeedApi> newsFeeds) {

        Message msg = mHandler.obtainMessage();
        if(newsFeeds!=null) {
            msg.obj = newsFeeds;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public NewsFeedAsyncTask(Handler handler) {
        mHandler = handler;
    }
}
