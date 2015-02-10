package example.test.hasBeen.profile.follow;

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

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.Follow;

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
            new DoFollowAsyncTask(new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0) {
                        Toast.makeText(mContext, "Follow success", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(mContext, "Unfollow success", Toast.LENGTH_LONG).show();
                        mFollow.setFollowingId(null);
                        if(mAdapter!=null && mAdapter.getClass().equals(FollowingAdapter.class)) {
                            mFollows.remove(mFollow);
                            mAdapter.notifyDataSetChanged();
                            mFollowStatus.setText(mFollows.size() + " following");
                        }
                    }
                }
            }).execute(mAccessToken,mFollow.getFollowingId());

        }
    }
}
