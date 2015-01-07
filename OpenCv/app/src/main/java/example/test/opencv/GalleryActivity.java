package example.test.opencv;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
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
    int beforeScroll = 0;
    int viewCount = 0;
    ImageLoader imageLoader;
    GridView gallery;
    String[] proj = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.MIME_TYPE
    };
    int[] idx = new int[proj.length];
    Bitmap fromImg, toImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        gallery = (GridView) findViewById(R.id.Grid_gallery);
        mImagePath = new ArrayList<GalleryAdapter.PhotoData>();
        final ContentResolver resolver = getContentResolver();
        final Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,null, MediaStore.MediaColumns.DATE_ADDED+" desc");
        galleryAdapter = new GalleryAdapter(getBaseContext(), mImagePath, imageLoader);
        gallery.setAdapter(galleryAdapter);
        if (cursor != null && cursor.moveToFirst()) {

            idx[0] = cursor.getColumnIndex(proj[0]);
            idx[1] = cursor.getColumnIndex(proj[1]);
            idx[2] = cursor.getColumnIndex(proj[2]);
            idx[3] = cursor.getColumnIndex(proj[3]);
            idx[4] = cursor.getColumnIndex(proj[4]);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GalleryAdapter.PhotoData goodPhoto;
                    int maxEdge=-1;
                    do {
                        int photoID = cursor.getInt(idx[0]);
                        String photoPath = cursor.getString(idx[1]);
                        String displayName = cursor.getString(idx[2]);
                        int dataTaken = cursor.getInt(idx[3]);
                        String format = cursor.getString(idx[4]);
                        Log.i("Image format",format);
                        if(!format.endsWith("jpg") && !format.endsWith("jpeg")) continue;
                        if (displayName != null) {
                            GalleryAdapter.PhotoData photo = new GalleryAdapter.PhotoData(photoID, photoPath, dataTaken);
                            if (mImagePath.size() > 0) {
                                GalleryAdapter.PhotoData beforePhoto = mImagePath.get(mImagePath.size() - 1);

                                if (Math.abs(beforePhoto.photoTaken - dataTaken) <= 5000) continue;
                                double hist = compareHistogram(
                                        MediaStore.Images.Thumbnails.getThumbnail(resolver, beforePhoto.photoID, MediaStore.Images.Thumbnails.MICRO_KIND, null),
                                        MediaStore.Images.Thumbnails.getThumbnail(resolver, photoID, MediaStore.Images.Thumbnails.MICRO_KIND, null));
//                                Log.i("Photo histogram", hist + "");
                                if (hist > 0.8) {
                                    if(maxEdge==-1)
                                        maxEdge = detectEdge(MediaStore.Images.Thumbnails.getThumbnail(resolver, beforePhoto.photoID, MediaStore.Images.Thumbnails.MICRO_KIND, null));
                                    int edge = detectEdge(MediaStore.Images.Thumbnails.getThumbnail(resolver, photoID, MediaStore.Images.Thumbnails.MICRO_KIND, null));
                                    if(maxEdge<edge) {
                                        maxEdge = edge;
                                        goodPhoto = new GalleryAdapter.PhotoData(photoID,photoPath,dataTaken);
                                    }
                                    continue;
                                }
                                goodPhoto = photo;
                            }else
                                goodPhoto = photo;
                            maxEdge = -1;
                            mImagePath.add(goodPhoto);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    galleryAdapter.notifyDataSetChanged();
                                }
                            });
//                            Log.i("Photo Path", displayName);
                        }
                    }
                    while (cursor.moveToNext());
                }
            }).start();
        }
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

    protected int detectEdge(Bitmap img) {
        int threshold = 100;
        Mat srcGray = new Mat();
        Mat hierarchy = new Mat();
        List<MatOfPoint> countours = new ArrayList<>();
        Mat src = new Mat();
        Utils.bitmapToMat(img, src);
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(srcGray, srcGray, new Size(3, 3));
        Mat cannyOutput = new Mat();
        Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2, 3, true);
        Imgproc.findContours(cannyOutput, countours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        int edgeCnt = countours.size();
        return edgeCnt;
    }

    protected double compareHistogram(Bitmap fromImg, Bitmap toImg) {
        Mat hsvFrom = new Mat(fromImg.getWidth(), fromImg.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(fromImg, hsvFrom);
        Mat hsvTo = new Mat(toImg.getWidth(), toImg.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(toImg, hsvTo);

        Imgproc.cvtColor(hsvFrom, hsvFrom, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(hsvTo, hsvTo, Imgproc.COLOR_BGR2HSV);
        MatOfInt histSize = new MatOfInt(25);
        MatOfFloat ranges = new MatOfFloat(0f, 256f);

        Mat histFrom = new Mat();
        Mat histTo = new Mat();

        Imgproc.calcHist(Arrays.asList(hsvFrom), new MatOfInt(0), new Mat(), histFrom, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(hsvTo), new MatOfInt(0), new Mat(), histTo, histSize, ranges);

        return Imgproc.compareHist(histFrom, histTo, Imgproc.CV_COMP_CORREL);
    }
}
