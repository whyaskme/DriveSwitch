package com.driveswitch;

public class Message
{
    public Message()
    {
        UserId = Constants.EmptyObjectId;
        isHtml = true;
        Subject = "";
        Body = "";
        ToEmail = Utilities.urlEncode(Constants.AdminInfo.Email);
        ToName = Utilities.urlEncode(Constants.AdminInfo.Name);
        Location = new Location(Constants.EmptyObjectId, Constants.EmptyObjectId, "", 0.00, 0.00);
    }

    public String UserId;
    public Boolean isHtml;
    public String Subject;
    public String Body;
    public String ToEmail;
    public String ToName;
    public Location Location;
}
