package co.hasBeen.setting;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import co.hasBeen.model.api.User;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-25.
 */
public class EditAccountAsyncTask extends HasBeenAsyncTask<Object, Void, Boolean> {
    final static String URL = Session.DOMAIN + "account";

    @Override
    protected Boolean doInBackground(Object... params) {
        try {
            User user = (User) params[1];
            uri = Uri.parse(URL);
            HttpPut put = new HttpPut(uri.toString());
            put.addHeader("User-Agent", "Android");
            put.addHeader("Content-Type", "application/json");
            put.addHeader("Authorization", "Bearer " + params[0]);
            Gson gson = new Gson();
            put.setEntity(new StringEntity(gson.toJson(user), HTTP.UTF_8));
            response = client.execute(put);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean user) {
        super.onPostExecute(user);
        Message msg = mHandler.obtainMessage();
        if (user != null) {
            msg.what = 0;
        } else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);

    }

    public EditAccountAsyncTask(Handler mHandler) {
        super(mHandler);
    }
}
