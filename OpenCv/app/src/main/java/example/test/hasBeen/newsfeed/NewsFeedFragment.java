package example.test.hasBeen.newsfeed;

import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.day.DayView;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.model.database.Day;
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
//    private ListView mListView;
    private NewsFeedAdapter mFeedAdapter;
    List<Day> mFeeds;
    FrameLayout mMapBox;
    MapRoute mapRoute ;
    boolean flag;
    Long lastUpdateTime;
    private PullToRefreshListView mListView;
    void init() {

        mListView = (PullToRefreshListView) mView.findViewById(R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mFeeds = new ArrayList<>();
        mFeedAdapter = new NewsFeedAdapter(getActivity(), mFeeds);
//        mListView.addHeaderView(mTransparentHeaderView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!flag) {
                    int index = position-1;
                    Log.i(TAG, mFeeds.get(index).getId() + "");
                    flag = true;
                    Intent intent = new Intent(getActivity(), DayView.class);
                    intent.putExtra("dayId", mFeeds.get(index).getId());
                    startActivity(intent);
                    flag = false;
                }
            }
        });
        mListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                showProgress();
                new NewsFeedAsyncTask(handler).execute(mAccessToekn,lastUpdateTime);
            }
        });
        ListView actualListView = mListView.getRefreshableView();
        actualListView.setAdapter(mFeedAdapter);
//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            boolean loading = false;
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.i("List item",firstVisibleItem+visibleItemCount+" "+totalItemCount);
//                if(firstVisibleItem+visibleItemCount >= totalItemCount && !loading) {
//                    loading = true;
//                    showProgress();
//                    new NewsFeedAsyncTask(handler).execute(mAccessToekn,lastUpdateTime,loading);
//                }
//            }
//        });

//        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
//                .findFragmentById(R.id.map));
//
//
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap map) {
//                mMap = map;
//                mapRoute = new MapRoute(map,getActivity());
//                mFeedAdapter.mMap = map;
//                mFeedAdapter.mMapRoute = mapRoute;
//                mSlidingUpPanelLayout.collapsePane();
//                UiSettings setting = map.getUiSettings();
//                setting.setAllGesturesEnabled(false);
//                setting.setZoomControlsEnabled(false);
//                setting.setMyLocationButtonEnabled(false);
//            }
//        });
//        collapseMap();
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
                    final List<Day> feeds =  (List<Day>)msg.obj;
                    Log.i(TAG, feeds.size() + "");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(Day feed : feeds) {
                                            mFeeds.add(feed);
                                            mFeedAdapter.notifyDataSetChanged();
                                            lastUpdateTime = feed.getUpdatedTime();
                                        }
//                                        if(feeds.size()>0) {
//                                            Log.i("mapRoute", mFeeds.get(0).getMainPlace().getLat() + "");
//                                            mapRoute.addMarker(mFeeds.get(0).getMainPlace().getLat(), mFeeds.get(0).getMainPlace().getLon());
//                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    if(dialog!=null) {
                        dialog.dismiss();
                        dialog = null;
                    }
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

    ProgressDialog dialog;

    protected void showProgress() {
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setMessage("Loading the NewsFeeds");
        dialog.setProgress(100);
        dialog.show();
    }
}
