package co.hasBeen.social;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import co.hasBeen.day.EnterDayListner;
import co.hasBeen.photo.EnterPhotoListner;

/**
 * Created by 주현 on 2015-04-01.
 */
public class LinkActivity extends Activity {
    final static String PHOTOS = "http://www.hasbeen.co/#/photos/";
    final static String DAYS = "http://www.hasbeen.co/#/days/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent()!=null){
            Uri uri = getIntent().getData();
            if(uri != null)
            {
                String url = uri.toString();

                Log.i("URL", url);
                if(url.contains("photos")) {
                    String id = url.substring(PHOTOS.length());
                    Log.i("id",id);
                    new EnterPhotoListner(Long.parseLong(id),this).onClick(null);
                }else if(url.contains("days")) {
                    String id = url.substring(DAYS.length());
                    Log.i("id",id);
                    new EnterDayListner(Long.parseLong(id),this).onClick(null);
                }
            }
        }
    }
}
