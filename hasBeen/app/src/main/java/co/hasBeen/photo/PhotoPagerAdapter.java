package co.hasBeen.photo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import co.hasBeen.model.api.Photo;

/**
 * Created by 주현 on 2015-03-30.
 */
public class PhotoPagerAdapter extends FragmentPagerAdapter {
    PhotoFragment[] fragments;
    List<Photo> photoList;
    public PhotoPagerAdapter(FragmentManager fm,List photoList)
    {
        super(fm);
        this.photoList = photoList;
        fragments = new PhotoFragment[photoList.size()];
    }
    public PhotoFragment getItem(int index) {
        if(fragments[index]==null) {
            Bundle bundle = new Bundle();
            bundle.putLong("id",photoList.get(index).getId());
            fragments[index] = new PhotoFragment();
            fragments[index].setArguments(bundle);
        }
        return fragments[index];
    }
    public Photo getPhoto(int index) {
        return getItem(index).mPhoto;
    }
    public int getCount() {
        return photoList.size();
    }

}
