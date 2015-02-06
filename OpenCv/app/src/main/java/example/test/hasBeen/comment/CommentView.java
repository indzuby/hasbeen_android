package example.test.hasBeen.comment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.Comment;
import example.test.hasBeen.utils.CacheControl;
import example.test.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-28.
 */
public class CommentView extends ActionBarActivity {
    CommnetAdapter mCommentAdapter;
    ListView mListView;
    EditText mEnterComment;
    List<Comment> mCommentList;
    String mType;
    Long mId;
    String mAccessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getIntent().getStringExtra("type");
        mId = getIntent().getLongExtra("id", 0);
        mAccessToken = Session.getString(this, "accessToken", null);
        new CommentAsyncTask(handler).execute(mAccessToken,mType,mId);
        setContentView(R.layout.comments);
    }
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                mCommentList = (List) msg.obj;
                init();
            }
        }
    };
    protected void init(){
        initActionBar();
        mListView = (ListView) findViewById(R.id.commetList);
        mCommentAdapter = new CommnetAdapter(this,mCommentList);
        View enterView= LayoutInflater.from(this).inflate(R.layout.comment_footer, null, false);
        mListView.addFooterView(enterView);
        mListView.setAdapter(mCommentAdapter);
        mListView.setSelection(mListView.getCount());
        mEnterComment = (EditText) enterView.findViewById(R.id.enterComment);
        mEnterComment.requestFocus();
        ImageView send = (ImageView) enterView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View v) {
                if(!flag) {
                    flag = true;
                    String contents = mEnterComment.getText().toString();
                    mEnterComment.setText("");
                    Toast.makeText(getBaseContext(), "Complete written", Toast.LENGTH_LONG).show();
                    new WriteCommentAsyncTask(new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            flag = false;
                            if(msg.what==0) {
                                Comment comment = (Comment) msg.obj;
                                mCommentList.add(comment);
                                mCommentAdapter.notifyDataSetChanged();
                                mListView.setSelection(mCommentList.size()-1);
                            }
                        }
                    }).execute(mAccessToken, mType, mId, contents);
                }
            }
        });
    }
    protected void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText("Comments");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
        CacheControl.deleteCache(this);
    }
}
