using System;
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

namespace WebApi.Models
{
    public class City : Base
    {
        public City()
        {
            _t = "City";

            CountryId = ObjectId.Empty;
            TimeZoneId = ObjectId.Empty;
            StateId = ObjectId.Empty;
            CountyId = ObjectId.Empty;
        }

        public City(string name)
        {
            Utils _utils = new Utils();
            City _myCity = _utils.GetCityByName(name);

            _t = _myCity._t;
            Name = _myCity.Name;
            CountryId = _myCity.CountryId;
            TimeZoneId = _myCity.TimeZoneId;
            StateId = _myCity.StateId;
            CountyId = _myCity.CountyId;
            EstimatedPopulation = _myCity.EstimatedPopulation;
        }

        public City(Int32 zipCode)
        {
            Utils _utils = new Utils();
            City _myCity = _utils.GetCityByZipCode(zipCode);

            _t = _myCity._t;
            Name = _myCity.Name;
            CountryId = _myCity.CountryId;
            TimeZoneId = _myCity.TimeZoneId;
            StateId = _myCity.StateId;
            CountyId = _myCity.CountyId;
            EstimatedPopulation = _myCity.EstimatedPopulation;
        }

        public ObjectId CountryId { get; set; }
        public ObjectId StateId { get; set; }
        public ObjectId CountyId { get; set; }
        public ObjectId TimeZoneId { get; set; }
        public Int32 EstimatedPopulation { get; set; }
    }
}
