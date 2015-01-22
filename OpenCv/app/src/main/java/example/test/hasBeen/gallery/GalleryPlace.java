package example.test.hasBeen.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.geolocation.GeoFourSquare;
import example.test.hasBeen.geolocation.GeoGoogle;
import example.test.hasBeen.model.HasBeenCategory;
import example.test.hasBeen.model.HasBeenPhoto;
import example.test.hasBeen.model.HasBeenPlace;
import example.test.hasBeen.model.HasBeenPosition;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-22.
 */
public class GalleryPlace extends ActionBarActivity implements OnMapReadyCallback {
    final static int SUCCESS = 0;
    final static int FAILED = - 1;
    GalleryPlaceAdapter mPlaceAdapter ;
    List<HasBeenCategory> mCategories;
    ListView mListView;
    Long mPositionId;
    HasBeenPlace mPlace;
    DatabaseHelper database;

    float mLat,mLon;
    ImageView mPlaceIcon;
    TextView mPlaceName;
    TextView mPlaceCategory;
    HasBeenPosition mPosition;
    GoogleMap mMap;
    private static final String TAG = "Map Activity";
    protected void initMap() throws Exception{
        mPlace = database.selectPlace(mPosition.getPlaceId());
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.view_map);
        mapFragment.getMapAsync(this);
        mLat = mPlace.getLat();
        mLon = mPlace.getLon();
        Log.i("mPlace name", mPlace.getName());
        if(mPlace.getName().length()>10)
            mPlaceName.setTextSize(16);
        if(mPlace.getName().length()>25)
            mPlaceName.setText(mPlace.getName().substring(0,25)+" ...");
        else
            mPlaceName.setText(mPlace.getName());
        mPlaceCategory.setText(mPlace.getCategoryName());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(mPlace.getCategoryIconPrefix() + "88" + mPlace.getCategoryIconSuffix());
                    final Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPlaceIcon.setImageBitmap(bm);
                            mPlaceIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        UiSettings setting = map.getUiSettings();
        map.addMarker(new MarkerOptions()
                    .position(new LatLng(mLat, mLon)));
        setting.setZoomControlsEnabled(true);
        setting.setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLat, mLon), 16));
    }

    protected void init() throws Exception{
        setContentView(R.layout.gallery_place_edit);
        mPositionId = getIntent().getLongExtra("positionId",0);

        mPlaceName = (TextView) findViewById(R.id.place_name);
        mPlaceCategory = (TextView) findViewById(R.id.place_category);
        mPlaceIcon = (ImageView) findViewById(R.id.place_icon);
        database = new DatabaseHelper(this);
        mPosition = database.selectPosition(mPositionId);
        initActionBar();
        initMap();

        mCategories = new ArrayList<>();
//        HasBeenCategory first = new HasBeenCategory();
//        first.setVenueId("first");
//        mCategories.add(first);
        mPlaceAdapter = new GalleryPlaceAdapter(this,mCategories,mPosition);
        mListView = (ListView) findViewById(R.id.place_list);
        mListView.setAdapter(mPlaceAdapter);
        new GeoFourSquare(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(final Message msg) {
                try {
                    switch (msg.what) {
                        case FAILED: // failed

                            break;
                        case SUCCESS: // success
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    List<HasBeenCategory> categories = (List) msg.obj;
                                    Iterator iterator = categories.iterator();
                                    while(iterator.hasNext()) {
                                        mCategories.add((HasBeenCategory) iterator.next());
                                        mPlaceAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute(mLat, mLon, null, 25);

    }
    protected void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.action_bar_back);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.action_bar_title);
        titleView.setText("Select Place");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
