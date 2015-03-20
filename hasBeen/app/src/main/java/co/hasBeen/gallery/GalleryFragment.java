package co.hasBeen.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import co.hasBeen.utils.GlideRequest;
import co.hasBeen.utils.HasBeenFragment;

/**
 * Created by zuby on 2015-01-14.
 */
public class GalleryFragment extends HasBeenFragment {
    PullToRefreshListView mGalleryListView;
    GalleryDayAdapter mDayAdapter;
    ItemModule mDayData;
    List<Day> mDayList;
    DatabaseHelper database;

    class LoadThread extends Thread {
        Long date;
        boolean autoRefresh;

        LoadThread(Long date) {
            this.date = date;
            autoRefresh = false;
        }

        LoadThread(Long date, boolean autoRefresh) {
            this.date = date;
            this.autoRefresh = autoRefresh;
        }

        @Override
        public void run() {
            try {
                List<Day> days;
                days = mDayData.bringTenDay(date);
                if (days != null) {
                    if (!autoRefresh) {
                        mDayList.addAll(days);
                    } else {
                        Collections.reverse(days);
                        for (Day day : days) {
                            if (hasDay(day)) continue;
                            Log.i("newDays", day.getId() + "");
                            mDayList.add(0, day);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mDayList.size() <= 0) {
                        mView.findViewById(R.id.galleryDefault).setVisibility(View.VISIBLE);
                    } else
                        mView.findViewById(R.id.galleryDefault).setVisibility(View.GONE);
                    mDayAdapter.notifyDataSetChanged();
                    stopLoading();
                    if (mGalleryListView.isRefreshing())
                        mGalleryListView.onRefreshComplete();
                }
            });
        }
    }

    public void newDaysLoad() {
        if (!mGalleryListView.isRefreshing())
            startLoading();
        Date date = new Date();
        long offset = date.getTimezoneOffset()*60000;
        new LoadThread(date.getTime()-offset, true).start();
    }

    protected boolean hasDay(Day day) throws Exception {
        for (Day existDay : mDayList) {
            if (!database.hasDay(existDay.getId()))
                mDayList.remove(existDay);
        }
        for (Day existDay : mDayList) {
            if (existDay.getId().equals(day.getId())) return true;
        }
        return false;
    }

    protected void loadDays() {
        startLoading();
        try {
            Day day = database.selectLastDay();
            long offset = new Date().getTimezoneOffset()*60000;
            Long date = new Date().getTime() - offset;
            if (day != null)
                date = day.getDate() + 10000;
            new LoadThread(date).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void init() throws Exception {
        mGalleryListView = (PullToRefreshListView) mView.findViewById(R.id.galleryList);
        mDayData = new ItemModule(getActivity());
        mDayList = new ArrayList<>();
        mDayAdapter = new GalleryDayAdapter(getActivity(), mDayList);
        mGalleryListView.setRefreshing(false);
        ListView listView = mGalleryListView.getRefreshableView();
        listView.setAdapter(mDayAdapter);
        database = new DatabaseHelper(getActivity());
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
        mGalleryListView.setOnDragListener(new GlideRequest(getActivity()));
    }

    @Override
    public void showTab() {
        if (!isShowTab())
            loadDays();
        setShowTab();
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

}
