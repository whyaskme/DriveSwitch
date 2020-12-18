using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Data.Repositories.Models
{
    public class Location
    {
        public Location(ObjectId referenceId, ObjectId tncId, String uiScreen, Double latitude, Double longitude)
        {
            DateTime = DateTime.UtcNow;
            UIScreen = uiScreen;
            UserId = referenceId;
            TNCId = tncId;
            Longitude = longitude;
            Latitude = latitude;
        }

        public DateTime DateTime { get; set; }
        public String UIScreen { get; set; }
        public ObjectId UserId { get; set; }
        public ObjectId TNCId { get; set; }
        public Double Latitude { get; set; }
        public Double Longitude { get; set; }
    }
}