package co.hasBeen.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.User;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.profile.ProfileFollowListner;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-02-02.
 */
public class UserAdapter extends BaseAdapter {
    List<User> mUsers;
    Context mContext;
    String mAccessToken;
    public UserAdapter(List<User> mUsers, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        mAccessToken = Session.getString(mContext, "accessToken", null);
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public User getItem(int position) {
        return mUsers.get(position);
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
        User user = getItem(position);

        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView profileName = (TextView) view.findViewById(R.id.name);
        TextView followSatus = (TextView) view.findViewById(R.id.followStatus);
        ImageView followImage = (ImageView) view.findViewById(R.id.followImage);
        followImage.setVisibility(View.GONE);
        Glide.with(mContext).load(user.getImageUrl()).placeholder(R.mipmap.profile_placeholder).transform(new CircleTransform(mContext)).into(profileImage);
        profileName.setText(Util.parseName(user, mContext));
        followSatus.setText(mContext.getString(R.string.follow_status,user.getFollowerCount(),user.getFollowingCount()));
        if(user.getFollow()==null || user.getFollow().getId()==null) {
            followImage.setImageResource(R.drawable.follow_gray);
        }else {
            followImage.setImageResource(R.drawable.following_selector);
        }
        followImage.setOnClickListener(new ProfileFollowListner(user.getFollow(),mAccessToken,user.getId()));
        view.setOnClickListener(new ProfileClickListner(mContext, user.getId()));
        return view;
    }
}