package co.hasBeen.photo;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
import co.hasBeen.report.ReportListner;
import co.hasBeen.social.LoveListner;
import co.hasBeen.social.ShareListner;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-29.
 */
public class    PhotoView extends ActionBarActivity {
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
    PhotoDialog mPhotoDialog;
    Long mMyid;
    EditText mDescription;
    String mBeforeDescription;
    boolean isEdit;
    InputMethodManager mImm;
    View mLoading;
    boolean isLoading;
    TextView titleView;
    View dayButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoId = getIntent().getLongExtra("id", 0);
        mAccessToken = Session.getString(this, "accessToken", null);
        mMyid = Session.getLong(this, "myUserid", 0);
        init();
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mPhoto = (Photo) msg.obj;
                    Log.i("Photo", mPhoto.getPlaceName());
                    dayButton.setOnClickListener(new EnterDayListner(mPhoto.getDay().getId(),getBaseContext()));
                    titleView.setText(mPhoto.getDay().getTitle());
                    initView();
                    initComment();
                    stopLoading();
                    break;
                case -1:
                    break;
            }
        }
    };

    protected void initView() {
        View titleBox = findViewById(R.id.dayTitleBox);
        ImageView profileImage = (ImageView) titleBox.findViewById(R.id.profileImage);
        TextView profileName = (TextView) titleBox.findViewById(R.id.profileName);
        TextView placeName = (TextView) titleBox.findViewById(R.id.placeName);
        TextView date = (TextView) titleBox.findViewById(R.id.date);

        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mDescription = (EditText) findViewById(R.id.description);
        mLikeCount = (TextView) findViewById(R.id.likeCount);
        mCommentCount = (TextView) findViewById(R.id.commentCount);
        mShareCount = (TextView) findViewById(R.id.shareCount);
        Glide.with(this).load(mPhoto.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(this)).placeholder(R.drawable.placeholder1).into(profileImage);
        Log.i(TAG, mPhoto.getPlaceName());
        profileName.setText(Util.parseName(mPhoto.getUser(), this));
        placeName.setText(mPhoto.getPlaceName());
        date.setText(HasBeenDate.convertDate(mPhoto.getTakenTime()));
        mDescription.setText(mPhoto.getDescription());
        findViewById(R.id.title).setVisibility(View.GONE);
        mLikeCount.setText(getString(R.string.like_count, mPhoto.getLoveCount()));
        mCommentCount.setText(getString(R.string.comment_count, mPhoto.getCommentCount()));
        mShareCount.setText(getString(R.string.share_count, mPhoto.getShareCount()));

        ImageView imageView = (ImageView) findViewById(R.id.photo);
        Glide.with(this).load(mPhoto.getLargeUrl()).placeholder(R.drawable.placeholder1).into(imageView);
        profileImage.setOnClickListener(new ProfileClickListner(this, mPhoto.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(this, mPhoto.getUser().getId()));

        LinearLayout loveButton = (LinearLayout) findViewById(R.id.loveButton);
        ImageView love = (ImageView) findViewById(R.id.love);
        TextView loveText = (TextView) loveButton.findViewById(R.id.loveText);
        if (mPhoto.getLove() != null) {
            love.setImageResource(R.drawable.photo_like_pressed);
            loveText.setTextColor(this.getResources().getColor(R.color.light_black));
        } else {
            love.setImageResource(R.drawable.photo_like);
            loveText.setTextColor(this.getResources().getColor(R.color.light_gray));
        }
        loveButton.setOnClickListener(new LoveListner(this, mPhoto, "photos", mLikeCount));
        LinearLayout shareButton = (LinearLayout) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new ShareListner(this, "photos", mPhoto, mShareCount));


    }

    protected void initComment() {

        final LinearLayout commentBox = (LinearLayout) findViewById(R.id.commentBox);
        final View moreComment = LayoutInflater.from(this).inflate(R.layout.more_comments, null);
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
                                commentBox.addView(CommentView.makeComment(getBaseContext(), comment), 2);
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
            View view = CommentView.makeComment(this, comment);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EnterCommentListner(PhotoView.this, "photos", mPhoto, mCommentCount);
                }
            });
            commentBox.addView(view);
        }

        LinearLayout commentButton = (LinearLayout) findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new EnterCommentListner(PhotoView.this, "photos", mPhoto, mCommentCount, commentBox));
        final EditText mEnterComment = (EditText) findViewById(R.id.enterComment);
        ImageView send = (ImageView) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    String contents = mEnterComment.getText().toString();
                    mEnterComment.setText("");
                    Toast.makeText(getBaseContext(), getString(R.string.description_ok), Toast.LENGTH_LONG).show();
                    new WriteCommentAsyncTask(new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            flag = false;
                            if (msg.what == 0) {
                                Comment comment = (Comment) msg.obj;
                                commentBox.addView(CommentView.makeComment(getBaseContext(), comment));
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
    PhotoAsyncTask photoAsyncTask;
    NearByPhotoAsyncTask nearByPhotoAsyncTask;
    protected void init() {
        setContentView(R.layout.photo);
        mLoading = findViewById(R.id.refresh);
        startLoading();
        photoAsyncTask= new PhotoAsyncTask(handler);
        photoAsyncTask.execute(mAccessToken, mPhotoId);
        initActionBar();
        nearByPhotoAsyncTask = new NearByPhotoAsyncTask(nearByHandler);
        nearByPhotoAsyncTask.execute(mAccessToken, mPhotoId);
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
        View nearBy = findViewById(R.id.nearBy);

        LinearLayout nearBy1 = (LinearLayout) nearBy.findViewById(R.id.nearBy1);
        LinearLayout nearBy2 = (LinearLayout) nearBy.findViewById(R.id.nearBy2);

        for (int i = 0; i < mNearByPhotos.size(); i++) {
            final Photo photo = mNearByPhotos.get(i);
            View nearByItem = LayoutInflater.from(this).inflate(R.layout.near_by_item, null);
            ImageView image = (ImageView) nearByItem.findViewById(R.id.profileImage);
            TextView name = (TextView) nearByItem.findViewById(R.id.profileName);
            TextView date = (TextView) nearByItem.findViewById(R.id.date);
            ImageView nearPhoto = (ImageView) nearByItem.findViewById(R.id.photo);
            TextView description = (TextView) nearByItem.findViewById(R.id.description);
            TextView likeCount = (TextView) nearByItem.findViewById(R.id.loveCount);
            TextView commentCount = (TextView) nearByItem.findViewById(R.id.commentCount);
            TextView shareCount = (TextView) nearByItem.findViewById(R.id.shareCount);

            Glide.with(this).load(photo.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(this)).into(image);
            name.setText(Util.parseName(photo.getUser(), this));
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
            nearByItem.setOnClickListener(new EnterPhotoListner(photo.getId(), this));
        }
    }

    protected void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_photo, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        View moreVert = mCustomActionBar.findViewById(R.id.moreVert);
        moreVert.setOnClickListener(new EditLisnter());
        dayButton = findViewById(R.id.dayButton);
    }

    protected void initEditActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);
        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText(getString(R.string.action_bar_edit_title));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnEditView(mBeforeDescription);
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        TextView done = (TextView) mCustomActionBar.findViewById(R.id.actionBarDone);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeforeDescription = mDescription.getText().toString();
                new PhotoEditAsyncTask(new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == 0)
                            backOnEditView(mBeforeDescription);
                        else
                            Toast.makeText(getBaseContext(), getString(R.string.common_error), Toast.LENGTH_LONG).show();

                    }
                }).execute(mAccessToken, mPhoto.getId(), mBeforeDescription);
            }
        });
    }

    protected void startLoading() {
        isLoading = true;
        mLoading.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        isLoading = false;
        mLoading.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }

    protected void backOnEditView(String description) {
        mDescription.setFocusable(false);
        mDescription.setFocusableInTouchMode(false);
        mDescription.setText(description);
        isEdit = false;
        initActionBar();
        mImm.hideSoftInputFromWindow(mDescription.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    @Override
    public void onDestroy() {
//        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        nearByPhotoAsyncTask.cancel(true);
        photoAsyncTask.cancel(true);
        System.gc();
        super.onDestroy();
    }

    class EditLisnter implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mPhoto.getUser().getId() == mMyid) {
                View.OnClickListener del = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PhotoDeleteAsyncTask(new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                mPhotoDialog.dismiss();
                                super.handleMessage(msg);
                                if (msg.what == 0) {
                                    Toast.makeText(getBaseContext(), getString(R.string.remove_photo_ok), Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(getBaseContext(), getString(R.string.remove_photo_error), Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute(mAccessToken, mPhoto.getId());
                    }
                };
                View.OnClickListener edit = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPhotoDialog.dismiss();
                        mDescription.setFocusable(true);
                        mDescription.setFocusableInTouchMode(true);
                        isEdit = true;
                        mBeforeDescription = mDescription.getText().toString();
                        initEditActionBar();
                        mImm.showSoftInput(mDescription, InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        mDescription.requestFocus(mBeforeDescription.length() - 1);
                    }
                };
                View.OnClickListener cover = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CoverAsyncTask(new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                if (msg.what == 0) {
                                    Toast.makeText(getBaseContext(), getString(R.string.cover_ok), Toast.LENGTH_LONG).show();
                                    mPhotoDialog.dismiss();
                                } else {
                                    Toast.makeText(getBaseContext(), getString(R.string.common_error), Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute(mAccessToken, mPhoto.getId());
                    }
                };
                mPhotoDialog = new PhotoDialog(PhotoView.this, cover, del, edit);
                mPhotoDialog.show();
            } else {
                mPhotoDialog = new PhotoDialog(PhotoView.this, true);
                View.OnClickListener report = new ReportListner("photos", mPhoto.getId(), getBaseContext(), mPhotoDialog);
                View.OnClickListener share = new ShareListner(getBaseContext(), "photos", mPhoto, mShareCount);
                mPhotoDialog.setLisnter(report,share);
                mPhotoDialog.show();
            }
        }

    }
}

