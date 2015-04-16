package co.hasBeen.search;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    boolean isKeyPress=false;
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
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String search = mSearchText.getText().toString();
                    doSearch(search);
                }
                return false;
            }
        });
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = mSearchText.getText().toString();
                Log.i("key",s.toString());
                if(search.length()>2 && !isKeyPress) {
                    if(mViewPager.getCurrentItem()==TRIP) {
                        doSearch(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        View cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
            }
        });
        changeTab(TRIP);
    }
    protected void changeTab(int position){
        mTripButton.setSelected(false);
        mPeopleButton.setSelected(false);
        mSearchText.setText("");
        if (position==0) {
            mTripButton.setSelected(true);
            mSearchText.setHint(getString(R.string.hint_trips));
        }
        else if (position==1) {
            mPeopleButton.setSelected(true);
            mSearchText.setHint(getString(R.string.hint_users));
        }
    }
    protected void doSearch(String keyword){
        if(isKeyPress) mSearchText.setText(keyword);
        if(mViewPager.getCurrentItem()==TRIP) {
            ((DayFragment)pagerAdapter.getItem(TRIP)).doRecommnad(keyword);
        }else {
            ((PeopleFragment)pagerAdapter.getItem(PEOPLE)).doSearch(keyword);
        }
        isKeyPress = false;
    }
    protected void doSearch(String keyword, String placeId) {
        if(isKeyPress) mSearchText.setText(keyword);
        if(mViewPager.getCurrentItem()==TRIP) {
            ((DayFragment)pagerAdapter.getItem(TRIP)).doSearch(keyword,placeId);
        }
        isKeyPress = false;
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
