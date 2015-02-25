package co.hasBeen.profile.follow;

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
 * Created by zuby on 2015-01-27.
 */
public class UnFollowAsyncTask extends AsyncTask<Object,Void,Boolean> {
    Handler mHandler;
    final static String URL = Session.DOMAIN+"follows/";

    @Override
    protected Boolean doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL + params[1]);
            HttpDelete del = new HttpDelete(uri.toString());
            del.addHeader("User-Agent","Android");
            del.addHeader("Content-Type","application/json");
            del.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(del);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 204) {
                return true;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean id) {

        Message msg = mHandler.obtainMessage();
        if(id!=null) {
            msg.obj = id;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public UnFollowAsyncTask(Handler handler) {
        mHandler = handler;
    }
}
