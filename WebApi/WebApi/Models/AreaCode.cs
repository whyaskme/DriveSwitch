using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace WebApi.Models
{
    public class AreaCode : Base
    {
        public AreaCode()
        {
            _t = "AreaCode";

            AreaCodeNumber = 000;

            CountryId = ObjectId.Empty;
            StateId = ObjectId.Empty;
            CountyId = ObjectId.Empty;
            CityId = ObjectId.Empty;
            TimeZoneId = ObjectId.Empty;

            Longitude = 0.00;
            Latitude = 0.00;
        }
        public Int16 AreaCodeNumber { get; set; }
        public ObjectId CountryId { get; set; }
        public ObjectId StateId { get; set; }
        public ObjectId CountyId { get; set; }
        public ObjectId CityId { get; set; }
        public ObjectId TimeZoneId { get; set; }
        public Double Longitude { get; set; }
        public Double Latitude { get; set; }
        public Int32 EstimatedPopulation { get; set; }
    }
}