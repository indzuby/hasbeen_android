package example.test.opencv;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class GalleryActivity extends ActionBarActivity {

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
            System.loadLibrary("opencv_java");
            System.loadLibrary("opencv_info");
            //System.loadLibrary("opencv_core");
        }
    }

    List<GalleryAdapter.PhotoData> mImagePath;
    GalleryAdapter galleryAdapter;
    int beforeScroll=0;
    int viewCount = 0;
    ImageLoader imageLoader;
    GridView gallery;
    String[] proj = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
    };
    int[] idx = new int[proj.length];
    Bitmap fromImg,toImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        gallery = (GridView) findViewById(R.id.Grid_gallery);
        mImagePath = new ArrayList<GalleryAdapter.PhotoData>();
        ContentResolver resolver = getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj);
        if (cursor != null && cursor.moveToFirst()){
            idx[0] = cursor.getColumnIndex(proj[0]);
            idx[1] = cursor.getColumnIndex(proj[1]);
            idx[2] = cursor.getColumnIndex(proj[2]);
            idx[3] = cursor.getColumnIndex(proj[3]);
            do {

                int photoID = cursor.getInt(idx[0]);
                String photoPath = cursor.getString(idx[1]);
                String displayName = cursor.getString(idx[2]);
                int dataTaken = cursor.getInt(idx[3]);
                if( displayName != null ) {

                    GalleryAdapter.PhotoData photo = new GalleryAdapter.PhotoData(photoID,photoPath,dataTaken);
                    if(mImagePath.size()>0) {
                        GalleryAdapter.PhotoData beforePhoto = mImagePath.get(mImagePath.size() - 1);
                        Log.i("Photo Taken", Math.abs(beforePhoto.photoTaken - dataTaken) + "");
                        if( Math.abs(beforePhoto.photoTaken - dataTaken)<=5000) continue;
//                        Mat fromMat = new Mat();
//                        Mat toMat = new Mat();
//                        fromImg = MediaStore.Images.Thumbnails.getThumbnail(resolver, beforePhoto.photoID,MediaStore.Images.Thumbnails.MICRO_KIND, null);
////                        Utils.bitmapToMat(fromImg,fromMat);
//                        toImg = MediaStore.Images.Thumbnails.getThumbnail(resolver, photoID,MediaStore.Images.Thumbnails.MICRO_KIND, null);
////                        Utils.bitmapToMat(toImg,fromMat);
//                        fromMat = Highgui.imread(beforePhoto.photoPath,1);
//                        toMat = Highgui.imread(photoPath,1);
//                        double hist = compareHistogram(fromMat,toMat);
//                        if(hist<80)
                            mImagePath.add(photo);
                    }
                    else
                        mImagePath.add(photo);
                    Log.i("Photo Path",displayName);
                }
            }
            while( cursor.moveToNext() );
        }
//        Collections.reverse(mImagePath);



        imageLoader = new ImageLoader(resolver);
        galleryAdapter = new GalleryAdapter(getBaseContext(),mImagePath,imageLoader);
//        galleryAdapter = new GalleryAdapter(getBaseContext(),mImagePath);
        gallery.setAdapter(galleryAdapter);
        imageLoader.setListener(galleryAdapter);
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
}
