package example.test.hasBeen.gallery;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import example.test.hasBeen.ImageLoader;
import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.geolocation.GeoFourSquare;
import example.test.hasBeen.geolocation.GeoGoogle;
import example.test.hasBeen.model.HasBeenDay;
import example.test.hasBeen.model.HasBeenPhoto;
import example.test.hasBeen.model.HasBeenPlace;
import example.test.hasBeen.model.HasBeenPosition;


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
    List<HasBeenPhoto> mPhotoList;
    GalleryAdapter galleryAdapter;
    ContentResolver resolver;
    ImageLoader imageLoader;
    GridView gallery;
    Cursor cursor;
    Thread photoThread;
//    DBHelper db;
    DatabaseHelper database;
    GeoGoogle geo;
    boolean flag;
    protected void init() {
        gallery = (GridView) findViewById(R.id.Grid_gallery);
        mImagePath = new ArrayList<GalleryAdapter.PhotoData>();
        resolver = getContentResolver();
        galleryAdapter = new GalleryAdapter(getBaseContext(), mImagePath, imageLoader);
        gallery.setAdapter(galleryAdapter);
//        db = new DBHelper(this);
        database = new DatabaseHelper(this);
        geo = new GeoGoogle(this);
        mPhotoList = new ArrayList();
        flag = true;
    }

    protected Bitmap getThumbnail(int id) {
        return MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
    }

    protected void takePhoto() {
        String[] proj = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE
        };
        final int[] idx = new int[proj.length];
        cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < idx.length; i++)
                idx[i] = cursor.getColumnIndex(proj[i]);

            photoThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    GalleryAdapter.PhotoData goodPhoto;
                    int maxEdge = -1;
                    do {
                        int photoID = cursor.getInt(idx[0]);
                        String photoPath = cursor.getString(idx[1]);
                        String displayName = cursor.getString(idx[2]);
                        long dataTaken = cursor.getLong(idx[3]);
                        String format = cursor.getString(idx[4]);
                        float lat = cursor.getFloat(idx[5]);
                        float lon = cursor.getFloat(idx[6]);
                        if(lat==0 && lon==0){
                            continue;
                            // position : others
                        }


                        if (!format.endsWith("jpg") && !format.endsWith("jpeg")) continue;

                        if (displayName != null) {
                            GalleryAdapter.PhotoData photo = new GalleryAdapter.PhotoData(photoID, photoPath, dataTaken);
                            if (mImagePath.size() > 0) {

                                GalleryAdapter.PhotoData beforePhoto = mImagePath.get(mImagePath.size() - 1);
                                if (Math.abs(beforePhoto.photoTaken - dataTaken) <= 5000) continue;

                                Bitmap beforePhotoMap = getThumbnail(beforePhoto.photoID);
                                Bitmap currentPhotoMap = getThumbnail(photoID);

                                double hist = compareHistogram(beforePhotoMap, currentPhotoMap);
//                                Log.i("Photo histogram", hist + "");
                                if (hist > 0.8) {
                                    if (maxEdge == -1)
                                        maxEdge = detectEdge(beforePhotoMap);
                                    int edge = detectEdge(currentPhotoMap);
                                    if (maxEdge < edge) {
                                        maxEdge = edge;
                                        goodPhoto = new GalleryAdapter.PhotoData(photoID, photoPath, dataTaken);
                                    }
                                    continue;
                                }
                                goodPhoto = photo;
                            } else
                                goodPhoto = photo;
                            maxEdge = -1;
                            mImagePath.add(goodPhoto);
                            if(lat==0 && lon==0){
                                // position : others
                            }else {
                                HasBeenPhoto dbphoto = new HasBeenPhoto("","","","","",lat,lon,getDate(dataTaken),new Long(0),new Long(0));
                                mPhotoList.add(dbphoto);
//                                HasBeenPhoto(int id,String title, String description, String country, String city, String place_name, float lat, float lon, String taken_date,int day_id, int position_id)
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    galleryAdapter.notifyDataSetChanged();
                                }
                            });
//                            Log.i("Photo Path", displayName);
                        }
                    }
                    while (cursor.moveToNext() && flag);
                    Collections.sort(mPhotoList,new Comparator<HasBeenPhoto>() {
                        @Override
                        public int compare(HasBeenPhoto lhs, HasBeenPhoto rhs) {
                            if(lhs.getTakenDate().getTime() > rhs.getTakenDate().getTime())
                                return 1;
                            return -1;
                        }
                    });
                    Log.i("first photo",mPhotoList.get(0).getTakenDate().toGMTString());
                    setGeoData();
                }
            });
            photoThread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        flag = false;
    }
    @Override
    protected void onResume(){
        super.onResume();
        flag=true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        init();
        takePhoto();
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
    protected Date getDate(long taken_date){
        return new Date(taken_date);

    }
    protected void insertDB(){
        Iterator iterator = mPhotoList.iterator();
        try {
            HasBeenPhoto lastPhoto = database.getLastPhoto();
            List<HasBeenPhoto> photoList = new ArrayList<>();
            if(lastPhoto!=null)
                Log.i("lastPhoto",lastPhoto.getTakenDate().toString());
            while (iterator.hasNext()) {
                HasBeenPhoto photo = (HasBeenPhoto) iterator.next();
                if(isBeforeThatDate(lastPhoto, photo))
                    continue;
                database.insertPhoto(photo);
                if(photo.getPlaceName().length()>0 ) {
                    if(!database.hasVenueId(photo.getVenueId())) {
                        HasBeenPlace place = new HasBeenPlace(photo.getVenueId(), photo.getCategoryId(), photo.getCategryName(), photo.getCountry(), photo.getCity(), photo.getPlaceName(), photo.getLat(), photo.getLon(), photo.getCategoryIconPrefix(), photo.getCategoryIconSuffix());
                        Long place_id = database.insertPlace(place);
                        photo.setPlaceId(place_id);
                        database.updatePhoto(photo);
                    }else
                        photo.setPlaceId(database.getPlaceIdByVenueId(photo.getVenueId()));

                }
                if(photoList.size()==0) {
                    photoList.add(photo);
                }
                else {
                    HasBeenPhoto beforePhoto = photoList.get(0);
                    if (!isSameThatDate(beforePhoto, photo)) {
                        HasBeenDay day = new HasBeenDay("","",photoList.size(),beforePhoto.getTakenDate(),beforePhoto.getCountry(),beforePhoto.getCity(),beforePhoto.getId());
                        Long dayId = database.insertDay(day);
                        updateDayId(dayId,photoList);
                        photoList = new ArrayList<>();
                        photoList.add(photo);
                    } else {
                        photoList.add(photo);
                    }
                }
                if(!iterator.hasNext()) {
                    HasBeenPhoto beforePhoto = photoList.get(0);
                    HasBeenDay day = new HasBeenDay("","",photoList.size(),beforePhoto.getTakenDate(),beforePhoto.getCountry(),beforePhoto.getCity(),beforePhoto.getId());
                    Long dayId = database.insertDay(day);
                    updateDayId(dayId,photoList);
                }
            }
            Log.i("Day id",database.getLastPhoto().getDayId()+"");
            Log.i("photo_id",database.getLastPhoto().getId()+"");
//            Log.i("place cnt",database.getPlaceDao().countOf()+"");
//            Log.i("day cnt",database.getDayDao().countOf()+"");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    protected  void updateDayId(Long dayId, List photoList) {
        List<HasBeenPhoto> positions = new ArrayList<>();
        Iterator iter = photoList.iterator();
        while(iter.hasNext()) {
            HasBeenPhoto photo = (HasBeenPhoto) iter.next();
            photo.setDayId(dayId);
            if(photo.getPlaceId()==null) {
                if(positions.size()>0) {
                   insertPosition(positions);
                }
                insertPosition(photo);
                positions = new ArrayList<>();
            }else {
                positions.add(photo);
                if(!iter.hasNext())
                    insertPosition(positions);
            }
            try {
                database.updatePhoto(photo);
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
//    public HasBeenPosition(Long day_id, String type, Date startDate, Date endDate,Long mainPhotoId, Long placeId, String categoryIconPrefix, String categoryIconSuffix) {
    protected void insertPosition(List<HasBeenPhoto> positions) {
        HasBeenPhoto photo = (HasBeenPhoto) positions.get(0);

        HasBeenPosition position = new HasBeenPosition(
                photo.getDayId(),
                "Place",
                photo.getTakenDate(),
                positions.get(positions.size()-1).getTakenDate(),
                photo.getId(),
                photo.getPlaceId(),
                photo.getCategoryIconPrefix(),
                photo.getCategoryIconSuffix());
        try {
            Long id = database.insertPosition(position);
            Log.i("Position id",id+"");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    protected void insertPosition(HasBeenPhoto photo) {
        HasBeenPosition position = new HasBeenPosition(
                photo.getDayId(),
                "",
                photo.getTakenDate(),
                photo.getTakenDate(),
                photo.getId(),
                null,
                null,
                null);
        try {
            Long id = database.insertPosition(position);
            Log.i("Position id",id+"");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected boolean isBeforeThatDate(HasBeenPhoto lastPhoto , HasBeenPhoto currentPhoto){
        if(lastPhoto==null) return false;
//        Log.i("photo time",lastPhoto.getTakenDate().getTime()+"");
//        Log.i("photo time",currentPhoto.getTakenDate().getTime()+"");
        if(currentPhoto.getTakenDate().getTime() - lastPhoto.getTakenDate().getTime() > 1000) return false;
        return true;
    }
    protected boolean isSameThatDate(HasBeenPhoto beforePhoto, HasBeenPhoto currentPhoto) {
        Log.i("beforePhoto",beforePhoto.getTakenDate().toString());
        Log.i("currentPhoto",currentPhoto.getTakenDate().toString());
        if(currentPhoto.getTakenDate().getYear() == beforePhoto.getTakenDate().getYear() &&
                currentPhoto.getTakenDate().getMonth() == beforePhoto.getTakenDate().getMonth() &&
                currentPhoto.getTakenDate().getDate() == beforePhoto.getTakenDate().getDate()) return true;
        return false;
    }
    protected void setGeoData(){
        Iterator iterator = mPhotoList.iterator();
        while(iterator.hasNext()) {
            HasBeenPhoto photo = (HasBeenPhoto) iterator.next();
            if(photo.getLat()==0 && photo.getLon()==0) {

            }else {
                photo.setCity(geo.getCity(photo.getLat(), photo.getLon()));
                photo.setCountry(geo.getCountry(photo.getLat(),photo.getLon()));
                new GeoFourSquare(handler).execute(photo.getLat(),photo.getLon(),photo);
            }
        }

    }
    private Handler handler = new Handler() {
        int cnt=0;
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case -1:
                    // 에러 처리
                    break;
                case 0:
                    // 정상 응답 처리
//                    Log.i("Call back",msg.obj+"");
                    cnt++;
                    if(cnt == mPhotoList.size()) {
                        insertDB();
                    }
                    break;
            }
        }
    };
}
