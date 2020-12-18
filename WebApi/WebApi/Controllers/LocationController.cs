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

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

using Data.Repositories.Models;
using Data.Repositories.MongoDB;

namespace WebApi.Controllers
{
    /// <summary>
    /// General Location services.
    /// </summary>
    /// 

    public class LocationController : ApiController
    {
        Repo _repo = new Repo();

        /// <summary>
        /// Report User location. Pass Location object in post data.
        /// </summary>
        /// <param name="userId"></param>
        /// <param name="serializedLocation"></param>
        /// <returns>JSON</returns>
        public HttpResponseMessage Post(string userId, [FromBody]string serializedLocation)
        {
            try
            {
                dynamic deserializedLocation = Newtonsoft.Json.JsonConvert.DeserializeObject(serializedLocation);

                ObjectId refId = ObjectId.Parse(userId.Trim());
                ObjectId tncId = ObjectId.Parse(deserializedLocation.TNCId.Value);

                String uiScreen = deserializedLocation.UIScreen.Value;
                uiScreen = uiScreen.Replace("com.driveswitch.", "").Replace("Activity", "");
                uiScreen = uiScreen.Replace("class ", "");

                Double latitude = Convert.ToDouble(deserializedLocation.Latitude.Value);
                Double longitude = Convert.ToDouble(deserializedLocation.Longitude.Value);

                Location _location = new Location(refId, tncId, uiScreen, latitude, longitude);

                _repo.UpdateUserLocation(_location);

                // Need to return the user here for integrated subscription validation
                User _user = _repo.GetUser(userId.Trim());

                // Check if subscription valid and set event accordingly
                DateTime currentDate = DateTime.Now;
                DateTime expiresDate = Convert.ToDateTime(_user.ExpireDate);

                DateTime currentUtcDate = TimeZoneInfo.ConvertTimeToUtc(currentDate);
                DateTime expireUtcDate = TimeZoneInfo.ConvertTimeToUtc(expiresDate);

                TimeSpan difference = currentUtcDate - expireUtcDate;
                if (difference.TotalSeconds > 0)
                {
                    // Update expired user in db
                    _user.Expired = true;
                    _repo.UpdateUser(_user, 0.00, 0.00);
                }

                var jsonResponse = "";

                if (_user != null)
                    jsonResponse = _repo.SanitizeJsonString(_user.ToJson());
                else
                    jsonResponse = "{'Status':'User location saved'}";

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(jsonResponse, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                ServiceResponse _response = new ServiceResponse(ex.ToString());
                var _tmpResponse = _response.ToJson();

                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(_tmpResponse, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }
    }
}
