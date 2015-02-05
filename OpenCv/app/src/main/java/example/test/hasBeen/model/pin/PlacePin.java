package example.test.hasBeen.model.pin;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import example.test.hasBeen.model.api.PlaceApi;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-30.
 */
public class PlacePin implements ClusterItem{
    PlaceApi mPlace;
    private LatLng mPosition;
    Bitmap image;

    public PlaceApi getmPlace() {
        return mPlace;
    }

    public Bitmap getImage() {
        return image;
    }

    public PlacePin(final PlaceApi place, Context context) {
        mPlace = place;
        mPosition = new LatLng(place.getLat(),place.getLon());
        if(mPlace.getImage()!=null)
            image = mPlace.getImage();
        else {
            Glide.with(context).load(mPlace.getCategoryIconPrefix()+"88"+mPlace.getCategoryIconSuffix()).asBitmap().into(new SimpleTarget<Bitmap>(Util.convertDpToPixel(40, context), Util.convertDpToPixel(40, context)) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    image = resource;
                    mPlace.setImage(image);
                }
            });
        }
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
