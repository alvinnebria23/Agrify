package com.example.agrify;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.agrify.ChatPackage.InboxActivity;
import com.example.agrify.LoginPackage.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String token = "";
    private NotificationManager notifManager;
    private InputStream in;
    Bitmap myBitmap;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String userID = remoteMessage.getData().get("user_id").toString();
        String profilePicture = Constants.PATH_PROFILE_IMAGES_FOLDER+remoteMessage.getData().get("profile_pic").toString();
        String fullName = remoteMessage.getData().get("fullName").toString();
        String message = remoteMessage.getData().get("message").toString();
        createNotification(userID, profilePicture, fullName, message, getApplicationContext());

    }
    public void createNotification(String userID, String profilePic, String name, String aMessage, Context context) {
        Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
        pushNotification.putExtra("userID", userID);
        pushNotification.putExtra("profilePicture", profilePic);
        pushNotification.putExtra("fullName", name);
        pushNotification.putExtra("message", aMessage);
        final int NOTIFY_ID = 0; // ID of notification
        String message = "";
        String id = context.getString(R.string.default_notification_channel_id); // default_channel_id
        String title = context.getString(R.string.default_notification_channel_title); // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        final NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(aMessage.length() > 35){
            message = aMessage.substring(0,35) + "...";
        }else{
            message = aMessage;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100});
                notifManager.createNotificationChannel(mChannel);
            }
            try {
                URL url = new URL(profilePic);
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
            String contentTitle = setContentTitle(name);
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, InboxActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(contentTitle)                            // required
                    .setSmallIcon(R.drawable.agrifylogoonly)   // required
                    .setLargeIcon(myBitmap)
                    .setContentText(message) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100});
            notifManager.notify(NOTIFY_ID, builder.build());
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(R.drawable.agrifylogoonly)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    return;
                }
                // Get new Instance ID token
                token = task.getResult().getToken();
            }
        });
    }
    public String setContentTitle(String name){
        String nameArray[] = name.split(" ");
        String firstLetterFirstName   = nameArray[0].charAt(0) + "";
        String firstLetterLastName    = nameArray[1].charAt(0) + "";
        String fullName = firstLetterFirstName.toUpperCase() + nameArray[0].substring(1, nameArray[0].length()) + " " + firstLetterLastName.toUpperCase() + nameArray[1].substring(1, nameArray[1].length());
        return fullName + " sent you a message.";
    }
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
