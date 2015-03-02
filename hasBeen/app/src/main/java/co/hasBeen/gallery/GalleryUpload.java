package co.hasBeen.gallery;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Photo;
import co.hasBeen.model.database.Place;
import co.hasBeen.model.database.Position;
import co.hasBeen.model.network.TakeStaticMap;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-29.
 */
public class GalleryUpload extends ActionBarActivity {
    Long mDayId;
    TextView mTextDate;
    TextView mTextArea;
    String mData;
    Day mDayUpload;
    DatabaseHelper database;
    Long mUserid;
    ContentResolver resolver;
    EditText mTitle;
    EditText mDescription;
    String mAccessToekn;
    int mUploadCount;
    boolean mStaticMapUpload ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseHelper(this);
        resolver = getContentResolver();
        init();
    }

    protected void init() {
        setContentView(R.layout.gallery_upload);
        mAccessToekn = Session.getString(this,"accessToken",null);
        initActionBar();
        mData = getIntent().getStringExtra("data");
        mDayUpload = JsonConverter.convertJsonToDay(mData);
        notifyDayChanged(mDayUpload);
        mDayUpload.setPlaceList(makePlaceList(mDayUpload));
        mUserid = Session.getLong(this, "myUserid", 0);
        mDayId = getIntent().getLongExtra("id", 0);
        mTextDate = (TextView) findViewById(R.id.date);
        mTextDate.setText(getIntent().getStringExtra("date"));
        mTextArea = (TextView) findViewById(R.id.placeName);
        mTextArea.setText(getIntent().getStringExtra("area"));
        ImageView mainPhoto = (ImageView) findViewById(R.id.mainPhoto);
        Glide.with(this).load(mDayUpload.getMainPhoto().getPhotoPath()).centerCrop().into(mainPhoto);
        TextView photoCount = (TextView) findViewById(R.id.photoCount);
        photoCount.setText("+"+ (mDayUpload.getPhotoCount()-1));
        mTitle = (EditText) findViewById(R.id.title);
        mDescription = (EditText) findViewById(R.id.description);
    }

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
        titleView.setText("Upload");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((mTitle.getText().toString().length()<2 || mTitle.getText().toString().length()>30) &&
                        (mDescription.getText().toString().length()<2 || mDescription.getText().toString().length()>255)) {
                    Toast.makeText(getBaseContext(),"Title은 2글자이상 30글자 이하여야하고,\nDescription은 2글자 이상 255글자 이하여야합니다",Toast.LENGTH_LONG).show();
                    return ;
                }

                showProgress();
                mUploadCount = 0;
                mStaticMapUpload = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(mUploadCount != mDayUpload.getPhotoCount() || !mStaticMapUpload)  {
                            dialog.setProgress(mUploadCount);
                        }
                        String title = mTitle.getText().toString();
                        String description = mDescription.getText().toString();
                        if(title.length()==0)
                            title = Util.convertPlaceName(mDayUpload.getPositionList());
                        mDayUpload.setTitle(title);
                        mDayUpload.setDescription(description);
                        Log.i("done", "pressed");
                        new UploadAsyncTask(mUploadHandler).execute(mAccessToekn, mDayUpload);
                    }
                }).start();
                uploadStorage();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
    }
    Handler mUploadHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                Toast.makeText(getBaseContext(),"Upload complete.",Toast.LENGTH_LONG).show();
                dialog.dismiss();
//                Intent intent = new Intent(getBaseContext(), DayView.class);
//                intent.putExtra("dayId", (Long)msg.obj);
//                startActivity(intent);
                setResult(RESULT_OK);
                finish();
            }else {
                Toast.makeText(getBaseContext(),"Upload error.",Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
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
        int count = 0 ;
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
        int count = 0 ;
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

    protected void uploadStorage() {
        try {
            new TakeStaticMap(new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0) {
                        String binary = (String) msg.obj;
                        mDayUpload.setStaticMapBinary(binary);
                        mStaticMapUpload = true;
                    }else {
                        Toast.makeText(getBaseContext(),"Upload error.",Toast.LENGTH_LONG).show();
                        setResult(RESULT_CANCELED);
                        finish();
                    }

                }
            }).execute(mDayUpload.getMainPhoto().getLat(), mDayUpload.getMainPhoto().getLon());
            for (Position position : mDayUpload.getPositionList()) {
                for (Photo photo : position.getPhotoList()) {
                    try {
                        photo.setBinary(Util.getLargeImage(photo));
                        mUploadCount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    ProgressDialog dialog;

    protected void showProgress() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading image...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(mDayUpload.getPhotoCount());
        dialog.show();
    }
}
