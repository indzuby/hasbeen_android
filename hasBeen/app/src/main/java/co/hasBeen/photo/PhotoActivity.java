package co.hasBeen.photo;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.localytics.android.Localytics;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Photo;
import co.hasBeen.report.ReportListner;
import co.hasBeen.social.ShareListner;
import co.hasBeen.utils.HasBeenPager;
import co.hasBeen.utils.Session;

/**
* Created by zuby on 2015-03-31.
*/
public class PhotoActivity extends ActionBarActivity {
    final static String TAG = "Photo view";
    Photo mPhoto;
    Long mPhotoId;
    List<Photo> mPhotoList;
    String mAccessToken;
    TextView mShareCount;
    PhotoDialog mPhotoDialog;
    Long mMyid;
    EditText mDescription;
    String mBeforeDescription;
    boolean isEdit;
    InputMethodManager mImm;
    TextView titleView;
    View dayButton;
    HasBeenPager mViewPager;
    PhotoPagerAdapter pagerAdapter;
    PhotoListAsyncTask asynctask;
    Handler listHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                mPhotoList = (List)msg.obj;
                init();
            }else {

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view);
        mPhotoId = getIntent().getLongExtra("id",0);
        mAccessToken = Session.getString(this, "accessToken", null);
        mMyid = Session.getLong(this, "myUserid", 0);
        asynctask = new PhotoListAsyncTask(listHandler);
        asynctask.execute(mAccessToken,mPhotoId);
        initActionBar();
    }
    protected void init() {
        mViewPager = (HasBeenPager) findViewById(R.id.pager);
        pagerAdapter = new PhotoPagerAdapter(getSupportFragmentManager(),mPhotoList);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPhoto = pagerAdapter.getPhoto(position);
            }
        });
        mViewPager.setCurrentItem(getIndex(mPhotoId));
    }
    protected int getIndex(Long id) {
        int i=0;
        for(Photo photo : mPhotoList) {
            if(photo.getId().equals(id)) return i;
            i++;
        }
        return 0;
    }
    protected void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_photo, null);
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
        View moreVert = mCustomActionBar.findViewById(R.id.moreVert);
        moreVert.setOnClickListener(new EditLisnter());
        dayButton = findViewById(R.id.dayButton);
    }

    public void initEditActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);
        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText(getString(R.string.action_bar_edit_title));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnEditView(mBeforeDescription);
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        TextView done = (TextView) mCustomActionBar.findViewById(R.id.actionBarDone);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeforeDescription = mDescription.getText().toString();
                new PhotoEditAsyncTask(new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == 0)
                            backOnEditView(mBeforeDescription);
                        else
                            Toast.makeText(getBaseContext(), getString(R.string.common_error), Toast.LENGTH_LONG).show();

                    }
                }).execute(mAccessToken, mPhoto.getId(), mBeforeDescription);
            }
        });
    }
    public void backOnEditView(String description) {
        mDescription.setFocusable(false);
        mDescription.setFocusableInTouchMode(false);
        mDescription.setText(description);
        isEdit = false;
        initActionBar();
        mImm.hideSoftInputFromWindow(mDescription.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        titleView.setText(mPhoto.getDay().getTitle());
    }

    @Override
    public void onDestroy() {
        asynctask.cancel(true);
        System.gc();
        super.onDestroy();
    }

    class EditLisnter implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(mPhotoList==null) return;
            mPhoto = pagerAdapter.getPhoto(getIndex(mPhotoId));
            if (mPhoto.getUser().getId() == mMyid) {
                View.OnClickListener del = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PhotoDeleteAsyncTask(new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                mPhotoDialog.dismiss();
                                super.handleMessage(msg);
                                if (msg.what == 0) {
                                    Toast.makeText(getBaseContext(), getString(R.string.remove_photo_ok), Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(getBaseContext(), getString(R.string.remove_photo_error), Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute(mAccessToken, mPhoto.getId());
                    }
                };
                View.OnClickListener edit = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPhotoDialog.dismiss();
                        setEdit();
                    }
                };
                View.OnClickListener cover = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CoverAsyncTask(new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                if (msg.what == 0) {
                                    Toast.makeText(getBaseContext(), getString(R.string.cover_ok), Toast.LENGTH_LONG).show();
                                    mPhotoDialog.dismiss();
                                } else {
                                    Toast.makeText(getBaseContext(), getString(R.string.common_error), Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute(mAccessToken, mPhoto.getId());
                    }
                };
                mPhotoDialog = new PhotoDialog(PhotoActivity.this, cover, del, edit);
                mPhotoDialog.show();
            } else {
                mPhotoDialog = new PhotoDialog(PhotoActivity.this, true);
                View.OnClickListener report = new ReportListner("photos", mPhoto.getId(), getBaseContext(), mPhotoDialog);
                View.OnClickListener share = new ShareListner(getBaseContext(), "photos", mPhoto, mShareCount);
                mPhotoDialog.setLisnter(report,share);
                mPhotoDialog.show();
            }
        }

    }
    @Override
    public void onBackPressed() {
        if(!isEdit)
            super.onBackPressed();
        else
            backOnEditView(mBeforeDescription);

    }
    public void setEdit(){
        mDescription.setFocusable(true);
        mDescription.setFocusableInTouchMode(true);
        isEdit = true;
        mBeforeDescription = mDescription.getText().toString();
        mDescription.requestFocus(mBeforeDescription.length() - 1);
        initEditActionBar();
        mImm.showSoftInput(mDescription, InputMethodManager.RESULT_UNCHANGED_SHOWN);
        mImm.showSoftInput(mDescription, InputMethodManager.SHOW_FORCED);
        mImm.showSoftInput(mDescription, InputMethodManager.SHOW_IMPLICIT);
        mImm.showSoftInput(mDescription, InputMethodManager.RESULT_SHOWN);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Photo View");
        Localytics.upload();
    }
}

