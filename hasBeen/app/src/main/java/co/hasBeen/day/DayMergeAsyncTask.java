package co.hasBeen.day;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;

import co.hasBeen.model.network.SFSSLSocketFactory;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-26.
 */
public class DayMergeAsyncTask extends AsyncTask<Object,Void,Boolean> {
    Handler mHandler;
    //    final static String URL = "https://gist.githubusercontent.com/indzuby/a22f8ae73de9c3e3339c/raw/7f07e297c50961151fe4b8a4839a65d7d176fa91/DayView";
    final static String URL = Session.DOMAIN+"positions/";

    @Override
    protected Boolean doInBackground(Object... params) {
        HttpClient client = SFSSLSocketFactory.getHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL+params[1]+"/mergeToPrev");
            HttpPut put = new HttpPut(uri.toString());
            put.addHeader("User-Agent","Android");
            put.addHeader("Content-Type", "application/json");
            put.addHeader("Authorization", "Bearer " + params[0]);
            response = client.execute(put);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 200)
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

    public DayMergeAsyncTask(Handler handler) {
        mHandler = handler;
    }
}

