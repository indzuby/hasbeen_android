package co.hasBeen.photo;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import co.hasBeen.model.network.SFSSLSocketFactory;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-09.
 */
public class CoverAsyncTask extends AsyncTask<Object,Void,Boolean> {
    final static String URL = Session.DOMAIN+"photos/";
    @Override
    protected Boolean doInBackground(Object... params) {
        HttpClient client =  SFSSLSocketFactory.getHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL +params[1]+"/cover");
            HttpGet get = new HttpGet(uri.toString());
            get.addHeader("User-Agent","Android");
            get.addHeader("Content-Type","application/json");
            get.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 200) {
                return true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        Message msg = mHandler.obtainMessage();
        if(status !=null) {
            msg.obj = status;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }
    Handler mHandler;

    public CoverAsyncTask(Handler mHandler) {
        this.mHandler = mHandler;
    }
}
