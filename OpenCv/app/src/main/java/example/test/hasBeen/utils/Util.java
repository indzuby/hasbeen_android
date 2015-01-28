package example.test.hasBeen.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.net.URL;
import java.util.List;

import example.test.hasBeen.model.api.PositionApi;
import example.test.hasBeen.model.api.User;

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
    public static String convertPlaceName(List<PositionApi> positions) {
        if(positions.size()<=0) return "";
        String place = positions.get(0).getPlace().getName()+" - "+positions.get(positions.size()-1).getPlace().getName();
        if(place.length()>40)
            place = place.substring(0,40)+" ...";
        return place;
    }

}
