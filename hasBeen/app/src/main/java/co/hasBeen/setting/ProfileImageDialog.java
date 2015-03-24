package co.hasBeen.setting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import co.hasBeen.R;

/**
 * Created by 주현 on 2015-03-24.
 */
public class ProfileImageDialog extends Dialog {

    View takeFbImage;
    View mLoading;
    View.OnClickListener fbImageListner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.profile_image_dialog);
        setCanceledOnTouchOutside(true);
        setLayout();
        takeFbImage.setOnClickListener(fbImageListner);
    }

    public ProfileImageDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

    }

    public void setFbImageListner(View.OnClickListener fbImageListner) {
        this.fbImageListner = fbImageListner;
    }

    private void setLayout() {
        takeFbImage = findViewById(R.id.fbProfileImage);
        mLoading = findViewById(R.id.refresh);
    }
    public void startLoading(){
        mLoading.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        mLoading.startAnimation(rotate);
        findViewById(R.id.dialogBox).setVisibility(View.GONE);

    }
    public void stopLoading(){
        mLoading.clearAnimation();
        mLoading.setVisibility(View.GONE);
    }
}
