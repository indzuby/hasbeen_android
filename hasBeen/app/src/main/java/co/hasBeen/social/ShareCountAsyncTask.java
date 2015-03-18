package co.hasBeen.social;

import android.net.Uri;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;

import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-11.
 */
public class ShareCountAsyncTask extends HasBeenAsyncTask<Object,Void,Boolean> {
    final static String URL = Session.DOMAIN;
    @Override
    protected Boolean doInBackground(Object... params) {
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
