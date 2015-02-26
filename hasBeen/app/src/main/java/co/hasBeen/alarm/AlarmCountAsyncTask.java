package co.hasBeen.alarm;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-26.
 */
public class AlarmCountAsyncTask extends AsyncTask<Object, Void, Integer> {
    final static String URL = Session.DOMAIN+"/alarmsCount";

    @Override
    protected Integer doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL);
            HttpGet get = new HttpGet(uri.toString());
            get.addHeader("User-Agent", "Android");
            get.addHeader("Content-Type", "application/json");
            get.addHeader("Authorization", "Bearer " + params[0]);
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                JSONObject json = new JSONObject(EntityUtils.toString(entity));
                Integer count = json.getInt("alarmsCount");
                return count;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Integer count) {
        Message msg = mHandler.obtainMessage();
        if (count != null) {
            msg.obj = count;
            msg.what = 0;
        } else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);

    }

    Handler mHandler;

    public AlarmCountAsyncTask(Handler mHandler) {
        this.mHandler = mHandler;
    }
}