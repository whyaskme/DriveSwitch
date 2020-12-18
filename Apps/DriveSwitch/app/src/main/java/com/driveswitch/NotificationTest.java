package com.driveswitch;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

import android.content.Intent;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import android.widget.Toast;

import android.os.Handler;
import android.os.Message;

import com.driveswitch.driveswitch.R;

public class NotificationTest extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DriveSwitch";
    private static final String TAG_PRE = "[" + NotificationTest.class.getSimpleName() + "] ";

    private static final int EVENT_SHOW_CREATE_NOS = 0;
    private static final int EVENT_LIST_CURRENT_NOS = 1;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private boolean isEnabledNLS = false;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case EVENT_SHOW_CREATE_NOS:
                    break;
                case EVENT_LIST_CURRENT_NOS:
                    break;
                default:
                    break;
            }
        }
    };

    private NotificationManager notificationManager;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Double userLatitude = 0.00;
    private Double userLongitude = 0.00;

    private View mProgressView;
    private View mFormView;

    private User myUser = null;

    private Boolean testCreated = false;

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

        isEnabledNLS = isEnabled();
        if (!isEnabledNLS)
        {
            showConfirmDialog();
        }
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
        populateMyUser();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_test);
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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {

                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();

                Utilities.logUserLocationAndValidateSubscription(NotificationTest.this, myUser._id, Constants.EmptyObjectId, userLatitude, userLongitude);
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

        filterMenus();

        getTNCListbox();

        Button notificationButton = (Button) findViewById(R.id.btnCreateNotify);
        Utilities.enableButton(notificationButton, false);

        Button switchboardButton = (Button) findViewById(R.id.btnSwitchboard);
        Utilities.enableButton(switchboardButton, false);

        Button clearNotificationsButton = (Button) findViewById(R.id.btnClearNotifications);
        Utilities.enableButton(clearNotificationsButton, false);
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

    public void buttonOnClicked(View view)
    {
        Button clearNotificationsButton = (Button) findViewById(R.id.btnClearNotifications);
        Button switchboardButton = (Button) findViewById(R.id.btnSwitchboard);

        switch (view.getId())
        {
            case R.id.btnCreateNotify:
                createNotification(this);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(EVENT_SHOW_CREATE_NOS), 50);
                testCreated = true;

                Utilities.enableButton(switchboardButton, true);
                Utilities.enableButton(clearNotificationsButton, true);
                break;
            case R.id.btnSwitchboard:
                if(testCreated)
                    startActivity(new Intent(NotificationTest.this, TNCControlActivity.class));
                break;
            case R.id.btnEnableUnEnableNotify:
                openNotificationAccess();
                break;
            case R.id.btnClearNotifications:
                TNCRideRequests.removeAllRequests(this);
                notificationManager.cancelAll();

                // Reset listbox
                getTNCListbox();

                switchboardButton = (Button) findViewById(R.id.btnSwitchboard);
                Utilities.enableButton(switchboardButton, false);

                clearNotificationsButton = (Button) findViewById(R.id.btnClearNotifications);
                Utilities.enableButton(clearNotificationsButton, false);
                break;
            default:
                break;
        }
    }

    private void getTNCListbox()
    {
        notificationManager.cancelAll();

        Integer tncCount = myUser.TNCs.size();

        String[] tncItems = new String[tncCount+1];

        String listLabel = "";

        if(tncCount == 1)
            listLabel = "Select a rideshare";
        else if(tncCount > 1)
            listLabel += "Select from " + tncCount + " rideshares";

        tncItems[0] = listLabel;

        // Loop through User's TNCs and set accordingly
        for (Integer i = 0; i < tncCount; i++)
        {
            TNC currentTNC = myUser.TNCs.get(i);
            tncItems[i+1] = currentTNC.Name;
        }

        // Set TNC listbox
        final Spinner tncListBox = (Spinner) findViewById(R.id.inpTNCs);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tncItems);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        tncListBox.setAdapter(spinnerArrayAdapter);
        tncListBox.setSelection(0);

        tncListBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                Button notificationButton = (Button) findViewById(R.id.btnCreateNotify);

                Integer selectionValue = tncListBox.getSelectedItemPosition();
                if(selectionValue > 0)
                    Utilities.enableButton(notificationButton, true);
                else
                    Utilities.enableButton(notificationButton, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
            }
        });

        filterMenus();
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
            intent.setData(Uri.parse("http://driveswitch.com/dashboard/"));
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

    private boolean isEnabled()
    {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);

        if (!TextUtils.isEmpty(flat))
        {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++)
            {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null)
                {
                    if (TextUtils.equals(pkgName, cn.getPackageName()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void createNotification(Context context)
    {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Spinner tncList = (Spinner) findViewById(R.id.inpTNCs);

        if(tncList.getSelectedItemPosition() > 0)
        {
            String packageName = "";

            String selectedTNC = tncList.getSelectedItem().toString();
            switch(selectedTNC)
            {
                case Constants.TNCList.FARE:
                    packageName = Constants.TNCPackageNames.FARE;
                    break;
                case Constants.TNCList.Fasten:
                    packageName = Constants.TNCPackageNames.Fasten;
                    break;
                case Constants.TNCList.GetMe:
                    packageName = Constants.TNCPackageNames.GetMe;
                    break;
                case Constants.TNCList.InstaRyde:
                    packageName = Constants.TNCPackageNames.InstaRyde;
                    break;
                case Constants.TNCList.Lyft:
                    packageName = Constants.TNCPackageNames.Lyft;
                    break;
                case Constants.TNCList.RideAustin:
                    packageName = Constants.TNCPackageNames.RideAustin;
                    break;
                case Constants.TNCList.ScoopMe:
                    packageName = Constants.TNCPackageNames.ScoopMe;
                    break;
                case Constants.TNCList.Tride:
                    packageName = Constants.TNCPackageNames.Tride;
                    break;
                case Constants.TNCList.Uber:
                    packageName = Constants.TNCPackageNames.Uber;
                    break;
                case Constants.TNCList.WingZ:
                    packageName = Constants.TNCPackageNames.WingZ;
                    break;
            }

            Notification rideRequest = new Notification();
            rideRequest.UserId = myUser._id;
            rideRequest.PackageName = packageName;
            rideRequest.NotificationTicker = myUser.FirstName + " " + myUser.LastName + " is requesting a ride on " + selectedTNC;
            rideRequest.NotificationTitle = selectedTNC + " ride request...";
            rideRequest.NotificationText = myUser.FirstName + " " + myUser.LastName + " is requesting a ride on " + selectedTNC;

            String TNCId = "";
            String UIScreen = "NotificationTest";

            Location _location = new Location(rideRequest.UserId, TNCId, UIScreen, userLatitude, userLongitude);
            rideRequest.Location = _location;

            // Just testing here...
            Boolean isNewRequest = TNCRideRequests.addRequest(this, rideRequest);

            // Create notification message to broadcast
            NotificationCompat.Builder notificationMessage = new NotificationCompat.Builder(this);
            notificationMessage.setTicker(rideRequest.NotificationTicker);
            notificationMessage.setContentTitle(rideRequest.NotificationTitle);
            notificationMessage.setContentText(rideRequest.NotificationText);
            notificationMessage.setSmallIcon(R.mipmap.ic_launcher);
            notificationMessage.setAutoCancel(true);

            // Create an explicit intent to handle request
            Intent resultIntent = new Intent(this, TNCControlActivity.class);
            resultIntent.putExtra("packageName", selectedTNC);
            resultIntent.putExtra("notificationTicker", rideRequest.NotificationTicker);
            resultIntent.putExtra("notificationTitle", rideRequest.NotificationTitle);
            resultIntent.putExtra("notificationText", rideRequest.NotificationText);

            // The stack builder object will contain an artificial back stack for the started Activity.
            // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(TNCControlActivity.class);

            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // Add intent to message
            notificationMessage.setContentIntent(resultPendingIntent);

            //notificationManager.cancelAll();
            //manager.notify((int)System.currentTimeMillis(), notificationMessage.build());

            testCreated = true;

            Toast.makeText(this, "Created ride request for " + selectedTNC, Toast.LENGTH_SHORT).show();
        }
    }

    private void openNotificationAccess()
    {
        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    private void showConfirmDialog()
    {
        new AlertDialog.Builder(this)
                .setMessage("Please enable NotificationMonitor access")
                .setTitle("Notification Access")
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                openNotificationAccess();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // do nothing
                            }
                        })
                .create().show();
    }
}
