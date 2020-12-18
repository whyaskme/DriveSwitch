package com.driveswitch;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
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

import android.widget.Button;

import android.support.v7.app.ActionBar;
import android.widget.TextView;

import org.json.JSONObject;

public class AboutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Double userLatitude = 0.00;
    private Double userLongitude = 0.00;

    private View mProgressView;
    private View mFormView;

    User myUser = null;

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
    protected void onCreate(Bundle savedInstanceState) {

        populateMyUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
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

        mFormView = findViewById(R.id.mainContainer);
        mProgressView = findViewById(R.id.progress_bar);

        // Set Save button action
        Button mResume = (Button) findViewById(R.id.btnResume);
        mResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewJoeResume();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();

                Utilities.logUserLocationAndValidateSubscription(AboutActivity.this, myUser._id, Constants.EmptyObjectId, userLatitude, userLongitude);
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

        setVersion();

        setAboutText();

        filterMenus();
    }

    private void setVersion()
    {
        try {
            TextView lblVersion = (TextView) findViewById(R.id.lblVersion);

            String versionNumber = "Version 1.2.3";

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;

            lblVersion.setText("Version " + version);
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
    }

    private void setAboutText()
    {
        String aboutJoeContent = "";
        aboutJoeContent += "<b>Joseph J. Baranauskas</b>";
        aboutJoeContent += "<br />CEO - CTO - Driver</i>";
        aboutJoeContent += "<br /><br /><i>Alcohol & calculus don't</i>";
        aboutJoeContent += "<br /><i>mix. Never drink & derive!</i>";

        TextView aboutJosephBaranauskas = (TextView) findViewById(R.id.textJosephBaranauskas);
        aboutJosephBaranauskas.setText(Html.fromHtml(aboutJoeContent));

        String aboutContent = "";
        aboutContent += "<p>My name is Joseph J. Baranauskas and I am the founder of DriveSwitch. Most of my friends just call me Joe and this includes you too my friend!</p>";
        aboutContent += "<p>DriveSwitch is a consortium of well seasoned, professional rideshare drivers located in Austin, TX.</p>";
        aboutContent += "<p>On May 9, 2016, Uber and Lyft pulled out of Austin after the failure of a much heated public vote called Proposition 1.</p>";
        aboutContent += "<p>The passage of Proposition 1 would have overturned the City of Austin’s onerous rideshare requirements that both Uber and Lyft vehemently opposed.</p>";
        aboutContent += "<p>Upon their expected exodus from Austin after the Prop 1 failure, there was a void in Austin for affordable rideshare options as opposed to public transportation and unseemly taxi alternatives.</p>";
        aboutContent += "<p>This void was quickly filled by approx. 15 new rideshare companies. Some came from out-of-state, while others were born here.</p>";
        aboutContent += "<p>Given the market fragmentation caused by Uber and Lyft's departures, numerous drivers (<i>myself included</i>) found ourselves juggling multiple ridesharing apps in an effort to recover from Uber and Lyft’s pullout.</p>";
        aboutContent += "<p>Collectively, we quickly realized the challenges faced while managing multiple rideshare apps when driving.</p>";
        aboutContent += "<p>While experiencing the issues firsthand, we recognized the need for a switchboard, if you will; and well, the DriveSwitch concept was conceived.</p>";
        aboutContent += "<p>We sincerely thank you for installing the DriveSwitch app and hope you find it makes life easier as a multi-tasking, dedicated rideshare pilot. Rock on and go make that much deserved chedda!</p>";

        TextView aboutText = (TextView) findViewById(R.id.textAboutView);
        aboutText.setText(Html.fromHtml(aboutContent));
    }

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

    private void viewJoeResume()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://baranauskas.com/resume"));
        startActivity(intent);
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
