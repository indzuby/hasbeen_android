package co.hasBeen.alarm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Alarm;
import co.hasBeen.model.api.AlarmCount;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-03.
 */
public class AlarmFragment extends Fragment{
    View mView;
    ListView mList;
    List<Alarm> mAlarmsNews;
    List<Alarm> mAlarmsYou;
    AlarmAdapter adapter;
    public AlarmCount mAlarmCount;
    ImageView mNewsRedDot,mYouRedDot;
    String mAccessToken;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.alarm,container,false);
        mAccessToken = Session.getString(getActivity(),"accessToken",null);
        init();
        return mView;
    }
    Handler alarmNewsHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0) {

            }
        }
    };
    protected void init(){
        mList = (ListView) mView.findViewById(R.id.alarmList);
        View headerView =  LayoutInflater.from(getActivity()).inflate(R.layout.alarm_header, null, false);
        mList.addHeaderView(headerView);
        mNewsRedDot = (ImageView) headerView.findViewById(R.id.newsRedDot);
        mYouRedDot = (ImageView) headerView.findViewById(R.id.youRedDot);
        new AlarmListAsyncTask(alarmNewsHandler).execute(mAccessToken,AlarmListAsyncTask.CATEGORY_NEWS);
        mAlarmsNews = new ArrayList<>();
        mAlarmsYou = new ArrayList<>();
        adapter = new AlarmAdapter(mAlarmsNews,getActivity());
        mList.setAdapter(adapter);

    }
    public void redDotRefresh(AlarmCount count){
        mAlarmCount = count;
        if(mAlarmCount.getNewsCount()==0)
            mNewsRedDot.setVisibility(View.GONE);
        if(mAlarmCount.getYouCount()==0)
            mYouRedDot.setVisibility(View.GONE);
    }
}
