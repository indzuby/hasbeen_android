package example.test.hasBeen.day;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.PhotoApi;
import example.test.hasBeen.model.api.PositionApi;
import example.test.hasBeen.model.database.Day;
import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.model.database.Position;
import example.test.hasBeen.utils.HasBeenDate;
import it.sephiroth.android.library.widget.HListView;

/**
 * Created by zuby on 2015-01-27.
 */
public class DayAdapter extends BaseAdapter {
    Context mContext;
    List<PositionApi> mPositionList;
    public DayAdapter(Context mContext, List mPositionList)
    {
        this.mContext = mContext;
        this.mPositionList = mPositionList;
    }

    @Override
    public int getCount() {
        return mPositionList.size();
    }

    @Override
    public PositionApi getItem(int position) {
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
        PositionApi position = getItem(index);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.day_photos, null);
        }
        TextView placeName = (TextView) view.findViewById(R.id.placeName);
        TextView placeTime = (TextView) view.findViewById(R.id.placeTime);
        ImageView placeIcon = (ImageView) view.findViewById(R.id.placeIcon);
        placeTime.setText(HasBeenDate.convertTime(position.getStartDate(), position.getEndDate()));
        if(position.getPlace()!=null) {
            placeName.setText(position.getPlace().getName());
            Glide.with(mContext).load(position.getPlace().getCategoryIconPrefix() + "88" + position.getPlace().getCategoryIconSuffix()).into(placeIcon);
        }else {
            placeName.setHint("Enter the place name");
            placeName.setText("");
        }
        HListView hListView = (HListView) view.findViewById(R.id.hListView);
        if (position.getPhotoList() != null && position.getPhotoList().size() > 0) {
            View transparentHeaderView = LayoutInflater.from(mContext).inflate(R.layout.day_hlist_header, null, false);
            hListView.addHeaderView(transparentHeaderView);
            PhotoAdapter photoAdapter = new PhotoAdapter(position.getPhotoList());
            hListView.setAdapter(photoAdapter);
        }
        return view;
    }

    class PhotoAdapter extends BaseAdapter {
        List<Photo> mPhotoList;

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
            Glide.with(mContext).load(photo.getMediumUrl()).into(imageView);
            return view;
        }
    }
}
