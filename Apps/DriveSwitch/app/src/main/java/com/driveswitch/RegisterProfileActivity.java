package com.driveswitch;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.driveswitch.driveswitch.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterProfileActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_register_profile);

        // Set logo in ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo_driveswitch);

        mFormView = findViewById(R.id.mainContainer);
        mProgressView = findViewById(R.id.progress_bar);

        // Set Phone Number masking
        EditText mEditPhoneNumber = (EditText) findViewById(R.id.inpMobileNumber);
        mEditPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Set Gender listbox
        Spinner staticSpinner = (Spinner) findViewById(R.id.inpGender);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this, R.array.list_gender, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        staticSpinner.setSelection(0);

        // Set Next button action
        Button mNextButton = (Button) findViewById(R.id.btnNext);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileSettings();
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

                //Utilities.logUserLocation(RegisterProfileActivity.this, myUser._id, Constants.EmptyObjectId, userLatitude, userLongitude);
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

        populateMyUser();
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

    private void saveProfileSettings()
    {
        // Validate inputs
        TextView userMobileNumber = (TextView)findViewById(R.id.inpMobileNumber);
        Spinner userGender = (Spinner) findViewById(R.id.inpGender);
        TextView userZipCode = (TextView) findViewById(R.id.inpZipCode);

        userMobileNumber.setError(null);
        //userGender.setError(null);
        userZipCode.setError(null);

        Boolean phoneValidation = Utilities.validateMobilePhone(userMobileNumber.getText().toString());
        if(!phoneValidation)
        {
            userMobileNumber.setError("Enter a valid mobile phone number");
            userMobileNumber.requestFocus();
            return;
        }

        Integer selectedGender = userGender.getSelectedItemPosition();
        if(selectedGender == 0)
        {
            Toast.makeText(this,"Please select your Gender", Toast.LENGTH_SHORT).show();
            userGender.requestFocus();
            return;
        }

        Boolean zipCodeValidation = Utilities.validateZipCode(userZipCode.getText().toString());
        if(!zipCodeValidation)
        {
            userZipCode.setError("Enter a valid Zip Code");
            userZipCode.requestFocus();
            return;
        }

        showProgress(true);

        SharedPreferences prefs = this.getSharedPreferences(Constants.User.Profile, Context.MODE_PRIVATE);
        String serializedUser = prefs.getString(Constants.User.Profile, null);

        if(serializedUser != null)
        {
            // Read response into Json object from preferences
            JSONObject jsonUser = null;

            try
            {
                jsonUser = new JSONObject(serializedUser);

                User myUser = new User(jsonUser);

                String[] phoneElements = userMobileNumber.getText().toString().split(" ");

                // Parse phone elements with another split
                String[] localElements = phoneElements[1].split("-");

                myUser.Contact.Phone.AreaCode = Integer.parseInt(phoneElements[0].replace("(","").replace(")",""));
                myUser.Contact.Phone.Exchange = localElements[0];
                myUser.Contact.Phone.Number = localElements[1];

                myUser.Gender = selectedGender;
                myUser.Contact.Address.ZipCode = userZipCode.getText().toString().trim();

                Gson gson = new Gson();
                String jsonUpdatedUser = gson.toJson(myUser);

                JSONArray updatedProfile = Utilities.updateUser(this, jsonUser.getString("_id"), jsonUpdatedUser);

                showProgress(false);

                if(updatedProfile != null)
                {
                    // Check to see if duplicate phone
                    String testUserString = updatedProfile.toString();

                    Boolean isPhoneAlreadyRegistered = testUserString.toLowerCase().contains("already registered");
                    if(isPhoneAlreadyRegistered)
                    {
                        // Show user email is already registered
                        userMobileNumber.setError("Phone number (" + myUser.Contact.Phone.AreaCode + ") " + myUser.Contact.Phone.Exchange + "-" + myUser.Contact.Phone.Number + " already registered!");
                        userMobileNumber.requestFocus();
                        return;
                    }
                    else
                    {
                        stopLocationReporting();
                        startActivity(new Intent(this, RegisterTNCActivity.class));
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
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
