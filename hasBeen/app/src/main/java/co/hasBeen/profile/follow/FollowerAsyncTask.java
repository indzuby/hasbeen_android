package co.hasBeen.profile.follow;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import co.hasBeen.model.api.Follow;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FollowerAsyncTask extends HasBeenAsyncTask<Object,Void,List<Follow>> {

//    final static String URL = "https://gist.githubusercontent.com/indzuby/43da0aae3a9e9875d670/raw/16c0c339eea735923c7d8a495403a5e543936757/FollowerList";
    final static String URL = Session.DOMAIN+"users/";
    @Override
    protected List<Follow> doInBackground(Object... params) {
        try {
            uri = Uri.parse(URL+params[1]+"/follower");
            HttpGet get = new HttpGet(uri.toString());
            get.addHeader("User-Agent","Android");
            get.addHeader("Content-Type","application/json");
            get.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                //Read the server response and attempt to parse it as JSON
                Reader reader = new InputStreamReader(content);


                List<Follow> Followers = JsonConverter.convertJsonFollowList(reader);
                content.close();
                return Followers;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Follow> Followers) {

        Message msg = mHandler.obtainMessage();
        if(Followers !=null) {
            msg.obj = Followers;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public FollowerAsyncTask(Handler handler) {
        super(handler);
    }
}