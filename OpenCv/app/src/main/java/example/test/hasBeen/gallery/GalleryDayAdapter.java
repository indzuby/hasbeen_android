package example.test.hasBeen.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.model.database.Day;
import example.test.hasBeen.model.database.Position;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.SlidingUpPanelLayout;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-14.
 */
public class GalleryDayAdapter extends BaseAdapter {
    Context mContext;
    List<Day> mGalleryList;
    DatabaseHelper database;
    MapRoute mMapRoute;
    ListView mListview;
    TextView parentDate;
    TextView parentPlace;
    SlidingUpPanelLayout parentSliding;
    public GalleryDayAdapter(Context context, List<Day> galleryList) {
        mContext = context;
        mGalleryList = galleryList;
        database = new DatabaseHelper(context);

    }
    @Override
    public int getCount() {
        return mGalleryList.size();
    }

    @Override
    public Day getItem(int position) {
        return mGalleryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){
        View view = convertView;
        final Day day = getItem(position);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_level_1_item, null);
        }

        final TextView timeView = (TextView) view.findViewById(R.id.galleryL1TextDate);
        final TextView areaView = (TextView) view.findViewById(R.id.galleryL1TextArea);
        ListView dayList = (ListView) view.findViewById(R.id.galleryL1DayView);
        try {
            List<Position> mPositionList;
            if(day.getPositions()==null) {
                mPositionList= database.selectPositionByDayId(day.getId());
                day.setPositions(mPositionList);
            }else
                mPositionList = day.getPositions();
            String str;
            if (mPositionList.size() > 1)
                str = database.selectPlaceNameByPlaceId(mPositionList.get(0).getPlaceId())
                        + " - "
                        + database.selectPlaceNameByPlaceId(mPositionList.get(mPositionList.size() - 1).getPlaceId());
            else
                str = database.selectPlaceNameByPlaceId(mPositionList.get(0).getPlaceId());
            if(str.length()>35)
                str = str.substring(0,35)+"...";
            areaView.setText(str);
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

        RelativeLayout dayBox = (RelativeLayout) view.findViewById(R.id.day_top_box);
        dayBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapRoute.createRouteDay(day.getId());
//                mListview.setSelection(position);
                parentSliding.collapsePane();
//                mListview.smoothScrollToPositionFromTop(position,0,350);
//                mListview.setSelection(position);
                List<Position> mPositionList;
                mPositionList = day.getPositions();
                String str;
                try {
                    if (mPositionList.size() > 1)
                        str = database.selectPlaceNameByPlaceId(mPositionList.get(0).getPlaceId())
                                + " - "
                                + database.selectPlaceNameByPlaceId(mPositionList.get(mPositionList.size() - 1).getPlaceId());
                    else
                        str = database.selectPlaceNameByPlaceId(mPositionList.get(0).getPlaceId());
                    if(str.length()>35)
                        str = str.substring(0,35)+"...";
                    parentDate.setText(HasBeenDate.convertDate(day.getDate()));
                    parentPlace.setText(str);
                }catch (Exception e){
                    e.printStackTrace();;
                }
            }
        });
        return view;
    }
}
