package co.hasBeen.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
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
import co.hasBeen.model.pin.PhotoPin;

/**
 * Created by zuby on 2015-01-30.
 */
public class PhotoMarker extends DefaultClusterRenderer<PhotoPin> {

    TextView mClusterCount;
    private IconGenerator mClusterIconGenerator;
    Context mContext;
    private ImageView mClusterImageView;
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
    }

    @Override
    protected void onBeforeClusterRendered(final Cluster<PhotoPin> cluster, final MarkerOptions markerOptions) {

        final PhotoPin photo = cluster.getItems().iterator().next();

        mClusterImageView.setImageBitmap(photo.getImage());
        mClusterCount.setVisibility(View.VISIBLE);
        mClusterCount.setText(cluster.getSize() + "");
        Bitmap icon = mClusterIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

    }

    @Override
    protected void onBeforeClusterItemRendered(PhotoPin photo, final MarkerOptions markerOptions) {
//        Glide.with(mContext).load(photo.getPhoto().getSmallUrl()).into(mClusterImageView);
        mClusterImageView.setImageBitmap(photo.getImage());
        mClusterCount.setVisibility(View.GONE);
        Bitmap icon = mClusterIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<PhotoPin> cluster) {
        return cluster.getSize() > 1;
    }
}
