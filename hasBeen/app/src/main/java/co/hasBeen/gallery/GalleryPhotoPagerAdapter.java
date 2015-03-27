package co.hasBeen.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import co.hasBeen.model.api.Photo;

/**
 * Created by zuby on 2015-01-20.
 */

public class GalleryPhotoPagerAdapter extends FragmentPagerAdapter {
    GalleryPhotoFragment[] fragments;
    List<Photo> photoList;
    public GalleryPhotoPagerAdapter(FragmentManager fm,List photoList)
    {
        super(fm);
        this.photoList = photoList;
        fragments = new GalleryPhotoFragment[photoList.size()];
    }

    public Fragment getItem(int index) {
        if(fragments[index]==null) {
            Bundle bundle = new Bundle();
            bundle.putLong("id",photoList.get(index).getId());
            fragments[index] = new GalleryPhotoFragment();
            fragments[index].setArguments(bundle);
        }
        return fragments[index];
    }
    public void setDescription(int index, String description) {
        fragments[index].setDescription(description);
    }
    public int getCount() {
        return photoList.size();
    }

}
