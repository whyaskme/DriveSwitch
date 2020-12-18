using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;
//using MongoDB.Driver.Builders;

namespace WebApi.Models
{
    public class Country : Base
    {
        public Country()
        {
            _t = "Country";

            Abbr = "";
            TimeZoneId = ObjectId.Empty;
            CountryCodeUrl = "https://countrycode.org/";
            InternationalDialingPrefix = "";
            CountryCallingCode = "1"; // 1 = United States
            ISOCode = "";
            AreaKm2 = 0;
        }
        public string Abbr { get; set; }
        public Int32 EstimatedPopulation { get; set; }
        public ObjectId TimeZoneId { get; set; }
        public string CountryCodeUrl { get; set; }
        public string InternationalDialingPrefix { get; set; }
        public string CountryCallingCode { get; set; }
        public string ISOCode { get; set; }
        public Int32 AreaKm2 { get; set; }
    }
}