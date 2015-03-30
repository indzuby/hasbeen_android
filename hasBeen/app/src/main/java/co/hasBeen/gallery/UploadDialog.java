package co.hasBeen.gallery;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import co.hasBeen.R;

/**
 * Created by 주현 on 2015-02-11.
 */
public class UploadDialog extends Dialog {

    TextView progress;
    TextView photoCount;
    int maxCount;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.upload_dialog);

        setLayout();
    }

    public UploadDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        count = 0;
    }

    public void setMaxCount(int maxProgress) {
        maxCount = maxProgress;
    }

    public void setPhotoCount(int count) {
        Log.i("count",count+"");
        int percent = (int) (((float) (count+1) / maxCount) * 100);
        progress.setText(percent + "%");
        photoCount.setText(getContext().getString(R.string.uploading_count,count+1,maxCount));
        this.count = count;
    }
    private void setLayout() {
        progress = (TextView) findViewById(R.id.progress);
        photoCount = (TextView) findViewById(R.id.photoCount);
        View uploading = findViewById(R.id.uploading);
        uploading.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        uploading.startAnimation(rotate);
    }
}