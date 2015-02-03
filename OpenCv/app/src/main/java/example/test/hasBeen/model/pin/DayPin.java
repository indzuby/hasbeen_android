package example.test.hasBeen.model.pin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.InputStream;
import java.net.URL;

import example.test.hasBeen.model.api.DayApi;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-30.
 */
public class DayPin implements ClusterItem{
    DayApi mDay;
    private LatLng mPosition;
    Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public DayPin(DayApi day) {
        mDay = day;
        mPosition = new LatLng(mDay.getMainPlace().getLat(),mDay.getMainPlace().getLon());
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(mDay.getPhotoList().get(0).getSmallUrl());
                    InputStream in = url.openStream();
                    image = Util.getBitmapClippedCircle(BitmapFactory.decodeStream(in));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public DayApi getDay() {
        return mDay;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
