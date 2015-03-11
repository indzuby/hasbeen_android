package co.hasBeen.social;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import co.hasBeen.R;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-02.
 */
public class ShareListner implements View.OnClickListener {
    Context context;
    String type;
    Long id;
    String url;
    Integer loveCount;
    Integer commentCount;
    Integer shareCount;
    TextView socialAction;

    public ShareListner(Context context, String type, Long id, Integer loveCount, Integer commentCount, Integer shareCount, TextView socialAction) {
        this.context = context;
        this.type = type;
        this.id = id;
        this.loveCount = loveCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
        this.socialAction = socialAction;
        url = Session.WEP_DOMAIN + type+"/" + id;
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
        String accessToken  = Session.getString(context, "accessToken", null);
        new ShareCountAsyncTask().execute(accessToken,type,id);
        shareCount++;
        socialAction.setText(context.getString(R.string.social_status,loveCount,commentCount,shareCount));
    }
}
