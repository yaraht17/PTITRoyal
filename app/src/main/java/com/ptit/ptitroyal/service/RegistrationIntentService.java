package com.ptit.ptitroyal.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.ptit.ptitroyal.R;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.JSONObjectRequestListener;
import com.ptit.ptitroyal.data.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "TienDH";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sp = getSharedPreferences(Constants.PTIT_ROYAL_PREFERENCES, Context.MODE_PRIVATE);
        String accessToken = sp.getString(Constants.ACCESS_TOKEN, "");
        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "Access Token: " + accessToken);
            Log.d(TAG, "GCM Registration Token: " + token);

            sendRegistrationToServer(accessToken, token);

            subscribeTopics(token);


        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }

    private void sendRegistrationToServer(String accessToken, String token) {
        Log.d("TienDH", "Token: " + token);

        APIConnection.updateGCMID(this, accessToken, token, new JSONObjectRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String status = response.getString(Constants.STATUS);
                    if (status.equals(Constants.SUCCESS)) {
                        Log.d("TienDH", "GCM ID Update Success");
                    } else {
                        Log.d("TienDH", "GCM ID Update Fail");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.d("TienDH", "GCM ID Update Error");
            }
        });
    }
    
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

}