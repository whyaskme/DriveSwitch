using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Data.Repositories.Models
{
    public class RenewalPeriod
    {
        public RenewalPeriod()
        {
            _id = ObjectId.GenerateNewId();
            _t = "RenewalPeriod";

            Name = "";
            Period = 0;
            DiscountPercent = 0.00;
            Amount = 0.00;
        }

        public ObjectId _id { get; set; }
        public string _t { get; set; }
        public Int16 Period { get; set; }
        public Double DiscountPercent { get; set; }
        public string Name { get; set; }
        public Double Amount { get; set; }
    }
}