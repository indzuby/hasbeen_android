package co.hasBeen.setting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import co.hasBeen.R;
import co.hasBeen.model.api.User;

/**
 * Created by 주현 on 2015-03-24.
 */
public class GenderDialog extends Dialog {

    View male,female;
    User.Gender gender;
    View.OnClickListener maleListner,femaleListner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.gender_dialog);
        findViewById(R.id.box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setLayout();
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleListner.onClick(v);
                dismiss();
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleListner.onClick(v);
                dismiss();
            }
        });
    }

    public GenderDialog(Context context,User.Gender gender) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.gender = gender;
    }
    public void setListner(View.OnClickListener maleListner, View.OnClickListener femaleListner){
        this.maleListner = maleListner;
        this.femaleListner = femaleListner;
    }
    private void setLayout() {
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        if(gender!=null) {
            if(gender == User.Gender.MALE)
                findViewById(R.id.maleCheck).setVisibility(View.VISIBLE);
            else
                findViewById(R.id.femaleCheck).setVisibility(View.VISIBLE);
        }
    }
}
