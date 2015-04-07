package co.hasBeen.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.localytics.android.Localytics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.model.api.RecentSearch;
import co.hasBeen.model.api.User;
import co.hasBeen.utils.HasBeenFragment;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-24.
 */
public class PeopleFragment extends HasBeenFragment{
    final static String TYPE = "USER";
    String mAccessToken;
    List<User> mUserList;
    String mKeyword;
    ListView mListView;
    DataBaseHelper database;
    UserAdapter mUserAdapter;
    boolean isComplete;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.search_people, container, false);
        mAccessToken = Session.getString(getActivity(), "accessToken", null);
        mListView = (ListView)mView.findViewById(R.id.listView);
        database = new DataBaseHelper(getActivity());
        initDefault();
        return mView;
    }

    protected void initDefault(){
        // database
        RecentAdapter adapter = new RecentAdapter(getActivity(),TYPE);
        mListView.setAdapter(adapter);
    }
    SearchPeopleAsyncTask searchPeopleAsyncTask;
    public void doSearch(String keyword){
        mKeyword = keyword;
        mUserList = new ArrayList<>();
        mUserAdapter = new UserAdapter(mUserList,getActivity());
        mListView.setAdapter(mUserAdapter);
        isComplete = false;
        startLoading();
        searchPeopleAsyncTask = new SearchPeopleAsyncTask(userHandler);
        searchPeopleAsyncTask.execute(mAccessToken,keyword);
        insertRecentKeyword(keyword);
    }
    protected void insertRecentKeyword(String keyword) {
        RecentSearch recent = new RecentSearch();
        recent.setKeyword(keyword);
        recent.setType(TYPE);
        recent.setCreateDate(new Date().getTime());
        try {
            database.insertRecent(recent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    Handler userHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0) {
                List<User> users= (List) msg.obj;
                for(User user : users)
                    mUserList.add(user);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mUserAdapter.notifyDataSetChanged();
                    }
                });
                if(users.size()<=0)
                    isComplete = true;
                init();
            }else {
            }
            stopLoading();
        }
    };
    protected void init(){
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(visibleItemCount<totalItemCount && firstVisibleItem+visibleItemCount>=totalItemCount) {
                    if(!isLoading && !isComplete) {
                        startLoading();
                        User user = mUserList.get(mUserList.size()-1);
                        searchPeopleAsyncTask = new SearchPeopleAsyncTask(userHandler);
                        searchPeopleAsyncTask.execute(mAccessToken,mKeyword,user.getPage());
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("User Search");
        Localytics.upload();
    }

    @Override
    public void onDestroy() {
        if(searchPeopleAsyncTask !=null)
            searchPeopleAsyncTask.cancel(true);
        System.gc();
        super.onDestroy();
    }
}
