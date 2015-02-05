package example.test.hasBeen.account;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

import example.test.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-27.
 */
public class SignUpAsyncTask extends AsyncTask<String, Void, String> {
    Handler mHandler;
    final static String DOMAIN = Session.DOMAIN;
    final static String SIGNUPEMAIL = "signUpWithEmail";
    final static String SIGNUPSOCIAL = "signUpWithSocial";
    final static String AUTHORIZATION = "hasBeenClientId:hasBeenSecret";
    final static String EMAIL = "1";
    final static String SOCIAL = "2";

    @Override
    protected String doInBackground(String... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            if (params[0].equals(EMAIL))
                uri = Uri.parse(DOMAIN + SIGNUPEMAIL);
            else
                uri = Uri.parse(DOMAIN + SIGNUPSOCIAL);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri.toString());
            httppost.addHeader("User-Agent","Android");
            httppost.addHeader("Content-Type","application/json");
            httppost.addHeader("Authorization","Basic "+ Base64.encodeToString(AUTHORIZATION.getBytes(),Base64.NO_WRAP));
            // Add your data
            JSONObject param = new JSONObject();
            if(params[0].equals(EMAIL)) {
                param.put("email", params[1]);
                param.put("firstName", params[2]);
                param.put("lastName", params[3]);
                param.put("password", params[4]);
            }else {
                param.put("socialType", "FACEBOOK");
                param.put("token", params[1]);
            }
            httppost.setEntity(new StringEntity(param.toString()));
            // Execute HTTP Post Request

            Log.i("Request", param.toString());
            response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == 201) {
                Log.i("Sign up","Success");
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                return params[1];
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String token) {

        Message msg = mHandler.obtainMessage();
        if (token!=null) {
            msg.obj = token;
            msg.what = 0;
        } else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public SignUpAsyncTask(Handler handler) {
        mHandler = handler;
    }
}
