package com.driveswitch;

/**
 * Created by Joes-Acer on 1/18/2017.
 */

public class UserRole
{
    public UserRole()
    {
        _id = Constants.EmptyObjectId;
        _t = "UserRole";
        Name = "";
        Enabled = false;
    }

    public String _id;
    public String _t;
    public String Name;
    public Boolean Enabled;
}
