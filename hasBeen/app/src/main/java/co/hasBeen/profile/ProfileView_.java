package co.hasBeen.profile;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.map.MapRoute;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Loved;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.User;
import co.hasBeen.profile.follow.FollowView;
import co.hasBeen.profile.map.LikeAsyncTask;
import co.hasBeen.profile.map.ProfileDayAsyncTask;
import co.hasBeen.profile.map.ProfilePhotoAsyncTask;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-02-05.
 */
public class ProfileView_ extends ActionBarActivity {
    final static int DAY = 1;
    final static int PHOTO = 2;
    final static int LOVE = 3;
    int nowTab = DAY;
    int subTab = DAY;

    Long mUserId;
    String mAccessToken;
    User mUser;

    List<Day> mDays;
    List<Photo> mPhotos;
    List<Day> mLikeDays;
    List<Photo> mLikePhotos;

    SupportMapFragment mMapFragment;
    MapRoute mMapRoute;
    GoogleMap mMap;
    TextView titleView ;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (ProfileView_.this != null) {
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
        }
    };

    Handler dayHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mDays = (List<Day>) msg.obj;
                    dayRendering(mDays);
                    stopLoading();
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
                    mPhotos = (List<Photo>) msg.obj;
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
                    List<Loved> mLovedDays = (List<Loved>) msg.obj;
                    mLikeDays = new ArrayList<>();
                    for(Loved love : mLovedDays)
                        mLikeDays.add(love.getDay());

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
                    List<Loved> mLovedPhotos = (List<Loved>) msg.obj;
                    mLikePhotos = new ArrayList<>();
                    for(Loved love : mLovedPhotos)
                        mLikePhotos.add(love.getPhoto());

                    photoRendering(mLikePhotos);
                    break;
                case -1:
                    break;
            }
        }
    };
    ProfileAsyncTask asyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getIntent().getLongExtra("userId",0);
        mAccessToken = Session.getString(this, "accessToken", null);
        init();
        startLoading();
        asyncTask = new ProfileAsyncTask(handler);
        asyncTask.execute(mAccessToken,mUserId);
    }

    protected void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
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
        mCustomActionBar.findViewById(R.id.moreVert).setVisibility(View.GONE);
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);

    }
    protected void init(){
        setContentView(R.layout.profile);
        initActionBar();
        mLoading = findViewById(R.id.refresh);
        mMapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map));

        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMapRoute = new MapRoute(map, ProfileView_.this);
                UiSettings setting = map.getUiSettings();
                nowTabIndicator(R.id.dayButton);
                setting.setZoomControlsEnabled(false);
                setting.setRotateGesturesEnabled(false);
                setting.setMyLocationButtonEnabled(false);
            }
        });
        RelativeLayout dayButton = (RelativeLayout) findViewById(R.id.dayButton);
        RelativeLayout photoButton = (RelativeLayout) findViewById(R.id.photoButton);
        RelativeLayout likeButton = (RelativeLayout) findViewById(R.id.likeButton);
        dayButton.setOnClickListener(new ProfileBarOnClickListner());
        photoButton.setOnClickListener(new ProfileBarOnClickListner());
        likeButton.setOnClickListener(new ProfileBarOnClickListner());
    }
    class ProfileBarOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            nowTabIndicator(v.getId());
            switch (v.getId()) {
                case R.id.dayButton:
                    nowTab = DAY;
                    mapRendering(DAY);
                    break;
                case R.id.photoButton:
                    nowTab = PHOTO;
                    mapRendering(PHOTO);
                    break;
                case R.id.likeButton:
                    nowTab = LOVE;
                    subTab = DAY;
                    initLikeBar();
                    mapRendering(LOVE);
                    break;
                default:
                    break;
            }
        }
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
    protected String getProfileTitle(String name){
        return name+getString(R.string.possessive)+" "+getString(R.string.action_bar_profile_title);
    }
    protected void initProfile() {
        ImageView coverImage = (ImageView) findViewById(R.id.coverImage);
        ImageView profileImage = (ImageView) findViewById(R.id.profileImage);
        TextView profileName = (TextView) findViewById(R.id.name);
        TextView followerStatus = (TextView) findViewById(R.id.followerStatus);
        TextView followingStatus = (TextView) findViewById(R.id.followingStatus);
        final ImageView followImage = (ImageView) findViewById(R.id.setting_follow);
        TextView dayCount = (TextView) findViewById(R.id.dayCount);
        TextView photoCount = (TextView) findViewById(R.id.photoCount);
        TextView loveCount = (TextView) findViewById(R.id.loveCount);
        if(mUser.getCoverPhoto()!=null) Glide.with(this).load(mUser.getCoverPhoto().getLargeUrl()).placeholder(Util.getPlaceHolder((int)Math.random()*10)).into(coverImage);
        else Glide.with(this).load(R.drawable.coverholder).into(coverImage);
        Glide.with(this).load(mUser.getImageUrl()).placeholder(R.mipmap.profile_placeholder).transform(new CircleTransform(this)).into(profileImage);
        profileName.setText(Util.parseName(mUser, this));
        titleView.setText(getProfileTitle(Util.parseName(mUser, this)));
        followerStatus.setText(getString(R.string.follower_count, mUser.getFollowerCount()));
        followingStatus.setText(getString(R.string.following_count, mUser.getFollowingCount()));
        followerStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FollowView.class);
                intent.putExtra("userId", mUser.getId());
                intent.putExtra("type", "other");
                intent.putExtra("page",0);
                startActivity(intent);
            }
        });
        followingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FollowView.class);
                intent.putExtra("userId", mUser.getId());
                intent.putExtra("type", "my");
                intent.putExtra("page",1);
                startActivity(intent);
            }
        });

        if(mUser.getFollow()!=null)
            followImage.setImageResource(R.drawable.following);
        else {
            followImage.setImageResource(R.drawable.follow);
        }
        followImage.setOnClickListener(new ProfileFollowListner(mUser.getFollow(),mAccessToken,mUser.getId()));
        dayCount.setText(mUser.getDayCount() + "");
        photoCount.setText(mUser.getPhotoCount() + "");
        loveCount.setText(mUser.getLoveCount() + "");
    }

    protected void initLikeBar() {
        LinearLayout dayButton = (LinearLayout) findViewById(R.id.likeDayButton);
        LinearLayout photoButton = (LinearLayout) findViewById(R.id.likePhotoButton);
        dayButton.setOnClickListener(new LikeBarOnClickListner());
        photoButton.setOnClickListener(new LikeBarOnClickListner());
        findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
        ((ImageView) findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day);
        ((TextView) findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.theme_white));
        findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter));

        findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
        ((ImageView) findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo2);
        ((TextView) findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.light_gray));
    }

    class LikeBarOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.likeDayButton:
                    findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
                    ((ImageView) findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day);
                    ((TextView) findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.theme_white));

                    findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
                    ((ImageView) findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo2);
                    ((TextView) findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.light_gray));
                    findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter));
                    subTab = DAY;
                    mapRendering(LOVE);
                    break;
                case R.id.likePhotoButton:

                    findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
                    ((ImageView) findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day2);
                    ((TextView) findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.light_gray));
                    findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter2));

                    findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
                    ((ImageView) findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo);
                    ((TextView) findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.theme_white));

                    subTab = PHOTO;
                    mapRendering(LOVE);

                    break;
            }
        }
    }

    protected void mapRendering(int flag) {
        if (flag == DAY) {
            if (mDays != null)
                dayRendering(mDays);
            else
                new ProfileDayAsyncTask(dayHandler).execute(mAccessToken,mUser.getId());

        } else if (flag == PHOTO) {
            if (mPhotos != null)
                photoRendering(mPhotos);
            else
                new ProfilePhotoAsyncTask(photoHandler).execute(mAccessToken,mUser.getId());

        } else if (flag == LOVE) {
            if (subTab == DAY) {
                if (mLikeDays != null)
                    dayRendering(mLikeDays);
                else
                    new LikeAsyncTask(likeDayHandler).execute(mAccessToken,mUser.getId(),"Days");

            } else {
                if (mLikePhotos != null)
                    photoRendering(mLikePhotos);
                else
                    new LikeAsyncTask(likePhotoHandler).execute(mAccessToken,mUser.getId(),"Photos");
            }
        }
    }

    protected void dayRendering( List<Day> days) {

        try {
            LatLng location = new LatLng(days.get(0).getMainPlace().getLat(), days.get(0).getMainPlace().getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
            mMapRoute.addMarkerClusterDay(days);
        }catch (Exception e) {
            mMap.clear();
            e.printStackTrace();
        }
    }

    protected void photoRendering( List<Photo> photos) {
        try {
            LatLng location = new LatLng(photos.get(0).getLat(), photos.get(0).getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
            mMapRoute.addMarkerClusterPhoto(photos);
        }catch (Exception e) {
            mMap.clear();
            e.printStackTrace();
        }
    }
    View mLoading;
    boolean isLoading;
    protected void startLoading() {
        isLoading = true;
        mLoading.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        isLoading = false;
        mLoading.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }
    @Override
    protected void onDestroy() {
        asyncTask.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("ProFile View");
        Localytics.upload();
    }
}
