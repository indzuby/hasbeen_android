package co.hasBeen.photo;

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
public class PhotoDialog extends Dialog{

    View.OnClickListener mDel;
    View.OnClickListener mEdit;
    View.OnClickListener mCover;
    boolean isReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.photo_dialog);

        setLayout();
        mRemoveButton.setOnClickListener(mDel);
        mEditButton.setOnClickListener(mEdit);
        mCoverPhoto.setOnClickListener(mCover);
    }

    public void setCover(View.OnClickListener mCover) {
        this.mCover = mCover;
    }
    public PhotoDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        isReport = false;
    }
    public void setLisnter(View.OnClickListener del, View.OnClickListener edit){

        mDel = del;
        mEdit = edit;
    }
    public PhotoDialog(Context context,View.OnClickListener cover ,View.OnClickListener del, View.OnClickListener edit) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        mCover = cover;
        mDel = del;
        mEdit = edit;
        isReport = false;
    }
    public PhotoDialog(Context context,boolean isReport) {
        // Dialog 배경을 투명 처리 해준다.
        this(context);
        this.isReport = isReport;
    }
    private TextView mRemoveButton;
    private TextView mEditButton;
    private TextView mCoverPhoto;
        /*
     * Layout
     */
    private void setLayout(){
        mRemoveButton = (TextView) findViewById(R.id.removePhoto);
        mEditButton = (TextView) findViewById(R.id.editPhoto);
        mCoverPhoto = (TextView) findViewById(R.id.coverPhoto);
        if(isReport) {
            mCoverPhoto.setVisibility(View.GONE);
            mRemoveButton.setText(getContext().getString(R.string.report_photo));
            mEditButton.setText(getContext().getString(R.string.share_photo));
        }
    }
}