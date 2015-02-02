package example.test.hasBeen.model.pin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.InputStream;
import java.net.URL;

import example.test.hasBeen.model.api.PhotoApi;

/**
 * Created by zuby on 2015-01-30.
 */
public class PhotoPin  implements ClusterItem{
    PhotoApi photo;
    private LatLng mPosition;
    Bitmap image;
    public PhotoApi getPhoto() {
        return photo;
    }

    public Bitmap getImage() {
        return image;
    }

    public PhotoPin(PhotoApi phot) {
        this.photo = phot;
        mPosition = new LatLng(photo.getLat(),photo.getLon());
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(photo.getSmallUrl());
                    InputStream in = url.openStream();
                    image = BitmapFactory.decodeStream(in);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
