package co.hasBeen.geolocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.maps.android.ui.IconGenerator;

import co.hasBeen.R;
import co.hasBeen.utils.CircleTransform;

/**
 * Created by zuby on 2015-01-30.
 */
public class PlaceMarker {

    public static IconGenerator getMarker(Context context, String url) {
        IconGenerator iconGenerator = new IconGenerator(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.day_pin, null, false);
        iconGenerator.setContentView(layout);
        iconGenerator.setBackground(null);
        ImageView imageView = (ImageView) layout.findViewById(R.id.photo);
        Glide.with(context).load(url).asBitmap().transform(new CircleTransform(context)).into(imageView);
        return iconGenerator;
    }

}
