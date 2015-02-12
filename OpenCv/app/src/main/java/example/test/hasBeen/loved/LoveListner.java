package example.test.hasBeen.loved;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import example.test.hasBeen.R;
import example.test.hasBeen.day.LoveAsyncTask;
import example.test.hasBeen.model.api.Loved;
import example.test.hasBeen.model.database.Day;
import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-09.
 */
public class LoveListner implements View.OnClickListener{
    Context mContext;
    boolean isLoved;
    Day mDay;
    Loved mLove;
    Photo mPhoto;
    String mAccessToken;
    String mType;
    Long mid;
    TextView mSocialAction;
    Typeface medium,regular;
    public LoveListner(Context mContext, Object data,String type,TextView socialAction) {
        this.mContext = mContext;
        mAccessToken = Session.getString(mContext,"accessToken",null);
        mType = type;
        if(type.equals("days")) {
            mDay = (Day) data;
            mLove = mDay.getLove();
            mid = mDay.getId();
        }else {
            mPhoto = (Photo) data;
            mLove = mPhoto.getLove();
            mid = mPhoto.getId();
        }
        if(mLove==null)
            isLoved = true;
        else
            isLoved = false;
        mSocialAction = socialAction;
        medium = Typeface.createFromAsset(mContext.getAssets(),"fonts/Roboto-Medium.ttf");
        regular = Typeface.createFromAsset(mContext.getAssets(),"fonts/Roboto-Regular.ttf");
    }

    @Override
    public void onClick(View v) {
        final ImageView loveButton;
        loveButton = (ImageView) v.findViewById(R.id.love);
        final TextView loveText;
        loveText = (TextView) v.findViewById(R.id.loveText);
        if(isLoved) {
            new LoveAsyncTask(new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0) {
                        loveButton.setImageResource(R.drawable.photo_like_pressed);
                        Loved love = new Loved();
                        love.setId((Long) msg.obj);
                        if(mType.equals("days")) {
                            mDay.setLove(love);
                            mDay.setLoveCount(mDay.getLoveCount()+1);
                            mSocialAction.setText(mDay.getLoveCount() + " Likes · " + mDay.getCommentCount() + " Commnents · " + mDay.getShareCount() + " Shared");
                        }else {
                            mPhoto.setLove(love);
                            mPhoto.setLoveCount(mPhoto.getLoveCount() + 1);
                            mSocialAction.setText(mPhoto.getLoveCount() + " Likes · " + mPhoto.getCommentCount() + " Commnents · " + mPhoto.getShareCount() + " Shared");
                        }
                        mLove = love;
                        isLoved = false;
                        loveText.setTextColor(mContext.getResources().getColor(R.color.light_black));
                        loveText.setTypeface(medium);

                    }

                }
            }).execute(mAccessToken,mType,mid);
        }else {
            new LoveDelAsyncTask(new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0) {
                        loveButton.setImageResource(R.drawable.photo_like);
                        if(mType.equals("days")) {
                            mDay.setLove(null);
                            mDay.setLoveCount(mDay.getLoveCount() - 1);
                            mSocialAction.setText(mDay.getLoveCount() + " Likes · " + mDay.getCommentCount() + " Commnents · " + mDay.getShareCount() + " Shared");
                        }else {
                            mPhoto.setLove(null);
                            mPhoto.setLoveCount(mPhoto.getLoveCount() - 1);
                            mSocialAction.setText(mPhoto.getLoveCount() + " Likes · " + mPhoto.getCommentCount() + " Commnents · " + mPhoto.getShareCount() + " Shared");
                        }
                        isLoved = true;
                        mLove = null;
                        loveText.setTextColor(mContext.getResources().getColor(R.color.light_gray));
                        loveText.setTypeface(regular);
                    }
                }
            }).execute(mAccessToken,mLove.getId());
        }
    }
}
