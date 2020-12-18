using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using System.Web.Http;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;

using Newtonsoft;
using Newtonsoft.Json;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

using Data.Repositories.Models;
using Data.Repositories.MongoDB;

namespace WebApi.Controllers
{
    /// <summary>
    /// User account operations REQUIRE GPS coordinates: latitude and longitude as input parameters for logging purposes.
    /// </summary>
    /// <returns>JSON</returns>
    public class UserController : ApiController
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public string cookieTimespan = ConfigurationManager.AppSettings["CookieTimespan"];

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public Int16 cookieExpires = Convert.ToInt16(ConfigurationManager.AppSettings["CookieExpires"]);

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public Int16 userExpires = Convert.ToInt16(ConfigurationManager.AppSettings["UserExpires"]);

        DateTime expirationDate = DateTime.UtcNow;

        Repo _repo = new Repo();

        /// <summary>
        /// Returns a list of all registered Users.
        /// </summary>
        /// <returns>JSON</returns>
        public HttpResponseMessage Get()
        {
            try
            {
                var _userJson = _repo.GetUserList().ToJson();

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(_userJson, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }

        /// <summary>
        /// If a User email address is not found, we will generate a new User and return the new User object for registration purposes. Otherwise, we return the registered User data.
        /// </summary>
        /// <param name="email"></param>
        /// <returns>JSON</returns>
        public HttpResponseMessage Get(string email)
        {
            var jsonResponse = "";
            //Boolean isExpired = false;

            try
            {
                User _user = _repo.GetUserByEmailAddress(email.Trim());

                if (_user == null) // New user
                {
                    _user = new User();
                    _user._id = ObjectId.GenerateNewId();
                    _user.Enabled = false;
                    _user.ExpireDate = DateTime.UtcNow.AddDays(userExpires);

                    var tmpEmail = email.Split('@');
                    var userName = tmpEmail[0].Trim();
                    var userDomain = tmpEmail[1].Trim();

                    Email newEmail = new Email();
                    newEmail.UserName = userName;
                    newEmail.Domain = userDomain;

                    _user.Contact.Email = newEmail;

                    _repo.CreateUser(_user, 0.00, 0.00);

                    // Get rid of object identifiers and dates as they break json formatting
                    jsonResponse = _repo.SanitizeJsonString(_user.ToJson());
                }
                else
                {
                    //Boolean isExpired = false;

                    jsonResponse = "{";
                    jsonResponse += "'Status': '" + email.Trim() + " already registered.', ";
                    jsonResponse += "'Enabled': '" + _user.Enabled + "', ";

                    // Create Subscription check event
                    Event subscriptionEvent = new Event(0.00, 0.00);

                    // Check if subscription valid and set event accordingly
                    DateTime currentDate = DateTime.Now;
                    DateTime expiresDate = Convert.ToDateTime(_user.ExpireDate);

                    DateTime currentUtcDate = TimeZoneInfo.ConvertTimeToUtc(currentDate);
                    DateTime expireUtcDate = TimeZoneInfo.ConvertTimeToUtc(expiresDate);

                    TimeSpan difference = currentUtcDate - expireUtcDate;
                    if (difference.TotalSeconds > 0)
                    {
                        // Account expired
                        //isExpired = true;

                        subscriptionEvent.TypeId = Constants.Event.Account.SubscriptionExpired.Item1;
                        subscriptionEvent.Name = Constants.Event.Account.SubscriptionExpired.Item2;

                        // Update expired user in db
                        _user.Expired = true;
                        _repo.UpdateUser(_user, 0.00, 0.00);
                    }
                    else
                    {
                        //isExpired = false;

                        subscriptionEvent.TypeId = Constants.Event.Account.SubscriptionCurrent.Item1;
                        subscriptionEvent.Name = Constants.Event.Account.SubscriptionCurrent.Item2;
                    }

                    jsonResponse += "'Expired': '" + _user.Expired + "', ";
                    jsonResponse += "'ExpireDate': '" + _user.ExpireDate + "'";
                    jsonResponse += "}";

                    jsonResponse = _repo.SanitizeJsonString(jsonResponse);

                    Reference userRef = new Reference(_user._id, Constants.Reference.TypeUser);

                    subscriptionEvent.Reference = userRef;

                    //_repo.SaveEvent(subscriptionEvent);
                }

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(jsonResponse, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }

        /// <summary>
        /// User Login. Be sure to pass the User's Email and Password. Always pass user GPS coordinates as latitude and longitude string values.
        /// </summary>
        /// <param name="email"></param>
        /// <param name="password"></param>
        /// <param name="latitude"></param>
        /// <param name="longitude"></param>
        /// <returns>JSON</returns>
        public HttpResponseMessage Get(string email, string password, string latitude, string longitude)
        {
            try
            {
                email = System.Net.WebUtility.UrlDecode(email);
                password = System.Net.WebUtility.UrlDecode(password);
                latitude = System.Net.WebUtility.UrlDecode(latitude);
                longitude = System.Net.WebUtility.UrlDecode(longitude);

                var _user = _repo.LoginUser(email, password, Convert.ToDouble(latitude), Convert.ToDouble(longitude));

                // Get rid of object identifiers and dates as they break json formatting
                var jsonResponse = "";

                if (_user != null)
                    jsonResponse = _repo.SanitizeJsonString(_user.ToJson());
                else
                    jsonResponse = "{'Result':'Email (" + email + ") not registered'}";

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(jsonResponse, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }

        /// <summary>
        /// User Logout. Be sure to pass the userId. Always pass user GPS coordinates as latitude and longitude string values.
        /// </summary>
        /// <param name="userId"></param>
        /// <param name="userAction"></param>
        /// <param name="latitude"></param>
        /// <param name="longitude"></param>
        /// <returns></returns>
        public HttpResponseMessage Get(string userId, int userAction, string latitude, string longitude)
        {
            var _jsonResponse = "";

            try
            {
                if (userId == "")
                    userId = ObjectId.Empty.ToString();

                switch(userAction)
                {
                    case 999: // Logout User
                        _jsonResponse = _repo.LogOutUser(ObjectId.Parse(userId), Convert.ToDouble(latitude), Convert.ToDouble(longitude)).ToJson();
                        break;
                }

                // Get rid of object identifiers and dates as they break json formatting
                var jsonResponse = "{'Status':'User logged out'}"; // _repo.SanitizeJsonString(_jsonResponse.ToJson());

                // Expire the cookie to logoff
                //expirationDate = DateTime.UtcNow.AddDays(30);
                //_repo.CreateCookie("Account-User", jsonResponse, expirationDate);

                //var responseCookie = HttpContext.Current.Response.Cookies;

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(jsonResponse, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }

        // POST: api/User
        /// <summary>
        /// Updates User account. Pass User object in post data. Returns updated User data.
        /// </summary>
        /// <param name="userId"></param>
        /// <param name="latitude"></param>
        /// <param name="longitude"></param>
        /// <param name="serializedUser"></param>
        /// <returns>JSON</returns>
        public HttpResponseMessage Post(string userId, string latitude, string longitude, [FromBody]string serializedUser)
        {
            var jsonResponse = "";

            try
            {
                User _user = _repo.GetUser(userId.Trim());

                dynamic deserializedUser = Newtonsoft.Json.JsonConvert.DeserializeObject(serializedUser);

                _user.FirstName = deserializedUser.FirstName;
                _user.LastName = deserializedUser.LastName;

                _user.DeviceType = deserializedUser.DeviceType;
                _user.Pwd = deserializedUser.Pwd;
                _user.Gender = deserializedUser.Gender;

                // Update Address
                // Process Zipcode to get ObjectId values where needed
                Int32 _zipCode = Convert.ToInt32(deserializedUser.Contact.Address.ZipCode);
                _user.Contact.Address.ZipCode = _zipCode;

                if (_zipCode != 0)
                {
                    // Lookup ZipCode data
                    ZipCode myZipcode = _repo.GetZipByValue(_zipCode);

                    _user.Contact.Address.CountryId = myZipcode.CountryId;
                    _user.Contact.Address.StateId = myZipcode.StateId;
                    _user.Contact.Address.CountyId = myZipcode.CountyId;
                    _user.Contact.Address.CityId = myZipcode.CityId;
                    _user.Contact.Address.TimeZoneId = myZipcode.TimeZoneId;
                }

                _user.Contact.Address.Address1 = deserializedUser.Contact.Address.Address1;
                _user.Contact.Address.Address2 = deserializedUser.Contact.Address.Address2;

                // Update Phone
                _user.Contact.Phone.PhoneType = deserializedUser.Contact.Phone.PhoneType;
                _user.Contact.Phone.AreaCode = deserializedUser.Contact.Phone.AreaCode;
                _user.Contact.Phone.Exchange = deserializedUser.Contact.Phone.Exchange;
                _user.Contact.Phone.Number = deserializedUser.Contact.Phone.Number;

                // Update Email
                _user.Contact.Email.UserName = deserializedUser.Contact.Email.UserName;
                _user.Contact.Email.Domain = deserializedUser.Contact.Email.Domain;

                #region Update testing


                _user.TNCs.Clear();
                foreach (var _tnc in deserializedUser.TNCs)
                {
                    // Lookup TNC in db and use one selected by name
                    TNC currentTNC = _repo.GetTNCByName(_tnc.Name.Value);

                    TNCList _tncItem = new TNCList();
                    _tncItem._id = currentTNC._id;
                    _tncItem.Name = currentTNC.Name;

                    _user.TNCs.Add(_tncItem);
                }


                #endregion

                // Ensure phone and email is unique
                bool emailIsUnique = _repo.emailIsUnique(_user);
                bool phoneIsUnique = _repo.phoneIsUnique(_user);

                if (!emailIsUnique)
                    jsonResponse = "{'Result':'Email (" + _user.Contact.Email.UserName + "@" + _user.Contact.Email.Domain + ") already registered'}";
                else if (!phoneIsUnique)
                    jsonResponse = "{'Result':'Phone (" + _user.Contact.Phone.AreaCode + ") " + _user.Contact.Phone.Exchange + "-" + _user.Contact.Phone.Number + ") already registered'}";
                else
                {
                    _user = _repo.UpdateUser(_user, Convert.ToDouble(latitude), Convert.ToDouble(longitude));

                    // Get rid of object identifiers and dates as they break json formatting
                    if (_user != null)
                        jsonResponse = _repo.SanitizeJsonString(_user.ToJson());
                    else
                        jsonResponse = "{'Result':'UserId (" + userId + ") not registered'}";
                }

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(jsonResponse, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }
    }
}
