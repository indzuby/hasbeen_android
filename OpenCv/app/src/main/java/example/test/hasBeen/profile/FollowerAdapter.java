package example.test.hasBeen.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.Follower;
import example.test.hasBeen.model.api.User;
import example.test.hasBeen.utils.CircleTransform;
import example.test.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FollowerAdapter extends BaseAdapter {
    List<Follower> mFollowers;
    Context mContext;

    public FollowerAdapter(List<Follower> mFollowers, Context mContext) {
        this.mFollowers = mFollowers;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mFollowers.size();
    }

    @Override
    public Follower getItem(int position) {
        return mFollowers.get(position);
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
        Follower follower = getItem(position);
        User toUser = follower.getToUser();

        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView profileName = (TextView) view.findViewById(R.id.profileName);
        TextView followSatus = (TextView) view.findViewById(R.id.followStatus);
        ImageView followImage = (ImageView) view.findViewById(R.id.followImage);

        Glide.with(mContext).load(toUser.getImageUrl()).asBitmap().transform(new CircleTransform(mContext)).into(profileImage);
        profileName.setText(Util.parseName(toUser,0));
        followSatus.setText(toUser.getFollowerCount()+" follower · "+toUser.getFollowingCount()+" following");
        if(follower.getFollowingId()==null) {
            followImage.setImageResource(R.drawable.follow_gray);
            followImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
}
