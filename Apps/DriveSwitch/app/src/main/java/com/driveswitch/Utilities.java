package com.driveswitch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.driveswitch.driveswitch.R;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.InputStream;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.net.URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.*;

public class Utilities extends AppCompatActivity
{
    private static WebApiTask myWebApiTask;

    private static String serviceHost = "";
    private static String serviceUrl = "";

    private static Pattern pattern;
    private static Matcher matcher;

    private static Gson gson = new Gson();

    private static List<Notification> tncRequestList = new ArrayList<Notification>();

    public Utilities() {
    }

    public static List<ApplicationInfo> listThirdPartyApps(Context _context)
    {
        List<ApplicationInfo> installedTNCApps = new ArrayList<ApplicationInfo>();

        PackageManager packageManager = _context.getPackageManager();
        List<ApplicationInfo> listOfApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        //List<PackageInfo> packs = _context.getPackageManager().getInstalledPackages(0);

        String packageName = "";

        for (int i = 0; i < listOfApps.size(); i++)
        {
            ApplicationInfo currentApp = listOfApps.get(i);

            Boolean isSystemApp = isSystemPackage(currentApp);

            if(!isSystemApp)
            {
                packageName = currentApp.packageName;

                // Testing
                //installedTNCApps.add(currentApp);

                // Check to see if this a rideshare app against constants
                switch (packageName) {
                    case Constants.TNCPackageNames.FARE:
                        installedTNCApps.add(currentApp);
                        break;
                    case Constants.TNCPackageNames.Fasten:
                        installedTNCApps.add(currentApp);
                        break;
                    case Constants.TNCPackageNames.GetMe:
                        installedTNCApps.add(currentApp);
                        break;
                    case Constants.TNCPackageNames.InstaRyde:
                        installedTNCApps.add(currentApp);
                        break;
                    case Constants.TNCPackageNames.Lyft:
                        installedTNCApps.add(currentApp);
                        break;
                    case Constants.TNCPackageNames.RideAustin:
                        installedTNCApps.add(currentApp);
                        break;
                    case Constants.TNCPackageNames.ScoopMe:
                        installedTNCApps.add(currentApp);
                        break;
                    case Constants.TNCPackageNames.Tride:
                        installedTNCApps.add(currentApp);
                        break;
                    case Constants.TNCPackageNames.Uber:
                        installedTNCApps.add(currentApp);
                        break;
                    case Constants.TNCPackageNames.WingZ:
                        installedTNCApps.add(currentApp);
                        break;
                }
            }
        }

        Collections.sort(installedTNCApps, new ApplicationInfo.DisplayNameComparator(packageManager));

        return installedTNCApps;
    }

    private static boolean isSystemPackage(ApplicationInfo appInfo)
    {
        return ((appInfo.flags & appInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    private static boolean isSystemPackageOld(PackageInfo pkgInfo)
    {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    public static String urlEncode(String toEncode)
    {
        String encodedValue = null;
        try
        {
            encodedValue = URLEncoder.encode(toEncode, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return encodedValue;
    }

    public static void enableButton(Button myButton, Boolean setEnabled)
    {
        if(setEnabled)
        {
            myButton.setAlpha(1f);
            myButton.setClickable(true);
        }
        else
        {
            myButton.setAlpha(.25f);
            myButton.setClickable(false);
        }
    }

    public static String normalizeCardNumber(String cardNumber)
    {
        StringBuilder sbCleanCardNumber = new StringBuilder();

        if (cardNumber == null)
            cardNumber = "";

        String[] myStringArray = cardNumber.split("(?!^)");
        for (int i = 0; i < myStringArray.length; i++)
        {
            String currentChar = myStringArray[i].toString();

            Boolean isDigit = Character.isDigit(currentChar.charAt(0));
            if(isDigit)
                sbCleanCardNumber.append(myStringArray[i]);
        }

        return sbCleanCardNumber.toString();
    }

    public static String formatLongDateString(String inputDate)
    {
        String formattedDate = inputDate;

        // Replace days
        inputDate = inputDate.replace("Monday,", "Mon");
        inputDate = inputDate.replace("Tuesday,", "Tue");
        inputDate = inputDate.replace("Wednesday,", "Wed");
        inputDate = inputDate.replace("Thursday,", "Thu");
        inputDate = inputDate.replace("Friday,", "Fri");
        inputDate = inputDate.replace("Saturday,", "Sat");
        inputDate = inputDate.replace("Sunday,", "Sun");

        // Replace Months
        inputDate = inputDate.replace("January", "Jan");
        inputDate = inputDate.replace("February", "Feb");
        inputDate = inputDate.replace("March", "Mar");
        inputDate = inputDate.replace("April", "Apr");
        inputDate = inputDate.replace("May", "May");
        inputDate = inputDate.replace("June", "Jun");
        inputDate = inputDate.replace("July", "Jul");
        inputDate = inputDate.replace("August", "Aug");
        inputDate = inputDate.replace("September", "Sep");
        inputDate = inputDate.replace("October", "Oct");
        inputDate = inputDate.replace("November", "Nov");
        inputDate = inputDate.replace("December", "Dec");

        return inputDate;
    }

    public static String formatSubscriptionDate(String inputDate)
    {
        String formattedDate = "";

        //Tue Jan 10 18:00:00 CST 2012
        String[] tmpVal = inputDate.split(" ");

        formattedDate = tmpVal[0] + " " + tmpVal[1] + " " + tmpVal[2] + " " + tmpVal[5]; // CST + " " + tmpVal[4];

        return formattedDate;
    }

    public static String formatUTCDate(String inputDate)
    {
        String formattedDate = "";

        // 2017-01-09T22:11:12.120Z
        // This works: 2011-05-18 16:35:01
        inputDate = inputDate.replace("T", " ");

        Date value = null;
        try
        {
            /* Get the device timezone */
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

            String tzDisplayName = tz.getDisplayName();

            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(inputDate);

            formattedDate = myDate.toString();

            //String[] tmpVal = formattedDate.split(" ");

            //formattedDate = tmpVal[0] + " " + tmpVal[1] + " " + tmpVal[2] + ", " + tmpVal[5];
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static String getDateFromUtc(String inputDate)
    {
        String formattedDate = "";

        // 2017-01-09T22:11:12.120Z
        // This works: 2011-05-18 16:35:01
        inputDate = inputDate.replace("T", " ");

        Date value = null;
        try
        {
            /* Get the device timezone */
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

            String tzDisplayName = tz.getDisplayName();

            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(inputDate);

            formattedDate = myDate.toString();

            String[] tmpVal = formattedDate.split(" ");

            formattedDate = tmpVal[0] + " " + tmpVal[1] + " " + tmpVal[2] + ", " + tmpVal[5];
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static JSONArray getSubscriptionPeriods()
    {
        try
        {
            serviceUrl = "Transaction";
            serviceUrl += "?requestType=RenewalPeriods";

            myWebApiTask = new WebApiTask(Constants.WebMethods.GET, serviceUrl, "");

            JSONArray serviceResponse = myWebApiTask.execute((Void) null).get();

            if(serviceResponse != null)
                return serviceResponse;
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
        finally {
            if(myWebApiTask != null)
                myWebApiTask = null;
        }
        return null;
    }

    public static void setNightMode(String setNightMode)
    {
        switch(setNightMode)
        {
            case "Off":

                break;
            case "On":

                break;
            case "Automatic":

                break;
        }
    }

    public static LoginResponse loginUser(Context _context, String email, String password, String userLatitude, String userLongitude)
    {
        Boolean userLoggedIn = false;

        LoginResponse _loginResponse = new LoginResponse();

        try
        {
            serviceUrl = "User";
            serviceUrl += "?email=" + urlEncode(email);
            serviceUrl += "&password=" + urlEncode(password);
            serviceUrl += "&latitude=" + userLatitude;
            serviceUrl += "&longitude=" + userLongitude;

            myWebApiTask = new WebApiTask(Constants.WebMethods.GET, serviceUrl, "");

            JSONArray serviceResponse = myWebApiTask.execute((Void) null).get();

            if(serviceResponse != null)
            {
                JSONObject userData = serviceResponse.getJSONObject(0);

                String responseString = userData.toString();

                Boolean isInvalidEmail = responseString.toLowerCase().contains("not registered");

                if(isInvalidEmail)
                {
                    _loginResponse.ResponseId = Constants.Login.BadEmail;
                    _loginResponse.FailureReason = email + " not registered!";
                }
                else
                {
                    _loginResponse.IsLoggedIn = userData.getBoolean("IsLoggedIn");

                    if(!_loginResponse.IsLoggedIn)
                    {
                        _loginResponse.ResponseId = Constants.Login.BadPassword;
                        _loginResponse.FailureReason = password + " password is incorrect!";
                    }
                    else
                    {
                        saveUserPreferences(_context, responseString);
                    }
                }
            }
        }
        catch(Exception ex)
        {
            _loginResponse.FailureReason = ex.toString();
        }
        finally {
            if(myWebApiTask != null)
                myWebApiTask = null;
        }
        return _loginResponse;
    }

    public static String[] validateSubscription(Context _context)
    {
        String[] expireResponse = new String[2];

        Boolean userExpired = false;
        String userExpireDate = "";

        serviceUrl = "User";
        String userEmail = "";

        try
        {
            SharedPreferences prefs = _context.getSharedPreferences(Constants.User.Profile, Context.MODE_PRIVATE);
            String serializedUser = prefs.getString(Constants.User.Profile, null);

            if(serializedUser != null)
            {
                JSONObject jsonUser = new JSONObject(serializedUser);

                User myUser = new User(jsonUser);

                userEmail = myUser.Contact.Email.UserName + "@" + myUser.Contact.Email.Domain;
            }

            serviceUrl += "?email=" + urlEncode(userEmail);

            myWebApiTask = new WebApiTask(Constants.WebMethods.GET, serviceUrl, "");

            JSONArray serviceResponse = myWebApiTask.execute((Void) null).get();

            if(serviceResponse != null)
            {
                JSONObject userData = serviceResponse.getJSONObject(0);

                // Save user expires prefs
                userExpireDate = userData.getString(Constants.User.ExpireDate);
                SharedPreferences.Editor editor = _context.getSharedPreferences(_context.getResources().getString(R.string.app_user_expired_date_key), _context.MODE_PRIVATE).edit();
                editor.putString(Constants.User.ExpireDate, userExpireDate);
                editor.commit();

                expireResponse[0] = userData.getString("Expired");
                expireResponse[1] = userExpireDate;
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
        finally {
            if(myWebApiTask != null)
                myWebApiTask = null;
        }
        return expireResponse;
    }

    public static JSONArray createNewUser(Context _context, String userEmail)
    {
        JSONArray newUser = null;

        serviceUrl = "User";

        try
        {
            serviceUrl += "?email=" + urlEncode(userEmail);

            myWebApiTask = new WebApiTask(Constants.WebMethods.GET, serviceUrl, "");

            newUser = myWebApiTask.execute((Void) null).get();

            if(newUser != null)
            {
                JSONObject userData = newUser.getJSONObject(0);

                // Save user data in prefs
                saveUserPreferences(_context, userData.toString());
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
        finally {
            if(myWebApiTask != null)
                myWebApiTask = null;
        }
        return newUser;
    }

    public static JSONArray updateUser(Context _context, String userId, String serializedUser)
    {
        JSONArray updatedUser = null;

        serviceUrl = "User";

        try
        {
            serviceUrl += "?userId=" + userId;
            serviceUrl += "&latitude=0.00";
            serviceUrl += "&longitude=0.00";

            myWebApiTask = new WebApiTask(Constants.WebMethods.POST, serviceUrl, serializedUser);

            updatedUser = myWebApiTask.execute((Void) null).get();
            if(updatedUser != null)
            {
                String testUserString = updatedUser.toString();

                Boolean isEmailOrPhoneAlreadyRegistered = testUserString.toLowerCase().contains("already registered");
                if(!isEmailOrPhoneAlreadyRegistered)
                {
                    JSONObject userData = updatedUser.getJSONObject(0);

                    // Save user data in prefs
                    saveUserPreferences(_context, userData.toString());
                }
                return updatedUser;
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
        finally {
            if(myWebApiTask != null)
                myWebApiTask = null;
        }
        return null;
    }

    public static User renewSubscription(Context _context, Transaction transaction)
    {
        User myUser = null;

        JSONArray updatedUser = null;

        try
        {
            serviceUrl = "Transaction";
            serviceUrl += "?userId=" + transaction.UserId;

            String serializedTransaction = gson.toJson(transaction);

            myWebApiTask = new WebApiTask(Constants.WebMethods.POST, serviceUrl, serializedTransaction);

            updatedUser = myWebApiTask.execute((Void) null).get();
            if(updatedUser != null)
            {
                myUser = new User(updatedUser.getJSONObject(0));

                String jsonUpdatedUser = gson.toJson(myUser);

                // Save user data in prefs
                saveUserPreferences(_context, jsonUpdatedUser);
            }
        }
        catch(Exception ex)
        {

        }
        finally {
            if(myWebApiTask != null)
                myWebApiTask = null;
        }
        return myUser;
    }

    public static String sendMessage(Message _message)
    {
        String messageConfirmation = "";

        JSONArray serviceResponse = null;

        serviceUrl = "Message";

        try
        {
            String serializedMessage = gson.toJson(_message);

            serviceUrl += "?userId=" + _message.UserId;

            myWebApiTask = new WebApiTask(Constants.WebMethods.POST, serviceUrl, serializedMessage);

            serviceResponse = myWebApiTask.execute((Void) null).get();
            if(serviceResponse != null)
            {
                JSONArray jsonResponse = new JSONArray(serviceResponse.toString());

                JSONObject response = new JSONObject(jsonResponse.getString(0));

                messageConfirmation = response.getString("Result");
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
        finally {
            if(myWebApiTask != null)
                myWebApiTask = null;
        }

        return messageConfirmation;
    }

    public static Boolean isNotificationRideRequest(String packageName, String notificationTicker, String notificationTitle, String notificationText)
    {
        //Boolean isTNCRequest = false;

        for(int i = 0; i < Constants.TNCNames.length; i++)
        {
            String currentTNC = Constants.TNCNames[i];

            if(packageName.contains(currentTNC))
                return true;

            if(notificationTicker.contains(currentTNC))
                return true;

            if(notificationTitle.contains(currentTNC))
                return true;

            if(notificationText.contains(currentTNC))
                return true;
        }

        return false;
    }

    public static void logNotification(Context context, String userId, String packageName, String notificationTicker, String notificationTitle, String notificationText, Double latitude, Double longitude)
    {
        JSONArray serviceResponse = null;

        serviceUrl = "Notification";

        try
        {
            Notification _notification = new Notification();

            _notification.UserId = userId;
            _notification.PackageName = packageName;

            _notification.NotificationTicker = notificationTicker;
            _notification.NotificationTitle = notificationTitle;
            _notification.NotificationText = notificationText;

            Location _location = new Location(userId, Constants.EmptyObjectId, "", latitude, longitude);
            _notification.Location = _location;

            _notification.Location.UserId = userId;
            _notification.Location.Latitude = latitude;
            _notification.Location.Longitude = longitude;

            _notification.Location.TNCId = Constants.EmptyObjectId;
            _notification.Location.UIScreen = ""; // context.getClass().toString();

            String serializedNotification = gson.toJson(_notification);

            serviceUrl += "?userId=" + userId;

            myWebApiTask = new WebApiTask(Constants.WebMethods.POST, serviceUrl, serializedNotification);

            serviceResponse = myWebApiTask.execute((Void) null).get();
            if(serviceResponse != null)
            {
                // Do nothing
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
        finally {
            if(myWebApiTask != null)
                myWebApiTask = null;
        }
    }

    public static void logUserLocationAndValidateSubscription(Context _context, String userId, String tncId, Double latitude, Double longitude)
    {
        // The wepapi service should return the updated user to combine subscription validation into one call.

        JSONArray updatedUser = null;

        String uiScreen = _context.getClass().toString();

        Location _location = new Location(userId, tncId, uiScreen, latitude, longitude);

        String serializedLocation = gson.toJson(_location);

        serviceUrl = "Location";

        try
        {
            serviceUrl += "?userId=" + userId;

            myWebApiTask = new WebApiTask(Constants.WebMethods.POST, serviceUrl, serializedLocation);

            updatedUser = myWebApiTask.execute((Void) null).get();
            if(updatedUser != null)
            {
                // Return updated user for integrated subscription validation
                JSONObject userData = updatedUser.getJSONObject(0);

                // Save user data in prefs
                saveUserPreferences(_context, userData.toString());
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
        finally {
            if(myWebApiTask != null)
                myWebApiTask = null;
        }
    }

    public static JSONArray getTNCList(Context _context)
    {
        try
        {
            serviceUrl = "TNC";

            myWebApiTask = new WebApiTask(Constants.WebMethods.GET, serviceUrl, "");

            JSONArray serviceResponse = myWebApiTask.execute((Void) null).get();

            if(serviceResponse != null)
                return serviceResponse;
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
        finally {
            if(myWebApiTask != null)
                myWebApiTask = null;
        }
        return null;
    }

    public static void saveUserPreferences(Context _context, String serializedUser)
    {
        try
        {
            // Update local User preferences
            SharedPreferences.Editor editor = _context.getSharedPreferences(Constants.User.Profile, _context.MODE_PRIVATE).edit();
            editor.putString(Constants.User.Profile, serializedUser);
            editor.commit();

            return;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void clearRideRequestsFromPreferences(Context _context)
    {
        try
        {
            // Update local User preferences
            SharedPreferences.Editor editor = _context.getSharedPreferences(Constants.User.RideRequests, _context.MODE_PRIVATE).edit();
            editor.clear();
            editor.commit();

            return;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void saveRideRequestsToPreferences(Context _context, List<Notification> rideRequests)
    {
        try
        {
            String serializedRideRequests = gson.toJson(rideRequests);

            // Update local User preferences
            SharedPreferences.Editor editor = _context.getSharedPreferences(Constants.User.RideRequests, _context.MODE_PRIVATE).edit();
            editor.putString(Constants.User.RideRequests, serializedRideRequests);
            editor.commit();

            return;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static List<Notification> getRideRequestsFromPreferences(Context _context)
    {
        List<Notification> tncRequestList = new ArrayList<Notification>();

        SharedPreferences prefs = _context.getSharedPreferences(Constants.User.RideRequests, Context.MODE_PRIVATE);
        String serializedRideRequests = prefs.getString(Constants.User.RideRequests, null);

        if(serializedRideRequests != null)
        {
            // Read response into Json object from preferences
            JSONArray jsonRequests = null;

            try
            {
                jsonRequests = new JSONArray(serializedRideRequests);

                // Loop through and populate list
                Integer requestCount = jsonRequests.length();
                for(int i = 0; i < requestCount; i++)
                {
                    JSONObject rideRequest = jsonRequests.getJSONObject(i);

                    Notification _notification = new Notification();
                    _notification.UserId = rideRequest.getString("UserId");
                    _notification.PackageName = rideRequest.getString("PackageName");
                    _notification.NotificationTicker = rideRequest.getString("NotificationTicker");
                    _notification.NotificationTitle = rideRequest.getString("NotificationTitle");
                    _notification.NotificationText = rideRequest.getString("NotificationText");

                    JSONObject jsonLocation = rideRequest.getJSONObject("Location");
                    String TNCId = jsonLocation.getString("TNCId");
                    String UIScreen = jsonLocation.getString("UIScreen");
                    Double Latitude = jsonLocation.getDouble("Latitude");
                    Double Longitude = jsonLocation.getDouble("Longitude");

                    Location _location = new Location(_notification.UserId, TNCId, UIScreen, Latitude, Longitude);

                    _notification.Location = _location;

                    tncRequestList.add(_notification);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            // Stored prefs are null

            String wtf = "";
        }
        return tncRequestList;
    }

    public static void clearUserPreferences(Context _context)
    {
        SharedPreferences.Editor editor = _context.getSharedPreferences(Constants.User.Profile, _context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

        editor = _context.getSharedPreferences(Constants.User.ExpireDate, _context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public static void clearPreferencesAndLogOff(Context _context)
    {
        String userId = "";

        try
        {
            SharedPreferences prefs = _context.getSharedPreferences(Constants.User.Profile, Context.MODE_PRIVATE);
            String serializedUser = prefs.getString(Constants.User.Profile, null);

            if(serializedUser != null)
            {
                JSONObject jsonUser = new JSONObject(serializedUser);

                userId = jsonUser.getString("_id");
            }

            clearUserPreferences(_context);

            serviceUrl = "";
            serviceUrl += "User";
            serviceUrl += "?userId=" + userId;
            serviceUrl += "&userAction=999";
            serviceUrl += "&latitude=0.00";
            serviceUrl += "&longitude=0.00";

            WebApiTask locationTask = new WebApiTask(Constants.WebMethods.GET, serviceUrl, "");
            locationTask.execute((Void) null);

            Intent intent = new Intent(_context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            _context.startActivity(intent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static String validateFirstName(String firstName)
    {
        String validationResult = "";

        if(firstName.length() < 2)
            validationResult = "First name must be 2 or more characters";

        return validationResult;
    }

    public static String validateLastName(String lastName)
    {
        String validationResult = "";

        if(lastName.length() < 2)
            validationResult = "Last name must be 2 or more characters";

        return validationResult;
    }

    public static String validatePassword(String password)
    {
        String validationResult = "";

        if(password.length() < 2)
            validationResult = "Password must be " + Constants.Login.MinPwdLen + " or more characters";

        return validationResult;
    }

    public static Boolean validateEmailAdress(String emailAddress)
    {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(emailAddress);

        Boolean emailMatches = matcher.matches();

        return emailMatches;
    }

    public static Boolean validateMobilePhone(String mobilePhone)
    {
        Boolean phoneMatches = false;

        if(mobilePhone.equalsIgnoreCase(""))
        {
            return phoneMatches;
        }
        else if(mobilePhone.contains(" ")) {
            String[] phoneElements = mobilePhone.split(" ");
            String cleanPhoneNumber = phoneElements[0].replace("(", "").replace(")", "-");
            cleanPhoneNumber += phoneElements[1];

            String PHONE_REGEX = "\\d{3}-\\d{3}-\\d{4}";

            Pattern pattern = Pattern.compile(PHONE_REGEX);
            Matcher matcher = pattern.matcher(cleanPhoneNumber);

            phoneMatches = matcher.matches();
        }

        return phoneMatches;
    }

    public static Boolean validateZipCode(String zipCode)
    {
        Boolean zipcodeMatches = false;

        Pattern pattern = Pattern.compile(ZIPCODE_PATTERN);
        Matcher matcher = pattern.matcher(zipCode);

        zipcodeMatches = matcher.matches();

        return zipcodeMatches;
    }

    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    //Phone Pattern
    private static final String PHONE_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    //Zipcode Pattern
    private static final String ZIPCODE_PATTERN = "^[0-9]{5}(?:-[0-9]{4})?$";

    public static boolean isNotNull(String txt){
        return txt!=null && txt.trim().length()>0 ? true: false;
    }

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    public static String getRandomString(final int sizeOfRandomString)
    {
        final Random random = new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}
