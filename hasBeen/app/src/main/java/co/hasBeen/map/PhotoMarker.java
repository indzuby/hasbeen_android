package co.hasBeen.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import co.hasBeen.R;
import co.hasBeen.map.pin.PhotoPin;

/**
 * Created by zuby on 2015-01-30.
 */
public class PhotoMarker extends DefaultClusterRenderer<PhotoPin> {

    TextView mClusterCount;
    private IconGenerator mClusterIconGenerator;
    private IconGenerator mIconGenerator;
    Context mContext;
    private ImageView mClusterImageView;
    private ImageView mImageView;
    int width, height;

    public PhotoMarker(Context context, GoogleMap map, ClusterManager<PhotoPin> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mClusterIconGenerator = new IconGenerator(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.photo_pin, null, false);
        mClusterIconGenerator.setContentView(layout);
        mClusterIconGenerator.setBackground(null);

        mClusterImageView = (ImageView) layout.findViewById(R.id.photo);
        width = mClusterImageView.getLayoutParams().width;
        height = mClusterImageView.getLayoutParams().height;

        mClusterCount = (TextView) layout.findViewById(R.id.clusterCount);

        mIconGenerator = new IconGenerator(context);
        mIconGenerator.setBackground(null);
        mImageView = new ImageView(context);
        int height = (int)context.getResources().getDimension(R.dimen.marker_height);
        int width = (int)context.getResources().getDimension(R.dimen.marker_width);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(height,width));
        mIconGenerator.setContentView(mImageView);
    }

    @Override
    protected void onBeforeClusterRendered(final Cluster<PhotoPin> cluster, final MarkerOptions markerOptions) {

        final PhotoPin photo = cluster.getItems().iterator().next();

        mClusterImageView.setImageBitmap(photo.getImage());
        mClusterCount.setVisibility(View.VISIBLE);
        mClusterCount.setText(cluster.getSize() + "");
        Bitmap icon = mClusterIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        icon.recycle();
    }

    @Override
    protected void onBeforeClusterItemRendered(PhotoPin photo, final MarkerOptions markerOptions) {
//        Glide.with(mContext).load(photo.getPhoto().getSmallUrl()).into(mClusterImageView);
        mImageView.setImageBitmap(photo.getImage());
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        icon.recycle();
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<PhotoPin> cluster) {
        return cluster.getSize() > 1;
    }

}
