package example.test.hasBeen.alarm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.Alarm;

/**
 * Created by 주현 on 2015-02-03.
 */
public class AlarmFragment extends Fragment{
    View mView;
    ListView mList;
    List<Alarm> mAlarms;
    AlarmAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.alarm,container,false);
        init();
        return mView;
    }
    protected void init(){
        mList = (ListView) mView.findViewById(R.id.alarmList);
        View headerView =  LayoutInflater.from(getActivity()).inflate(R.layout.alarm_header, null, false);
        mList.addHeaderView(headerView);
        mAlarms = new ArrayList<>(5);
        for(int i = 0 ;i<5;i++) {
            mAlarms.add(new Alarm());
        }
        adapter = new AlarmAdapter(mAlarms,getActivity());
        mList.setAdapter(adapter);
    }

}
