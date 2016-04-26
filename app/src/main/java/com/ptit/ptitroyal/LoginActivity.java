package com.ptit.ptitroyal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.JSONObjectRequestListener;
import com.ptit.ptitroyal.cores.CoreActivity;
import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends CoreActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "LoginActivity";

    private LoginButton btnFacebookLogin;
    private CallbackManager callbackManager;
    private int screenWidth;
    private AccessToken facebookToken;
    private AccessTokenTracker facebookTokenTracker;

    private ProfileTracker profileTracker;

    private SharedPreferences sharedPreferences;
    private String accessToken;

    private ProgressBar pBar;
    private LinearLayout layoutLogin, layoutLogo;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(Constants.PTIT_ROYAL_PREFERENCES, Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString(Constants.ACCESS_TOKEN, "");

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        layoutLogin = (LinearLayout) findViewById(R.id.layoutLogin);
        layoutLogo = (LinearLayout) findViewById(R.id.logoLayout);

        hideLogin();
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(1 * 1000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                layoutLogo.animate().translationY(-(screenWidth / 3)).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());
                checkLogin();
            }
        };
        background.start();
        btnFacebookLogin = (LoginButton) findViewById(R.id.login_button);
        btnFacebookLogin.setReadPermissions("public_profile email");
        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                facebookToken = AccessToken.getCurrentAccessToken();
                requestServer();

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                //update Profile
            }
        };
        facebookTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                facebookToken = currentAccessToken;
//                requestServer();
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        facebookTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }


    private void requestServer() {
        layoutLogin.setVisibility(View.INVISIBLE);
        facebookToken = AccessToken.getCurrentAccessToken();
        String s = facebookToken.getToken();
        Log.d("TienDH", " facebook_token: " + s);
        if (facebookToken != null) {
            showDialog("Login...");
            APIConnection.login(this, facebookToken.getToken(), new JSONObjectRequestListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    hideDialog();

                    try {
                        String status = response.getString(Constants.STATUS);
                        if (status.equals(Constants.SUCCESS)) {
                            accessToken = response.getString(Constants.RESULT);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.ACCESS_TOKEN, accessToken);
                            editor.apply();
                            intentHome();
                        } else {

                        }
                    } catch (JSONException e) {
                        LoginManager.getInstance().logOut();
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(VolleyError error) {
                    LoginManager.getInstance().logOut();
                    hideDialog();

                }
            });
        }
    }

    private void intentHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkLogin() {
        if (!accessToken.equals("")) {
            // show login
            verifyToken();

        } else {
            showLogin();

        }
    }

    Handler mHandler = new Handler();

    private Runnable delay = new Runnable() {
        public void run() {
            pBar.setVisibility(View.INVISIBLE);
            layoutLogin.setVisibility(View.VISIBLE);
        }
    };

    private void showLogin() {
        Log.d("TienDH", "showLogin()");
        mHandler.postDelayed(delay, 1000);
    }

    private void hideLogin() {
        pBar.setVisibility(View.VISIBLE);
        layoutLogin.setVisibility(View.INVISIBLE);
    }

    private void verifyToken() {
        APIConnection.request(this, Constants.API_VERIFY_TOKEN, accessToken, new JSONObjectRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                //check success
                try {
                    String status = response.getString(Constants.STATUS);
                    if (status.equals(Constants.SUCCESS)) {
                        intentHome();
                    } else {
                        showLogin();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(Constants.ACCESS_TOKEN);
                        editor.apply();
                        LoginManager.getInstance().logOut();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToastShort(getString(R.string.request_error));
                }
            }

            @Override
            public void onError(VolleyError error) {
                showToastShort(getString(R.string.request_error));
            }
        });

    }


}
