package example.test.hasBeen.model.pin;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

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

    public DayPin(DayApi day,Context context) {
        mDay = day;
        mPosition = new LatLng(mDay.getMainPlace().getLat(),mDay.getMainPlace().getLon());
        if(mDay.getMainPhoto()!=null) {
            image = mDay.getMainPhoto();
        }else {
            Glide.with(context).load(mDay.getPhotoList().get(0).getSmallUrl()).asBitmap().into(new SimpleTarget<Bitmap>(Util.convertDpToPixel(32, context), Util.convertDpToPixel(32, context)) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    image = Util.getBitmapClippedCircle(resource);
                    mDay.setMainPhoto(image);
                }
            });
        }
    }

    public DayApi getDay() {
        return mDay;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
