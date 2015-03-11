package co.hasBeen.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.model.api.Comment;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.R;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Util;

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
        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView profileName = (TextView) view.findViewById(R.id.profileName);
        TextView contents = (TextView) view.findViewById(R.id.contents);
        TextView commentTime = (TextView) view.findViewById(R.id.commentTime);

        Glide.with(mContext).load(comment.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(mContext)).into(profileImage);
        profileName.setText(Util.parseName(comment.getUser(),mContext));
        contents.setText(comment.getContents());
        commentTime.setText(HasBeenDate.getGapTime(comment.getCreatedTime(),mContext));
        profileImage.setOnClickListener(new ProfileClickListner(mContext,comment.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(mContext, comment.getUser().getId()));
        view.setBackground(mContext.getResources().getDrawable(R.drawable.long_pressed_selector));
        return view;
    }
}
