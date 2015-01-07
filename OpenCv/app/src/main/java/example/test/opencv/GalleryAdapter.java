package example.test.opencv;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by zuby on 2015-01-05.
 */
public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    private List<PhotoData> mImagePath;
    ImageLoader mimageLoader;
    int maxEdge;
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
            System.loadLibrary("opencv_java");
            System.loadLibrary("opencv_info");
            //System.loadLibrary("opencv_core");
        }
    }
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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.gallery_view,null);
            imageView = (ImageView)view.findViewById(R.id.view_gallery);
        }else
            imageView.setImageResource(R.drawable.loading);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        imageView.setLayoutParams(new AbsListView.LayoutParams(width / 3 - 10, width / 3 - 10));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(2, 2, 2, 2);
//
//        if (photo.photoPath != null) {
//            Log.i("Photo path", photo.photoPath);
//            Bitmap fromImg,toImg;
//            fromImg = mimageLoader.getImage(photo.photoID, photo.photoPath);
//            if(position>0) {
//                PhotoData before = getItem(position-1);
//                toImg = mimageLoader.getImage(before.photoID,before.photoPath);
//                if(fromImg!=null && toImg!=null) {
//                   double hist = compareHistogram(fromImg,toImg);
//                   if(hist<0.8) {
//                       imageView.setImageBitmap(fromImg);
//                   }
//                    else {
//                       mImagePath.remove(position);
//                       notifyDataSetChanged();
//                    }
//                }
//            }else {
//                imageView.setImageBitmap(fromImg);
//            }
//        }

        Glide.with(mContext).load(photo.photoPath)
                .centerCrop()
                .crossFade().placeholder(R.drawable.loading)
                .into(imageView);
        return imageView;
    }

}
