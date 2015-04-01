package co.hasBeen.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.RecentSearch;
import co.hasBeen.utils.HasBeenFragment;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-24.
 */
public class TripFragment extends HasBeenFragment {
    String mAccessToken;
    ListView mListView;
    TripAdapter mTripAdapter;
    List<Day> mDayList;
    String mKeyword;
    DatabaseHelper database;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.search_trip, container, false);
        mAccessToken = Session.getString(getActivity(), "accessToken", null);
        database = new DatabaseHelper(getActivity());
        mListView = (ListView)mView.findViewById(R.id.listView);
        initDefault();
        return mView;
    }

    protected void initDefault(){
        // database
        RecentAdapter adapter = new RecentAdapter(getActivity(),"TRIP");
        mListView.setAdapter(adapter);
    }
    SearchTripAsyncTask tripAsyncTask;
    public void doSearch(String keyword){
        mKeyword = keyword;
        mDayList = new ArrayList<>();
         mTripAdapter = new TripAdapter(mDayList,getActivity());
        mListView.setAdapter(mTripAdapter);
        startLoading();
        tripAsyncTask = new SearchTripAsyncTask(tripHandler);
        tripAsyncTask.execute(mAccessToken, keyword);
        insertRecentKeyword(keyword);
    }
    Handler tripHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0) {
                List<Day> days= (List) msg.obj;
                for(Day day : days)
                    mDayList.add(day);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTripAdapter.notifyDataSetChanged();
                    }
                });
                init();
            }else {
            }
            stopLoading();
        }
    };
    protected void insertRecentKeyword(String keyword) {
        RecentSearch recent = new RecentSearch();
        recent.setKeyword(keyword);
        recent.setType("TRIP");
        recent.setCreateDate(new Date().getTime());
        try {
            database.insertRecent(recent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void init(){
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(visibleItemCount<totalItemCount && firstVisibleItem+visibleItemCount>=totalItemCount) {
                    if(!isLoading) {
                        startLoading();
//                        Day day = mDayList.get(mDayList.size()-1);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Trip Search");
        Localytics.upload();
    }

    @Override
    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }
}
