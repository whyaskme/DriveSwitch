using System;
using System.Collections.Generic;
using System.Configuration;
using System.Diagnostics;
using System.Globalization;
using System.Net;
using System.Net.Mail;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using System.Web;
using System.Xml;

using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Core;

namespace Data.Repositories.Models
{
    public class NameLast
    {
        public NameLast()
        {
            _id = ObjectId.GenerateNewId();
            _t = "NameLast";
            Name = "";
        }
        public ObjectId _id { get; set; }
        public string _t { get; set; }
        public string Name { get; set; }
    }
}