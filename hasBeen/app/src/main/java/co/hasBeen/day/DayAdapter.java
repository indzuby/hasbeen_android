package co.hasBeen.day;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.map.EnterMapLisnter;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Position;
import co.hasBeen.photo.EnterPhotoListner;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.RecycleUtils;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;
import it.sephiroth.android.library.widget.HListView;

/**
 * Created by zuby on 2015-01-27.
 */
public class DayAdapter extends BaseAdapter {
    Context mContext;
    List<Position> mPositionList;
    List<PhotoAdapter> recycleAdapter = new ArrayList<>();
    boolean isMine;
    String mAccessToken;
    Day mDay;
    public DayAdapter(Context mContext, List mPositionList,boolean isMine,Day mDay)
    {
        this.mContext = mContext;
        this.mPositionList = mPositionList;
        this.isMine = isMine;
        this.mDay = mDay;
        mAccessToken = Session.getString(mContext,"accessToken",null);

    }
    public void recycle(){
        for(PhotoAdapter adapter : recycleAdapter)
            adapter.recycle();

    }

    @Override
    public int getCount() {
        return mPositionList.size();
    }

    @Override
    public Position getItem(int position) {
        return mPositionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent)
    {
        View view = convertView;
        Position position = getItem(index);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.day_photos, null);
        }
        TextView placeName = (TextView) view.findViewById(R.id.placeName);
        TextView placeTime = (TextView) view.findViewById(R.id.placeTime);
        TextView photoCount = (TextView) view.findViewById(R.id.photoCount);
        ImageView placeIcon = (ImageView) view.findViewById(R.id.placeIcon);
        placeTime.setText(HasBeenDate.convertTime(position.getStartTime(), position.getEndTime(),mContext));
        photoCount.setText(mContext.getString(R.string.photo_count,position.getPhotoList().size()));
        if(isMine) {
            if (index == 0)
                placeIcon.setOnClickListener(new PlaceIconClick(index, position.getId()));
            else if (index != 0)
                placeIcon.setOnClickListener(new PlaceIconClick(index, position.getId()));
        }
        if(position.getPlace()!=null) {
            placeName.setText(position.getPlace().getName());
            placeName.setOnClickListener(new EnterMapLisnter(mContext, mDay,position.getId()));
            placeTime.setOnClickListener(new EnterMapLisnter(mContext,mDay,position.getId()));
            Glide.with(mContext).load(position.getPlace().getCategoryIconPrefix() + "88" + position.getPlace().getCategoryIconSuffix()).into(placeIcon);
        }else {
            placeName.setText("Can not find the place");
        }
        HListView hListView = (HListView) view.findViewById(R.id.hListView);
        if (position.getPhotoList() != null && position.getPhotoList().size() > 0) {
            View transparentHeaderView = LayoutInflater.from(mContext).inflate(R.layout.day_hlist_header, null, false);
            hListView.addHeaderView(transparentHeaderView);

            PhotoAdapter photoAdapter = new PhotoAdapter(position.getPhotoList());
            recycleAdapter.add(photoAdapter);
            hListView.setAdapter(photoAdapter);
        }else {
            if(position.getType().equals("WALK")) {
                List<Photo> photos = new ArrayList<>();
//                photos.add(position.getMainPhoto());
                hListView.setAdapter(new PhotoAdapter(photos));
            }
        }
        return view;
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
        PlaceIconClick(int index, Long id, Long beforeid) {
            this.index = index;
            this.id = id;
            this.beforeid = beforeid;
        }
        @Override
        public void onClick(View v) {
            View.OnClickListener change = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DayChangePlace.class);
                    Gson gson = new Gson();
                    intent.putExtra("position", gson.toJson(getItem(index)));
                    ((Activity) mContext).startActivityForResult(intent,DayView.REQUEST_CODE);
                    dialog.dismiss();
                }
            };

            View.OnClickListener merge = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DayMergeAsyncTask(new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if(msg.what==0) {
                                mergePosition(index);
                                notifyDataSetChanged();
                            }else {
                                Toast.makeText(mContext,mContext.getString(R.string.common_error),Toast.LENGTH_LONG).show();
                            }
                            dialog.dismiss();
                        }
                    }).execute(mAccessToken,id);
                }
            };
            View.OnClickListener remove = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new PositionDeleteAsyncTask(new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if(msg.what==0) {
                                removePosition(index);
                                notifyDataSetChanged();
                            }else {
                                Toast.makeText(mContext,mContext.getString(R.string.remove_place_error),Toast.LENGTH_LONG).show();
                            }
                        }
                    }).execute(mAccessToken,id);
                }
            };
            if(index==0) {
                dialog = new PositionDialog(mContext,change,null,remove,true);
                dialog.show();
            }else {
                dialog = new PositionDialog(mContext, change, merge,remove);
                dialog.show();
            }
        }
    }
    protected void removePosition(int index){
        mPositionList.remove(index);
    }
    protected void mergePosition(int from) {
        Position toPosition = getItem(from-1);
        Position fromPosition = getItem(from);

        toPosition.getPhotoList().addAll(fromPosition.getPhotoList());

        int start = mDay.getPositionList().indexOf(toPosition) + 1;
        int end = mDay.getPositionList().indexOf(fromPosition);
        for(Position route : mDay.getPositionList().subList(start,end)) {
            if(route.getType().equalsIgnoreCase("direction")) // transportation type 추가 예정
                mDay.getPositionList().remove(route);
        }
        mPositionList.remove(from);
    }

    class PhotoAdapter extends BaseAdapter {
        List<Photo> mPhotoList;
        private List<WeakReference<View>> mRecycleList = new ArrayList<WeakReference<View>>();
        public void recycle() {
            for (WeakReference<View> ref : mRecycleList) {
                RecycleUtils.recursiveRecycle(ref.get());
            }
        }
        public PhotoAdapter(List<Photo> mPhotoList) {
            this.mPhotoList = mPhotoList;
        }

        @Override
        public int getCount() {
            return mPhotoList.size();
        }

        @Override
        public Photo getItem(int position) {
            return mPhotoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            Photo photo = getItem(position);
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.day_photo, null);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.photo);
            Glide.with(mContext).load(photo.getMediumUrl()).centerCrop().placeholder(Util.getPlaceHolder(photo.getId().intValue())).into(imageView);
            imageView.setOnClickListener(new EnterPhotoListner(photo.getId(),mContext));
            mRecycleList.add(new WeakReference<View>(imageView));
            return view;
        }
    }
}
