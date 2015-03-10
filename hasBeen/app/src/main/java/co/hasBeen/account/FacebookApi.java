package co.hasBeen.account;

import android.content.Context;

import com.facebook.Session;

/**
 * Created by 주현 on 2015-03-10.
 */
public class FacebookApi {
    public static void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        } else {

            session = new Session(context);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
            //clear your preferences if saved

        }

    }
}
