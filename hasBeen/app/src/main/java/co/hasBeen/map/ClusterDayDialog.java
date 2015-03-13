package co.hasBeen.map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.day.Recommandation;
import co.hasBeen.map.pin.DayPin;
import co.hasBeen.model.api.Day;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-12.
 */
public class ClusterDayDialog extends Dialog {
    List<Day> days;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.pin_day_dialog);

        setLayout();
        PhotoAdapter adapter= new PhotoAdapter();
        listView.setAdapter(adapter);
    }

    public ClusterDayDialog(Context context, Collection<DayPin> dayPins) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        days = new ArrayList<>();
        for(DayPin day : dayPins)
            days.add(day.getDay());
    }public ClusterDayDialog(Context context, DayPin dayPin) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        days = new ArrayList<>();
        days.add(dayPin.getDay());
    }
    private void setLayout() {
//        View uploading = findViewById(R.id.refresh);
//        uploading.setVisibility(View.VISIBLE);
//        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
//        uploading.startAnimation(rotate);
        listView = (ListView) findViewById(R.id.gridView);
    }
    class PhotoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return days.size();
        }

        @Override
        public Day getItem(int position) {
            return days.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            Day day = getItem(position);
            return Recommandation.getPinView(day,getContext(),view);
        }
        public int getWidth(){
            return Util.getPhotoHeight(getContext());
        }
    }
}
