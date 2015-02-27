package co.hasBeen.alarm;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.day.DayView;
import co.hasBeen.model.api.Alarm;
import co.hasBeen.model.api.User;
import co.hasBeen.model.database.Photo;
import co.hasBeen.photo.PhotoView;
import co.hasBeen.profile.ProfileClickListner;
import co.hasBeen.profile.ProfileView;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-02-03.
 */
public class AlarmAdapter extends BaseAdapter {

    List<Alarm> mAlarms;
    Context mContext;
    LayoutInflater inflater;

    public AlarmAdapter(List<Alarm> mAlarms, Context mContext) {
        this.mAlarms = mAlarms;
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mAlarms.size();
    }

    @Override
    public Alarm getItem(int position) {
        return mAlarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.alarm_item, null);

        Alarm alarm = getItem(position);
        ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
        profileImage.setOnClickListener(new ProfileClickListner(mContext, alarm.getUser().getId()));
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView time = (TextView) view.findViewById(R.id.time);
        time.setText(HasBeenDate.getGapTime(alarm.getCreatedTime()));
        Glide.with(mContext).load(alarm.getUser().getImageUrl()).asBitmap().transform(new CircleTransform(mContext)).into(profileImage);
        description.setText(getDescription(alarm));
        LinearLayout alarmBox = (LinearLayout) view.findViewById(R.id.alarmBox);
        alarmBox.removeAllViewsInLayout();
        alarmBox.addView(getAlarmBoxLayout(alarm));
        view.setOnClickListener(new AlarmItemClickListner(alarm));
        return view;
    }

    class AlarmItemClickListner implements View.OnClickListener {
        Alarm alarm;
        boolean flag = false;

        AlarmItemClickListner(Alarm alarm) {
            this.alarm = alarm;
        }

        @Override
        public void onClick(View v) {

            Alarm.Type type = alarm.getType();
            if (!flag) {
                flag = true;
                if (type == Alarm.Type.PHOTO_COMMENT || type == Alarm.Type.PHOTO_LOVE) {
                    Intent intent = new Intent(mContext, PhotoView.class);
                    intent.putExtra("photoId", alarm.getPhoto().getId());
                    mContext.startActivity(intent);
                } else if (type == Alarm.Type.DAY_COMMENT || type == Alarm.Type.DAY_LOVE || type == Alarm.Type.DAY_POST) {
                    Intent intent = new Intent(mContext, DayView.class);
                    intent.putExtra("dayId", alarm.getDay().getId());
                    mContext.startActivity(intent);
                } else if (type == Alarm.Type.FOLLOW ) {
                    Intent intent = new Intent(mContext, ProfileView.class);
                    intent.putExtra("userId", alarm.getToUser().getId());
                    mContext.startActivity(intent);
                }else if(type == Alarm.Type.FB_FRIEND) {
                    Intent intent = new Intent(mContext, ProfileView.class);
                    intent.putExtra("userId", alarm.getUser().getId());
                    mContext.startActivity(intent);
                }
                flag = false;
            }
        }
    }

    protected View getAlarmBoxLayout(Alarm alarm) {
        Alarm.Type type = alarm.getType();
        View view = null;
        if (type == Alarm.Type.DAY_COMMENT || type == Alarm.Type.DAY_POST || type == Alarm.Type.DAY_LOVE) {
            view = inflater.inflate(R.layout.alarm_photos, null);
            LinearLayout photoBox = (LinearLayout) view.findViewById(R.id.photoBox);
            List<Photo> photoList = alarm.getDay().getPhotoList();
            int photoCount = alarm.getDay().getPhotoCount();
            for (int i = 0; i < photoList.size(); i++) {
                Photo photo = photoList.get(i);
                View addView = inflater.inflate(R.layout.alarm_photo, null);
                ImageView image = (ImageView) addView.findViewById(R.id.photo);
                Glide.with(mContext).load(photo.getSmallUrl()).into(image);
                photoBox.addView(addView);
            }
            TextView photoCountView = (TextView) view.findViewById(R.id.photoCount);
            photoCountView.setText("+" + (photoCount - photoList.size()));
            if (photoCount - photoList.size() == 0)
                photoCountView.setVisibility(View.GONE);
        } else if (type == Alarm.Type.PHOTO_COMMENT || type == Alarm.Type.PHOTO_LOVE) {
            view = inflater.inflate(R.layout.alarm_photo, null);
            ImageView image = (ImageView) view.findViewById(R.id.photo);
            Glide.with(mContext).load(alarm.getPhoto().getSmallUrl()).into(image);
        } else if(type== Alarm.Type.FOLLOW){
            User toUser = alarm.getToUser();
            view = inflater.inflate(R.layout.alarm_follow, null);
            ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView socialStatus = (TextView) view.findViewById(R.id.socailStatus);
            Glide.with(mContext).load(toUser.getImageUrl()).asBitmap().transform(new CircleTransform(mContext)).into(profileImage);
            name.setText(Util.parseName(toUser, 0));
            socialStatus.setText(toUser.getFollowerCount() + " Follower · " + toUser.getFollowingCount() + " Following");
        }else {
            User toUser = alarm.getUser();
            view = inflater.inflate(R.layout.alarm_follow, null);
            ImageView profileImage = (ImageView) view.findViewById(R.id.profileImage);
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView socialStatus = (TextView) view.findViewById(R.id.socailStatus);
            Glide.with(mContext).load(toUser.getImageUrl()).asBitmap().transform(new CircleTransform(mContext)).into(profileImage);
            name.setText(Util.parseName(toUser, 0));
            socialStatus.setText(toUser.getFollowerCount() + " Follower · " + toUser.getFollowingCount() + " Following");
        }
        return view;
    }

    protected Spanned getDescription(Alarm alarm) {
        Alarm.Type type = alarm.getType();
        Spanned description = null;
        String toUser="";
        if(type != Alarm.Type.FB_FRIEND)
            toUser = Util.parseName(alarm.getToUser(), 0);
        if (type == Alarm.Type.PHOTO_COMMENT) {
            description = Html.fromHtml("<b>" + Util.parseName(alarm.getUser(), 0) + "</b> " + "commented <b>" + toUser + "</b>'s photo.");
        } else if (type == Alarm.Type.PHOTO_LOVE) {
            description = Html.fromHtml("<b>" + Util.parseName(alarm.getUser(), 0) + "</b> " + "liked <b>" + toUser + "</b>'s photo.");
        } else if (type == Alarm.Type.DAY_COMMENT) {
            description = Html.fromHtml("<b>" + Util.parseName(alarm.getUser(), 0) + "</b> " + "commented <b>" + toUser + "</b>'s day trip.");
        } else if (type == Alarm.Type.DAY_LOVE) {
            description = Html.fromHtml("<b>" + Util.parseName(alarm.getUser(), 0) + "</b> " + "liked <b>" + toUser + "</b>'s day trip.");
        } else if (type == Alarm.Type.DAY_POST) {
            description = Html.fromHtml("<b>" + Util.parseName(alarm.getUser(), 0) + "</b> " + "uploaded new trip.");
        } else if (type == Alarm.Type.FOLLOW) {
            description = Html.fromHtml("<b>" + Util.parseName(alarm.getUser(), 0) + "</b> " + "followed <b>" + toUser + "</b>.");
        } else if (type == Alarm.Type.FB_FRIEND) {
            description = Html.fromHtml("Facebook friend <b>" + Util.parseName(alarm.getUser(), 0) + "</b> " + "sign up <b>hasBeen</b>.");
        }
        return description;
    }
}
