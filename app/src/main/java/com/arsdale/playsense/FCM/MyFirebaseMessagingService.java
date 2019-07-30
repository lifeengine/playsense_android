package com.arsdale.playsense.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.arsdale.playsense.MainActivity;
import com.arsdale.playsense.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.d(TAG,"message: "+remoteMessage.getData().get("title") );
            Log.d(TAG,"message: "+remoteMessage.getData().get("message") );
            Log.d(TAG,"message: "+ remoteMessage.getData().get("subtitle") );

            sendNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("message"), remoteMessage.getData().get("subtitle"), remoteMessage.getData().get("attachment"));
            if (true) {
                scheduleJob();
            } else {
                handleNow();
            }

        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        Log.d(TAG,"=====-=-=-=-=shared job=-=-=-=-==-==-=-=-=");
    }


    private void handleNow() {
      Log.d(TAG, "Short lived task is done.");
    }


    private void sendRegistrationToServer(String token) {

    }

    private void sendNotification(String messageTitle, String messageBody, String url, String attachment) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("redirectUrl",url);
        Log.d("tag","message body: "+ messageBody);
        Log.d("tag","attachment: "+attachment);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 7878, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.project_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.main_icon)
                        .setContentTitle((messageTitle))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(messageBody)
                                .setBigContentTitle(messageTitle)
                        )
                        .setContentIntent(pendingIntent);


        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle(notificationBuilder);






        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(9887, notificationBuilder.build());

    }

}
