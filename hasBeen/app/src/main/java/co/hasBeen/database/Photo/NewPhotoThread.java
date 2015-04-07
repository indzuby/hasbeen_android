package co.hasBeen.database.Photo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.model.api.Day;

/**
 * Created by 주현 on 2015-04-06.
 */
public class NewPhotoThread extends Thread {
    Context mContext;
    Long mDayid;
    Handler mHandler;
    DataBaseHelper dataBase;
    boolean flag =  true;
    public NewPhotoThread(Context mContext, Long mDayid, Handler mHandler) throws Exception{
        this.mContext = mContext;
        this.mDayid = mDayid;
        this.mHandler = mHandler;
        dataBase = new DataBaseHelper(mContext);
    }
    public void cancel(){
        flag = false;
    }

    @Override
    public void run() {
        try {
            while (flag) {
                Day day = dataBase.selectLastDay();
                Long dayid;
                if (day == null)
                    dayid = 0L;
                else
                    dayid = day.getId();
                if (dayid > mDayid) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = day;
                    mHandler.sendMessage(msg);
                }
                if(dayid!= mDayid)
                    mDayid = dayid;
                Thread.sleep(3000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
