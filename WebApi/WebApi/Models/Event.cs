using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace WebApi.Models
{
    public class Event : Base
    {
        public Event(Double latitude, Double longitude)
        {
            _t = "Event";

            Name = "";
            TypeId = 0;
            DateTime = DateTime.UtcNow;

            Reference = new Reference(ObjectId.Empty, 0);
            Details = "None";

            Location = new Location(ObjectId.Empty, ObjectId.Empty, "", latitude, longitude);
        }
        public int TypeId { get; set; }
        public DateTime DateTime { get; set; }
        public Reference Reference { get; set; }
        public string Details { get; set; }
        public Location Location { get; set; }
    }
}