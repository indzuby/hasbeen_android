package co.hasBeen.gallery;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.database.Photo;
import co.hasBeen.model.database.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-21.
 */
public class GalleryShareAdapter extends GalleryPositionAdapter {
    Boolean[] isCheckedPosition;
    int[] mCheckedCount;
    public GalleryShareAdapter(Context context, List positions, Boolean[] isCheckedPosition) {
        super(context, positions);
        this.isCheckedPosition = isCheckedPosition;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Position getItem(int position) {
        return super.getItem(position);
    }

    public void setChecked(boolean value, int position) {
        isCheckedPosition[position] = value;
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View view = convertView;
        Position position = getItem(index);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_share_item, null);
        }
        TextView timeView = (TextView) view.findViewById(R.id.placeTime);
        TextView areaView = (TextView) view.findViewById(R.id.placeName);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);

        timeView.setText(HasBeenDate.convertTime(position.getStartTime(), position.getEndTime()));
        try{
            areaView.setText(database.selectPlaceNameByPlaceId(position.getPlaceId()));
            List<Photo> photos = position.getPhotoList();
            GallerySharePhotoAdapter galleryAdapter = new GallerySharePhotoAdapter(mContext,photos,position.getIsCheckedPhoto());
            gridView.setAdapter(galleryAdapter);
            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            int width = mContext.getResources().getDisplayMetrics().widthPixels;
            int k = (int)Math.ceil(photos.size()/3.0);
            params.height = k * (width * 4 / 15) + (k-1)* Util.pxFromDp(mContext, 2);
            ToggleButton checkedButton = (ToggleButton) view.findViewById(R.id.checkedAll);

            checkedButton.setOnClickListener(new CheckedListner(galleryAdapter,index));
            checkedButton.setOnCheckedChangeListener(new CheckedListner(galleryAdapter,index));
            galleryAdapter.mCheckedAllButton = checkedButton;
            if(isCheckedPosition[index]) {
                checkedButton.setChecked(true);
                galleryAdapter.mCheckedAll = true;
            }
            else {
                checkedButton.setChecked(false);
                galleryAdapter.mCheckedAll = false;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        if(index+1 == getCount())
            view.findViewById(R.id.bottomLine).setVisibility(View.GONE);
        return view;
    }
    class CheckedListner implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        GallerySharePhotoAdapter galleryAdapter;
        int index;
        public CheckedListner(GallerySharePhotoAdapter galleryAdapter ,int index) {
            this.galleryAdapter = galleryAdapter;
            this.index = index;
        }
        @Override
        public void onClick(View v) {

            galleryAdapter.setChecked();
            Log.i("checked all",galleryAdapter.mCheckedAll+"");
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                isCheckedPosition[index] = true;
            }else {
                isCheckedPosition[index] = false;
            }
        }
    }

}
