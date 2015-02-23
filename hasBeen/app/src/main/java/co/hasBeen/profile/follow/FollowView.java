package co.hasBeen.profile.follow;

import android.graphics.Typeface;
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

import co.hasBeen.R;

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
    Long mUserId;
    String mType;
    Typeface medium,regular;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getIntent().getLongExtra("userId",0);
        mType = getIntent().getStringExtra("type");
        init();
    }

    protected void init() {
        setContentView(R.layout.follow);
        medium = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Medium.ttf");
        regular = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Regular.ttf");
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
        titleView.setText("Follower & Following");
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
        mFollowerButton.setTypeface(medium);
        mFollowingButton.setOnClickListener(this);
        mFollowingButton.setTypeface(medium);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        FollowPagerAdapter pagerAdapter = new FollowPagerAdapter(getSupportFragmentManager(),mUserId,mType);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(0);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mNowTab != FOLLOWER) {
                    mFollowerButton.setTextColor(getResources().getColor(R.color.theme_color));
                    mFollowingButton.setTextColor(getResources().getColor(R.color.light_gray));
                    mNowTab = FOLLOWER;
                }else if (mNowTab != FOLLOWING) {
                    mFollowerButton.setTextColor(getResources().getColor(R.color.light_gray));
                    mFollowingButton.setTextColor(getResources().getColor(R.color.theme_color));
                    mNowTab = FOLLOWING;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.equals(mFollowerButton)) {
            mNowTab = FOLLOWER;
            ((TextView)findViewById(R.id.follower)).setTextColor(getResources().getColor(R.color.theme_color));
            ((TextView)findViewById(R.id.following)).setTextColor(getResources().getColor(R.color.light_gray));
            mViewPager.setCurrentItem(mNowTab);
        }else if(v.equals(mFollowingButton)){
            mNowTab = FOLLOWING;
            ((TextView)findViewById(R.id.following)).setTextColor(getResources().getColor(R.color.theme_color));
            ((TextView)findViewById(R.id.follower)).setTextColor(getResources().getColor(R.color.light_gray));
            mViewPager.setCurrentItem(mNowTab);
        }

    }
}