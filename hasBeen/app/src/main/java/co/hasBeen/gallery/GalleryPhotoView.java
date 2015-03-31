package co.hasBeen.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.model.api.Photo;

/**
 * Created by 주현 on 2015-03-27.
 */
public class GalleryPhotoView extends ActionBarActivity {
    final static int REQUEST_CODE = 2000;
    Long mPositionId;
    Long mPhotoId;
    ViewPager mViewPager;
    List<Photo> photoList;
    DatabaseHelper database;
    GalleryPhotoPagerAdapter mAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPositionId = getIntent().getLongExtra("position_id",0);
        mPhotoId = getIntent().getLongExtra("photo_id", 0);
        database = new DatabaseHelper(this);
        try {
            photoList = database.selectPhotoByPositionId(mPositionId);
        }catch (Exception e){
            e.printStackTrace();
        }
        init();
    }
    protected void init() {
        setContentView(R.layout.gallery_photo_view);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new GalleryPhotoPagerAdapter(getSupportFragmentManager(),photoList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(getIndex(mPhotoId));
    }
    protected int getIndex(Long id) {
        int i=0;
        for(Photo photo : photoList) {
            if(photo.getId().equals(id)) return i;
            i++;
        }
        return 0;
//        return getIntent().getIntExtra("index",0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Photo photo = database.selectPhoto(data.getLongExtra("id",0));
                int index = getIndex(data.getLongExtra("id",0));
                mAdapter.setDescription(index, photo.getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
