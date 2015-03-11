package co.hasBeen.profile.follow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.model.api.Follow;
import co.hasBeen.model.api.User;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;
import co.hasBeen.R;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FollowerAdapter extends BaseAdapter {
    List<Follow> mFollows;
    Context mContext;
    String mAccessToken;
    public TextView mCount;
    public FollowerAdapter(List<Follow> mFollows, Context mContext) {
        this.mFollows = mFollows;
        this.mContext = mContext;
        mAccessToken = Session.getString(mContext, "accessToken", null);
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
        Follow follow = getItem(position);
        final User fromUser = follow.getFromUser();

        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView profileName = (TextView) view.findViewById(R.id.profileName);
        TextView followSatus = (TextView) view.findViewById(R.id.followStatus);
        final ImageView followImage = (ImageView) view.findViewById(R.id.followImage);

        Glide.with(mContext).load(fromUser.getImageUrl()).asBitmap().transform(new CircleTransform(mContext)).into(profileImage);
        profileName.setText(Util.parseName(fromUser, mContext));
        profileImage.setOnClickListener(new ProfileClickListner(mContext, fromUser.getId()));
        profileName.setOnClickListener(new ProfileClickListner(mContext, fromUser.getId()));
        followSatus.setText(mContext.getString(R.string.follow_status,fromUser.getFollowerCount(),fromUser.getFollowingCount()));
        if(follow.getFollowingId()==null) {
            followImage.setImageResource(R.drawable.follow_gray);
        }else {
            followImage.setImageResource(R.drawable.following_selector);
        }
        followImage.setOnClickListener(new DoFollowListner(mContext,mAccessToken,fromUser.getId(),follow));
        return view;
    }
}