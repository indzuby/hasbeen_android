package example.test.hasBeen.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.*;
import example.test.hasBeen.model.api.Comment;

/**
 * Created by zuby on 2015-01-28.
 */
public class CommnetAdapter extends BaseAdapter{
    List<Comment> commentList;
    Context mContext;

    public CommnetAdapter( Context mContext,List<Comment> commentList) {
        this.commentList = commentList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Comment getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Comment comment = getItem(position);
        if(view==null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.comment, null);
        }
        return view;
    }
}
