package co.hasBeen.map.pin;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import co.hasBeen.model.api.Photo;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-30.
 */
public class PhotoPin  implements ClusterItem{
    Photo mPhoto;
    private LatLng mPosition;
    Bitmap image;
    String url;
    public Photo getPhoto() {
        return mPhoto;
    }

    public Bitmap getImage() {
        return image;
    }

    public PhotoPin(final Photo photo,Context context) {
        mPhoto = photo;
        mPosition = new LatLng(photo.getLat(),photo.getLon());
        if(mPhoto.getImage()!=null)
            image = photo.getImage();
        else {
            Glide.with(context).load(mPhoto.getSmallUrl()).asBitmap().into(new SimpleTarget<Bitmap>(Util.convertDpToPixel(40, context), Util.convertDpToPixel(40, context)) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    image = Bitmap.createBitmap(resource);
                    mPhoto.setImage(image);
                }
            });
        }
        url = mPhoto.getSmallUrl();
    }

    public String getUrl() {
        return url;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
