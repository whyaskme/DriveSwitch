using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.Net;
using System.Net.Mail;
using System.Text.RegularExpressions;
using System.Web;
using System.Xml;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Data.Repositories.Models
{
    public class State : Base
    {
        public State()
        {
            _t = "State";

            Abbr = "";

            CountryId = ObjectId.Empty;
            TimeZoneId = ObjectId.Empty;

            EstimatedPopulation = 0;
        }

        public string Abbr { get; set;}
        public ObjectId CountryId { get; set; }
        public ObjectId TimeZoneId { get; set; }
        public Int32 EstimatedPopulation { get; set; }
    }
}
