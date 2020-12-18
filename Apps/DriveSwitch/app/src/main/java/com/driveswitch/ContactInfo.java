package com.driveswitch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joes-Acer on 1/18/2017.
 */

public class ContactInfo
{
    public ContactInfo()
    {
        try
        {
            Address = new Address();
            Email = new Email();
            Phone = new Phone();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ContactInfo(JSONObject contactInfo)
    {
        try
        {
            JSONObject addressObj = contactInfo.getJSONObject("Address");
            Address = new Address(addressObj);

            JSONObject emailObj = contactInfo.getJSONObject("Email");
            Email = new Email(emailObj);

            JSONObject phoneObj = contactInfo.getJSONObject("Phone");
            Phone = new Phone(phoneObj);

            String wtf = "???";
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public Address Address;
    public Email Email;
    public Phone Phone;
}
