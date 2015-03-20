package co.hasBeen.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import co.hasBeen.R;

/**
 * Created by 주현 on 2015-03-20.
 */
public class ConfirmDialog extends Dialog {
    View.OnClickListener delete;
    public ConfirmDialog(Context context, View.OnClickListener delete) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.delete = delete;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.confirm_dialog);

        setLayout();
        findViewById(R.id.box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mDelete.setOnClickListener(delete);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete.onClick(v);
                dismiss();
            }
        });
    }
    View mCancle;
    View mDelete;
    private void setLayout() {
        mCancle = findViewById(R.id.cancle);
        mDelete = findViewById(R.id.delete);
    }
}
