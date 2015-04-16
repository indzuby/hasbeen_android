package co.hasBeen.main;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.localytics.android.LocalyticsActivityLifecycleCallbacks;

/**
 * Created by 주현 on 2015-03-18.
 */
public class HasBeenApplicaiton extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(
                new LocalyticsActivityLifecycleCallbacks(this));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
