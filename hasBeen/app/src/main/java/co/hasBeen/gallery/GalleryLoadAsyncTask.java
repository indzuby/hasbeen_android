package co.hasBeen.gallery;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.util.Date;
import java.util.List;

import co.hasBeen.database.ItemModule;
import co.hasBeen.model.api.Day;

/**
 * Created by 주현 on 2015-03-02.
 */
public class GalleryLoadAsyncTask extends AsyncTask<Object,Void,List<Day>> {
    Handler mHandler;
    ItemModule itemModule;
    @Override
    protected List<Day> doInBackground(Object... params) {
        Long date = new Date().getTime();
        itemModule = (ItemModule) params[0];
        try {
            if (params.length > 1)
                date = (Long) params[1];
            return itemModule.bringTenDay(date);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Day> days) {
        Message msg = mHandler.obtainMessage();
        mHandler.sendMessage(msg);
        if(days!=null) {
            msg.what = 0 ;
            msg.obj = days;
        }else
            msg.what=-1;
        mHandler.sendMessage(msg);
    }

    public GalleryLoadAsyncTask(Handler mHandler) {
        this.mHandler = mHandler;
    }
}
