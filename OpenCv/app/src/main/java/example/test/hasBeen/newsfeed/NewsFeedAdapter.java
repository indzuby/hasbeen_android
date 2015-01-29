package example.test.hasBeen.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.comment.CommentView;
import example.test.hasBeen.geolocation.MapRoute;
import example.test.hasBeen.model.api.NewsFeedApi;
import example.test.hasBeen.utils.HasBeenDate;
import example.test.hasBeen.utils.SlidingUpPanelLayout;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-26.
 */
public class NewsFeedAdapter extends BaseAdapter {
    Context mContext;
    List<NewsFeedApi> mFeeds;
    GoogleMap mMap;
    MapRoute mMapRoute;
    boolean flag;
    SlidingUpPanelLayout mSlidPanel;
    public NewsFeedAdapter(Context mContext, List feeds) {
        this.mContext = mContext;
        mFeeds = feeds;
    }

    @Override
    public int getCount() {
        return mFeeds.size();
    }

    @Override
    public NewsFeedApi getItem(int position) {
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
        NewsFeedApi feed = getItem(position);
        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        TextView profileName =  (TextView) view.findViewById(R.id.profileName);
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
        View imageBox = view.findViewById(R.id.imageBox);
        ImageView mainImage = (ImageView) imageBox.findViewById(R.id.mainImage);
        profileName.setText(Util.parseName(feed.getUser(),0)); // coutry code -> name format
        placeName.setText(feed.getMainPlace().getCity() + ", " + feed.getMainPlace().getCountry());
        date.setText(HasBeenDate.convertDate(feed.getDate()));
        dayTitle.setText(feed.getTitle());
        dayDescription.setText(feed.getDescription());
        socialAction.setText(feed.getLoveCount()+" Likes " + feed.getCommnetCount()+" Commnents "+feed.getShareCount()+" Shared");
        Glide.with(mContext).load(feed.getUser().getImageUrl()).centerCrop().into(profileImage);
        Glide.with(mContext).load(feed.getMainPhoto().getMediumUrl()).fitCenter().into(mainImage);

        ImageView commentButton = (ImageView) view.findViewById(R.id.comment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View v) {
                if(!flag) {
                    flag = true;
                    Intent intent = new Intent(mContext, CommentView.class);
                    mContext.startActivity(intent);
                    flag = false;
                }

            }
        });
        return view;
    }
}
