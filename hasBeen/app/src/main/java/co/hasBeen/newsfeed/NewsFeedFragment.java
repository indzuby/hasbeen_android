package co.hasBeen.newsfeed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.hasBeen.MainActivity;
import co.hasBeen.R;
import co.hasBeen.account.LoginActivity;
import co.hasBeen.day.DayView;
import co.hasBeen.model.api.Follow;
import co.hasBeen.model.api.Day;
import co.hasBeen.account.FacebookApi;
import co.hasBeen.social.FbFriendsAsyncTask;
import co.hasBeen.social.FbFriendsItem;
import co.hasBeen.social.FbFriendsView;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-23.
 */
public class NewsFeedFragment extends Fragment{
    final String TAG = "NewsFeed";
    View mView;
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
    Typeface medium,regular;
    void init() {
        medium = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Medium.ttf");
        regular = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Regular.ttf");

        mListView = (PullToRefreshListView) mView.findViewById(R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        mFeeds = new ArrayList<>();
        mFeedAdapter = new NewsFeedAdapter(getActivity(), mFeeds);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                isFirst = true;
                if(mFeeds.size()>0) {
                    firstUpdateTime = mFeeds.get(0).getUpdatedTime();
                    new NewsFeedAsyncTask(handler).execute(mAccessToken, "", firstUpdateTime);
                }
                else new NewsFeedAsyncTask(handler).execute(mAccessToken);
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
                    intent.putExtra("id", mFeeds.get(index).getId());
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
                    new NewsFeedAsyncTask(handler).execute(mAccessToken, lastUpdateTime);
                }
            }
        });
        mActualListView = mListView.getRefreshableView();
        mActualListView.setAdapter(mFeedAdapter);
        mLoadingView =  LayoutInflater.from(getActivity()).inflate(R.layout.loading_layout, null, false);
        mLoading = mLoadingView.findViewById(R.id.refresh);
        mActualListView.addFooterView(mLoadingView);
        mDefaultView = LayoutInflater.from(getActivity()).inflate(R.layout.newsfeed_default,null,false);
        dismissDefaultPage();
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
    protected void dismissDefaultPage(){
        mDefaultView.setVisibility(View.GONE);
        mActualListView.removeHeaderView(mDefaultView);
    }
    protected void showDefaultPage(){
        mActualListView.addHeaderView(mDefaultView);
        mDefaultView.setVisibility(View.VISIBLE);
        View hasBeenBtn = mDefaultView.findViewById(R.id.hasBeenBtn);
        ((TextView)hasBeenBtn.findViewById(R.id.searchText)).setTypeface(medium);
        hasBeenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeTab(1);
            }
        });
        new FbFriendsAsyncTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    final List<Follow> users = (List)msg.obj;
                    int count = users.size();
                    if(count<=0)  {
                        mDefaultView.findViewById(R.id.fbFriendsContainer).setVisibility(View.GONE);
                        return ;
                    }
                    TextView count1 = (TextView) mDefaultView.findViewById(R.id.fbFriendsCountFirst);
                    TextView count2 = (TextView) mDefaultView.findViewById(R.id.fbFriendsCountSecond);
//                    당신의 페이스북 친구 183명이 \nhasBeen을 이용중입니다.
                    count1.setText(Util.getFbCountFirst(count,0));
                    count1.setTypeface(medium);
                    count2.setText(Util.getFbCountSecond(count, 0));
                    count2.setTypeface(medium);
                    LinearLayout fbFriendsThree = (LinearLayout) mDefaultView.findViewById(R.id.fbFriendsThree);
                    List subUser = users.subList(0,(3<count ? 3 : count));
                    FbFriendsItem items = new FbFriendsItem(subUser,getActivity());
                    for(int i = 0; i<subUser.size();i++)
                        fbFriendsThree.addView(items.getView(i));
                    count2.setOnClickListener(new View.OnClickListener() {
                        boolean flag = false;

                        @Override
                        public void onClick(View v) {
                            if(!flag) {
                                flag = true;
                                Intent intent = new Intent(getActivity(), FbFriendsView.class);
                                startActivity(intent);
                                flag = false;
                            }
                        }
                    });
                }else {

                }
            }
        }).execute(mAccessToken);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.newsfeed, container, false);
        mAccessToken = Session.getString(getActivity(),"accessToken",null);
        new NewsFeedAsyncTask(handler).execute(mAccessToken);
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
                                        if(mFeeds.size()<=0)
                                            showDefaultPage();
                                        else {
                                            dismissDefaultPage();
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
                    int statusLine = (int) msg.obj;
                    if(statusLine == 401) {
                        Toast.makeText(getActivity(),"세션이 만료되었습니다 다시 로그인 해주세요.",Toast.LENGTH_LONG).show();
                        Session.remove(getActivity(),"accessToken");
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        FacebookApi.callFacebookLogout(getActivity());
                        startActivity(intent);
//                        getActivity().finish();
                    }
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
