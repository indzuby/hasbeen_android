package co.hasBeen.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.database.ItemModule;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-04-06.
 */
public class NewPhotoReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("Test", "START OF NewPhotoReceiver");

        Uri uri = intent.getData();
        Log.d("Test", "[onReceive] URI - " + uri);
        Cursor cursor = context.getContentResolver().query(
                intent.getData(), null, null, null, null);
        cursor.moveToFirst();
        long id = cursor
                .getLong(cursor.getColumnIndex("_id"));
//        Toast.makeText(context, "New Photo is Saved as : " + image_path,Toast.LENGTH_LONG).show();
        try {
            ItemModule itemModule = new ItemModule(context);
            itemModule.insertDay();
            DataBaseHelper dataBase = new DataBaseHelper(context);
            if(dataBase.hasPhotoByPhotoId(id)) { // day가 만들어진경우
                Session.putBoolean(context, "modifyDay", true);
            }else {
                Session.putBoolean(context, "modifyDay" + dataBase.getLastPhoto().getDayId(), true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
