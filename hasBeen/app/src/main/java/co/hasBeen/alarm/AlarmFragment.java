package co.hasBeen.alarm;

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
    int mTab = NEWS;
    View mView;
    PullToRefreshListView mNewList,mYouList;
    List<Alarm> mAlarmsNews;
    List<Alarm> mAlarmsYou;
    AlarmAdapter mNewAdapter,mYouAdapter;
    public AlarmCount mAlarmCount;
    ImageView mNewsRedDot,mYouRedDot;
    String mAccessToken;
    View mNewLoadingView;
    View mYouLoadingView;
    boolean isLoad = true;
    boolean isNewLoad = false;
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
                if(mNewList.isRefreshing() || mYouList.isRefreshing() || isNewLoad) {
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
                    redDotRefresh();
                }
            });
            isNewLoad = false;
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
                if(isLoad)
                    stopLoading();
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
                if(isLoad)
                    stopLoading();
            }
        }
    };
    protected void init(){
        mNewList = (PullToRefreshListView) mView.findViewById(R.id.alarmNewList);
        mYouList = (PullToRefreshListView) mView.findViewById(R.id.alarmYouList);
        mNewLoadingView =  LayoutInflater.from(getActivity()).inflate(R.layout.loading_layout, null, false);
        mYouLoadingView =  LayoutInflater.from(getActivity()).inflate(R.layout.loading_layout, null, false);
        mNewsRedDot = (ImageView) mView.findViewById(R.id.newsRedDot);
        mYouRedDot = (ImageView) mView.findViewById(R.id.youRedDot);
        mAlarmsNews = new ArrayList<>();
        mAlarmsYou = new ArrayList<>();
        mNewAdapter = new AlarmAdapter(mAlarmsNews,getActivity(),NEWS);
//        new AlarmListAsyncTask(alarmNewsHandler).execute(mAccessToken,AlarmListAsyncTask.CATEGORY_NEWS);
        mNewList.getRefreshableView().setAdapter(mNewAdapter);
        mYouAdapter = new AlarmAdapter(mAlarmsYou,getActivity(),YOU);
        mYouList.getRefreshableView().setAdapter(mYouAdapter);
        View newButton = mView.findViewById(R.id.alarmNews);
        View youButton = mView.findViewById(R.id.alarmYou);
        newButton.setOnClickListener(this);
        youButton.setOnClickListener(this);
        mNewList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getNewAlarmNews();
                mAlarmCount.setNewsCount(0);
            }
        });
        mYouList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getNewAlarmYou();
                mAlarmCount.setYouCount(0);
            }
        });
        mNewList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if(!isLoad) {
                    isLoad = true;
                    startLoading();
                    getOldAlarmNews();
                }
            }
        });
        mYouList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if(!isLoad) {
                    isLoad = true;
                    startLoading();
                    getOldAlarmYou();
                }
            }
        });
    }
    protected void startLoading(){
        isLoad = true;
        View loading;
        loading = mView.findViewById(R.id.refresh);
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        loading.startAnimation(rotate);
    }
    protected void stopLoading(){
        isLoad = false;
        View loading = mView.findViewById(R.id.refresh);
        loading.setVisibility(View.GONE);
        loading.clearAnimation();
    }
    @Override
    public void onClick(View v) {
        clearSelect();

        if(v.getId()==R.id.alarmNews){
            ((TextView)v.findViewById(R.id.news)).setTextColor(getResources().getColor(R.color.theme_color));
            selectTab(NEWS);
            mAlarmCount.setNewsCount(0);
            redDotRefresh();
            if(mAlarmsNews.size()<=0)
                new AlarmListAsyncTask(alarmNewsHandler).execute(mAccessToken,AlarmListAsyncTask.CATEGORY_NEWS);
        }else {
            ((TextView)v.findViewById(R.id.you)).setTextColor(getResources().getColor(R.color.theme_color));
            selectTab(YOU);
            mAlarmCount.setYouCount(0);
            redDotRefresh();
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
            if(mAlarmsNews.size()>0)
                getNewAlarmNews();
            mTab = NEWS;
        }else {
            mYouList.setVisibility(View.VISIBLE);
            mNewList.setVisibility(View.GONE);
            if(mAlarmsYou.size()>0) getNewAlarmYou();
            mTab = YOU;
        }
    }
    public void getNewAlarmNews(){
        if(!mNewList.isRefreshing()) startLoading();
        if(mAlarmsNews.size()>0) {
            isNewLoad = true;
            new AlarmListAsyncTask(alarmNewsHandler).execute(mAccessToken, AlarmListAsyncTask.CATEGORY_NEWS, "", mAlarmsNews.get(0).getId());
        }
        else
            new AlarmListAsyncTask(alarmNewsHandler).execute(mAccessToken, AlarmListAsyncTask.CATEGORY_NEWS);
    }
    public void getNewAlarmYou(){
        if(!mYouList.isRefreshing()) startLoading();
        if(mAlarmsYou.size()>0) {
            isNewLoad = true;
            new AlarmListAsyncTask(alarmYouHandler).execute(mAccessToken, AlarmListAsyncTask.CATEGORY_YOU, "", mAlarmsYou.get(0).getId());
        }
        else
            new AlarmListAsyncTask(alarmYouHandler).execute(mAccessToken, AlarmListAsyncTask.CATEGORY_YOU);
    }
    public void getOldAlarmNews(){
        startLoading();
        new AlarmListAsyncTask(alarmNewsHandler).execute(mAccessToken,AlarmListAsyncTask.CATEGORY_NEWS,mAlarmsNews.get(mAlarmsNews.size()-1).getId());
    }
    public void getOldAlarmYou(){
        startLoading();
        new AlarmListAsyncTask(alarmYouHandler).execute(mAccessToken,AlarmListAsyncTask.CATEGORY_YOU,mAlarmsYou.get(mAlarmsYou.size()-1).getId());
    }
    public void newAlarmLoad(AlarmCount count){
        mAlarmCount = count;
        if(mTab == NEWS) {
            mAlarmCount.setNewsCount(0);
            getNewAlarmNews();
        }else {
            mAlarmCount.setYouCount(0);
            getNewAlarmYou();
        }
    }
    public void redDotRefresh(){
        mNewsRedDot.setVisibility(View.VISIBLE);
        mYouRedDot.setVisibility(View.VISIBLE);
        if(mAlarmCount.getNewsCount()==0)
            mNewsRedDot.setVisibility(View.GONE);
        if(mAlarmCount.getYouCount()==0)
            mYouRedDot.setVisibility(View.GONE);
    }
}
