using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web;
using System.Web.Http;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

using Data.Repositories.Models;
using Data.Repositories.MongoDB;

namespace WebApi.Controllers
{
    /// <summary>
    /// State operations
    /// </summary>
    public class StateController : ApiController
    {
        Repo _repo = new Repo();

        // GET: api/State
        /// <summary>
        /// Returns a list of States by countryId.
        /// </summary>
        /// <param name="countryId"></param>
        /// <returns>JSON</returns>
        public HttpResponseMessage Get(string countryId)
        {
            if (countryId == null || countryId == "")
                countryId = Constants.DefaultCountryId.ToString();

            try
            {
                var stateJson = _repo.GetStateList(countryId).ToJson();

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(stateJson, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }

        //// GET: api/State/5
        //public string Get(int id)
        //{
        //    return "value";
        //}

        //// POST: api/State
        //public void Post([FromBody]string value)
        //{
        //}

        // PUT: api/State/5
        //public void Put(int id, [FromBody]string value)
        //{
        //}

        //// DELETE: api/State/5
        //public void Delete(int id)
        //{
        //}
    }
}
