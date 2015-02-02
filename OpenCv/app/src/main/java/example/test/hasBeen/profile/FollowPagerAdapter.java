package example.test.hasBeen.profile;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by zuby on 2015-01-20.
 */

public class FollowPagerAdapter extends FragmentPagerAdapter {
    Fragment mFollower= null, mFollowing = null;

    public FollowPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int index) {
        switch (index) {
            case 0: // index에 따라서 다른 fragment 돌려준다.
                if (mFollower== null)
                    mFollower= new FollowerFragment();
                return mFollower;
            case 1:
                if (mFollowing == null)
                    mFollowing = new FollowingFragment();
                return mFollowing;
        }
        return null;

    }

    public int getCount() {
        return 2;
    }

}
