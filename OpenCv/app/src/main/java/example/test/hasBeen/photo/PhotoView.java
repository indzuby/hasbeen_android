package example.test.hasBeen.photo;

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

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.api.PhotoApi;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-29.
 */
public class PhotoView extends ActionBarActivity{
    final static String TAG = "Photo view";
    PhotoApi mPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mPhoto = (PhotoApi) msg.obj;
                    Log.i("Photo",mPhoto.getPlaceName());
                    initView();
                    initComment();
                    break;
                case -1:
                    break;
            }
        }
    };
    protected void initView(){
        View titleBox = findViewById(R.id.dayTitleBox);
        ImageView profileImage = (ImageView) titleBox.findViewById(R.id.profileImage);
        TextView name = (TextView) titleBox.findViewById(R.id.profileName);
        TextView placeName = (TextView) titleBox.findViewById(R.id.placeName);
        TextView date = (TextView) titleBox.findViewById(R.id.date);

        TextView description = (TextView) findViewById(R.id.description);
        TextView socialAction = (TextView) findViewById(R.id.socialAction);
        Glide.with(this).load(mPhoto.getUser().getImageUrl()).centerCrop().into(profileImage);
        Log.i(TAG, mPhoto.getPlaceName());
        name.setText(Util.parseName(mPhoto.getUser(), 0));
        placeName.setText(mPhoto.getPlaceName());
        date.setText(HasBeenDate.convertDate(mPhoto.getTakenTime()));
        description.setText(mPhoto.getDescription());
        socialAction.setText(mPhoto.getLoveCount() + " Likes " + mPhoto.getCommentCount() + " Commnents " + mPhoto.getShareCount() + " Shared");
        ImageView imageView = (ImageView) findViewById(R.id.photo);
        Glide.with(this).load(mPhoto.getMediumUrl()).into(imageView);
    }
    protected void initComment(){

        LinearLayout commentBox = (LinearLayout) findViewById(R.id.commentBox);



        if(mPhoto.getCommetList().size()>3) {
            View moreComment = LayoutInflater.from(this).inflate(R.layout.more_comments,null);
            commentBox.addView(moreComment);
        }
        for(int i = 0 ; i <mPhoto.getCommetList().size() && i<3;i++){
            View commentView = LayoutInflater.from(this).inflate(R.layout.comment,null);
            TextView contents = (TextView) commentView.findViewById(R.id.contents);
            TextView commentTime = (TextView) commentView.findViewById(R.id.commentTime);
            contents.setText(mPhoto.getCommetList().get(i).getContents());
            commentTime.setText(HasBeenDate.getGapTime(mPhoto.getCommetList().get(i).getCreatedTime()));
            commentBox.addView(commentView);
        }
    }
    protected void init(){
        setContentView(R.layout.photo);
        new PhotoAsyncTask(handler).execute();
        initActionBar();
        EditText enterComment = (EditText) findViewById(R.id.enterComment);
        enterComment.setFocusable(false);
        enterComment.setEnabled(false);
        enterComment.setFocusableInTouchMode(false);
        new NearByPhotoAsyncTask(nearByHandler).execute();
    }
    List<PhotoApi> mNearByPhotos ;
    Handler nearByHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mNearByPhotos = (List<PhotoApi>) msg.obj;
                    initNearBy();
                    break;
                case -1 :
                    break;
            }
        }
    };
    protected void initNearBy(){
        View nearBy = findViewById(R.id.nearBy);

        LinearLayout nearBy1 = (LinearLayout) nearBy.findViewById(R.id.nearBy1);
        LinearLayout nearBy2 = (LinearLayout) nearBy.findViewById(R.id.nearBy2);

        for(int i = 0 ; i<mNearByPhotos.size();i++) {
            PhotoApi photo = mNearByPhotos.get(i);
            View nearByItem = LayoutInflater.from(this).inflate(R.layout.near_by_item,null);
            ImageView image = (ImageView) nearByItem.findViewById(R.id.profileImage);
            TextView name = (TextView) nearByItem.findViewById(R.id.profileName);
            TextView date = (TextView) nearByItem.findViewById(R.id.date);
            ImageView nearPhoto = (ImageView) nearByItem.findViewById(R.id.photo);
            TextView description = (TextView) nearByItem.findViewById(R.id.description);
            TextView likeCount = (TextView) nearByItem.findViewById(R.id.likeCount);
            TextView commentCount = (TextView) nearByItem.findViewById(R.id.commentCount);
            TextView shareCount = (TextView) nearByItem.findViewById(R.id.shareCount);

            Glide.with(this).load(photo.getUser().getImageUrl()).into(image);
            name.setText(Util.parseName(photo.getUser(),0));
//            date.setText(HasBeenDate.convertDate(photo.getTakenTime()));
            Glide.with(this).load(photo.getSmallUrl()).into(nearPhoto);
            description.setText(photo.getPlaceName());
            likeCount.setText(photo.getLoveCount()+"");
            commentCount.setText(photo.getCommentCount()+"");
            shareCount.setText(photo.getShareCount()+"");

            if(i%2==0)
                nearBy1.addView(nearByItem);
            else
                nearBy2.addView(nearByItem);
        }
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

}
