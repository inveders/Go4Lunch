package com.inved.go4lunch.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.inved.go4lunch.R;
import com.inved.go4lunch.firebase.User;
import com.inved.go4lunch.firebase.UserHelper;

import java.util.ArrayList;
import java.util.Map;

import static com.inved.go4lunch.controller.activity.RestaurantActivity.TAG;

public class NotificationService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASE_GO4LUNCH";
    private NotificationsActivity notificationsActivity = new NotificationsActivity();
    private FirebaseUser firebaseUser;
    private  String userUid;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);


    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
          super.onMessageReceived(remoteMessage);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if (firebaseUser != null) {

            userUid=firebaseUser.getUid();
        }


        Log.d(TAG, "NotificationService messages From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
    /*    if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            firebaseInformations(remoteMessage);

        /*    if (/* Check if data needs to be processed by long running job */// true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
          /*      Log.d(TAG, "Notification Service for schedulejob " + remoteMessage.getData());
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                Log.d(TAG, "We go in firebaseInformation in Notification Service " + remoteMessage.getData());
                //this.sendVisualNotification(remoteMessage);
                firebaseInformations(remoteMessage);
            }*/

        //}

        // Check if message contains a notification payload.
        if (remoteMessage.getData() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            firebaseInformations(remoteMessage);
        }

    }



    private void sendVisualNotificationAPI26(RemoteMessage remoteMessage,String restaurantName,String restaurantVicinity,ArrayList workmates) {

        Log.d("debago", "Notification API 26 restaurantName "+restaurantName);
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
                        .setContentText(getString(R.string.notification_message_text_app, restaurantName, restaurantVicinity,workmates))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                      //  .setStyle(inboxStyle);
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getString(R.string.notification_message_text_app, restaurantName, restaurantVicinity,workmates)));

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


    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void sendVisualNotification(RemoteMessage remoteMessage,String restaurantName,String restaurantVicinity,ArrayList workmates) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, NotificationsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Map<String, String> data = remoteMessage.getData();
        //String restaurantName = data.get("restaurantName");
        //String restaurantVicinity = data.get("restaurantAddress");
        //String workmates = data.get("restaurantWorkmates");



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
                        .setContentText(getString(R.string.notification_message_text_app, restaurantName, restaurantVicinity,workmates))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        //  .setStyle(inboxStyle);
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getString(R.string.notification_message_text_app, restaurantName, restaurantVicinity,workmates)));

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

    private void firebaseInformations(RemoteMessage remoteMessage){

        UserHelper.getUserWhateverLocation(userUid).get().addOnSuccessListener(queryDocumentSnapshots -> {
            User currentUser = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);

            if(currentUser != null){
                String restaurantName = TextUtils.isEmpty(currentUser.getRestaurantName()) ? getString(R.string.info_no_restaurant_name_found) : currentUser.getRestaurantName();
                String restaurantVicinity = TextUtils.isEmpty(currentUser.getRestaurantVicinity()) ? getString(R.string.info_no_restaurant_adresse_found) : currentUser.getRestaurantVicinity();

                UserHelper.getAllWorkmatesJoining(currentUser.getRestaurantPlaceId(),currentUser.getJobPlaceId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> workmates = new ArrayList<String>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                workmates.add(document.get("firstname").toString());

                            }
                            for (int i = 0; i <workmates.size() ; i++) {
                                if(workmates.get(i).equals(currentUser.getFirstname())){
                                    workmates.remove(i);
                                }
                                Log.d("debago", "workmates are "+ workmates);

                            }
                        } else {
                            Log.d("debago", "Error getting documents: ", task.getException());
                        }

                        Log.d("debago","NotificationService notifidationMessage "+getString(R.string.notification_message_text_app,restaurantName,restaurantVicinity,workmates));
                        //sendVisualNotificationAPI26(remoteMessage,restaurantName,restaurantVicinity,workmates);
                        sendVisualNotification(remoteMessage,restaurantName,restaurantVicinity,workmates);
                    }
                });


            }


        });

    }

}
