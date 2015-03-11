package co.hasBeen.social;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Follow;
import co.hasBeen.model.api.User;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.profile.follow.DoFollowListner;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FbFriendsItem {
    List<Follow> mFollowing;
    Context mContext;
    String mAccessToken;

    public FbFriendsItem(List<Follow> mFollowing, Context mContext) {
        this.mFollowing = mFollowing;
        this.mContext = mContext;
        mAccessToken = Session.getString(mContext, "accessToken", null);
    }

    public Follow getItem(int position) {
        return mFollowing.get(position);
    }


    public View getView(int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.follow_item, null);
        Follow follow = getItem(position);
        User toUser = follow.getToUser();

        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView profileName = (TextView) view.findViewById(R.id.profileName);
        TextView followSatus = (TextView) view.findViewById(R.id.followStatus);
        ImageView followImage = (ImageView) view.findViewById(R.id.followImage);

        Glide.with(mContext).load(toUser.getImageUrl()).asBitmap().transform(new CircleTransform(mContext)).into(profileImage);
        profileName.setText(Util.parseName(toUser, mContext));
        profileImage.setOnClickListener(new ProfileClickListner(mContext, toUser.getId()));
        profileName.setOnClickListener(new ProfileClickListner(mContext, toUser.getId()));
        followSatus.setText( mContext.getString(R.string.follow_status,toUser.getFollowerCount(),toUser.getFollowingCount()));
        if (follow.getFollowingId() == null) {
            followImage.setImageResource(R.drawable.follow_gray);
        } else {
            followImage.setImageResource(R.drawable.following_selector);
        }
        followImage.setOnClickListener(new DoFollowListner(mContext, mAccessToken, toUser.getId(), follow));
        return view;
    }

}