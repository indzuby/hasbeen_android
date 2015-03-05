package co.hasBeen.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.ItemModule;
import co.hasBeen.model.database.Day;

/**
 * Created by zuby on 2015-01-14.
 */
public class GalleryView extends Fragment {
    View mView;
    PullToRefreshListView mGalleryListView;
    GalleryDayAdapter mDayAdapter;
    ItemModule mDayData;
    List<Day> mDayList;
    View mLoadingView;
    View mLoading;
    boolean isLoading;

    class LoadThread extends Thread {
        Long date;

        LoadThread(Long date) {
            this.date = date;
        }

        @Override
        public void run() {
            if (isLoading) {
                try {
                    List<Day> days;
                    days = mDayData.bringTenDay(date);
                    mDayList.addAll(days);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDayAdapter.notifyDataSetChanged();
                            stopLoading();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void init() throws Exception {
        mGalleryListView = (PullToRefreshListView) mView.findViewById(R.id.galleryList);
        mDayData = new ItemModule(getActivity());
        mDayList = new ArrayList<>();
        mDayAdapter = new GalleryDayAdapter(getActivity(), mDayList);
        mGalleryListView.setRefreshing(false);
        mLoadingView = LayoutInflater.from(getActivity()).inflate(R.layout.loading_layout, null, false);
        mLoading = mLoadingView.findViewById(R.id.refresh);
        ListView listView = mGalleryListView.getRefreshableView();
        listView.addFooterView(mLoadingView);
        listView.setAdapter(mDayAdapter);
        startLoading();
        new LoadThread(new Date().getTime()).start();
        mGalleryListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if(!isLoading) {
                    startLoading();
                    if (mDayList.size() > 0)
                        new LoadThread(mDayList.get(mDayList.size() - 1).getDate()).start();
                }
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
        mLoadingView.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        isLoading = false;
        mLoadingView.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }
}
