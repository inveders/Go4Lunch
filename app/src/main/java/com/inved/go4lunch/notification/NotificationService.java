package com.inved.go4lunch.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.inved.go4lunch.R;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);


    }



    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
          super.onMessageReceived(remoteMessage);

       sendVisualNotification(remoteMessage);

    }


    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void sendVisualNotification(RemoteMessage remoteMessage) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, NotificationsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.notification_title));

        // 3 - Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        if(remoteMessage.getNotification()!=null){
            // 4 - Build a Notification object
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_notifications_primary_color_24dp)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(remoteMessage.getNotification().getBody())
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent)
                            //  .setStyle(inboxStyle);
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(remoteMessage.getNotification().getBody()));

            // 5 - Add the Notification to the Notification Manager and show it.
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
            // 6 - Support Version >= Android 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence channelName = "Message provenant de Firebase";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            // 7 - Show notification

                String NOTIFICATION_TAG = "FIREBASE_GO4LUNCH";
                int NOTIFICATION_ID = 7;
                notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
            }
        }

    }


}
