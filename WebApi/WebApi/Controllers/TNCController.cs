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
    /// Transportation Network Company (TNC) operations.
    /// </summary>
    public class TNCController : ApiController
    {
        Repo _repo = new Repo();

        // GET: api/TNC
        /// <summary>
        /// Returns a list of available ridesharing companies (TNCs) registered in the DriveSwitch system.
        /// </summary>
        /// <returns></returns>
        public HttpResponseMessage Get()
        {
            try
            {
                var tncList = _repo.GetTNCList().ToJson();

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(tncList, System.Text.Encoding.UTF8, "application/json");

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
