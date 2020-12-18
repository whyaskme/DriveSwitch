package com.driveswitch;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class TNCRideRequests
{
    public TNCRideRequests() {}

    public static Boolean addRequest(Context _context, Notification rideRequest)
    {
        Boolean isNewRequest = true;

        String newTNCName = rideRequest.PackageName;
        String existingTNCName = "";

        List<Notification> currentRequests = getPendingRequests(_context);

        // Loop through and make sure a request for this TNC dfoesn't already exist
        Integer requestCount = currentRequests.size();
        for(int i = 0; i < requestCount; i++)
        {
            Notification currentRequest = currentRequests.get(i);
            existingTNCName = currentRequest.PackageName;

            if(newTNCName.equalsIgnoreCase(existingTNCName))
                isNewRequest = false;
        }

        if(isNewRequest)
        {
            currentRequests.add(rideRequest);

            Utilities.saveRideRequestsToPreferences(_context, currentRequests);
        }

        return isNewRequest;
    }

    public static List<Notification> removeRequest(Context _context, String tncName)
    {
        // Reset so we can fetch fresh data from preferences
        Requests.clear();

        // Get requests from preferences
        Requests = Utilities.getRideRequestsFromPreferences(_context);

        // Loop through collection and remove the one that matches tncName
        for(int i = 0; i < Requests.size(); i++)
        {
            Notification currentRequest = Requests.get(i);

            String currentTNCName = currentRequest.PackageName;

            if(currentTNCName.equalsIgnoreCase(tncName))
            {
                Requests.remove(currentRequest);

                // Save updated requests to preferences
                Utilities.saveRideRequestsToPreferences(_context, Requests);
            }
        }

        // Get requests from preferences AGAIN
        Requests = Utilities.getRideRequestsFromPreferences(_context);

        return Requests;
    }

    public static void removeAllRequests(Context _context)
    {
        // Remove from preferences
        Utilities.clearRideRequestsFromPreferences(_context);
    }

    public static List<Notification> getPendingRequests(Context _context)
    {
        // Reset so we can fetch fresh data from preferences
        Requests.clear();

        // Get requests from preferences
        Requests = Utilities.getRideRequestsFromPreferences(_context);

        return Requests;
    }

    public static List<Notification> Requests = new ArrayList<Notification>();
}
