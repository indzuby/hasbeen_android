package co.hasBeen.map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.map.pin.DayPin;
import co.hasBeen.map.pin.PhotoPin;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Place;
import co.hasBeen.model.api.Position;
import co.hasBeen.photo.PhotoActivity;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-23.
 */
public class MapRoute {
    GoogleMap mMap;
    DataBaseHelper database;
    Context mContext;
    List <Marker> mMarkers;
    ClusterDayDialog clusterDayDialog;
    ClusterPhotoDialog clusterPhotoDialog;
    public MapRoute(GoogleMap mMap,Context context) {
        this.mMap = mMap;
        database = new DataBaseHelper(context);
        mContext = context;
    }

    public List<Marker> getmMarkers() {
        return mMarkers;
    }

    public void createRouteDay(List<Position> positions) {
        float sumX=0,sumY=0;
        mMap.clear();
        mMarkers = new ArrayList<>();
        List<Place> placeList = new ArrayList<>();
        for(Position position : positions) {
            Place place = position.getPlace();
            if(place==null) continue;
            placeList.add(place);
            Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .position(new LatLng(place.getLat(), place.getLon())));
            marker.setSnippet(position.getId()+"");
            mMarkers.add(marker);
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
    ClusterManager<DayPin> dayCluster ;
    List<DayPin> dayPins;
    public void addMarkerClusterDay(List<Day> days){
        mMap.clear();
        dayCluster = new ClusterManager<DayPin>(mContext,mMap);
        dayCluster.setRenderer(new DayMarker(mContext,mMap,dayCluster));
        mMap.setOnCameraChangeListener(dayCluster);
        mMap.setOnMarkerClickListener(dayCluster);
        dayCluster.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<DayPin>() {
            boolean flag = false;
            @Override
            public boolean onClusterItemClick(DayPin dayPin) {
                clusterDayDialog = new ClusterDayDialog(mContext, dayPin);
                clusterDayDialog.show();
                return true;
            }
        });
        dayCluster.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<DayPin>() {
            @Override
            public boolean onClusterClick(Cluster<DayPin> dayPinCluster) {
                Log.i("Cluster","click");
                LatLng location = dayPinCluster.getItems().iterator().next().getPosition();
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom+1));
                if(needDialog(mMap.getCameraPosition().zoom,dayPinCluster.getSize())) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom),500,null);
                    clusterDayDialog = new ClusterDayDialog(mContext, dayPinCluster.getItems());
                    clusterDayDialog.show();
                }else
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom+2),500,null);
                return true;
            }
        });
        dayPins = new ArrayList<>();
        for(Day day : days) {
//            Log.i("cluter location", location.latitude + "," + location.longitude);
            DayPin dayPin = new DayPin(day,mContext);
            dayPins.add(dayPin);
            dayCluster.addItem(dayPin);
        }
        dayCluster.cluster();
        LatLng location = new LatLng(days.get(0).getMainPlace().getLat(),days.get(0).getMainPlace().getLon());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 3));
    }
    ClusterManager<PhotoPin> photoCluster;
    List<PhotoPin> photoPins;
    public void addMarkerClusterPhoto(List<Photo> photos) {
        mMap.moveCamera(CameraUpdateFactory.zoomIn());
        mMap.clear();
        photoCluster = new ClusterManager<PhotoPin>(mContext,mMap);
        photoCluster.setRenderer(new PhotoMarker(mContext,mMap,photoCluster));
        mMap.setOnCameraChangeListener(photoCluster) ;
        mMap.setOnMarkerClickListener(photoCluster);
        photoCluster.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<PhotoPin>() {
            boolean flag = false;
            @Override
            public boolean onClusterItemClick(PhotoPin photoPin) {
                if(!flag) {
                    flag = true;
                    Intent intent = new Intent(mContext, PhotoActivity.class);
                    intent.putExtra("id",photoPin.getPhoto().getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    flag = false;
                }
                return true;
            }
        });
        photoCluster.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<PhotoPin>() {
            @Override
            public boolean onClusterClick(Cluster<PhotoPin> photoPinCluster) {
                Log.i("Cluster","click");
                LatLng location = photoPinCluster.getItems().iterator().next().getPosition();
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom+1));
                if(needDialog(mMap.getCameraPosition().zoom,photoPinCluster.getSize())) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom),500,null);
                    clusterPhotoDialog = new ClusterPhotoDialog(mContext,photoPinCluster.getItems());
                    clusterPhotoDialog.show();
                }else
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom+2),500,null);
                return true;
            }
        });
        photoPins = new ArrayList<>();
        for(Photo photo : photos) {
            PhotoPin photoPin = new PhotoPin(photo,mContext);
            photoPins.add(photoPin);
            photoCluster.addItem(photoPin);
        }
        photoCluster.cluster();
        LatLng location = new LatLng(photos.get(0).getLat(),photos.get(0).getLon());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 3));
    }
    public void removeDayPin(Long id){
        for(DayPin dayPin : dayPins) {
            if(dayPin.getDay().getId().equals(id)) {
                dayCluster.removeItem(dayPin);
                dayCluster.notifyAll();
                break;
            }
        }
    }
    public void removePhotoPin(Long id){
        for(PhotoPin photoPin : photoPins) {
            if(photoPin.getPhoto().getId().equals(id)) {
                photoCluster.removeItem(photoPin);
                photoCluster.notifyAll();
                break;
            }
        }
    }
    protected boolean needDialog(float zoom , int size){
        if(zoom>=14 || (zoom>=12 && size<=9) || (zoom>=8 && size<=4))
            return true;
        return false;
    }
    public void  displayRoute(LatLng from,LatLng to) {
//        mMap.addPolyline(new PolylineOptions().add(from, to)).setColor(mContext.getResources().getColor(R.color.theme_color));
        Polyline line = mMap.addPolyline(new PolylineOptions().add(from, to));
        line.setWidth(Util.convertDpToPixel(3,mContext));
        line.setColor(mContext.getResources().getColor(R.color.poly_line));
        line.setGeodesic(true);
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
