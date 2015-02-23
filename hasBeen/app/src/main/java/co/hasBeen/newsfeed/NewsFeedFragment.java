package co.hasBeen.newsfeed;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.day.DayView;
import co.hasBeen.model.database.Day;
import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-23.
 */
public class NewsFeedFragment extends Fragment{
    final String TAG = "NewsFeed";
    View mView;
    String mAccessToekn;
    private NewsFeedAdapter mFeedAdapter;
    List<Day> mFeeds;
    boolean flag;
    Long lastUpdateTime;
    Long firstUpdateTime;
    boolean isFirst = false;
    boolean isLoading = false;
    private PullToRefreshListView mListView;
    View mLoadingView;
    View mLoading;
    void init() {

        mListView = (PullToRefreshListView) mView.findViewById(R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mFeeds = new ArrayList<>();
        mFeedAdapter = new NewsFeedAdapter(getActivity(), mFeeds);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                isFirst = true;
                firstUpdateTime = mFeeds.get(0).getUpdatedTime();
                new NewsFeedAsyncTask(handler).execute(mAccessToekn,"",firstUpdateTime);
            }
        });
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
                if(!isLoading) {
                    isLoading = true;
                    isFirst = false;
                    startLoading();
                    new NewsFeedAsyncTask(handler).execute(mAccessToekn, lastUpdateTime);
                }
            }
        });
        ListView actualListView = mListView.getRefreshableView();
        actualListView.setAdapter(mFeedAdapter);
        mLoadingView =  LayoutInflater.from(getActivity()).inflate(R.layout.newsfeed_loading, null, false);
        mLoading = mLoadingView.findViewById(R.id.refresh);
        actualListView.addFooterView(mLoadingView);
    }
    protected void startLoading(){
        mLoadingView.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }
    protected void stopLoading(){
        mLoadingView.setVisibility(View.GONE);
        mLoading.clearAnimation();
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
                    if(isFirst)
                        Collections.reverse(feeds);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(Day feed : feeds) {
                                            if(!isFirst) {
                                                mFeeds.add(feed);
                                                lastUpdateTime = feed.getUpdatedTime();
                                            }else {
                                                mFeeds.add(0,feed);
                                            }
                                            mFeedAdapter.notifyDataSetChanged();

                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    if(mListView.isRefreshing()) {
                        mListView.onRefreshComplete();
                    }
                    if(isLoading) {
                        isLoading = false;
                        stopLoading();
                    }
                    break;
                case -1:
                    break;
            }
        }
    };
    ProgressDialog dialog;

    protected void showProgress() {
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Loading the NewsFeeds");
        dialog.setProgress(100);
        dialog.show();
    }
}
