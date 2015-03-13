package co.hasBeen.social;

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
                    ListView listView = (ListView) findViewById(R.id.list);
                    View mHeaderView = LayoutInflater.from(getBaseContext()).inflate(R.layout.follower_header, null, false);
                    TextView count = (TextView) mHeaderView.findViewById(R.id.count);
                    count.setText(getString(R.string.friends,mFriends.size()));
                    listView.addHeaderView(mHeaderView);
                    FollowingAdapter followingAdapter = new FollowingAdapter(mFriends, getBaseContext());
                    followingAdapter.mType = "other";
                    listView.setAdapter(followingAdapter);
                }
                stopLoading();
            }
        }).execute(mAccessToekn);
    }

    protected void init() {
        setContentView(R.layout.follower);
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
}