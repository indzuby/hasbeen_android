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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.database.ItemModule;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-14.
 */
public class GalleryDayAdapter extends BaseAdapter {
    Context mContext;
    List<Day> mGalleryList;
    DatabaseHelper database;
    ItemModule itemModule;
    public GalleryDayAdapter(Context context, List<Day> galleryList) throws Exception{
        mContext = context;
        mGalleryList = galleryList;
        database = new DatabaseHelper(context);
        itemModule = new ItemModule(context);
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
    public View getView(int position, View convertView, final ViewGroup parent){
        View view = convertView;
        Day day = getItem(position);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_item, null);
        }
        ImageView mainPhoto= (ImageView) view.findViewById(R.id.mainPhoto);
        ImageView upload= (ImageView) view.findViewById(R.id.upload);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView placeName = (TextView) view.findViewById(R.id.placeName);
        TextView dayStatus = (TextView) view.findViewById(R.id.dayStatus);

        try {
            date.setText(HasBeenDate.convertDate(day.getDate()));
            if(!database.hasDay(day.getId())) {
                mGalleryList.remove(position);
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
                return view;
            }
            if(!database.hasPhotoId(day.getMainPhotoId()))
                day = database.selectDay(day.getId());
            day.setMainPhoto(database.selectPhoto(day.getMainPhotoId()));
            Glide.with(mContext).load(day.getMainPhoto().getPhotoPath()).placeholder(Util.getPlaceHolder(position)).into(mainPhoto);
            initPlaceName(placeName,day);
            initDayStatus(dayStatus,day);
            mainPhoto.setOnClickListener(new EnterGalleryDayViewListner(day));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    class EnterGalleryDayViewListner implements View.OnClickListener {
        Day day;

        EnterGalleryDayViewListner(Day day) {
            this.day = day;
        }
        @Override
        public void onClick(View v) {
            if(day.getMainPlaceId()!=null || day.getMainPlace()!=null) {
                Intent intent = new Intent(mContext, GalleryDayView.class);
                intent.putExtra("id", day.getId());
                mContext.startActivity(intent);
            }else {
                Toast.makeText(mContext,mContext.getString(R.string.location_loading_text),Toast.LENGTH_LONG).show();
            }
        }
    }
    protected void initPlaceName(TextView placeName ,Day day) throws Exception{
        if (day.getMainPlaceId()==null && day.getMainPlace()==null) {
            placeName.setText("Loading...");
            new PlaceNameThread(placeName,day).start();
        } else if(day.getMainPlaceId()!=null){
            List<Position> positions = database.selectPositionByDayId(day.getId());
            initPlace(positions);
            String name = Util.convertPlaceName(positions);
            placeName.setText(name);
        }
    }
    class PlaceNameThread extends Thread {
        TextView textView;
        Day day;
        PlaceNameThread(TextView textView, Day day) {
            this.textView = textView;
            this.day = day;
        }

        @Override
        public void run() {
            try {
                while (!database.hasMainPlace(day.getId()))
                    Thread.sleep(500);
                day.setMainPlaceId(database.selectDay(day.getId()).getMainPlaceId());
                List<Position> positions = database.selectPositionByDayId(day.getId());
                initPlace(positions);
                final String name = Util.convertPlaceName(positions);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(name);
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    protected void initPlace(List<Position> positions) throws Exception{
        for(Position position : positions)
            position.setPlace(database.selectPlace(position.getPlaceId()));
    }
    protected void initDayStatus(TextView dayStatus ,Day day) throws Exception {
        int placeCount;
        int photoCount;
        String text;
        day.setCreatedTime(database.selectDay(day.getId()).getCreatedTime());
        placeCount = database.countPosition(day.getId());
        photoCount = day.getPhotoCount();
        if(day.getCreatedTime()==null)
            text = mContext.getString(R.string.non_load_status,placeCount,photoCount);
        else {
            day.setPhotoCount(database.countPhotoByDayid(day.getId()));
            photoCount =day.getPhotoCount();
            text = mContext.getString(R.string.load_status,placeCount,photoCount);
        }
        dayStatus.setText(text);
    }
}
