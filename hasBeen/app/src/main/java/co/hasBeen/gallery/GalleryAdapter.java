package co.hasBeen.gallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Photo;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-05.
 */
public class GalleryAdapter extends BaseAdapter {
    protected Context mContext;
    protected List<Photo> mImagePath;
    boolean flag=false;
    int height;
    public GalleryAdapter(Context context, List imagePath) {
        mContext = context;
        mImagePath = imagePath;
        height = getHeight();
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
        View view = convertView;
        Photo photo = getItem(position);
        if (view == null || photo.getPhotoId() == 0) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_view, null);
        }
        LinearLayout container = (LinearLayout) view.findViewById(R.id.photoContainer);
        container.setLayoutParams(new AbsListView.LayoutParams(height,height));
        ImageView imageView = (ImageView) view.findViewById(R.id.photo);
        imageView.setOnClickListener(new ImageListner(position,photo));

        Glide.with(mContext).load(photo.getPhotoPath())
                .centerCrop().placeholder(Util.getPlaceHolder(photo.getEdgeCount())).override(height, height)
                .into(imageView);
        return view;
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
            if(!flag) {
                flag = true;
                Intent intent = new Intent(mContext, GalleryPhoto.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", photo.getId());
                mContext.startActivity(intent);
                flag = false;
            }
        }
    }
    public int getHeight(){
        return Util.getPhotoHeight(mContext);
    }
}

