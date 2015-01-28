package example.test.hasBeen.gallery;

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

import example.test.hasBeen.R;
import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.utils.Util;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;
        final Photo photo = getItem(position);
        if (imageView == null || photo.getPhotoId() == 0) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.gallery_view, null);
            imageView = (ImageView) view.findViewById(R.id.view_gallery);
        } else
            imageView.setImageResource(R.drawable.loading);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int px = Util.pxFromDp(mContext,2);
        imageView.setLayoutParams(new AbsListView.LayoutParams(width * 4 / 15, width * 4 / 15));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(px, px, px, px);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag) {
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
        });
        Glide.with(mContext).load(photo.getPhotoPath())
                .centerCrop()
                .crossFade().placeholder(R.drawable.loading)
                .into(imageView);
        return imageView;
    }

}

