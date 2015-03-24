package co.hasBeen.setting;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-24.
 */
public class TakeFbProfileAsyncTask extends HasBeenAsyncTask<Object,Void,String>{
    final static String URL = Session.DOMAIN+"account/updateProfileImageFromSocial";
    @Override
    protected String doInBackground(Object... params) {
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
                JSONObject json = new JSONObject(EntityUtils.toString(entity));
                String url = json.getString("url");
                return url;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String url) {
        Message msg = mHandler.obtainMessage();
        if(url!=null) {
            msg.obj = url;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public TakeFbProfileAsyncTask(Handler mHandler) {
        super(mHandler);
    }
}
