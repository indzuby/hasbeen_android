package co.hasBeen.gallery;

import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.main.TutorialDialog;
import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.database.ItemModule;
import co.hasBeen.database.Photo.PhotoSyncThread;
import co.hasBeen.day.DayDialog;
import co.hasBeen.map.EnterMapLisnter;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-04.
 */
public class GalleryDayView extends ActionBarActivity {
    Long mDayId;
    ItemModule mItemModule;
    ListView mListView;
    GalleryPositionAdapter mPositionAdapter;
    List<Position> mPositionList;
    View mLoading;
    Day mDay;
    boolean isLoading;
    DataBaseHelper database;
    TextView titleView;
    DayDialog mDayDialog;
    EditText mDayTitle;
    EditText mDescription;
    boolean isEdit = false;
    String mBeforeTitle = "", mBeforeDescription;
    InputMethodManager mImm;
    int totalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDayId = getIntent().getLongExtra("id", 0);
        init();
    }
    Handler syncHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                try {
                    new LoadThread().start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    class LoadThread extends Thread {
        @Override
        public void run() {
            try {
                if (!database.hasDay(mDayId)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), getString(R.string.day_deleted), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
                mPositionList = mItemModule.getPhotosByDayid(mDayId);
                initPlace(mPositionList);
                mDay.setPositionList(mPositionList);
                View fullScreen = findViewById(R.id.fullScreen);
                fullScreen.setOnClickListener(new EnterMapLisnter(GalleryDayView.this, mDay, mDay.getPositionList().get(0).getId()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPositionAdapter = new GalleryPositionAdapter(GalleryDayView.this, mPositionList, mDay);
                        mListView.setAdapter(mPositionAdapter);

                        showTutorial();
                        try {
                            initHeader();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(dialog.isShowing())
                            dialog.dismiss();
                    }
                });
                if(photoSyncThread ==null) {
                    photoSyncThread = new PhotoSyncThread(mDayId, getBaseContext(), syncHandler);
                    photoSyncThread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    protected void showTutorial(){
        boolean tutorial = Session.getBoolean(this, "galleryTutorial", false);
        if (!tutorial) {
            try {
                TutorialDialog dialog = new TutorialDialog(this, getString(R.string.gallery_tutorial, Util.getVersion(this)));
                dialog.show();
                Session.putBoolean(this, "galleryTutorial", true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void showProgress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int before = mItemModule.photoCount;
                while (before < totalCount) {
                    if (before != mItemModule.photoCount) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.setPhotoCount(mItemModule.photoCount);
                            }
                        });
                        before = mItemModule.photoCount;
                    }
                }
                dialog.dismiss();
            }
        }).start();
    }

    PhotoSyncThread photoSyncThread;
    protected void init() {
        setContentView(R.layout.gallery_list);
        try {
            database = new DataBaseHelper(getBaseContext());
            mDay = database.selectDay(mDayId);
            mItemModule = new ItemModule(this);
            mPositionList = new ArrayList<>();
            mListView = (ListView) findViewById(R.id.listPhotos);
            mLoading = findViewById(R.id.refresh);
            View headerView = LayoutInflater.from(this).inflate(R.layout.gallery_header, null, false);
            mListView.addHeaderView(headerView);
            initActionBar();
            totalCount = mDay.getPhotoCount();
            Log.i("totalCount",totalCount+"");
            initProgress();
            mItemModule.photoCount = 0 ;
            new LoadThread().start();
            showProgress();
            ImageButton shareButton = (ImageButton) findViewById(R.id.shareButton);
            shareButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), GalleryShare.class);
                    intent.putExtra("id", mDayId);
                    startActivityForResult(intent, GalleryShare.REQUEST_UPLOAD);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initActionBar() throws Exception {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
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
                View.OnClickListener edit = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDayDialog.dismiss();
                        setEdit();
                    }
                };
                mDayDialog = new DayDialog(GalleryDayView.this);
                mDayDialog.setListner(null, edit);
                mDayDialog.show();
            }
        });
    }

    protected void initHeader() throws Exception {
        View headView = findViewById(R.id.galleryTitleBox);
        TextView date = (TextView) headView.findViewById(R.id.date);
        TextView placeName = (TextView) headView.findViewById(R.id.placeName);
        date.setText(HasBeenDate.convertDate(mDay.getDate()));
        initPlace(mPositionList);
        placeName.setText(Util.convertPlaceName(mPositionList));
        if (mDay.getTitle().length() > 0)
            titleView.setText(mDay.getTitle());
        else
            titleView.setText(Util.convertPlaceName(mPositionList));
        mDayTitle = (EditText) findViewById(R.id.title);
        mDescription = (EditText) findViewById(R.id.description);
        TextView totalPhoto = (TextView) findViewById(R.id.totalPhoto);
        mDayTitle.setText(mDay.getTitle());
        mDescription.setText(mDay.getDescription());
        totalPhoto.setText(getString(R.string.total_photo_count, mDay.getPhotoCount()));
        findViewById(R.id.socialAction).setVisibility(View.GONE);
        mDay.setMainPlace(database.selectPlace(mDay.getMainPlaceId()));
        ImageView map = (ImageView) findViewById(R.id.map);
        Glide.with(this).load(Util.getMapUrl(mDay.getMainPlace().getLat(), mDay.getMainPlace().getLon())).centerCrop().placeholder(Util.getPlaceHolder(1)).into(map);
        mDayTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEdit();
            }
        });
        mDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEdit();
            }
        });
    }

    protected void setEdit() {
        mDayTitle.setFocusable(true);
        mDayTitle.setFocusableInTouchMode(true);
        mDescription.setFocusable(true);
        mDescription.setFocusableInTouchMode(true);
        isEdit = true;
        mBeforeTitle = mDayTitle.getText().toString();
        mBeforeDescription = mDescription.getText().toString();
        mDayTitle.requestFocus(mBeforeTitle.length() - 1);
        initEditActionBar();
        mImm.showSoftInput(mDayTitle, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    protected void initEditActionBar() {
        findViewById(R.id.shareButton).setVisibility(View.GONE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);
        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText(getString(R.string.edit_day));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    backOnEditView(mBeforeTitle, mBeforeDescription);
                } catch (Exception e) {
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
                mBeforeTitle = mDayTitle.getText().toString();
                mBeforeDescription = mDescription.getText().toString();
                mDay.setDescription(mBeforeDescription);
                mDay.setTitle(mBeforeTitle);
                try {
                    database.updateDay(mDay);
                    backOnEditView(mBeforeTitle, mBeforeDescription);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void initPlace(List<Position> positions) throws Exception {
        for (Position position : positions) {
            position.setPlace(database.selectPlace(position.getPlaceId()));
            position.setPhotoList(database.selectPhotoByPositionId(position.getId()));
        }
    }

    protected void backOnEditView(String title, String description) throws Exception {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EnterMapLisnter.REQUEST_CODE && resultCode == RESULT_OK) {
            int index = data.getIntExtra("index", -1);
            Log.i("call back index", index + "");
            if (index != -1)
                mListView.setSelection(index);
        } else if (requestCode == GalleryShare.REQUEST_UPLOAD && resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isEdit)
            super.onBackPressed();
        else {
            try {
                backOnEditView(mBeforeTitle, mBeforeDescription);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Gallery Day View");
        Localytics.upload();
    }

    LoadDialog dialog;

    protected void initProgress() {
        dialog = new LoadDialog(this);
        dialog.setMaxCount(totalCount);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog!=null && dialog.isShowing()) dialog.dismiss();
        if(photoSyncThread !=null)
            photoSyncThread.cancel();
    }
}
