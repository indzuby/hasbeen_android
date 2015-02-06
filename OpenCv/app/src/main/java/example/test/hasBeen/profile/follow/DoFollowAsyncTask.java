package example.test.hasBeen.profile.follow;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import example.test.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-02.
 */
public class DoFollowAsyncTask extends AsyncTask<Object,Void,Boolean> {
    Handler mHandler;
    //    final static String URL = "https://gist.githubusercontent.com/indzuby/43da0aae3a9e9875d670/raw/16c0c339eea735923c7d8a495403a5e543936757/FollowerList";
    final static String URL = Session.DOMAIN+"users/";
    @Override
    protected Boolean doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL+params[1]+"/follow");
            HttpPost post = new HttpPost(uri.toString());
            post.addHeader("User-Agent","Android");
            post.addHeader("Content-Type","application/json");
            post.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 201) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                //Read the server response and attempt to parse it as JSON
                Reader reader = new InputStreamReader(content);
                content.close();
                return true;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean status) {

        Message msg = mHandler.obtainMessage();
        if(status) {
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public DoFollowAsyncTask(Handler handler) {
        mHandler = handler;
    }
}