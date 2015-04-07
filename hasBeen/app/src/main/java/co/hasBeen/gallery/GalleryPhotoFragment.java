package co.hasBeen.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.localytics.android.Localytics;

import co.hasBeen.R;
import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.model.api.Photo;
import co.hasBeen.utils.HasBeenDate;

/**
 * Created by zuby on 2015-01-16.
 */
public class GalleryPhotoFragment extends Fragment {
    final static int REQUEST_CODE = 2000;
    Long mPhotoId;
    Photo mPhoto;
    DataBaseHelper database;
    View mActionBar;
    View mFilter;
    public View mDescription;
    boolean toogle = true;
    protected void init() throws Exception {
        ImageView photo = (ImageView) mView.findViewById(R.id.photo);
        Glide.with(this).load(mPhoto.getPhotoPath()).placeholder(R.drawable.placeholder1)
                .into(photo);
        initActionBar();
        initDescription();
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toogleDescription();
            }
        });
    }

    protected void toogleDescription() {
        if (toogle) { // hide
            mActionBar.setVisibility(View.GONE);
            mFilter.setVisibility(View.GONE);
            mDescription.setVisibility(View.GONE);
        } else { // show
            mActionBar.setVisibility(View.VISIBLE);
            mFilter.setVisibility(View.VISIBLE);
            mDescription.setVisibility(View.VISIBLE);
        }
        toogle = !toogle;
    }

    protected void initActionBar() throws Exception {
        mActionBar = mView.findViewById(R.id.actionBarLayout);
//        mActionBar.setBackgroundColor(getResources().getColor(R.color.transparent));
        TextView placeName = (TextView) mActionBar.findViewById(R.id.actionBarTitle);
        placeName.setText(database.selectPlaceNameByPlaceId(mPhoto.getPlaceId()));
        ImageButton back = (ImageButton) mActionBar.findViewById(R.id.actionBarBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("back button clicked", "");
                getActivity().finish();
            }
        });
        ImageView moreVert = (ImageView) mActionBar.findViewById(R.id.moreVert);
    }
    public void setDescription(String desc) {
        TextView description = (TextView) mView.findViewById(R.id.description);
        description.setText(desc);
    }
    protected void initDescription() throws Exception {
        mFilter = mView.findViewById(R.id.filter);
        mDescription = mView.findViewById(R.id.descriptionLayout);
        TextView date = (TextView) mView.findViewById(R.id.date);
        TextView description = (TextView) mView.findViewById(R.id.description);
        View moreDescription = mView.findViewById(R.id.moreDescription);
        date.setText(HasBeenDate.convertDate(mPhoto.getTakenTime()));
        description.setText(mPhoto.getDescription());
        if (mPhoto.getDescription().length() <= 0)
            moreDescription.setVisibility(View.GONE);

        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toogleDescription();
            }
        });
        mDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("description clicked", "");
                Intent intent = new Intent(getActivity(), GalleryPhotoEdit.class);
                intent.putExtra("id", mPhotoId);
                getActivity().startActivityForResult(intent, REQUEST_CODE);

            }
        });
    }
    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.gallery_photo, container, false);
        mPhotoId = getArguments().getLong("id");
        try {
            database = new DataBaseHelper(getActivity());
            mPhoto = database.selectPhoto(mPhotoId);
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Gallery Photo View");
        Localytics.upload();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                Photo photo = database.selectPhoto(mPhotoId);
                mPhoto.setDescription(photo.getDescription());
                TextView description = (TextView) mView.findViewById(R.id.description);
                description.setText(photo.getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}