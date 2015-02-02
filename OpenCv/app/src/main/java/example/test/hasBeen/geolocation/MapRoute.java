package example.test.hasBeen.geolocation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.model.api.PhotoApi;
import example.test.hasBeen.model.api.PlaceApi;
import example.test.hasBeen.model.api.PositionApi;
import example.test.hasBeen.model.database.Place;
import example.test.hasBeen.model.database.Position;
import example.test.hasBeen.model.pin.PhotoPin;
import example.test.hasBeen.photo.PhotoView;

/**
 * Created by zuby on 2015-01-23.
 */
public class MapRoute {
    GoogleMap mMap;
    DatabaseHelper database;
    Context mContext;
    private ClusterManager<MyItem> mClusterManager;
    public MapRoute(GoogleMap mMap,Context context) {
        this.mMap = mMap;
        database = new DatabaseHelper(context);
        mContext = context;
    }
    public void createRouteDay(Long dayId){
        mMap.clear();
        try {
            List<Position> positionList = database.selectPositionByDayId(dayId);
            List<Place> placeList = new ArrayList<>();
            for(Position position : positionList){
                Long placeId = position.getPlaceId();
                Place place = database.selectPlace(placeId);
                placeList.add(place);
                mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(place.getLat(),place.getLon())));
            }
            LatLng location = new LatLng(placeList.get(0).getLat(),placeList.get(0).getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            for(int i = 1; i <placeList.size();i++) {
                LatLng from = new LatLng(placeList.get(i-1).getLat(),placeList.get(i-1).getLon());
                LatLng to = new LatLng(placeList.get(i).getLat(),placeList.get(i).getLon());
                displayRoute(from,to);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void createRouteDay(List<PositionApi> positions) {
        float sumLat=0;
        float sumLon=0;
        mMap.clear();
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        List<PlaceApi> placeList = new ArrayList<>();
        for(PositionApi position : positions) {
            PlaceApi place = position.getPlace();
            if(place==null) continue;
            placeList.add(place);
            mMap.addMarker(new MarkerOptions().position(new LatLng(place.getLat(), place.getLon())));
            sumLat += place.getLat();
            sumLon += place.getLon();
        }
        LatLng location = new LatLng(sumLat/placeList.size(),sumLon/placeList.size());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11));
        for(int i = 1; i <placeList.size();i++) {
            LatLng from = new LatLng(placeList.get(i - 1).getLat(),placeList.get(i - 1).getLon());
            LatLng to = new LatLng(placeList.get(i).getLat(),placeList.get(i).getLon());
            displayRoute(from,to);
        }

    }
    public void addMarkerCluster(List<LatLng> latLngs){
        mMap.clear();
        mClusterManager = new ClusterManager<MyItem>(mContext,mMap);

        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        for(LatLng location : latLngs) {
            Log.i("cluter location", location.latitude + "," + location.longitude);
            mClusterManager.addItem(new MyItem(location.latitude, location.longitude));
        }
    }
    public void addMarkerClusterPhoto(List<PhotoApi> photos) {
        mMap.clear();
        ClusterManager<PhotoPin> clusterManager = new ClusterManager<PhotoPin>(mContext,mMap);
        clusterManager.setRenderer(new PhotoMarker(mContext,mMap,clusterManager));
        mMap.setOnCameraChangeListener(clusterManager) ;
        mMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<PhotoPin>() {
            boolean flag = false;
            @Override
            public boolean onClusterItemClick(PhotoPin photoPin) {
                if(!flag) {
                    flag = true;
                    Intent intent = new Intent(mContext, PhotoView.class);
                    mContext.startActivity(intent);
                    flag = false;
                }
                return true;
            }
        });
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<PhotoPin>() {
            @Override
            public boolean onClusterClick(Cluster<PhotoPin> photoPinCluster) {
                Log.i("Cluster","click");
                LatLng location = photoPinCluster.getItems().iterator().next().getPosition();
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom+1));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom+1),500,null);
                return true;
            }
        });
        for(PhotoApi photo : photos) {
            clusterManager.addItem(new PhotoPin(photo));
        }
        clusterManager.cluster();

    }

    public void  displayRoute(LatLng from,LatLng to) {
        Polyline line = mMap.addPolyline(new PolylineOptions().add(from, to));
    }
    public void addMarker(float lat, float lon) {
        LatLng location = new LatLng(lat,lon);
        mMap.addMarker(new MarkerOptions()
                .position(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
    }
}
