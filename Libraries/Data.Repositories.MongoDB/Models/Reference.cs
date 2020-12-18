using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Data.Repositories.Models
{
    public class Reference
    {
        public Reference(ObjectId referenceId, Int16 referenceType)
        {
            ReferenceId = referenceId;
            ReferenceType = referenceType;
            //Name = "";
            //Details = "";
        }
        public ObjectId ReferenceId { get; set; }
        public Int16 ReferenceType { get; set; }
        //public string Name { get; set; }
        //public string Details { get; set; }
    }
}