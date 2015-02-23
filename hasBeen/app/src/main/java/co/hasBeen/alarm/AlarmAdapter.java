package co.hasBeen.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import co.hasBeen.model.api.Alarm;
import co.hasBeen.R;

/**
 * Created by 주현 on 2015-02-03.
 */
public class AlarmAdapter extends BaseAdapter {
    List<Alarm> mAlarms;
    Context mContext;

    public AlarmAdapter(List<Alarm> mAlarms, Context mContext) {
        this.mAlarms = mAlarms;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mAlarms.size();
    }

    @Override
    public Alarm getItem(int position) {
        return mAlarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view==null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.alarm_date, null);
        }
        return view;
    }
}
