package co.hasBeen.day;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import co.hasBeen.R;

/**
 * Created by 주현 on 2015-02-11.
 */
public class PositionDialog extends Dialog {

    View.OnClickListener mDel;
    View.OnClickListener mEdit;
    boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.day_dialog);

        setLayout();
        mRemoveButton.setOnClickListener(mDel);
        mEditButton.setOnClickListener(mEdit);
    }

    public PositionDialog(Context context, View.OnClickListener del, View.OnClickListener edit) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDel = del;
        mEdit = edit;
        isFirst = false;
    }

    public PositionDialog(Context context, View.OnClickListener del, View.OnClickListener edit, boolean isFirst) {
        // Dialog 배경을 투명 처리 해준다.
        this(context, del, edit);
        this.isFirst = isFirst;
    }
    private TextView mRemoveButton;
    private TextView mEditButton;

    /*
 * Layout
 */
    private void setLayout() {
        mRemoveButton = (TextView) findViewById(R.id.removeDay);
        mEditButton = (TextView) findViewById(R.id.editDay);
        mRemoveButton.setText("Change Position");
        mEditButton.setText("Merge with above place");
        if (isFirst)
            mEditButton.setVisibility(View.GONE);
    }
}