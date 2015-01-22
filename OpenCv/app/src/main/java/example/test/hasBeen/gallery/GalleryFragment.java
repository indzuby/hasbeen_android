package example.test.hasBeen.gallery;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import example.test.hasBeen.model.HasBeenDay;
import example.test.hasBeen.model.HasBeenPhoto;
import example.test.hasBeen.model.HasBeenPlace;
import example.test.hasBeen.model.HasBeenPosition;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.HasBeenOpenCv;

/**
 * Created by zuby on 2015-01-14.
 */
public class GalleryFragment extends Fragment{
    View view;
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
    TextView textDate;
    TextView areaView;
    HasBeenPhoto lastPhoto ;
    Long scrolledItem;
    LinearLayout mMapBox;
    protected void init() throws Exception{
        listView = (ListView) view.findViewById(R.id.galleryL1ListView);
        mGalleryList = new ArrayList<>();
        listAdapter = new GalleryDayAdapter(getActivity(), mGalleryList);
        textDate = (TextView) view.findViewById(R.id.galleryL1dayTopTextDate);
        areaView = (TextView) view.findViewById(R.id.galleryL1dayTopTextArea);

        listView.setAdapter(listAdapter);
        database = new DatabaseHelper(getActivity());
        resolver = getActivity().getContentResolver();
        mPhotoList = new ArrayList<>();
        lastPhoto = database.getLastPhoto();
//        MapFragment mapFragment = (MapFragment) view.getFragmentManager()
//                .findFragmentById(R.id.view_map);
//        mapFragment.getMapAsync(this);
//        geo = new GeoGoogle(this);
        mMapBox = (LinearLayout) view.findViewById(R.id.map_box);
        initEventListner();
        ImageButton fab = (ImageButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity().getBaseContext(),"Floating button pressed ",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),GalleryShare.class);
                intent.putExtra("id",scrolledItem);
                intent.putExtra("area",areaView.getText().toString());
                intent.putExtra("date",textDate.getText().toString());
                startActivity(intent);
            }
        });



    }
    protected void initEventListner (){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int beforeItem=0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(listAdapter!=null) {
                    if(visibleItemCount>0) {
                        textDate.setText(HasBeenDate.convertDate(listAdapter.mGalleryList.get(firstVisibleItem).getDate()));
                        areaView.setText(listAdapter.mGalleryList.get(firstVisibleItem).getArea());
                        scrolledItem = mGalleryList.get(firstVisibleItem).getId();
                    }
                    if(firstVisibleItem>beforeItem){
                        mMapBox.setVisibility(View.INVISIBLE);
                        Log.i("scroll","down");
                    }else if(firstVisibleItem<beforeItem){
                        mMapBox.setVisibility(View.VISIBLE);
                        Log.i("scroll","up");
                    }
                    beforeItem = firstVisibleItem;
                }
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.gallery_level_1,container,false);
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Iterator iterator = database.selectBeforeFiveDay().iterator();
                                    while (iterator.hasNext()) {
                                        HasBeenDay day = (HasBeenDay) iterator.next();
                                        mGalleryList.add(day);
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
        return view;
    }
    protected  void showProgress(){
        dialog = new ProgressDialog(getActivity());
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
                            }).execute(photo.getLat(), photo.getLon(), photo,1);

                        }
                    } else {
                        flag= false;
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
                        }).execute(photo.getLat(), photo.getLon(), photo,1);
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

    static final int REQUEST = 1;  // The request code
    static final int RESULT = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
        if(resultCode == RESULT) {
            try {
                listAdapter.notifyDataSetChanged();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
