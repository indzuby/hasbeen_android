package co.hasBeen.comment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.api.Comment;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.report.ReportListner;
import co.hasBeen.utils.CacheControl;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

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
    Long mLastCommentId;
    int mCommentCount;
    boolean hasMoreComment = false;
    View mMoreComment;
    CommentDialog mCommentDialog;
    Long mMyid;
    Handler removeHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                mCommentDialog.dismiss();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getIntent().getStringExtra("type");
        mId = getIntent().getLongExtra("id", 0);
        mAccessToken = Session.getString(this, "accessToken", null);
        mMyid = Session.getLong(this, "myUserid", 0);
        mCommentCount = getIntent().getIntExtra("commentCount",0);
        new CommentAsyncTask(handler).execute(mAccessToken,mType,mId);
        setContentView(R.layout.comments);
        init();
    }
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                List<Comment> comments = (List) msg.obj;
                Collections.reverse(comments);
                for(Comment comment : comments) {
                    mCommentList.add(0,comment);
                    mLastCommentId = comment.getId();
                }
                mCommentAdapter.notifyDataSetChanged();
                initMoreComment();
            }
        }
    };
    protected void initMoreComment(){
        if(mCommentList.size()<mCommentCount && !hasMoreComment) {
            hasMoreComment = true;
            mMoreComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CommentAsyncTask(handler).execute(mAccessToken,mType,mId,mLastCommentId);
                }
            });
            mListView.addHeaderView(mMoreComment);

        }else if(mCommentList.size() == mCommentCount) {
            mListView.removeHeaderView(mMoreComment);
        }
    }
    protected void init(){
        initActionBar();
        mListView = (ListView) findViewById(R.id.commetList);

        mCommentList = new ArrayList<>();
        mCommentAdapter = new CommnetAdapter(this,mCommentList);
        mMoreComment = LayoutInflater.from(getBaseContext()).inflate(R.layout.more_comments, null);
        View enterView= LayoutInflater.from(this).inflate(R.layout.comment_footer, null, false);
        mListView.addFooterView(enterView);
        mListView.setAdapter(mCommentAdapter);
        mListView.setSelection(mListView.getCount());
        mEnterComment = (EditText) enterView.findViewById(R.id.enterComment);
        mEnterComment.requestFocus();
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                registerForContextMenu(view);
                Log.v("long clicked", "pos: " + position);
                final int index;
                if(hasMoreComment)
                    index = position-1;
                else 
                    index = position;
                final Long commentid = mCommentList.get(index).getId();
                if(mCommentList.get(index).getUser().getId() == mMyid) {
                    View.OnClickListener del = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new EditCommentAsyncTask(removeHandler).execute(mAccessToken, commentid, "delete");
                            mCommentList.remove(index);
                            mCommentCount--;
                            mCommentAdapter.notifyDataSetChanged();
//                            Toast.makeText(getBaseContext(), "Complete remove", Toast.LENGTH_LONG).show();
                        }
                    };
                    mCommentDialog = new CommentDialog(CommentView.this);
                    mCommentDialog.setListner(del);
                    mCommentDialog.show();
                }else {
                    mCommentDialog = new CommentDialog(CommentView.this,true);
                    View.OnClickListener report = new ReportListner("comments",commentid,getBaseContext(),mCommentDialog);
                    mCommentDialog.setListner(report);
                    mCommentDialog.show();
                }
                return true;
            }
        });
        ImageView send = (ImageView) enterView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View v) {
                if(!flag) {
                    flag = true;
                    String contents = mEnterComment.getText().toString();
                    mEnterComment.setText("");
//                    Toast.makeText(getBaseContext(), "Complete written", Toast.LENGTH_LONG).show();
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
                                Session.putBoolean(getBaseContext(),"commentWrite",true);
                                Gson gson = new Gson();
                                String data = gson.toJson(comment);
                                Session.putString(getBaseContext(),"comment",data);
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
        titleView.setText(getString(R.string.action_bar_comment_title));
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
    public static View makeComment(Context context,Comment comment){
        View commentView = LayoutInflater.from(context).inflate(R.layout.comment,null);
        TextView contents = (TextView) commentView.findViewById(R.id.contents);
        TextView commentTime = (TextView) commentView.findViewById(R.id.commentTime);
        contents.setText(comment.getContents());
        commentTime.setText(HasBeenDate.getGapTime(comment.getCreatedTime(),context));
        ImageView profileImage = (ImageView) commentView.findViewById(R.id.profileImage);
        TextView profileName = (TextView) commentView.findViewById(R.id.profileName);
        Glide.with(context).load(comment.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(context)).into(profileImage);
        profileImage.setOnClickListener(new ProfileClickListner(context,comment.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(context, comment.getUser().getId()));
        profileName.setText(Util.parseName(comment.getUser(),context));
        return commentView;
    }
}
