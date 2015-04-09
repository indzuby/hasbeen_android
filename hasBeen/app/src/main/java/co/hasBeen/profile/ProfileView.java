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
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Loved;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.User;
import co.hasBeen.profile.follow.FollowView;
import co.hasBeen.profile.map.LikeAsyncTask;
import co.hasBeen.profile.map.ProfileDayAsyncTask;
import co.hasBeen.profile.map.ProfilePhotoAsyncTask;
import co.hasBeen.search.DayAdapter;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

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

    List<Day> mDays;
    List<Photo> mPhotos;
    List<Day> mLikeDays;
    List<Photo> mLikePhotos;
    ListView listView;
    TextView titleView ;
    View mHeaderView;
    DayAdapter dayAdapter;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (ProfileView.this != null) {
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
    boolean hasLikeBar = true;
    int beforeVisibleItem;
    View scrollTop;
    protected void init(){
        setContentView(R.layout.profile_list);
        initActionBar();
        mLoading = findViewById(R.id.refresh);
        listView = (ListView)findViewById(R.id.listView);
        mHeaderView =  LayoutInflater.from(this).inflate(R.layout.profile_header, null, false);
        listView.addHeaderView(mHeaderView);
        nowTabIndicator(R.id.dayButton);
        hasLikeBar = true;
        beforeVisibleItem = 0 ;
        scrollTop = findViewById(R.id.scrollTop);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mHeaderView.findViewById(R.id.likeButton).isSelected()){
                    if(hasLikeBar) {
                        if(beforeVisibleItem<firstVisibleItem) {
                            Animation ani = AnimationUtils.loadAnimation(getBaseContext(),R.anim.slide_down);
                            findViewById(R.id.likeBar).startAnimation(ani);
                            findViewById(R.id.likeBar).setVisibility(View.GONE);
                            hasLikeBar = false;
                        }
                    }else {
                        if(beforeVisibleItem>firstVisibleItem) {
                            Animation ani = AnimationUtils.loadAnimation(getBaseContext(),R.anim.slide_up);
                            findViewById(R.id.likeBar).startAnimation(ani);
                            findViewById(R.id.likeBar).setVisibility(View.VISIBLE);
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
        scrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setSmoothScrollbarEnabled(true);
                listView.setSelection(0);
            }
        });
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
        mHeaderView.findViewById(id).setSelected(true);
        if(id == R.id.dayButton)
            ((TextView) mHeaderView.findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_black));
        else if(id == R.id.photoButton)
            ((TextView) mHeaderView.findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_black));
        else {
            hasLikeBar = true;
            findViewById(R.id.likeBar).setVisibility(View.VISIBLE);
            ((TextView) mHeaderView.findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_black));
        }
    }
    protected void clearSelect() {
        hasLikeBar=false;
        mHeaderView.findViewById(R.id.dayButton).setSelected(false);
        mHeaderView.findViewById(R.id.photoButton).setSelected(false);
        mHeaderView.findViewById(R.id.likeButton).setSelected(false);
        findViewById(R.id.likeBar).clearAnimation();
        findViewById(R.id.likeBar).setVisibility(View.GONE);
        ((TextView) mHeaderView.findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView) mHeaderView.findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView) mHeaderView.findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_gray));
    }
    protected String getProfileTitle(String name){
        return name+getString(R.string.possessive)+" "+getString(R.string.action_bar_profile_title);
    }
    protected void initProfile() {
        ImageView coverImage = (ImageView) mHeaderView.findViewById(R.id.coverImage);
        ImageView profileImage = (ImageView) mHeaderView.findViewById(R.id.profileImage);
        TextView profileName = (TextView) mHeaderView.findViewById(R.id.name);
        TextView followerStatus = (TextView) mHeaderView.findViewById(R.id.followerStatus);
        TextView followingStatus = (TextView) mHeaderView.findViewById(R.id.followingStatus);
        final ImageView followImage = (ImageView) mHeaderView.findViewById(R.id.setting_follow);
        TextView dayCount = (TextView) mHeaderView.findViewById(R.id.dayCount);
        TextView photoCount = (TextView) mHeaderView.findViewById(R.id.photoCount);
        TextView loveCount = (TextView) mHeaderView.findViewById(R.id.loveCount);
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
        View dayButton = mHeaderView.findViewById(R.id.dayButton);
        View photoButton = mHeaderView.findViewById(R.id.photoButton);
        View likeButton = mHeaderView.findViewById(R.id.likeButton);

        dayButton.setOnClickListener(new ProfileBarOnClickListner());
        photoButton.setOnClickListener(new ProfileBarOnClickListner());
        likeButton.setOnClickListener(new ProfileBarOnClickListner());
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
        if(mUser==null) return;
        startLoading();
        if (flag == DAY) {
            if (mDays != null)
                dayRendering(mDays);
            else
                new ProfileDayAsyncTask(dayHandler).execute(mAccessToken, mUser.getId());

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
    public void dayRendering(List<Day> days) {
        DayAdapter dayAdapter = new DayAdapter(days,this);
        listView.setAdapter(dayAdapter);
        stopLoading();
    }
    public void photoRendering(List<Photo> photos) {
        PhotoAdapter photoAdapter = new PhotoAdapter(photos,this);
        listView.setAdapter(photoAdapter);
        stopLoading();
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
