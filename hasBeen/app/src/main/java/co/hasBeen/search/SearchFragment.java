package co.hasBeen.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.map.MapRoute;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-30.
 */
public class SearchFragment extends Fragment {
    final static int DAY = 1;
    final static int PHOTO = 2;
    List<Day> mDays;
    List<Photo> mPhotos;
    View mView;
    GoogleMap mMap;
    SupportMapFragment mMapFragment;
    TextView mDayButton;
    TextView mPhotoButton;
    ImageView mRefresh;
    MapRoute mMapRoute;
    int nowTab = DAY;
    String mAccessToken;
    boolean isrefresh = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.search, container, false);
        mAccessToken = Session.getString(getActivity(),"accessToken",null);
        new SearchDayAsyncTask(dayHandler).execute(mAccessToken);
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
                setting.setRotateGesturesEnabled(false);
                setting.setAllGesturesEnabled(true);
                setting.setMyLocationButtonEnabled(false);
                map.setMyLocationEnabled(false);
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
                    mRefresh.setImageResource(R.drawable.loading);
                    isrefresh = true;
                    mRefresh.startAnimation(rotate);
                    if(nowTab == DAY)
                        new SearchDayAsyncTask(dayHandler).execute(mAccessToken);
                    else
                        new SearchPhotoAsyncTask(photoHandler).execute(mAccessToken);
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
                    mDays = (List<Day>) msg.obj;
                    dayRendering();
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
                    mPhotos = (List<Photo>) msg.obj;
                    photoRendering();
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
                mapRendering(DAY);
                swapButtonColor();
            } else if (nowTab!=PHOTO && button.equals(mPhotoButton)) {
                mapRendering(PHOTO);
                swapButtonColor();
            }
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
    protected void mapRendering(int flag){
        if(flag==DAY) {
            if(mDays!=null)
                dayRendering();
            else
                new SearchDayAsyncTask(dayHandler).execute(mAccessToken);
        }else {
            if(mPhotos!=null)
                photoRendering();
            else
                new SearchPhotoAsyncTask(photoHandler).execute(mAccessToken);
        }
    }
    protected void dayRendering(){
        try {
            LatLng location = new LatLng(mDays.get(0).getMainPlace().getLat(), mDays.get(0).getMainPlace().getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
            mMapRoute.addMarkerCluster(mDays);
            if (isrefresh) {
                mRefresh.clearAnimation();
                mRefresh.setImageResource(R.drawable.refresh);
                isrefresh = false;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void photoRendering (){
        try {
            LatLng location = new LatLng(mPhotos.get(0).getLat(), mPhotos.get(0).getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
            mMapRoute.addMarkerClusterPhoto(mPhotos);
            if (isrefresh) {
                mRefresh.clearAnimation();
                mRefresh.setImageResource(R.drawable.refresh);
                isrefresh = false;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
