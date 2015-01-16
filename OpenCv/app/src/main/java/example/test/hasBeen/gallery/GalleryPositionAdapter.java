package example.test.hasBeen.gallery;

import android.app.ActionBar;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.model.HasBeenPhoto;
import example.test.hasBeen.model.HasBeenPosition;
import example.test.hasBeen.utils.HasBeenDate;

/**
 * Created by zuby on 2015-01-05.
 */
public class GalleryPositionAdapter extends BaseAdapter {
    private Context mContext;
    private List<HasBeenPosition> mPositions;
    DatabaseHelper database;
    public GalleryPositionAdapter(Context context, List positions) {
        mContext = context;
        mPositions = positions;
        database = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return mPositions.size();
    }

    @Override
    public HasBeenPosition getItem(int position) {
        return mPositions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View view = convertView;
        HasBeenPosition position = getItem(index);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_level_2_item, null);
        }
        TextView timeView = (TextView) view.findViewById(R.id.galleryL2TextTime);
        TextView areaView = (TextView) view.findViewById(R.id.galleryL2TextArea);
        GridView gridView = (GridView) view.findViewById(R.id.galleryL2GridView);
        LinearLayout line = (LinearLayout) view.findViewById(R.id.galleryL2Line);
        timeView.setText(HasBeenDate.convertTime(position.getStartDate(), position.getEndDate()));
        try{
            areaView.setText(database.selectPlaceNameByPlaceId(position.getPlaceId()));
            List<HasBeenPhoto> photos = database.selectPhotoByPositionId(position.getId());
            Log.i("photos count",photos.size()+"");
            GalleryAdapter galleryAdapter = new GalleryAdapter(mContext,photos);
            gridView.setAdapter(galleryAdapter);
            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            int width = mContext.getResources().getDisplayMetrics().widthPixels;
            Log.i("time - photo count",position.getEndDate().toString()+" "+photos.size());
            params.height = photos.size()/3 * width * 7 / 24 - 16 + (photos.size()/3-1)*4;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

}
