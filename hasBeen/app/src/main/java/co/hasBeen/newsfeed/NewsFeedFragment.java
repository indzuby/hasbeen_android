package co.hasBeen.newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.hasBeen.MainActivity;
import co.hasBeen.R;
import co.hasBeen.account.FacebookApi;
import co.hasBeen.account.LoginActivity;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Follow;
import co.hasBeen.social.FbFriendsAsyncTask;
import co.hasBeen.social.FbFriendsItem;
import co.hasBeen.social.FbFriendsView;
import co.hasBeen.utils.GlideRequest;
import co.hasBeen.utils.HasBeenFragment;
import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-23.
 */
public class NewsFeedFragment extends HasBeenFragment {
    final String TAG = "NewsFeed";
    String mAccessToken;
    private NewsFeedAdapter mFeedAdapter;
    List<Day> mFeeds;
    boolean flag;
    Long lastUpdateTime;
    Long firstUpdateTime;
    boolean isFirst = false;
    boolean isLoading = true;
    private PullToRefreshListView mListView;
    View mLoadingView;
    View mLoading;
    View mDefaultView;
    ListView mActualListView;
    ViewPager mViewPager;
    boolean isDefault;
    void init(){

        mListView = (PullToRefreshListView) mView.findViewById(R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        mFeeds = new ArrayList<>();
        mFeedAdapter = new NewsFeedAdapter(getActivity(), mFeeds);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                isFirst = true;
                if (mFeeds.size() > 0) {
                    firstUpdateTime = mFeeds.get(0).getUpdatedTime();
                    new NewsFeedAsyncTask(handler).execute(mAccessToken, "", firstUpdateTime);
                } else new NewsFeedAsyncTask(handler).execute(mAccessToken);
            }
        });
        mListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (!isLoading && !isDefault) {
                    isLoading = true;
                    isFirst = false;
                    startLoading();
                    new NewsFeedAsyncTask(handler).execute(mAccessToken, lastUpdateTime);
                }
            }
        });
        mListView.setOnDragListener(new GlideRequest(getActivity()));
        mActualListView = mListView.getRefreshableView();
        mActualListView.setAdapter(mFeedAdapter);
        mLoadingView = LayoutInflater.from(getActivity()).inflate(R.layout.loading_layout, null, false);
        mLoading = mLoadingView.findViewById(R.id.refresh);
        mActualListView.addFooterView(mLoadingView);
        mDefaultView = LayoutInflater.from(getActivity()).inflate(R.layout.newsfeed_default, null, false);
        dismissDefaultPage();
    }

    protected void startLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        mLoadingView.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }

    protected void dismissDefaultPage() {
        isDefault = false;
        mDefaultView.setVisibility(View.GONE);
        mActualListView.removeHeaderView(mDefaultView);
    }

    protected void showDefaultPage() {
        if(isDefault) return;
        isDefault = true;
        mActualListView.addHeaderView(mDefaultView);
        mDefaultView.setVisibility(View.VISIBLE);
        View hasBeenBtn = mDefaultView.findViewById(R.id.hasBeenBtn);
        hasBeenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeTab(1);
            }
        });
        new FbFriendsAsyncTask(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    final List<Follow> users = (List) msg.obj;
                    int count = users.size();
                    if (count <= 0) {
                        mDefaultView.findViewById(R.id.fbFriendsContainer).setVisibility(View.GONE);
                        return;
                    }
                    TextView count1 = (TextView) mDefaultView.findViewById(R.id.fbFriendsCountFirst);
                    TextView count2 = (TextView) mDefaultView.findViewById(R.id.fbFriendsCountSecond);
                    count1.setText(getString(R.string.your_facebook_friend_count, count));
                    count2.setText(getString(R.string.show_all_facebook_friend, count));
                    LinearLayout fbFriendsThree = (LinearLayout) mDefaultView.findViewById(R.id.fbFriendsThree);
                    List subUser = users.subList(0, (3 < count ? 3 : count));
                    FbFriendsItem items = new FbFriendsItem(subUser, getActivity());
                    for (int i = 0; i < subUser.size(); i++)
                        fbFriendsThree.addView(items.getView(i));
                    count2.setOnClickListener(new View.OnClickListener() {
                        boolean flag = false;

                        @Override
                        public void onClick(View v) {
                            if (!flag) {
                                flag = true;
                                Intent intent = new Intent(getActivity(), FbFriendsView.class);
                                startActivity(intent);
                                flag = false;
                            }
                        }
                    });
                } else {

                }
            }
        }).execute(mAccessToken);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.newsfeed, container, false);
        mAccessToken = Session.getString(getActivity(), "accessToken", null);
        new NewsFeedAsyncTask(handler).execute(mAccessToken);
        init();
        return mView;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    final List<Day> feeds = (List<Day>) msg.obj;
                    Log.i(TAG, feeds.size() + "");
                    if (isFirst)
                        Collections.reverse(feeds);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (Day feed : feeds) {
                                if (!isFirst) {
                                    mFeeds.add(feed);
                                    lastUpdateTime = feed.getUpdatedTime();
                                } else {
                                    mFeeds.add(0, feed);
                                }

                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mFeedAdapter.notifyDataSetChanged();
                                    if (mFeeds.size() <= 0)
                                        showDefaultPage();
                                    else {
                                        dismissDefaultPage();
                                    }
                                }
                            });
                        }
                    }).start();
                    if (mListView.isRefreshing()) {
                        mListView.onRefreshComplete();
                    }
                    if (isLoading) {
                        isLoading = false;
                        stopLoading();
                    }
                    break;
                case -1:
                    int statusLine = (int) msg.obj;
                    if (statusLine == 401) {
                        Toast.makeText(getActivity(), getString(R.string.session_gone), Toast.LENGTH_LONG).show();
                        Session.remove(getActivity(), "accessToken");
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        FacebookApi.callFacebookLogout(getActivity());
                        startActivity(intent);
//                        getActivity().finish();
                    }
                    break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("NewsFeed Fragment");
        Localytics.upload();
    }
}
