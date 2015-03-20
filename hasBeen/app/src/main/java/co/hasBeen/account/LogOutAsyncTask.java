package co.hasBeen.account;

import android.net.Uri;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import co.hasBeen.utils.SFSSLSocketFactory;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-26.
 */
public class LogOutAsyncTask extends AsyncTask<Object, Void, Boolean> {
    final static String URL = Session.DOMAIN+"/signOut";

    @Override
    protected Boolean doInBackground(Object... params) {
        HttpResponse response;
        Uri uri;
        HttpClient client = SFSSLSocketFactory.getHttpClient();
        try {
            uri = Uri.parse(URL);
            HttpGet get = new HttpGet(uri.toString());
            get.addHeader("User-Agent", "Android");
            get.addHeader("Content-Type", "application/json");
            get.addHeader("Authorization", "Bearer " + params[0]);
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}