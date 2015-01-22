package example.test.hasBeen;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import example.test.hasBeen.gallery.GalleryActivity;
import example.test.hasBeen.gallery.GalleryFragment;

/**
 * Created by zuby on 2015-01-20.
 */
public class hasBeenTabListner implements ActionBar.TabListener {
    Activity mActivity;
    int mSelected;
    private Fragment mFragment;

    public hasBeenTabListner(Activity activity, int selected) {
        mActivity = activity;
        mSelected = selected;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment == null) {
            // If not, instantiate and add it to the activity

            if(mSelected != 3) {
                mFragment = Fragment.instantiate(mActivity, GalleryActivity.class.getName());
                ft.add(android.R.id.content, mFragment, "Gallery");
            }
            else if(mSelected == 3) {
                mFragment = Fragment.instantiate(mActivity, GalleryFragment.class.getName());
                ft.add(android.R.id.content, mFragment, "Gallery");
            }

        } else {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
