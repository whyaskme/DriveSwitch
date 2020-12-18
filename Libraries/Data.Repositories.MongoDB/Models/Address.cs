using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Data.Repositories.Models
{
    public class Address
    {
        public Address()
        {
            CountryId = Constants.DefaultCountryId;
            StateId = ObjectId.Empty;
            CountyId = ObjectId.Empty;
            CityId = ObjectId.Empty;
            ZipCode = 00000;
            TimeZoneId = ObjectId.Empty;
            Address1 = "";
            Address2 = "";
        }
        public ObjectId CountryId { get; set; }
        public ObjectId StateId { get; set; }
        public ObjectId CountyId { get; set; }
        public ObjectId CityId { get; set; }
        public Int32 ZipCode { get; set; }
        public ObjectId TimeZoneId { get; set; }
        public string Address1 { get; set; }
        public string Address2 { get; set; }
    }
}