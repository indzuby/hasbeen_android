package co.hasBeen.gallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.model.database.Photo;
import co.hasBeen.utils.Util;
import co.hasBeen.R;

/**
 * Created by zuby on 2015-01-05.
 */
public class GalleryAdapter extends BaseAdapter {
    protected Context mContext;
    protected List<Photo> mImagePath;
    boolean flag=false;
    public GalleryAdapter(Context context, List imagePath) {
        mContext = context;
        mImagePath = imagePath;
    }

    @Override
    public int getCount() {
        return mImagePath.size();
    }

    @Override
    public Photo getItem(int position) {
        return mImagePath.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;
        Photo photo = getItem(position);
        if (imageView == null || photo.getPhotoId() == 0) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.gallery_view, null);
            imageView = (ImageView) view.findViewById(R.id.view_gallery);
        } else
            imageView.setImageResource(R.drawable.loading);
        setHeight(imageView);
        imageView.setOnClickListener(new ImageListner(position,photo));
        Glide.with(mContext).load(photo.getPhotoPath())
                .centerCrop().placeholder(Util.getPlaceHolder(photo.getEdgeCount()))
                .into(imageView);
        return imageView;
    }
    class ImageListner implements View.OnClickListener {
        int position;
        Photo photo;
        boolean flag = false;
        ImageListner(int position, Photo photo) {
            this.position = position;
            this.photo = photo;
        }

        @Override
        public void onClick(View v) {
            flag = true;
            Intent intent = new Intent(mContext, GalleryPhoto.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("photoId",photo.getId());
            intent.putExtra("photoCount",getCount());
            intent.putExtra("photoNth",position+1);
            mContext.startActivity(intent);
            flag = false;

        }
    }
    public void setHeight(ImageView imageView){
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int px = Util.pxFromDp(mContext,1);
        imageView.setLayoutParams(new AbsListView.LayoutParams(width * 4 / 15 - px*2, width * 4 / 15- px*2));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(px, px, px, px);
    }
}

