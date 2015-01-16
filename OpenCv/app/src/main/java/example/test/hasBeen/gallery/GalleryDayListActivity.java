package example.test.hasBeen.gallery;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.opencv.android.OpenCVLoader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.geolocation.GeoFourSquare;
import example.test.hasBeen.geolocation.GeoGoogle;
import example.test.hasBeen.model.HasBeenPlace;
import example.test.hasBeen.model.HasBeenPosition;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.model.HasBeenDay;
import example.test.hasBeen.model.HasBeenPhoto;
import example.test.hasBeen.utils.HasBeenOpenCv;

/**
 * Created by zuby on 2015-01-14.
 */
public class GalleryDayListActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    private ClusterManager<MyItem> mClusterManager;
    private static final String TAG = "Map Activity";
    GeoGoogle geo;
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

    }
    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = geo.getLocation().latitude;
        double lng = geo.getLocation().longitude;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng);
            mClusterManager.addItem(offsetItem);
        }
    }
    public void  displayRoute(LatLng from,LatLng to) {
        Polyline line = mMap.addPolyline(new PolylineOptions().add(from, to));
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        UiSettings setting = map.getUiSettings();
        mClusterManager = new ClusterManager<MyItem>(this,map);
        setting.setZoomControlsEnabled(true);
        setting.setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(geo.getLocation(), 13));
        map.setOnCameraChangeListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(TAG,"Marker Selectd");
                if(mMap.getCameraPosition().zoom>=8) {
                    Toast.makeText(getBaseContext(), "Max Zoom", Toast.LENGTH_LONG).show();
                }else
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom+1),500,null);
                return false;
            }
        });
        Toast.makeText(this,GeoGoogle.getCity(this,geo.getLocation().latitude,geo.getLocation().longitude),Toast.LENGTH_LONG).show();;
        addItems();
        displayRoute(geo.getLocation(),new LatLng(geo.getLocation().latitude+0.03,geo.getLocation().longitude+0.03));
    }

    static {

        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
            System.loadLibrary("opencv_java");
            System.loadLibrary("opencv_info");
            //System.loadLibrary("opencv_core");
        }
    }
    final static int SUCCESS = 0;
    final static int FAILED = - 1;
    List<HasBeenPhoto> mPhotoByDB;
    ListView listView;
    List<HasBeenDay> mGalleryList;
    GalleryDayAdapter listAdapter;
    DatabaseHelper database;
    ContentResolver resolver;
    boolean flag = true;
    Cursor cursor;
    List<HasBeenPhoto> mPhotoList;
    ProgressDialog dialog;

    HasBeenPhoto lastPhoto ;
    protected void init() throws Exception{
        setContentView(R.layout.gallery_level_1);
        listView = (ListView) findViewById(R.id.galleryL1ListView);
        mGalleryList = new ArrayList<>();
        listAdapter = new GalleryDayAdapter(this, mGalleryList);
        listView.setAdapter(listAdapter);
        database = new DatabaseHelper(this);
        resolver = this.getContentResolver();
        mPhotoList = new ArrayList<>();
        lastPhoto = database.getLastPhoto();
        if(lastPhoto!=null)
            Log.i("last photo",lastPhoto.getPlaceName());
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.view_map);
        mapFragment.getMapAsync(this);
        geo = new GeoGoogle(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            init();
            showProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        takePhoto();
                        buildDay();
//                        mGalleryList = database.selectBeforeFiveDay();
                        Log.i("databse cnt", mGalleryList.size() + "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Iterator iterator = database.selectBeforeFiveDay().iterator();
                                    while (iterator.hasNext()) {
                                        mGalleryList.add((HasBeenDay) iterator.next());
                                        listAdapter.notifyDataSetChanged();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
//                                listAdapter = new GalleryDayAdapter(getBaseContext(),mGalleryList);
//                                listView.setAdapter(listAdapter);
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected  void showProgress(){
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setMessage("Writting on database");
        dialog.setProgress(100);
        dialog.show();
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
        if (lastPhoto != null)
            cursor = MediaStore.Images.Media.query(resolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj,
                    MediaStore.Images.Media.DATE_TAKEN + ">?",
                    new String[]{"" + lastPhoto.getTakenDate().getTime()},
                    MediaStore.MediaColumns.DATE_ADDED);
        else
            cursor = MediaStore.Images.Media.query(resolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj,
                    null,
                    MediaStore.MediaColumns.DATE_ADDED);

        Log.i("Images count", cursor.getCount() + "");
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < idx.length; i++)
                idx[i] = cursor.getColumnIndex(proj[i]);

            do {

                long photoID = cursor.getInt(idx[0]);
                String photoPath = cursor.getString(idx[1]);
                String displayName = cursor.getString(idx[2]);
                long dataTaken = cursor.getLong(idx[3]);
                String format = cursor.getString(idx[4]);
                float lat = cursor.getFloat(idx[5]);
                float lon = cursor.getFloat(idx[6]);
                if (lat == 0 && lon == 0) continue;
                try {
                    if (database.hasPhotoId(photoID)) continue;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("photo name",displayName);
                if (isNotJpg(format)) continue;

                if (displayName != null) {
                    HasBeenPhoto photo = new HasBeenPhoto();
//                            "", "", "", "", "", lat, lon, getDate(dataTaken), new Long(0), null, new Long(photoID), photoPath,null
                    photo.setTitle("");
                    photo.setCity("");
                    photo.setCountry("");
                    photo.setDescription("");
                    photo.setPlaceName("");
                    photo.setLat(lat);
                    photo.setLon(lon);
                    photo.setTakenDate(HasBeenDate.getDate(dataTaken));
                    photo.setDayId(new Long(0));
                    photo.setPhotoId(new Long(photoID));
                    photo.setPhotoPath(photoPath);

                    mPhotoList.add(photo);
//                    Log.i("Photo Date", photo.getTakenDate().toString());
                }

            } while (cursor.moveToNext());
            insertDB();
        }
    }
    public boolean isNotJpg(String format){
        if (!format.endsWith("jpg") && !format.endsWith("jpeg")) return true;
        return false;
    }
    public void insertDB(){
        Log.i("photo list size",mPhotoList.size()+"");
        Iterator iterator = mPhotoList.iterator();
        HasBeenDay day;
        try {
            HasBeenPhoto beforePhoto=database.getLastPhoto();
            while(iterator.hasNext()) {
                HasBeenPhoto photo = (HasBeenPhoto) iterator.next();
                Long id = database.insertPhoto(photo);
                photo.setClearestId(id);
                if(beforePhoto!=null && HasBeenDate.isSameDate(beforePhoto,photo) ) {
                    photo.setDayId(beforePhoto.getDayId());
                    database.increasePhotoCount(beforePhoto.getDayId());
                }else {
                    day = new HasBeenDay();
                    day.setTitle("");
                    day.setDescription("");
                    day.setPhotoCount(1);
                    day.setDate(photo.getTakenDate());
                    day.setCountry("");
                    day.setCity("");
                    Long dayId = database.insertDay(day);
                    photo.setDayId(dayId);
                    beforePhoto = photo;
                    Log.i("Day id",dayId+"");
                }
                database.updatePhoto(photo);
//                Log.i("Photo Date",photo.getTakenDate().toString());
            }
        }catch (Exception e) {
            e.printStackTrace();
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
    HasBeenPhoto bestPhoto = null;
    ArrayList<HasBeenPhoto> photos = null;
    protected void buildDay() {
        try {
            mPhotoByDB = database.selectAllPhoto();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterator iterator = mPhotoByDB.iterator();
        try {
            HasBeenPhoto lastPhoto = database.getLastPhoto();
            bestPhoto = null;
            photos = new ArrayList<HasBeenPhoto>();
            flag = true;
            while (iterator.hasNext()) {
                if (!flag) continue;
                final HasBeenPhoto photo = (HasBeenPhoto) iterator.next();
                Log.i("photo city - clearest_id",photo.getPlaceName()+" - "+photo.getClearestId());
                if (photo.getEdgeCount() != null){
                    if(photo.getId() == photo.getClearestId())
                        bestPhoto = photo;
                    continue;
                }
                if (true || HasBeenDate.isDateRangeInThree(photo, lastPhoto)) {
                    Integer edge = photo.getEdgeCount();
                    if (edge == null) {
                        edge = HasBeenOpenCv.detectEdge(getThumbnail(photo.getPhotoId()));
                        photo.setEdgeCount(edge);
//                        database.updatePhoto(photo);
                    }
                    if (bestPhoto != null && HasBeenDate.isSameDate(bestPhoto, photo)) {
                        if (isSimilary(bestPhoto, photo)) {
                            if (isMaxEdge(edge, bestPhoto)) {
                                photos.add(bestPhoto);
                                photo.setPlaceName(bestPhoto.getPlaceName());
                                photo.setPositionId(bestPhoto.getPositionId());
                                photo.setPlaceId(bestPhoto.getPlaceId());
                                photo.setFourSquare(bestPhoto.getVenueId(),bestPhoto.getCategoryId(),bestPhoto.getCategryName(),bestPhoto.getCategoryIconPrefix(),bestPhoto.getCategoryIconSuffix());
                                bestPhoto = photo;
                                photos.add(photo);
                            } else {
                                photos.add(photo);
                            }
                        } else {
                            updateClearestId(photos, bestPhoto);
                            photos = new ArrayList<HasBeenPhoto>();
                            flag = false;
                            new GeoFourSquare(new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(Message msg) {
                                    try {
                                        switch (msg.what) {
                                            case FAILED: // failed

                                                break;
                                            case SUCCESS: // success
                                                HasBeenPhoto updatedPhoto = (HasBeenPhoto) msg.obj;
//                                                Log.i("place_name",updatedPhoto.getPlaceName());
                                                if (isSamePlace(updatedPhoto, bestPhoto)) {
                                                    updatedPhoto.setPlaceId(bestPhoto.getPlaceId());
                                                    updatedPhoto.setPositionId(bestPhoto.getPositionId());
                                                    database.updatePhoto(updatedPhoto);
                                                    database.updatePositionEndTime(updatedPhoto.getPositionId(), updatedPhoto.getTakenDate());
                                                    Log.i("same place",updatedPhoto.getId()+"");
                                                } else {
                                                    insertNewPosition(updatedPhoto);
                                                }
                                                bestPhoto = updatedPhoto;
                                                photos.add(updatedPhoto);
                                                break;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    flag = true;
                                }
                            }).execute(photo.getLat(), photo.getLon(), photo);

                        }
                    } else {
                        flag= false;
                        Log.i("photos size",photos.size()+"");
                        if(photos.size()>0) Log.i("BestPhoto ida",bestPhoto.getId()+"");
                        updateClearestId(photos, bestPhoto);
                        photos = new ArrayList<HasBeenPhoto>();
                        new GeoFourSquare(new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                try {
                                    switch (msg.what) {
                                        case FAILED: // failed
                                            break;
                                        case SUCCESS: // success
                                            HasBeenPhoto updatedPhoto = (HasBeenPhoto) msg.obj;
                                            insertNewPosition(updatedPhoto);
                                            bestPhoto = updatedPhoto;
                                            photos.add(updatedPhoto);
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                flag = true;
                            }
                        }).execute(photo.getLat(), photo.getLon(), photo);
                    }
                }
            }
            updateClearestId(photos, bestPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //    public HasBeenPlace(String venue_id, String category_id, String category_name, String country, String city, String name, float lat, float lon, String categoryIconPrefix,String categoryIconSuffix) {
    protected void insertNewPosition(HasBeenPhoto photo) throws SQLException {
        HasBeenPlace place = new HasBeenPlace();
        HasBeenPosition position = new HasBeenPosition();
        Long placeId;
        Long positionId;
        if (photo.getPlaceId() != null && database.hasVenueId(photo.getVenueId())) {
            placeId = database.getPlaceIdByVenueId(photo.getVenueId());
        } else {
            place.setVenueId(photo.getVenueId());
            place.setCategoryId(photo.getCategoryId());
            place.setCategoryName(photo.getCategryName());
            place.setCountry(photo.getCountry());
            place.setCity(photo.getCity());
            place.setName(photo.getPlaceName());
            place.setLat(photo.getLat());
            place.setLon(photo.getLon());
            place.setCategoryIconPrefix(photo.getCategoryIconPrefix());
            place.setCategoryIconSuffix(photo.getCategoryIconSuffix());
            place.setMainPhotoId(photo.getId());
            placeId = database.insertPlace(place);
        }
        position.setDayId(photo.getDayId());
        position.setStartDate(photo.getTakenDate());
        position.setEndDate(photo.getTakenDate());
        position.setMainPhotoId(photo.getId());
        position.setType("Place");
        position.setPlaceId(placeId);
        position.setCategoryIconPrefix(photo.getCategoryIconPrefix());
        position.setCategoryIconSuffix(photo.getCategoryIconSuffix());
        positionId = database.insertPosition(position);

        photo.setPlaceId(placeId);
        photo.setPositionId(positionId);
        database.updatePhoto(photo);
    }
    protected boolean isSamePlace(HasBeenPhoto aPhoto , HasBeenPhoto bPhoto){
        Log.i("is same?",aPhoto.getPlaceName()+"=="+bPhoto.getPlaceName());
        if(aPhoto.getVenueId().equals(bPhoto.getVenueId()))
            return true;
        return false;
    }
    protected  void updateClearestId(List<HasBeenPhoto> photos,HasBeenPhoto bestPhoto) throws SQLException{
        if (photos.size() > 0) {
            Iterator photosIter = photos.iterator();
            while (photosIter.hasNext()) {
                HasBeenPhoto badPhoto = (HasBeenPhoto) photosIter.next();

                badPhoto.setClearestId(bestPhoto.getId());
                badPhoto.setPlaceName(bestPhoto.getPlaceName());
                badPhoto.setPositionId(bestPhoto.getPositionId());
                badPhoto.setPlaceId(bestPhoto.getPlaceId());
                badPhoto.setCity(bestPhoto.getCity());
                badPhoto.setCountry(bestPhoto.getCountry());
                badPhoto.setFourSquare(bestPhoto.getVenueId(),bestPhoto.getCategoryId(),bestPhoto.getCategryName(),bestPhoto.getCategoryIconPrefix(),bestPhoto.getCategoryIconSuffix());
                database.updatePhoto(badPhoto);
            }
            if(database.hasMainPhotoIdInDay(bestPhoto.getDayId()))
                database.updateDayMainPhotoId(bestPhoto.getDayId(),bestPhoto.getId());
            if(database.hasMainPhotoIdInPosition(bestPhoto.getPositionId()))
                database.updatePositionMainPhotoId(bestPhoto.getPositionId(),bestPhoto.getId());
            database.updatePositionEndTime(bestPhoto.getPositionId(),photos.get(photos.size()-1).getTakenDate());
        }

    }
    protected boolean isSimilary(HasBeenPhoto beforePhoto, HasBeenPhoto photo) {
        if (HasBeenDate.isTimeRangeInFive(beforePhoto.getTakenDate(), photo.getTakenDate()))
            return true;

        double hist = HasBeenOpenCv.compareHistogram(getThumbnail(beforePhoto.getPhotoId()), getThumbnail(photo.getPhotoId()));
        Log.i("from id",beforePhoto.getId()+"");
        Log.i("to id",photo.getId()+"");
        Log.i("similary",hist+"");
        if (hist >= 0.8)
            return true;
        return false;
    }

    protected boolean isMaxEdge(int edge, HasBeenPhoto photo) {
        if (photo == null) return true;
        if (edge > photo.getEdgeCount()) return true;
        return false;
    }


    protected Bitmap getThumbnail(long id) {
        return MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
    }
}
