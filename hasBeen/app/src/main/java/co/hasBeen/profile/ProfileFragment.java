package co.hasBeen.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.main.MainActivity;
import co.hasBeen.R;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Loved;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.User;
import co.hasBeen.profile.follow.FollowView;
import co.hasBeen.profile.map.LikeAsyncTask;
import co.hasBeen.profile.map.ProfileDayAsyncTask;
import co.hasBeen.profile.map.ProfileMap;
import co.hasBeen.profile.map.ProfilePhotoAsyncTask;
import co.hasBeen.search.DayAdapter;
import co.hasBeen.setting.SettingView;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenFragment;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-30.
 */
public class ProfileFragment extends HasBeenFragment implements View.OnClickListener{
    final static int DAY = 1;
    final static int PHOTO = 2;
    final static int LOVE = 3;
    User mUser;
    int nowTab = DAY;
    int subTab = DAY;

    List<Day> mDays;
    List<Photo> mPhotos;
    List<Day> mLikeDays;
    List<Photo> mLikePhotos;

    String mAccessToken;
    View mHeaderView;
    DayAdapter dayAdapter;
    PhotoAdapter photoAdapter;
    PullToRefreshListView refreshListView;
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.profile_list, container, false);
        mAccessToken = Session.getString(getActivity(), "accessToken", null);
        init();
        return mView;
    }
    public void initAll(){
        if(refreshListView.isRefreshing())
            refreshListView.onRefreshComplete();
        startLoading();
        initList();
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
                    List<Day> list = (List<Day>) msg.obj;
                    mDays.addAll(list);
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
                    List<Photo> list = (List<Photo>) msg.obj;
                    mPhotos.addAll(list);
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
                    for(Loved love : mLovedPhotos)
                        mLikePhotos.add(love.getPhoto());
                    photoRendering(mLikePhotos);
                    break;
                case -1:
                    break;
            }
        }
    };
    boolean hasLikeBar = true;
    int beforeVisibleItem;
    View scrollTop;
    protected void init() {
        refreshListView = (PullToRefreshListView) mView.findViewById(R.id.listView);
        listView = refreshListView.getRefreshableView();
        mHeaderView =  LayoutInflater.from(getActivity()).inflate(R.layout.profile_header, null, false);
        listView.addHeaderView(mHeaderView);
        nowTabIndicator(R.id.dayButton);
        hasLikeBar = true;
        beforeVisibleItem = 0 ;
        scrollTop = mView.findViewById(R.id.scrollTop);
        scrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setSmoothScrollbarEnabled(true);
                listView.setSelection(0);
            }
        });
        refreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mHeaderView.findViewById(R.id.likeButton).isSelected()){
                    if(hasLikeBar) {
                        if(beforeVisibleItem<firstVisibleItem) {
                            Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
                            mView.findViewById(R.id.likeBar).startAnimation(ani);
                            mView.findViewById(R.id.likeBar).setVisibility(View.GONE);
                            hasLikeBar = false;
                        }
                    }else {
                        if(beforeVisibleItem>firstVisibleItem) {
                            Animation ani = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_up);
                            mView.findViewById(R.id.likeBar).startAnimation(ani);
                            mView.findViewById(R.id.likeBar).setVisibility(View.VISIBLE);
                            hasLikeBar = true;
                        }
                    }
                    beforeVisibleItem = firstVisibleItem;
                }
                if(firstVisibleItem==0)
                    scrollTop.setVisibility(View.GONE);
                else
                    scrollTop.setVisibility(View.VISIBLE);
            }
        });
        refreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if(!isLoading) {
                    startLoading();
                    mapScrollRendering(nowTab);
                }
            }
        });
    }

    protected void initLikeBar() {
        LinearLayout dayButton = (LinearLayout) mView.findViewById(R.id.likeDayButton);
        LinearLayout photoButton = (LinearLayout) mView.findViewById(R.id.likePhotoButton);
        dayButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);
        mView.findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
        ((ImageView) mView.findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day);
        ((TextView) mView.findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.theme_white));
        mView.findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter));

        mView.findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
        ((ImageView) mView.findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo2);
        ((TextView) mView.findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.light_gray));
    }

    @Override
    public void onClick(View v) {
        initList();
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
            case R.id.mapButton:
                Intent intent = new Intent(getActivity(), ProfileMap.class);
                intent.putExtra("userId",mUser.getId());
                startActivity(intent);
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
                subTab = DAY;
                mapRendering(LOVE);
                nowTab = LOVE;
                break;
            default:
                break;
        }
    }
    public void initList(){
        mDays = new ArrayList<>();
        mPhotos = new ArrayList<>();
        mLikeDays = new ArrayList<>();
        mLikePhotos = new ArrayList<>();
    }
    public void selectLikeDay(){
        mView.findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
        ((ImageView) mView.findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day);
        ((TextView) mView.findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.theme_white));
        mView.findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
        ((ImageView) mView.findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo2);
        ((TextView) mView.findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.light_gray));
        mView.findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter));
    }
    public void selectLikePhoto(){
        mView.findViewById(R.id.likeDayButton).setBackgroundColor(getResources().getColor(R.color.theme_white));
        ((ImageView) mView.findViewById(R.id.likeDayIcon)).setImageResource(R.drawable.like_day2);
        ((TextView) mView.findViewById(R.id.likeDayText)).setTextColor(getResources().getColor(R.color.light_gray));
        mView.findViewById(R.id.likeBar).setBackground(getResources().getDrawable(R.drawable.filter2));
        mView.findViewById(R.id.likePhotoButton).setBackgroundColor(getResources().getColor(R.color.theme_color));
        ((ImageView) mView.findViewById(R.id.likePhotoIcon)).setImageResource(R.drawable.like_photo);
        ((TextView) mView.findViewById(R.id.likePhotoText)).setTextColor(getResources().getColor(R.color.theme_white));

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
        hasLikeBar = false;
        mView.findViewById(R.id.dayButton).setSelected(false);
        mView.findViewById(R.id.photoButton).setSelected(false);
        mView.findViewById(R.id.likeButton).setSelected(false);
        mView.findViewById(R.id.likeBar).clearAnimation();
        mView.findViewById(R.id.likeBar).setVisibility(View.GONE);
        ((TextView) mView.findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView) mView.findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView) mView.findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_gray));
    }

    protected void initProfile() {
        ImageView coverImage = (ImageView) mHeaderView.findViewById(R.id.coverImage);
        ImageView profileImage = (ImageView) mHeaderView.findViewById(R.id.profileImage);
        TextView profileName = (TextView) mHeaderView.findViewById(R.id.name);
        TextView followerStatus = (TextView) mHeaderView.findViewById(R.id.followerStatus);
        TextView followingStatus = (TextView) mHeaderView.findViewById(R.id.followingStatus);
        ImageView setting = (ImageView) mView.findViewById(R.id.setting_follow);
        TextView dayCount = (TextView) mHeaderView.findViewById(R.id.dayCount);
        TextView photoCount = (TextView) mHeaderView.findViewById(R.id.photoCount);
        TextView loveCount = (TextView) mHeaderView.findViewById(R.id.loveCount);
        if(mUser.getCoverPhoto()!=null) Glide.with(getActivity()).load(mUser.getCoverPhoto().getLargeUrl()).placeholder(Util.getPlaceHolder((int)Math.random()*10)).into(coverImage);
        else Glide.with(this).load(R.drawable.coverholder).into(coverImage);
        Glide.with(this).load(mUser.getImageUrl()).placeholder(R.mipmap.profile_placeholder).transform(new CircleTransform(getActivity())).into(profileImage);
        profileName.setText(Util.parseName(mUser, getActivity()));
        followerStatus.setText(getString(R.string.follower_count, mUser.getFollowerCount()));
        followingStatus.setText(getString(R.string.following_count, mUser.getFollowingCount()));
        followerStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowView.class);
                intent.putExtra("userId", mUser.getId());
                intent.putExtra("type", "other");
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
        View dayButton = mHeaderView.findViewById(R.id.dayButton);
        View photoButton = mHeaderView.findViewById(R.id.photoButton);
        View likeButton = mHeaderView.findViewById(R.id.likeButton);
        View mapButton = mHeaderView.findViewById(R.id.mapButton);
        dayButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);
        likeButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);
    }

    protected void mapRendering(int flag) {
        if(mUser==null) return;
        startLoading();
        long lastId = 0L ;
        if (flag == DAY) {
            dayAdapter = new DayAdapter(mDays,getActivity());
            refreshListView.setAdapter(dayAdapter);
            new ProfileDayAsyncTask(dayHandler).execute(mAccessToken, mUser.getId(),lastId);

        } else if (flag == PHOTO) {
            photoAdapter = new PhotoAdapter(mPhotos,getActivity());
            refreshListView.setAdapter(photoAdapter);
            new ProfilePhotoAsyncTask(photoHandler).execute(mAccessToken,mUser.getId(),lastId);

        } else if (flag == LOVE) {
            if (subTab == DAY) {
                dayAdapter = new DayAdapter(mLikeDays,getActivity());
                refreshListView.setAdapter(dayAdapter);
                new LikeAsyncTask(likeDayHandler).execute(mAccessToken,mUser.getId(),"Days",lastId);

            } else {
                photoAdapter = new PhotoAdapter(mLikePhotos,getActivity());
                refreshListView.setAdapter(photoAdapter);
                new LikeAsyncTask(likePhotoHandler).execute(mAccessToken,mUser.getId(),"Photos",lastId);
            }
        }
    }
    protected void mapScrollRendering(int flag) {
        if(mUser==null) return;
        startLoading();
        long lastId = 0L ;
        if (flag == DAY) {
            if (mDays.size()>0)
                lastId = mDays.get(mDays.size()-1).getId();
            new ProfileDayAsyncTask(dayHandler).execute(mAccessToken, mUser.getId(),lastId);

        } else if (flag == PHOTO) {
            if (mPhotos.size()>0)
                lastId = mPhotos.get(mPhotos.size()-1).getId();
            new ProfilePhotoAsyncTask(photoHandler).execute(mAccessToken,mUser.getId(),lastId);

        } else if (flag == LOVE) {
            if (subTab == DAY) {
                if (mLikeDays.size()>0)
                    lastId = mLikeDays.get(mLikeDays.size()-1).getLoveId();
                new LikeAsyncTask(likeDayHandler).execute(mAccessToken,mUser.getId(),"Days",lastId);
            } else {
                if (mLikePhotos.size()>0)
                    lastId = mLikePhotos.get(mLikePhotos.size()-1).getLoveId();
                new LikeAsyncTask(likePhotoHandler).execute(mAccessToken,mUser.getId(),"Photos",lastId);
            }
        }
    }
    public void dayRendering(List<Day> days) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dayAdapter.notifyDataSetChanged();
            }
        });
        stopLoading();
    }
    public void photoRendering(List<Photo> photos) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                photoAdapter.notifyDataSetChanged();
            }
        });
        stopLoading();
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
