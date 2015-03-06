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
    public MapRoute(GoogleMap mMap,Context context) {
        this.mMap = mMap;
        database = new DatabaseHelper(context);
        mContext = context;
    }
    public void createRouteDay(List<Position> positions) {
        float sumX=0,sumY=0;
        mMap.clear();
        List<Place> placeList = new ArrayList<>();
        for(Position position : positions) {
            Place place = position.getPlace();
            if(place==null) continue;
            placeList.add(place);
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .position(new LatLng(place.getLat(), place.getLon()))).setSnippet(position.getId()+"");
            sumX+= place.getLat();
            sumY+= place.getLon();
        }
        LatLng location = new LatLng(sumX/placeList.size(),sumY/placeList.size());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
        LatLng from=null,to;
        for(Position position : positions) {
            Place place = position.getPlace();
            if(place==null) {
                for(Position.Gps gps : position.getGpsList()){
                    to = new LatLng(gps.getLat(),gps.getLon());
                    if(from!=null)
                        displayRoute(from,to);
                    from = to;
                }
            }else {
                to = new LatLng(place.getLat(),place.getLon());
                if(from!=null)
                    displayRoute(from,to);
                from = to;
            }
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
