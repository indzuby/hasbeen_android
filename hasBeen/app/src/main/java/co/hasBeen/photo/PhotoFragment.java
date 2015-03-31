package co.hasBeen.photo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.localytics.android.Localytics;

import java.util.Collections;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.comment.CommentAsyncTask;
import co.hasBeen.comment.CommentView;
import co.hasBeen.comment.EnterCommentListner;
import co.hasBeen.comment.WriteCommentAsyncTask;
import co.hasBeen.day.EnterDayListner;
import co.hasBeen.model.api.Comment;
import co.hasBeen.model.api.Photo;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.social.LoveListner;
import co.hasBeen.social.ShareListner;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-29.
 */
public class PhotoFragment extends Fragment {
    final static String TAG = "Photo view";
    Photo mPhoto;
    Long mPhotoId;
    String mAccessToken;
    int mTotalCommentCount;
    TextView mLikeCount;
    TextView mCommentCount;
    TextView mShareCount;
    int mViewCommentCount;
    Long lastCommentId;
    Long mMyid;
    EditText mDescription;
    InputMethodManager mImm;
    View mLoading;
    boolean isLoading;
    TextView titleView;
    View dayButton;
    ListView listView;
    boolean isNearBy=false;
    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.photo, container, false);
        mAccessToken = Session.getString(getActivity(), "accessToken", null);
        mMyid = Session.getLong(getActivity(), "myUserid", 0);
        mPhotoId = getArguments().getLong("id");
        titleView  = ((PhotoActivity)getActivity()).titleView;
        dayButton = ((PhotoActivity)getActivity()).dayButton;
        init();
        return mView;
    }
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mPhoto = (Photo) msg.obj;
                    dayButton.setOnClickListener(new EnterDayListner(mPhoto.getDay().getId(),getActivity()));
                    titleView.setText(mPhoto.getDay().getTitle());
                    intHeaderView();
                    initComment();
                    stopLoading();
                    break;
                case -1:
                    break;
            }
        }
    };

    protected void intHeaderView() {
        View titleBox = mHeaderView.findViewById(R.id.dayTitleBox);
        ImageView profileImage = (ImageView) titleBox.findViewById(R.id.profileImage);
        TextView profileName = (TextView) titleBox.findViewById(R.id.profileName);
        TextView placeName = (TextView) titleBox.findViewById(R.id.placeName);
        TextView date = (TextView) titleBox.findViewById(R.id.date);

        mImm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        mDescription = (EditText) mHeaderView.findViewById(R.id.description);
        mLikeCount = (TextView) mHeaderView.findViewById(R.id.likeCount);
        mCommentCount = (TextView) mHeaderView.findViewById(R.id.commentCount);
        mShareCount = (TextView) mHeaderView.findViewById(R.id.shareCount);
        Glide.with(this).load(mPhoto.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(getActivity())).placeholder(R.drawable.placeholder1).into(profileImage);
        Log.i(TAG, mPhoto.getPlaceName());
        profileName.setText(Util.parseName(mPhoto.getUser(), getActivity()));
        placeName.setText(mPhoto.getPlaceName());
        date.setText(HasBeenDate.convertDate(mPhoto.getTakenTime()));
        mDescription.setText(mPhoto.getDescription());
        mView.findViewById(R.id.title).setVisibility(View.GONE);
        mLikeCount.setText(getString(R.string.like_count, mPhoto.getLoveCount()));
        mCommentCount.setText(getString(R.string.comment_count, mPhoto.getCommentCount()));
        mShareCount.setText(getString(R.string.share_count, mPhoto.getShareCount()));

        ImageView imageView = (ImageView) mHeaderView.findViewById(R.id.photo);
        Glide.with(this).load(mPhoto.getLargeUrl()).placeholder(R.drawable.placeholder1).into(imageView);
        profileImage.setOnClickListener(new ProfileClickListner(getActivity(), mPhoto.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(getActivity(), mPhoto.getUser().getId()));

        LinearLayout loveButton = (LinearLayout) mHeaderView.findViewById(R.id.loveButton);
        ImageView love = (ImageView) mHeaderView.findViewById(R.id.love);
        TextView loveText = (TextView) loveButton.findViewById(R.id.loveText);
        if (mPhoto.getLove() != null) {
            love.setImageResource(R.drawable.photo_like_pressed);
            loveText.setTextColor(this.getResources().getColor(R.color.light_black));
        } else {
            love.setImageResource(R.drawable.photo_like);
            loveText.setTextColor(this.getResources().getColor(R.color.light_gray));
        }
        loveButton.setOnClickListener(new LoveListner(getActivity(), mPhoto, "photos", mLikeCount));
        LinearLayout shareButton = (LinearLayout) mHeaderView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new ShareListner(getActivity(), "photos", mPhoto, mShareCount));
        if (mPhoto.getUser().getId() == mMyid) {
            mDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PhotoView)getActivity()).setEdit();
                }
            });
        }
    }

    protected void initComment() {

        final LinearLayout commentBox = (LinearLayout) mHeaderView.findViewById(R.id.commentBox);
        final View moreComment = LayoutInflater.from(getActivity()).inflate(R.layout.more_comments, null);
        mTotalCommentCount = mPhoto.getCommentCount();
        mViewCommentCount = mPhoto.getCommentList().size();
        if (mTotalCommentCount > 10)
            commentBox.addView(moreComment);
        if (mTotalCommentCount > 0)
            lastCommentId = mPhoto.getCommentList().get(0).getId();
        moreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommentAsyncTask(new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == 0) {
                            List<Comment> commentList = (List) msg.obj;
                            lastCommentId = commentList.get(0).getId();
                            Collections.reverse(commentList);
                            for (Comment comment : commentList) {
                                commentBox.addView(CommentView.makeComment(getActivity(), comment), 2);
                                mViewCommentCount++;
                            }

                            if (mViewCommentCount == mTotalCommentCount)
                                moreComment.setVisibility(View.GONE);
                        }
                    }
                }).execute(mAccessToken, "photos", mPhoto.getId(), lastCommentId);
            }
        });
        for (int i = 0; i < mPhoto.getCommentList().size(); i++) {
            Comment comment = mPhoto.getCommentList().get(i);
            View view = CommentView.makeComment(getActivity(), comment);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EnterCommentListner(getActivity(), "photos", mPhoto, mCommentCount);
                }
            });
            commentBox.addView(view);
        }

        LinearLayout commentButton = (LinearLayout) mHeaderView.findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new EnterCommentListner(getActivity(), "photos", mPhoto, mCommentCount, commentBox));
        final EditText mEnterComment = (EditText) mHeaderView.findViewById(R.id.enterComment);
        ImageView send = (ImageView) mHeaderView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    String contents = mEnterComment.getText().toString();
                    mEnterComment.setText("");
                    Toast.makeText(getActivity(), getString(R.string.description_ok), Toast.LENGTH_LONG).show();
                    new WriteCommentAsyncTask(new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            flag = false;
                            if (msg.what == 0) {
                                Comment comment = (Comment) msg.obj;
                                commentBox.addView(CommentView.makeComment(getActivity(), comment));
                                mTotalCommentCount++;
                                mPhoto.setCommentCount(mTotalCommentCount);
                                mCommentCount.setText(getString(R.string.comment_count, mPhoto.getCommentCount()));
                                mViewCommentCount++;
                            }
                        }
                    }).execute(mAccessToken, "photos", mPhoto.getId(), contents);
                }
            }
        });
        ;
    }
    View mFooterView;
    View mHeaderView;
    PhotoAsyncTask photoAsyncTask;
    NearByPhotoAsyncTask nearByPhotoAsyncTask;
    protected void init() {
        mLoading = mView.findViewById(R.id.refresh);
        startLoading();
        listView = (ListView) mView.findViewById(R.id.listView);
        mHeaderView =  LayoutInflater.from(getActivity()).inflate(R.layout.photo_header, null, false);
        mFooterView =  LayoutInflater.from(getActivity()).inflate(R.layout.near_by, null, false);
        listView.addHeaderView(mHeaderView);
        listView.addFooterView(mFooterView);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return null;
            }
        });
        photoAsyncTask= new PhotoAsyncTask(handler);
        photoAsyncTask.execute(mAccessToken, mPhotoId);
        nearByPhotoAsyncTask = new NearByPhotoAsyncTask(nearByHandler);
        nearByPhotoAsyncTask.execute(mAccessToken, mPhotoId);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+1 == totalItemCount) {
                    if(isNearBy) {
                        titleView.setText(getString(R.string.near_by));
                        dayButton.setVisibility(View.GONE);
                        ((PhotoActivity)getActivity()).mViewPager.setSwipeEnabled(false);
                        isNearBy = false;
                    }
                }else {
                    if(!isNearBy) {
                        ((PhotoActivity)getActivity()).mViewPager.setSwipeEnabled(true);
                        if(mPhoto!=null) titleView.setText(mPhoto.getDay().getTitle());
                        dayButton.setVisibility(View.VISIBLE);
                        isNearBy = true;
                    }
                }

            }
        });
    }

    List<Photo> mNearByPhotos;
    Handler nearByHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mNearByPhotos = (List<Photo>) msg.obj;
                    initNearBy();
                    break;
                case -1:
                    break;
            }
        }
    };

    protected void initNearBy() {
        View nearBy = mFooterView;

        LinearLayout nearBy1 = (LinearLayout) nearBy.findViewById(R.id.nearBy1);
        LinearLayout nearBy2 = (LinearLayout) nearBy.findViewById(R.id.nearBy2);

        for (int i = 0; i < mNearByPhotos.size(); i++) {
            final Photo photo = mNearByPhotos.get(i);
            View nearByItem = LayoutInflater.from(getActivity()).inflate(R.layout.near_by_item, null);
            ImageView image = (ImageView) nearByItem.findViewById(R.id.profileImage);
            TextView name = (TextView) nearByItem.findViewById(R.id.profileName);
            TextView date = (TextView) nearByItem.findViewById(R.id.date);
            ImageView nearPhoto = (ImageView) nearByItem.findViewById(R.id.photo);
            TextView description = (TextView) nearByItem.findViewById(R.id.description);
            TextView likeCount = (TextView) nearByItem.findViewById(R.id.loveCount);
            TextView commentCount = (TextView) nearByItem.findViewById(R.id.commentCount);
            TextView shareCount = (TextView) nearByItem.findViewById(R.id.shareCount);

            Glide.with(this).load(photo.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(getActivity())).into(image);
            name.setText(Util.parseName(photo.getUser(), getActivity()));
            Glide.with(this).load(photo.getMediumUrl()).into(nearPhoto);
            description.setText(photo.getPlaceName());
            likeCount.setText(photo.getLoveCount() + "");
            commentCount.setText(photo.getCommentCount() + "");
            shareCount.setText(photo.getShareCount() + "");
            date.setText(HasBeenDate.convertDate(photo.getTakenTime()));
            if (i % 2 == 0)
                nearBy1.addView(nearByItem);
            else
                nearBy2.addView(nearByItem);
            nearByItem.setOnClickListener(new EnterPhotoListner(photo.getId(), getActivity()));
        }
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
    public void onDestroy() {
//        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        nearByPhotoAsyncTask.cancel(true);
        photoAsyncTask.cancel(true);
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        nearByPhotoAsyncTask.cancel(true);
        photoAsyncTask.cancel(true);
        System.gc();
        super.onDestroyView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Photo View");
        Localytics.upload();
    }
}

