package co.hasBeen.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Trip;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-17.
 */
public class GallerySelectAdapter extends BaseAdapter {
    List<Trip> mTrips;
    Context mContext;

    public GallerySelectAdapter(List<Trip> mTrips, Context mContext) {
        this.mTrips = mTrips;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mTrips.size();
    }

    @Override
    public Trip getItem(int position) {
        return mTrips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTrips.get(position).getId();
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View view = convertView;
        Trip trip = getItem(index);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_trip_item, null);
        }
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView dayCount = (TextView) view.findViewById(R.id.dayCount);
        TextView placeName = (TextView) view.findViewById(R.id.placeName);
        LinearLayout photoBox = (LinearLayout) view.findViewById(R.id.photosBox);
        date.setText(HasBeenDate.convertDate(trip.getStartTime(), trip.getEndTime()));
        dayCount.setText(mContext.getString(R.string.day_count, trip.getItineraryLong()));
        String startPlace = trip.getStartCity() + ", " + trip.getStartCountry();
        String endPlace = trip.getEndCity() + ", " + trip.getEndCountry();
        placeName.setText(Util.convertPlaceName(startPlace, endPlace));
        photoBox.removeAllViews();
        int i = 1;
        for (Day day : trip.getDayList())
            photoBox.addView(getDayItem(day, i++, trip.getItineraryLong()));
        view.setOnClickListener(new TripClickListner(trip.getId()));
        return view;
    }
    public View getDayItem(Day day, int index,int total){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gallery_trip_photo, null);

        ImageView mainPhoto = (ImageView) view.findViewById(R.id.mainPhoto);
        TextView textView = (TextView) view.findViewById(R.id.dayIndex);
        int width = Util.getTripHeight(mContext);
        int height = width;
        Glide.with(mContext).load(day.getMainPhoto().getSmallUrl()).placeholder(Util.getPlaceHolder(index)).override(width,height).into(mainPhoto);
        textView.setText("Day "+ (index));
        if(index==5 && total!=5)
            textView.setText("More days");
        view.setLayoutParams(new AbsListView.LayoutParams(width,height));
        return view;
    }
    class TripClickListner implements View.OnClickListener{
        Long id;

        TripClickListner(Long id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            Activity activity = (Activity) mContext;
            intent.putExtra("id", id);
            activity.setResult(activity.RESULT_OK, intent);
            activity.finish();
        }
    }
}
