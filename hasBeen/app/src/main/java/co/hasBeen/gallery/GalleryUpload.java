package co.hasBeen.gallery;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.hasBeen.R;
import co.hasBeen.main.TutorialDialog;
import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Place;
import co.hasBeen.model.api.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-29.
 */
public class GalleryUpload extends ActionBarActivity {
    final static int REQUEST_CODE = 2001;
    Long mDayId;
    TextView mTextDate;
    TextView mTextArea;
    String mData;
    Day mDayUpload;
    DataBaseHelper database;
    Long mUserid;
    ContentResolver resolver;
    EditText mTitle;
    EditText mDescription;
    String mAccessToekn;
    boolean isUpload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DataBaseHelper(this);
        resolver = getContentResolver();
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void init() throws Exception {
        setContentView(R.layout.gallery_upload);
        boolean tutorial = Session.getBoolean(this,"uploadTutorial",false);
        if(!tutorial) {
            try {
                TutorialDialog dialog = new TutorialDialog(this, getString(R.string.upload_tutorial, Util.getTutorialVersion()));
                dialog.show();
                Session.putBoolean(this, "uploadTutorial", true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        mAccessToekn = Session.getString(this, "accessToken", null);
        mData = getIntent().getStringExtra("data");
        mDayUpload = JsonConverter.convertJsonToDay(mData);
        notifyDayChanged(mDayUpload);
        mDayUpload.setPlaceList(makePlaceList(mDayUpload));
        mUserid = Session.getLong(this, "myUserid", 0);
        mDayId = getIntent().getLongExtra("id", 0);
        mTextDate = (TextView) findViewById(R.id.date);
        mTextDate.setText(HasBeenDate.convertDate(mDayUpload.getDate()));
        mTextArea = (TextView) findViewById(R.id.placeName);
        mTextArea.setText(Util.convertPlaceName(mDayUpload.getPositionList()));
        ImageView mainPhoto = (ImageView) findViewById(R.id.mainPhoto);
        Glide.with(this).load(mDayUpload.getMainPhoto().getPhotoPath()).centerCrop().into(mainPhoto);
        TextView photoCount = (TextView) findViewById(R.id.photoCount);
        photoCount.setText("+" + (mDayUpload.getPhotoCount() - 1));
        if (mDayUpload.getPhotoCount() == 1)
            photoCount.setVisibility(View.GONE);
        mTitle = (EditText) findViewById(R.id.title);
        mDescription = (EditText) findViewById(R.id.description);
        mTitle.setText(mDayUpload.getTitle());
        mTitle.setSelection(mDayUpload.getTitle().length());
        mDescription.setText(mDayUpload.getDescription());
        mDescription.setSelection(mDayUpload.getDescription().length());
        View daySelectBox = findViewById(R.id.daySelectBox);
        daySelectBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryUpload.this, GallerySelectTripView.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        initActionBar();
    }

    UploadAsyncTask uploadAsyncTask;

    protected void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        TextView doneButton = (TextView) mCustomActionBar.findViewById(R.id.actionBarDone);
        titleView.setText(getString(R.string.action_bar_upload_title));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitle.getText().toString().length() < 2 || mTitle.getText().toString().length() > 30) {
                    Toast.makeText(getBaseContext(), getString(R.string.title_size_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mDescription.getText().toString().length() < 2 || mDescription.getText().toString().length() > 255) {
                    Toast.makeText(getBaseContext(), getString(R.string.description_size_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (isUpload) return;
                isUpload = true;
//                showProgress();
                String title = mTitle.getText().toString();
                String description = mDescription.getText().toString();
                if (title.length() == 0)
                    title = Util.convertPlaceName(mDayUpload.getPositionList());
                mDayUpload.setTitle(title);
                mDayUpload.setDescription(description);
                uploadAsyncTask = new UploadAsyncTask(mUploadHandler);
                uploadAsyncTask.execute(mAccessToekn, mDayUpload);
//                        while(uploadAsyncTask.photoCount != mDayUpload.getPhotoCount())  {
//                            dialog.setPhotoCount(uploadAsyncTask.photoCount);
//                        }
                Toast.makeText(getBaseContext(), getString(R.string.uploading), Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    Handler mUploadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
//                Toast.makeText(getBaseContext(),getString(R.string.upload_ok),Toast.LENGTH_LONG).show();
//                dialog.dismiss();
//                Intent intent = new Intent(getBaseContext(), DayView.class);
//                intent.putExtra("id", (Long)msg.obj);
//                startActivity(intent);
//                setResult(RESULT_OK);
//                finish();
            } else {
//                dialog.dismiss();
//                Toast.makeText(getBaseContext(),getString(R.string.common_error),Toast.LENGTH_LONG).show();
//                setResult(RESULT_CANCELED);
//                finish();
            }
        }
    };

    protected List<Place> makePlaceList(Day day) {
        Map<Long, Place> placeList = new HashMap<>();
        List<Position> positionList = day.getPositionList();
        for (int i = 0; i < positionList.size(); i++) {
            Position position = positionList.get(i);
            try {
                position.setPlace(database.selectPlace(position.getPlaceId()));
                Place place = position.getPlace();
                place.setIdFromMobile(place.getId());
                placeList.put(position.getPlaceId(), place);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return setLocationName(placeList);
    }

    protected List<Place> setLocationName(Map<Long, Place> placeMap) {
//        for(Place place : placeMap.values()) {
//            place.setCity(GeoGoogle.getCity(this,place.getLat(),place.getLon()));
//            place.setCountry(GeoGoogle.getCountry(this, place.getLat(), place.getLon()));
//            Log.i("city country",place.getCity()+" "+place.getCountry());
//        }
        return new ArrayList<>(placeMap.values());
    }

    protected void notifyDayChanged(Day day) {
        int count = 0;
        List<Position> positionList = day.getPositionList();
        List<Position> newPositionList = new ArrayList<>();
        for (int i = 0; i < positionList.size(); i++) {
            Position position = positionList.get(i);
            if (day.getIsCheckedPosition()[i]) {
                position.setIdFromMobile(position.getId());
                newPositionList.add(position);
                count += notifyPhotoChanged(positionList.get(i));
            }
        }
        day.setPhotoCount(count);
        day.setPositionList(newPositionList);
    }

    protected int notifyPhotoChanged(Position position) {
        List<Photo> photoList = position.getPhotoList();
        List<Photo> newPhotoList = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < photoList.size(); i++) {
            Photo photo = photoList.get(i);
            if (position.getIsCheckedPhoto()[i]) {
                photo.setIdFromMobile(photo.getId());
                newPhotoList.add(photo);
                count++;
            }
        }
        position.setPhotoList(newPhotoList);
        return count;
    }

    LoadDialog dialog;

    protected void showProgress() {
        dialog = new LoadDialog(this);
        dialog.setCancelable(false);
        dialog.setMaxCount(mDayUpload.getPhotoCount());
        dialog.show();
    }

    protected void selectTrip(String placeName) {
        TextView tripTitle = (TextView) findViewById(R.id.tripTitle);
        tripTitle.setText(placeName);
        ImageView addTrip = (ImageView) findViewById(R.id.addTrip);
        addTrip.setImageResource(R.drawable.add_trip_pressed);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Long id = data.getLongExtra("id", 0L);
            String placeName = data.getStringExtra("placeName");
            selectTrip(placeName);
            mDayUpload.setTripId(id);
        }
    }

    @Override
    protected void onDestroy() {
        if (uploadAsyncTask != null) {
            uploadAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Gallery Upload View");
        Localytics.upload();
    }
}
