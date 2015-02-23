package co.hasBeen.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.geolocation.MapRoute;
import co.hasBeen.model.api.Loved;
import co.hasBeen.model.api.User;
import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Photo;
import co.hasBeen.profile.follow.FollowView;
import co.hasBeen.profile.map.LikeDayAsyncTask;
import co.hasBeen.profile.map.LikePhotoAsyncTask;
import co.hasBeen.profile.map.ProfileDayAsyncTask;
import co.hasBeen.profile.map.ProfilePhotoAsyncTask;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;
import co.hasBeen.R;

/**
 * Created by zuby on 2015-01-30.
 */
public class ProfileFragment extends Fragment {
    final static int DAY = 1;
    final static int PHOTO = 2;
    final static int LOVE = 3;
    View mView;
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
        initAll();
        return mView;
    }
    public void initAll(){
        new ProfileAsyncTask(handler).execute(mAccessToken);
        init();
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
                setting.setZoomControlsEnabled(true);
                setting.setRotateGesturesEnabled(false);
                setting.setMyLocationButtonEnabled(false);
                View zoomControls = mMapFragment.getView().findViewById(0x1);
                if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                    // ZoomControl is inside of RelativeLayout
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

                    // Align it to - parent top|left
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    int margin = Util.convertDpToPixel(16, getActivity());
                    params.setMargins(0,margin,margin,0);
                    // Update margins, set to 10dp
                }
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
            clearSelect();
            switch (v.getId()) {
                case R.id.dayButton:
                    nowTab = DAY;
                    mView.findViewById(R.id.daySelectBar).setVisibility(View.VISIBLE);
                    ((TextView) mView.findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_black));
                    mapRendering(DAY);
                    break;
                case R.id.photoButton:
                    nowTab = PHOTO;
                    mView.findViewById(R.id.photoSelectBar).setVisibility(View.VISIBLE);
                    ((TextView) mView.findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_black));
                    mapRendering(PHOTO);
                    break;
                case R.id.likeButton:
                    nowTab = LOVE;
                    mView.findViewById(R.id.loveSelectBar).setVisibility(View.VISIBLE);
                    ((TextView) mView.findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_black));
                    mView.findViewById(R.id.likeBar).setVisibility(View.VISIBLE);
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
        mView.findViewById(R.id.likeBar).setVisibility(View.GONE);
        mView.findViewById(R.id.loveSelectBar).setVisibility(View.GONE);
        ((TextView) mView.findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_gray));
        mView.findViewById(R.id.photoSelectBar).setVisibility(View.GONE);
        ((TextView) mView.findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_gray));
        mView.findViewById(R.id.daySelectBar).setVisibility(View.GONE);
        ((TextView) mView.findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_gray));
        mMap.clear();
    }

    protected void initProfile() {
        ImageView coverImage = (ImageView) mView.findViewById(R.id.coverImage);
        ImageView profileImage = (ImageView) mView.findViewById(R.id.profileImage);
        TextView profileName = (TextView) mView.findViewById(R.id.profileName);
        TextView followStatus = (TextView) mView.findViewById(R.id.followStatus);
        ImageView setting = (ImageView) mView.findViewById(R.id.setting_follow);
        TextView dayCount = (TextView) mView.findViewById(R.id.dayCount);
        TextView photoCount = (TextView) mView.findViewById(R.id.photoCount);
        TextView loveCount = (TextView) mView.findViewById(R.id.loveCount);
        if(mUser.getCoverPhoto()!=null) Glide.with(getActivity()).load(mUser.getCoverPhoto().getLargeUrl()).placeholder(R.drawable.coverholder).into(coverImage);
        else Glide.with(getActivity()).load(R.drawable.coverholder).into(coverImage);
        Glide.with(getActivity()).load(mUser.getImageUrl()).transform(new CircleTransform(getActivity())).into(profileImage);
        profileName.setText(Util.parseName(mUser, 0));
        followStatus.setText(mUser.getFollowerCount() + " Follower · " + mUser.getFollowingCount() + " Following");
        followStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowView.class);
                intent.putExtra("userId",mUser.getId());
                intent.putExtra("type","my");
                startActivity(intent);
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
            e.printStackTrace();
        }
    }

    protected void photoRendering(final List<Photo> photos) {
        try {
            LatLng location = new LatLng(photos.get(0).getLat(), photos.get(0).getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
            mMapRoute.addMarkerClusterPhoto(photos);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
