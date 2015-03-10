package co.hasBeen.social;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by 주현 on 2015-03-02.
 */
public class ShareListner implements View.OnClickListener {
    Context context;
    String url;
    public ShareListner(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    public void onClick(View v) {
        Intent msg = new Intent(Intent.ACTION_SEND);
        msg.addCategory(Intent.CATEGORY_DEFAULT);
        msg.putExtra(Intent.EXTRA_TEXT, url);
        msg.putExtra(Intent.EXTRA_STREAM,url);
        msg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        msg.setType("text/plain");
        context.startActivity(Intent.createChooser(msg, "Share"));
    }
}
