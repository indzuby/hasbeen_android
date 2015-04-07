package co.hasBeen.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by 주현 on 2015-04-07.
 */
public class RemovePhotoReciver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Uri uri = intent.getData();
        Log.d("Test", "[onReceive] URI - " + uri);
        Cursor cursor = context.getContentResolver().query(
                intent.getData(), null, null, null, null);
        cursor.moveToFirst();
        long id = cursor.getLong(cursor.getColumnIndex("_id"));
//        Toast.makeText(context, "New Photo is Saved as : " + image_path,Toast.LENGTH_LONG).show();
        try {

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
