package example.test.opencv;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.List;

/**
 * Created by zuby on 2015-01-05.
 */
public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    private List<PhotoData> mImagePath;
    ImageLoader mimageLoader;
    public static class PhotoData {

        int photoID;
        String photoPath;
        int photoTaken;
        public PhotoData(int id, String path, int taken) {
            photoID = id;
            photoPath = path;
            photoTaken = taken;
        }

    }
    public GalleryAdapter(Context context,List imagePath) {
        mContext = context;
        mImagePath = imagePath;
    }
    public GalleryAdapter(Context context,List imagePath,ImageLoader imageLoader) {
        mContext = context;
        mImagePath = imagePath;
        mimageLoader = imageLoader;
    }
    @Override
    public int getCount() {
        return mImagePath.size();
    }

    @Override
    public PhotoData getItem(int position) {
        return mImagePath.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView)convertView;
        PhotoData photo = getItem(position);
        if(imageView==null || photo.photoID==0) {
            imageView = new ImageView(mContext);
        }else {
            imageView.setImageResource(R.drawable.loading);
        }
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        imageView.setLayoutParams(new AbsListView.LayoutParams(width / 4 - 12, width / 4 - 12));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(2, 2, 2, 2);

        if (photo.photoPath == null) {
            imageView.setImageResource(R.drawable.loading);
        } else {
            Log.i("Photo path", photo.photoPath);
            imageView.setImageBitmap(mimageLoader.getImage(photo.photoID, photo.photoPath));
//                imageView.setImageBitmap(getImage(photo.photoID, photo.photoPath));
        }
        return imageView;
    }
    public Bitmap getImage(Integer uid, String path){
        ContentResolver resolver = mContext.getContentResolver();
        String[] proj = { MediaStore.Images.Thumbnails.DATA };
        Bitmap micro = MediaStore.Images.Thumbnails.getThumbnail(resolver, uid, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        if( micro != null ) {
            return micro;
        }
        else {
            Cursor mini = MediaStore.Images.Thumbnails.queryMiniThumbnail(resolver, uid, MediaStore.Images.Thumbnails.MINI_KIND, proj);
            if( mini != null && mini.moveToFirst() ) {
                path = mini.getString(mini.getColumnIndex(proj[0]));
            }
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
        if( options.outWidth > 96 ) {

            int ws = options.outWidth / 96 + 1;
            if( ws > options.inSampleSize )
                options.inSampleSize = ws;
        }
        if( options.outHeight > 96 ) {

            int hs = options.outHeight / 96 + 1;
            if( hs > options.inSampleSize )
                options.inSampleSize = hs;
        }
        return BitmapFactory.decodeFile(path, options);
    }

}
