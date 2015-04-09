package co.hasBeen.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Photo;
import co.hasBeen.photo.EnterPhotoListner;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-04-09.
 */
public class PhotoAdapter extends BaseAdapter {
    List<Photo> mPhotos;
    Context mContext;
    int[] mPhotoId={R.id.photo1,R.id.photo2,R.id.photo3};
    public PhotoAdapter(List<Photo> mPhotos, Context mContext) {
        this.mPhotos = mPhotos;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return (int) Math.ceil((double)mPhotos.size()/3);
    }

    @Override
    public List<Photo> getItem(int position) {
        List<Photo> photos = new ArrayList<>();
        for(int i = position*3; i <(position+1)*3 && i < mPhotos.size();i++) {
            photos.add(mPhotos.get(i));
        }
        return photos;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        List<Photo> photos = getItem(position);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.profile_photos, null);
        }
        setPhotos(view,photos);
        return view;
    }
    public void setPhotos(View view,List<Photo> photos){

        for(int id : mPhotoId)
            view.findViewById(id).setVisibility(View.INVISIBLE);
        for(int i = 0 ; i<photos.size();i++){
            ImageView photo = (ImageView) view.findViewById(mPhotoId[i]);
            photo.setVisibility(View.VISIBLE);
            int margin = 2;
            int width = Util.getProfilePhotoHeight(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width,width);
            layoutParams.setMargins(margin,margin,margin,margin);
            photo.setLayoutParams(layoutParams);
            Glide.with(mContext).load(photos.get(i).getSmallUrl()).centerCrop().placeholder(Util.getPlaceHolder((int)(Math.random()*10))).into(photo);
            view.setOnClickListener(new EnterPhotoListner(photos.get(i).getId(),mContext));
        }
     }
}
