package example.test.hasBeen.comment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.Comment;

/**
 * Created by zuby on 2015-01-28.
 */
public class CommentView extends ActionBarActivity {
    CommnetAdapter mCommnetAdapter;
    ListView mListView;
    EditText mEnterComment;
    List<Comment> mCommentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    protected void init(){
        setContentView(R.layout.comments);
        initActionBar();
        mListView = (ListView) findViewById(R.id.commetList);
        mCommentList = new ArrayList<>(20);
        for(int i = 0 ;i<20;i++) {
            mCommentList.add(new Comment());
        }
        mCommnetAdapter = new CommnetAdapter(this,mCommentList);
        View enterView= LayoutInflater.from(this).inflate(R.layout.comment_footer, null, false);
        mListView.addFooterView(enterView);
        mListView.setAdapter(mCommnetAdapter);
        mListView.setSelection(mListView.getCount());
        mEnterComment = (EditText) enterView.findViewById(R.id.enterComment);
        mEnterComment.requestFocus();
        ImageView send = (ImageView) enterView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place,null);
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
}
