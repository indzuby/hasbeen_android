package example.test.hasBeen.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.model.HasBeenDay;
import example.test.hasBeen.model.HasBeenPosition;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-14.
 */
public class GalleryDayAdapter extends BaseAdapter {
    Context mContext;
    List<HasBeenDay> mGalleryList;
    DatabaseHelper database;
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
            view = inflater.inflate(R.layout.gallery_level_1_item, null);
        }

        TextView timeView = (TextView) view.findViewById(R.id.galleryL1TextDate);
        TextView areaView = (TextView) view.findViewById(R.id.galleryL1TextArea);
        ListView dayList = (ListView) view.findViewById(R.id.galleryL1DayView);
        try {
            List<HasBeenPosition> mPositionList;
            if(day.getPositions()==null) {
                mPositionList= database.selectPositionByDayId(day.getId());
                day.setPositions(mPositionList);
            }else
                mPositionList = day.getPositions();

            if (mPositionList.size() > 1)
                areaView.setText(database.selectPlaceNameByPlaceId(mPositionList.get(0).getPlaceId())
                        + " - "
                        + database.selectPlaceNameByPlaceId(mPositionList.get(mPositionList.size() - 1).getPlaceId()));
            else
                areaView.setText(database.selectPlaceNameByPlaceId(mPositionList.get(0).getPlaceId()));
            int width = mContext.getResources().getDisplayMetrics().widthPixels;
            int photoCnt = database.selectPhotoByDayid(day.getId());

            GalleryPositionAdapter positionAdapter;
            if(day.getPositionAdapter()==null) {
                positionAdapter = new GalleryPositionAdapter(mContext, mPositionList);
                day.setPositionAdapter(positionAdapter);
            }else
                positionAdapter = day.getPositionAdapter();
            ViewGroup.LayoutParams params = dayList.getLayoutParams();
            dayList.setAdapter(positionAdapter);
            params.height = mPositionList.size() * Util.pxFromDp(mContext, 72) + (photoCnt) * (width * 4 / 15);
            if (position == getCount() - 1) {
                params.height = mContext.getResources().getDisplayMetrics().heightPixels - Util.pxFromDp(mContext, 96);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        timeView.setText(HasBeenDate.convertDate(day.getDate()));
        mGalleryList.get(position).setArea(areaView.getText().toString());
        return view;
    }
}
