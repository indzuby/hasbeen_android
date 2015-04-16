package co.hasBeen.profile.map;

import android.content.Intent;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.MainActivity;
import co.hasBeen.R;
import co.hasBeen.account.LoginActivity;
import co.hasBeen.map.MapRoute;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Loved;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Position;
import co.hasBeen.model.api.User;
import co.hasBeen.profile.ProfileAsyncTask;
import co.hasBeen.profile.ProfileFragment;
import co.hasBeen.profile.follow.FollowView;
import co.hasBeen.setting.ProfileImageListner;
import co.hasBeen.setting.SettingView;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-08.
 */
public class ProfileMap extends ActionBarActivity implements OnMapReadyCallback,View.OnClickListener{
    final static int DAY = 1;
    final static int PHOTO = 2;
    final static int LOVE = 3;
    User mUser;
    int nowTab = DAY;
    int subTab = DAY;
    GoogleMap mMap;
    MapFragment mMapFragment;
    public MapRoute mMapRoute;

    List<Day> mDays;
    List<Photo> mPhotos;
    List<Day> mLikeDays;
    List<Photo> mLikePhotos;

    String mAccessToken;
    Long mUserId;
    List<Loved> mLovedPhotos;
    List<Loved> mLovedDays;
    Handler dayHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    List days = (List<Day>) msg.obj;
                        mDays = days;
                        dayRendering(mDays);
                    break;
                case -1:
                    break;
            }
        }
    };
    Handler photoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    List photos =(List<Photo>) msg.obj;
                    mPhotos = photos;
                    photoRendering(mPhotos);
                    break;
                case -1:
                    break;
            }
        }
    };
    Handler likeDayHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    List days = (List<Loved>) msg.obj;;
                    if(mLovedDays==null ||mLovedDays.size()!= days.size()) {
                        mLovedDays = days;
                        mLikeDays = new ArrayList<>();
                        for (Loved love : mLovedDays)
                            mLikeDays.add(love.getDay());
                    }
                    dayRendering(mLikeDays);
                    break;
                case -1:
                    break;
            }
        }
    };
    Handler likePhotoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    List photos = (List<Loved>) msg.obj;
                    if(mLovedPhotos==null||photos.size()!=mLovedPhotos.size()) {
                        mLovedPhotos = photos;
                        mLikePhotos = new ArrayList<>();
                        for (Loved love : mLovedPhotos)
                            mLikePhotos.add(love.getPhoto());
                    }
                    photoRendering(mLikePhotos);
                    break;
                case -1:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_map);
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

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mUser = (User) msg.obj;
                    initProfile();
                    mapRendering(DAY);
                    break;
                case -1:
                    break;
            }
        }
    };
    protected void init() throws Exception{
        mUserId = getIntent().getLongExtra("userId",0);
        mAccessToken = Session.getString(this, "accessToken", null);
        mMapRoute = new MapRoute(mMap,this);
        initActionBar();
        startLoading();
        new ProfileAsyncTask(handler).execute(mAccessToken,mUserId);
    }
    TextView titleView;
    protected void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText(getString(R.string.action_bar_profile_title));
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
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Profile Map View");
        Localytics.upload();
    }

    protected void dayRendering(final List<Day> days) {
        if(days.size()>0) {
            try {
                LatLng location = new LatLng(days.get(0).getMainPlace().getLat(), days.get(0).getMainPlace().getLon());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                mMapRoute.addMarkerClusterDay(days);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }else
            mMap.clear();
        stopLoading();
    }
    public void nowTabIndicator(int id){
        clearSelect();
        findViewById(id).setSelected(true);
        if(id == R.id.dayButton)
            ((TextView) findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_black));
        else if(id == R.id.photoButton)
            ((TextView) findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_black));
        else {
            findViewById(R.id.likeBar).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_black));
        }
    }
    protected void photoRendering(final List<Photo> photos) {
        if(photos.size()>0) {
            try {
                LatLng location = new LatLng(photos.get(0).getLat(), photos.get(0).getLon());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                mMapRoute.addMarkerClusterPhoto(photos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
            mMap.clear();
        stopLoading();
    }
    protected void clearSelect() {
        findViewById(R.id.dayButton).setSelected(false);
        findViewById(R.id.photoButton).setSelected(false);
        findViewById(R.id.likeButton).setSelected(false);
        findViewById(R.id.likeBar).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView) findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView) findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_gray));
        if(mMap!=null) mMap.clear();
    }

    protected void initProfile() {

        TextView dayCount = (TextView) findViewById(R.id.dayCount);
        TextView photoCount = (TextView) findViewById(R.id.photoCount);
        TextView loveCount = (TextView) findViewById(R.id.loveCount);
        dayCount.setText(mUser.getDayCount() + "");
        photoCount.setText(mUser.getPhotoCount() + "");
        loveCount.setText(mUser.getLoveCount() + "");
        titleView.setText(getProfileTitle(Util.parseName(mUser, this)));
        RelativeLayout dayButton = (RelativeLayout) findViewById(R.id.dayButton);
        RelativeLayout photoButton = (RelativeLayout) findViewById(R.id.photoButton);
        RelativeLayout likeButton = (RelativeLayout) findViewById(R.id.likeButton);
        dayButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);
        likeButton.setOnClickListener(this);
        nowTabIndicator(R.id.dayButton);
    }

    protected void initLikeBar() {
        LinearLayout dayButton = (LinearLayout) findViewById(R.id.likeDayButton);
        LinearLayout photoButton = (LinearLayout) findViewById(R.id.likePhotoButton);
        dayButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);
        findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
        ((ImageView) findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day);
        ((TextView) findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.theme_white));
        findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter));

        findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
        ((ImageView) findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo2);
        ((TextView) findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.light_gray));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.likeDayButton:
                selectLikeDay();
                subTab = DAY;
                mapRendering(LOVE);
                return;
            case R.id.likePhotoButton:
                selectLikePhoto();
                subTab = PHOTO;
                mapRendering(LOVE);
                return;
        }
        nowTabIndicator(v.getId());
        switch (v.getId()) {
            case R.id.dayButton:
                mapRendering(DAY);
                nowTab = DAY;
                break;
            case R.id.photoButton:
                mapRendering(PHOTO);
                nowTab = PHOTO;
                break;
            case R.id.likeButton:
                initLikeBar();
                nowTab = LOVE;
                subTab = DAY;
                mapRendering(LOVE);
                break;
            default:
                break;
        }
    }
    public void selectLikeDay(){
        findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
        ((ImageView) findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day);
        ((TextView) findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.theme_white));
        findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
        ((ImageView) findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo2);
        ((TextView) findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.light_gray));
        findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter));
    }
    public void selectLikePhoto(){
        findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
        ((ImageView) findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day2);
        ((TextView) findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.light_gray));
        findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter2));
        findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
        ((ImageView) findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo);
        ((TextView) findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.theme_white));

    }
    protected String getProfileTitle(String name){
        return name+getString(R.string.possessive)+" "+getString(R.string.action_bar_profile_title);
    }

    protected void mapRendering(int flag) {
        if(mUser==null) return;
        startLoading();
        if (flag == DAY) {
            new ProfileDayAsyncTask(dayHandler).execute(mAccessToken,mUser.getId());

        } else if (flag == PHOTO) {
            new ProfilePhotoAsyncTask(photoHandler).execute(mAccessToken,mUser.getId());

        } else if (flag == LOVE) {
            if (subTab == DAY) {
                new LikeAsyncTask(likeDayHandler).execute(mAccessToken,mUser.getId(),"Days");
            } else {
                new LikeAsyncTask(likePhotoHandler).execute(mAccessToken,mUser.getId(),"Photos");
            }
        }
    }
    View mLoading;
    boolean isLoading;
    protected void startLoading() {
        isLoading = true;
        mLoading = findViewById(R.id.refresh);
        mLoading.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        isLoading = false;
        mLoading = findViewById(R.id.refresh);
        mLoading.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Session.REQUEST_PHOTO_CODE && resultCode==Session.DLETE_CODE) {
            Long id = data.getLongExtra("id",0);
            finish();
            startActivity(getIntent());
        }else if(requestCode==Session.REQUEST_DAY_CODE && resultCode==Session.DLETE_CODE) {
            Long id = data.getLongExtra("id",0);
            finish();
            startActivity(getIntent());
        }
    }
}
