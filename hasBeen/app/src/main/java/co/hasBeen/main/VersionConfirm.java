package co.hasBeen.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import co.hasBeen.R;

/**
 * Created by 주현 on 2015-03-20.
 */
public class VersionConfirm extends Dialog {
    public VersionConfirm(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.version_confirm);

        setLayout();
        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getContext().getString(R.string.store_domain)));
                getContext().startActivity(intent);
                System.exit(0);
            }
        });
    }
    View mCancle;
    View mUpdate;
    private void setLayout() {
        mCancle = findViewById(R.id.cancel);
        mUpdate = findViewById(R.id.update);
    }
}
