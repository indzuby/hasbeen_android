package co.hasBeen.comment;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by 주현 on 2015-02-06.
 */
public class EnterCommentListner implements View.OnClickListener{
    Context mContext;
    String type;
    Long id;
    boolean flag = false;
    int mCount;
    public EnterCommentListner(Context mContext, String type, Long id,int count) {
        this.mContext = mContext;
        this.type = type;
        this.id = id;
        this.mCount = count;
    }

    @Override
    public void onClick(View v) {

        if (!flag) {
            flag = true;
            Intent intent = new Intent(mContext, CommentView.class);
            intent.putExtra("type",type);
            intent.putExtra("id",id);
            intent.putExtra("commentCount",mCount);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            flag = false;
        }
    }
}
