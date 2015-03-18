package co.hasBeen;

import android.app.Application;

import com.localytics.android.LocalyticsActivityLifecycleCallbacks;

/**
 * Created by 주현 on 2015-03-18.
 */
public class HasBeenApplicaiton extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(
                new LocalyticsActivityLifecycleCallbacks(this));
    }
}
