package co.hasBeen.utils;

import android.content.Context;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;

/**
 * Created by 주현 on 2015-03-20.
 */
public class GlideRequest implements View.OnDragListener {
    Context mContext;

    public GlideRequest(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            Glide.with(mContext).pauseRequests();
        }else if(event.getAction() == MotionEvent.ACTION_UP)
            Glide.with(mContext).resumeRequests();
        return false;
    }
}
