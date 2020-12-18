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
    /// <summary>
    /// 
    /// </summary>
    public class Base
    {

        /// <summary>
        /// 
        /// </summary>
        public Base()
        {
            _id = ObjectId.GenerateNewId();
            _t = "Base";

            Enabled = true;

            Name = "";
        }
        /// <summary>
        /// 
        /// </summary>
        public ObjectId _id { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string _t { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public bool Enabled { get; set; }
        public string Name { get; set; }
    }
}