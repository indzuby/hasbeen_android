package co.hasBeen.gallery;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.database.ItemModule;
import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Position;
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
    Typeface medium,regular;
    public GalleryDayAdapter(Context context, List<Day> galleryList) throws Exception{
        mContext = context;
        mGalleryList = galleryList;
        medium = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        regular = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Regular.ttf");
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

        date.setText(HasBeenDate.convertDate(day.getDate()));
        date.setTypeface(medium);
        placeName.setTypeface(regular);
        try {
            day.setMainPhoto(database.selectPhoto(day.getMainPhotoId()));
            Glide.with(mContext).load(day.getMainPhoto().getPhotoPath()).placeholder(Util.getPlaceHolder(position)).into(mainPhoto);
            initPlaceName(placeName,day);
            initDayStatus(dayStatus,day);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    protected void initPlaceName(TextView placeName ,Day day) throws Exception{
        if (day.getMainPlaceId() == null && day.getMainPlace() == null) {
            itemModule.getPlace(day, placeName);
        } else {
            List<Position> positions = database.selectPositionByDayId(day.getId());
            initPlace(positions);
            String name = Util.convertPlaceName(positions);
            placeName.setText(name);
            if(name.length()<=0)
                placeName.setVisibility(View.GONE);
            else
                placeName.setVisibility(View.VISIBLE);
        }
    }
    protected void initPlace(List<Position> positions) throws Exception{
        for(Position position : positions) 
            position.setPlace(database.selectPlace(position.getPlaceId()));
    }
    protected void initDayStatus(TextView dayStatus ,Day day) throws Exception {
        int placeCount;
        int photoCount;
        placeCount = database.countPosition(day.getId());
        photoCount = day.getPhotoCount();
        if(placeCount == 1 && photoCount == 1) {
            dayStatus.setVisibility(View.INVISIBLE);
        }else {
            dayStatus.setText(placeCount + " places · "+photoCount+" photos");
            dayStatus.setVisibility(View.VISIBLE);
        }
    }
}
