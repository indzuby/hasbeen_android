package co.hasBeen.day;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.geolocation.GeoFourSquare;
import co.hasBeen.geolocation.MapRoute;
import co.hasBeen.model.database.Category;
import co.hasBeen.model.database.Place;
import co.hasBeen.model.database.Position;
import co.hasBeen.utils.JsonConverter;

/**
 * Created by zuby on 2015-01-22.
 */
public class DayChangePlace extends ActionBarActivity implements OnMapReadyCallback {
    final static int SUCCESS = 0;
    final static int FAILED = - 1;
    DayPlaceAdapter mPlaceAdapter ;
    List<Category> mCategories;
    ListView mListView;
    Place mPlace;
    DatabaseHelper database;
    Position mPosition;
    float mLat,mLon;
    ImageView mPlaceIcon;
    TextView mPlaceName;
    TextView mPlaceCategory;
    GoogleMap mMap;

    private static final String TAG = "Map Activity";
    protected void initMap() throws Exception{
        mPlace = mPosition.getPlace();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
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
        Glide.with(this).load(mPlace.getCategoryIconPrefix() + "88" + mPlace.getCategoryIconSuffix()).into(mPlaceIcon);

    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        MapRoute mapRoute = new MapRoute(map,getBaseContext());
        UiSettings setting = map.getUiSettings();
        setting.setZoomControlsEnabled(true);
        setting.setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(false);
        setting.setRotateGesturesEnabled(false);
        mapRoute.addMarker(mLat,mLon);
    }

    protected void init() throws Exception{
        setContentView(R.layout.gallery_place_edit);
        String positionJson = getIntent().getStringExtra("position");
        mPlaceName = (TextView) findViewById(R.id.profileName);
        mPlaceCategory = (TextView) findViewById(R.id.placeIcon);
        mPlaceIcon = (ImageView) findViewById(R.id.place_icon);
        database = new DatabaseHelper(this);
        mPosition = JsonConverter.convertJsonToPosition(positionJson);
        initActionBar();
        initMap();

        mCategories = new ArrayList<>();
//        HasBeenCategory first = new HasBeenCategory();
//        first.setVenueId("first");
//        mCategories.add(first);
        mPlaceAdapter = new DayPlaceAdapter(this,mCategories,mPosition);
        mPlaceAdapter.mIndex = getIntent().getIntExtra("index",-1);
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
                                    List<Category> categories = (List) msg.obj;
                                    for(Category category : categories)
                                        mCategories.add(category);
                                    mPlaceAdapter.notifyDataSetChanged();
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
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
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
