package com.driveswitch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joes-Acer on 1/18/2017.
 */

public class Email
{
    public Email()
    {
        UserName = "";
        Domain = "";
    }

    public Email(JSONObject emailObj)
    {
        try
        {
            UserName = emailObj.getString("UserName");
            Domain = emailObj.getString("Domain");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String UserName;
    public String Domain;
}
