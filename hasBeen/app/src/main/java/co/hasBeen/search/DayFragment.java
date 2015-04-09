package co.hasBeen.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.RecentSearch;
import co.hasBeen.model.network.PlaceAutocomplete;
import co.hasBeen.utils.HasBeenFragment;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-24.
 */
public class DayFragment extends HasBeenFragment {
    final static String TYPE = "DAY";
    String mAccessToken;
    ListView mListView;
    DayAdapter mDayAdapter;
    List<Day> mDayList;
    String mKeyword;
    DataBaseHelper database;
    boolean isComplete;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.search_day, container, false);
        mAccessToken = Session.getString(getActivity(), "accessToken", null);
        database = new DataBaseHelper(getActivity());
        mListView = (ListView)mView.findViewById(R.id.listView);
        initDefault();
        return mView;
    }

    protected void initDefault(){
        // database
        RecentAdapter adapter = new RecentAdapter(getActivity(),TYPE);
        mListView.setAdapter(adapter);
    }
    SearchDayAsyncTask dayAsyncTask;
    public void doSearch(String keyword,String placeId){
        mKeyword = keyword;
        mDayList = new ArrayList<>();
         mDayAdapter = new DayAdapter(mDayList,getActivity());
        mListView.setAdapter(mDayAdapter);
        dayAsyncTask = new SearchDayAsyncTask(dayHandler);
        dayAsyncTask.execute(mAccessToken, placeId);
        insertRecentKeyword(keyword,placeId);
    }
    public void doRecommnad(String keyword){
        startLoading();
        new PlaceAutocompleteAsyncTask(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    stopLoading();
                    Gson gson = new Gson();
                    String output = gson.toJson(msg.obj);
                    PlaceAutocomplete places = (PlaceAutocomplete) msg.obj;
                    Log.i("OUTPUT", output);
                    RecommandSearchAdapter adapter = new RecommandSearchAdapter(places.getPredictions(),getActivity());
                    mListView.setAdapter(adapter);
                }
            }
        },getActivity()).execute(keyword);
    }
    Handler dayHandler = new Handler(Looper.getMainLooper()) {
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
                        mDayAdapter.notifyDataSetChanged();
                    }
                });
                if(days.size()<=0)
                    isComplete = true;
                init();
            }else {
            }
            stopLoading();
        }
    };
    protected void insertRecentKeyword(String keyword, String placeId) {
        RecentSearch recent = new RecentSearch();
        recent.setKeyword(keyword);
        recent.setType(TYPE);
        recent.setCreateDate(new Date().getTime());
        recent.setPlaceId(placeId);
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
                    if(!isLoading && !isComplete) {
                        startLoading();
                        Day day = mDayList.get(mDayList.size()-1);
                        dayAsyncTask = new SearchDayAsyncTask(dayHandler);
                        dayAsyncTask.execute(mAccessToken, mKeyword, day.getPage());
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Day Search");
        Localytics.upload();
    }

    @Override
    public void onDestroy() {
        if(dayAsyncTask !=null)
            dayAsyncTask.cancel(true);
        System.gc();
        super.onDestroy();
    }
}
