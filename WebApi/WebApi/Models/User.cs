using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace WebApi.Models
{
    public class User : Base
    {
        public User()
        {
            _t = "User";
            _id = ObjectId.Empty;

            RegistrationDate = DateTime.UtcNow;
            Expired = false;
            ExpireDate = DateTime.UtcNow;

            DeviceType = 0;

            IsLoggedIn = false;

            FirstName = "";
            LastName = "";

            Pwd = "";

            Gender = 0;

            Contact = new ContactInfo();

            TNCs = new List<TNCList>();

            Roles = new List<UserRole>();

            var newUserRole = new UserRole(Constants.UserRoles.Consumer.Item2);
            Roles.Add(newUserRole);

            CreditCards = new List<CreditCard>();
            Transactions = new List<Transaction>();
        }

        public User(string userRole)
        {
            _t = "User";

            RegistrationDate = DateTime.UtcNow;
            Expired = false;
            ExpireDate = DateTime.UtcNow;

            DeviceType = 0;
            IsLoggedIn = false;

            FirstName = "";
            LastName = "";

            Pwd = "";

            Gender = 0;

            Contact = new ContactInfo();

            TNCs = new List<TNCList>();

            Roles = new List<UserRole>();

            var newUserRole = new UserRole(userRole);
            Roles.Add(newUserRole);

            CreditCards = new List<CreditCard>();
            Transactions = new List<Transaction>();
        }
        public DateTime RegistrationDate { get; set; }
        public Boolean Expired { get; set; }
        public DateTime ExpireDate { get; set; }
        public Int16 DeviceType { get; set; } // Android (Phone) = 1, Android (Tablet) = 2, IOS (Phone) - 3, IOS (Tablet) - 4
        public bool IsLoggedIn { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Pwd { get; set; }
        public int Gender { get; set; } // 0 = Not specified, Female = 1, Male = 2
        public ContactInfo Contact { get; set; }
        public List<TNCList> TNCs { get; set; }
        public List<UserRole> Roles { get; set; }
        public List<CreditCard> CreditCards { get; set; }
        public List<Transaction> Transactions { get; set; }
    }
}