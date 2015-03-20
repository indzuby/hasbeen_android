package co.hasBeen.day;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import co.hasBeen.model.api.Day;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-27.
 */
public class DayAsyncTask extends HasBeenAsyncTask<Object,Void,Day> {
//    final static String URL = "https://gist.githubusercontent.com/indzuby/a22f8ae73de9c3e3339c/raw/7f07e297c50961151fe4b8a4839a65d7d176fa91/DayView";
    final static String URL = Session.DOMAIN+"days/";

    @Override
    protected Day doInBackground(Object... params) {
        try {
            uri = Uri.parse(URL+params[1]);
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

                Day day = JsonConverter.convertJsonDay(reader);
                content.close();
                return day;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Day day) {
        super.onPostExecute(day);
        Message msg = mHandler.obtainMessage();
        if (day != null) {
            msg.obj = day;
            msg.what = 0;
        } else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public DayAsyncTask(Handler handler) {
        super(handler);
    }
}
