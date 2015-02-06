package example.test.hasBeen.profile;

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

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.model.api.DayApi;
import example.test.hasBeen.model.api.Loved;
import example.test.hasBeen.model.api.PhotoApi;
import example.test.hasBeen.model.api.User;
import example.test.hasBeen.profile.follow.DoFollowAsyncTask;
import example.test.hasBeen.profile.follow.FollowView;
import example.test.hasBeen.profile.map.LikeDayAsyncTask;
import example.test.hasBeen.profile.map.LikePhotoAsyncTask;
import example.test.hasBeen.profile.map.ProfileDayAsyncTask;
import example.test.hasBeen.profile.map.ProfilePhotoAsyncTask;
import example.test.hasBeen.utils.CircleTransform;
import example.test.hasBeen.utils.Session;
import example.test.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-02-05.
 */
public class ProfileView extends ActionBarActivity {
    final static int DAY = 1;
    final static int PHOTO = 2;
    final static int LOVE = 3;
    int nowTab = DAY;
    int subTab = DAY;

    Long mUserId;
    String mAccessToken;
    User mUser;

    List<DayApi> mDays;
    List<PhotoApi> mPhotos;
    List<DayApi> mLikeDays;
    List<PhotoApi> mLikePhotos;

    List<Loved> mLovedPhotos;
    List<Loved> mLovedDays;
    SupportMapFragment mMapFragment;
    MapRoute mMapRoute;
    GoogleMap mMap;

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

    Handler dayHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mDays = (List<DayApi>) msg.obj;
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
                    mPhotos = (List<PhotoApi>) msg.obj;
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
                    mLovedDays = (List<Loved>) msg.obj;
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
                    mLovedPhotos = (List<Loved>) msg.obj;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getIntent().getLongExtra("userId",0);
        mAccessToken = Session.getString(this,"accessToken",null);
        new ProfileAsyncTask(handler).execute(mAccessToken,mUserId);
        init();
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
        titleView.setText("Profile");
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
        mMapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map));


        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMapRoute = new MapRoute(map, getBaseContext());
                UiSettings setting = map.getUiSettings();
                setting.setZoomControlsEnabled(false);
                setting.setMyLocationButtonEnabled(true);
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
            clearSelect();
            switch (v.getId()) {
                case R.id.dayButton:
                    nowTab = DAY;
                    findViewById(R.id.daySelectBar).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_black));
                    mapRendering(DAY);
                    break;
                case R.id.photoButton:
                    nowTab = PHOTO;
                    findViewById(R.id.photoSelectBar).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_black));
                    mapRendering(PHOTO);
                    break;
                case R.id.likeButton:
                    nowTab = LOVE;
                    findViewById(R.id.loveSelectBar).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_black));
                    findViewById(R.id.likeBar).setVisibility(View.VISIBLE);
                    subTab = DAY;
                    initLikeBar();
                    mapRendering(LOVE);
                    break;
                default:
                    break;
            }
        }
    }

    protected void clearSelect() {
        findViewById(R.id.likeBar).setVisibility(View.GONE);
        findViewById(R.id.loveSelectBar).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_gray));
        findViewById(R.id.photoSelectBar).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_gray));
        findViewById(R.id.daySelectBar).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_gray));
    }
    protected void initProfile() {
        ImageView coverImage = (ImageView) findViewById(R.id.coverImage);
        ImageView profileImage = (ImageView) findViewById(R.id.profileImage);
        TextView profileName = (TextView) findViewById(R.id.profileName);
        TextView followStatus = (TextView) findViewById(R.id.followStatus);
        final ImageView followImage = (ImageView) findViewById(R.id.setting_follow);
        TextView dayCount = (TextView) findViewById(R.id.dayCount);
        TextView photoCount = (TextView) findViewById(R.id.photoCount);
        TextView loveCount = (TextView) findViewById(R.id.loveCount);
        if(mUser.getCoverPhoto()!=null) Glide.with(this).load(mUser.getCoverPhoto().getLargeUrl()).placeholder(R.drawable.placeholder).into(coverImage);
        else Glide.with(this).load(R.drawable.coverholder).into(coverImage);
        Glide.with(this).load(mUser.getImageUrl()+"?type=large").transform(new CircleTransform(this)).into(profileImage);
        profileName.setText(Util.parseName(mUser, 0));
        followStatus.setText(mUser.getFollowerCount() + " Follower · " + mUser.getFollowingCount() + " Following");
        followStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FollowView.class);
                intent.putExtra("userId",mUser.getId());
                startActivity(intent);
            }
        });

        if(mUser.getFollow()!=null)
            followImage.setImageResource(R.drawable.following);
        else {
            followImage.setImageResource(R.drawable.follow);
            followImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DoFollowAsyncTask(new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if(msg.what==0){
                                followImage.setImageResource(R.drawable.following);;
                                followImage.setOnClickListener(null);
                            }else {

                            }
                        }
                    }).execute(mAccessToken,mUser.getId());

                }
            });
        }
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
                    new LikeDayAsyncTask(likeDayHandler).execute(mAccessToken,mUser.getId());


            } else {
                if (mLikePhotos != null)
                    photoRendering(mLikePhotos);
                else
                    new LikePhotoAsyncTask(likePhotoHandler).execute(mAccessToken,mUser.getId());
            }
        }
    }

    protected void dayRendering(final List<DayApi> days) {

        try {
            LatLng location = new LatLng(days.get(0).getMainPlace().getLat(), days.get(0).getMainPlace().getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
            mMapRoute.addMarkerCluster(days);
        }catch (Exception e) {
            mMap.clear();
            e.printStackTrace();
        }
    }

    protected void photoRendering(final List<PhotoApi> photos) {
        try {
            LatLng location = new LatLng(photos.get(0).getLat(), photos.get(0).getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
            mMapRoute.addMarkerClusterPhoto(photos);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
