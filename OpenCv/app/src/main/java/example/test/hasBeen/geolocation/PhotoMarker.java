package example.test.hasBeen.geolocation;

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

import example.test.hasBeen.R;
import example.test.hasBeen.model.pin.PhotoPin;

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
        mClusterIconGenerator.setBackground(null);

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
