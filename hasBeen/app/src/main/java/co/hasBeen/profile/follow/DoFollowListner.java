package co.hasBeen.profile.follow;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Follow;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-10.
 */
public class DoFollowListner implements View.OnClickListener {
    Context mContext;
    String mAccessToken;
    Long id;
    Follow mFollow;
    TextView mFollowStatus;
    BaseAdapter mAdapter;
    List<Follow> mFollows;
    public DoFollowListner(Context mContext, String mAccessToken, Long id,Follow follow) {
        this.mContext = mContext;
        this.mAccessToken = mAccessToken;
        this.id = id;
        mFollow = follow;
    }
    public DoFollowListner(Context mContext, String mAccessToken, Long id,Follow follow,TextView followStatus,List<Follow> follows,BaseAdapter adapter) {
        this(mContext,mAccessToken,id,follow);
        mFollowStatus = followStatus;
        mAdapter = adapter;
        mFollows = follows;
    }

    @Override
    public void onClick(View v) {
        ImageView icon = (ImageView) v;
        if(mFollow.getFollowingId()==null) {
            icon.setImageResource(R.drawable.following_selector);
            Session.putBoolean(mContext,"following",true);
            new DoFollowAsyncTask(new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0) {
                        Toast.makeText(mContext, mContext.getString(R.string.follow_success), Toast.LENGTH_LONG).show();
                        mFollow.setFollowingId((Long)msg.obj);
                    }
                }
            }).execute(mAccessToken,id);
        }else {
            icon.setImageResource(R.drawable.follow_gray);
            new UnFollowAsyncTask(new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0) {
                        Toast.makeText(mContext, mContext.getString(R.string.unfollow_success), Toast.LENGTH_LONG).show();
                        mFollow.setFollowingId(null);
                        if(mAdapter!=null && mAdapter.getClass().equals(FollowingAdapter.class)) {
                            mFollows.remove(mFollow);
                            mAdapter.notifyDataSetChanged();
                            mFollowStatus.setText(mContext.getString(R.string.following_count,mFollows.size()));
                        }
                    }
                }
            }).execute(mAccessToken,mFollow.getFollowingId());

        }
    }
}
