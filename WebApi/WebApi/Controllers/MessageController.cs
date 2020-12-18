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
    /// System messaging operations.
    /// </summary>
    public class MessageController : ApiController
    {
        Repo _repo = new Repo();

        // POST: api/Message
        /// <summary>
        /// Sends messages to DriveSwitch admin. Pass Message object in post data. Returns message delivery receipt.
        /// </summary>
        /// <param name="serializedMessage"></param>
        /// <returns>JSON</returns>
        public HttpResponseMessage Post([FromBody]string serializedMessage)
        {
            try
            {
                dynamic deserializedUser = Newtonsoft.Json.JsonConvert.DeserializeObject(serializedMessage);

                User _user = _repo.GetUser(deserializedUser.UserId.Value);

                Message _message = new Message();

                _message.UserId = _user._id;
                _message.FromName = _user.FirstName + " " + _user.LastName;
                _message.FromEmail = _user.Contact.Email.UserName + "@" + _user.Contact.Email.Domain;

                _message.ToEmail = System.Net.WebUtility.UrlDecode(deserializedUser.ToEmail.Value);
                _message.ToName = System.Net.WebUtility.UrlDecode(deserializedUser.ToName.Value);

                _message.Subject = System.Net.WebUtility.UrlDecode(deserializedUser.Subject.Value);
                _message.Body = System.Net.WebUtility.UrlDecode(deserializedUser.Body.Value);

                string deliveryResponse = _repo.CreateMessage(_message);

                // Get rid of object identifiers and dates as they break json formatting
                var jsonResponse = "{'Result':'" + deliveryResponse + "'}";

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
