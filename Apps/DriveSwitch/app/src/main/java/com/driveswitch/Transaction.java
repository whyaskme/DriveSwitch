package com.driveswitch;


import java.util.Date;

public class Transaction
{
    public Transaction()
    {
        _t = "Transaction";
        _id = Constants.EmptyObjectId;

        UserId = "";
        RenewalPeriod = 0;
        SavePaymentMethod = false;

        Date = new Date();
        Type = "";
        Amount = "";
        ProcessorId = "";
        PaymentMethodId = "";
        ResultCode = "";
        ResultName = "";
        ResultDetails = "";

        Latitude = "";
        Longitude = "";

        PaymentCard = new CreditCard();
    }

    public String _t;
    public String _id;

    public String UserId;
    public Integer RenewalPeriod;
    public Boolean SavePaymentMethod;

    public Date Date;
    public String Type;
    public String Amount;
    public String ProcessorId;
    public String PaymentMethodId;
    public String ResultCode;
    public String ResultName;
    public String ResultDetails;
    public String Latitude;
    public String Longitude;
    public CreditCard PaymentCard;
}
