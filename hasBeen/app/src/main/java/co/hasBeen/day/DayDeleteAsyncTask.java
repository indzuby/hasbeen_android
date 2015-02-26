package co.hasBeen.day;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-26.
 */
public class DayDeleteAsyncTask extends AsyncTask<Object,Void,Boolean> {
    Handler mHandler;
    //    final static String URL = "https://gist.githubusercontent.com/indzuby/a22f8ae73de9c3e3339c/raw/7f07e297c50961151fe4b8a4839a65d7d176fa91/DayView";
    final static String URL = Session.DOMAIN+"days/";

    @Override
    protected Boolean doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL+params[1]);
            HttpDelete del = new HttpDelete(uri.toString());
            del.addHeader("User-Agent","Android");
            del.addHeader("Content-Type", "application/json");
            del.addHeader("Authorization", "Bearer " + params[0]);
            response = client.execute(del);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 204)
                return true;
            return null;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean day) {

        Message msg = mHandler.obtainMessage();
        if(day!=null) {
            msg.obj = day;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public DayDeleteAsyncTask(Handler handler) {
        mHandler = handler;
    }
}
