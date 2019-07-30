package com.arsdale.playsense.FCM;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompatExtras;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.arsdale.playsense.MainActivity;
import com.arsdale.playsense.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MyReceiver extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;

        sendNotification(intent.getStringExtra("title"),intent.getStringExtra("message"), intent.getStringExtra("subtitle"), intent.getStringExtra("attachment"));

    }


    public void sendNotification(final String messageTitle, final String messageBody, String url, final String attachment) {

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("redirectUrl",url);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 7878, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final String channelId = mContext.getString(R.string.project_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mContext, channelId)
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

        if (attachment == null || attachment.equals("") ) {
            setNotification(channelId,notificationBuilder);
        }else {
            new BitmapReturn(attachment, messageTitle, messageBody, channelId, notificationBuilder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }


    }

    public void setNotification(String channelId, NotificationCompat.Builder notificationBuilder) {

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.cancel(9887);
        notificationManager.notify(9887, notificationBuilder.build());

    }

    public class BitmapReturn extends AsyncTask {

        private String attachment = "";

        private NotificationCompat.Builder notificationBuilder;
        private String messageTitle = "";
        private String messageBody = "";
        private String channelId = "";

        public BitmapReturn(String attachment, String messageTitle, String messageBody, String channelId, NotificationCompat.Builder notificationBuilder ) {
            this.attachment = attachment;
            this.messageTitle = messageTitle;
            this.messageBody = messageBody;
            this.channelId = channelId;
            this.notificationBuilder = notificationBuilder;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            InputStream in;
            URL url = null;
            Bitmap myBitmap = null;
            try {
                url = new URL(this.attachment);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(in);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.remoteview);

            //notificationBuilder.setLargeIcon(myBitmap);
            contentView.setImageViewBitmap(R.id.imgArea,myBitmap);
            contentView.setTextViewText(R.id.titleText, messageTitle);
            contentView.setTextViewText(R.id.messageText, messageBody);
            notificationBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());



            RemoteViews smallContentView = new RemoteViews(mContext.getPackageName(), R.layout.remote_small_view);
            smallContentView.setTextViewText(R.id.smallTitleText, messageTitle);
            smallContentView.setTextViewText(R.id.smallMessageText, messageBody);
            smallContentView.setImageViewBitmap(R.id.smallImgArea, myBitmap);


            //Drawable draw = new BitmapDrawable(mContext.getResources(), myBitmap);


            notificationBuilder.setCustomContentView(smallContentView);
            notificationBuilder.setCustomBigContentView(contentView);

            setNotification(channelId,notificationBuilder);

            return myBitmap;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

    }


}
