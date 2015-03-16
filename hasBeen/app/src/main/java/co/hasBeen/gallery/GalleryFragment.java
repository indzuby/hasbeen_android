package co.hasBeen.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.database.ItemModule;
import co.hasBeen.model.api.Day;

/**
 * Created by zuby on 2015-01-14.
 */
public class GalleryFragment extends Fragment {
    View mView;
    PullToRefreshListView mGalleryListView;
    GalleryDayAdapter mDayAdapter;
    ItemModule mDayData;
    List<Day> mDayList;
    View mLoading;
    DatabaseHelper database;
    boolean isLoading;

    class LoadThread extends Thread {
        Long date;
        boolean autoRefresh;
        LoadThread(Long date) {
            this.date = date; autoRefresh = false;
        }

        LoadThread(Long date, boolean autoRefresh) {
            this.date = date;
            this.autoRefresh = autoRefresh;
        }

        @Override
        public void run() {
            if (isLoading) {
                try {
                    List<Day> days;
                    days = mDayData.bringTenDay(date);
                    if(!autoRefresh) {
                        if (days != null)
                            mDayList.addAll(days);
                    }else {
                        Collections.reverse(days);
                        for(Day day :days) {
                            if(hasDay(day)) continue;
                            Log.i("newDays", day.getId() + "");
                            mDayList.add(0,day);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mDayList.size() <= 0) {
                    mView.findViewById(R.id.galleryDefault).setVisibility(View.VISIBLE);
                } else
                    mView.findViewById(R.id.galleryDefault).setVisibility(View.GONE);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDayAdapter.notifyDataSetChanged();
                        stopLoading();
                        if(mGalleryListView.isRefreshing())
                            mGalleryListView.onRefreshComplete();
                    }
                });
            }
        }
    }
    public void newDaysLoad() {
        isLoading=true;
        if(!mGalleryListView.isRefreshing())
            startLoading();
        Long date = new Date().getTime();
        new LoadThread(date,true).start();

    }
    protected boolean hasDay(Day day){
        for(Day existDay : mDayList) {
            if(existDay.getId().equals(day.getId())) return true;
        }
        return false;
    }

    protected void loadDays() throws Exception{
        startLoading();
        Day day = database.selectLastDay();
        Long date = new Date().getTime();
        if (day != null)
            date = day.getDate() + 10000;
        new LoadThread(date).start();
    }
    protected void init() throws Exception {
        mGalleryListView = (PullToRefreshListView) mView.findViewById(R.id.galleryList);
        mDayData = new ItemModule(getActivity());
        mDayList = new ArrayList<>();
        mDayAdapter = new GalleryDayAdapter(getActivity(), mDayList);
        mGalleryListView.setRefreshing(false);
        mLoading = mView.findViewById(R.id.refresh);
        ListView listView = mGalleryListView.getRefreshableView();
        listView.setAdapter(mDayAdapter);
        database = new DatabaseHelper(getActivity());
        loadDays();
        mGalleryListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (!isLoading) {
                    startLoading();
                    if (mDayList.size() > 0)
                        new LoadThread(mDayList.get(mDayList.size() - 1).getDate()).start();
                }
            }
        });
        mGalleryListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                newDaysLoad();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.gallery, container, false);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mView;
    }

    protected void startLoading() {
        isLoading = true;
        mLoading.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        isLoading = false;
        mLoading.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }
}
