package example.test.hasBeen.day;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.comment.CommentView;
import example.test.hasBeen.comment.EnterCommentListner;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.loved.LoveListner;
import example.test.hasBeen.model.api.Comment;
import example.test.hasBeen.model.api.User;
import example.test.hasBeen.model.database.Day;
import example.test.hasBeen.model.database.Position;
import example.test.hasBeen.profile.ProfileClickListner;
import example.test.hasBeen.utils.CircleTransform;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.Session;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-27.
 */
public class DayView extends ActionBarActivity{
    final static String TAG = "Day View";
    TextView titleView;
    ListView mListView;
    View mHeaderView;
    View mFooterView;
    int nowScrolled = 0 ;
    GoogleMap mMap = null;
    Day mDay;
    MapRoute mMapRoute = null;
    DayAdapter mDayAdapter;
    Long mDayId;
    String mAccessToken;
    TextView mSocialAction;
    Typeface medium,regular;
    LinearLayout mRecommendationLayout;
    List<Day> mRecommendationList;
    LinearLayout mSocialBar;
    boolean hasSocialBar = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showProgress();
        super.onCreate(savedInstanceState);
        mDayId = getIntent().getLongExtra("dayId",0);
        mAccessToken = Session.getString(this,"accessToken",null);
        init();
    }
    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Day day = (Day)msg.obj;
                    List<Position> positions = new ArrayList<>();
                    for(Position position : day.getPositionList()) {
                        if(position.getType().equals("PLACE"))
                            positions.add(position);
                    }
                    mDay = day;
                    mDay.setPositionList(positions);
                    Log.i(TAG,mDay.getId()+"");
                    titleView.setText(mDay.getTitle());
                    initHeaderView();
                    initBodyView();
                    initFoorteView();
                    dialog.dismiss();
                    break;
                case -1:
                    Log.i(TAG,"NULL");
                    break;
            }
        }
    };
    protected void initHeaderView(){
        View titleBox = findViewById(R.id.dayTitleBox);
        ImageView profileImage = (ImageView) titleBox.findViewById(R.id.profileImage);
        TextView profileName = (TextView) titleBox.findViewById(R.id.profileName);
        TextView placeName = (TextView) titleBox.findViewById(R.id.placeName);
        TextView date = (TextView) titleBox.findViewById(R.id.date);

        TextView dayTitle = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        mSocialAction = (TextView) findViewById(R.id.socialAction);
        TextView totalPhoto = (TextView) findViewById(R.id.totalPhoto);
        Glide.with(this).load(mDay.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(this)).into(profileImage);
        Log.i(TAG, mDay.getMainPlace().getName());
        profileName.setText(Util.parseName(mDay.getUser(), 0));
        placeName.setText(Util.convertPlaceName(mDay.getPositionList()));
        date.setText(HasBeenDate.convertDate(mDay.getDate()));
        dayTitle.setText(mDay.getTitle());
        dayTitle.setTypeface(medium);
        description.setText(mDay.getDescription());
        description.setTypeface(regular);
        mSocialAction.setText(mDay.getLoveCount()+" Likes · " + mDay.getCommentCount()+" Commnents · "+mDay.getShareCount()+" Shared");
        totalPhoto.setText("Total " + mDay.getPhotoCount() + " photos");
        Log.i(TAG, mDay.getPositionList().size() + "");
        profileImage.setOnClickListener(new ProfileClickListner(this, mDay.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(this, mDay.getUser().getId()));
        new Thread(new Runnable() {
            boolean flag=true;
            @Override
            public void run() {
                while(flag) {
                    if(mMapRoute!=null && mMap!=null){
                        flag = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMapRoute.createRouteDay(mDay.getPositionList());
                            }
                        });

                    }
                }
            }
        }).start();
    }
    protected void initBodyView (){
        mDayAdapter = new DayAdapter(this,mDay.getPositionList());
        mListView.setAdapter(mDayAdapter);
    }
    protected  void initFoorteView(){
        mSocialBar = (LinearLayout) findViewById(R.id.socialBar);
        LinearLayout commentButton = (LinearLayout)findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new EnterCommentListner(getBaseContext(),"days",mDay.getId(),mDay.getCommentCount()));
        LinearLayout loveButton = (LinearLayout) findViewById(R.id.loveButton);
        ImageView love = (ImageView) loveButton.findViewById(R.id.love);
        TextView loveText = (TextView) loveButton.findViewById(R.id.loveText);
        if(mDay.getLove()!=null) {
            love.setImageResource(R.drawable.photo_like_pressed);
            loveText.setTextColor(this.getResources().getColor(R.color.light_black));
            loveText.setTypeface(medium);
        }
        else {
            love.setImageResource(R.drawable.photo_like);
            loveText.setTextColor(this.getResources().getColor(R.color.light_gray));
            loveText.setTypeface(regular);
        }
        loveButton.setOnClickListener(new LoveListner(this,mDay,"days",mSocialAction));

        LinearLayout commentBox = (LinearLayout) findViewById(R.id.commetBox);
        TextView moreComments = (TextView) findViewById(R.id.moreComments);
        List<Comment> comments = mDay.getCommentList();
        for(int i = 0 ;i<3 && i<comments.size();i++) {
            Comment comment = comments.get(i);
            commentBox.addView(CommentView.makeComment(this, comment));
        }
        if(mDay.getCommentCount()>3)
            moreComments.setText(mDay.getCommentCount()-3+" comments more");
        else {
            moreComments.setVisibility(View.GONE);
        }
        EditText enterComment = (EditText) findViewById(R.id.enterComment);
        enterComment.setFocusableInTouchMode(false);
        enterComment.setFocusable(false);
        enterComment.setOnClickListener(new EnterCommentListner(getBaseContext(),"days",mDay.getId(),mDay.getCommentCount()));

        mRecommendationLayout = (LinearLayout) findViewById(R.id.recommendationLayout);
        new RecommendationAsyncTask(recommendationHandler).execute(mAccessToken,mDayId);

    }
    Handler recommendationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                mRecommendationList = (List) msg.obj;
                for(Day day : mRecommendationList) {
                    mRecommendationLayout.addView(initRecommendation(day));
                }
            }
        }
    };
    protected void init(){
        setContentView(R.layout.day);
        medium = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Medium.ttf");
        regular = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Regular.ttf");
        initActionBar();
        new DayAsyncTask(handler).execute(mAccessToken, mDayId);
        mHeaderView =  LayoutInflater.from(this).inflate(R.layout.day_header, null, false);
        mFooterView =  LayoutInflater.from(this).inflate(R.layout.day_footer, null, false);
        mListView = (ListView) findViewById(R.id.listPhotos);
        mListView.addHeaderView(mHeaderView);
        mListView.addFooterView(mFooterView);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.i("scroll position",firstVisibleItem+" "+totalItemCount);
                if(firstVisibleItem+1 == totalItemCount) {
                    if(hasSocialBar) {
                        Animation ani = AnimationUtils.loadAnimation(getBaseContext(),R.anim.slide_down);
                        mSocialBar.startAnimation(ani);
                        mSocialBar.setVisibility(View.GONE);
                        hasSocialBar = false;
                    }
                }else {
                    if(!hasSocialBar) {
                        Animation ani = AnimationUtils.loadAnimation(getBaseContext(),R.anim.slide_up);
                        mSocialBar.startAnimation(ani);
                        mSocialBar.setVisibility(View.VISIBLE);
                        hasSocialBar = true;
                    }
                }

            }
        });
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMapRoute = new MapRoute(mMap,getBaseContext());
                UiSettings setting = map.getUiSettings();
                setting.setRotateGesturesEnabled(false);
                setting.setZoomControlsEnabled(false);
                setting.setMyLocationButtonEnabled(false);
            }
        });

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
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText("Day2-Beautiful Swiss");
        titleView.setTypeface(medium);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);

    }
    protected View initRecommendation(final Day day){
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mRecommend = mInflater.inflate(R.layout.interested_in,null);
        TextView placeName = (TextView) mRecommend.findViewById(R.id.placeName);
        TextView profileName = (TextView) mRecommend.findViewById(R.id.profileName);
        ImageView profileImage = (ImageView) mRecommend.findViewById(R.id.profileImage);
        TextView date = (TextView) mRecommend.findViewById(R.id.date);
        ImageView mainPhoto = (ImageView) mRecommend.findViewById(R.id.mainPhoto);
        TextView loveCount = (TextView) mRecommend.findViewById(R.id.loveCount);
        TextView commentCount = (TextView) mRecommend.findViewById(R.id.commentCount);
        TextView shareCount = (TextView) mRecommend.findViewById(R.id.shareCount);
        TextView dayIndex = (TextView) mRecommend.findViewById(R.id.dayIndex);

        User user = day.getUser();
        placeName.setText(day.getTitle());
        profileName.setText(Util.parseName(user, 0));
        Glide.with(this).load(user.getImageUrl()).asBitmap().transform(new CircleTransform(this)).into(profileImage);
        date.setText(HasBeenDate.convertDate(day.getDate()));
//        Glide.with(this).load(day.getMainPhoto().getMediumUrl()).into(mainPhoto);
        Glide.with(this).load(day.getPhotoList().get(0).getMediumUrl()).into(mainPhoto);
        loveCount.setText(day.getLoveCount()+"");
        commentCount.setText(day.getCommentCount() + "");
        shareCount.setText(day.getShareCount()+"");
        dayIndex.setText("Day "+day.getItineraryIndex());
        dayIndex.setTypeface(medium);
        placeName.setTypeface(medium);
        mRecommend.setOnClickListener(new View.OnClickListener() {
            boolean flag;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    Intent intent = new Intent(getBaseContext(), DayView.class);
                    intent.putExtra("dayId", day.getId());
                    startActivity(intent);
                    flag = false;
                }
            }
        });
        profileImage.setOnClickListener(new ProfileClickListner(this, day.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(this, day.getUser().getId()));
        return mRecommend;
    }
    ProgressDialog dialog;

    protected void showProgress() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setMessage("Wait a minutes...");
        dialog.setProgress(100);
        dialog.show();
    }
    @Override
    public void onDestroy() {
        if(mDayAdapter!=null)
            mDayAdapter.recycle();
        mRecommendationLayout.removeAllViews();
//        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
//        unbindDrawables(findViewById(R.id.listPhotos));
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();

        System.gc();
    }
}
