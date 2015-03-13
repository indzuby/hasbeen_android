package co.hasBeen.newsfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.comment.EnterCommentListner;
import co.hasBeen.day.DayDialog;
import co.hasBeen.social.LoveListner;
import co.hasBeen.map.MapRoute;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.report.ReportListner;
import co.hasBeen.social.ShareListner;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-26.
 */
public class NewsFeedAdapter extends BaseAdapter {
    final static String MAP_URL = "http://maps.googleapis.com/maps/api/staticmap?size=480x240&scale=2&zoom=9&markers=icon:http://image.hasbeen.co/common/marker.png%7C";
    Context mContext;
    List<Day> mFeeds;
    GoogleMap mMap;
    MapRoute mMapRoute;
    boolean flag;
    DayDialog mDayDialog;
    String mAccessToken;
    int layout[] = {R.layout.newsfeed_image_layout_6, R.layout.newsfeed_image_layout_4, R.layout.newsfeed_image_layout_1, R.layout.newsfeed_image_layout_3, R.layout.newsfeed_image_layout_5, R.layout.newsfeed_image_layout_2};
    int length[] = {1, 2, 3, 3, 4, 5};
    int image[] = {R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.image5};
    int height[] = {R.dimen.newsfeed_image_height_normal, R.dimen.newsfeed_image_height_normal, R.dimen.newsfeed_image_height_normal, R.dimen.newsfeed_image_height_large, R.dimen.newsfeed_image_height_large, R.dimen.newsfeed_image_height_large};

    public NewsFeedAdapter(Context mContext, List feeds) {
        this.mContext = mContext;
        mFeeds = feeds;
        mAccessToken = Session.getString(mContext,"accessToken",null);
    }

    @Override
    public int getCount() {
        return mFeeds.size();
    }

    @Override
    public Day getItem(int position) {
        return mFeeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.newsfeed_feed, null);
        }
        final Day feed = getItem(position);

        TextView newsfeedName = (TextView) view.findViewById(R.id.newsfeedName);
        TextView newsfeedReason = (TextView) view.findViewById(R.id.newsfeedReason);
        if (!feed.getNewsFeedType().equals("DAY_POST")) {
            newsfeedName.setText(Util.parseName(feed.getFollowing(), mContext));
            newsfeedName.setOnClickListener(new ProfileClickListner(mContext, feed.getFollowing().getId()));
        } else {
            newsfeedName.setText(Util.parseName(feed.getUser(), mContext));
            newsfeedName.setOnClickListener(new ProfileClickListner(mContext, feed.getUser().getId()));
        }
        newsfeedReason.setText(Util.getNewsFeedReason(feed.getNewsFeedType(),mContext));
        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView profileName = (TextView) view.findViewById(R.id.profileName);
        TextView placeName = (TextView) view.findViewById(R.id.placeName);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView dayTitle = (TextView) view.findViewById(R.id.title);
        TextView dayDescription = (TextView) view.findViewById(R.id.description);
        final TextView likeCount = (TextView) view.findViewById(R.id.likeCount);
        final TextView commentCount = (TextView) view.findViewById(R.id.commentCount);
        final TextView shareCount = (TextView) view.findViewById(R.id.shareCount);
//        View imageBox = view.findViewById(R.id.imageBox);
//        ImageView mainImage = (ImageView) imageBox.findViewById(R.id.mainImage);
        FrameLayout imageBox = (FrameLayout) view.findViewById(R.id.imageBox);
        imageBox.removeAllViews();
        View imageLayout = getImageLayout(feed.getItineraryIndex(), feed.getPhotoList(), imageBox);

//        ImageView mainImage = (ImageView) imageLayout.findViewById(R.id.mainImage);

        profileName.setText(Util.parseName(feed.getUser(), mContext)); // coutry code -> name format
        placeName.setText(feed.getMainPlace().getCity() + ", " + feed.getMainPlace().getCountry());
//        date.setText(HasBeenDate.convertDate(feed.getDate()));
        dayTitle.setText(feed.getTitle());
        dayDescription.setText(feed.getDescription());
        likeCount.setText(mContext.getString(R.string.like_count,feed.getLoveCount()));
        commentCount.setText(mContext.getString(R.string.comment_count, feed.getCommentCount()));
        shareCount.setText(mContext.getString(R.string.share_count,feed.getShareCount()));
//        socialAction.setText(mContext.getString(R.string.social_status,feed.getLoveCount(),feed.getCommentCount(),feed.getShareCount()));
        Glide.with(mContext).load(feed.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(mContext)).into(profileImage);
//        Glide.with(mContext).load(feed.getMainPhoto().getMediumUrl()).centerCrop().into(mainImage);
        imageBox.addView(imageLayout);

        LinearLayout commentButton = (LinearLayout) view.findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new EnterCommentListner(mContext, "days", feed,commentCount));
        profileImage.setOnClickListener(new ProfileClickListner(mContext, feed.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(mContext, feed.getUser().getId()));
        LinearLayout loveButton = (LinearLayout) view.findViewById(R.id.loveButton);
        LinearLayout shareButton = (LinearLayout) view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new ShareListner(mContext, "days",feed,shareCount));
        ImageView love = (ImageView) loveButton.findViewById(R.id.love);
        TextView loveText = (TextView) loveButton.findViewById(R.id.loveText);
        if (feed.getLove() != null) {
            love.setImageResource(R.drawable.photo_like_pressed);
            loveText.setTextColor(mContext.getResources().getColor(R.color.light_black));
        } else {
            love.setImageResource(R.drawable.photo_like);
            loveText.setTextColor(mContext.getResources().getColor(R.color.light_gray));
        }
        loveButton.setOnClickListener(new LoveListner(mContext, feed, "days", likeCount));
        date.setText(HasBeenDate.convertDate(feed.getDate()));
        ImageView moreVert = (ImageView) view.findViewById(R.id.moreVert);
        moreVert.setVisibility(View.VISIBLE);

        moreVert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View.OnClickListener del = new ReportListner("days",feed.getId(),mContext,mDayDialog);
                View.OnClickListener edit = new ShareListner(mContext, "days",feed,shareCount);
                mDayDialog = new DayDialog(mContext, del,edit,true);
                mDayDialog.show();
            }
        });

        ImageView mapThumbnail = (ImageView) view.findViewById(R.id.mapThumbnail);
        Glide.with(mContext).load(feed.getStaticMapUrl()).into(mapThumbnail);
        return view;
    }

    protected View getImageLayout(int index, List<Photo> photoList, FrameLayout imageBox) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        int k = 5;
        if (photoList.size() <= 1) k = 1;
        else if (photoList.size() <= 2) k = 2;
        else if (photoList.size() <= 3) k = 4;
        else if (photoList.size() <= 4) k = 5;
        else if (photoList.size() <= 5) k = 6;
        view = inflater.inflate(layout[index % k], null);
        for (int i = 0; i < length[index % k] && i < photoList.size(); i++) {
            ImageView imageView = (ImageView) view.findViewById(image[i]);
            Glide.with(mContext).load(photoList.get(i).getMediumUrl()).centerCrop().placeholder(Util.getPlaceHolder(index)).into(imageView);
        }
        imageBox.getLayoutParams().height = (int) mContext.getResources().getDimension(height[index % k]);
        return view;
    }
}
