package co.hasBeen.gallery;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.database.ItemModule;
import co.hasBeen.day.DayDialog;
import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-04.
 */
public class GalleryDayView extends ActionBarActivity {
    Long mDayId;
    ItemModule mItemModule;
    Typeface medium,regular;
    ListView mListView;
    GalleryPositionAdapter mPositionAdapter ;
    List<Position> mPositionList;
    View mLoading;
    Day mDay;
    boolean isLoading;
    DatabaseHelper database;
    TextView titleView;
    DayDialog mDayDialog;
    EditText mDayTitle;
    EditText mDescription;
    boolean isEdit = false;
    String mBeforeTitle="",mBeforeDescription;
    InputMethodManager mImm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDayId = getIntent().getLongExtra("dayId", 0);
        init();
    }

    class LoadThread extends Thread {

        @Override
        public void run() {
            if (isLoading) {
                try {
                    mPositionList =  mItemModule.getPhotosByDate(mDayId);
                    initPlace(mPositionList);
                    mDay.setPositionList(mPositionList);
                    Thread.sleep(500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPositionAdapter = new GalleryPositionAdapter(getBaseContext(),mPositionList);
                            mListView.setAdapter(mPositionAdapter);
                            try {
                                initHeader();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            stopLoading();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    protected void init(){
        setContentView(R.layout.gallery_list);
        medium = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Medium.ttf");
        regular = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Regular.ttf");
        try {
            database = new DatabaseHelper(getBaseContext());
            mDay = database.selectDay(mDayId);
            mItemModule = new ItemModule(this);
            mPositionList = new ArrayList<>();
            mListView = (ListView) findViewById(R.id.listPhotos);
            mLoading = findViewById(R.id.refresh);
            View headerView =  LayoutInflater.from(this).inflate(R.layout.gallery_header, null, false);
            mListView.addHeaderView(headerView);
            initActionBar();
            startLoading();
            new LoadThread().start();
            ImageButton shareButton = (ImageButton) findViewById(R.id.shareButton);
            shareButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                Toast.makeText(getActivity().getBaseContext(),"Floating button pressed ",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getBaseContext(), GalleryShare.class);
                    intent.putExtra("dayId", mDayId);
                    startActivity(intent);
                }
            });
            mDay.setMainPlace(database.selectPlace(mDay.getMainPlaceId()));
            ImageView map = (ImageView) findViewById(R.id.map);
            Glide.with(this).load(Util.getMapUrl(mDay.getMainPlace().getLat(),mDay.getMainPlace().getLon())).centerCrop().placeholder(Util.getPlaceHolder(1)).into(map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void initActionBar() throws Exception{
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setTypeface(medium);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        ImageView moreVert = (ImageView) mCustomActionBar.findViewById(R.id.moreVert);
        moreVert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View.OnClickListener del = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getBaseContext(), "준비중입니다.", Toast.LENGTH_LONG).show();
                    }
                };
                View.OnClickListener edit = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDayDialog.dismiss();
                        mDayTitle.setFocusable(true);
                        mDayTitle.setFocusableInTouchMode(true);
                        mDescription.setFocusable(true);
                        mDescription.setFocusableInTouchMode(true);
                        isEdit = true;
                        mBeforeTitle = mDayTitle.getText().toString();
                        mBeforeDescription = mDescription.getText().toString();
                        initEditActionBar();
                        mImm.showSoftInput(mDayTitle, InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        mDayTitle.requestFocus(mBeforeTitle.length() - 1);
                    }
                };
                mDayDialog = new DayDialog(GalleryDayView.this, del, edit);
                mDayDialog.show();
            }
        });
    }
    protected void initHeader() throws Exception{
        View headView = findViewById(R.id.galleryTitleBox);
        TextView date = (TextView) headView.findViewById(R.id.date);
        TextView placeName = (TextView) headView.findViewById(R.id.placeName);
        date.setText(HasBeenDate.convertDate(mDay.getDate()));
        initPlace(mPositionList);
        placeName.setText(Util.convertPlaceName(mPositionList));
        if(mDay.getTitle().length()>0)
            titleView.setText(mDay.getTitle());
        else
            titleView.setText(Util.convertPlaceName(mPositionList));
        mDayTitle = (EditText) findViewById(R.id.title);
        mDescription = (EditText) findViewById(R.id.description);
        TextView totalPhoto = (TextView) findViewById(R.id.totalPhoto);
        mDayTitle.setText(mDay.getTitle());
        mDescription.setText(mDay.getDescription());
        mDescription.setTypeface(regular);
        totalPhoto.setText("Total " + mDay.getPhotoCount() + " photos");
        findViewById(R.id.socialAction).setVisibility(View.GONE);
    }
    protected void initEditActionBar(){
        findViewById(R.id.shareButton).setVisibility(View.GONE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);
        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText("Edit Day");
        titleView.setTypeface(medium);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    backOnEditView(mBeforeTitle, mBeforeDescription);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        TextView done = (TextView) mCustomActionBar.findViewById(R.id.actionBarDone);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeforeTitle  = mDayTitle.getText().toString();
                mBeforeDescription = mDescription.getText().toString();
                mDay.setDescription(mBeforeDescription);
                mDay.setTitle(mBeforeTitle);
                try{
                    database.updateDay(mDay);
                    backOnEditView(mBeforeTitle,mBeforeDescription);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    protected void initPlace(List<Position> positions) throws Exception{
        for(Position position : positions)
            position.setPlace(database.selectPlace(position.getPlaceId()));
    }
    protected void startLoading() {
        isLoading = true;
        mLoading.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        isLoading = false;
        mLoading.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }
    protected void backOnEditView(String title, String description) throws Exception{
        mDayTitle.setFocusable(false);
        mDayTitle.setFocusableInTouchMode(false);
        mDescription.setFocusable(false);
        mDescription.setFocusableInTouchMode(false);
        mDayTitle.setText(title);
        mDescription.setText(description);
        findViewById(R.id.shareButton).setVisibility(View.VISIBLE);
        isEdit = false;
        initActionBar();
        initHeader();
        mImm.hideSoftInputFromWindow(mDayTitle.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
