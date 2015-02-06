package example.test.hasBeen.profile.follow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.Follow;
import example.test.hasBeen.utils.Session;

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
    Handler followerHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mFollowers = (List) msg.obj;
                    initFollower();
                    break;
                case -1:
                    break;
            }
        }
    };
    protected void initFollower() {
        mCount.setText(mFollowers.size() + " followers");

        FollowerAdapter followerAdapter = new FollowerAdapter(mFollowers, getActivity());
        mList.setAdapter(followerAdapter);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.follower, container, false);
        mUserId = getArguments().getLong("userId");
        mAccessToken = Session.getString(getActivity(),"accessToken",null);
        new FollowerAsyncTask(followerHandler).execute(mAccessToken,mUserId);
        init();
        return mView;
    }
    protected void init() {
        mList = (ListView) mView.findViewById(R.id.list);

        View mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.follower_header, null, false);
        mCount = (TextView) mHeaderView.findViewById(R.id.count);
        mList.addHeaderView(mHeaderView);

    }
}