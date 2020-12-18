package com.driveswitch;

public class Constants
{
    public static String EmptyObjectId = "000000000000000000000000";

    public static class ServiceHost
    {
        public static String LocalHost = "http://10.0.2.2/api/";
        public static String Production = "http://api.driveswitch.com/api/";
    }

    public static class WebMethods
    {
        public static String OPTIONS = "OPTIONS";
        public static String GET = "GET";
        public static String HEAD = "HEAD";
        public static String POST = "POST";
        public static String PUT = "PUT";
        public static String DELETE = "DELETE";
        public static String TRACE = "TRACE";
        public static String PATCH = "PATCH";
    }

    public static class Login
    {
        public static Boolean Success = true;
        public static Integer BadEmail = 0;
        public static Integer BadPassword = 1;
        public static Integer MinPwdLen = 4;
    }

    public static class User
    {
        public static String Profile = "Profile";
        public static String ExpireDate = "ExpireDate";
        public static String TNCs = "Profile";
        public static String RideRequests = "RideRequests";

        public static class Role
        {
            public static String System_Administrator = "57be0508d00bc528b4bfb0f9";
            public static String Site_Administrator = "57be0527d00bc528b4bfb0fa";
            public static String Group_Administrator = "57be052dd00bc528b4bfb0fb";
            public static String Consumer = "57be0533d00bc528b4bfb0fc";
        }
    }

    public static class CreditCard
    {
        public static String[] Unknown = {"567b0b9e3ee6522c6c863934", ""};
        public static String[] MasterCard = {"567b0ba63ee6522c6c863936", ""};
        public static String[] VISA = {"567b0ba83ee6522c6c863938", ""};
        public static String[] Amex = {"567b0bac3ee6522c6c86393a", ""};
        public static String[] Discover = {"567b0bac3ee6522c6c86393c", ""};
        public static String[] DinersClub = {"567b0bac3ee6522c6c86393e", ""};
        public static String[] JCB = {"567b0bac3ee6522c6c863940", ""};
        public static String[] enRoute = {"567b0bac3ee6522c6c863942", ""};
    }

    public static class PhoneType
    {
        public static Integer Home = 0;
        public static Integer Mobile = 1;
    }

    public static String[] TNCNames = {"FARE", "Fasten", "GetMe", "InstaRyde", "Lyft", "Ride|Austin", "ScoopMe", "Tride", "Uber", "WingZ"};

    public static class TNCList
    {
        public static final String FARE = "FARE";
        public static final String Fasten = "Fasten";
        public static final String GetMe = "GetMe";
        public static final String InstaRyde = "InstaRyde";
        public static final String Lyft = "Lyft";
        public static final String RideAustin = "Ride|Austin";
        public static final String ScoopMe = "ScoopMe";
        public static final String Tride = "Tride";
        public static final String Uber = "Uber";
        public static final String WingZ = "WingZ";
    }

    public static class TNCPackageNames
    {
        public static final String FARE = "com.fare.drivefare";
        public static final String Fasten = "com.fastendriver";
        public static final String GetMe = "com.getittechnologies.getit";
        public static final String InstaRyde = "com.instaryde.android";
        public static final String Lyft = "me.lyft.android";
        public static final String RideAustin = "com.rideaustin.driver";
        public static final String ScoopMe = "com.scoopyou.scoopyoudriver";
        public static final String Tride = "com.tride.driver";
        public static final String Uber = "com.ubercab.driver";
        public static final String WingZ = "com.tickengo";
    }

    public static class TNCIds
    {
        public static String FARE = "57ac8e2cd00bd223c0561cfb";
        public static String Fasten = "57ac8e9cd00bd323c0c0fafc";
        public static String GetMe = "57ac8ee2d00bd423c0726b2e";
        public static String InstaRyde = "57ac8f0ed00bd523c0f27f98";
        public static String Lyft = "57ac8f50d00bd623c0e747ae";
        public static String RideAustin = "57ac8f7ed00bd723c027441e";
        public static String ScoopMe = "57ac8faad00bd823c0220909";
        public static String Tride = "57b214048ef99a0fd883005c";
        public static String Uber = "57ac8fd2d00bd923c04d76bb";
        public static String WingZ = "57b107d68ef99a0fd883004f";
    }

    public static class AdminInfo
    {
        public static String Name = "DriveSwitch Administrator";
        public static String Email = "admin@driveswitch.com";
    }
}
