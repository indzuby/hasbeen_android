package example.test.hasBeen.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.model.database.Day;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.SlidingUpPanelLayout;

/**
 * Created by zuby on 2015-01-14.
 */
public class GalleryFragment extends Fragment implements SlidingUpPanelLayout.PanelSlideListener{
    View mView;
    ListView mListView;
    List<Day> mGalleryList;
    GalleryDayAdapter mListAdapter;
    DatabaseHelper database;
    TextView mTextDate;
    TextView mAreaView;
    Long scrolledItem;
    RelativeLayout mGalleryBox ;
    RelativeLayout mDayTopBox;
    ImageButton mFab;
    GoogleMap mMap;
    MapRoute mMapRoute;
    boolean mMapVisible=true;
    SlidingUpPanelLayout mSlidingUpPanelLayout;
    protected void init() throws Exception {
        mListView = (ListView) mView.findViewById(R.id.galleryL1ListView);
        mTextDate = (TextView) mView.findViewById(R.id.galleryL1dayTopTextDate);
        mAreaView = (TextView) mView.findViewById(R.id.galleryL1dayTopTextArea);

        database = new DatabaseHelper(getActivity());
        mGalleryBox = (RelativeLayout) mView.findViewById(R.id.gallery_box);
        mDayTopBox = (RelativeLayout) mView.findViewById(R.id.day_top_box);
        mFab = (ImageButton) mView.findViewById(R.id.fab);

        initEventListner();
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        try {
            mGalleryList = database.selectBeforeFiveDay();
        }catch (Exception e){
            mGalleryList = new ArrayList<>();
            e.printStackTrace();
        }
        mListAdapter = new GalleryDayAdapter(getActivity(), mGalleryList);


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                UiSettings setting = map.getUiSettings();
                setting.setAllGesturesEnabled(false);
                setting.setZoomControlsEnabled(false);
                setting.setMyLocationButtonEnabled(false);
                mSlidingUpPanelLayout.collapsePane();
                try {
                    mMapRoute = new MapRoute(mMap,getActivity());
                    mListAdapter.mMapRoute = mMapRoute;
                    mListAdapter.mListview = mListView;
                    mListAdapter.parentDate = mTextDate;
                    mListAdapter.parentPlace = mAreaView;
                    mListAdapter.parentSliding = mSlidingUpPanelLayout;
                    mMapRoute.createRouteDay(scrolledItem = database.selectBeforeFiveDay().get(0).getId());
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) mView.findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);
        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height)+128;
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // transparent view at the top of ListView

        // init header view for ListView

//        mTransparentHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.transparent_header_view, null, false);
//        mSpaceView = mTransparentHeaderView.findViewById(R.id.space);
//        mListView.addHeaderView(mTransparentHeaderView);

        mListView.setAdapter(mListAdapter);
    }

    protected void initEventListner() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int beforeItem = 0;
            private int height;
            private int mLastFirstVisibleItem;
            private boolean mIsScrollingUp;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mListAdapter != null) {
                    if (visibleItemCount > 0) {
                        mTextDate.setText(HasBeenDate.convertDate(mListAdapter.mGalleryList.get(firstVisibleItem).getDate()));
                        mAreaView.setText(mListAdapter.mGalleryList.get(firstVisibleItem).getArea());
                        scrolledItem = mGalleryList.get(firstVisibleItem).getId();
                    }
                }
            }
        });
        mListView.setOnTouchListener(new View.OnTouchListener() {
            float startPosition = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        float endPosition = event.getY();
                        if (endPosition > startPosition) {
                            Log.i("scroll", "down");
                        } else if (endPosition < startPosition) {
                            Log.i("scroll", "up");
                        }
                        startPosition = endPosition;
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startPosition = event.getY();
                        break;
                }
                return false;
            }
        });
        mFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity().getBaseContext(),"Floating button pressed ",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), GalleryShare.class);
                intent.putExtra("id", scrolledItem);
                intent.putExtra("area", mAreaView.getText().toString());
                intent.putExtra("date", mTextDate.getText().toString());
                startActivity(intent);
            }
        });
        mDayTopBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Day click","click");
                mMapRoute.createRouteDay(scrolledItem);
                mSlidingUpPanelLayout.collapsePane();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.gallery_level_1, container, false);
        try {
            init();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Iterator iterator = database.selectBeforeFiveDay().iterator();
//                                    while (iterator.hasNext()) {
//                                        HasBeenDay day = (HasBeenDay) iterator.next();
//                                        mGalleryList.add(day);
//                                        mListAdapter.notifyDataSetChanged();
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mView;
    }


    static final int REQUEST = 1;  // The request code
    static final int RESULT = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
        if (resultCode == RESULT) {
            try {
                mListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void collapseMap() {
//        mSpaceView.setVisibility(View.VISIBLE);
//        mTransparentView.setVisibility(View.GONE);
    }

    private void expandMap() {
//        mSpaceView.setVisibility(View.GONE);
//        mTransparentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
//        expandMap();
    }

    @Override
    public void onPanelExpanded(View view) {
//        collapseMap();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

}
