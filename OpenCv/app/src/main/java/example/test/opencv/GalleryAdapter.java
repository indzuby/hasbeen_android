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

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zuby on 2015-01-05.
 */
public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    private List<PhotoData> mImagePath;
    ImageLoader mimageLoader;

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
            imageView = new ImageView(mContext);
        }else {
            imageView.setImageResource(R.drawable.loading);
        }
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        imageView.setLayoutParams(new AbsListView.LayoutParams(width / 3 - 10, width / 3 - 10));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(2, 2, 2, 2);

        if (photo.photoPath == null) {
            imageView.setImageResource(R.drawable.loading);
        } else {
            Log.i("Photo path", photo.photoPath);
            Bitmap fromImg,toImg;
            fromImg = mimageLoader.getImage(photo.photoID, photo.photoPath);
            if(position>0) {
                PhotoData before = getItem(position-1);
                toImg = mimageLoader.getImage(before.photoID,before.photoPath);
                if(fromImg!=null && toImg!=null) {
                    double hist = compareHistogram(fromImg,toImg);
                    Log.e("유사도 ",hist+"");
                   if(hist<0.8)
                        imageView.setImageBitmap(fromImg);
                    else {
                        mImagePath.remove(position);
                        notifyDataSetChanged();
                    }
                }
            }else
                imageView.setImageBitmap(fromImg);
        }
        return imageView;
    }
    protected double compareHistogram(Bitmap fromImg, Bitmap toImg) {
        Mat hsvFrom = new Mat(fromImg.getWidth(),fromImg.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(fromImg, hsvFrom);
        Mat hsvTo = new Mat(toImg.getWidth(),toImg.getHeight(),CvType.CV_8UC1);
        Utils.bitmapToMat(toImg,hsvTo);
//        hsvFrom = new Mat();
//        hsvTo = new Mat();

//        Imgproc.cvtColor(fromMat,hsvFrom,Imgproc.COLOR_BGR2HSV);
//        Imgproc.cvtColor(toMat,hsvTo,Imgproc.COLOR_BGR2HSV);

        Imgproc.cvtColor(hsvFrom, hsvFrom, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(hsvTo,hsvTo,Imgproc.COLOR_BGR2HSV);
        MatOfInt histSize = new MatOfInt(25);
        MatOfFloat ranges = new MatOfFloat(0f,256f); ;
        MatOfInt channels = new MatOfInt(0,1);

        Mat histFrom = new Mat();
        Mat histTo = new Mat();


//        Imgproc.calcHist(Arrays.asList(hsvFrom), channels, new Mat(), histFrom, histSize, ranges,true);
//        Core.normalize(histFrom,histFrom, 0, 1, Core.NORM_MINMAX, -1, new Mat());
//
//        Imgproc.calcHist(Arrays.asList(hsvTo),channels,new Mat(),histTo,histSize,ranges,true);
//        Core.normalize(histTo, histTo, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        Imgproc.calcHist(Arrays.asList(hsvFrom), new MatOfInt(0), new Mat(), histFrom, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(hsvTo),new MatOfInt(0),new Mat(),histTo,histSize,ranges);

        return Imgproc.compareHist(histFrom,histTo,Imgproc.CV_COMP_CORREL);
    }

}
