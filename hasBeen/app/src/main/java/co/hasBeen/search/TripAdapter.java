package co.hasBeen.search;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import co.hasBeen.day.Recommandation;
import co.hasBeen.model.api.Day;

/**
 * Created by 주현 on 2015-04-01.
 */
public class TripAdapter extends BaseAdapter {
    List<Day> mDayList;
    Context mContext;

    public TripAdapter(List<Day> mDayList, Context mContext) {
        this.mDayList = mDayList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mDayList.size();
    }

    @Override
    public Day getItem(int position) {
        return mDayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Day day = getItem(position);
        View view = Recommandation.getPinView(day,mContext,convertView);
//        ImageView mainPhoto = (ImageView) view.findViewById(R.id.mainPhoto);
//        TextView photoCount = (TextView) view.findViewById(R.id.photoCount);
//        TextView placeName = (TextView) view.findViewById(R.id.placeName);
//        TextView name = (TextView) view.findViewById(R.id.name);
//        TextView date = (TextView) view.findViewById(R.id.date);
//        Glide.with(mContext).load(day.getMainPhoto().getSmallUrl()).centerCrop().into(mainPhoto);
//        photoCount.setText((day.getPhotoCount()-1)+"");
//        placeName.setText(Util.convertPlaceName(day.getPositionList()));
//        name.setText(Util.parseName(day.getUser(),mContext));
//        date.setText(HasBeenDate.convertDate(day.getDate()));
//        view.setOnClickListener(new EnterDayListner(day.getId(),mContext));
        return view;
    }
}
