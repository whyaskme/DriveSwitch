using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace WebApi.Models
{
    public class County : Base
    {
        public County()
        {
            _t = "County";

            State = "";
            StateId = ObjectId.Empty;
            TimeZoneId = ObjectId.Empty;

            EstimatedPopulation = 0;
        }

        public string State { get; set; }
        public ObjectId StateId { get; set; }
        public ObjectId TimeZoneId { get; set; }
        public Int32 EstimatedPopulation { get; set; }
    }
}