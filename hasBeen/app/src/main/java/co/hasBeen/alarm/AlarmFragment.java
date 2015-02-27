package co.hasBeen.alarm;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Alarm;
import co.hasBeen.model.api.AlarmCount;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-03.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener{
    final static int NEWS = 0;
    final static int YOU = 1;
    View mView;
    PullToRefreshListView mNewList,mYouList;
    List<Alarm> mAlarmsNews;
    List<Alarm> mAlarmsYou;
    AlarmAdapter mNewAdapter,mYouAdapter;
    public AlarmCount mAlarmCount;
    ImageView mNewsRedDot,mYouRedDot;
    String mAccessToken;
    Long lastNewAlarmId;
    Long lastYouAlarmId;
    Typeface medium,regular;
    View mNewLoadingView;
    View mYouLoadingView;
    boolean isLoad = true;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.alarm,container,false);
        mAccessToken = Session.getString(getActivity(),"accessToken",null);
        init();
        return mView;
    }
    class RefreshThread extends Thread {
        List<Alarm> newAlarms;
        List<Alarm> alarms;
        AlarmAdapter adapter;
        RefreshThread(List<Alarm> newAlarms, List<Alarm> alarms,AlarmAdapter adapter) {
            this.newAlarms = newAlarms;
            this.alarms = alarms;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            for(Alarm alarm : newAlarms) {
                if(mNewList.isRefreshing() || mYouList.isRefreshing()) {
                    alarms.add(0, alarm);
                }
                else
                    alarms.add(alarm);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    if(mNewList.isRefreshing()) mNewList.onRefreshComplete();
                    if(mYouList.isRefreshing()) mYouList.onRefreshComplete();
                }
            });
        }
    }
    Handler alarmNewsHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0) {
                List<Alarm> alarms = (List) msg.obj;
                if(mNewList.isRefreshing()) {
                    Collections.reverse(alarms);
                }
                new RefreshThread(alarms,mAlarmsNews,mNewAdapter).start();
                if(isLoad) {
                    isLoad = false;
                    stopLoading(NEWS);
                }
            }
        }
    };
    Handler alarmYouHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0) {
                List<Alarm> alarms = (List) msg.obj;
                if(mYouList.isRefreshing()) {
                    Collections.reverse(alarms);
                }
                new RefreshThread(alarms,mAlarmsYou,mYouAdapter).start();
                if(isLoad) {
                    isLoad = false;
                    stopLoading(YOU);
                }

            }
        }
    };
    protected void init(){
        medium = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Medium.ttf");
        regular = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Regular.ttf");
        mNewList = (PullToRefreshListView) mView.findViewById(R.id.alarmNewList);
        mYouList = (PullToRefreshListView) mView.findViewById(R.id.alarmYouList);
        mNewLoadingView =  LayoutInflater.from(getActivity()).inflate(R.layout.newsfeed_loading, null, false);
        mYouLoadingView =  LayoutInflater.from(getActivity()).inflate(R.layout.newsfeed_loading, null, false);
        mNewsRedDot = (ImageView) mView.findViewById(R.id.newsRedDot);
        mYouRedDot = (ImageView) mView.findViewById(R.id.youRedDot);
        mAlarmsNews = new ArrayList<>();
        mAlarmsYou = new ArrayList<>();
        mNewAdapter = new AlarmAdapter(mAlarmsNews,getActivity());
        new AlarmListAsyncTask(alarmNewsHandler).execute(mAccessToken,AlarmListAsyncTask.CATEGORY_NEWS);
        mNewList.getRefreshableView().addFooterView(mNewLoadingView);
        mNewList.getRefreshableView().setAdapter(mNewAdapter);
        mYouAdapter = new AlarmAdapter(mAlarmsYou,getActivity());
        mYouList.getRefreshableView().addFooterView(mYouLoadingView);
        mYouList.getRefreshableView().setAdapter(mYouAdapter);
        View newButton = mView.findViewById(R.id.alarmNews);
        View youButton = mView.findViewById(R.id.alarmYou);
        newButton.setOnClickListener(this);
        youButton.setOnClickListener(this);
        ((TextView)mView.findViewById(R.id.you)).setTypeface(medium);
        ((TextView)mView.findViewById(R.id.news)).setTypeface(medium)r;
        mNewList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getNewAlarmNews();
                mAlarmCount.setNewsCount(0);
                redDotRefresh(mAlarmCount);
            }
        });
        mYouList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getNewAlarmYou();
                mAlarmCount.setYouCount(0);
                redDotRefresh(mAlarmCount);
            }
        });
        mNewList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if(!isLoad) {
                    isLoad = true;
                    startLoading(NEWS);
                    getOldAlarmNews();
                }
            }
        });
        mYouList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if(!isLoad) {
                    isLoad = true;
                    startLoading(YOU);
                    getOldAlarmYou();
                }
            }
        });
    }
    protected void startLoading(int index){
        View loading;
        View view ;
        if(index==NEWS)
            view = mNewLoadingView;
        else
            view = mYouLoadingView;
        view.setVisibility(View.VISIBLE);
        loading = view.findViewById(R.id.refresh);
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        loading.startAnimation(rotate);
    }
    protected void stopLoading(int index){
        View view;
        if(index==NEWS)
            view = mNewLoadingView;
        else
            view = mYouLoadingView;
        View loading = view.findViewById(R.id.refresh);
        view.setVisibility(View.GONE);
        loading.clearAnimation();
    }
    @Override
    public void onClick(View v) {
        clearSelect();

        if(v.getId()==R.id.alarmNews){
            ((TextView)v.findViewById(R.id.news)).setTextColor(getResources().getColor(R.color.theme_color));
            selectTab(NEWS);
            mAlarmCount.setNewsCount(0);
            redDotRefresh(mAlarmCount);
        }else {
            ((TextView)v.findViewById(R.id.you)).setTextColor(getResources().getColor(R.color.theme_color));
            selectTab(YOU);
            mAlarmCount.setYouCount(0);
            redDotRefresh(mAlarmCount);
            if(mAlarmsYou.size()<=0)
                new AlarmListAsyncTask(alarmYouHandler).execute(mAccessToken,AlarmListAsyncTask.CATEGORY_YOU);
        }
    }
    protected void clearSelect(){
        ((TextView)mView.findViewById(R.id.news)).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView)mView.findViewById(R.id.you)).setTextColor(getResources().getColor(R.color.light_gray));
    }
    public void selectTab(int index){
        if(index == NEWS) {
            mNewList.setVisibility(View.VISIBLE);
            mYouList.setVisibility(View.GONE);
            if(mAlarmsNews.size()>0)getNewAlarmNews();
        }else {
            mYouList.setVisibility(View.VISIBLE);
            mNewList.setVisibility(View.GONE);
            if(mAlarmsYou.size()>0) getNewAlarmYou();
        }
    }
    public void getNewAlarmNews(){
        new AlarmListAsyncTask(alarmNewsHandler).execute(mAccessToken, AlarmListAsyncTask.CATEGORY_NEWS, "", mAlarmsNews.get(0).getId());
    }
    public void getNewAlarmYou(){
        new AlarmListAsyncTask(alarmYouHandler).execute(mAccessToken,AlarmListAsyncTask.CATEGORY_YOU,"",mAlarmsYou.get(0).getId());
    }
    public void getOldAlarmNews(){
        new AlarmListAsyncTask(alarmNewsHandler).execute(mAccessToken,AlarmListAsyncTask.CATEGORY_NEWS,mAlarmsNews.get(mAlarmsNews.size()-1).getId());
    }
    public void getOldAlarmYou(){
        new AlarmListAsyncTask(alarmYouHandler).execute(mAccessToken,AlarmListAsyncTask.CATEGORY_YOU,mAlarmsYou.get(mAlarmsYou.size()-1).getId());
    }
    public void redDotRefresh(AlarmCount count){
        mAlarmCount = count;
        mNewsRedDot.setVisibility(View.VISIBLE);
        mYouRedDot.setVisibility(View.VISIBLE);
        if(mAlarmCount.getNewsCount()==0)
            mNewsRedDot.setVisibility(View.GONE);
        if(mAlarmCount.getYouCount()==0)
            mYouRedDot.setVisibility(View.GONE);
    }
}
