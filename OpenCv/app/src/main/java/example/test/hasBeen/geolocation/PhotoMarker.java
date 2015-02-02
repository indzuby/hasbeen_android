package example.test.hasBeen.geolocation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.PhotoApi;
import example.test.hasBeen.model.pin.PhotoPin;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-30.
 */
public class PhotoMarker extends DefaultClusterRenderer<PhotoPin> {

    TextView mClisterCount;
    private IconGenerator mClusterIconGenerator ;
    Context mContext;
    private ImageView mClusterImageView;
    public PhotoMarker(Context context, GoogleMap map, ClusterManager<PhotoPin> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mClusterIconGenerator = new IconGenerator(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.photo_pin, null, false);
        mClusterIconGenerator.setContentView(layout);

        mClusterImageView = (ImageView) layout.findViewById(R.id.photo);
        mClisterCount = (TextView) layout.findViewById(R.id.clusterCount);
    }

    @Override
    protected void onBeforeClusterRendered(final Cluster<PhotoPin> cluster, final MarkerOptions markerOptions) {

        final PhotoPin photo = cluster.getItems().iterator().next();

//        Glide.with(mContext).load(photo.getPhoto().getSmallUrl()).into(mClusterImageView);
        mClusterImageView.setImageBitmap(photo.getImage());
        mClisterCount.setVisibility(View.VISIBLE);
        mClisterCount.setText(cluster.getSize()+"");
        Bitmap icon = mClusterIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

    }

    @Override
    protected void onBeforeClusterItemRendered(PhotoPin photo, final MarkerOptions markerOptions) {
//        Glide.with(mContext).load(photo.getPhoto().getSmallUrl()).into(mClusterImageView);
        mClusterImageView.setImageBitmap(photo.getImage());
        mClisterCount.setVisibility(View.GONE);
        Bitmap icon = mClusterIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
    @Override
    protected boolean shouldRenderAsCluster(Cluster<PhotoPin> cluster) {
        return cluster.getSize() >1 ;
    }
}
