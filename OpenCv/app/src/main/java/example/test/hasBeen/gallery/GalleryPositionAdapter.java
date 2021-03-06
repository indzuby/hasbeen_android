package example.test.hasBeen.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.model.database.Place;
import example.test.hasBeen.model.database.Position;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-05.
 */
public class GalleryPositionAdapter extends BaseAdapter {
    static final int REQUEST = 1;  // The request code
    static final int RESULT = 2;
    protected Context mContext;
    protected List<Position> mPositions;
    DatabaseHelper database;
    Long mPositionId;
    int mIndex;
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
    public Position getItem(int position) {
        return mPositions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View view = convertView;
        Position position = getItem(index);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_level_2_item, null);
        }
        TextView timeView = (TextView) view.findViewById(R.id.placeTime);
        TextView areaView = (TextView) view.findViewById(R.id.placeName);
        GridView gridView = (GridView) view.findViewById(R.id.galleryL2GridView);
//        LinearLayout line = (LinearLayout) view.findViewById(R.id.galleryL2Line);
        ImageView categoryIcon = (ImageView) view.findViewById(R.id.placeIcon);
        categoryIcon.setOnClickListener(new CategoryListner(index,position));
        categoryIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        timeView.setText(HasBeenDate.convertTime(position.getStartTime(), position.getEndTime()));

        try{
            Place place = database.selectPlace(position.getPlaceId());
            Glide.with(mContext).load(place.getCategoryIconPrefix() + "88" + place.getCategoryIconSuffix()).into(categoryIcon);
            areaView.setText(place.getName());
            List<Photo> photos = database.selectPhotoByPositionId(position.getId());
            GalleryAdapter galleryAdapter = new GalleryAdapter(mContext,photos);
            gridView.setAdapter(galleryAdapter);
//            ViewGroup.LayoutParams params = gridView.getLayoutParams();
//            int width = mContext.getResources().getDisplayMetrics().widthPixels;
//            int k = (int)Math.ceil(photos.size()/3.0);
//            int px = Util.pxFromDp(mContext, 2);
//            int padding = Util.pxFromDp(mContext,10);
////            params.height = k * (width * 4 / 15) + (k-1)*px+padding+2;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    class CategoryListner implements View.OnClickListener{
        int index;
        Position position;
        CategoryListner(int index, Position position) {
            this.index = index;
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, GalleryPlace.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("positionId", position.getId());
            intent.putExtra("index",index);
            mPositionId = position.getId();
            mIndex = index;
            Activity activity = (Activity) mContext;
            Session.putBoolean(mContext, "placeChange", false);
            activity.startActivityForResult(intent,REQUEST);
            startCallBack(index);

        }
    }


    void startCallBack(final int index) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag=false;
                do{
                    flag = Session.getBoolean(mContext,"placeChange",false);
                    if(flag) {
                        try {

                            mPositions.set(index, database.selectPosition(mPositions.get(index).getId()));
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }while(!flag);
            }
        }).start();
    }
}
