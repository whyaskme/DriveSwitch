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
    /// Country operations.
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    /// 
    public class CountryController : ApiController
    {
        Repo _repo = new Repo();

        // GET: api/Country
        /// <summary>
        /// Returns a list of available Countries.
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public List<Country> Get()
        {
            try
            {
                List<Country> _countryList  = _repo.GetCountryList();

                return _countryList;
            }
            catch(Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return null;
            }
        }

        //// GET: api/Country/5
        //public string Get(int id)
        //{
        //    return "value";
        //}

        //// POST: api/Country
        //public void Post([FromBody]string value)
        //{
        //}

        //// PUT: api/Country/5
        //public void Put(int id, [FromBody]string value)
        //{
        //}

        //// DELETE: api/Country/5
        //public void Delete(int id)
        //{
        //}
    }
}
