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

import org.opencv.android.OpenCVLoader;

import co.hasBeen.alarm.AlarmCountAsyncTask;
import co.hasBeen.database.CreateDataBase;
import co.hasBeen.utils.Session;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    View newsfeed,search,gallery,alarm,profile;
    int tabIcon[] = {R.drawable.newsfeed_pressed,R.drawable.search_pressed,R.drawable.gallery_pressed,R.drawable.alarm_pressed,R.drawable.profile_pressed};
    TextView mCount;
    static {

        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
            System.loadLibrary("opencv_java");
            System.loadLibrary("opencv_info");
            //System.loadLibrary("opencv_core");
        }
    }
    private String TAG = "MainActivity";
    TabPagerAdapter mPagerAdapter;
    ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgress();
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
        changeIcon(0);
        mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeIcon(position);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                initDatabase();
                dialog.dismiss();
            }
        }).start();
        String accessToken = Session.getString(this,"accessToken",null);
        new AlarmCountAsyncTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    int count = (int) msg.obj;
                    if(count==0)
                        return ;
                    mCount.setText(count + "");
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
        clearSelect();
        switch (index) {
            case 0:
                newsfeed.setSelected(true);
                break;
            case 1:
                search.setSelected(true);
                break;
            case 2:
                gallery.setSelected(true);
                break;
            case 3:
                alarm.setSelected(true);
                break;
            case 4:
                profile.setSelected(true);
                break;
        }
    }

    protected void initDatabase(){
        try {
            CreateDataBase createDataBase = new CreateDataBase(this);
            createDataBase.takePhoto();
            createDataBase.buildDay();
        }catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
        if(resultCode == RESULT) {
            try {
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ProgressDialog dialog;

    protected void showProgress() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setMessage("Writting on database");
        dialog.setProgress(100);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        System.gc();
        Log.i("Destory","yes");
        System.exit(0);
        super.onDestroy();
    }
}
