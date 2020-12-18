package com.driveswitch;

public class Notification
{
    public Notification()
    {
        UserId = "";
        PackageName = "";
        NotificationTicker = "";
        NotificationTitle = "";
        NotificationText = "";

        Location = new Location(UserId, Constants.EmptyObjectId, "", 0.00, 0.00);
    }

    public String UserId;
    public String PackageName;
    public String NotificationTicker;
    public String NotificationTitle;
    public String NotificationText;
    public Location Location;
}
