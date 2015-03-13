package co.hasBeen.social;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import co.hasBeen.R;
import co.hasBeen.model.api.Social;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-02.
 */
public class ShareListner implements View.OnClickListener {
    Context context;
    String type;
    Long id;
    String url;
    TextView socialAction;
    Social social;
    public ShareListner(Context context, String type, Social social, TextView socialAction) {
        this.context = context;
        this.type = type;
        this.social = social;
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
        social.setShareCount(social.getShareCount()+1);
        socialAction.setText(context.getString(R.string.share_count, social.getShareCount()));
    }
}
