package co.hasBeen.day;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.hasBeen.R;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.User;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-12.
 */
public class Recommandation {
    public static View getView(Day day, Context context,View view) {
        View mRecommend = view;
        if (view == null){
            LayoutInflater mInflater = LayoutInflater.from(context);
            mRecommend = mInflater.inflate(R.layout.interested_in, null);
        }
        TextView placeName = (TextView) mRecommend.findViewById(R.id.placeName);
        TextView profileName = (TextView) mRecommend.findViewById(R.id.name);
        ImageView profileImage = (ImageView) mRecommend.findViewById(R.id.profileImage);
        TextView date = (TextView) mRecommend.findViewById(R.id.date);
        ImageView mainPhoto = (ImageView) mRecommend.findViewById(R.id.mainPhoto);
        TextView loveCount = (TextView) mRecommend.findViewById(R.id.loveCount);
        TextView commentCount = (TextView) mRecommend.findViewById(R.id.commentCount);
        TextView shareCount = (TextView) mRecommend.findViewById(R.id.shareCount);
        TextView dayIndex = (TextView) mRecommend.findViewById(R.id.dayIndex);

        User user = day.getUser();
        placeName.setText(day.getTitle());
        profileName.setText(Util.parseName(user, context));
        Glide.with(context).load(user.getImageUrl()).asBitmap().transform(new CircleTransform(context)).into(profileImage);
        date.setText(HasBeenDate.convertDate(day.getDate()));
        Glide.with(context).load(day.getMainPhoto().getMediumUrl()).placeholder(Util.getPlaceHolder(day.getItineraryIndex())).into(mainPhoto);
//        Glide.with(context).load(day.getPhotoList().get(0).getMediumUrl()).placeholder(Util.getPlaceHolder(day.getItineraryIndex())).into(mainPhoto);
        loveCount.setText(day.getLoveCount()+"");
        commentCount.setText(day.getCommentCount() + "");
        shareCount.setText(day.getShareCount() + "");
        dayIndex.setText("Day " + day.getItineraryIndex());
        mRecommend.setOnClickListener(new EnterDayListner(day.getId(),context));
        profileImage.setOnClickListener(new ProfileClickListner(context, day.getUser().getId()));
        profileName.setOnClickListener(new ProfileClickListner(context, day.getUser().getId()));
        return mRecommend;
    }
    public static View getPinView(Day day, Context context, View view){
        View mView = getView(day,context,view);
        View mainPhoto = mView.findViewById(R.id.mainPhoto);
        mainPhoto.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.pin_day_height);
        return mView;
    }
}
