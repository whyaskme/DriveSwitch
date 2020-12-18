using System;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Data.Repositories.Models
{
    public static class Constants
    {
        // Default TypeIds
        public static Int16 DefaultCountryType = 1;
        public static Int16 DefaultStateType = 2;
        public static Int16 DefaultCountyType = 3;
        public static Int16 DefaultCityType = 4;

        public static string DisabledColor = "#ed1c24";
        public static string EnabledColor = "#007236";

        // Default Type values
        public static ObjectId EmptyObjectId = ObjectId.Parse("000000000000000000000000");

        public static ObjectId DefaultCountryId = ObjectId.Parse("57acbfeea388c60af8d4965a");
        public static ObjectId DefaultRegionId = ObjectId.Parse("000000000000000000000001");
        public static ObjectId DefaultStateId = ObjectId.Parse("000000000000000000000002");
        public static ObjectId DefaultCountyId = ObjectId.Parse("000000000000000000000003");
        public static ObjectId DefaultCityId = ObjectId.Parse("000000000000000000000004");
        public static ObjectId DefaultTimeZoneId = ObjectId.Parse("000000000000000000000005");

        public static ObjectId EncryptionKey = DefaultCountryId;

        //public static Tuple<int, string, string> Created = new Tuple<int, string, string>(AdvertisementStartRange, "Advertisement (Created)", "[" + TokenKeys.UserRole.Replace(TokenKeys.ItemSep, "").Replace(TokenKeys.KVSep, "") + "] account for ([" + TokenKeys.UserFullName.Replace(TokenKeys.ItemSep, "").Replace(TokenKeys.KVSep, "") + "]) Created by ([" + TokenKeys.UpdatedByLoggedinAdminFullName.Replace(TokenKeys.ItemSep, "").Replace(TokenKeys.KVSep, "") + "]).");

        public static Tuple<int, string, string> Base = new Tuple<int, string, string>(EventStartRange, "Event: Base", "Base Assignments Event Class");
        public static Tuple<int, string, string> Exception = new Tuple<int, string, string>(ExceptionStartRange, "Exception: Error", "Exceptions Event Class");

        #region TokenKeys

            public static class TokenKeys
            {
                public const string ItemSep = "|";          // Pipe(|) used as item seperator
                public const string KVSep = ":";            // Colon(:) used as Key/value seperator

                public static string UpdatedByLoggedinAdminFullName = ItemSep + "UpdatedByLoggedinAdminFullName" + KVSep;
                public static string UpdatedValues = ItemSep + "UpdatedValues" + KVSep;
                public static string UserFullName = ItemSep + "UserFullName" + KVSep;
                public static string UserName = ItemSep + "UserName" + KVSep;
                public static string UserRole = ItemSep + "UserRole" + KVSep;
            }

        #endregion

        #region Event class range initialization

            public const int EventStartRange = 0;
            public const int AccountStartRange = 1000;
            public const int GPSStartRange = 2000;
            public const int TNCStartRange = 3000;
            public const int ReferenceStartRange = 4000;
            public const int ExceptionStartRange = 5000;

        #endregion

        #region References

        public static class Reference
        {
            // ReferenceTypes
            public static Int16 TypeProcess = 0;
            public static Int16 TypeUser = 1;
        }

        #endregion

        #region UserRoles

        public static class UserRoles
        {
            public static Tuple<ObjectId, string> SystemAdministrator = new Tuple<ObjectId, string>(ObjectId.Parse("57be0508d00bc528b4bfb0f9"), "System Administrator");
            public static Tuple<ObjectId, string> SiteAdministrator = new Tuple<ObjectId, string>(ObjectId.Parse("57be0527d00bc528b4bfb0fa"), "Site Administrator");
            public static Tuple<ObjectId, string> GroupAdministrator = new Tuple<ObjectId, string>(ObjectId.Parse("57be052dd00bc528b4bfb0fb"), "Group Administrator");
            public static Tuple<ObjectId, string> Consumer = new Tuple<ObjectId, string>(ObjectId.Parse("57be0533d00bc528b4bfb0fc"), "Consumer");
        }

        #endregion

        #region Transactions

            public static class Transaction
            {
                public static class CreditCard
                {
                    public static Tuple<ObjectId, string> Unknown = new Tuple<ObjectId, string>(ObjectId.Parse("567b0b9e3ee6522c6c863934"), "Unknown");
                    public static Tuple<ObjectId, string> MasterCard = new Tuple<ObjectId, string>(ObjectId.Parse("567b0ba63ee6522c6c863936"), "MasterCard");
                    public static Tuple<ObjectId, string> VISA = new Tuple<ObjectId, string>(ObjectId.Parse("567b0ba83ee6522c6c863938"), "VISA");
                    public static Tuple<ObjectId, string> Amex = new Tuple<ObjectId, string>(ObjectId.Parse("567b0bac3ee6522c6c86393a"), "Amex");
                    public static Tuple<ObjectId, string> Discover = new Tuple<ObjectId, string>(ObjectId.Parse("567b0bac3ee6522c6c86393c"), "Discover");
                    public static Tuple<ObjectId, string> DinersClub = new Tuple<ObjectId, string>(ObjectId.Parse("567b0bac3ee6522c6c86393e"), "DinersClub");
                    public static Tuple<ObjectId, string> JCB = new Tuple<ObjectId, string>(ObjectId.Parse("567b0bac3ee6522c6c863940"), "JCB");
                    public static Tuple<ObjectId, string> enRoute = new Tuple<ObjectId, string>(ObjectId.Parse("567b0bac3ee6522c6c863942"), "enRoute");
                }

                public static class PaymentProcessor
                {
                    public static Tuple<ObjectId, string> PayPal = new Tuple<ObjectId, string>(ObjectId.Parse("5878001fefd7d7e0fb69aeb5"), "PayPal");
                }

                public static class RenewalPeriod
                {
                    public static Tuple<Int16, string> Monthly = new Tuple<Int16, string>(30, "Monthly");
                    public static Tuple<Int16, string> Quarterly = new Tuple<Int16, string>(90, "Quarterly");
                    public static Tuple<Int16, string> Annually = new Tuple<Int16, string>(365, "Annually");
                }
            }

        #endregion

        #region Messaging

        public static class Messaging
        {
            public static class Status
            {
                public static Int16 None = 0;
                public static Int16 Recieved = 1;
                public static Int16 Sent = 2;
                public static Int16 Replied = 3;
            }
        }

        #endregion

        #region EventLog

        public static class Event
        {
            public static Tuple<int, string, string> Generic = new Tuple<int, string, string>(EventStartRange + 1, "Event: Generic", "Generic Event Class");

            public static class Account
            {
                public static Tuple<int, string> Created = new Tuple<int, string>(AccountStartRange, "Account: Created");
                public static Tuple<int, string> Updated = new Tuple<int, string>(Created.Item1 + 1, "Account: Updated");
                public static Tuple<int, string> Deleted = new Tuple<int, string>(Updated.Item1 + 1, "Account: Deleted");
                public static Tuple<int, string> Disabled = new Tuple<int, string>(Deleted.Item1 + 1, "Account: Disabled");
                public static Tuple<int, string> Enabled = new Tuple<int, string>(Disabled.Item1 + 1, "Account: Enabled");
                public static Tuple<int, string> Flagged = new Tuple<int, string>(Enabled.Item1 + 1, "Account: Flagged");
                public static Tuple<int, string> LoggedIn = new Tuple<int, string>(Flagged.Item1 + 1, "Account: LogIn");
                public static Tuple<int, string> LoggedOut = new Tuple<int, string>(LoggedIn.Item1 + 1, "Account: LogOut");
                public static Tuple<int, string> FailedLoginBadUserName = new Tuple<int, string>(LoggedOut.Item1 + 1, "Account: LogIn Failed - Bad Username");
                public static Tuple<int, string> FailedLoginBadPwd = new Tuple<int, string>(FailedLoginBadUserName.Item1 + 1, "Account: LogIn Failed - Bad Pwd");
                public static Tuple<int, string> RandomUserGenerated = new Tuple<int, string>(FailedLoginBadPwd.Item1 + 1, "Account: Random Generation");
                public static Tuple<int, string> SubscriptionCurrent = new Tuple<int, string>(RandomUserGenerated.Item1 + 1, "Account: Subscription Current");
                public static Tuple<int, string> SubscriptionExpired = new Tuple<int, string>(SubscriptionCurrent.Item1 + 1, "Account: Subscription Expired");
                public static Tuple<int, string> DisabledAccountAccess = new Tuple<int, string>(SubscriptionExpired.Item1 + 1, "Account: Disabled Attempt");
        }

        public static class UserActions
        {
            // UI account actions
            public static Tuple<int, string> ViewPersonalSettings = new Tuple<int, string>(Account.DisabledAccountAccess.Item1, "Account: View Personal Settings");
            public static Tuple<int, string> ViewAddressSettings = new Tuple<int, string>(ViewPersonalSettings.Item1 + 1, "Account: View Address Settings");
            public static Tuple<int, string> ViewRideshareSettings = new Tuple<int, string>(ViewAddressSettings.Item1 + 1, "Account: View Rideshare Settings");
            public static Tuple<int, string> ViewEventHistory = new Tuple<int, string>(ViewRideshareSettings.Item1 + 1, "Account: View Event History");
            public static Tuple<int, string> ViewSwitchboard = new Tuple<int, string>(ViewEventHistory.Item1 + 1, "Account: View Switchboard");
            public static Tuple<int, string> ViewSupport = new Tuple<int, string>(ViewSwitchboard.Item1 + 1, "Account: View Support");
        }

        public static class TNC
        {
            public static Tuple<int, string> Created = new Tuple<int, string>(TNCStartRange, "TNC: Created");
            public static Tuple<int, string> Updated = new Tuple<int, string>(Created.Item1 + 1, "TNC: Updated");
            public static Tuple<int, string> Deleted = new Tuple<int, string>(Updated.Item1 + 1, "TNC: Deleted");
            public static Tuple<int, string> Disabled = new Tuple<int, string>(Deleted.Item1 + 1, "TNC: Disabled");
            public static Tuple<int, string> Enabled = new Tuple<int, string>(Disabled.Item1 + 1, "TNC: Enabled");
            public static Tuple<int, string> Flagged = new Tuple<int, string>(Enabled.Item1 + 1, "TNC: Flagged");

            // User TNC actions
            public static Tuple<int, string> RideRequestAccepted = new Tuple<int, string>(Flagged.Item1 + 1, "Ride Request: Accepted");
            public static Tuple<int, string> RideRequestIgnored = new Tuple<int, string>(RideRequestAccepted.Item1 + 1, "Ride Request: Ignored");
            public static Tuple<int, string> RideRequestRejected = new Tuple<int, string>(RideRequestIgnored.Item1 + 1, "Ride Request: Rejected");
        }

        public static class GPS
        {
            public static Tuple<int, string> Report = new Tuple<int, string>(GPSStartRange, "Location");
        }
    }

    #endregion
    }
}
