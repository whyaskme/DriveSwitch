package com.driveswitch;


import java.util.Date;

public class Event
{
    public Event(Double latitude, Double longitude)
    {
        _t = "Event";

        Name = "";
        TypeId = 0;
        DateTime = new Date();

        Reference = new Reference(Constants.EmptyObjectId, 0);
        Details = "None";

        Location = new Location(Constants.EmptyObjectId, Constants.EmptyObjectId, "", latitude, longitude);
    }
    public String _t;
    public String Name;
    public Integer TypeId;
    public Date DateTime ;
    public Reference Reference;
    public String Details;
    public Location Location;
}
