package com.driveswitch;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.driveswitch.driveswitch.R;

public class SecurityForgotPasswordActivity extends AppCompatActivity {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_forgot_password);

        // Set logo in ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo_driveswitch);

        mFormView = findViewById(R.id.mainContainer);
        mProgressView = findViewById(R.id.progress_bar);

        // Set Next button action
        Button mNextButton = (Button) findViewById(R.id.btnNext);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navSendPassword();
            }
        });

        // Set Cancel button action
        Button mCancelButton = (Button) findViewById(R.id.btnCancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRegistration();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();

                Utilities.logUserLocationAndValidateSubscription(SecurityForgotPasswordActivity.this, myUser._id, Constants.EmptyObjectId, userLatitude, userLongitude);
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
    }

    private void navSendPassword()
    {
        showProgress(true);

        stopLocationReporting();

        // Text password to user
        startActivity(new Intent(this, MainActivity.class));
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

    private void cancelRegistration()
    {
        startActivity(new Intent(this, MainActivity.class));
    }
}
