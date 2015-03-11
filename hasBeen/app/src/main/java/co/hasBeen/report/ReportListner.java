package co.hasBeen.report;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import co.hasBeen.R;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-11.
 */
public class ReportListner implements View.OnClickListener{
    String mAccessToken;
    String type;
    Long id;
    Context context;
    Dialog dialog;
    public ReportListner(String type, Long id, Context context, Dialog dialog) {
        this.type = type;
        this.id = id;
        this.context = context;
        mAccessToken = Session.getString(context,"accessToken",null);
        this.dialog = dialog;
    }

    @Override
    public void onClick(View v) {
        new ReportAsyncTask(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    Toast.makeText(context, context.getString(R.string.report_ok), Toast.LENGTH_LONG).show();
                   dialog.dismiss();
                }
            }
        }).execute(mAccessToken,type+"/"+id+"/report");
    }
}
