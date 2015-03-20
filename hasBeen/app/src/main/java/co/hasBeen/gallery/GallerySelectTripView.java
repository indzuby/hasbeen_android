package co.hasBeen.gallery;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Trip;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-17.
 */
public class GallerySelectTripView extends ActionBarActivity {
    String mAccessToekn;
    View mLoading;
    boolean isLoading;
    List<Trip> mTrip;
    ListView mListView;
    Long mUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccessToekn = Session.getString(this, "accessToken", null);
        mUserId = Session.getLong(this,"myUserid",0L);
        init();
    }
    Handler tripHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                mTrip = (List) msg.obj;
                GallerySelectAdapter adapter = new GallerySelectAdapter(mTrip,GallerySelectTripView.this);
                mListView.setAdapter(adapter);
                stopLoading();
            }
        }
    };
    TripAsyncTask asyncTask;
    protected void init(){
        setContentView(R.layout.gallery_select_trip);
        initActionBar();
        mLoading = findViewById(R.id.refresh);
        startLoading();
        mListView = (ListView)findViewById(R.id.listView);
        asyncTask = new TripAsyncTask(tripHandler);
        asyncTask.execute(mAccessToekn,mUserId);
    }
    protected void initActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText(getString(R.string.action_bar_select_day));
        ImageView moreVert = (ImageView) mCustomActionBar.findViewById(R.id.moreVert);
        moreVert.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
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
    protected void onDestroy() {
        asyncTask.cancel(true);
        super.onDestroy();
    }
}
