package co.hasBeen.profile;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import co.hasBeen.model.api.User;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-29.
 */
public class ProfileAsyncTask extends HasBeenAsyncTask<Object, Void, User> {

    final static String URL2 = "https://gist.githubusercontent.com/indzuby/de7426b596733baa9ed1/raw/205b50195800f1669786b65fee82986a36b061f0/Profile";
    final static String URL = Session.DOMAIN+"users/";
    @Override
    protected User doInBackground(Object... params) {
        try {
            if(params.length<2)
                uri = Uri.parse(URL);
//                uri = Uri.parse(URL2);
            else
                uri = Uri.parse(URL+params[1]);
            HttpGet get = new HttpGet(uri.toString());
            get.addHeader("User-Agent","Android");
            get.addHeader("Content-Type","application/json");
            get.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                //Read the server response and attempt to parse it as JSON
                Reader reader = new InputStreamReader(content);
                User user = JsonConverter.convertJsonToUser(reader);
                content.close();
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        super.onPostExecute(user);
        Message msg = mHandler.obtainMessage();
        if (user != null) {
            msg.obj = user;
            msg.what = 0;
        } else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public ProfileAsyncTask(Handler mHandler) {
        super(mHandler);
    }
}
