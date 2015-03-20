package co.hasBeen.profile.follow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import co.hasBeen.model.api.Follow;
import co.hasBeen.utils.Session;
import co.hasBeen.R;

/**
 * Created by 주현 on 2015-02-02.
 */
public class FollowerFragment extends Fragment {
    View mView;
    List<Follow> mFollowers;
    ListView mList;
    TextView mCount;
    Long mUserId;
    String mAccessToken;
    View mLoading;
    boolean isLoading;
    Handler followerHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(getActivity()!=null) {
                switch (msg.what) {
                    case 0:
                        mFollowers = (List) msg.obj;
                        initFollower();
                        break;
                    case -1:
                        break;
                }
                stopLoading();
            }
        }
    };
    protected void initFollower(){
        mCount.setText(getString(R.string.following_count,mFollowers.size()));

        FollowerAdapter followerAdapter = new FollowerAdapter(mFollowers, getActivity());
        mList.setAdapter(followerAdapter);
        followerAdapter.mCount = mCount;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.follower, container, false);
        mUserId = getArguments().getLong("userId");
        mAccessToken = Session.getString(getActivity(), "accessToken", null);
        new FollowerAsyncTask(followerHandler).execute(mAccessToken,mUserId);
        init();
        return mView;
    }
    protected void init() {
        mList = (ListView) mView.findViewById(R.id.list);

        View mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.follower_header, null, false);
        mCount = (TextView) mHeaderView.findViewById(R.id.count);
        mList.addHeaderView(mHeaderView);
        mLoading = mView.findViewById(R.id.refresh);
        startLoading();
    }

    protected void startLoading() {
        isLoading = true;
        mLoading.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        isLoading = false;
        mLoading.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}