package co.hasBeen.social;

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
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-27.
 */
public class LoveAsyncTask extends AsyncTask<Object,Void,Long> {
    Handler mHandler;
    final static String daysURL = Session.DOMAIN+"days/";
    final static String photosURL = Session.DOMAIN+"photos/";

    @Override
    protected Long doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            if(params[1].equals("days"))
                uri = Uri.parse(daysURL + params[2]+"/loves");
            else
                uri = Uri.parse(photosURL + params[2]+"/loves");
            HttpPost post = new HttpPost(uri.toString());
            post.addHeader("User-Agent","Android");
            post.addHeader("Content-Type","application/json");
            post.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 201) {
                HttpEntity entity = response.getEntity();
                JSONObject json = new JSONObject(EntityUtils.toString(entity));
                Long id = json.getLong("id");
                return id;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Long id) {

        Message msg = mHandler.obtainMessage();
        if(id !=null) {
            msg.obj = id;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public LoveAsyncTask(Handler handler) {
        mHandler = handler;
    }
}
