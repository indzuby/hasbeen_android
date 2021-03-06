package co.hasBeen.account;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-09.
 */
public class DeviceAsyncTask extends HasBeenAsyncTask<Object,Void ,Boolean> {
    final static String URL = Session.DOMAIN+"account/updateDeviceId/";

    public DeviceAsyncTask(Handler handler) {
        super(handler);
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        try {
            uri = Uri.parse(URL );
            HttpPut put = new HttpPut(uri.toString());
            put.addHeader("User-Agent","Android");
            put.addHeader("Content-Type", "application/json");
            put.addHeader("Authorization", "Bearer " + params[0]);
            JSONObject param = new JSONObject();
            param.put("deviceId",params[1]);
            put.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));
            response = client.execute(put);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 200) {
                Log.i("Device register","Success");
                return true;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        Message msg = mHandler.obtainMessage();
        if(status!=null) {
            msg.what= 0 ;
        }else
            msg.what = -1;
        mHandler.sendMessage(msg);
    }
}
