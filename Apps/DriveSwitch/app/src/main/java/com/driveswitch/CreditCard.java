package com.driveswitch;

/**
 * Created by Joes-Acer on 1/18/2017.
 */

public class CreditCard
{
    public CreditCard()
    {
        _id = Constants.EmptyObjectId;
        _t = "CreditCard";

        FullName = "";
        CardTypeId = Constants.CreditCard.Unknown[0];
        CardTypeName = Constants.CreditCard.Unknown[1];
        Number = "";
        Expires = "";
        Zipcode = "";
        CVVCode = "";
    }
    public String _id;
    public String _t;
    public String FullName;
    public String CardTypeId;
    public String CardTypeName;
    public String Number;
    public String Expires;
    public String Zipcode;
    public String CVVCode;
}
