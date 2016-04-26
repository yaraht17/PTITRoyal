package com.ptit.ptitroyal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.astuetz.PagerSlidingTabStrip;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.JSONObjectRequestListener;
import com.ptit.ptitroyal.cores.CoreActivity;
import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.fragments.ActivitiesFragment;
import com.ptit.ptitroyal.fragments.GastronomyFragment;
import com.ptit.ptitroyal.fragments.StudyFragment;
import com.ptit.ptitroyal.models.Author;
import com.ptit.ptitroyal.models.User;
import com.ptit.ptitroyal.service.RegistrationIntentService;
import com.ptit.ptitroyal.view.AwesomeButton;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends CoreActivity implements View.OnClickListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "TienDH";

    private SharedPreferences sharedPreferences;
    public static String accessToken;


    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

    private StudyFragment studyFragment;
    private ActivitiesFragment activitiesFragment;
    private GastronomyFragment gastronomyFragment;

    private static BadgeView bvNotification;

    public static Author user;

    private User userProfile;

    private DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;

    private LinearLayout postStatus;
    private ImageView imgNotification;

    private TextView txtUserName;
    private ImageView imgAvatar;

    private AwesomeButton btnLeft;
    private TextView txtTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());

        sharedPreferences = getSharedPreferences(Constants.PTIT_ROYAL_PREFERENCES, Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString(Constants.ACCESS_TOKEN, "");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerLayout = mNavigationView.getHeaderView(0);

        txtUserName = (TextView) headerLayout.findViewById(R.id.txtName);
        imgAvatar = (ImageView) headerLayout.findViewById(R.id.imgAvatar);

        btnLeft = (AwesomeButton) findViewById(R.id.btnLeft);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnLeft.setOnClickListener(this);

        studyFragment = new StudyFragment();
        activitiesFragment = new ActivitiesFragment();
        gastronomyFragment = new GastronomyFragment();

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        postStatus = (LinearLayout) findViewById(R.id.postStatus);
        imgNotification = (ImageView) findViewById(R.id.imgNotification);
        imgNotification.setOnClickListener(this);
        postStatus.setOnClickListener(this);
        bvNotification = new BadgeView(this, imgNotification);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent;
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {

                    case R.id.nav_profiles:
                        intent = new Intent(getApplicationContext(), TimelineActivity.class);
                        intent.putExtra("USER", userProfile);
                        startActivity(intent);

                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.nav_chat_bot:
                        intent = new Intent(getApplicationContext(), ChatBotActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.nav_event:
                        intent = new Intent(getApplicationContext(), EventActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.nav_logout:
                        logout();
                        mDrawerLayout.closeDrawers();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }


    public static void setNumberOfNotifications(int number) {
        if (number > 0) {
            bvNotification.setText(String.valueOf(number));
            bvNotification.show();
        } else {
            bvNotification.hide();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        APIConnection.getNumberOfNotifications(this, accessToken);
        getProfile();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imgNotification:

                intent = new Intent(this, NotificationsActivity.class);
                startActivity(intent);
                break;
            case R.id.postStatus:
                intent = new Intent(this, PostActivity.class);
                startActivity(intent);
                break;
            case R.id.btnLeft:
                if (userProfile != null)
                    mDrawerLayout.openDrawer(GravityCompat.START);
            default:
                break;
        }
    }

    private void getProfile() {
        APIConnection.request(getApplicationContext(), Constants.API_PROFILE, accessToken, new JSONObjectRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {

                try {
                    String status = response.getString(Constants.STATUS);
                    if (status.equals(Constants.SUCCESS)) {
                        JSONObject result = response.getJSONObject("result");
                        User employee = new Gson().fromJson(result.toString(), User.class);
                        userProfile = new User(employee.getId(), employee.getName(),
                                employee.getAvatar(), employee.getCover(), employee.getGender(), employee.getEmail());
                        txtUserName.setText(employee.getName());
                        Picasso.with(MainActivity.this)
                                .load(employee.getAvatar())
                                .placeholder(R.mipmap.ic_avatar)
                                .error(R.mipmap.ic_avatar)
                                .into(imgAvatar);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                showToastLong(getString(R.string.request_error));
            }
        });

    }

    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("\n" + getString(R.string.logout_question) + "\n");
        builder.setCancelable(false);
        builder.setPositiveButton("Hủy",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //logout here
                        pDialog = new ProgressDialog(MainActivity.this);
                        pDialog.setMessage("Logout...");
                        pDialog.show();
                        APIConnection.request(getApplicationContext(), Constants.API_LOGOUT, accessToken, new JSONObjectRequestListener() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        try {
                                            String status = response.getString(Constants.STATUS);
                                            if (status.equals(Constants.SUCCESS)) {
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.remove(Constants.ACCESS_TOKEN);
                                                editor.apply();
                                                pDialog.cancel();
                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                                LoginManager.getInstance().logOut();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        pDialog.cancel();
                                        showToastLong(getString(R.string.request_error));
                                    }
                                }

                        );
                    }
                }

        );
        builder.create().

                show();
    }


    public class MyPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

        private final String[] TITLES = {"Học tập", "Vui chơi", "Ăn uống"};
        private final int[] ICONS = {R.mipmap.ic_study, R.mipmap.ic_play, R.mipmap.ic_food};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return activitiesFragment;
                case 2:
                    return gastronomyFragment;
                default:
                    return studyFragment;
            }
        }

        @Override
        public View getCustomTabView(ViewGroup parent, int position) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_tab, null);
            ImageView imgIcon = (ImageView) v.findViewById(R.id.imgIcon);
            imgIcon.setImageDrawable(getResources().getDrawable(ICONS[position]));
            return v;
        }

        @Override
        public void tabSelected(View tab) {
            tab.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        @Override
        public void tabUnselected(View tab) {
            tab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

    }

}

