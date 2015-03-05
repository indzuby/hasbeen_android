package co.hasBeen.photo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
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
import co.hasBeen.loved.LoveListner;
import co.hasBeen.model.api.Comment;
import co.hasBeen.model.database.Photo;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.social.ShareListner;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-29.
 */
public class PhotoView extends ActionBarActivity {
    final static String TAG = "Photo view";
    Photo mPhoto;
    Long mPhotoId;
    String mAccessToken;
    int mTotalCommentCount;
    TextView mSocialAction;
    int mViewCommentCount;
    Long lastCommentId;
    Typeface medium, regular;
    Long mMyid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgress();
        mPhotoId = getIntent().getLongExtra("photoId", 0);
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
                    initView();
                    initComment();
                    dialog.dismiss();
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

        TextView description = (TextView) findViewById(R.id.description);
        mSocialAction = (TextView) findViewById(R.id.socialAction);
        Glide.with(this).load(mPhoto.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(this)).placeholder(R.drawable.placeholder1).into(profileImage);
        Log.i(TAG, mPhoto.getPlaceName());
        profileName.setText(Util.parseName(mPhoto.getUser(), 0));
        placeName.setText(mPhoto.getPlaceName());
        date.setText(HasBeenDate.convertDate(mPhoto.getTakenTime()));
        description.setText(mPhoto.getDescription());
        findViewById(R.id.title).setVisibility(View.GONE);
        mSocialAction.setText(mPhoto.getLoveCount() + " Likes · " + mPhoto.getCommentCount() + " Commnents · " + mPhoto.getShareCount() + " Shared");
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
            loveText.setTypeface(medium);
        } else {
            love.setImageResource(R.drawable.photo_like);
            loveText.setTextColor(this.getResources().getColor(R.color.light_gray));
            loveText.setTypeface(regular);
        }
        loveButton.setOnClickListener(new LoveListner(this, mPhoto, "photos", mSocialAction));
        LinearLayout shareButton = (LinearLayout) findViewById(R.id.shareButton);
        String url = Session.WEP_DOMAIN+"days/"+mPhoto.getId();
        shareButton.setOnClickListener(new ShareListner(this,url));


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
                                mSocialAction.setText(mPhoto.getLoveCount() + " Likes · " + mPhoto.getCommentCount() + " Commnents · " + mPhoto.getShareCount() + " Shared");
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
                    new EnterCommentListner(getBaseContext(), "photos", mPhoto.getId(),mPhoto.getCommentCount());
                }
            });
            commentBox.addView(view);
        }

        LinearLayout commentButton = (LinearLayout) findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new EnterCommentListner(this, "photos", mPhoto.getId(),mPhoto.getCommentCount()));
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
                    Toast.makeText(getBaseContext(), "Complete written", Toast.LENGTH_LONG).show();
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
                                mSocialAction.setText(mPhoto.getLoveCount() + " Likes · " + mPhoto.getCommentCount() + " Commnents · " + mPhoto.getShareCount() + " Shared");
                                mViewCommentCount++;
                            }
                        }
                    }).execute(mAccessToken, "photos", mPhoto.getId(), contents);
                }
            }
        });
        ;
    }
    protected void init() {
        setContentView(R.layout.photo);
        medium = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Medium.ttf");
        regular = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Regular.ttf");
        new PhotoAsyncTask(handler).execute(mAccessToken, mPhotoId);
        initActionBar();
        new NearByPhotoAsyncTask(nearByHandler).execute(mAccessToken, mPhotoId);
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
            name.setText(Util.parseName(photo.getUser(), 0));
            name.setTypeface(medium);
            Glide.with(this).load(photo.getMediumUrl()).into(nearPhoto);
            description.setText(photo.getPlaceName());
            description.setTypeface(medium);
            likeCount.setText(photo.getLoveCount() + "");
            commentCount.setText(photo.getCommentCount() + "");
            shareCount.setText(photo.getShareCount() + "");
            date.setText(HasBeenDate.convertDate(photo.getTakenTime()));
            date.setTypeface(regular);
            if (i % 2 == 0)
                nearBy1.addView(nearByItem);
            else
                nearBy2.addView(nearByItem);
            nearByItem.setOnClickListener(new View.OnClickListener() {
                boolean flag = false;

                @Override
                public void onClick(View v) {
                    if (!flag) {
                        flag = true;
                        Intent intent = new Intent(getBaseContext(), PhotoView.class);
                        intent.putExtra("photoId", photo.getId());
                        startActivity(intent);
                        finish();
                        flag = false;
                    }

                }
            });
        }
    }

    protected void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText("Photo");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);

    }

    ProgressDialog dialog;

    protected void showProgress() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setMessage("Wait a minutes...");
        dialog.setProgress(100);
        dialog.show();
    }

    @Override
    public void onDestroy() {
//        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }
}
