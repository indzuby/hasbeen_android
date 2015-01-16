package example.test.hasBeen.gallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.model.HasBeenDay;
import example.test.hasBeen.model.HasBeenPosition;
import example.test.hasBeen.utils.ExpandedListView;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-14.
 */
public class GalleryDayAdapter extends BaseAdapter {
    Context mContext;
    List<HasBeenDay> mGalleryList;
    DatabaseHelper database;
    ProgressDialog dialog;
    public GalleryDayAdapter(Context context, List<HasBeenDay> galleryList) {
        mContext = context;
        mGalleryList = galleryList;
        database = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return mGalleryList.size();
    }

    @Override
    public HasBeenDay getItem(int position) {
        return mGalleryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        HasBeenDay day = getItem(position);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_level1_item, null);
        }

        TextView timeView = (TextView) view.findViewById(R.id.galleryL1TextDate);
        TextView areaView = (TextView) view.findViewById(R.id.galleryL1TextArea);
        ListView dayList = (ListView) view.findViewById(R.id.galleryL1DayView);
        try {
            List<HasBeenPosition> mPositionList = database.selectPositionByDayId(day.getId());
            Log.i("position size", mPositionList.size() + "");
            if(mPositionList.size()>1)
                areaView.setText(database.selectPlaceNameByPlaceId(mPositionList.get(0).getPlaceId())
                        +" - "
                        + database.selectPlaceNameByPlaceId(mPositionList.get(mPositionList.size()-1).getPlaceId()));
            else
                areaView.setText(database.selectPlaceNameByPlaceId(mPositionList.get(0).getPlaceId()));
            GalleryPositionAdapter positionAdapter = new GalleryPositionAdapter(mContext,mPositionList);
            ViewGroup.LayoutParams params =  dayList.getLayoutParams();
            int width = mContext.getResources().getDisplayMetrics().widthPixels;
            int photoCnt = database.selectPhotoByDayid(day.getId());
            Log.i("Day id - photo count",photoCnt+" ");
            params.height = mPositionList.size() * Util.pxFromDp(mContext,56) + (photoCnt) * (width * 7 / 24 - 12) + (photoCnt-1)*4;
//            dayList.setLayoutParams(params);

            Log.i("Date's height",HasBeenDate.convertDate(day.getDate())+" "+params.height);
            dayList.setAdapter(positionAdapter);

        }catch (Exception e) {
            e.printStackTrace();
        }

        timeView.setText(HasBeenDate.convertDate(day.getDate()));
        return view;
    }
}
