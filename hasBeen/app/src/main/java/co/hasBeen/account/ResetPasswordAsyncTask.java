package co.hasBeen.account;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;

import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-23.
 */
public class ResetPasswordAsyncTask extends HasBeenAsyncTask<String, Void, Boolean> {
    final static String URL = Session.DOMAIN+"resetPassword";
    public final static String CLIENT = "hasBeenClientId";
    final static String SECRET = "hasBeenSecret";
    @Override
    protected Boolean doInBackground(String... params) {
        try {
            uri = Uri.parse(URL+"?email="+params[0]);
            HttpGet httpGet = new HttpGet(uri.toString());
            httpGet.addHeader("User-Agent", "Android");
            httpGet.addHeader("Content-Type", "application/json");
            String authorization = CLIENT + ":" + SECRET;
            httpGet.addHeader("Authorization", "Basic " + Base64.encodeToString(authorization.getBytes(), Base64.NO_WRAP));
            // Add your data
            response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == 200) {
                Log.i("Log in", "Success");
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                content.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean response) {
        super.onPostExecute(response);
        Message msg = mHandler.obtainMessage();
        if (response != null) {
            msg.obj = response;
            msg.what = 0;
        } else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public ResetPasswordAsyncTask(Handler handler) {
        super(handler);
    }
}
