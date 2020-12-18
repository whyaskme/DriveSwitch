using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

using Data.Repositories.Models;
using Data.Repositories.MongoDB;

namespace WebApi.Controllers
{
    /// <summary>
    /// General Event services.
    /// </summary>
    public class EventController : ApiController
    {
        // Repo _repo = new Repo();

        /// <summary>
        /// Get User events by userId.
        /// </summary>
        /// <returns></returns>
        /// 
        Repo _repo = new Repo();

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        [Obsolete]
        public HttpResponseMessage Get(string userId, string eventsPerPage, string eventsToSkip)
        {
            List<Event> userEvents = new List<Event>();
            try
            {
                userEvents = _repo.GetUserEvents(userId, Convert.ToInt16(eventsPerPage), Convert.ToInt16(eventsToSkip));

                // Get rid of object identifiers and dates as they break json formatting
                var jsonResponse = _repo.SanitizeJsonString(userEvents.ToJson());

                // Add EventCount to Json
                var eventCount = _repo.GetUserEventsCount(userId);

                var array = Newtonsoft.Json.Linq.JArray.Parse(jsonResponse);
                var itemsToAdd = new Newtonsoft.Json.Linq.JObject();
                itemsToAdd["EventCount"] = eventCount;
                //array.Add(eventCountToAdd);

                // Add last activity date
                var lastActivityDate = _repo.GetUserLastActivityDate(userId);

                itemsToAdd["LastActivityDate"] = lastActivityDate;

                array.Add(itemsToAdd);

                jsonResponse = Newtonsoft.Json.JsonConvert.SerializeObject(array);

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
        /// Log a User event. Pass Event object in post data.
        /// </summary>
        /// <param name="userId"></param>
        /// <param name="eventId"></param>
        /// <param name="latitude"></param>
        /// <param name="longitude"></param>
        /// <param name="eventDetails"></param>
        /// <returns>application/json</returns>
        public HttpResponseMessage Post(string userId, string eventId, string latitude, string longitude, [FromBody]string eventDetails)
        {
            try
            {
                var userEventJson = _repo.CreateUserEvent(userId, eventId, eventDetails, Convert.ToDouble(latitude), Convert.ToDouble(longitude)).ToJson();

                // Get rid of object identifiers and dates as they break json formatting
                var jsonResponse = _repo.SanitizeJsonString(userEventJson);

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

        //// PUT: api/Event/5
        //public void Put(int id, [FromBody]string value)
        //{
        //}

        //// DELETE: api/Event/5
        //public void Delete(int id)
        //{
        //}
    }
}
