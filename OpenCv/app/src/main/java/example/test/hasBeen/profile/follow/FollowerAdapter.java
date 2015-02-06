package example.test.hasBeen.profile.follow;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.Follow;
import example.test.hasBeen.model.api.User;
import example.test.hasBeen.profile.ProfileClickListner;
import example.test.hasBeen.utils.CircleTransform;
import example.test.hasBeen.utils.Session;
import example.test.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FollowerAdapter extends BaseAdapter {
    List<Follow> mFollows;
    Context mContext;
    String mAccessToken;
    public FollowerAdapter(List<Follow> mFollows, Context mContext) {
        this.mFollows = mFollows;
        this.mContext = mContext;
        mAccessToken = Session.getString(mContext,"accessToken",null);
    }

    @Override
    public int getCount() {
        return mFollows.size();
    }

    @Override
    public Follow getItem(int position) {
        return mFollows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.follow_item,null);
        }
        Follow Follow = getItem(position);
        final User fromUser = Follow.getFromUser();

        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView profileName = (TextView) view.findViewById(R.id.profileName);
        TextView followSatus = (TextView) view.findViewById(R.id.followStatus);
        final ImageView followImage = (ImageView) view.findViewById(R.id.followImage);

        Glide.with(mContext).load(fromUser.getImageUrl()).asBitmap().transform(new CircleTransform(mContext)).into(profileImage);
        profileName.setText(Util.parseName(fromUser,0));
        profileImage.setOnClickListener(new DoProfileClickListner(mContext, fromUser.getId()));
        profileName.setOnClickListener(new DoProfileClickListner(mContext, fromUser.getId()));
        followSatus.setText(fromUser.getFollowerCount()+" Follower · "+fromUser.getFollowingCount()+" following");
        if(Follow.getFollowingId()==null) {
            followImage.setImageResource(R.drawable.follow_gray);
            followImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DoFollowAsyncTask(new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if(msg.what==0) {
                                Toast.makeText(mContext,"Follow success",Toast.LENGTH_LONG).show();

                            }
                        }
                    }).execute(mAccessToken,fromUser.getId());
                }
            });
        }else {
            followImage.setImageResource(R.drawable.following_green);
            followImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        return view;
    }
    class DoProfileClickListner extends ProfileClickListner {
        public DoProfileClickListner(Context context, Long id) {
            super(context, id);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
        }
    }
}