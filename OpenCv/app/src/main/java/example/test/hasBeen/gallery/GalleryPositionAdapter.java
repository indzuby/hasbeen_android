package example.test.hasBeen.gallery;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.net.URL;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.model.HasBeenPhoto;
import example.test.hasBeen.model.HasBeenPlace;
import example.test.hasBeen.model.HasBeenPosition;
import example.test.hasBeen.utils.HasBeenDate;

/**
 * Created by zuby on 2015-01-05.
 */
public class GalleryPositionAdapter extends BaseAdapter {
    static final int REQUEST = 1;  // The request code
    static final int RESULT = 2;
    protected Context mContext;
    protected List<HasBeenPosition> mPositions;
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
    public HasBeenPosition getItem(int position) {
        return mPositions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
        View view = convertView;
        final HasBeenPosition position = getItem(index);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_level_2_item, null);
        }
        TextView timeView = (TextView) view.findViewById(R.id.galleryL2TextTime);
        TextView areaView = (TextView) view.findViewById(R.id.galleryL2TextArea);
        GridView gridView = (GridView) view.findViewById(R.id.galleryL2GridView);
//        LinearLayout line = (LinearLayout) view.findViewById(R.id.galleryL2Line);
        final ImageView categoryIcon = (ImageView) view.findViewById(R.id.place_category);
        categoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GalleryPlace.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("positionId", position.getId());
                mPositionId = position.getId();
                mIndex = index;
                Activity activity = (Activity) mContext;
                activity.startActivityForResult(intent,REQUEST);

            }
        });
        categoryIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        timeView.setText(HasBeenDate.convertTime(position.getStartDate(), position.getEndDate()));

        try{
            HasBeenPlace place_tmp;
            if(position.getPlace()==null) {
                place_tmp = database.selectPlace(position.getPlaceId());
                position.setPlace(place_tmp);
            }else
                place_tmp = position.getPlace();
            final HasBeenPlace place = place_tmp;
            if(place.getBitmap()==null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(place.getCategoryIconPrefix() + "88" + place.getCategoryIconSuffix());
                            final Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    place.setBitmap(bm);
                                    categoryIcon.setImageBitmap(bm);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }else
                categoryIcon.setImageBitmap(place.getBitmap());
            areaView.setText(place.getName());
            List<HasBeenPhoto> photos;
            if(position.getPhotos()==null) {
                photos = database.selectPhotoByPositionId(position.getId());
                position.setPhotos(photos);
            }else
                photos = position.getPhotos();
            GalleryAdapter galleryAdapter;
            if(position.getGalleryAdapter()==null) {
                galleryAdapter = new GalleryAdapter(mContext,photos);
                position.setGalleryAdapter(galleryAdapter);
            }else
                galleryAdapter = position.getGalleryAdapter();
            gridView.setAdapter(galleryAdapter);
            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            int width = mContext.getResources().getDisplayMetrics().widthPixels;
            int k = (int)Math.ceil(photos.size()/3);
            params.height = k * (width * 4 / 15) + (k-1)*4;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

}
