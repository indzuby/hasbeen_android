package example.test.hasBeen.profile;

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

import example.test.hasBeen.R;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FollowView extends ActionBarActivity implements View.OnClickListener{

    final static int FOLLOWER = 0;
    final static int FOLLOWING = 1;
    int mNowTab = FOLLOWER;
    TextView mFollowerButton;
    TextView mFollowingButton;
    ViewPager mViewPager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    protected void init() {
        setContentView(R.layout.follow);
        initActionBar();

    }
    protected void initActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText("FollowingView & Following");
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

        mFollowerButton = (TextView) findViewById(R.id.follower);
        mFollowingButton = (TextView) findViewById(R.id.following);

        mFollowerButton.setOnClickListener(this);
        mFollowingButton.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        FollowPagerAdapter pagerAdapter = new FollowPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mNowTab != FOLLOWER) {
                    mFollowerButton.setTextColor(getResources().getColor(R.color.theme_color));
                    mFollowingButton.setTextColor(getResources().getColor(R.color.light_gray));
                    mNowTab = FOLLOWER;
                }else
                if (mNowTab != FOLLOWING) {
                    mFollowerButton.setTextColor(getResources().getColor(R.color.light_gray));
                    mFollowingButton.setTextColor(getResources().getColor(R.color.theme_color));
                    mNowTab = FOLLOWING;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.follower:
                    mFollowerButton.setTextColor(getResources().getColor(R.color.theme_color));
                    mFollowingButton.setTextColor(getResources().getColor(R.color.light_gray));
                    mNowTab = FOLLOWER;
                    mViewPager.setCurrentItem(mNowTab);
                break;
            case R.id.following:
                    mFollowerButton.setTextColor(getResources().getColor(R.color.light_gray));
                    mFollowingButton.setTextColor(getResources().getColor(R.color.theme_color));
                    mNowTab = FOLLOWING;
                    mViewPager.setCurrentItem(mNowTab);
                break;
        }
    }
}
