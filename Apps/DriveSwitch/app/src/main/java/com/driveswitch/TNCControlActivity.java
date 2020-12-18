package com.driveswitch;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.driveswitch.driveswitch.R;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import android.support.v7.app.ActionBar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

public class TNCControlActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Double userLatitude = 0.00;
    private Double userLongitude = 0.00;

    private View mProgressView;
    private View mFormView;

    User myUser = null;

    NotificationManager notificationManager;

    private Integer ANIMATION_DURATION = 1500;

    // Set to 1 less than what we need due to 1 second offset
    private Integer ANIMATION_REPEAT_COUNT = 9;

    String activeTNCId = Constants.EmptyObjectId;

    private Uri notification;
    private Ringtone notificationPlayer;

    private TextView requestLabel;

    @Override
    public void onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        // Stop location reporting
        //locationManager.removeUpdates(locationListener);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        // Resume location reporting
        //locationManager.requestLocationUpdates(getResources().getString(R.string.app_location_provider), Integer.parseInt(getResources().getString(R.string.app_location_refresh_interval)), 0, locationListener);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // Stop location reporting
        stopLocationReporting();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void stopLocationReporting()
    {
        // Stop location reporting
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_1);
        notificationPlayer = RingtoneManager.getRingtone(getApplicationContext(), notification);

        requestLabel = (TextView) findViewById(R.id.lblMessage);

        // Register broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("RideRequest"));

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        populateMyUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnccontrol);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set logo in ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo_driveswitch);

        // Set button listeners
        View mSelectTNCButton = (View) findViewById(R.id.FARE_Container);
        mSelectTNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTNCButton(Constants.TNCList.FARE, Constants.TNCPackageNames.FARE);
            }
        });

        mSelectTNCButton = (View) findViewById(R.id.Fasten_Container);
        mSelectTNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTNCButton(Constants.TNCList.Fasten, Constants.TNCPackageNames.Fasten);
            }
        });

        mSelectTNCButton = (View) findViewById(R.id.GetMe_Container);
        mSelectTNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTNCButton(Constants.TNCList.GetMe, Constants.TNCPackageNames.GetMe);
            }
        });

        mSelectTNCButton = (View) findViewById(R.id.InstaRyde_Container);
        mSelectTNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTNCButton(Constants.TNCList.InstaRyde, Constants.TNCPackageNames.InstaRyde);
            }
        });

        mSelectTNCButton = (View) findViewById(R.id.Lyft_Container);
        mSelectTNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTNCButton(Constants.TNCList.Lyft, Constants.TNCPackageNames.Lyft);
            }
        });

        mSelectTNCButton = (View) findViewById(R.id.RideAustin_Container);
        mSelectTNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTNCButton(Constants.TNCList.RideAustin, Constants.TNCPackageNames.RideAustin);
            }
        });

        mSelectTNCButton = (View) findViewById(R.id.ScoopMe_Container);
        mSelectTNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTNCButton(Constants.TNCList.ScoopMe, Constants.TNCPackageNames.ScoopMe);
            }
        });

        mSelectTNCButton = (View) findViewById(R.id.Tride_Container);
        mSelectTNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTNCButton(Constants.TNCList.Tride, Constants.TNCPackageNames.Tride);
            }
        });

        mSelectTNCButton = (View) findViewById(R.id.Uber_Container);
        mSelectTNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTNCButton(Constants.TNCList.Uber, Constants.TNCPackageNames.Uber);
            }
        });

        mSelectTNCButton = (View) findViewById(R.id.WingZ_Container);
        mSelectTNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTNCButton(Constants.TNCList.WingZ, Constants.TNCPackageNames.WingZ);
            }
        });

        mFormView = findViewById(R.id.mainContainer);
        mProgressView = findViewById(R.id.progress_bar);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();

                // This updates the user's stored prefs with current user data for integrated subscription validation
                Utilities.logUserLocationAndValidateSubscription(TNCControlActivity.this, myUser._id, activeTNCId, userLatitude, userLongitude);
                populateMyUser();
                validateSubscription();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(getResources().getString(R.string.app_location_provider), Integer.parseInt(getResources().getString(R.string.app_location_refresh_interval)), 0, locationListener);

        // This updates the user's stored prefs with current user data for integrated subscription validation
        Utilities.logUserLocationAndValidateSubscription(this, myUser._id, activeTNCId, userLatitude, userLongitude);
        populateMyUser();
        validateSubscription();

        setUserTNCButtons();

        filterMenus();
        setRequestDisplay();

        // Start waiting animation
        setRequestLabel();

        //startTNCApps();
    }

    private void validateSubscription()
    {
        try {
            if (myUser.Expired)
            {
                stopLocationReporting();

                // Create an explicit intent to handle request
                Intent resultIntent = new Intent(this, SettingsSubscriptionActivity.class);
                resultIntent.putExtra("Expired", myUser.Expired);
                resultIntent.putExtra("ExpireDate", myUser.ExpireDate);

                startActivity(resultIntent);
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
    }

    private void setRequestDisplay()
    {
        notificationManager.cancelAll();

        List<Notification> currentRequests = TNCRideRequests.getPendingRequests(this);

        if(currentRequests != null)
        {
            if(currentRequests.size() > 0)
            {
                hideAllButtons();

                Integer pendingRequestCount = currentRequests.size();

                String tncName = "";
                String tncPackage = "";

                for (int i = 0; i < pendingRequestCount; i++) {
                    Notification _request = currentRequests.get(i);

                    tncPackage = _request.PackageName;

                    switch (tncPackage) {
                        case Constants.TNCPackageNames.FARE:
                            tncName = Constants.TNCList.FARE;
                            break;
                        case Constants.TNCPackageNames.Fasten:
                            tncName = Constants.TNCList.Fasten;
                            break;
                        case Constants.TNCPackageNames.GetMe:
                            tncName = Constants.TNCList.GetMe;
                            break;
                        case Constants.TNCPackageNames.InstaRyde:
                            tncName = Constants.TNCList.InstaRyde;
                            break;
                        case Constants.TNCPackageNames.Lyft:
                            tncName = Constants.TNCList.Lyft;
                            break;
                        case Constants.TNCPackageNames.RideAustin:
                            tncName = Constants.TNCList.RideAustin;
                            break;
                        case Constants.TNCPackageNames.ScoopMe:
                            tncName = Constants.TNCList.ScoopMe;
                            break;
                        case Constants.TNCPackageNames.Tride:
                            tncName = Constants.TNCList.Tride;
                            break;
                        case Constants.TNCPackageNames.Uber:
                            tncName = Constants.TNCList.Uber;
                            break;
                        case Constants.TNCPackageNames.WingZ:
                            tncName = Constants.TNCList.WingZ;
                            break;
                    }

                    setTNCButtonHilight(tncName);
                }
            }
            else
            {
                // No requests. Do nothing.
            }
        }
    }

    private void setUserTNCButtons()
    {
        try
        {
            Integer selctedCount = myUser.TNCs.size();
            if(selctedCount > 0)
            {
                for (Integer i = 0; i < selctedCount; i++)
                {
                    TNC _tnc = myUser.TNCs.get(i);
                    showTNCButton(_tnc.Name);
                }
            }
            else
            {
                startActivity(new Intent(this, SettingsTNCActivity.class));
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
    }

    private void startTNCApps()
    {
        PackageManager pm = getPackageManager();
        Intent tncIntent;

        try
        {
            Integer selectedCount = myUser.TNCs.size();
            if(selectedCount > 0)
            {
                for (Integer i = 0; i < selectedCount; i++)
                {
                    TNC _tnc = myUser.TNCs.get(i);
                    switch(_tnc.Name)
                    {
                        case Constants.TNCList.FARE:
                            tncIntent = pm.getLaunchIntentForPackage(Constants.TNCPackageNames.FARE);
                            startActivity(tncIntent);
                            break;
                        case Constants.TNCList.Fasten:
                            tncIntent = pm.getLaunchIntentForPackage(Constants.TNCPackageNames.FARE);
                            startActivity(tncIntent);
                            break;
                        case Constants.TNCList.GetMe:
                            tncIntent = pm.getLaunchIntentForPackage(Constants.TNCPackageNames.GetMe);
                            startActivity(tncIntent);
                            break;
                        case Constants.TNCList.InstaRyde:
                            tncIntent = pm.getLaunchIntentForPackage(Constants.TNCPackageNames.InstaRyde);
                            startActivity(tncIntent);
                            break;
                        case Constants.TNCList.Lyft:
                            tncIntent = pm.getLaunchIntentForPackage(Constants.TNCPackageNames.Lyft);
                            startActivity(tncIntent);
                            break;
                        case Constants.TNCList.RideAustin:
                            tncIntent = pm.getLaunchIntentForPackage(Constants.TNCPackageNames.RideAustin);
                            startActivity(tncIntent);
                            break;
                        case Constants.TNCList.ScoopMe:
                            tncIntent = pm.getLaunchIntentForPackage(Constants.TNCPackageNames.ScoopMe);
                            startActivity(tncIntent);
                            break;
                        case Constants.TNCList.Tride:
                            tncIntent = pm.getLaunchIntentForPackage(Constants.TNCPackageNames.Tride);
                            startActivity(tncIntent);
                            break;
                        case Constants.TNCList.Uber:
                            tncIntent = pm.getLaunchIntentForPackage(Constants.TNCPackageNames.Uber);
                            startActivity(tncIntent);
                            break;
                        case Constants.TNCList.WingZ:
                            tncIntent = pm.getLaunchIntentForPackage(Constants.TNCPackageNames.WingZ);
                            startActivity(tncIntent);
                            break;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(this, "Inside startTNCApps() EXCEPTION (" + ex.toString() + ")", Toast.LENGTH_LONG).show();
        }
    }

    private void killRideshareApps()
    {
        Toast.makeText(this, "Inside killRideshareApps()", Toast.LENGTH_SHORT).show();

        ActivityManager activityManager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);

        try
        {
            Integer selctedCount = myUser.TNCs.size();
            if(selctedCount > 0)
            {
                for (Integer i = 0; i < selctedCount; i++)
                {
                    TNC _tnc = myUser.TNCs.get(i);
                    showTNCButton(_tnc.Name);

                    switch(_tnc.Name)
                    {
                        case Constants.TNCList.FARE:
                            activityManager.killBackgroundProcesses(Constants.TNCPackageNames.FARE);
                            break;
                        case Constants.TNCList.Fasten:
                            activityManager.killBackgroundProcesses(Constants.TNCPackageNames.Fasten);
                            break;
                        case Constants.TNCList.GetMe:
                            activityManager.killBackgroundProcesses(Constants.TNCPackageNames.GetMe);
                            break;
                        case Constants.TNCList.InstaRyde:
                            activityManager.killBackgroundProcesses(Constants.TNCPackageNames.InstaRyde);
                            break;
                        case Constants.TNCList.Lyft:
                            activityManager.killBackgroundProcesses(Constants.TNCPackageNames.Lyft);
                            break;
                        case Constants.TNCList.RideAustin:
                            activityManager.killBackgroundProcesses(Constants.TNCPackageNames.RideAustin);
                            break;
                        case Constants.TNCList.ScoopMe:
                            activityManager.killBackgroundProcesses(Constants.TNCPackageNames.ScoopMe);
                            break;
                        case Constants.TNCList.Tride:
                            activityManager.killBackgroundProcesses(Constants.TNCPackageNames.Tride);
                            break;
                        case Constants.TNCList.Uber:
                            activityManager.killBackgroundProcesses(Constants.TNCPackageNames.Uber);
                            break;
                        case Constants.TNCList.WingZ:
                            activityManager.killBackgroundProcesses(Constants.TNCPackageNames.WingZ);
                            break;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(this, "Inside killRideshareApps EXCEPTION (" + ex.toString() + ")", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTNCButtonHilight(String tncName)
    {
        ImageView buttonLogo = null;

        switch(tncName)
        {
            case Constants.TNCList.FARE:
                ANIMATION_REPEAT_COUNT = 2;
                buttonLogo = (ImageView) findViewById(R.id.FARE_Logo);
                buttonLogo.setImageResource(R.drawable.logo_fare_on);
                buttonLogo.setVisibility(View.VISIBLE);
                break;
            case Constants.TNCList.Fasten:
                ANIMATION_REPEAT_COUNT = 3;
                buttonLogo = (ImageView) findViewById(R.id.Fasten_Logo);
                buttonLogo.setImageResource(R.drawable.logo_fasten_on);
                buttonLogo.setVisibility(View.VISIBLE);
                break;
            case Constants.TNCList.GetMe:
                ANIMATION_REPEAT_COUNT = 4;
                buttonLogo = (ImageView) findViewById(R.id.GetMe_Logo);
                buttonLogo.setImageResource(R.drawable.logo_getme_on);
                buttonLogo.setVisibility(View.VISIBLE);
                break;
            case Constants.TNCList.InstaRyde:
                ANIMATION_REPEAT_COUNT = 5;
                buttonLogo = (ImageView) findViewById(R.id.InstaRyde_Logo);
                buttonLogo.setImageResource(R.drawable.logo_instaryde_on);
                buttonLogo.setVisibility(View.VISIBLE);
                break;
            case Constants.TNCList.Lyft:
                ANIMATION_REPEAT_COUNT = 6;
                buttonLogo = (ImageView) findViewById(R.id.Lyft_Logo);
                buttonLogo.setImageResource(R.drawable.logo_lyft_on);
                buttonLogo.setVisibility(View.VISIBLE);
                break;
            case Constants.TNCList.RideAustin:
                ANIMATION_REPEAT_COUNT = 7;
                buttonLogo = (ImageView) findViewById(R.id.RideAustin_Logo);
                buttonLogo.setImageResource(R.drawable.logo_rideaustin_on);
                buttonLogo.setVisibility(View.VISIBLE);
                break;
            case Constants.TNCList.ScoopMe:
                ANIMATION_REPEAT_COUNT = 8;
                buttonLogo = (ImageView) findViewById(R.id.ScoopMe_Logo);
                buttonLogo.setImageResource(R.drawable.logo_scoopme_on);
                buttonLogo.setVisibility(View.VISIBLE);
                break;
            case Constants.TNCList.Tride:
                ANIMATION_REPEAT_COUNT = 10;
                buttonLogo = (ImageView) findViewById(R.id.Tride_Logo);
                buttonLogo.setImageResource(R.drawable.logo_tride_on);
                buttonLogo.setVisibility(View.VISIBLE);
                break;
            case Constants.TNCList.Uber:
                ANIMATION_REPEAT_COUNT = 11;
                buttonLogo = (ImageView) findViewById(R.id.Uber_Logo);
                buttonLogo.setImageResource(R.drawable.logo_uber_on);
                buttonLogo.setVisibility(View.VISIBLE);
                break;
            case Constants.TNCList.WingZ:
                ANIMATION_REPEAT_COUNT = 12;
                buttonLogo = (ImageView) findViewById(R.id.WingZ_Logo);
                buttonLogo.setImageResource(R.drawable.logo_wingz_on);
                buttonLogo.setVisibility(View.VISIBLE);
                break;
        }

        // Set button animation
        setButtonAnimation(buttonLogo, tncName, true);
    }

    private void setButtonAnimation(ImageView selectedLogo, String tncName, Boolean startAnimation)
    {
        final View buttonContainer = (View)selectedLogo.getParent();
        buttonContainer.setBackgroundResource(R.drawable.style_button_main);
        buttonContainer.setVisibility(View.VISIBLE);

        final ImageView buttonLogo = selectedLogo;

        final String selectedTNC = tncName;

        Animation mAnimation = new AlphaAnimation(1.0F, 0.25F);
        mAnimation.setDuration(ANIMATION_DURATION);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(ANIMATION_REPEAT_COUNT);

        mAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation arg0)
            {
                setRequestLabel();
                playNotificationSound();
            }
            @Override
            public void onAnimationRepeat(Animation arg0)
            {
                setRequestLabel();
                playNotificationSound();
            }
            @Override
            public void onAnimationEnd(Animation arg0)
            {
                String selectedPackageName = "";

                buttonContainer.setBackgroundResource(R.drawable.style_button_disabled);
                buttonContainer.setVisibility(View.GONE);

                ImageView buttonLogo = null;

                switch(selectedTNC)
                {
                    case Constants.TNCList.FARE:
                        selectedPackageName = Constants.TNCPackageNames.FARE;
                        buttonLogo = (ImageView) findViewById(R.id.FARE_Logo);
                        buttonLogo.setImageResource(R.drawable.logo_fare_off);
                        buttonLogo.setVisibility(View.GONE);
                        break;
                    case Constants.TNCList.Fasten:
                        selectedPackageName = Constants.TNCPackageNames.Fasten;
                        buttonLogo = (ImageView) findViewById(R.id.Fasten_Logo);
                        buttonLogo.setImageResource(R.drawable.logo_fasten_off);
                        buttonLogo.setVisibility(View.GONE);
                        break;
                    case Constants.TNCList.GetMe:
                        selectedPackageName = Constants.TNCPackageNames.GetMe;
                        buttonLogo = (ImageView) findViewById(R.id.GetMe_Logo);
                        buttonLogo.setImageResource(R.drawable.logo_getme_off);
                        buttonLogo.setVisibility(View.GONE);
                        break;
                    case Constants.TNCList.InstaRyde:
                        selectedPackageName = Constants.TNCPackageNames.InstaRyde;
                        buttonLogo = (ImageView) findViewById(R.id.InstaRyde_Logo);
                        buttonLogo.setImageResource(R.drawable.logo_instaryde_off);
                        buttonLogo.setVisibility(View.GONE);
                        break;
                    case Constants.TNCList.Lyft:
                        selectedPackageName = Constants.TNCPackageNames.Lyft;
                        buttonLogo = (ImageView) findViewById(R.id.Lyft_Logo);
                        buttonLogo.setImageResource(R.drawable.logo_lyft_off);
                        buttonLogo.setVisibility(View.GONE);
                        break;
                    case Constants.TNCList.RideAustin:
                        selectedPackageName = Constants.TNCPackageNames.RideAustin;
                        buttonLogo = (ImageView) findViewById(R.id.RideAustin_Logo);
                        buttonLogo.setImageResource(R.drawable.logo_rideaustin_off);
                        buttonLogo.setVisibility(View.GONE);
                        break;
                    case Constants.TNCList.ScoopMe:
                        selectedPackageName = Constants.TNCPackageNames.ScoopMe;
                        buttonLogo = (ImageView) findViewById(R.id.ScoopMe_Logo);
                        buttonLogo.setImageResource(R.drawable.logo_scoopme_off);
                        buttonLogo.setVisibility(View.GONE);
                        break;
                    case Constants.TNCList.Tride:
                        selectedPackageName = Constants.TNCPackageNames.Tride;
                        buttonLogo = (ImageView) findViewById(R.id.Tride_Logo);
                        buttonLogo.setImageResource(R.drawable.logo_tride_off);
                        buttonLogo.setVisibility(View.GONE);
                        break;
                    case Constants.TNCList.Uber:
                        selectedPackageName = Constants.TNCPackageNames.Uber;
                        buttonLogo = (ImageView) findViewById(R.id.Uber_Logo);
                        buttonLogo.setImageResource(R.drawable.logo_uber_off);
                        buttonLogo.setVisibility(View.GONE);
                        break;
                    case Constants.TNCList.WingZ:
                        selectedPackageName = Constants.TNCPackageNames.WingZ;
                        buttonLogo = (ImageView) findViewById(R.id.WingZ_Logo);
                        buttonLogo.setImageResource(R.drawable.logo_wingz_off);
                        buttonLogo.setVisibility(View.GONE);
                        break;
                }

                // Remove the current request
                List<Notification> currentRequests = TNCRideRequests.removeRequest(TNCControlActivity.this, selectedPackageName);

                if(currentRequests.size() < 1) {
                    setUserTNCButtons();
                }

                setRequestLabel();
            }
        });

        if(startAnimation)
            buttonContainer.startAnimation(mAnimation);
        else
            buttonContainer.clearAnimation();
    }

    public void playNotificationSound()
    {
        Boolean isPlaying = notificationPlayer.isPlaying();
        if(!isPlaying)
            notificationPlayer.play();
        else
        {
            notificationPlayer.stop();
            notificationPlayer.play();
        }
    }

    private void setRequestLabel()
    {
        requestLabel = (TextView) findViewById(R.id.lblMessage);
        requestLabel.setText("Waiting for requests...");
        requestLabel.setTextColor(Color.parseColor("#808080"));

        Animation mAnimation = new AlphaAnimation(1.0F, 0.0F);
        mAnimation.setDuration(2000);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(Animation.INFINITE);

        requestLabel.setAnimation(mAnimation);

        List<Notification> currentRequests = TNCRideRequests.getPendingRequests(TNCControlActivity.this);

        if(currentRequests != null)
        {
            if(currentRequests.size() > 0)
            {
                requestLabel.clearAnimation();

                Integer pendingRequestCount = currentRequests.size();
                if (pendingRequestCount > 0)
                {
                    requestLabel.setTextColor(Color.parseColor("#eb416b"));

                    if (pendingRequestCount == 1)
                        requestLabel.setText("1 ride request pending...");
                    else
                        requestLabel.setText(pendingRequestCount + " ride requests pending...");
                }
            }
        }
    }

    private void hideAllButtons()
    {
        notificationManager.cancelAll();

        LinearLayout tncContainer = (LinearLayout) findViewById(R.id.tncContainer);
        Integer tncItems = tncContainer.getChildCount();

        for(int i = 0; i < tncItems; i++)
        {
            RelativeLayout buttonContainer = (RelativeLayout) tncContainer.getChildAt(i);

            // Set buttton background
            buttonContainer.setBackgroundResource(R.drawable.style_button_disabled);
            buttonContainer.setVisibility(View.GONE);
            buttonContainer.clearAnimation();

            // Set button logos
            ImageView buttonLogo = (ImageView) findViewById(R.id.FARE_Logo);
            buttonLogo.setImageResource(R.drawable.logo_fare_off);
            buttonLogo.setVisibility(View.GONE);

            buttonLogo = (ImageView) findViewById(R.id.Fasten_Logo);
            buttonLogo.setImageResource(R.drawable.logo_fasten_off);
            buttonLogo.setVisibility(View.GONE);

            buttonLogo = (ImageView) findViewById(R.id.GetMe_Logo);
            buttonLogo.setImageResource(R.drawable.logo_getme_off);
            buttonLogo.setVisibility(View.GONE);

            buttonLogo = (ImageView) findViewById(R.id.InstaRyde_Logo);
            buttonLogo.setImageResource(R.drawable.logo_instaryde_off);
            buttonLogo.setVisibility(View.GONE);

            buttonLogo = (ImageView) findViewById(R.id.Lyft_Logo);
            buttonLogo.setImageResource(R.drawable.logo_lyft_off);
            buttonLogo.setVisibility(View.GONE);

            buttonLogo = (ImageView) findViewById(R.id.RideAustin_Logo);
            buttonLogo.setImageResource(R.drawable.logo_rideaustin_off);
            buttonLogo.setVisibility(View.GONE);

            buttonLogo = (ImageView) findViewById(R.id.ScoopMe_Logo);
            buttonLogo.setImageResource(R.drawable.logo_scoopme_off);
            buttonLogo.setVisibility(View.GONE);

            buttonLogo = (ImageView) findViewById(R.id.Tride_Logo);
            buttonLogo.setImageResource(R.drawable.logo_tride_off);
            buttonLogo.setVisibility(View.GONE);

            buttonLogo = (ImageView) findViewById(R.id.Uber_Logo);
            buttonLogo.setImageResource(R.drawable.logo_uber_off);
            buttonLogo.setVisibility(View.GONE);

            buttonLogo = (ImageView) findViewById(R.id.WingZ_Logo);
            buttonLogo.setImageResource(R.drawable.logo_wingz_off);
            buttonLogo.setVisibility(View.GONE);
        }
    }

    private void selectTNCButton(String tncName, String packgeName)
    {
        // Stop sound
        notificationPlayer.stop();

        // Clear buttons
        setUserTNCButtons();

        // Kill running rideshare apps
        //killRideshareApps();

        // Activate the TNC package and terminate the rest. Will reload apps onResume.
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packgeName);
        startActivity(intent);

        //Toast.makeText(this, "Activate TNC package (" + packgeName + ") for " + tncName, Toast.LENGTH_SHORT).show();
    }

    private void showTNCButton(String tncName)
    {
        View buttonContainer = null;
        ImageView buttonLogo = null;

        switch(tncName)
        {
            case Constants.TNCList.FARE:
                buttonContainer = findViewById(R.id.FARE_Container);
                buttonLogo = (ImageView) findViewById(R.id.FARE_Logo);
                buttonLogo.setImageResource(R.drawable.logo_fare_off);
                buttonLogo.setVisibility(View.VISIBLE);
                // Stop button animation
                setButtonAnimation(buttonLogo, tncName, false);
                break;
            case Constants.TNCList.Fasten:
                buttonContainer = findViewById(R.id.Fasten_Container);
                buttonLogo = (ImageView) findViewById(R.id.Fasten_Logo);
                buttonLogo.setImageResource(R.drawable.logo_fasten_off);
                buttonLogo.setVisibility(View.VISIBLE);
                // Stop button animation
                setButtonAnimation(buttonLogo, tncName, false);
                break;
            case Constants.TNCList.GetMe:
                buttonContainer = findViewById(R.id.GetMe_Container);
                buttonLogo = (ImageView) findViewById(R.id.GetMe_Logo);
                buttonLogo.setImageResource(R.drawable.logo_getme_off);
                buttonLogo.setVisibility(View.VISIBLE);
                // Stop button animation
                setButtonAnimation(buttonLogo, tncName, false);
                break;
            case Constants.TNCList.InstaRyde:
                buttonContainer = findViewById(R.id.InstaRyde_Container);
                buttonLogo = (ImageView) findViewById(R.id.InstaRyde_Logo);
                buttonLogo.setImageResource(R.drawable.logo_instaryde_off);
                buttonLogo.setVisibility(View.VISIBLE);
                // Stop button animation
                setButtonAnimation(buttonLogo, tncName, false);
                break;
            case Constants.TNCList.Lyft:
                buttonContainer = findViewById(R.id.Lyft_Container);
                buttonLogo = (ImageView) findViewById(R.id.Lyft_Logo);
                buttonLogo.setImageResource(R.drawable.logo_lyft_off);
                buttonLogo.setVisibility(View.VISIBLE);
                // Stop button animation
                setButtonAnimation(buttonLogo, tncName, false);
                break;
            case Constants.TNCList.RideAustin:
                buttonContainer = findViewById(R.id.RideAustin_Container);
                buttonLogo = (ImageView) findViewById(R.id.RideAustin_Logo);
                buttonLogo.setImageResource(R.drawable.logo_rideaustin_off);
                buttonLogo.setVisibility(View.VISIBLE);
                // Stop button animation
                setButtonAnimation(buttonLogo, tncName, false);
                break;
            case Constants.TNCList.ScoopMe:
                buttonContainer = findViewById(R.id.ScoopMe_Container);
                buttonLogo = (ImageView) findViewById(R.id.ScoopMe_Logo);
                buttonLogo.setImageResource(R.drawable.logo_scoopme_off);
                buttonLogo.setVisibility(View.VISIBLE);
                // Stop button animation
                setButtonAnimation(buttonLogo, tncName, false);
                break;
            case Constants.TNCList.Tride:
                buttonContainer = findViewById(R.id.Tride_Container);
                buttonLogo = (ImageView) findViewById(R.id.Tride_Logo);
                buttonLogo.setImageResource(R.drawable.logo_tride_off);
                buttonLogo.setVisibility(View.VISIBLE);
                // Stop button animation
                setButtonAnimation(buttonLogo, tncName, false);
                break;
            case Constants.TNCList.Uber:
                buttonContainer = findViewById(R.id.Uber_Container);
                buttonLogo = (ImageView) findViewById(R.id.Uber_Logo);
                buttonLogo.setImageResource(R.drawable.logo_uber_off);
                buttonLogo.setVisibility(View.VISIBLE);
                // Stop button animation
                setButtonAnimation(buttonLogo, tncName, false);
                break;
            case Constants.TNCList.WingZ:
                buttonContainer = findViewById(R.id.WingZ_Container);
                buttonLogo = (ImageView) findViewById(R.id.WingZ_Logo);
                buttonLogo.setImageResource(R.drawable.logo_wingz_off);
                buttonLogo.setVisibility(View.VISIBLE);
                // Stop button animation
                setButtonAnimation(buttonLogo, tncName, false);
                break;
        }

        buttonContainer.setVisibility(View.VISIBLE);
        buttonContainer.setBackgroundResource(R.drawable.style_button_disabled);
    }

    private void getExtras()
    {
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null)
        {
            Notification rideRequest = new Notification();

            rideRequest.PackageName = bundle.getString("packageName");
            rideRequest.NotificationTicker = bundle.getString("notificationTicker");
            rideRequest.NotificationTitle = bundle.getString("notificationTitle");
            rideRequest.NotificationText = bundle.getString("notificationText");

            TNCRideRequests.addRequest(this, rideRequest);

            setRequestDisplay();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Notification rideRequest = new Notification();

            rideRequest.PackageName = intent.getStringExtra("packageName");
            rideRequest.NotificationTicker = intent.getStringExtra("notificationTicker");
            rideRequest.NotificationTitle = intent.getStringExtra("notificationTitle");
            rideRequest.NotificationText = intent.getStringExtra("notificationText");
        }
    };

    private void filterMenus()
    {
        try {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu myMenu = navigationView.getMenu();

            for (int i = 0; i < myUser.Roles.size(); i++)
            {
                UserRole _role = myUser.Roles.get(i);

                if(_role.Name.equalsIgnoreCase("Site Administrator"))
                    myMenu.setGroupVisible(R.id.nav_menu_admin, true);
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
    }

    private void populateMyUser()
    {
        try
        {
            SharedPreferences prefs = this.getSharedPreferences(Constants.User.Profile, Context.MODE_PRIVATE);
            String serializedUser = prefs.getString(Constants.User.Profile, null);

            if (serializedUser != null)
            {
                JSONObject jsonUser = new JSONObject(serializedUser);
                myUser = new User(jsonUser);

                String wtf = "";
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
    }

    private void userLogout()
    {
        stopLocationReporting();
        Utilities.clearPreferencesAndLogOff(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        stopLocationReporting();

        // Right menu items
        int id = item.getItemId();

        if (id == R.id.action_settings_personal_info)
        {
            startActivity(new Intent(this, SettingsPersonalActivity.class));
        }
        else if (id == R.id.action_settings_profile_info)
        {
            startActivity(new Intent(this, SettingsProfileActivity.class));
        }
        else if (id == R.id.action_settings_tnc_info)
        {
            startActivity(new Intent(this, SettingsTNCActivity.class));
        }
        else if (id == R.id.action_settings_subscription_info)
        {
            startActivity(new Intent(this, SettingsSubscriptionActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        stopLocationReporting();

        // Left menu items
        int id = item.getItemId();

        if (id == R.id.nav_logout)
        {
            userLogout();
        }
        else if (id == R.id.nav_notification_test)
        {
            startActivity(new Intent(this, NotificationTest.class));
        }
        else if (id == R.id.nav_switchboard)
        {
            startActivity(new Intent(this, TNCControlActivity.class));
        }
        else if (id == R.id.nav_nightmode)
        {
            startActivity(new Intent(this, SettingsNightModeActivity.class));
        }
        else if (id == R.id.nav_about)
        {
            startActivity(new Intent(this, AboutActivity.class));
        }
        else if (id == R.id.nav_contact)
        {
            startActivity(new Intent(this, ContactActivity.class));
        }

        else if (id == R.id.nav_dashboard)
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("http://driveswitch.com/dashboard/?sess=" + myUser._id.toString()));
            startActivity(intent);
        }
        else if (id == R.id.nav_system)
        {
            startActivity(new Intent(this, AdministrationActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
