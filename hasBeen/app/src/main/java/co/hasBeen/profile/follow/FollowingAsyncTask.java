package co.hasBeen.profile.follow;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import co.hasBeen.model.api.Follow;
import co.hasBeen.model.network.SFSSLSocketFactory;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FollowingAsyncTask extends AsyncTask<Object,Void,List<Follow>> {
    Handler mHandler;
//    final static String URL = "https://gist.githubusercontent.com/indzuby/e0892db0c7155a62fc22/raw/86990d9574684a129edd71e8e1eafd1b80c5d635/FollowingList";
    final static String URL = Session.DOMAIN+"users/";
    @Override
    protected List<Follow> doInBackground(Object... params) {
        HttpClient client = SFSSLSocketFactory.getHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            uri = Uri.parse(URL+params[1]+"/following");
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

    public FollowingAsyncTask(Handler handler) {
        mHandler = handler;
    }
}