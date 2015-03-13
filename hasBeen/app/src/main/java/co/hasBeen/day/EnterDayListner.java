package co.hasBeen.day;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by 주현 on 2015-03-13.
 */
public class EnterDayListner implements View.OnClickListener {
    boolean flag;
    Context mContext;
    Long id;

    public EnterDayListner(Long id,Context mContext) {
        this.mContext = mContext;
        this.id = id;
    }

    @Override
    public void onClick(View v) {
        if(!flag) {
            flag = true;
            Intent intent = new Intent(mContext, DayView.class);
            intent.putExtra("id",id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            flag = false;
        }
    }
}
