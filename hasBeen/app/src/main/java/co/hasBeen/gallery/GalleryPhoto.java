package co.hasBeen.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.model.api.Photo;
import co.hasBeen.utils.HasBeenDate;

/**
 * Created by zuby on 2015-01-16.
 */
public class GalleryPhoto extends Activity {
    final static int REQUEST_CODE = 2000;
    Long mPhotoId;
    Photo mPhoto;
    DatabaseHelper database;
    View mActionBar;
    View mFilter;
    View mDescription;
    boolean toogle = true; // true : actionbar,description show , false : hide

    protected void init() throws Exception {
        setContentView(R.layout.gallery_photo);
        ImageView photo = (ImageView) findViewById(R.id.photo);
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
        mActionBar = findViewById(R.id.actionBarLayout);
//        mActionBar.setBackgroundColor(getResources().getColor(R.color.transparent));
        TextView placeName = (TextView) mActionBar.findViewById(R.id.actionBarTitle);
        placeName.setText(database.selectPlaceNameByPlaceId(mPhoto.getPlaceId()));
        ImageButton back = (ImageButton) mActionBar.findViewById(R.id.actionBarBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("back button clicked", "");
                finish();
            }
        });
        ImageView moreVert = (ImageView) mActionBar.findViewById(R.id.moreVert);
    }

    protected void initDescription() throws Exception {
        mFilter = findViewById(R.id.filter);
        mDescription = findViewById(R.id.descriptionLayout);
        TextView date = (TextView) findViewById(R.id.date);
        TextView description = (TextView) findViewById(R.id.description);
        View moreDescription = findViewById(R.id.moreDescription);
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
                Intent intent = new Intent(getBaseContext(), GalleryPhotoEdit.class);
                intent.putExtra("id", mPhotoId);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Photo photo = database.selectPhoto(mPhotoId);
                mPhoto.setDescription(photo.getDescription());
                TextView description = (TextView) findViewById(R.id.description);
                description.setText(photo.getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoId = getIntent().getLongExtra("id", 0);
        try {
            database = new DatabaseHelper(this);
            mPhoto = database.selectPhoto(mPhotoId);
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}