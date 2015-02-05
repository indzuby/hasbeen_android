package example.test.hasBeen.day;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.comment.CommentView;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.model.api.Comment;
import example.test.hasBeen.model.api.DayApi;
import example.test.hasBeen.model.api.PositionApi;
import example.test.hasBeen.utils.CircleTransform;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.Session;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-27.
 */
public class DayView extends ActionBarActivity{
    final static String TAG = "Day View";
    TextView titleView;
    ListView mListView;
    View mHeaderView;
    View mFooterView;
    int nowScrolled = 0 ;
    GoogleMap mMap = null;
    DayApi mDay;
    MapRoute mMapRoute = null;
    DayAdapter mDayAdapter;
    Long mDayId;
    String mAccessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showProgress();
        super.onCreate(savedInstanceState);
        mDayId = getIntent().getLongExtra("dayId",0);
        mAccessToken = Session.getString(this,"accessToken",null);
        init();
    }
    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    DayApi day = (DayApi)msg.obj;
                    List<PositionApi> positions = new ArrayList<>();
                    for(PositionApi position : day.getPositionList()) {
                        if(position.getType().equals("PLACE"))
                            positions.add(position);
                    }
                    mDay = day;
                    mDay.setPositionList(positions);
                    Log.i(TAG,mDay.getId()+"");
                    titleView.setText(mDay.getTitle());
                    initHeaderView();
                    initBodyView();
                    initFoorteView();
                    dialog.dismiss();
                    break;
                case -1:
                    Log.i(TAG,"NULL");
                    break;
            }
        }
    };
    protected void initHeaderView(){
        View titleBox = findViewById(R.id.dayTitleBox);
        ImageView profileImage = (ImageView) titleBox.findViewById(R.id.profileImage);
        TextView name = (TextView) titleBox.findViewById(R.id.profileName);
        TextView placeName = (TextView) titleBox.findViewById(R.id.placeName);
        TextView date = (TextView) titleBox.findViewById(R.id.date);

        TextView dayTitle = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        TextView socialAction = (TextView) findViewById(R.id.socialAction);
        TextView totalPhoto = (TextView) findViewById(R.id.totalPhoto);
        Glide.with(this).load(mDay.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(this)).into(profileImage);
        Log.i(TAG, mDay.getMainPlace().getName());
        name.setText(Util.parseName(mDay.getUser(), 0));
        placeName.setText(Util.convertPlaceName(mDay.getPositionList()));
        date.setText(HasBeenDate.convertDate(mDay.getDate()));
        dayTitle.setText(mDay.getTitle());
        description.setText(mDay.getDescription());
        socialAction.setText(mDay.getLoveCount()+" Likes · " + mDay.getCommentCount()+" Commnents · "+mDay.getShareCount()+" Shared");
        totalPhoto.setText("Total " + mDay.getPhotoCount() + " photos");
        Log.i(TAG, mDay.getPositionList().size() + "");
        new Thread(new Runnable() {
            boolean flag=true;
            @Override
            public void run() {
                while(flag) {
                    if(mMapRoute!=null && mMap!=null){
                        flag = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMapRoute.createRouteDay(mDay.getPositionList());
                            }
                        });

                    }
                }
            }
        }).start();
    }
    protected void initBodyView (){
        mDayAdapter = new DayAdapter(this,mDay.getPositionList());
        mListView.setAdapter(mDayAdapter);
    }
    protected  void initFoorteView(){
        ImageButton commentButton = (ImageButton) findViewById(R.id.comment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View v) {
                if(!flag) {
                    flag = true;
                    Intent intent = new Intent(getBaseContext(), CommentView.class);
                    startActivity(intent);
                    flag = false;
                }

            }
        });

        LinearLayout commentBox = (LinearLayout) findViewById(R.id.commetBox);
        TextView moreComments = (TextView) findViewById(R.id.moreComments);
        List<Comment> comments = mDay.getCommentList();
        for(int i = 0 ;i<3 && i<comments.size();i++) {
            Comment comment = comments.get(i);
            View commentView = getLayoutInflater().inflate(R.layout.comment,null);
            TextView contents = (TextView) commentView.findViewById(R.id.contents);
            TextView commentTime = (TextView) commentView.findViewById(R.id.commentTime);
            contents.setText(comment.getContents());
            commentTime.setText(HasBeenDate.getGapTime(comment.getCreatedTime()));
            ImageView profileImage = (ImageView) commentView.findViewById(R.id.profileImage);
            TextView profileName = (TextView) commentView.findViewById(R.id.profileName);
            Glide.with(this).load(comment.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(this)).into(profileImage);
            profileName.setText(Util.parseName(comment.getUser(),0));
            commentBox.addView(commentView);

        }
        if(comments.size()>3)
            moreComments.setText(comments.size()-3+" comments more");
        else
            moreComments.setVisibility(View.GONE);

    }
    protected void init(){
        setContentView(R.layout.day);
        initActionBar();
        new DayAsyncTask(handler).execute(mAccessToken, mDayId);
        mHeaderView =  LayoutInflater.from(this).inflate(R.layout.day_header, null, false);
        mFooterView =  LayoutInflater.from(this).inflate(R.layout.day_footer, null, false);
        mListView = (ListView) findViewById(R.id.listPhotos);
        mListView.addHeaderView(mHeaderView);
        mListView.addFooterView(mFooterView);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMapRoute = new MapRoute(mMap,getBaseContext());
                UiSettings setting = map.getUiSettings();
                setting.setAllGesturesEnabled(false);
                setting.setZoomControlsEnabled(false);
                setting.setMyLocationButtonEnabled(false);
            }
        });

    }

    protected void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText("Day2-Beautiful Swiss");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);

    }

    ProgressDialog dialog;

    protected void showProgress() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setMessage("Wait a minutes...");
        dialog.setProgress(100);
        dialog.show();
    }
    @Override
    public void onDestroy() {
        if(mDayAdapter!=null)
            mDayAdapter.recycle();


//        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
//        unbindDrawables(findViewById(R.id.listPhotos));
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();

        System.gc();
    }
}
