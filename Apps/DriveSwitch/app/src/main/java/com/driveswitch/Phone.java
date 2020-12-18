package com.driveswitch;

import org.json.JSONException;
import org.json.JSONObject;

public class Phone
{
    public Phone()
    {
        PhoneType = 0;
        CountryCode = "1"; // 1 = United States
        AreaCode = 000;
        Exchange = "000";
        Number = "0000";
    }

    public Phone(JSONObject phoneObj)
    {
        try
        {
            PhoneType = phoneObj.getInt("AreaCode"); // 0=Mobile, 1=Home, 2=Work, 3=Fax
            CountryCode = phoneObj.getString("AreaCode"); // 1 = United States
            AreaCode = phoneObj.getInt("AreaCode");
            Exchange = phoneObj.getString("AreaCode");
            Number = phoneObj.getString("AreaCode");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public Integer PhoneType;
    public String CountryCode;
    public Integer AreaCode ;
    public String Exchange;
    public String Number;
}
