package co.hasBeen.database.Photo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.sql.SQLException;

import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-30.
 */
public class AddAsyncTask extends Thread {
    Context mContext;
    Long mDayid;
    Handler mHandler;
    boolean isCancelled;
    public AddAsyncTask(Context context, Long dayid,Handler handler) throws SQLException {
        mContext = context;
        mDayid = dayid;
        mHandler = handler;
    }
    public void cancel(){
        isCancelled = false;
    }
    @Override
    public void run(){
        try {
            while (!isCancelled) {
                boolean modfiy = Session.getBoolean(mContext,"modifyDay"+mDayid,false);
                if(modfiy) {
                    Session.putBoolean(mContext, "modifyDay" + mDayid, false);
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }
                Thread.sleep(3000);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
