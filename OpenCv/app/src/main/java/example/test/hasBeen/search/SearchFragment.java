package example.test.hasBeen.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.model.api.NewsFeedApi;
import example.test.hasBeen.model.api.PhotoApi;

/**
 * Created by zuby on 2015-01-30.
 */
public class SearchFragment extends Fragment {
    final static int DAY = 1;
    final static int PHOTO = 2;
    List<NewsFeedApi> mDays;
    List<PhotoApi> mPhotos;
    View mView;
    GoogleMap mMap;
    SupportMapFragment mMapFragment;
    TextView mDayButton;
    TextView mPhotoButton;
    ImageView mRefresh;
    MapRoute mMapRoute;
    int nowTab = DAY;

    boolean isrefresh = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.search, container, false);
        new SearchDayAsyncTask(dayHandler).execute();
        init();
        return mView;
    }

    protected void init() {
        mMapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));


        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMapRoute = new MapRoute(map, getActivity());
                UiSettings setting = map.getUiSettings();
                setting.setZoomControlsEnabled(true);
                setting.setMyLocationButtonEnabled(true);
                map.setMyLocationEnabled(true);
            }
        });
        mDayButton = (TextView) mView.findViewById(R.id.dayButton);
        mPhotoButton = (TextView) mView.findViewById(R.id.photoButton);
        mRefresh = (ImageView) mView.findViewById(R.id.refresh);

        mDayButton.setOnClickListener(new ButtonClickListner());
        mPhotoButton.setOnClickListener(new ButtonClickListner());
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isrefresh) {
                    Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                    isrefresh = true;
                    mRefresh.startAnimation(rotate);
                    if(nowTab == DAY)
                        new SearchDayAsyncTask(dayHandler).execute();
                    else
                        new SearchPhotoAsyncTask(photoHandler).execute();
                }
            }
        });
    }

    Handler dayHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mDays = (List<NewsFeedApi>) msg.obj;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<LatLng> latLngs = new ArrayList<>();
                                        for (NewsFeedApi day : mDays)
                                            latLngs.add(new LatLng(day.getMainPlace().getLat(), day.getMainPlace().getLon()));

                                        LatLng location = latLngs.get(0);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                                        mMapRoute.addMarkerCluster(latLngs);
                                        if(isrefresh) {
                                            mRefresh.clearAnimation();
                                            isrefresh = false;
                                        }

                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case -1:
                    break;
            }
        }
    };


    Handler photoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mPhotos = (List<PhotoApi>) msg.obj;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LatLng location = new LatLng(mPhotos.get(0).getLat(),mPhotos.get(0).getLon());
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                                        mMapRoute.addMarkerClusterPhoto(mPhotos);
                                        if(isrefresh) {
                                            mRefresh.clearAnimation();
                                            isrefresh = false;
                                        }

                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case -1:
                    break;
            }
        }
    };
    class ButtonClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TextView button = (TextView) v;

            if (nowTab!=DAY && button.equals(mDayButton)) {
                new SearchDayAsyncTask(dayHandler).execute();
            } else if (nowTab!=PHOTO && button.equals(mPhotoButton)) {
                new SearchPhotoAsyncTask(photoHandler).execute();
            }
            swapButtonColor();
        }
    }

    public void swapButtonColor() {

        if (nowTab != DAY) {
            nowTab = DAY;
            mDayButton.setTextColor(getResources().getColor(R.color.theme_color));
            mPhotoButton.setTextColor(getResources().getColor(R.color.light_black));
        } else {
            nowTab = PHOTO;
            mDayButton.setTextColor(getResources().getColor(R.color.light_black));
            mPhotoButton.setTextColor(getResources().getColor(R.color.theme_color));

        }

    }

}
