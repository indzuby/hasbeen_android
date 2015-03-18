package co.hasBeen.gallery;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import co.hasBeen.model.api.Day;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-24.
 */
public class UploadAsyncTask extends HasBeenAsyncTask<Object,Void,Long> {
    final static String URL = Session.DOMAIN+"days";
    @Override
    protected Long doInBackground(Object... params) {
        try {
            Day dayUpload = (Day) params[1];
            uri = Uri.parse(URL);
            HttpPost post = new HttpPost(uri.toString());
            post.addHeader("User-Agent","Android");
            post.addHeader("Content-Type","application/json");
            post.addHeader("Authorization","Bearer " +params[0]);
            Gson gson = new Gson();
            String jsonString = gson.toJson(dayUpload);
            JSONObject param = new JSONObject(jsonString);
            post.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));
            response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 201) {
                HttpEntity entity = response.getEntity();
                //Read the server response and attempt to parse it as JSON
                JSONObject json = new JSONObject(EntityUtils.toString(entity));
                Long id = json.getLong("id");
                return id;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Long id) {

        Message msg = mHandler.obtainMessage();
        if(id!=null) {
            msg.what = 0;
            msg.obj = id;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

    public UploadAsyncTask(Handler handler) {
        super(handler);
    }
}
