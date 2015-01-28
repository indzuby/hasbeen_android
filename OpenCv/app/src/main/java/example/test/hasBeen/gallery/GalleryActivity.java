package example.test.hasBeen.gallery;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.geolocation.GeoGoogle;
import example.test.hasBeen.model.database.Photo;


public class GalleryActivity extends Fragment {

    List<Photo> mPhotoList;
    GalleryAdapter galleryAdapter;
    ContentResolver resolver;
    GridView gallery;
    DatabaseHelper database;
    GeoGoogle geo;
    boolean flag;
    View view;
    protected void init() {
        gallery = (GridView) view.findViewById(R.id.galleryL2GridView);
        database = new DatabaseHelper(getActivity().getBaseContext());
        resolver = getActivity().getContentResolver();
        mPhotoList = new ArrayList<>();
        try {
            mPhotoList = database.selectPhotoClearestPhoto();
        } catch (Exception e) {
            e.printStackTrace();
        }

        galleryAdapter = new GalleryAdapter(getActivity().getBaseContext(), mPhotoList);
        gallery.setAdapter(galleryAdapter);
        geo = new GeoGoogle(getActivity().getBaseContext());
        flag = true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallery_level_2_item,container,false);
        init();
        return view;
    }


}
