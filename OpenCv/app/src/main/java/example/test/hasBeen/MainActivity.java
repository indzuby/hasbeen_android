package example.test.hasBeen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

import example.test.hasBeen.database.CreateDataBase;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener{

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
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
            System.loadLibrary("opencv_java");
            System.loadLibrary("opencv_info");
            //System.loadLibrary("opencv_core");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showProgress();
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        ActionBar.Tab newsfeed = actionBar
                .newTab()
                .setIcon(R.drawable.ic_newsfeed)
                .setTabListener(this);
        ActionBar.Tab search = actionBar
                .newTab()
                .setIcon(R.drawable.ic_search)
                .setTabListener(this);
        ActionBar.Tab gallery = actionBar
                .newTab()
                .setIcon(R.drawable.ic_gallery)
                .setTabListener(this);
        ActionBar.Tab alarm = actionBar
                .newTab()
                .setIcon(R.drawable.ic_alarm)
                .setTabListener(this);
        ActionBar.Tab profile = actionBar
                .newTab()
                .setIcon(R.drawable.ic_profile)

                .setTabListener(this);
        actionBar.addTab(newsfeed);
        actionBar.addTab(search);
        actionBar.addTab(gallery);
        actionBar.addTab(alarm);
        actionBar.addTab(profile);
        mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {

                initDatabase();
                dialog.dismiss();
            }
        }).start();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

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
}
