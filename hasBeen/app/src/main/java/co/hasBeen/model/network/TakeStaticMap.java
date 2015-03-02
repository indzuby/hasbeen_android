package co.hasBeen.model.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.net.URL;

import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-02.
 */
public class TakeStaticMap extends AsyncTask<Object, Void, Bitmap> {
    final static String MAP_URL = "http://maps.googleapis.com/maps/api/staticmap?size=480x240&scale=2&zoom=14&markers=icon:http://image.hasbeen.co/common/marker.png%7C";
    Handler mHandler;
    public TakeStaticMap(Handler mHandler) {
        this.mHandler = mHandler;
    }
    @Override
    protected Bitmap doInBackground(Object... params) {
        try {
            float lat = (float) params[0];
            float lon = (float) params[1];
            HttpClient client = new DefaultHttpClient();
            URL url = new URL(MAP_URL + lat + "," + lon);
            HttpGet request = new HttpGet(url.toString());
            InputStream in = client.execute(request).getEntity().getContent();
            Bitmap bmp =  BitmapFactory.decodeStream(in);
            in.close();
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Bitmap s) {
        super.onPostExecute(s);
        Message msg = mHandler.obtainMessage();
        if(s!=null) {
            msg.what = 0;
            msg.obj = Util.getBinaryBitmap(s,true);
            mHandler.sendMessage(msg);
        }else {
            msg.what=-1;
            mHandler.sendMessage(msg);
        }
    }
}
