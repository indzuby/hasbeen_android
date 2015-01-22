package example.test.hasBeen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.opencv.android.OpenCVLoader;

import java.util.Arrays;

import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.gallery.GalleryActivity;
import example.test.hasBeen.gallery.GalleryAdapter;
import example.test.hasBeen.gallery.GalleryDayListActivity;
import example.test.hasBeen.gallery.GalleryFragment;
import example.test.hasBeen.utils.Util;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener{

    private String TAG = "MainActivity";
    Button btnGallery,btnMap,btnDB,btnDBclear;
    LoginButton authButton;
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
//        btnGallery = (Button) findViewById(R.id.btn_gallery);
//        btnGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(getBaseContext(), GalleryActivity.class);
////                startActivity(intent);
//                GalleryActivity fragment = new GalleryActivity();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_layout,fragment);
//                transaction.commit();
//
//            }
//        });
//        btnMap = (Button) findViewById(R.id.btn_map);
//        btnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), MapActivity.class);
//                startActivity(intent);
//            }
//        });
//        authButton = (LoginButton) findViewById(R.id.btn_facebookLogin);
//        // set permission list, Don't foeget to add email
//        authButton.setReadPermissions(Arrays.asList("user_likes", "email"));
//        // session state call back event
//        authButton.setSessionStatusCallback(new Session.StatusCallback() {
//
//            @Override
//            public void call(Session session, SessionState state, Exception exception) {
//
//                if (session.isOpened()) {
//                    Log.i(TAG, "Access Token" + session.getAccessToken());
//                    Request.executeMeRequestAsync(session,
//                            new Request.GraphUserCallback() {
//                                @Override
//                                public void onCompleted(GraphUser user, Response response) {
//                                    if (user != null) {
//                                        Log.i(TAG, "User ID " + user.getId());
//                                        Log.i(TAG, "Email " + user.asMap().get("email"));
//                                        Toast.makeText(getBaseContext(), user.asMap().get("email").toString(), Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            });
//                }
//            }
//        });
////        final DBHelper db = new DBHelper(getBaseContext());
//        final DatabaseHelper dbHelper = new DatabaseHelper(this);
//        btnDB = (Button) findViewById(R.id.btn_db);
//        btnDB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(getBaseContext(), GalleryDayListActivity.class);
////                startActivity(intent);
//                GalleryFragment fragment = new GalleryFragment();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_layout,fragment);
//                transaction.commit();
//            }
//        });

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
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
}
