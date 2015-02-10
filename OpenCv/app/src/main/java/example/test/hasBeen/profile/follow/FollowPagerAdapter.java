package example.test.hasBeen.profile.follow;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by zuby on 2015-01-20.
 */

public class FollowPagerAdapter extends FragmentPagerAdapter {
    Fragment mFollower= null, mFollowing = null;
    Long mUserId;
    String mType;
    public FollowPagerAdapter(FragmentManager fm,Long userId,String type) {
        super(fm);
        mUserId = userId;
        mType = type;
    }

    public Fragment getItem(int index) {
        Bundle bundle = new Bundle();
        bundle.putLong("userId",mUserId);
        bundle.putString("type",mType);
        switch (index) {
            case 0: // index에 따라서 다른 fragment 돌려준다.
                if (mFollower== null)
                    mFollower = new FollowerFragment();

                mFollower.setArguments(bundle);
                return mFollower;
            case 1:
                if (mFollowing == null)
                    mFollowing = new FollowingFragment();
                mFollowing.setArguments(bundle);
                return mFollowing;
        }
        return null;

    }

    public int getCount() {
        return 2;
    }

}