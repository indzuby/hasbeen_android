package co.hasBeen.profile.map;

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
 * Created by zuby on 2015-01-29.
 */
public class ProfilePhotoAsyncTask extends HasBeenAsyncTask<Object,Void,List<Photo>> {

//    final static String URL = "https://gist.githubusercontent.com/indzuby/01dd9766562e90d0af7e/raw/d4aca1859f83a9599dbe15541624b1499aae8ea2/photoNearBy";
    final static String URL = Session.DOMAIN+"users/";
    @Override
    protected List<Photo> doInBackground(Object... params) {
        try {
            if(params.length<=2)
                uri = Uri.parse(URL+params[1]+"/allPhotos");
            else
                uri = Uri.parse(URL+params[1]+"/photos?lastPhotoId="+params[2]);
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

    public ProfilePhotoAsyncTask(Handler mHandler) {
        super(mHandler);
    }
}
