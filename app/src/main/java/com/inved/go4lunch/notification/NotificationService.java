package com.inved.go4lunch.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.inved.go4lunch.R;

import java.util.Map;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.TAG;

public class NotificationService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASE_GO4LUNCH";
    private NotificationsActivity notificationsActivity = new NotificationsActivity();



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //  super.onMessageReceived(remoteMessage);


        // Check if message contains a data payload.
        if (remoteMessage.getData() != null) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            this.sendVisualNotification(remoteMessage);

      /*      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                this.sendVisualNotificationAPI26(remoteMessage);
            } else {
                this.sendVisualNotification(remoteMessage);
            }*/

        }

      /*  if (remoteMessage.getNotification() != null) {
            // 1 - Get message sent by Firebase
            String message = remoteMessage.getNotification().getBody();
            // 8 - Show notification after received message
            this.sendVisualNotification(message);
            //Log.e("TAG", message);
        }*/
    }

    private void sendVisualNotificationAPI26(RemoteMessage remoteMessage) {
    }


    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void sendVisualNotification(RemoteMessage remoteMessage) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, NotificationsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

      /*  Map<String, String> data = remoteMessage.getData();
        String restaurantName = data.get("restaurantName");
        String restaurantAddress = data.get("restaurantAddress");*/

        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.notification_title));
        //inboxStyle.addLine();

        // 3 - Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notifications_primary_color_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(remoteMessage.getData().get(getString(R.string.notification_message_text, "La table de Mario", "12 rue principale Ottange")))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

}
