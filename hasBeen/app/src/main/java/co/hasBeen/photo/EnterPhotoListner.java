package co.hasBeen.photo;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-13.
 */
public class EnterPhotoListner implements View.OnClickListener {
    Long id;
    Context mContext;
    boolean flag;
    public EnterPhotoListner(Long id, Context context) {
        this.id = id;
        this.mContext = context;
        flag = false;
    }

    @Override
    public void onClick(View v) {
        if(!flag) {
            flag = true;
//            Intent intent = new Intent(mContext, PhotoView.class);
            Intent intent = new Intent(mContext, PhotoActivity.class);
            intent.putExtra("id",id);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Util.scanForActivity(mContext).startActivityForResult(intent, Session.REQUEST_PHOTO_CODE);
            Util.scanForActivity(mContext).startActivityForResult(intent, Session.REQUEST_PHOTO_CODE);
            flag = false;
        }
    }
}
