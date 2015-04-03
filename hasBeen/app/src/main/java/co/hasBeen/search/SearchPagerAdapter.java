package co.hasBeen.search;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by zuby on 2015-01-20.
 */

public class SearchPagerAdapter extends FragmentPagerAdapter {
    Fragment mTrip = null, mPeople = null;
    public SearchPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public Fragment getItem(int index) {
        switch (index) {
            case 0: // index에 따라서 다른 fragment 돌려준다.
                if (mTrip == null)
                    mTrip = new DayFragment();
                return mTrip;
            case 1:
                if (mPeople == null)
                    mPeople = new PeopleFragment();
                return mPeople;
        }
        return null;

    }

    public int getCount() {
        return 2;
    }

}