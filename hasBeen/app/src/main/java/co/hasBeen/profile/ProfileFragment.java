package co.hasBeen.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import co.hasBeen.MainActivity;
import co.hasBeen.R;
import co.hasBeen.map.MapRoute;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Loved;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.User;
import co.hasBeen.profile.follow.FollowView;
import co.hasBeen.profile.map.LikeDayAsyncTask;
import co.hasBeen.profile.map.LikePhotoAsyncTask;
import co.hasBeen.profile.map.ProfileDayAsyncTask;
import co.hasBeen.profile.map.ProfilePhotoAsyncTask;
import co.hasBeen.setting.ProfileImageListner;
import co.hasBeen.setting.SettingView;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenFragment;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-30.
 */
public class ProfileFragment extends HasBeenFragment {
    final static int DAY = 1;
    final static int PHOTO = 2;
    final static int LOVE = 3;
    User mUser;
    int nowTab = DAY;
    int subTab = DAY;
    GoogleMap mMap;
    SupportMapFragment mMapFragment;
    MapRoute mMapRoute;

    List<Day> mDays;
    List<Photo> mPhotos;
    List<Day> mLikeDays;
    List<Photo> mLikePhotos;

    String mAccessToken;

    List<Loved> mLovedPhotos;
    List<Loved> mLovedDays;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.profile, container, false);
        mAccessToken = Session.getString(getActivity(), "accessToken", null);
        init();
        return mView;
    }
    public void initAll(){
        startLoading();
        mDays = null;
        mPhotos = null;
        mLikeDays = null;
        mLikePhotos = null;
        new ProfileAsyncTask(handler).execute(mAccessToken);
        nowTabIndicator(R.id.dayButton);
    }

    @Override
    public void showTab() {
        super.showTab();
        if(!isShowTab())
            initAll();
        setShowTab();
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mUser = (User) msg.obj;
                    Session.putLong(getActivity(),"myUserid",mUser.getId());
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

    protected void init() {
        RelativeLayout dayButton = (RelativeLayout) mView.findViewById(R.id.dayButton);
        RelativeLayout photoButton = (RelativeLayout) mView.findViewById(R.id.photoButton);
        RelativeLayout likeButton = (RelativeLayout) mView.findViewById(R.id.likeButton);
        dayButton.setOnClickListener(new ProfileBarOnClickListner());
        photoButton.setOnClickListener(new ProfileBarOnClickListner());
        likeButton.setOnClickListener(new ProfileBarOnClickListner());

        mMapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));


        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMapRoute = new MapRoute(map, getActivity());
                UiSettings setting = map.getUiSettings();
                setting.setZoomControlsEnabled(false);
                setting.setRotateGesturesEnabled(false);
                setting.setMyLocationButtonEnabled(false);
            }
        });
    }

    protected void initLikeBar() {
        LinearLayout dayButton = (LinearLayout) mView.findViewById(R.id.likeDayButton);
        LinearLayout photoButton = (LinearLayout) mView.findViewById(R.id.likePhotoButton);
        dayButton.setOnClickListener(new LikeBarOnClickListner());
        photoButton.setOnClickListener(new LikeBarOnClickListner());
        mView.findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
        ((ImageView) mView.findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day);
        ((TextView) mView.findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.theme_white));
        mView.findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter));

        mView.findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
        ((ImageView) mView.findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo2);
        ((TextView) mView.findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.light_gray));
    }

    class LikeBarOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.likeDayButton:
                    mView.findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
                    ((ImageView) mView.findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day);
                    ((TextView) mView.findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.theme_white));

                    mView.findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
                    ((ImageView) mView.findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo2);
                    ((TextView) mView.findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.light_gray));
                    mView.findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter));
                    subTab = DAY;
                    mapRendering(LOVE);
                    break;
                case R.id.likePhotoButton:

                    mView.findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
                    ((ImageView) mView.findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day2);
                    ((TextView) mView.findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.light_gray));
                    mView.findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter2));

                    mView.findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
                    ((ImageView) mView.findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo);
                    ((TextView) mView.findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.theme_white));

                    subTab = PHOTO;
                    mapRendering(LOVE);

                    break;
            }
        }
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
        mView.findViewById(id).setSelected(true);
        if(id == R.id.dayButton)
            ((TextView) mView.findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_black));
        else if(id == R.id.photoButton)
            ((TextView) mView.findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_black));
        else {
            mView.findViewById(R.id.likeBar).setVisibility(View.VISIBLE);
            ((TextView) mView.findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_black));
        }
    }
    protected void clearSelect() {
        mView.findViewById(R.id.dayButton).setSelected(false);
        mView.findViewById(R.id.photoButton).setSelected(false);
        mView.findViewById(R.id.likeButton).setSelected(false);
        mView.findViewById(R.id.likeBar).setVisibility(View.GONE);
        ((TextView) mView.findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView) mView.findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView) mView.findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_gray));
        if(mMap!=null) mMap.clear();
    }

    protected void initProfile() {
        ImageView coverImage = (ImageView) mView.findViewById(R.id.coverImage);
        ImageView profileImage = (ImageView) mView.findViewById(R.id.profileImage);
        TextView profileName = (TextView) mView.findViewById(R.id.name);
        TextView followerStatus = (TextView) mView.findViewById(R.id.followerStatus);
        TextView followingStatus = (TextView) mView.findViewById(R.id.followingStatus);
        ImageView setting = (ImageView) mView.findViewById(R.id.setting_follow);
        TextView dayCount = (TextView) mView.findViewById(R.id.dayCount);
        TextView photoCount = (TextView) mView.findViewById(R.id.photoCount);
        TextView loveCount = (TextView) mView.findViewById(R.id.loveCount);
        if(mUser.getCoverPhoto()!=null) Glide.with(getActivity()).load(mUser.getCoverPhoto().getLargeUrl()).placeholder(R.drawable.coverholder).into(coverImage);
        else Glide.with(getActivity()).load(R.drawable.coverholder).into(coverImage);
        Glide.with(getActivity()).load(mUser.getImageUrl()).transform(new CircleTransform(getActivity())).into(profileImage);
        profileName.setText(Util.parseName(mUser, getActivity()));
        followerStatus.setText(getString(R.string.follower_count, mUser.getFollowerCount()));
        followingStatus.setText(getString(R.string.following_count, mUser.getFollowingCount()));
        profileImage.setOnClickListener(new ProfileImageListner(getActivity(),profileImage));
        followerStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowView.class);
                intent.putExtra("userId", mUser.getId());
                intent.putExtra("type", "my");
                intent.putExtra("page",0);
                startActivity(intent);
            }
        });
        followingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowView.class);
                intent.putExtra("userId", mUser.getId());
                intent.putExtra("type", "my");
                intent.putExtra("page",1);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingView.class);
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_LOGOUT);
            }
        });
        dayCount.setText(mUser.getDayCount() + "");
        photoCount.setText(mUser.getPhotoCount() + "");
        loveCount.setText(mUser.getLoveCount() + "");
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

    protected void dayRendering(final List<Day> days) {
        try {
            LatLng location = new LatLng(days.get(0).getMainPlace().getLat(), days.get(0).getMainPlace().getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
            mMapRoute.addMarkerCluster(days);
        }catch (Exception e) {
            mMap.clear();
            e.printStackTrace();
        }
    }

    protected void photoRendering(final List<Photo> photos) {
        try {
            LatLng location = new LatLng(photos.get(0).getLat(), photos.get(0).getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
            mMapRoute.addMarkerClusterPhoto(photos);
        }catch (Exception e) {
            mMap.clear();
            e.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Profile Framgent");
        Localytics.upload();
    }
}
