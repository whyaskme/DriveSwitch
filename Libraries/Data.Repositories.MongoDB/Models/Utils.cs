using System;
using System.Collections.Generic;
using System.Configuration;
using System.Diagnostics;
using System.Globalization;
using System.Net;
using System.Net.Mail;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using System.Web;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Xml;

using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Core;

namespace Data.Repositories.Models
{
    public class Utils
    {
        string cookieDomain = "";
        public Utils()
        {
            cookieDomain = HttpContext.Current.Request.ServerVariables["SERVER_NAME"];
        }

        int PwdLength = Convert.ToInt16(ConfigurationManager.AppSettings["PwdLength"]);
        int PwdNumberOfNonAlphanumericCharacters = Convert.ToInt16(ConfigurationManager.AppSettings["PwdNumberOfNonAlphanumericCharacters"]);

        public string EncryptString(string stringToEncrypt, string encryptionKey)
        {
            var encryptedString = Security.EncryptAndEncode(stringToEncrypt, encryptionKey);

            return encryptedString;
        }

        public string DecryptString(string stringToDecrypt, string decryptionKey)
        {
            var decryptedString = Security.DecodeAndDecrypt(stringToDecrypt, decryptionKey);

            return decryptedString;
        }

        //public User CreateRandomUser(string Latitude, string Longitude)
        //{
        //    try
        //    {
        //        User _randomUser = new User(Constants.UserRoles.Consumer.Item2);

        //        Random _random = new Random();
        //        var _randomNumber = 0;

        //        var _femaleNamesCount = 4275; // _mongoFemaleNameCollection.Find<NameFemale>(s => s._t == "NameFemale").ToListAsync<NameFemale>().Result.Count;
        //        var _maleNamesCount = 1219; // _mongoFemaleNameCollection.Find<NameFemale>(s => s._t == "NameFemale").ToListAsync<NameFemale>().Result.Count;
        //        var _lastNamesCount = 88799; // _mongoLastNameCollection.Find<NameLast>(s => s._t == "NameLast").ToListAsync<NameLast>().Result.Count;
        //        var _streetNamesCount = 91670; // _mongoFemaleNameCollection.Find<NameFemale>(s => s._t == "NameFemale").ToListAsync<NameFemale>().Result.Count;

        //        _randomUser.DeviceType = Convert.ToInt16(_random.Next(1, 5));

        //        _randomUser.Gender = _random.Next(1, 3);
        //        if (_randomUser.Gender == 1) // Get Female name
        //        {
        //            _mongoFemaleNameCollection = _mongoDatabase.GetCollection<NameFemale>("Names");

        //            _randomNumber = _random.Next(0, _femaleNamesCount);

        //            var _femaleNames = _mongoFemaleNameCollection.Find<NameFemale>(s => s._t == "NameFemale").Limit(1).Skip(_randomNumber).ToListAsync<NameFemale>().Result;
        //            foreach (NameFemale _femaleName in _femaleNames)
        //            {
        //                _randomUser.FirstName = UppercaseFirstLetter(_femaleName.Name);
        //            }
        //        }
        //        else // Get Male name
        //        {
        //            _mongoMaleNameCollection = _mongoDatabase.GetCollection<NameMale>("Names");

        //            _randomNumber = _random.Next(0, _maleNamesCount);

        //            var _maleNames = _mongoMaleNameCollection.Find<NameMale>(s => s._t == "NameMale").Limit(1).Skip(_randomNumber).ToListAsync<NameMale>().Result;
        //            foreach (NameMale _maleName in _maleNames)
        //            {
        //                _randomUser.FirstName = UppercaseFirstLetter(_maleName.Name);
        //            }
        //        }

        //        // Get Last name
        //        _mongoLastNameCollection = _mongoDatabase.GetCollection<NameLast>("Names");

        //        _randomNumber = _random.Next(0, _lastNamesCount);

        //        var _lastNames = _mongoLastNameCollection.Find<NameLast>(s => s._t == "NameLast").Limit(1).Skip(_randomNumber).ToListAsync<NameLast>().Result;
        //        foreach (NameLast _lastName in _lastNames)
        //        {
        //            _randomUser.LastName = UppercaseFirstLetter(_lastName.Name);
        //        }

        //        Email _randomEmail = new Email();
        //        _randomEmail.UserName = _randomUser.FirstName.ToLower() + "." + _randomUser.LastName.ToLower();
        //        _randomEmail.Domain = "gmail.com";
        //        _randomUser.Contact.Email = _randomEmail;

        //        _mongoStreetNameCollection = _mongoDatabase.GetCollection<NameStreet>("Names");

        //        _randomNumber = _random.Next(1, _streetNamesCount);

        //        var _streetNames = _mongoStreetNameCollection.Find<NameStreet>(s => s._t == "NameStreet").Limit(1).Skip(_randomNumber).ToListAsync<NameStreet>().Result;
        //        foreach (NameStreet _streetName in _streetNames)
        //        {
        //            _randomNumber = _random.Next(1, 9999);
        //            _randomUser.Contact.Address.Address1 = _randomNumber + " " + _streetName.Name;
        //        }
        //        _randomUser.Contact.Address.Address2 = "";

        //        _randomUser.Contact.Address.CountryId = Constants.DefaultCountryId;

        //        // Get random State
        //        _randomNumber = _random.Next(1, 50);
        //        _mongoStateCollection = _mongoDatabase.GetCollection<State>("States");
        //        var _randomStates = _mongoStateCollection.Find<State>(s => s._t == "State").Limit(1).Skip(_randomNumber).ToListAsync<State>().Result;
        //        foreach (State _state in _randomStates)
        //        {
        //            _randomUser.Contact.Address.StateId = _state._id;
        //        }

        //        // Total County count for random State
        //        _mongoCountyCollection = _mongoDatabase.GetCollection<County>("Counties");
        //        var _randomCountyCount = _mongoCountyCollection.Find<County>(s => s.StateId == _randomUser.Contact.Address.StateId).ToListAsync<County>().Result.Count;

        //        // Get random County
        //        _randomNumber = _random.Next(1, _randomCountyCount);
        //        var _randomCounties = _mongoCountyCollection.Find<County>(s => s.StateId == _randomUser.Contact.Address.StateId).Limit(1).Skip(_randomNumber).ToListAsync<County>().Result;
        //        foreach (County _county in _randomCounties)
        //        {
        //            _randomUser.Contact.Address.CountyId = _county._id;
        //        }

        //        // Total City count for random County
        //        _mongoCityCollection = _mongoDatabase.GetCollection<City>("Cities");
        //        var _randomCityCount = _mongoCityCollection.Find<City>(s => s.CountyId == _randomUser.Contact.Address.CountyId).ToListAsync<City>().Result.Count;

        //        // Get random City
        //        _randomNumber = _random.Next(1, _randomCityCount);
        //        var _randomCities = _mongoCityCollection.Find<City>(s => s.CountyId == _randomUser.Contact.Address.CountyId).Limit(1).Skip(_randomNumber).ToListAsync<City>().Result;
        //        foreach (City _city in _randomCities)
        //        {
        //            _randomUser.Contact.Address.CityId = _city._id;
        //        }

        //        // Get AreaCode by CityId
        //        var _randomAreaCode = 000;
        //        _mongoAreaCodeCollection = _mongoDatabase.GetCollection<AreaCode>("AreaCodes");
        //        var _areaCodes = _mongoAreaCodeCollection.Find<AreaCode>(s => s.CityId == _randomUser.Contact.Address.CityId).ToListAsync<AreaCode>().Result;
        //        if (_areaCodes.Count > 0)
        //        {
        //            foreach(AreaCode _areaCode in _areaCodes)
        //            {
        //                _randomAreaCode = _areaCode.AreaCodeNumber;
        //            }
        //        }

        //        Phone _randomPhone = new Phone();
        //        _randomPhone.CountryCode = "1"; // United States
        //        _randomPhone.AreaCode = _randomAreaCode;
        //        _randomPhone.Exchange = _random.Next(555, 999).ToString();
        //        _randomPhone.Number = _random.Next(1000, 9999).ToString();
        //        _randomUser.Contact.Phone = _randomPhone;

        //        // Total ZipCode count for random City
        //        _mongoZipCodeCollection = _mongoDatabase.GetCollection<ZipCode>("ZipCodes");
        //        var _randomZipCodeCount = _mongoZipCodeCollection.Find<ZipCode>(s => s.CityId == _randomUser.Contact.Address.CityId).ToListAsync<ZipCode>().Result.Count;

        //        // Get random ZipCode
        //        _randomNumber = _random.Next(1, _randomZipCodeCount);

        //        // If we don't set to 0 when count is 1 || 0, it returns nothing
        //        if (_randomNumber < 2)
        //            _randomNumber = 0;

        //        double randomLatitude = 0;
        //        double randomLongitude = 0;

        //        var _randomZipCodes = _mongoZipCodeCollection.Find<ZipCode>(s => s.CityId == _randomUser.Contact.Address.CityId).Limit(1).Skip(_randomNumber).ToListAsync<ZipCode>().Result;
        //        foreach (ZipCode _zipCode in _randomZipCodes)
        //        {
        //            _randomUser.Contact.Address.ZipCode = _zipCode.Zip;
        //            _randomUser.Contact.Address.TimeZoneId = _zipCode.TimeZoneId;

        //            randomLatitude = _zipCode.Latitude;
        //            randomLongitude = _zipCode.Longitude;

        //            // This is used on the client side for location settings in hidden fields
        //            //Event randomEvent = new Event(randomLatitude, randomLongitude);
        //            //randomEvent.TypeId = Constants.Event.Account.RandomUserGenerated.Item1;
        //            //randomEvent.Name = Constants.Event.Account.RandomUserGenerated.Item2;
        //            //_randomUser.EventHistory.Add(randomEvent);
        //        }

        //        //_randomUser.Pwd = GenerateRandomPassword(PwdLength, PwdNumberOfNonAlphanumericCharacters).Trim();

        //        _randomUser.Pwd = RandomAlphaNumericString().ToLower();

        //        // Get and set random TNCs
        //        _mongoTNCCollection = _mongoDatabase.GetCollection<TNC>("TNCs");
        //        var _tncCount = _mongoTNCCollection.Find<TNC>(s => s.Enabled == true).ToListAsync<TNC>().Result.Count;
        //        var _tncs = _mongoTNCCollection.Find<TNC>(s => s.Enabled == true).ToListAsync<TNC>().Result;
        //        foreach (TNC _tnc in _tncs)
        //        {
        //            _randomNumber = _random.Next(1, _tncCount);

        //            if(_randomNumber < 5)
        //            {
        //                TNCList _tncItem = new TNCList();
        //                _tncItem._id = _tnc._id;
        //                _tncItem.Name = _tnc.Name;

        //                _randomUser.TNCs.Add(_tncItem);
        //            }
        //        }

        //        _randomUser.IsLoggedIn = true;

        //        // Insert User in DB
        //        // Don't do this now. Just return User to calling UI and let the client register
        //        var registrationResponse = CreateUser(_randomUser, randomLatitude, randomLongitude);

        //        return _randomUser;
        //    }
        //    catch(Exception ex)
        //    {
        //        var errMsg = ex.ToString();
        //    }

        //    return null;
        //}

        public string RandomAlphaNumericString()
        {
            var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            var stringChars = new char[8];
            var random = new Random();

            for (int i = 0; i < stringChars.Length; i++)
            {
                stringChars[i] = chars[random.Next(chars.Length)];
            }

            var finalString = new String(stringChars);

            return finalString;
        }

        public string GenerateRandomPassword(int length, int numberOfNonAlphanumericCharacters)
        {
            return System.Web.Security.Membership.GeneratePassword(length, numberOfNonAlphanumericCharacters).ToString();
        }

        public string GeoLocationByUserIp(string userIp)
        {
            var stateName = "Unknown";
            var cityName = "Unknown";
            var zipCode = "Unknown";

            try
            {
                var request = "http://freegeoip.net/xml/" + userIp;
                var webRequest = WebRequest.Create(request);
                webRequest.Method = "GET";

                var res = webRequest.GetResponse();
                var response = res.GetResponseStream();
                var xmlDoc = new XmlDocument();
                if (response != null)
                {
                    xmlDoc.Load(response);

                    stateName = xmlDoc.ChildNodes[1].ChildNodes[4].InnerText;
                    cityName = xmlDoc.ChildNodes[1].ChildNodes[5].InnerText;
                    zipCode = xmlDoc.ChildNodes[1].ChildNodes[6].InnerText;
                }

                return cityName + ", " + stateName + " " + zipCode;
            }
            catch (Exception ex)
            {
                return "Error: GeoLocationByUserIp(" + userIp + ") " + ex.Message;
            }
        }

        #region String functions

        public bool IsValueObjectId(string queryValue)
        {
            try
            {
                ObjectId.Parse(queryValue);
                return true;
            }
            catch
            {
                return false;
            }
        }

        public bool HasLowerCase(string evalString)
        {
            return !string.IsNullOrEmpty(evalString) && Regex.IsMatch(evalString, "[a-z]");
        }

        public bool HasUpperCase(string evalString)
        {
            return !string.IsNullOrEmpty(evalString) && Regex.IsMatch(evalString, "[A-Z]");
        }

        public bool HasNumeric(string evalString)
        {
            return !string.IsNullOrEmpty(evalString) && Regex.IsMatch(evalString, "[0-9]");
        }

        public string FormatNumber(string formatValue)
        {
            int inputNumber = Convert.ToInt32(formatValue);
            var formattedNumber = String.Format("{0:##,####,####}", inputNumber);

            return formattedNumber;
        }

        public string SanitizeString(string response)
        {
            var cleanedString = response;
            cleanedString = cleanedString.Replace(" & ", " and ");
            cleanedString = cleanedString.Replace("'", "&apos;");

            return cleanedString;
        }

        public string SanitizeXmlString(string response)
        {
            var cleanedString = response;
            cleanedString = cleanedString.Replace("&", "&amp;").Replace(Environment.NewLine, "");
            return cleanedString;
        }

        public bool Contains(string source, string toCheck, StringComparison comp)
        {
            return source.IndexOf(toCheck, comp) >= 0;
        }

        #endregion

        #region Cookie methods

        public void CreateCookie(string cookieName, string cookieValue, DateTime cookieExpires)
        {
            var myCookie = new HttpCookie(cookieName) { Value = cookieValue, Expires = cookieExpires };

            myCookie.Domain = cookieDomain;

            HttpContext.Current.Response.Cookies.Add(myCookie);
        }

        public string ReadCookie(string cookieName)
        {
            var myCookie = HttpContext.Current.Request.Cookies[cookieName];
            Debug.Assert(condition: myCookie != null, message: "myCookie != null");
            return myCookie.Value;
        }

        public void UpdateCookie()
        {
        }

        public void DeleteCookie(string cookieName)
        {
            HttpContext.Current.Response.Cookies.Remove(cookieName);
        }

        #endregion
    }
}