package co.hasBeen.account;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import co.hasBeen.model.network.LoginTokenRequest;
import co.hasBeen.model.network.LoginTokenResponse;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-27.
 */
public class LogInAsyncTask extends HasBeenAsyncTask<String, Void, LoginTokenResponse> {
    final static String URL=  Session.SSL_DOMAIN+"oauth/token";
    public final static String CLIENT = "hasBeenClientId";
    final static String SECRET="hasBeenSecret";

    @Override
    protected LoginTokenResponse doInBackground(String... params) {
        try {
            uri = Uri.parse(URL);
            HttpPost httppost = new HttpPost(uri.toString());
            httppost.addHeader("User-Agent","Android");
            httppost.addHeader("Content-Type","application/x-www-form-urlencoded");
            String authorization =CLIENT + ":"+SECRET;
            httppost.addHeader("Authorization","Basic "+ Base64.encodeToString(authorization.getBytes(), Base64.NO_WRAP));
            // Add your data

            LoginTokenRequest request = new LoginTokenRequest();
            Gson gson = new Gson();
            request.setUsername(params[0]);
            request.setPassword(params[1]);
            request.setGrant_type(params[2]);
            request.setScope(params[3]);
            request.setClient_id(CLIENT);
            request.setClient_secret(SECRET);
            List<NameValuePair> data = new ArrayList<>();
            data.add(new BasicNameValuePair("username",params[0]));
            data.add(new BasicNameValuePair("password",params[1]));
            data.add(new BasicNameValuePair("grant_type",params[2]));
            data.add(new BasicNameValuePair("scope",params[3]));
            data.add(new BasicNameValuePair("client_id",CLIENT));
            data.add(new BasicNameValuePair("client_secret",SECRET));
//            JSONObject param = new JSONObject();
//            param.put("username", params[0]);
//            param.put("password", params[1]);
//            param.put("grant_type", params[2]);
//            param.put("scope", params[3]);
//            param.put("client_id",params[4]);
//            param.put("client_secret",SECRET);
//            httppost.setEntity(new StringEntity(gson.toJson(request)));
            httppost.setEntity(new UrlEncodedFormEntity(data));
            // Execute HTTP Post Request

            Log.i("Request", gson.toJson(request));
            response = client.execute(httppost);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == 200) {
                Log.i("Log in","Success");
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                //Read the server response and attempt to parse it as JSON
                Reader reader = new InputStreamReader(content);

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                LoginTokenResponse token = gson.fromJson(reader,LoginTokenResponse.class);
                content.close();
                return token;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(LoginTokenResponse response) {
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

    public LogInAsyncTask(Handler handler) {
        super(handler);
    }
}
