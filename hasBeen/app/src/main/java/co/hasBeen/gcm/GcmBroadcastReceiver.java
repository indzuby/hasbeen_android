package co.hasBeen.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import co.hasBeen.utils.Session;


/**
 * Created by 주현 on 2015-02-23.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        Bundle bundle = intent.getExtras();
        for (String key : bundle.keySet())
        {
            Object value = bundle.get(key);
            if (key.equalsIgnoreCase("registration_id"))
            {
                String regId = bundle.getString(key);
                Session.putString(context,"regid",regId);
            }
        }
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}