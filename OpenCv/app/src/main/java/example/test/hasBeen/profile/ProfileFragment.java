package example.test.hasBeen.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.User;
import example.test.hasBeen.utils.CircleTransform;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-30.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    final static int DAY = 1;
    final static int PHOTO = 2;
    final static int LOVE = 3;
    View mView;
    User mUser;
    int nowTab = DAY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.profile, container, false);
        new ProfileAsyncTask(handler).execute();
        init();
        return mView;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mUser = (User) msg.obj;
                    initProfile();
                    break;
                case -1:
                    break;
            }
        }
    };

    protected void init() {
        RelativeLayout dayButton = (RelativeLayout) mView.findViewById(R.id.dayButton);
        RelativeLayout photoButton = (RelativeLayout) mView.findViewById(R.id.photoButton);
        RelativeLayout likeButton = (RelativeLayout) mView.findViewById(R.id.likeButton);
        dayButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);
        likeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        clearSelect();
        switch (v.getId()) {
            case R.id.dayButton:
                nowTab = DAY;
                mView.findViewById(R.id.daySelectBar).setVisibility(View.VISIBLE);
                ((TextView) mView.findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_black));
                break;
            case R.id.photoButton:
                nowTab = PHOTO;
                mView.findViewById(R.id.photoSelectBar).setVisibility(View.VISIBLE);
                ((TextView) mView.findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_black));
                break;
            case R.id.likeButton:
                nowTab = LOVE;
                mView.findViewById(R.id.loveSelectBar).setVisibility(View.VISIBLE);
                ((TextView) mView.findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_black));
                break;
            default:
                break;
        }
    }

    protected void clearSelect() {

        mView.findViewById(R.id.loveSelectBar).setVisibility(View.GONE);
        ((TextView) mView.findViewById(R.id.loveCount)).setTextColor(getResources().getColor(R.color.light_gray));
        mView.findViewById(R.id.photoSelectBar).setVisibility(View.GONE);
        ((TextView) mView.findViewById(R.id.photoCount)).setTextColor(getResources().getColor(R.color.light_gray));
        mView.findViewById(R.id.daySelectBar).setVisibility(View.GONE);
        ((TextView) mView.findViewById(R.id.dayCount)).setTextColor(getResources().getColor(R.color.light_gray));
    }

    protected void initProfile() {
        ImageView coverImage = (ImageView) mView.findViewById(R.id.coverImage);
        ImageView profileImage = (ImageView) mView.findViewById(R.id.profileImage);
        TextView profileName = (TextView) mView.findViewById(R.id.profileName);
        TextView followStatus = (TextView) mView.findViewById(R.id.followStatus);
        ImageView setting = (ImageView) mView.findViewById(R.id.setting);
        TextView dayCount = (TextView) mView.findViewById(R.id.dayCount);
        TextView photoCount = (TextView) mView.findViewById(R.id.photoCount);
        TextView loveCount = (TextView) mView.findViewById(R.id.loveCount);
        Glide.with(getActivity()).load(mUser.getCoverPhoto().getLargeUrl()).into(coverImage);
        Glide.with(getActivity()).load(mUser.getImageUrl()).transform(new CircleTransform(getActivity())).into(profileImage);
        profileName.setText(Util.parseName(mUser, 0));
        followStatus.setText(mUser.getFollowerCount() + " Follower · " + mUser.getFollowingCount() + " Following");
        followStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowView.class);
                startActivity(intent);
            }
        });
        dayCount.setText(mUser.getDayCount() + "");
        photoCount.setText(mUser.getPhotoCount() + "");
        loveCount.setText(mUser.getLoveCount() + "");
    }
}
