package co.hasBeen.setting;

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
 * Created by 주현 on 2015-03-25.
 */
public class AccountAsyncTask extends HasBeenAsyncTask<Object,Void,User> {
    final static String URL = Session.DOMAIN+"account";
    @Override
    protected User doInBackground(Object... params) {
        try {
            uri = Uri.parse(URL);
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
                User user = JsonConverter.convertJsonToUser(reader);
                return user;
            }

        }catch (Exception e) {
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

    public AccountAsyncTask(Handler mHandler) {
        super(mHandler);
    }
}
