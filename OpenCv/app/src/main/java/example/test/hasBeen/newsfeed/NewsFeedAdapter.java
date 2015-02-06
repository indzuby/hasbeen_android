package example.test.hasBeen.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.comment.CommentView;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.model.api.DayApi;
import example.test.hasBeen.model.api.PhotoApi;
import example.test.hasBeen.profile.ProfileClickListner;
import example.test.hasBeen.utils.CircleTransform;
import example.test.hasBeen.utils.SlidingUpPanelLayout;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-26.
 */
public class NewsFeedAdapter extends BaseAdapter {
    Context mContext;
    List<DayApi> mFeeds;
    GoogleMap mMap;
    MapRoute mMapRoute;
    boolean flag;
    SlidingUpPanelLayout mSlidPanel;
    int layout[] = {R.layout.newsfeed_image_layout_6, R.layout.newsfeed_image_layout_4, R.layout.newsfeed_image_layout_5, R.layout.newsfeed_image_layout_3, R.layout.newsfeed_image_layout_1, R.layout.newsfeed_image_layout_2};
    int length[] = {1, 2, 2, 3, 3, 5};
    int image[] = {R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.image5};

    public NewsFeedAdapter(Context mContext, List feeds) {
        this.mContext = mContext;
        mFeeds = feeds;
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
        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView profileName = (TextView) view.findViewById(R.id.profileName);
        TextView placeName = (TextView) view.findViewById(R.id.placeName);
        placeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapRoute.addMarker(mFeeds.get(position).getMainPlace().getLat(), mFeeds.get(position).getMainPlace().getLon());
                mSlidPanel.collapsePane();
            }
        });
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView dayTitle = (TextView) view.findViewById(R.id.title);
        TextView dayDescription = (TextView) view.findViewById(R.id.description);
        TextView socialAction = (TextView) view.findViewById(R.id.socialAction);
//        View imageBox = view.findViewById(R.id.imageBox);
//        ImageView mainImage = (ImageView) imageBox.findViewById(R.id.mainImage);
        FrameLayout imageBox = (FrameLayout) view.findViewById(R.id.imageBox);
        imageBox.removeAllViews();
        View imageLayout = getImageLayout(feed.getItineraryIndex(), feed.getPhotoList());

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

        ImageView commentButton = (ImageView) view.findViewById(R.id.comment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    Intent intent = new Intent(mContext, CommentView.class);
                    intent.putExtra("type","days");
                    intent.putExtra("id",feed.getId());
                    mContext.startActivity(intent);
                    flag = false;
                }

            }
        });
        profileImage.setOnClickListener(new ProfileClickListner(mContext,feed.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(mContext,feed.getUser().getId()));
        return view;
    }

    protected View getImageLayout(int index, List<PhotoApi> photoList) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        int k = 5;
        if (photoList.size() <= 1) k = 1;
        else if (photoList.size() <= 2) k = 3;
        else if (photoList.size() <= 4) k = 5;
        view = inflater.inflate(layout[index % k], null);
        for (int i = 0; i < length[index % k] && i < photoList.size(); i++) {
            ImageView imageView = (ImageView) view.findViewById(image[i]);
            Glide.with(mContext).load(photoList.get(i).getMediumUrl()).centerCrop().placeholder(R.drawable.placeholder).into(imageView);
        }
        return view;
    }
}
