package example.test.hasBeen.gallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.opencv.android.OpenCVLoader;

import java.util.List;

import example.test.hasBeen.ImageLoader;
import example.test.hasBeen.R;
import example.test.hasBeen.model.HasBeenPhoto;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-05.
 */
public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    private List<HasBeenPhoto> mImagePath;
    public GalleryAdapter(Context context, List imagePath) {
        mContext = context;
        mImagePath = imagePath;
    }

    @Override
    public int getCount() {
        return mImagePath.size();
    }

    @Override
    public HasBeenPhoto getItem(int position) {
        return mImagePath.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;
        final HasBeenPhoto photo = getItem(position);
        if (imageView == null || photo.getPhotoId() == 0) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.gallery_view, null);
            imageView = (ImageView) view.findViewById(R.id.view_gallery);
        } else
            imageView.setImageResource(R.drawable.loading);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        imageView.setLayoutParams(new AbsListView.LayoutParams(width * 7 / 24 - 12, width * 7 / 24 - 12));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int px = Util.pxFromDp(mContext,2);
        imageView.setPadding(px, px, px, px);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,GalleryFullPhoto.class);
                intent.putExtra("path",photo.getPhotoPath());
                mContext.startActivity(intent);
            }
        });
        Log.i("Clearest id = id", photo.getClearestId() + " " + photo.getId());
        Glide.with(mContext).load(photo.getPhotoPath())
                .centerCrop()
                .crossFade().placeholder(R.drawable.loading)
                .into(imageView);
        return imageView;
    }

}
