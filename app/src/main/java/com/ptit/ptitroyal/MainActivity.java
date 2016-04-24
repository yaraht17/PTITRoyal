package com.ptit.ptitroyal;

import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.cores.CoreActivity;
import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.fragments.ActivitiesFragment;
import com.ptit.ptitroyal.fragments.GastronomyFragment;
import com.ptit.ptitroyal.fragments.StudyFragment;
import com.ptit.ptitroyal.models.Author;
import com.ptit.ptitroyal.service.RegistrationIntentService;
import com.readystatesoftware.viewbadger.BadgeView;

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

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    private int mCurrentSelectedPosition;


    private LinearLayout postStatus;
    private ImageView imgNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(Constants.PTIT_ROYAL_PREFERENCES, Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString(Constants.ACCESS_TOKEN, "");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


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

        APIConnection.getNumberOfNotifications(this, accessToken);

        setUpToolbar();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);


        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        setUpNavDrawer();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_home:


                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.navigation_item_profiles:
                        Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                        startActivity(intent);

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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        Menu menu = mNavigationView.getMenu();
        menu.getItem(mCurrentSelectedPosition).setChecked(true);
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

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
            default:
                break;
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

        private final String[] TITLES = {"Học tập", "Vui chơi", "Ăn uống"};
        private final int[] ICONS = {R.mipmap.ic_study, R.mipmap.ic_activities, R.mipmap.ic_gastronomy};

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

