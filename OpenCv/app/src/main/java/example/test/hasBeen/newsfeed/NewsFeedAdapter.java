package example.test.hasBeen.newsfeed;

import android.content.Context;
import android.graphics.Typeface;
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

import example.test.hasBeen.R;
import example.test.hasBeen.comment.EnterCommentListner;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.loved.LoveListner;
import example.test.hasBeen.model.api.DayApi;
import example.test.hasBeen.model.api.PhotoApi;
import example.test.hasBeen.profile.ProfileClickListner;
import example.test.hasBeen.utils.CircleTransform;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-26.
 */
public class NewsFeedAdapter extends BaseAdapter {
    final static String MAP_URL = "http://maps.googleapis.com/maps/api/staticmap?size=480x240&scale=2&zoom=9&markers=icon:http://image.hasbeen.co/common/marker.png%7C";
    Context mContext;
    List<DayApi> mFeeds;
    GoogleMap mMap;
    MapRoute mMapRoute;
    boolean flag;
    Typeface medium,regular;
    int layout[] = {R.layout.newsfeed_image_layout_6, R.layout.newsfeed_image_layout_4, R.layout.newsfeed_image_layout_1, R.layout.newsfeed_image_layout_3, R.layout.newsfeed_image_layout_2};
    int length[] = {1, 2, 3, 3, 5};
    int image[] = {R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.image5};
    int height[]= {R.dimen.newsfeed_image_height_normal,R.dimen.newsfeed_image_height_normal,R.dimen.newsfeed_image_height_normal,R.dimen.newsfeed_image_height_large,R.dimen.newsfeed_image_height_large};
    public NewsFeedAdapter(Context mContext, List feeds) {
        this.mContext = mContext;
        mFeeds = feeds;
        medium = Typeface.createFromAsset(mContext.getAssets(),"fonts/Roboto-Medium.ttf");
        regular = Typeface.createFromAsset(mContext.getAssets(),"fonts/Roboto-Regular.ttf");
    }

    @Override
    public int getCount() {
        return mFeeds.size();
    }

    @Override
    public DayApi getItem(int position) {
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
        final DayApi feed = getItem(position);

        TextView newsfeedName = (TextView) view.findViewById(R.id.newsfeedName);
        TextView newsfeedReason = (TextView) view.findViewById(R.id.newsfeedReason);
        newsfeedName.setTypeface(medium);
        newsfeedReason.setTypeface(regular);
        if(!feed.getNewsFeedType().equals("DAY_POST")) {
            newsfeedName.setText(Util.parseName(feed.getFollowing(), 0));
            newsfeedName.setOnClickListener(new ProfileClickListner(mContext,feed.getFollowing().getId()));
        }
        else {
            newsfeedName.setText(Util.parseName(feed.getUser(), 0));
            newsfeedName.setOnClickListener(new ProfileClickListner(mContext,feed.getUser().getId()));
        }
        newsfeedReason.setText(Util.getNewsFeedReason(feed.getNewsFeedType()));
        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView profileName = (TextView) view.findViewById(R.id.profileName);
        TextView placeName = (TextView) view.findViewById(R.id.placeName);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView dayTitle = (TextView) view.findViewById(R.id.title);
        TextView dayDescription = (TextView) view.findViewById(R.id.description);
        TextView socialAction = (TextView) view.findViewById(R.id.socialAction);
//        View imageBox = view.findViewById(R.id.imageBox);
//        ImageView mainImage = (ImageView) imageBox.findViewById(R.id.mainImage);
        FrameLayout imageBox = (FrameLayout) view.findViewById(R.id.imageBox);
        imageBox.removeAllViews();
        View imageLayout = getImageLayout(feed.getItineraryIndex(), feed.getPhotoList(),imageBox);

//        ImageView mainImage = (ImageView) imageLayout.findViewById(R.id.mainImage);

        profileName.setText(Util.parseName(feed.getUser(), 0)); // coutry code -> name format
        placeName.setText(feed.getMainPlace().getCity() + ", " + feed.getMainPlace().getCountry());
//        date.setText(HasBeenDate.convertDate(feed.getDate()));
        dayTitle.setText(feed.getTitle());
        dayDescription.setText(feed.getDescription());
        socialAction.setText(feed.getLoveCount() + " Likes · " + feed.getCommentCount() + " Commnents · " + feed.getShareCount() + " Shared");
        Glide.with(mContext).load(feed.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(mContext)).into(profileImage);
//        Glide.with(mContext).load(feed.getMainPhoto().getMediumUrl()).centerCrop().into(mainImage);
        imageBox.addView(imageLayout);

        LinearLayout commentButton = (LinearLayout) view.findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new EnterCommentListner(mContext,"days",feed.getId()));
        profileImage.setOnClickListener(new ProfileClickListner(mContext,feed.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(mContext,feed.getUser().getId()));
        LinearLayout loveButton = (LinearLayout) view.findViewById(R.id.loveButton);
        ImageView love = (ImageView) loveButton.findViewById(R.id.love);
        if(feed.getLove()!=null)
            love.setImageResource(R.drawable.photo_like_pressed);
        else
            love.setImageResource(R.drawable.photo_like);

        loveButton.setOnClickListener(new LoveListner(mContext,feed,"days",socialAction));
        date.setText(HasBeenDate.convertDate(feed.getCreatedTime()));
        placeName.setTypeface(medium);
        dayTitle.setTypeface(medium);
        profileName.setTypeface(regular);
        dayDescription.setTypeface(regular);
        ImageView moreVert = (ImageView) view.findViewById(R.id.moreVert);
        moreVert.setVisibility(View.VISIBLE);

        ImageView mapThumbnail = (ImageView) view.findViewById(R.id.mapThumbnail);
        Glide.with(mContext).load(MAP_URL+feed.getMainPlace().getLat()+","+feed.getMainPlace().getLon()).into(mapThumbnail);
        return view;
    }

    protected View getImageLayout(int index, List<PhotoApi> photoList,FrameLayout imageBox) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        int k = 5;
        if (photoList.size() <= 1) k = 1;
        else if (photoList.size() <= 2) k = 2;
        else if(photoList.size()<=3) k = 4;
        else if (photoList.size() <= 4) k = 5;
        view = inflater.inflate(layout[index % k], null);
        for (int i = 0; i < length[index % k] && i < photoList.size(); i++) {
            ImageView imageView = (ImageView) view.findViewById(image[i]);
            Glide.with(mContext).load(photoList.get(i).getMediumUrl()).centerCrop().placeholder(R.drawable.placeholder).into(imageView);
        }
        imageBox.getLayoutParams().height = (int) mContext.getResources().getDimension(height[index%k]);
        return view;
    }
}
