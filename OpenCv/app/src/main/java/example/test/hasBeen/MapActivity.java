package example.test.hasBeen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import example.test.hasBeen.geolocation.GeoGoogle;

/**
 * Created by zuby on 2015-01-08.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    private ClusterManager<MyItem> mClusterManager;
    private static final String TAG = "Map Activity";
    GeoGoogle geo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.view_map);
        mapFragment.getMapAsync(this);
        geo = new GeoGoogle(this);
        /*Button selectPhoto = (Button) findViewById(R.id.btn_selectPhoto);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
//        new FourSquareAsyncTask().execute();
    }
    public void onMapReady(GoogleMap map) {
        mMap = map;
        UiSettings setting = map.getUiSettings();
//        LatLng location = geo.getLocation();
//        for(int i = 0; i <5;i++) {
//            map.addMarker(new MarkerOptions()
//                    .position(new LatLng(location.latitude-0.01*i,location.longitude-0.01*i))
//                    .title("hasBeen").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)).flat(true));
//        }
        mClusterManager = new ClusterManager<MyItem>(this,map);
        setting.setZoomControlsEnabled(true);
        setting.setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(geo.getLocation(), 4));
        map.setOnCameraChangeListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(TAG,"Marker Selectd");
                if(mMap.getCameraPosition().zoom>=8) {
                    Toast.makeText(getBaseContext(),"Max Zoom",Toast.LENGTH_LONG).show();
                }else
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom+1),500,null);
                return false;
            }
        });
        Toast.makeText(this,geo.getCity(geo.getLocation().latitude,geo.getLocation().longitude),Toast.LENGTH_LONG).show();;
        addItems();
        displayRoute(geo.getLocation(),new LatLng(geo.getLocation().latitude+0.03,geo.getLocation().longitude+0.03));

    }
    public class MyItem implements ClusterItem{
        private final LatLng mPosition;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

    }
    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = geo.getLocation().latitude;
        double lng = geo.getLocation().longitude;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng);
            mClusterManager.addItem(offsetItem);
        }
    }
   public void  displayRoute(LatLng from,LatLng to) {
       Polyline line = mMap.addPolyline(new PolylineOptions().add(from, to));
   }


}
