package co.hasBeen.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.DisplayMetrics;

import java.net.URL;
import java.util.List;

import co.hasBeen.model.api.User;
import co.hasBeen.R;
import co.hasBeen.model.database.Position;

/**
 * Created by zuby on 2015-01-16.
 */
public class Util {
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
    public static int convertDpToPixel(int dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
    public static int pxFromDp(Context context, int dp)
    {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
    public static String parseName(User user,int countryCode) {

        return user.getFirstName()+" "+user.getLastName();

    }
    public static Bitmap BitmapToUrl(String urlString){

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
        if(positions.size()<=0) return "";
        String place = positions.get(0).getPlace().getName()+" — "+positions.get(positions.size()-1).getPlace().getName();
        if(place.length()>35)
            place = place.substring(0,35)+" ...";
        return place;
    }
    public static String cropPlaceName(String name) {
        String placeName = name;
        if(placeName.length()>30)
            placeName = placeName.substring(0,30 )+" ...";
        return placeName;
    }
    public static Bitmap getBitmapClippedCircle(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float)(width / 2)
                , (float)(height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }
    public static String getNewsFeedReason(String type){
        if(type.equals("PHOTO_COMMENT")) {
            return " Commented on photo";
        }else if(type.equals("PHOTO_LOVE")){
            return " liked on photo";
        }else if(type.equals("DAY_POST")) {
            return " Posted on day trip";
        }else if(type.equals("DAY_COMMENT")) {
            return " Commented on day trip";
        }else if(type.equals("DAY_LOVE")) {
            return " liked on day trip";
        }
        return "";
    }
    static int[] placeHolder = {R.drawable.placeholder1,R.drawable.placeholder2,R.drawable.placeholder3,R.drawable.placeholder4,R.drawable.placeholder5};
    public static int getPlaceHolder(int index) {
        return placeHolder[index%5];
    }

    public static String getFbCountFirst(int count,int countryCode) {
        return "당신의 페이스북 친구"+ count+"명이 \nhasBeen을 이용중입니다.";
    }
    public static String getFbCountSecond(int count,int countryCode) {
        return count+"명의 친구들 모두 보기";
    }

}
