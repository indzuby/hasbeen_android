package co.hasBeen.map;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-08.
 */
public class MapActivity extends ActionBarActivity implements OnMapReadyCallback{
    Day mDay;
    GoogleMap mMap;
    MapRoute mMapRoute;
    MapFragment mMapFragment;
    Long beforeid=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        UiSettings setting = map.getUiSettings();
        setting.setRotateGesturesEnabled(false);
        setting.setZoomControlsEnabled(true);
        setting.setMyLocationButtonEnabled(false);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("marker clicked", marker.getSnippet());
                initFooter(Long.parseLong(marker.getSnippet()));
                return true;
            }
        });
        View zoomControls = mMapFragment.getView().findViewById(0x1);
        if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // ZoomControl is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();
            // Align it to - parent top|left
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            int margin = Util.convertDpToPixel(16, getBaseContext());
            params.setMargins(0,margin,margin,0);
            // Update margins, set to 10dp
        }
        try {
            init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void init() throws Exception{
        Long positionId = getIntent().getLongExtra("positionId",0);
        String data = getIntent().getStringExtra("day");
        mDay = JsonConverter.convertJsonToDay(data);
        mMapRoute = new MapRoute(mMap,getBaseContext());
        mMapRoute.createRouteDay(mDay.getPositionList());
        initActionBar();
        initFooter(positionId);
    }
    protected void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText(mDay.getTitle());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
    }
    Position position;
    protected void initFooter(Long positionId){
        View positionBox = findViewById(R.id.positionBox);
        LinearLayout photoBox = (LinearLayout) findViewById(R.id.photoBox);
        position = mDay.getPositionList().get(0);
        for(Position pos : mDay.getPositionList()) {
            if(pos.getId().equals(positionId)) {
                position = pos;
                break;
            }
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(position.getPlace().getLat(),position.getPlace().getLon())), 300, null);
        selectMarker(positionId+"");
        beforeid = positionId;
        List<Photo> photoList = position.getPhotoList();
        int photoCount = position.getPhotoList().size();
        photoBox.removeAllViews();
        for (int i = 0; i < photoCount && i<5; i++) {
            Photo photo = photoList.get(i);
            View addView = LayoutInflater.from(getBaseContext()).inflate(R.layout.alarm_photo, null);
            ImageView image = (ImageView) addView.findViewById(R.id.photo);
            if(photo.getSmallUrl()!=null) Glide.with(getBaseContext()).load(photo.getSmallUrl()).into(image);
            else Glide.with(getBaseContext()).load(photo.getPhotoPath()).into(image);
            photoBox.addView(addView);
        }
        TextView photoCountView = (TextView) findViewById(R.id.photoCount);
        photoCountView.setText("+" + (photoCount - 5));
        if (photoCount - 5 <= 0)
            photoCountView.setVisibility(View.GONE);
        positionBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("index", mDay.getPositionList().indexOf(position));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        ImageView placeIcon = (ImageView) findViewById(R.id.placeIcon);
        Glide.with(this).load(position.getPlace().getCategoryIconPrefix() + "88" + position.getPlace().getCategoryIconSuffix()).into(placeIcon);
        TextView placeName = (TextView) findViewById(R.id.placeName);
        TextView time = (TextView) findViewById(R.id.time);
        placeName.setText(position.getPlace().getName());
        time.setText(HasBeenDate.convertTime(position.getStartTime(),position.getEndTime(),this));
    }
    public void selectMarker(String snippet) {

        for(Marker marker : mMapRoute.getmMarkers())
            if(marker.getSnippet().equalsIgnoreCase(snippet)) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_select));
            }else if(beforeid!=null && marker.getSnippet().equalsIgnoreCase(beforeid+""))
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
    }
}
