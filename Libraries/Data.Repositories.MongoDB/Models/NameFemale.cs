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
    public class NameFemale
    {
        public NameFemale()
        {
            _id = ObjectId.GenerateNewId();
            _t = "NameFemale";
            Name = "";
        }
        public ObjectId _id { get; set; }
        public string _t { get; set; }
        public string Name { get; set; }
    }
}