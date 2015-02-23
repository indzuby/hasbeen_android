package co.hasBeen.geolocation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.day.DayView;
import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Place;
import co.hasBeen.model.pin.DayPin;
import co.hasBeen.photo.PhotoView;
import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.model.database.Photo;
import co.hasBeen.model.database.Position;
import co.hasBeen.model.pin.PhotoPin;

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
//                Bitmap icon =PlaceMarker.getMarker(mContext, place.getCategoryIconPrefix() + "88" + place.getCategoryIconSuffix()).makeIcon();
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                        .position(new LatLng(place.getLat(), place.getLon())));
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
    public void createRouteDay(List<Position> positions) {
        mMap.clear();
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        List<Place> placeList = new ArrayList<>();
        for(Position position : positions) {
            Place place = position.getPlace();
            if(place==null) continue;
            placeList.add(place);
//            Bitmap icon =PlaceMarker.getMarker(mContext, place.getCategoryIconPrefix() + "88" + place.getCategoryIconSuffix()).makeIcon();
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .position(new LatLng(place.getLat(), place.getLon())));
        }
        LatLng location = new LatLng(placeList.get(0).getLat(),placeList.get(0).getLon());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
        for(int i = 1; i <placeList.size();i++) {
            LatLng from = new LatLng(placeList.get(i - 1).getLat(),placeList.get(i - 1).getLon());
            LatLng to = new LatLng(placeList.get(i).getLat(),placeList.get(i).getLon());
            displayRoute(from,to);
        }

    }
    public void addMarkerCluster(List<Day> days){
        mMap.clear();
        ClusterManager<DayPin> clusterManager = new ClusterManager<DayPin>(mContext,mMap);
        clusterManager.setRenderer(new DayMarker(mContext,mMap,clusterManager));
        mMap.setOnCameraChangeListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<DayPin>() {
            boolean flag = false;
            @Override
            public boolean onClusterItemClick(DayPin dayPin) {
                if(!flag) {
                    flag = true;
                    Intent intent = new Intent(mContext, DayView.class);
                    intent.putExtra("dayId",dayPin.getDay().getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    flag = false;
                }
                return true;
            }
        });
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<DayPin>() {
            @Override
            public boolean onClusterClick(Cluster<DayPin> dayPinCluster) {
                Log.i("Cluster","click");
                LatLng location = dayPinCluster.getItems().iterator().next().getPosition();
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom+1));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom+1),500,null);
                return true;
            }
        });
        for(Day day : days) {
//            Log.i("cluter location", location.latitude + "," + location.longitude);
            clusterManager.addItem(new DayPin(day,mContext));
        }
        clusterManager.cluster();
    }
    public void addMarkerClusterPhoto(List<Photo> photos) {
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
                    intent.putExtra("photoId",photoPin.getPhoto().getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        for(Photo photo : photos) {
            clusterManager.addItem(new PhotoPin(photo,mContext));
        }
        clusterManager.cluster();

    }

    public void  displayRoute(LatLng from,LatLng to) {
        mMap.addPolyline(new PolylineOptions().add(from, to)).setColor(mContext.getResources().getColor(R.color.theme_color));
    }
    public void addMarker(float lat, float lon) {
        LatLng location = new LatLng(lat,lon);
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
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
