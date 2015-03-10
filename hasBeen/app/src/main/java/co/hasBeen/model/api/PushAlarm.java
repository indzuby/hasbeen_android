package co.hasBeen.model.api;

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

    public String getMessage(){
        String msg = Util.parseName(firstName,lastName,0)+" ";
        if(action.equalsIgnoreCase("comment")) {
            msg+="commented ";
        }
        if(resource.equalsIgnoreCase("photo")) {
            msg += "your photo";
        }else if(resource.equalsIgnoreCase("day")){
            msg += "your day trip";
        }
        return msg;
    }
}
