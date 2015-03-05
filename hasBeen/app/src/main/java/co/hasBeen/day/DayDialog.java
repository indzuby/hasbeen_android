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
public class DayDialog extends Dialog{

    View.OnClickListener mDel;
    View.OnClickListener mEdit;
    boolean isReport;
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
    public DayDialog(Context context, View.OnClickListener del,View.OnClickListener edit) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        mDel = del;
        mEdit = edit;
        isReport = false;
    }
    public DayDialog(Context context, View.OnClickListener del,View.OnClickListener edit,boolean isReport) {
        // Dialog 배경을 투명 처리 해준다.
        this(context,del,edit);
        this.isReport = isReport;
    }
    private TextView mRemoveButton;
    private TextView mEditButton;
        /*
     * Layout
     */
    private void setLayout(){
        mRemoveButton = (TextView) findViewById(R.id.removeDay);
        mEditButton = (TextView) findViewById(R.id.editDay);
        if(isReport) {
            mRemoveButton.setText("Report this day trip");
            mEditButton.setText("Share this day trip");
        }
    }
}