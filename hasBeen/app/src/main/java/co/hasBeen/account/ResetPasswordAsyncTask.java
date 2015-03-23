package co.hasBeen.account;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import co.hasBeen.utils.SFSSLSocketFactory;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-23.
 */
public class ResetPasswordAsyncTask extends AsyncTask<String, Void, Boolean> {
    Handler mHandler;
    final static String URL = Session.SSL_DOMAIN+"resetPassword";
    public final static String CLIENT = "hasBeenClientId";
    final static String SECRET = "hasBeenSecret";
    @Override
    protected Boolean doInBackground(String... params) {
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL);
            HttpClient httpclient = SFSSLSocketFactory.getHttpClient();
            HttpPost httppost = new HttpPost(uri.toString());
            httppost.addHeader("User-Agent", "Android");
            httppost.addHeader("Content-Type", "application/json");
            String authorization = CLIENT + ":" + SECRET;
            httppost.addHeader("Authorization", "Basic " + Base64.encodeToString(authorization.getBytes(), Base64.NO_WRAP));
            // Add your data
            List<NameValuePair> data = new ArrayList<>();
            data.add(new BasicNameValuePair("email", params[0]));
            httppost.setEntity(new UrlEncodedFormEntity(data));
            response = httpclient.execute(httppost);
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
        mHandler = handler;
    }
}
