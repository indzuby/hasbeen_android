package example.test.hasBeen.newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.day.DayView;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.model.api.DayApi;
import example.test.hasBeen.utils.Session;
import example.test.hasBeen.utils.SlidingUpPanelLayout;

/**
 * Created by zuby on 2015-01-23.
 */
public class NewsFeedFragment extends Fragment implements SlidingUpPanelLayout.PanelSlideListener {
    final String TAG = "NewsFeed";
    View mView;
    GoogleMap mMap;
    String mAccessToekn;
    private MapFragment mMapFragment;
    SlidingUpPanelLayout mSlidingUpPanelLayout;
    private View mTransparentHeaderView;
    private View mTransparentView;
    private View mSpaceView;
    private ListView mListView;
    private NewsFeedAdapter mFeedAdapter;
    List<DayApi> mFeeds;
    FrameLayout mMapBox;
    MapRoute mapRoute ;
    boolean flag;
    Long lastUpdateTime;
    void init() {

        mListView = (ListView) mView.findViewById(R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) mView.findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);
        mMapBox = (FrameLayout) mView.findViewById(R.id.mapBox);
        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height) + 128;
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // transparent view at the top of ListView
        mTransparentView = mView.findViewById(R.id.transparentView);

        // init header view for ListView
        mTransparentHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.transparent_header_view, null, false);
        mSpaceView = mTransparentHeaderView.findViewById(R.id.space);

        mFeeds = new ArrayList<>();
        mFeedAdapter = new NewsFeedAdapter(getActivity(), mFeeds);
//        mListView.addHeaderView(mTransparentHeaderView);
        mListView.setAdapter(mFeedAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!flag) {
                    Log.i(TAG, mFeeds.get(position).getId() + "");
                    flag = true;
                    Intent intent = new Intent(getActivity(), DayView.class);
                    intent.putExtra("dayId", mFeeds.get(position).getId());
                    startActivity(intent);
                    flag = false;
                }
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.i("List item",firstVisibleItem+visibleItemCount+" "+totalItemCount);
                if(firstVisibleItem+visibleItemCount >= totalItemCount) {
                    new NewsFeedAsyncTask(handler).execute(mAccessToekn,lastUpdateTime);
                }
            }
        });
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mapRoute = new MapRoute(map,getActivity());
                mFeedAdapter.mMap = map;
                mFeedAdapter.mMapRoute = mapRoute;
                mFeedAdapter.mSlidPanel = mSlidingUpPanelLayout;
                mSlidingUpPanelLayout.collapsePane();
                UiSettings setting = map.getUiSettings();
                setting.setAllGesturesEnabled(false);
                setting.setZoomControlsEnabled(false);
                setting.setMyLocationButtonEnabled(false);
            }
        });
        collapseMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.newsfeed, container, false);
        mAccessToekn = Session.getString(getActivity(),"accessToken",null);
        new NewsFeedAsyncTask(handler).execute(mAccessToekn);
        init();
        return mView;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    final List<DayApi> feeds =  (List<DayApi>)msg.obj;
                    Log.i(TAG,feeds.size()+"");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(DayApi feed : feeds) {
                                            mFeeds.add(feed);
                                            mFeedAdapter.notifyDataSetChanged();
                                            lastUpdateTime = feed.getUpdatedTime();
                                        }
                                        if(feeds.size()>0) {
                                            Log.i("mapRoute", mFeeds.get(0).getMainPlace().getLat() + "");
                                            mapRoute.addMarker(mFeeds.get(0).getMainPlace().getLat(), mFeeds.get(0).getMainPlace().getLon());
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

    private void collapseMap() {
//        mSpaceView.setVisibility(View.VISIBLE);
//        mMapBox.getLayoutParams().height = Util.convertDpToPixel(160,getActivity());
//        mTransparentView.setVisibility(View.GONE);
    }

    private void expandMap() {
//        mSpaceView.setVisibility(View.GONE);
//        mMapBox.getLayoutParams().height = Util.convertDpToPixel(320,getActivity());
//        mTransparentView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(6f), 1000, null);
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(3f), 1000, null);
    }

    @Override
    public void onPanelAnchored(View view) {

    }

}
