package com.driveswitch;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.support.v7.app.ActionBar;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SettingsSubscriptionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Double userLatitude = 0.00;
    private Double userLongitude = 0.00;

    private View mProgressView;
    private View mFormView;

    User myUser = null;

    private Boolean isInitialized = false;

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

    private Integer selectedRenewalPeriod = 0;

    private String userId = "";

    public TextView subscriptionHistoryLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        populateMyUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_subscription);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set logo in ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo_driveswitch);

        mFormView = findViewById(R.id.mainContainer);
        mProgressView = findViewById(R.id.progress_bar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set Renew button action
        Button mRenewPeriodButton = (Button) findViewById(R.id.btnRenewalPeriod);
        mRenewPeriodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRewalPeriod();
            }
        });

        // Set Renew button action
        Button mRenewButton = (Button) findViewById(R.id.btnRenewSubscription);
        mRenewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renewSubscription();
            }
        });

        // Set Continue button action
        Button mContinueButton = (Button) findViewById(R.id.btnContinue);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueUse();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();

                Utilities.logUserLocationAndValidateSubscription(SettingsSubscriptionActivity.this, myUser._id, Constants.EmptyObjectId, userLatitude, userLongitude);
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
        Utilities.logUserLocationAndValidateSubscription(this, myUser._id, Constants.EmptyObjectId, userLatitude, userLongitude);
        populateMyUser();
        validateSubscription();

        // Process extras if referred from another activity
        getExtras();

        getRenewalPeriods();

        subscriptionHistoryLabel = (TextView) findViewById(R.id.textSubscriptionHistory);
        subscriptionHistoryLabel.setVisibility(View.GONE);

        filterMenus();
    }

    private void validateSubscription()
    {
        try
        {
            setSubscriptionLabel();

            if (myUser.Expired)
            {
                stopLocationReporting();
                setFormView("Period");
            }
            else
            {
                setFormView("History");
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
    }

    private void getExtras()
    {
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null)
        {
            myUser.Expired = bundle.getBoolean("Expired");
            myUser.ExpireDate = bundle.getString("ExpireDate");

            validateSubscription();
        }
    }

    public void setSubscriptionLabel()
    {
        String expireLabel = "Account expires ";

        String formattedDate = Utilities.getDateFromUtc(myUser.ExpireDate);

        Date expireDate = new Date(formattedDate);
        Date nowDate = new Date();

        TextView userExpires = (TextView) findViewById(R.id.textExpires);
        if (expireDate.compareTo(nowDate) < 0) // Account expired!
        {
            expireLabel = "Account expired ";
            userExpires.setTextColor(Color.parseColor("#eb416b"));
            userExpires.setTypeface(null, Typeface.BOLD);
        }

        userExpires.setText(expireLabel + formattedDate);
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
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
    }

    private void getRenewalPeriods()
    {
        final Spinner periodList = (Spinner) findViewById(R.id.inpRenewalPeriod);

        List<String> myArraySpinner = new ArrayList<String>();
        myArraySpinner.add("Available subscriptions");

        // Get renewal periods from webapi
        JSONArray renewalPeriods = Utilities.getSubscriptionPeriods();

        for(Integer i = 0; i < renewalPeriods.length(); i++)
        {
            JSONObject currentPeriod = null;
            try
            {
                currentPeriod = renewalPeriods.getJSONObject(i);

                String periodLength = currentPeriod.getString("Name");
                String periodAmount = currentPeriod.getString("Amount");

                myArraySpinner.add(periodLength + " = $" + periodAmount + " ");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myArraySpinner);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        periodList.setAdapter(spinnerArrayAdapter);

        periodList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                selectedRenewalPeriod = periodList.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
            }
        });
    }

    private void selectRewalPeriod()
    {
        // Show payment process
        if(selectedRenewalPeriod > 0)
            setFormView("Payment");
        else
            Toast.makeText(SettingsSubscriptionActivity.this,"Please select an available subscription!", Toast.LENGTH_SHORT).show();
    }

    private void continueUse()
    {
        stopLocationReporting();
        startActivity(new Intent(this, TNCControlActivity.class));
    }

    private void showSubscriptionHistoryData()
    {
        subscriptionHistoryLabel = (TextView) findViewById(R.id.textSubscriptionHistory);

        if (myUser != null)
        {
            try
            {
                if(myUser.Transactions != null)
                {
                    if(!isInitialized) {
                        subscriptionHistoryLabel.setVisibility(View.VISIBLE);

                        LinearLayout historyContainer = (LinearLayout) findViewById(R.id.user_history_data);

                        Integer transactionNumber = 0;

                        for (Iterator<Transaction> i = myUser.Transactions.iterator(); i.hasNext(); ) {
                            transactionNumber++;

                            Transaction _transaction = i.next();

                            String transDate = Utilities.formatSubscriptionDate(_transaction.Date.toString());
                            String transType = _transaction.Type;
                            String transAmount = _transaction.Amount;

                            LinearLayout historyItemContainer = new LinearLayout(this);

                            historyItemContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            historyContainer.addView(historyItemContainer);

                            TextView tv1 = new TextView(this);
                            tv1.setText(transactionNumber + " - " + transDate + " - " + transType + " ($" + transAmount + ")");

                            historyItemContainer.addView(tv1);
                        }
                        isInitialized = true;
                    }
                }
                else
                {
                    subscriptionHistoryLabel.setVisibility(View.GONE);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setAccountInfo()
    {
        TextView cardHolderName = (TextView) findViewById(R.id.inpNameOnCreditCard);
        TextView cardNumber = (TextView) findViewById(R.id.inpCreditCardNumber);
        TextView cardZipcode = (TextView) findViewById(R.id.inpBillingZipcode);
        TextView cardExpires = (TextView) findViewById(R.id.inpCreditCardExpires);
        TextView cardCCVCode = (TextView) findViewById(R.id.inpCreditCardCVVNumber);

        cardHolderName.setText(myUser.FirstName + " " + myUser.LastName);
        cardNumber.setText("4736 2221 3125 0824");

        String userZipcode = myUser.Contact.Address.ZipCode;
        cardZipcode.setText(userZipcode);

        cardExpires.setText("07/19");
        cardCCVCode.setText("670");
    }

    private void renewSubscription()
    {
        boolean cancel = false;
        View focusView = null;

        TextView cardHolderName = (TextView) findViewById(R.id.inpNameOnCreditCard);
        TextView cardNumber = (TextView) findViewById(R.id.inpCreditCardNumber);
        TextView cardZipcode = (TextView) findViewById(R.id.inpBillingZipcode);
        TextView cardExpires = (TextView) findViewById(R.id.inpCreditCardExpires);
        TextView cardCVVCode = (TextView) findViewById(R.id.inpCreditCardCVVNumber);

        cardHolderName.setError(null);
        cardNumber.setError(null);
        cardZipcode.setError(null);
        cardExpires.setError(null);
        cardCVVCode.setError(null);

        // Validate cardHolderName
        if(cardHolderName.getText().length() < 2)
        {
            cardHolderName.setError("Card holder name must be 2 or more characters");
            focusView = cardHolderName;
            focusView.requestFocus();
            cancel = true;
            return;
        }

        // Validate cardNumber
        if(cardNumber.getText().length() < 19)
        {
            cardNumber.setError("Card number must be 19 characters");
            focusView = cardNumber;
            focusView.requestFocus();
            cancel = true;
            return;
        }

        // Validate cardZipcode
        if(cardZipcode.getText().length() < 5)
        {
            cardZipcode.setError("Billing zipcode must be 5 characters");
            focusView = cardZipcode;
            focusView.requestFocus();
            cancel = true;
            return;
        }

        // Validate cardExpires
        if(cardExpires.getText().length() < 2)
        {
            cardExpires.setError("Card number must be 19 characters");
            focusView = cardExpires;
            focusView.requestFocus();
            cancel = true;
            return;
        }

        // Validate cardCVVCode
        if(cardCVVCode.getText().length() < 3)
        {
            cardCVVCode.setError("Card security code must be 3 or more characters");
            focusView = cardCVVCode;
            focusView.requestFocus();
            cancel = true;
            return;
        }

        try
        {
            showProgress(true);

            Transaction userTransaction = new Transaction();

            userTransaction.UserId = myUser._id;
            userTransaction.RenewalPeriod = selectedRenewalPeriod;
            userTransaction.SavePaymentMethod = true;
            userTransaction.Latitude = userLatitude.toString();
            userTransaction.Longitude = userLongitude.toString();

            // Set payment card info
            userTransaction.PaymentCard.CardTypeId = "";
            userTransaction.PaymentCard.CardTypeName = "";
            userTransaction.PaymentCard.FullName = cardHolderName.getText().toString().replace(" ", "%20");
            userTransaction.PaymentCard.Number = Utilities.normalizeCardNumber(cardNumber.getText().toString());
            userTransaction.PaymentCard.Expires = cardExpires.getText().toString();
            userTransaction.PaymentCard.Zipcode = cardZipcode.getText().toString();
            userTransaction.PaymentCard.CVVCode = cardCVVCode.getText().toString();

            User updatedUser = Utilities.renewSubscription(this, userTransaction);
            if(updatedUser != null)
            {
                myUser = updatedUser;

                setSubscriptionLabel();
                setFormView("History");

                Toast.makeText(this, "Thank you for renewing your subscription!", Toast.LENGTH_SHORT).show();
            }

            showProgress(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String wtf = "";
    }

    private void setFormView(String formToShow)
    {
        View periodView = this.findViewById(R.id.user_period_selection_form);
        View paymentView = this.findViewById(R.id.user_subscription_form);
        View historyView = this.findViewById(R.id.user_history_form);

        periodView.setVisibility(View.GONE);
        paymentView.setVisibility(View.GONE);
        historyView.setVisibility(View.GONE);

        switch(formToShow)
        {
            case "Period":
                periodView.setVisibility(View.VISIBLE);
                break;
            case "Payment":
                paymentView.setVisibility(View.VISIBLE);
                setAccountInfo();
                break;
            case "History":
                historyView.setVisibility(View.VISIBLE);
                showSubscriptionHistoryData();
                break;
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
