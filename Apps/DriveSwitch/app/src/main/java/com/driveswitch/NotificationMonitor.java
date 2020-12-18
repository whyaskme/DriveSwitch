package com.driveswitch;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.driveswitch.driveswitch.R;

import org.json.JSONObject;

public class NotificationMonitor extends NotificationListenerService
{
    private Context _context;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Double userLatitude = 0.00;
    private Double userLongitude = 0.00;

    private User myUser = null;
    private String userId = "";

    private String packageName = "";
    private String notificationTicker = "";
    private String notificationText = "";
    private String notificationTitle = "";

    private Boolean isTNCRequest = false;

    @Override
    public void onCreate()
    {
        super.onCreate();

        _context = this.getApplicationContext();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        try
        {
            userId = myUser._id;
            if(userId == null)
                userId = Constants.EmptyObjectId;

            packageName = sbn.getPackageName();
            notificationTicker = sbn.getNotification().tickerText.toString();

            Bundle extras = sbn.getNotification().extras;
            if(extras != null)
            {
                if (extras.containsKey("android.text"))
                {
                    if (extras.getCharSequence("android.text") != null)
                    {
                        CharSequence chars = extras.getCharSequence("android.text");
                        if(!TextUtils.isEmpty(chars))
                            notificationText = chars.toString();
                    }
                }

                if (extras.containsKey("android.title"))
                {
                    if (extras.getString("android.text") != null) {
                        notificationTitle = extras.getString("android.title");
                    }
                }
            }

            //Utilities.logNotification(_context, userId, packageName, notificationTicker, notificationTitle, notificationText, userLatitude, userLongitude);

            //isTNCRequest = true;
            // Loop through TNCs. If message is related to one, then handle. Otherwise ignore.
            isTNCRequest = Utilities.isNotificationRideRequest(packageName, notificationTicker, notificationTitle, notificationText);
            if(isTNCRequest)
            {
                Intent notificationMessage = new Intent("RideRequest");
                notificationMessage.putExtra("packageName", packageName);
                notificationMessage.putExtra("notificationTicker", notificationTicker);
                notificationMessage.putExtra("notificationTitle", notificationTitle);
                notificationMessage.putExtra("notificationText", notificationText);

                LocalBroadcastManager.getInstance(this).sendBroadcast(notificationMessage);

                Integer NOTIFICATION_ID = sbn.getId();

                try {
                    Intent resultIntent = new Intent(this, TNCControlActivity.class);
                    resultIntent.putExtra("packageName", packageName);
                    resultIntent.putExtra("notificationTicker", notificationTicker);
                    resultIntent.putExtra("notificationTitle", notificationTitle);
                    resultIntent.putExtra("notificationText", notificationText);

                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(NOTIFICATION_ID);

                    //startActivity(resultIntent);

                    //NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

                    //Intent notifyIntent = new Intent(new Intent(this, TNCControlActivity.class));

                    // Sets the Activity to start in a new, empty task
                    //notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // Creates the PendingIntent
                    //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Puts the PendingIntent into the notification builder
                    //builder.setContentIntent(pendingIntent);

                    // Notifications are issued by sending them to the NotificationManager system service.
                    //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // Builds an anonymous Notification object from the builder, and passes it to the NotificationManager
                    //mNotificationManager.notify(NOTIFICATION_ID, builder.build());
                }
                catch(Exception ex)
                {
                    notificationText = ex.toString();
                    //Utilities.logNotification(_context, userId, packageName, notificationTicker, notificationTitle, notificationText, userLatitude, userLongitude);
                }
            }
            else
            {
                // Ignore notification!
            }
        }
        catch(Exception ex)
        {
            notificationText = ex.toString();

            //Utilities.logNotification(_context, userId, packageName, notificationTicker, notificationTitle, notificationText, userLatitude, userLongitude);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn)
    {
        //mRemovedNotification = sbn;
    }
}
