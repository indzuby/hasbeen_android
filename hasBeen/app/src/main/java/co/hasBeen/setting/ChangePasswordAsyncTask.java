package co.hasBeen.setting;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-25.
 */
public class ChangePasswordAsyncTask  extends HasBeenAsyncTask<String, Void, Boolean> {
    final static String URL = Session.DOMAIN + "account/updatePassword";

    @Override
    protected Boolean doInBackground(String... params) {
        try {

            uri = Uri.parse(URL);
            HttpPut put = new HttpPut(uri.toString());
            put.addHeader("User-Agent", "Android");
            put.addHeader("Content-Type", "application/json");
            put.addHeader("Authorization", "Bearer " + params[0]);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("password",params[1]);
            jsonObject.put("newPassword",params[2]);
            jsonObject.put("newPasswordConfirm",params[3]);
            put.setEntity(new StringEntity(jsonObject.toString(), HTTP.UTF_8));
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

    public ChangePasswordAsyncTask(Handler mHandler) {
        super(mHandler);
    }
}
