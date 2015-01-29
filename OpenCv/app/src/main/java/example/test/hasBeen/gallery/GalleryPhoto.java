package example.test.hasBeen.gallery;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.utils.HasBeenDate;

/**
 * Created by zuby on 2015-01-16.
 */
public class GalleryPhoto extends ActionBarActivity {
    Long mPhotoId;
    int mPhotoCount;
    int mPhotoNth;
    Photo mPhoto;
    DatabaseHelper mDatabase;
    ScrollView mScrollView;
    ImageView mImageView;
    TextView mDateView;
    EditText mDescriptionView;
    ImageButton mEditButton;
    TextView mPlaceNameView;
    TextView mPhotosCountView;
    InputMethodManager mImm;
    boolean edit = false;
    protected void init(){
        setContentView(R.layout.gallery_photo);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);
        mDatabase = new DatabaseHelper(this);
        mPhotoId = (Long) getIntent().getSerializableExtra("photoId");
        mPhotoCount = getIntent().getIntExtra("photoCount",0);
        mPhotoNth = getIntent().getIntExtra("photoNth",0);
        mDateView = (TextView) findViewById(R.id.date);
        mImageView = (ImageView) findViewById(R.id.full_size_photo);
        mDescriptionView = (EditText) findViewById(R.id.description);
        mEditButton = (ImageButton) findViewById(R.id.edit_button);
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.hideSoftInputFromWindow(mDescriptionView.getWindowToken(),0);
        initActionBar();
    }
    protected void initActionBar(){
        try {
            mPhoto = mDatabase.selectPhoto(mPhotoId);
        }catch (Exception e) {
            e.printStackTrace();
        }
        edit = false;
        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.transparent));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomActionBar = mInflater.inflate(R.layout.full_photo_actionbar,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.photoBack);
        mPlaceNameView = (TextView) mCustomActionBar.findViewById(R.id.name);
        mPhotosCountView = (TextView) mCustomActionBar.findViewById(R.id.photoCount);
        String str = mPhoto.getPlaceName();
        if(str.length()>20)
            str = str.substring(0,20)+"...";
        mPlaceNameView.setText(str);
        mPhotosCountView.setText(mPhotoNth+" of "+mPhotoCount+" photos");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("back button clicked", "");
                finish();
            }
        });
        mDescriptionView.setText(mPhoto.getDescription());
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);

    }
    protected void initActionBarEdit(){
        edit = true;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        TextView doneButton = (TextView) mCustomActionBar.findViewById(R.id.actionBarDone);
        titleView.setText("Edit Photo");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressedEditDone();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressedEditDone();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        Glide.with(this).load(mPhoto.getPhotoPath())
                .into(mImageView);
        mScrollView.smoothScrollTo(mScrollView.getScrollX(),mScrollView.getLayoutParams().height);
        mDateView.setText(HasBeenDate.convertDate(mPhoto.getTakenDate()));
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDescriptionView.setFocusable(true);
                mDescriptionView.setEnabled(true);
                mDescriptionView.setFocusableInTouchMode(true);
                mDescriptionView.setClickable(true);
                mDescriptionView.setSelected(true);
                mDescriptionView.requestFocus();
                mEditButton.setVisibility(View.INVISIBLE);
                mImm.showSoftInput(mDescriptionView,InputMethodManager.SHOW_FORCED);
                initActionBarEdit();
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                // do something here
                if(edit) {
                    pressedEditDone();
                }else
                    finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void pressedEditDone(){
        String text = mDescriptionView.getText().toString();
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
        mPhoto.setDescription(text);
        try {
            mDatabase.updatePhoto(mPhoto);
        }catch (Exception e) {
            e.printStackTrace();
        }
        initActionBar();
        mDescriptionView.setFocusable(false);
        mDescriptionView.setEnabled(false);
        mDescriptionView.setFocusableInTouchMode(false);
        mDescriptionView.setClickable(false);
        mDescriptionView.setSelected(false);
        mEditButton.setVisibility(View.VISIBLE);

    }
}
