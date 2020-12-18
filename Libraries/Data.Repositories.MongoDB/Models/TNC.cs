using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Data.Repositories.Models
{
    public class TNC : Base
    {
        public TNC()
        {
            _t = "TNC";

            Contact = new ContactInfo();
            Url = "";
            LogoUrl = "";
        }
        public ContactInfo Contact { get; set; }
        public string Url { get; set; }
        public string LogoUrl { get; set; }
    }
}