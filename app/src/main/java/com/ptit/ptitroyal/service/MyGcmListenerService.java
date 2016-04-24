package com.ptit.ptitroyal.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.ptit.ptitroyal.MainActivity;
import com.ptit.ptitroyal.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.Set;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, "From: " + from);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        String content = "";

        Set<String> keys = data.keySet();
        for (String key : keys) {
            Log.d("TienDH", "key: " + key);
        }
        String type = data.getString("gcm.notification.type");
        String fromData = data.getString("gcm.notification.from");
        String name = "";
        try {
            JSONObject fromJson = new JSONObject(fromData);
            name = fromJson.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TienDH", "from: " + fromData);
        Log.d("TienDH", "type: " + type);

        //1 like, 2 cmt
        if (Integer.parseInt(type) == 1) {
            content = name + " like your post";
        } else if (Integer.parseInt(type) == 2) {
            content = name + " comment your post";
        }

        sendNotification("PTIT Royal", content);
        Log.d("TienDH", "data: " + data.toString());

        // [END_EXCLUDE]
    }
    // [END receive_message]

    private void sendNotification(String title, String content) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random rd = new Random();
        int pos = rd.nextInt(10);
        notificationManager.notify(pos /* ID of notification */, notificationBuilder.build());
    }
}