package co.hasBeen.day;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import co.hasBeen.R;
import co.hasBeen.utils.ConfirmDialog;

/**
 * Created by 주현 on 2015-02-11.
 */
public class PositionDialog extends Dialog {

    View.OnClickListener mChange;
    View.OnClickListener mMerge;
    View.OnClickListener mRemove;
    boolean isFirst;
    Long mid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.5f;
            getWindow().setAttributes(lpWindow);

            setContentView(R.layout.position_dialog);

        setLayout();
        mChangeButton.setOnClickListener(mChange);
        mMergeButton.setOnClickListener(mMerge);
        if(mRemove!=null) {
            mRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmDialog dialog = new ConfirmDialog(getContext(),mRemove);
                    dialog.show();
                    dismiss();
                }
            });
        }
        findViewById(R.id.box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public PositionDialog(Context context, View.OnClickListener change, View.OnClickListener merge,View.OnClickListener remove) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mChange = change;
        mMerge = merge;
        isFirst = false;
        mRemove = remove;
    }

    public PositionDialog(Context context, View.OnClickListener change, View.OnClickListener merge,View.OnClickListener remove, boolean isFirst) {
        // Dialog 배경을 투명 처리 해준다.
        this(context, change, merge,remove);
        this.isFirst = isFirst;
    }
    private TextView mChangeButton;
    private TextView mMergeButton;
    private TextView mRemoveButton;
    /*
 * Layout
 */
    private void setLayout() {
        mChangeButton = (TextView) findViewById(R.id.chagePlace);
        mMergeButton = (TextView) findViewById(R.id.mergePlace);
        mRemoveButton = (TextView) findViewById(R.id.removePlace);
        if (isFirst)
            mMergeButton.setVisibility(View.GONE);
        if(mRemove==null)
            mRemoveButton.setVisibility(View.GONE);
    }
}