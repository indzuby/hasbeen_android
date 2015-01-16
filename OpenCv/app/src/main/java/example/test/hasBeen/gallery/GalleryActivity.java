package example.test.hasBeen.gallery;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.geolocation.GeoGoogle;
import example.test.hasBeen.model.HasBeenPhoto;


public class GalleryActivity extends ActionBarActivity {

    List<HasBeenPhoto> mPhotoList;
    List<HasBeenPhoto> mPhotoByDB;
    LinearLayout line;
    GalleryAdapter galleryAdapter;
    ContentResolver resolver;
    GridView gallery;
    //    DBHelper db;
    DatabaseHelper database;
    GeoGoogle geo;
    boolean flag;

    protected void init() {
        gallery = (GridView) findViewById(R.id.galleryL2GridView);
        line = (LinearLayout) findViewById(R.id.galleryL2Line);
        database = new DatabaseHelper(this);
        resolver = getContentResolver();
        mPhotoList = new ArrayList<>();
        try {
//            mPhotoByDB = database.selectAllPhoto();
//            mPhotoList = database.selectAllPhoto();
            mPhotoList = database.selectPhotoClearestPhoto();
        } catch (Exception e) {
            e.printStackTrace();
        }

        galleryAdapter = new GalleryAdapter(getBaseContext(), mPhotoList);
        gallery.setAdapter(galleryAdapter);
        geo = new GeoGoogle(this);
        flag = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_level_2_item);
        init();
    }


}
