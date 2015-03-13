package co.hasBeen.profile;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import co.hasBeen.R;
import co.hasBeen.model.api.Follow;
import co.hasBeen.profile.follow.DoFollowAsyncTask;
import co.hasBeen.profile.follow.UnFollowAsyncTask;

/**
 * Created by 주현 on 2015-03-12.
 */
public class ProfileFollowListner implements View.OnClickListener {

    Follow follow;
    ImageView image;
    String mAccessToken;
    Long id;

    public ProfileFollowListner(Follow follow, ImageView image, String mAccessToken, Long id) {
        this.follow = follow;
        this.image = image;
        this.mAccessToken = mAccessToken;
        this.id = id;
    }

    @Override
    public void onClick(View v) {
        if(follow == null) {
            new DoFollowAsyncTask(new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 0) {
                        image.setImageResource(R.drawable.following);
                        follow = new Follow();
                        follow.setId((Long)msg.obj);
                    }
                }
            }).execute(mAccessToken, id);
        }else {
            new UnFollowAsyncTask(new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0) {
                        follow = null;
                        image.setImageResource(R.drawable.follow_gray);
                    }
                }
            }).execute(mAccessToken,follow.getId());
        }
    }
}