package co.hasBeen.model.api;

import android.content.Context;

import co.hasBeen.R;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-10.
 */
public class PushAlarm {
    String resource;
    String action;
    Long id;
    String firstName;
    String lastName;
    String userImageUrl;

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
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
        String msg = Util.parseName(firstName,lastName,context);
        if(action.equalsIgnoreCase("comment") && resource.equalsIgnoreCase("photo")) {
            msg+=context.getString(R.string.commented_photo_alarm,msg,context.getString(R.string.you_user));
        }else if(action.equalsIgnoreCase("comment") && resource.equalsIgnoreCase("day")) {
            msg+=context.getString(R.string.commented_day_alarm,msg,context.getString(R.string.you_user));
        }
        return msg;
    }
}
