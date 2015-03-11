package co.hasBeen.day;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.comment.CommentView;
import co.hasBeen.comment.EnterCommentListner;
import co.hasBeen.social.LoveListner;
import co.hasBeen.map.EnterMapLisnter;
import co.hasBeen.model.api.Comment;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Position;
import co.hasBeen.model.api.User;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.report.ReportListner;
import co.hasBeen.social.ShareListner;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-27.
 */
public class DayView extends ActionBarActivity{
    final static String TAG = "Day View";
    public final static int REQUEST_CODE = 2001;
    TextView titleView;
    ListView mListView;
    View mHeaderView;
    View mFooterView;
    int nowScrolled = 0 ;
    View mLoading;
    Day mDay;
    boolean isLoading;
    DayAdapter mDayAdapter;
    Long mDayId;
    String mAccessToken;
    TextView mSocialAction;
    LinearLayout mRecommendationLayout;
    List<Day> mRecommendationList;
    LinearLayout mSocialBar;
    boolean hasSocialBar = true;
    DayDialog mDayDialog;
    EditText mDayTitle;
    EditText mDescription;
    boolean isEdit = false;
    String mBeforeTitle="",mBeforeDescription;
    InputMethodManager mImm;
    Long userId;
    List<Position> mPositions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDayId = getIntent().getLongExtra("id",0);
        mAccessToken = Session.getString(this,"accessToken",null);
        userId = Session.getLong(getBaseContext(), "myUserid", 0);
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
                    mPositions = positions;
                    mDay = day;
                    Log.i(TAG,mDay.getId()+"");
                    titleView.setText(mDay.getTitle());
                    initHeaderView();
                    initBodyView();
                    initFoorteView();
                    stopLoading();
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
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mDayTitle = (EditText) findViewById(R.id.title);
        mDescription = (EditText) findViewById(R.id.description);
        mSocialAction = (TextView) findViewById(R.id.socialAction);
        TextView totalPhoto = (TextView) findViewById(R.id.totalPhoto);
        Glide.with(this).load(mDay.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(this)).into(profileImage);
        Log.i(TAG, mDay.getMainPlace().getName());
        profileName.setText(Util.parseName(mDay.getUser(), this));
        placeName.setText(Util.convertPlaceName(mPositions));
        date.setText(HasBeenDate.convertDate(mDay.getDate()));
        mDayTitle.setText(mDay.getTitle());
        mDescription.setText(mDay.getDescription());
        mSocialAction.setText(getString(R.string.social_status,mDay.getLoveCount(),mDay.getCommentCount(),mDay.getShareCount()));
        totalPhoto.setText(getString(R.string.total_photo_count,mDay.getPhotoCount()));
        profileImage.setOnClickListener(new ProfileClickListner(this, mDay.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(this, mDay.getUser().getId()));
        ImageView map = (ImageView) findViewById(R.id.map);
        Glide.with(this).load(mDay.getStaticMapUrl()).centerCrop().placeholder(Util.getPlaceHolder(1)).into(map);
        View fullScreen = findViewById(R.id.fullScreen);
        fullScreen.setOnClickListener(new EnterMapLisnter(this,mDay,mDay.getPositionList().get(0).getId()));
    }
    protected void initBodyView (){
        mDayAdapter = new DayAdapter(this,mPositions,userId == mDay.getUser().getId(),mDay);
        mListView.setAdapter(mDayAdapter);
    }
    protected  void initFoorteView(){
        mSocialBar = (LinearLayout) findViewById(R.id.socialBar);
        LinearLayout commentButton = (LinearLayout)findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new EnterCommentListner(getBaseContext(),"days",mDay.getId(),mDay.getCommentCount()));
        LinearLayout loveButton = (LinearLayout) findViewById(R.id.loveButton);
        LinearLayout shareButton = (LinearLayout) findViewById(R.id.shareButton);
        ImageView love = (ImageView) loveButton.findViewById(R.id.love);
        TextView loveText = (TextView) loveButton.findViewById(R.id.loveText);
        if(mDay.getLove()!=null) {
            love.setImageResource(R.drawable.photo_like_pressed);
            loveText.setTextColor(this.getResources().getColor(R.color.light_black));
        }
        else {
            love.setImageResource(R.drawable.photo_like);
            loveText.setTextColor(this.getResources().getColor(R.color.light_gray));
        }
        loveButton.setOnClickListener(new LoveListner(this,mDay,"days",mSocialAction));

        shareButton.setOnClickListener(new ShareListner(this, "days",mDay.getId(),mDay.getLoveCount(),mDay.getPhotoCount(),mDay.getShareCount(),mSocialAction));
        LinearLayout commentBox = (LinearLayout) findViewById(R.id.commetBox);
        TextView moreComments = (TextView) findViewById(R.id.moreComments);
        List<Comment> comments = mDay.getCommentList();
        for(int i = 0 ;i<3 && i<comments.size();i++) {
            Comment comment = comments.get(i);
            commentBox.addView(CommentView.makeComment(this, comment));
        }
        if(mDay.getCommentCount()>3)
            moreComments.setText(getString(R.string.more_comment,mDay.getCommentCount()-3));
        else {
            moreComments.setVisibility(View.GONE);
        }
        EditText enterComment = (EditText) findViewById(R.id.enterComment);
        enterComment.setFocusableInTouchMode(false);
        enterComment.setFocusable(false);
        enterComment.setOnClickListener(new EnterCommentListner(getBaseContext(),"days",mDay.getId(),mDay.getCommentCount()));

        mRecommendationLayout = (LinearLayout) findViewById(R.id.recommendationLayout);
        try {
            new RecommendationAsyncTask(recommendationHandler).execute(mAccessToken, mDayId);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    Handler recommendationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                try {
                    mRecommendationList = (List) msg.obj;
                    for (Day day : mRecommendationList) {
                        mRecommendationLayout.addView(initRecommendation(day));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };
    protected void init(){
        setContentView(R.layout.day);
        initActionBar();
        mLoading = findViewById(R.id.refresh);
        startLoading();
        new DayAsyncTask(handler).execute(mAccessToken, mDayId);
        mHeaderView =  LayoutInflater.from(this).inflate(R.layout.gallery_header, null, false);
        mFooterView =  LayoutInflater.from(this).inflate(R.layout.day_footer, null, false);
        mListView = (ListView) findViewById(R.id.listPhotos);
        mListView.addHeaderView(mHeaderView);
        mHeaderView.setVerticalScrollBarEnabled(false);
        mHeaderView.setClickable(false);
        mListView.addFooterView(mFooterView);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
        titleView.setText(mBeforeTitle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        ImageView moreVert = (ImageView) mCustomActionBar.findViewById(R.id.moreVert);

        moreVert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDay.getUser().getId() == userId) {
                    View.OnClickListener del = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DayDeleteAsyncTask(new Handler(Looper.getMainLooper()){
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    if(msg.what==0) {
                                        Toast.makeText(getBaseContext(),getString(R.string.remove_day_ok),Toast.LENGTH_LONG).show();
                                        finish();
                                    }else {
                                        Toast.makeText(getBaseContext(),getString(R.string.common_error),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).execute(mAccessToken,mDay.getId());
                        }
                    };
                    View.OnClickListener edit = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDayDialog.dismiss();
                            mDayTitle.setFocusable(true);
                            mDayTitle.setFocusableInTouchMode(true);
                            mDescription.setFocusable(true);
                            mDescription.setFocusableInTouchMode(true);
                            isEdit = true;
                            mBeforeTitle = mDayTitle.getText().toString();
                            mBeforeDescription = mDescription.getText().toString();
                            initEditActionBar();
                            mImm.showSoftInput(mDayTitle, InputMethodManager.RESULT_UNCHANGED_SHOWN);
                            mDayTitle.requestFocus(mBeforeTitle.length() - 1);
                        }
                    };
                    mDayDialog = new DayDialog(DayView.this, del,edit);
                    mDayDialog.show();
                }else {
                    View.OnClickListener del = new ReportListner("days",mDay.getId(),getBaseContext(),mDayDialog);
                    View.OnClickListener edit = new ShareListner(getBaseContext(), "days",mDay.getId(),mDay.getLoveCount(),mDay.getPhotoCount(),mDay.getShareCount(),mSocialAction);
                    mDayDialog = new DayDialog(DayView.this, del,edit,true);
                    mDayDialog.show();
                }
            }
        });
    }

    protected void initEditActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);
        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText(getString(R.string.edit_day));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnEditView(mBeforeTitle,mBeforeDescription);
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        TextView done = (TextView) mCustomActionBar.findViewById(R.id.actionBarDone);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeforeTitle  = mDayTitle.getText().toString();
                mBeforeDescription = mDescription.getText().toString();
                new DayEditAsyncTask(new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if(msg.what==0)
                            backOnEditView(mBeforeTitle,mBeforeDescription);
                        else
                            Toast.makeText(getBaseContext(),getString(R.string.common_error),Toast.LENGTH_LONG).show();

                    }
                }).execute(mAccessToken, mDay.getId(),mBeforeTitle,mBeforeDescription);
            }
        });
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
        profileName.setText(Util.parseName(user, this));
        Glide.with(this).load(user.getImageUrl()).asBitmap().transform(new CircleTransform(this)).into(profileImage);
        date.setText(HasBeenDate.convertDate(day.getDate()));
        Glide.with(this).load(day.getMainPhoto().getMediumUrl()).placeholder(Util.getPlaceHolder(day.getItineraryIndex())).into(mainPhoto);
//        Glide.with(this).load(day.getPhotoList().get(0).getMediumUrl()).placeholder(Util.getPlaceHolder(day.getItineraryIndex())).into(mainPhoto);
        loveCount.setText(day.getLoveCount()+"");
        commentCount.setText(day.getCommentCount() + "");
        shareCount.setText(day.getShareCount() + "");
        dayIndex.setText("Day " + day.getItineraryIndex());
        mRecommend.setOnClickListener(new View.OnClickListener() {
            boolean flag;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    Intent intent = new Intent(getBaseContext(), DayView.class);
                    intent.putExtra("id", day.getId());
                    startActivity(intent);
                    flag = false;
                }
            }
        });
        profileImage.setOnClickListener(new ProfileClickListner(this, day.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(this, day.getUser().getId()));
        return mRecommend;
    }
    protected void backOnEditView(String title, String description){
        mDayTitle.setFocusable(false);
        mDayTitle.setFocusableInTouchMode(false);
        mDescription.setFocusable(false);
        mDescription.setFocusableInTouchMode(false);
        mDayTitle.setText(title);
        mDescription.setText(description);
        isEdit = false;
        initActionBar();
        mImm.hideSoftInputFromWindow(mDayTitle.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }
    @Override
    public void onBackPressed() {
        if(!isEdit)
            super.onBackPressed();
        else
            backOnEditView(mBeforeTitle,mBeforeDescription);

    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode == RESULT_OK) {
            startActivity(getIntent());
            finish();
        }else if(requestCode==EnterMapLisnter.REQUEST_CODE && resultCode == RESULT_OK) {
            int index = data.getIntExtra("index",-1);
            if(index!=-1) {
                Log.i("call back index",mDay.getPositionList().get(index).getPlace().getName());
                Log.i("call back index",mPositions.indexOf(mDay.getPositionList().get(index))+"");
                mListView.setSelection(mPositions.indexOf(mDay.getPositionList().get(index))+1);
                mListView.setScrollY(mListView.getScrollY()-Util.convertDpToPixel(56,this));
            }
        }
    }
}
