package co.hasBeen.setting;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import co.hasBeen.R;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-24.
 */
public class ProfileImageListner implements View.OnClickListener {
    Context mContext;
    String mAccessToken;
    ImageView profileImage;
    public ProfileImageListner(Context mContext, ImageView profileImage) {
        this.mContext = mContext;
        this.profileImage = profileImage;
        mAccessToken = Session.getString(mContext,"accessToken",null);
    }

    @Override
    public void onClick(View v) {
        final ProfileImageDialog dialog = new ProfileImageDialog(mContext);
        View.OnClickListener fbImage = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.startLoading();
                Glide.clear(profileImage);
                new TakeFbProfileAsyncTask(new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        dialog.dismiss();
                        super.handleMessage(msg);
                        if (msg.what == 0) {
                            String url = (String) msg.obj;
                            Toast.makeText(mContext, mContext.getString(R.string.fb_profile_image_success), Toast.LENGTH_LONG).show();
                            Glide.with(mContext).load(url).transform(new CircleTransform(mContext)).into(profileImage);
                        } else
                            Toast.makeText(mContext, mContext.getString(R.string.common_error), Toast.LENGTH_LONG).show();
                    }
                }).execute(mAccessToken);
            }
        };
        dialog.setFbImageListner(fbImage);
        dialog.show();
    }
}
