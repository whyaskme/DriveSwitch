using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace WebApi.Models
{
    public class Phone
    {
        public Phone()
        {
            PhoneType = 0; // 0=Mobile, 1=Home, 2=Work, 3=Fax
            CountryCode = "1"; // 1 = United States
            AreaCode = 000;
            Exchange = "000";
            Number = "0000";
        }

        public int PhoneType { get; set; }
        public string CountryCode { get; set; }
        public int AreaCode { get; set; }
        public string Exchange { get; set; }
        public string Number { get; set; }
    }
}