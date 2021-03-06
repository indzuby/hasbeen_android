package co.hasBeen.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.day.PositionDialog;
import co.hasBeen.map.EnterMapLisnter;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Place;
import co.hasBeen.model.api.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;
import it.sephiroth.android.library.widget.HListView;

/**
 * Created by zuby on 2015-01-05.
 */
public class GalleryPositionAdapter extends BaseAdapter{
    static final int REQUEST = 1;  // The request code
    static final int RESULT = 2;
    protected Context mContext;
    protected List<Position> mPositions;
    DataBaseHelper database;
    Long mPositionId;
    int mIndex;
    List<View> mItemList;
    Day mDay;
    public GalleryPositionAdapter(Context context, List positions,Day mDay) {
        mContext = context;
        mPositions = positions;
        database = new DataBaseHelper(context);
        mItemList = new ArrayList<>();
        this.mDay =mDay;
    }

    @Override
    public int getCount() {
        return mPositions.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Position getItem(int position) {
        return mPositions.get(position);
    }

    @Override
    public View getView(int index,View convertView,ViewGroup parent) {
        View view = convertView;
        Position position = getItem(index);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_position_item, null);
        }
        TextView placeTime = (TextView) view.findViewById(R.id.placeTime);
        TextView placeName = (TextView) view.findViewById(R.id.placeName);
        TextView photoCount = (TextView) view.findViewById(R.id.photoCount);
        HListView hListView = (HListView) view.findViewById(R.id.hListView);
        ImageView categoryIcon = (ImageView) view.findViewById(R.id.placeIcon);
        if (index == 0)
            categoryIcon.setOnClickListener(new PlaceIconClick(index, position.getId()));
        else if (index != 0)
            categoryIcon.setOnClickListener(new PlaceIconClick(index, position.getId()));
        categoryIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        placeTime.setText(HasBeenDate.convertTime(position.getStartTime(), position.getEndTime(),mContext));

        try{
            Place place = database.selectPlace(position.getPlaceId());
            Glide.with(mContext).load(place.getCategoryIconPrefix() + "88" + place.getCategoryIconSuffix()).into(categoryIcon);
            placeName.setText(place.getName());
            placeName.setOnClickListener(new EnterMapLisnter(mContext, mDay, position.getId()));
            placeTime.setOnClickListener(new EnterMapLisnter(mContext, mDay, position.getId()));
            List<Photo> photos = position.getPhotoList();
            if(photos!=null) {
//            gridView.getLayoutParams().height = getHeight(photos.size());
                GalleryAdapter galleryAdapter = new GalleryAdapter(mContext, photos);
                if(hListView.getHeaderViewsCount()==0) {
                    View transparendView = LayoutInflater.from(mContext).inflate(R.layout.transparent_header_view, null, false);
                    hListView.addHeaderView(transparendView);
                }
                hListView.setAdapter(galleryAdapter);
            }
            photoCount.setText(mContext.getString(R.string.photo_count,photos.size()));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    protected int getHeight(int photoCount) {
        int count = (int)Math.ceil((float)photoCount/3);
        return Util.getPhotoHeight(mContext)*count;
    }
    class PlaceIconClick implements View.OnClickListener {
        int index;
        Long id;
        Long beforeid;
        PositionDialog dialog;
        PlaceIconClick(int index, Long id) {
            this.index = index;
            this.id = id;
        }
        @Override
        public void onClick(View v) {
            View.OnClickListener change = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GalleryPlace.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", id);
                    intent.putExtra("index",index);
                    mIndex = index;
                    Session.putBoolean(mContext, "placeChange", false);
                    mContext.startActivity(intent);
                    startCallBack(index);
                    dialog.dismiss();
                }
            };
            if(index==0) {
                dialog = new PositionDialog(mContext,change,null,null,true);
                dialog.show();
            }else {
                View.OnClickListener merge = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            mergePosition(index);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                };
                dialog = new PositionDialog(mContext, change, merge,null);
                dialog.show();
            }
        }
    }
    protected void mergePosition(int from) throws Exception{
        Position toPosition = getItem(from-1);
        Position fromPosition = getItem(from);
        database.updatePhotosPlaceId(fromPosition.getId(),toPosition.getPlaceId());
        database.mergePosition(toPosition.getId(),fromPosition.getId());
        toPosition.getPhotoList().addAll(fromPosition.getPhotoList());
        mPositions.remove(from);
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
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
