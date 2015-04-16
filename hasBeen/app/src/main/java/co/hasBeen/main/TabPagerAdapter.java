package co.hasBeen.main;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import co.hasBeen.alarm.AlarmFragment;
import co.hasBeen.gallery.GalleryFragment;
import co.hasBeen.newsfeed.NewsFeedFragment;
import co.hasBeen.profile.ProfileFragment;
import co.hasBeen.search.SearchFragment;
import co.hasBeen.utils.HasBeenFragment;

/**
 * Created by zuby on 2015-01-20.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {
    HasBeenFragment mNewsFeed = null, mSearch = null, mGallery = null, mAlarm = null, mProfile = null;

    public TabPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    public HasBeenFragment getItem(int index) {
        switch (index) {
            case 0: // index에 따라서 다른 fragment 돌려준다.
                if (mNewsFeed == null)
                    mNewsFeed = new NewsFeedFragment();
                return mNewsFeed;
            case 1:
                if (mSearch == null)
                    mSearch = new SearchFragment();
                return mSearch;
            case 2:
                if (mGallery == null)
                    mGallery = new GalleryFragment();
                return mGallery;
            case 3:
                if (mAlarm == null)
                    mAlarm = new AlarmFragment();
                return mAlarm;
            case 4:
                if (mProfile == null)
                    mProfile = new ProfileFragment();
                return mProfile;

        }
        return null;

    }

    public int getCount() {
        return 5;
    }

}
