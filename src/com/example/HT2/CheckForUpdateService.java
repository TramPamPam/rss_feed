package com.example.HT2;

import com.example.HT2.activities.RSSActivity;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class CheckForUpdateService extends Service {

    NotificationManager notifManager;
    JSONObject lastJson;

    static final int TIMER_INTERVAL = 300000; //5m
    static final int NOTIFICATION_ID = 12365897;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            serviceTask();
            timerHandler.postDelayed(this, TIMER_INTERVAL);
        }
    };

    public void onCreate() {
        super.onCreate();
        notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sendNotification("Service starting to work!", false);

        Log.d("HW6", "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("HW6", "onStartCommand");
//                serviceTask();
        timerHandler.postDelayed(timerRunnable, 0);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        timerHandler.removeCallbacks(timerRunnable);
        notifManager.cancel(NOTIFICATION_ID);
        super.onDestroy();
        Log.d("HW6", "onDestroy");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d("HW6", "onBind");
        return null;
    }

    void serviceTask() {
        new Thread(new Runnable() {
            public void run() {
                JSONObject json = RSSActivity.getJSONFromUrl(RSSActivity.URL);
                if (lastJson != null)
                {
                    if (json.hashCode() != lastJson.hashCode())
                    {
                        sendNotification("RSS Updated!", true);
                        Log.d("HW6", "RSS Updated!");
                    }
                }
                lastJson = json;

//                                Log.d("HW6", "get json " + json);
            }
        }).start();
    }

    void sendNotification(String message, boolean isAnimated) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.location_web_site).setContentTitle("RSS Reader Check for update service").setContentText(message);

        if (isAnimated)
        {
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        }

        Intent resultIntent = new Intent(this, RSSActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(RSSActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        notifManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
