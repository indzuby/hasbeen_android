package example.test.hasBeen.model.pin;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import example.test.hasBeen.model.database.Day;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-30.
 */
public class DayPin implements ClusterItem{
    Day mDay;
    private LatLng mPosition;
    Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public DayPin(Day day,Context context) {
        mDay = day;
        mPosition = new LatLng(mDay.getMainPlace().getLat(),mDay.getMainPlace().getLon());
        if(mDay.getImage()!=null) {
            image = mDay.getImage();
        }else {
                Glide.with(context).load(mDay.getMainPhoto().getSmallUrl()).asBitmap().into(new SimpleTarget<Bitmap>(Util.convertDpToPixel(32, context), Util.convertDpToPixel(32, context)) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    image = Util.getBitmapClippedCircle(resource) ;
//                        image = resource;

                        mDay.setImage(image);
                    }
                });
//            try {
//                URL url = new URL(mDay.getPhotoList().get(0).getSmallUrl());
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setDoInput(true);
//                conn.connect();
//                InputStream is = conn.getInputStream();
//                image = BitmapFactory.decodeStream(is);
//                mDay.setMainPhoto(image);
//            }catch(Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    public Day getDay() {
        return mDay;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
