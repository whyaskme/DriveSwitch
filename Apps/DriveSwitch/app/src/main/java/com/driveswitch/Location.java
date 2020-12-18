package com.driveswitch;

import java.util.Date;

/**
 * Created by Joes-Acer on 1/19/2017.
 */

public class Location
{
    public Location(String userId, String tncId, String uiScreen, Double latitude, Double longitude)
    {
        DateTime = new Date();
        UIScreen = uiScreen;
        UserId = userId;
        TNCId = tncId;
        Latitude = latitude;
        Longitude = longitude;
    }

    public Date DateTime;
    public String TNCId;
    public String UIScreen;
    public String UserId;
    public Double Latitude;
    public Double Longitude;
}
