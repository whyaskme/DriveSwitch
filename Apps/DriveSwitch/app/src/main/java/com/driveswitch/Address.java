package com.driveswitch;

import org.json.JSONException;
import org.json.JSONObject;

public class Address
{
    public Address()
    {
        CountryId = Constants.EmptyObjectId;
        StateId = Constants.EmptyObjectId;
        CountyId = Constants.EmptyObjectId;
        CityId = Constants.EmptyObjectId;
        ZipCode = "00000";
        TimeZoneId = Constants.EmptyObjectId;
        Address1 = Constants.EmptyObjectId;
        Address2 = Constants.EmptyObjectId;
    }

    public Address(JSONObject addressObj)
    {
        try
        {
            CountryId = addressObj.getString("CountryId");
            StateId = addressObj.getString("StateId");
            CountyId = addressObj.getString("CountyId");
            CityId = addressObj.getString("CityId");
            ZipCode = addressObj.getString("ZipCode");
            TimeZoneId = addressObj.getString("TimeZoneId");
            Address1 = addressObj.getString("Address1");
            Address2 = addressObj.getString("Address2");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public String CountryId;
    public String StateId;
    public String CountyId;
    public String CityId;
    public String ZipCode;
    public String TimeZoneId;
    public String Address1;
    public String Address2;
}
