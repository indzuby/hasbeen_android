package co.hasBeen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.localytics.android.Localytics;


import co.hasBeen.account.LoginActivity;
import co.hasBeen.alarm.AlarmCountAsyncTask;
import co.hasBeen.alarm.AlarmFragment;
import co.hasBeen.gallery.GalleryFragment;
import co.hasBeen.model.api.AlarmCount;
import co.hasBeen.newsfeed.NewsFeedFragment;
import co.hasBeen.profile.ProfileFragment;
import co.hasBeen.utils.Session;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    public final static int REQUEST_LOGOUT =2020;
    View newsfeed,search,gallery,alarm,profile;
    boolean isAlarmRead = false;
    int tabIcon[] = {R.drawable.newsfeed_pressed,R.drawable.search_pressed,R.drawable.gallery_pressed,R.drawable.alarm_pressed,R.drawable.profile_pressed};
    TextView mCount;
    AlarmCount mAlarmCount;
    private String TAG = "MainActivity";
    TabPagerAdapter mPagerAdapter;
    ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.action_bar_tab, null, false);
        newsfeed = view.findViewById(R.id.newsfeed);
        search = view.findViewById(R.id.search);
        gallery = view.findViewById(R.id.gallery);
        alarm = view.findViewById(R.id.alarm);
        mCount = (TextView)alarm.findViewById(R.id.count);
        profile = view.findViewById(R.id.profile);
        newsfeed.setOnClickListener(this);
        search.setOnClickListener(this);
        gallery.setOnClickListener(this);
        alarm.setOnClickListener(this);
        profile.setOnClickListener(this);
        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
        mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeIcon(position);
            }
        });
        clearSelect();
        newsfeed.setSelected(true);
        getAlarmCount();
    }
    public void getAlarmCount(){
        String accessToken = Session.getString(this,"accessToken",null);
        mAlarmCount = new AlarmCount();
        new AlarmCountAsyncTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    mAlarmCount = (AlarmCount) msg.obj;
                    if(mAlarmCount.getTotalCount()==0)
                        return ;
                    mCount.setText(mAlarmCount.getTotalCount() + "");
                    mCount.setVisibility(View.VISIBLE);
                }
            }
        }).execute(accessToken);
    }
    public void readAlarm() {
        mCount.setVisibility(View.GONE);
    }
    void clearSelect(){
        newsfeed.setSelected(false);
        search.setSelected(false);
        gallery.setSelected(false);
        alarm.setSelected(false);
        profile.setSelected(false);
    }
    @Override
    public void onClick(View v) {
        clearSelect();
        v.setSelected(true);
        switch (v.getId()) {
            case R.id.newsfeed :
                changeTab(0);
                break;
            case R.id.search :
                changeTab(1);
                break;
            case R.id.gallery :
                changeTab(2);
                break;
            case R.id.alarm :
                changeTab(3);
                break;
            case R.id.profile :
                changeTab(4);
                break;
        }
    }
    protected void changeIcon(int index){
        if(index!=3 && isAlarmRead){
            getAlarmCount();
            isAlarmRead = false;
        }
        clearSelect();
        switch (index) {
            case 0:
                newsfeed.setSelected(true);
                NewsFeedFragment newsFeed =(NewsFeedFragment) mPagerAdapter.getItem(0);
                newsFeed.autoRefresh();
                break;
            case 1:
                search.setSelected(true);
                break;
            case 2:
                gallery.setSelected(true);
                GalleryFragment gallery =(GalleryFragment) mPagerAdapter.getItem(2);
                if(gallery.isShowTab()) gallery.newDaysLoad();
                break;
            case 3:
                alarm.setSelected(true);
                isAlarmRead = true;
                AlarmFragment alarm =(AlarmFragment)mPagerAdapter.getItem(3);
                alarm.newAlarmLoad(mAlarmCount);
                readAlarm();
                break;
            case 4:
                profile.setSelected(true);
                ProfileFragment profile = (ProfileFragment)mPagerAdapter.getItem(4);
                if(profile.isShowTab()) profile.initAll();
                break;
        }
        mPagerAdapter.getItem(index).showTab();
    }
    public void changeTab(int position) {
        mViewPager.setCurrentItem(position,true);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    static final int REQUEST = 1;  // The request code
    static final int RESULT = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MyAdapter", "onActivityResult");
        if(resultCode == RESULT_OK && requestCode == REQUEST_LOGOUT) {
            finish();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
        if(requestCode==Session.REQUEST_PHOTO_CODE && resultCode==Session.DLETE_CODE) {
            Long id = data.getLongExtra("id",0);
            ProfileFragment profile = (ProfileFragment)mPagerAdapter.getItem(4);
            profile.mMapRoute.removeDayPin(id);
        }else if(requestCode==Session.REQUEST_DAY_CODE && resultCode==Session.DLETE_CODE) {
            Long id = data.getLongExtra("id",0);
            ProfileFragment profile = (ProfileFragment)mPagerAdapter.getItem(4);
            profile.mMapRoute.removePhotoPin(id);
        }
    }

    ProgressDialog dialog;
    @Override
    protected void onDestroy() {
        System.gc();
        Log.i("Destory","yes");
        Glide.get(getBaseContext()).clearMemory();
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Main Acivity");
        Localytics.upload();
    }
}
