package co.hasBeen.photo;

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

import co.hasBeen.model.api.Photo;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-31.
 */
public class PhotoListAsyncTask extends HasBeenAsyncTask<Object,Void,List<Photo>> {
    final static String URL=Session.DOMAIN+"photos/";
    public PhotoListAsyncTask(Handler mHandler) {
        super(mHandler);
    }

    @Override
    protected List<Photo> doInBackground(Object... params) {
        try {
            uri = Uri.parse(URL + params[1] + "/list");
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
                List<Photo> photos = JsonConverter.convertJsonPhotoList(reader);
                content.close();
                return photos;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(List<Photo>  photos) {

        Message msg = mHandler.obtainMessage();
        if(photos !=null) {
            msg.obj = photos;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }

}
