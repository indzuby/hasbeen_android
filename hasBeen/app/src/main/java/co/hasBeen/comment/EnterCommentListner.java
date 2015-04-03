package co.hasBeen.comment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.hasBeen.R;
import co.hasBeen.model.api.Comment;
import co.hasBeen.model.api.Social;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-06.
 */
public class EnterCommentListner implements View.OnClickListener {
    Context mContext;
    String type;
    boolean flag = false;
    Social mSocial;
    TextView mCommentCount;
    LinearLayout mCommentBox;
    public EnterCommentListner(Context mContext, String type, Social social, TextView commentCount) {
        this.mContext = mContext;
        this.type = type;
        mSocial = social;
        mCommentCount = commentCount;
    }
    public EnterCommentListner(Context mContext, String type, Social social, TextView commentCount,LinearLayout commentBox) {
        this.mContext = mContext;
        this.type = type;
        mSocial = social;
        mCommentCount = commentCount;
        this.mCommentBox = commentBox;
    }

    @Override
    public void onClick(View v) {

        if (!flag) {
            flag = true;
            Intent intent = new Intent(mContext, CommentView.class);
            intent.putExtra("type", type);
            intent.putExtra("id", mSocial.getId());
            intent.putExtra("commentCount", mSocial.getCommentCount());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            startCallBack();
            flag = false;
        }
    }

    void startCallBack() {
        Session.putBoolean(mContext, "commentWrite", false);
        Session.putString(mContext,"comment",null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag;
                do {
                    flag = Session.getBoolean(mContext, "commentWrite", false);
                    if (flag) {
                        mSocial.setCommentCount(mSocial.getCommentCount() + 1);
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCommentCount.setText(mContext.getString(R.string.comment_count, mSocial.getCommentCount()));
                                if(mCommentBox!=null) {
                                    String json = Session.getString(mContext,"comment",null);
                                    if(json!=null) {
                                        Comment comment = JsonConverter.convertJsonToComment(json);
                                        mCommentBox.addView(CommentView.makeComment(mContext, comment,null));
                                    }
                                }

                            }
                        });
                    }

                } while (!flag);
            }
        }).start();
    }
}
