package co.hasBeen.comment;

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
public class CommentDialog extends Dialog{

    View.OnClickListener mDel;
    boolean isReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.comment_dialog);

        setLayout();
        mRemoveButton.setOnClickListener(mDel);
    }
    public CommentDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
    }
    public CommentDialog(Context context,boolean isReport) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.isReport = isReport;
    }
    public void setListner(View.OnClickListener del){
        mDel = del;
    }
    private TextView mRemoveButton;
    private TextView mEditButton;
        /*
     * Layout
     */
    private void setLayout(){
        mRemoveButton = (TextView) findViewById(R.id.removeComment);
        mEditButton = (TextView) findViewById(R.id.editComment);
        mEditButton.setVisibility(View.GONE);
        if(isReport)
            mRemoveButton.setText(getContext().getString(R.string.report_comment));
    }
}