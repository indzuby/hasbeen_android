package example.test.hasBeen.gallery;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.model.HasBeenCategory;
import example.test.hasBeen.model.HasBeenPosition;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-22.
 */
public class GalleryPlaceAdapter extends BaseAdapter{
    static final int RESULT = 2;
    List<HasBeenCategory> mCategories;
    HasBeenPosition mPosition;
    Context mContext;
    DatabaseHelper database;
    public GalleryPlaceAdapter(Context context ,List<HasBeenCategory> categories,HasBeenPosition position) {
        mCategories = categories;
        mContext = context;
        mPosition = position;
        database = new DatabaseHelper(mContext);
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public HasBeenCategory getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public boolean getType(int position) {
        if(mCategories.get(position).getVenueId()!=null && mCategories.get(position).getVenueId().equals("first")) return true;
        return false;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        boolean type = getType(position);
        final HasBeenCategory category = getItem(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view==null) {
            if(type)
                view = inflater.inflate(R.layout.gallery_place_first_item, null);
            else
                view = inflater.inflate(R.layout.gallery_place_item, null);
        }

        if(type) {
            if(view!=null) {
                if(view.findViewById(R.id.place_category)!=null)
                    view = inflater.inflate(R.layout.gallery_place_first_item, null);
            }
        }else {
            if(view!=null) {
                if(view.findViewById(R.id.place_category)==null)
                view = inflater.inflate(R.layout.gallery_place_item, null);
            }
            TextView placeName = (TextView) view.findViewById(R.id.place_name);
            TextView placeCategory = (TextView) view.findViewById(R.id.place_category);
            final ImageView placeIcon = (ImageView) view.findViewById(R.id.place_icon);
            placeName.setText(category.getPlaceName());
            placeCategory.setText(category.getCategryName());
            if(category.getBitmap()==null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(category.getCategoryIconPrefix() + "88" + category.getCategoryIconSuffix());
                            final Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    category.setBitmap(bm);
                                    placeIcon.setImageBitmap(bm);
                                    placeIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }else {
                placeIcon.setImageBitmap(category.getBitmap());
                placeIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            RelativeLayout placeBox = (RelativeLayout) view.findViewById(R.id.place_box);
            placeBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePlace(category);
                    ((Activity) mContext).setResult(RESULT);
                    ((Activity)mContext).finish();
                }
            });

        }
        return view;
    }
    void updatePlace(HasBeenCategory category){
        try {
            if (database.hasVenueId(category.getVenueId())) {
                Long placeId = database.getPlaceIdByVenueId(category.getVenueId());
                mPosition.setPlaceId(placeId);
                Log.i("place id ",placeId +"");
                database.updatePosition(mPosition);
                Log.i("place id changed",database.selectPosition(mPosition.getId()).getPlaceId()+"");
                database.updatePhotosPlaceId(mPosition.getId(),placeId);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
