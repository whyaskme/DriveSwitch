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
    // http://localhost/api/Notification?userId=57beb61ad00bd2381c91030c&source=Lyft&details=Ride request detected&latitude=0.00&longitude=0.00

    /// <summary>
    /// User Notification operations REQUIRE GPS coordinates: latitude and longitude as input parameters for logging purposes.
    /// </summary>
    /// <returns>JSON</returns>
    public class NotificationController : ApiController
    {
        Repo _repo = new Repo();


        // POST: api/Notification
        /// <summary>
        /// Reports a device Notification. Pass Notification object in post data.
        /// </summary>
        /// <param name="userId"></param>
        /// <param name="serializedNotification"></param>
        /// <returns>JSON</returns>
        public HttpResponseMessage Post(string userId, [FromBody]string serializedNotification)
        {
            try
            {
                dynamic deserializedNotification = Newtonsoft.Json.JsonConvert.DeserializeObject(serializedNotification);

                String PackageName = System.Net.WebUtility.UrlDecode(deserializedNotification.PackageName.Value);
                
                String NotificationTicker = System.Net.WebUtility.UrlDecode(deserializedNotification.NotificationTicker.Value);
                String NotificationTitle = System.Net.WebUtility.UrlDecode(deserializedNotification.NotificationTitle.Value);
                String NotificationText = System.Net.WebUtility.UrlDecode(deserializedNotification.NotificationText.Value);

                var _notification = new Notification(deserializedNotification.Location.Latitude.Value, deserializedNotification.Location.Longitude.Value);
                _notification.UserId = ObjectId.Parse(userId);

                _notification.PackageName = PackageName;

                _notification.NotificationTicker = NotificationTicker;
                _notification.NotificationTitle = NotificationTitle;
                _notification.NotificationText = NotificationText;

                // Location data
                _notification.Location.UserId = ObjectId.Parse(userId);
                _notification.Location.TNCId = ObjectId.Parse(deserializedNotification.Location.TNCId.Value);

                string uiScreen = System.Net.WebUtility.UrlDecode(deserializedNotification.Location.UIScreen.Value);
                uiScreen = uiScreen.Replace("com.driveswitch.", "").Replace("Activity", "");
                uiScreen = uiScreen.Replace("class ", "");

                _notification.Location.UIScreen = uiScreen;

                var jsonString = _repo.CreateNotification(_notification);

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(jsonString, System.Text.Encoding.UTF8, "application/json");

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
