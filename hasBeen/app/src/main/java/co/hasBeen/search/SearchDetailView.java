package co.hasBeen.search;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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
    public EditText mSearchText;
    SearchPagerAdapter pagerAdapter;
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
        pagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeTab(position);
            }
        });
        mSearchText = (EditText) findViewById(R.id.searchText);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String serach = mSearchText.getText().toString();
                    doSearch(serach);
                }
                return false;
            }
        });
        View cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
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
    protected void doSearch(String keyword){
        if(mViewPager.getCurrentItem()==TRIP) {
            ((DayFragment)pagerAdapter.getItem(TRIP)).doSearch(keyword);

        }else {
            ((PeopleFragment)pagerAdapter.getItem(PEOPLE)).doSearch(keyword);
        }
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
