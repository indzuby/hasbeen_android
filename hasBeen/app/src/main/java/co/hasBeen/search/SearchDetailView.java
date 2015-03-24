package co.hasBeen.search;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.localytics.android.Localytics;

import co.hasBeen.R;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-24.
 */
public class SearchDetailView extends ActionBarActivity implements View.OnClickListener{
    final static int TRIP = 0;
    final static int PEOPLE = 1;

    String mAccessToken;
    View mTripButton;
    View mPeopleButton;
    int mNowTab = TRIP;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_detail);
        mAccessToken = Session.getString(this,"accessToken",null);
        init();
    }
    protected void init(){
        mTripButton =  findViewById(R.id.tripButton);
        mPeopleButton =  findViewById(R.id.peopleButton);
        mTripButton.setSelected(true);
        mTripButton.setOnClickListener(this);
        mPeopleButton.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        SearchPagerAdapter pagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeTab(position);
            }
        });
    }
    protected void changeTab(int position){
        mTripButton.setSelected(false);
        mPeopleButton.setSelected(false);
        if (position==0)
            mTripButton.setSelected(true);
        else if (position==1)
            mPeopleButton.setSelected(true);
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tripButton) {
            mViewPager.setCurrentItem(TRIP);
        }else
            mViewPager.setCurrentItem(PEOPLE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Search detail view");
        Localytics.upload();
    }
}
