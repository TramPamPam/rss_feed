package com.example.HT2;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;
import com.example.HT2.activities.FeedListActivity;
import com.example.HT2.tasks.DownloadFilesTask;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RssUpdaterService extends Service {

    private Timer updateTimer;
    private String lastRead = null;//new Date(1, 1, 1);

    /** For showing and hiding our notification. */
    NotificationManager mNM;

    String url = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";
    public static final String PREFS_NAME = "shared_prefs";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {

        updateTimer = new Timer("RSSServiceUpdateTimer");

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


    Toast.makeText(this, "RSSService Started", Toast.LENGTH_LONG).show();

        // TODO: Read from user preferences
        int period = 10;

        // cancel the current timer
        updateTimer.cancel();

        // create a new timer
        updateTimer = new Timer("RSSServiceUpdateTimer");
        updateTimer.scheduleAtFixedRate(
                new TimerTask() {

                    @Override
                    public void run() {
                        RssUpdaterService.this.refreshFeed();
                    }

                }, 50, period*60*1000);
        return super.onStartCommand(intent, flags, startId);
    }

    protected void refreshFeed() {
        DownloadFilesTask downloadFilesTask = (DownloadFilesTask) new DownloadFilesTask(null).execute(url);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
//        String lastDate = settings.getString("date","");
        int lastSize = settings.getInt("size",-1) ;
        int lastItemsSize;
        if (downloadFilesTask.feedList != null)
            lastItemsSize = downloadFilesTask.feedList.size();
        else
            lastItemsSize = -1;
        //String lastItemDate = lastItem.getDate();
        if (lastItemsSize>lastSize){
            announceNewFeed(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("size", lastItemsSize);
            editor.commit();
            //update last date in prefs:
//            lastDate = lastItemDate;
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putString("date", lastDate);
//            editor.commit();
        }
    }

    private void announceNewFeed(RssUpdaterService feed) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_social_person)
                        .setContentTitle("Rss Feed says:")
                        .setContentText("There is new post in rss feed!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, FeedListActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(FeedListActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel the persistent notification.
        mNM.cancel(R.string.remote_service_started);
        Toast.makeText(this, "RSSService Stopped", Toast.LENGTH_LONG).show();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_social_person)
                        .setContentTitle("Rss Feed says:")
                        .setContentText("We've started update service for rss feed!");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, FeedListActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(FeedListActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }
}
