using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Data.Repositories.Models
{
    public class Email
    {
        public Email()
        {
            UserName = "admin";
            Domain = "@driveswitch.com";
        }
        public string UserName { get; set; }
        public string Domain { get; set; }
    }
}