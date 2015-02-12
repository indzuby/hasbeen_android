package example.test.hasBeen.comment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import example.test.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-06.
 */
public class EditCommentAsyncTask extends AsyncTask<Object, Void, Boolean> {
    final static String URL = Session.DOMAIN;

    @Override
    protected Boolean doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL + "/comments/" + params[1]);
            if (params[2].equals("delete")) {
                HttpDelete delete = new HttpDelete(uri.toString());
                delete.addHeader("User-Agent", "Android");
                delete.addHeader("Content-Type", "application/json");
                delete.addHeader("Authorization", "Bearer " + params[0]);
                response = client.execute(delete);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == 204) {
                    return true;
                }
            }else {
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        return null;
    }

    @Override
    protected void onPostExecute(Boolean Boolean) {
        Message msg = mHandler.obtainMessage();
        if (Boolean != null) {
            msg.obj = Boolean;
            msg.what = 0;
        } else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);

    }

    Handler mHandler;

    public EditCommentAsyncTask(Handler mHandler) {
        this.mHandler = mHandler;
    }
}
