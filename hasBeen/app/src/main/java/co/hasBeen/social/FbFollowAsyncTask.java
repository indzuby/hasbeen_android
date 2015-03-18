package co.hasBeen.social;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;

import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FbFollowAsyncTask extends HasBeenAsyncTask<Object,Void,Boolean> {

    //    final static String URL = "https://gist.githubusercontent.com/indzuby/43da0aae3a9e9875d670/raw/16c0c339eea735923c7d8a495403a5e543936757/FollowerList";
    final static String URL = Session.DOMAIN+"follows/facebookFriends";
    @Override
    protected Boolean doInBackground(Object... params) {
        try {
            uri = Uri.parse(URL);
            HttpPost post = new HttpPost(uri.toString());
            post.addHeader("User-Agent","Android");
            post.addHeader("Content-Type","application/json");
            post.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 201) {
                HttpEntity entity = response.getEntity();
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
            msg.what = 0;
            msg.obj = id;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public FbFollowAsyncTask(Handler mHandler) {
        super(mHandler);
    }
}