package co.hasBeen.social;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.hasBeen.R;
import co.hasBeen.model.api.Loved;
import co.hasBeen.model.api.Social;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-09.
 */
public class LoveListner implements View.OnClickListener{
    Context mContext;
    Social mSocial;
    String mAccessToken;
    String mType;
    TextView mLikeCount;
    public LoveListner(Context mContext, Social social,String mType,TextView loveCount) {
        this.mContext = mContext;
        mAccessToken = Session.getString(mContext,"accessToken",null);
        this.mSocial =social;
        this.mLikeCount = loveCount;
        this.mType = mType;
    }

    @Override
    public void onClick(View v) {
        final ImageView loveButton;
        loveButton = (ImageView) v.findViewById(R.id.love);
        final TextView loveText;
        loveText = (TextView) v.findViewById(R.id.loveText);
        if(mSocial.getLove()==null) {
            new LoveAsyncTask(new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0) {
                        loveButton.setImageResource(R.drawable.photo_like_pressed);
                        Loved love = new Loved();
                        love.setId((Long) msg.obj);
                        mSocial.setLove(love);
                        mSocial.setLoveCount(mSocial.getLoveCount()+1);
                        mLikeCount.setText(mContext.getString(R.string.like_count, mSocial.getLoveCount()));
                        loveText.setTextColor(mContext.getResources().getColor(R.color.light_black));
                    }
                }
            }).execute(mAccessToken,mType,mSocial.getId());
        }else {
            new LoveDelAsyncTask(new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0) {
                        loveButton.setImageResource(R.drawable.photo_like);
                        mSocial.setLove(null);
                        mSocial.setLoveCount(mSocial.getLoveCount()-1);
                        mLikeCount.setText(mContext.getString(R.string.like_count, mSocial.getLoveCount()));
                        loveText.setTextColor(mContext.getResources().getColor(R.color.light_gray));
                    }
                }
            }).execute(mAccessToken,mSocial.getLove().getId());
        }
    }
}
