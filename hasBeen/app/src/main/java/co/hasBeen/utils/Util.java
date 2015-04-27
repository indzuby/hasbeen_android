package co.hasBeen.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.media.ExifInterface;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Position;
import co.hasBeen.model.api.User;

/**
 * Created by zuby on 2015-01-16.
 */
public class Util {
    final static String MAP_URL = "http://maps.googleapis.com/maps/api/staticmap?size=480x240&scale=2&zoom=14&markers=icon:http://hasbeen.blob.core.windows.net/common/marker.png%7C";
    final static int imgSize = 1080;
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static int pxFromDp(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public static String parseName(User user,Context context) {

        return parseName(user.getFirstName(),user.getLastName(),context);

    }

    public static String parseName(String firstName, String lastName,Context context) {

        return context.getString(R.string.name,firstName,lastName);

    }

    public static Bitmap BitmapToUrl(String urlString) {

        try {
            URL url = new URL(urlString);
            Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            return bm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertPlaceName(List<Position> positions) {
        if (positions.size() <= 0) return "";
        return convertPlaceName(positions.get(0).getPlace().getName(), positions.get(positions.size() - 1).getPlace().getName());
    }

    public static String convertPlaceName(String start, String end) {
        String place = start;
        if (!start.equals(end)) {
            place += " — " + end;
        }
        return place;
    }

    public static Bitmap getBitmapClippedCircle(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float) (width / 2)
                , (float) (height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

    public static String getNewsFeedReason(String type,Context context) {
        if (type.equals("PHOTO_COMMENT")) {
            return context.getString(R.string.comment_photo);
        } else if (type.equals("PHOTO_LOVE")) {
            return context.getString(R.string.love_photo);
        } else if (type.equals("DAY_POST")) {
            return context.getString(R.string.post_day_trip);
        } else if (type.equals("DAY_COMMENT")) {
            return context.getString(R.string.comment_day_trip);
        } else if (type.equals("DAY_LOVE")) {
            return context.getString(R.string.love_day_trip);
        }
        return "";
    }

    static int[] placeHolder = {R.drawable.placeholder1, R.drawable.placeholder2, R.drawable.placeholder3, R.drawable.placeholder4, R.drawable.placeholder5};

    public static int getPlaceHolder(int index) {
        return placeHolder[index % 5];
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        try {
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return bitmap;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    return bitmap;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getBinaryBitmap(Bitmap bitmap, boolean isMap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (!isMap)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        else
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
        byte[] bitmapdata = bos.toByteArray();
        String binary  =  Base64.encodeToString(bitmapdata,Base64.DEFAULT);
        return binary;
    }

    public static String getLargeImage(Photo photo) throws Exception {
        String path = photo.getPhotoPath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
        if (options.outWidth > imgSize) {
            int ws = options.outWidth / imgSize + 1;
            if (ws > options.inSampleSize)
                options.inSampleSize = ws;
        }
        if (options.outHeight > imgSize) {
            int hs = options.outHeight / imgSize + 1;
            if (hs > options.inSampleSize)
                options.inSampleSize = hs;
        }

        ExifInterface exif = new ExifInterface(path);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        Bitmap rotateBitmap = rotateBitmap(bitmap, orientation);
        photo.setHeight(rotateBitmap.getHeight());
        photo.setWidth(rotateBitmap.getWidth());
        return getBinaryBitmap(rotateBitmap, false);
    }

    public static int getPhotoHeight(Context mContext) {
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int catgoryWith = Util.convertDpToPixel(72, mContext);
        return (width - catgoryWith) / 3 - 2;
    }

    public static int getProfilePhotoHeight(Context mContext) {
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        return width / 3 - 2;
    }
    public static int getTripHeight(Context mContext) {
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int catgoryWith = Util.convertDpToPixel(32, mContext);
        return (width - catgoryWith) / 5 - Util.convertDpToPixel(2, mContext);
    }
    Handler mHandler;

    public static String getMapUrl(float lat, float lon) {
        return MAP_URL + lat + "," + lon;
    }


    public static String getThumbnail(long id,Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(resolver,id, MediaStore.Images.Thumbnails.MINI_KIND,null);
        if( cursor != null && cursor.getCount() > 0 ) {
            cursor.moveToFirst();//**EDIT**
            return cursor.getString( cursor.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );

        }
        return null;
    }
    public static long getDateTime(String path) throws Exception{
        ExifInterface exif = new ExifInterface(path);
        String time = exif.getAttribute(ExifInterface.TAG_DATETIME);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date date =  simpleDateFormat.parse(time);
        long offset = date.getTimezoneOffset()*60000;
        return new DateTime(simpleDateFormat.parse(time).getTime()-offset).withZone(DateTimeZone.UTC).getMillis();
    }
    public static String getVersion(Context context) throws Exception{
        PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return i.versionName;
    }
    public static String getTutorialVersion(){
        return "1.2";
    }

    public static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity)cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)cont).getBaseContext());

        return null;
    }
}

