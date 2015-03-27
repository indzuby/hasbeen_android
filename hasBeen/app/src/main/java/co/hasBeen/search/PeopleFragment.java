package co.hasBeen.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.hasBeen.R;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-24.
 */
public class PeopleFragment extends Fragment{
    View mView;
    String mAccessToken;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.follower, container, false);
        mAccessToken = Session.getString(getActivity(), "accessToken", null);
        init();
        return mView;
    }
    protected void init(){

    }
}