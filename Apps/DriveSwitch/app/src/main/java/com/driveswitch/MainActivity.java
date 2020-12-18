package com.driveswitch;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.v7.app.ActionBar;

import android.content.Intent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.driveswitch.driveswitch.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private User myUser;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Double userLatitude = 0.00;
    private Double userLongitude = 0.00;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    private Boolean emailIsValid = false;
    private Boolean passwordIsValid = false;

    private TextView loginStatus;

    private View mProgressView;
    private View mFormView;

    private String loginErrorType = "Email";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Debugging
        //Utilities.clearUserPreferences(this);

        // Check to see if logged in already
        checkIfLoggedIn();

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.inpEmail);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.inpPassword);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.btnLogin || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // Set logo in ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo_driveswitch);

        Button mEmailSignInButton = (Button) findViewById(R.id.btnLogin);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mFormView = findViewById(R.id.user_form);
        mProgressView = findViewById(R.id.progress_bar);

        // Set forgot password button action
        Button mForgotPasswordButton = (Button) findViewById(R.id.btnForgotPassword);
        mForgotPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                navToForgotPassword();
            }
        });

        // Set register button action
        Button mRegisterButton = (Button) findViewById(R.id.btnregister);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                navToPersonalInfo();
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
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

    private void checkIfLoggedIn()
    {
        // Check prefs
        SharedPreferences prefs = this.getSharedPreferences(Constants.User.Profile, Context.MODE_PRIVATE);
        String serializedUser = prefs.getString(Constants.User.Profile, null);

        if(serializedUser != null)
        {
            // Read response into Json object from preferences
            JSONObject jsonUser = null;

            try
            {
                jsonUser = new JSONObject(serializedUser);

                //Utilities.testUserUpdate(this, jsonUser.getString("_id"), jsonUser);

                if(jsonUser.getBoolean("IsLoggedIn")) {
                    startActivity(new Intent(this, TNCControlActivity.class));
                    //startActivity(new Intent(this, NotificationTest.class));
                    //startActivity(new Intent(this, RegisterTNCActivity.class));
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void navToForgotPassword()
    {
        startActivity(new Intent(this, SecurityForgotPasswordActivity.class));
    }

    private void navToPersonalInfo()
    {
        startActivity(new Intent(this, RegisterPersonalInfoActivity.class));
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("A valid email address is required");
            mEmailView.requestFocus();
            emailIsValid = false;
            return;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("Invalid email address format");
            mEmailView.requestFocus();
            emailIsValid = false;
            return;
        }
        else {
            emailIsValid = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password))
        {
            mPasswordView.setError("Please enter a password");
            mPasswordView.requestFocus();
            passwordIsValid = false;
            return;
        }
        else if (!isPasswordValid(password))
        {
            mPasswordView.setError("Password must be at least (" + getResources().getString(R.string.app_security_minimum_password_length) + ") characters");
            mPasswordView.requestFocus();
            passwordIsValid = false;
            return;
        }
        else {
            passwordIsValid = true;
        }


        if(emailIsValid && passwordIsValid) {

            showProgress(true);

            LoginResponse _loginResponse = Utilities.loginUser(this, email, password, userLatitude.toString(), userLongitude.toString());

            if (_loginResponse.IsLoggedIn)
            {
                startActivity(new Intent(this, TNCControlActivity.class));
            } else {
                showProgress(false);

                mEmailView.setError(null);
                mPasswordView.setError(null);

                // Show login failure reason
                switch (_loginResponse.ResponseId) {
                    case 0: // Email not registered
                        mEmailView.setError(_loginResponse.FailureReason);
                        mEmailView.requestFocus();
                        return;
                    case 1: // Bad password
                        mPasswordView.setError(_loginResponse.FailureReason);
                        mPasswordView.requestFocus();
                        return;
                }
            }
        }
    }

    private boolean isEmailValid(String email) {
        // Validate with RegEx
        Boolean emailIsValid = Utilities.validateEmailAdress(email);

        return emailIsValid;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= Integer.parseInt(getResources().getString(R.string.app_security_minimum_password_length));
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        //mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}

