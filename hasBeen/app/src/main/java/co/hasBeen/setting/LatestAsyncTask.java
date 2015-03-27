package co.hasBeen.setting;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-25.
 */
public class LatestAsyncTask extends HasBeenAsyncTask<Object,Void,String> {
    final static String URL = Session.DOMAIN+"latestVersion";
    public LatestAsyncTask(Handler mHandler) {
        super(mHandler);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Message msg = mHandler.obtainMessage();
        if (s != null) {
            msg.obj = s;
            msg.what = 0;
        } else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    @Override
    protected String doInBackground(Object... params) {
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
                JSONObject json = new JSONObject(EntityUtils.toString(entity));
                return json.getString("number");
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}