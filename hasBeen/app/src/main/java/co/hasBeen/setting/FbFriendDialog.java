package co.hasBeen.setting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Follow;
import co.hasBeen.profile.follow.FollowingAdapter;
import co.hasBeen.social.FbFriendsAsyncTask;

/**
 * Created by 주현 on 2015-02-11.
 */
public class FbFriendDialog extends Dialog {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.facebook_dialog);

        setLayout();
    }
    String mAccessToken;
    public FbFriendDialog(Context context, String accessToken) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        mAccessToken = accessToken;
    }
    ListView mListView;
    private void setLayout(){
        mListView = (ListView) findViewById(R.id.listView);
        new FbFriendsAsyncTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    List<Follow> users = (List) msg.obj;
                    FollowingAdapter adapter = new FollowingAdapter(users,getContext());
                    mListView.setAdapter(adapter);
                }
            }
        }).execute(mAccessToken);
    }
}