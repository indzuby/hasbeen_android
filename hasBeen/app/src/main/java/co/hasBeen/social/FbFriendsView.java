package co.hasBeen.social;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localytics.android.Localytics;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Follow;
import co.hasBeen.profile.follow.FollowingAdapter;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FbFriendsView extends ActionBarActivity{
    List<Follow> mFriends;
    String mAccessToekn;
    View mLoading;
    boolean isLoading;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mAccessToekn = Session.getString(this, "accessToken", null);
        new FbFriendsAsyncTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    mFriends = (List) msg.obj;
                    ListView listView = (ListView) findViewById(R.id.listView);
                    View mHeaderView = getHeaderView();
                    listView.addHeaderView(mHeaderView);
                    FollowingAdapter followingAdapter = new FollowingAdapter(mFriends, getBaseContext());
                    followingAdapter.mType = "other";
                    listView.setAdapter(followingAdapter);
                }
                stopLoading();
            }
        }).execute(mAccessToekn);
    }
    protected View getHeaderView(){
        View mHeaderView = LayoutInflater.from(getBaseContext()).inflate(R.layout.follower_header, null, false);
        RelativeLayout box = (RelativeLayout)mHeaderView.findViewById(R.id.followerHeader);
        box.getLayoutParams().height = (int) getResources().getDimension(R.dimen.fb_count_height);
        TextView count = (TextView) mHeaderView.findViewById(R.id.count);
        count.setText(getString(R.string.your_facebook_friend_count,mFriends.size()));
        View folowAll = mHeaderView.findViewById(R.id.followAll);
        folowAll.setVisibility(View.VISIBLE);
        folowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FbFollowAsyncTask(new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if(msg.what==0) {
                            Toast.makeText(getBaseContext(),getString(R.string.all_follow_success),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(FbFriendsView.this,FbFriendsView.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).execute(mAccessToekn);
            }
        });
        return mHeaderView;
    }
    protected void init() {
        setContentView(R.layout.follow);
        initActionBar();
        mLoading = findViewById(R.id.refresh);
        startLoading();

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
        titleView.setText(getString(R.string.action_bar_fbfriends_title));
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
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Facebook friend list View");
        Localytics.upload();
    }
}