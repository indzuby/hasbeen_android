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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import co.hasBeen.model.api.Alarm;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-26.
 */
public class AlarmListAsyncTask extends AsyncTask<Object, Void, List<Alarm>> {
    final static String URL = Session.DOMAIN+"/alarmsCount";
    final static String CATEGORY_PARAMS="category";
    public final static String CATEGORY_YOU = "YOU";
    public final static String CATEGORY_NEWS="NEWS";
    @Override
    protected List<Alarm> doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL+"?"+CATEGORY_PARAMS+"="+params[1]);
            HttpGet get = new HttpGet(uri.toString());
            get.addHeader("User-Agent", "Android");
            get.addHeader("Content-Type", "application/json");
            get.addHeader("Authorization", "Bearer " + params[0]);
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                //Read the server response and attempt to parse it as JSON
                Reader reader = new InputStreamReader(content);
                List<Alarm> alarm = JsonConverter.convertJsonAlarmList(reader);
                content.close();

                return alarm;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(List<Alarm> alarm) {
        Message msg = mHandler.obtainMessage();
        if (alarm != null) {
            msg.obj = alarm;
            msg.what = 0;
        } else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);

    }

    Handler mHandler;

    public AlarmListAsyncTask(Handler mHandler) {
        this.mHandler = mHandler;
    }
}