using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Data.Repositories.Models
{
    public class ContactInfo
    {
        public ContactInfo()
        {
            Address = new Address();
            Email = new Email();
            Phone = new Phone();
        }

        public Address Address { get; set; }
        public Email Email { get; set; }
        public Phone Phone { get; set; }
    }
}