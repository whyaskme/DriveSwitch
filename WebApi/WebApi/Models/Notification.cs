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
    public class Notification : Base
    {
        public Notification(Double latitude, Double longitude)
        {
            _t = "Notification";

            UserId = ObjectId.Empty;
            Name = "";
            DateTime = DateTime.UtcNow;
            PackageName = "";
            NotificationTicker = "";
            NotificationTitle = "";
            NotificationText = "";

            Location = new Location(UserId, ObjectId.Empty, "", latitude, longitude);
        }
        public ObjectId UserId { get; set; }
        public DateTime DateTime { get; set; }
        public string PackageName { get; set; }
        public string NotificationTicker { get; set; }
        public string NotificationTitle { get; set; }
        public string NotificationText { get; set; }
        public Location Location { get; set; }
    }
}