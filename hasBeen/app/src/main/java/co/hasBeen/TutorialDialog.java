package co.hasBeen;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by 주현 on 2015-03-23.
 */
public class TutorialDialog extends Dialog {


    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.tutorial);

        setLayout();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    public TutorialDialog(Context context,String url) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.url = url;
    }
    ImageView image;
    private void setLayout(){
        image = (ImageView) findViewById(R.id.tutorialImage);
        Glide.with(getContext()).load(url).fitCenter().into(image);
    }
}