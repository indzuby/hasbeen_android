package example.test.hasBeen.gallery;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.model.database.Position;
import example.test.hasBeen.utils.HasBeenDate;

/**
 * Created by zuby on 2015-01-21.
 */
public class GalleryShareAdapter extends GalleryPositionAdapter {
    public GalleryShareAdapter(Context context, List positions) {
        super(context, positions);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Position getItem(int position) {
        return super.getItem(position);
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
        TextView timeView = (TextView) view.findViewById(R.id.galleryShareTextTime);
        TextView areaView = (TextView) view.findViewById(R.id.area);
        GridView gridView = (GridView) view.findViewById(R.id.galleryShareGridView);

        timeView.setText(HasBeenDate.convertTime(position.getStartDate(), position.getEndDate()));
        try{
            areaView.setText(database.selectPlaceNameByPlaceId(position.getPlaceId()));
            List<Photo> photos = database.selectPhotoByPositionId(position.getId());
            final GallerySharePhotoAdapter galleryAdapter = new GallerySharePhotoAdapter(mContext,photos);
            gridView.setAdapter(galleryAdapter);
            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            int width = mContext.getResources().getDisplayMetrics().widthPixels;
            int k = (int)Math.ceil(photos.size()/3);
            params.height = k * (width * 4 / 15) + (k-1)*4;
            ToggleButton checkedButton = (ToggleButton) view.findViewById(R.id.galleryShareCheckedAll);
            checkedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    galleryAdapter.setChecked();
                    Log.i("checked all",galleryAdapter.mCheckedAll+"");
                }
            });
            galleryAdapter.mCheckedAllButton = checkedButton;
//            ImageButton checkedButton = (ImageButton) view.findViewById(R.id.galleryShareCheckedAll);
//            checkedButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    galleryAdapter.setmChecked();
//                    Log.i("checked all",galleryAdapter.mCheckedAll+"");
//                }
//            });
        }catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
