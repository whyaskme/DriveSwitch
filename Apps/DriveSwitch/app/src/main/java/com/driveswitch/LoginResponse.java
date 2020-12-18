package com.driveswitch;

public class LoginResponse
{
    public LoginResponse()
    {
        IsLoggedIn = false;
        ResponseId = 0;
        FailureReason = "";
    }

    Boolean IsLoggedIn;
    Integer ResponseId;
    String FailureReason;
}
