package co.hasBeen.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import co.hasBeen.MainActivity;
import co.hasBeen.R;
import co.hasBeen.day.DayView;
import co.hasBeen.model.api.Alarm;
import co.hasBeen.model.api.PushAlarm;
import co.hasBeen.photo.PhotoView;
import co.hasBeen.utils.CircleTransform;
import co.hasBeen.utils.JsonConverter;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras.getString("push"));
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            final PushAlarm push = JsonConverter.convertJsonToPushAlarm(msg);
            Bitmap bitmap = getBitmapFromURL(push.getUserImageUrl());
            Intent intent = getActivity(push);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.icon)
                            .setLargeIcon(bitmap)
                            .setContentTitle("HasBeen")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(push.getMessage(this)))
                            .setContentText(push.getMessage(this)).setAutoCancel(true)
                            .setVibrate(new long[]{0, 500});

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Intent getActivity(PushAlarm push) {
        Alarm.Type type = push.getType();
        Intent intent = new Intent(this,MainActivity.class);
        if(type.equals(Alarm.Type.DAY_COMMENT) || type.equals(Alarm.Type.DAY_LOVE)){
            intent = new Intent(this,DayView.class);
            intent.putExtra("id",push.getId());
            if(type.equals(Alarm.Type.DAY_COMMENT))intent.putExtra("type","comment");
        }else if(type.equals(Alarm.Type.PHOTO_COMMENT) || type.equals(Alarm.Type.PHOTO_LOVE)){
            intent = new Intent(this,PhotoView.class);
            intent.putExtra("id",push.getId());
            if(type.equals(Alarm.Type.PHOTO_COMMENT))intent.putExtra("type","comment");
        }
        return intent;
    }
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return CircleTransform.getCircularBitmapImage(myBitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}