package co.hasBeen.alarm;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import co.hasBeen.model.api.AlarmCount;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-26.
 */
public class AlarmCountAsyncTask extends HasBeenAsyncTask<Object, Void, AlarmCount> {
    final static String URL = Session.DOMAIN+"/alarmCount";

    @Override
    protected AlarmCount doInBackground(Object... params) {
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
                AlarmCount count = new AlarmCount();
                count.setNewsCount(json.getInt("newsCount"));
                count.setYouCount(json.getInt("youCount"));
                return count;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(AlarmCount count) {
        Message msg = mHandler.obtainMessage();
        if (count != null) {
            msg.obj = count;
            msg.what = 0;
        } else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);

    }


    public AlarmCountAsyncTask(Handler mHandler) {
        super(mHandler);
    }
}