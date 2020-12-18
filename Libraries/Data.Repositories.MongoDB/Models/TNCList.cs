using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Data.Repositories.Models
{
    public class TNCList
    {
        public TNCList()
        {
            _id = ObjectId.GenerateNewId();
            Name = "";
        }

        public ObjectId _id { get; set; }
        public String Name { get; set; }
    }
}