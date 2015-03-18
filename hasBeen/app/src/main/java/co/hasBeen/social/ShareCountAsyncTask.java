package co.hasBeen.social;

import android.net.Uri;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import co.hasBeen.model.network.SFSSLSocketFactory;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-11.
 */
public class ShareCountAsyncTask extends AsyncTask<Object,Void,Boolean> {
    final static String URL = Session.DOMAIN;
    @Override
    protected Boolean doInBackground(Object... params) {
        HttpClient client = SFSSLSocketFactory.getHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL+params[1]+"/"+params[2]+"/shares");
            HttpPost post = new HttpPost(uri.toString());
            post.addHeader("User-Agent","Android");
            post.addHeader("Content-Type","application/json");
            post.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 201) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
