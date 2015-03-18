package co.hasBeen.newsfeed;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import co.hasBeen.model.api.Day;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-27.
 */
public class NewsFeedAsyncTask extends HasBeenAsyncTask<Object,Void,List<Day>> {
//    final static String URL = "https://gist.githubusercontent.com/indzuby/c9e87b33ca65eac93065/raw/4000d9c125b1e56c60f77523dc806e4a9cdb303d/NewsFeed";
    final static String URL = Session.DOMAIN+"newsFeed";
    int status;
    @Override
    protected List<Day> doInBackground(Object... params) {
        try {
            uri = Uri.parse(URL);
            if(params.length>2) {
                uri = Uri.parse(URL+"?firstUpdatedTime="+params[2]);
            }else if(params.length>1) {
                uri = Uri.parse(URL+"?lastUpdatedTime="+params[1]);
            }
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
                List<Day> posts = JsonConverter.convertJsonDayList(reader);
                content.close();
                return posts;
            }else if(statusLine.getStatusCode()==401) {
                status = 401;
                return null;
            }
        }catch (Exception e) {
            if(response!=null && response.getStatusLine().getStatusCode()==401) {
                status = 401;
                return null;
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Day> newsFeeds) {

        Message msg = mHandler.obtainMessage();
        if(newsFeeds!=null) {
            msg.obj = newsFeeds;
            msg.what = 0;
        }else {
            msg.obj = status ;
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public NewsFeedAsyncTask(Handler handler) {
        super(handler);
    }
}
