package co.hasBeen.model.api;

import android.content.Context;

import co.hasBeen.R;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-10.
 */
public class PushAlarm {
    Long id;
    String firstName;
    String lastName;
    String userImageUrl;
    Alarm.Type type;

    public Alarm.Type getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getMessage(Context context){
        String msg = "";
        if(getType().equals(Alarm.Type.PHOTO_COMMENT)) {
            msg=context.getString(R.string.commented_photo_push, Util.parseName(firstName, lastName, context),context.getString(R.string.your_user));
        }else if(getType().equals(Alarm.Type.DAY_COMMENT)) {
            msg=context.getString(R.string.commented_day_push,Util.parseName(firstName, lastName, context),context.getString(R.string.your_user));
        }
        else if(getType().equals(Alarm.Type.PHOTO_LOVE)) {
            msg=context.getString(R.string.loved_photo_push,Util.parseName(firstName, lastName, context),context.getString(R.string.your_user));
        }else if(getType().equals(Alarm.Type.DAY_LOVE)) {
            msg=context.getString(R.string.loved_day_push,Util.parseName(firstName, lastName, context),context.getString(R.string.your_user));
        }else if(getType().equals(Alarm.Type.DAY_POST)) {
            String you = context.getString(R.string.your_user);
            msg=context.getString(R.string.day_post_done,you);
        }
        return msg;
    }
}
