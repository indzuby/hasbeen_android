package co.hasBeen.profile.follow;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.localytics.android.Localytics;

import co.hasBeen.R;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FollowView extends ActionBarActivity implements View.OnClickListener{
    final static int FOLLOWER = 0;
    final static int FOLLOWING = 1;

    View mFollowerButton;
    View mFollowingButton;
    ViewPager mViewPager;
    Long mUserId;
    String mType;
    int mPage;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getIntent().getLongExtra("userId",0);
        mType = getIntent().getStringExtra("type");
        mPage = getIntent().getIntExtra("page",0);
        init();
    }

    protected void init() {
        setContentView(R.layout.follow_view);
        initActionBar();
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
        titleView.setText(getString(R.string.action_bar_follow_title));
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

        mFollowerButton =  findViewById(R.id.follower);
        mFollowingButton =  findViewById(R.id.following);

        mFollowerButton.setOnClickListener(this);
        mFollowingButton.setOnClickListener(this);
        mFollowerButton.setSelected(true);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        FollowPagerAdapter pagerAdapter = new FollowPagerAdapter(getSupportFragmentManager(),mUserId,mType);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeTab(position);
            }
        });
        mViewPager.setCurrentItem(mPage);
    }
    protected void changeTab(int position){
        mFollowerButton.setSelected(false);
        mFollowingButton.setSelected(false);
        if (position==0)
            mFollowerButton.setSelected(true);
        else if (position==1)
            mFollowingButton.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.follower) {
            mViewPager.setCurrentItem(FOLLOWER);
        }else
            mViewPager.setCurrentItem(FOLLOWING);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Follow View");
        Localytics.upload();
    }
}