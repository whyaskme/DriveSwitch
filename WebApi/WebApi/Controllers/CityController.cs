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
    /// City operations
    /// </summary>
    /// 
    public class CityController : ApiController
    {
        Repo _repo = new Repo();

        // GET: api/City
        /// <summary>
        /// Returns a list of Cities by regionTypeId: Country=1, State=2, County=3. Ensure you also pass the requested regionId.
        /// </summary>
        /// <param name="regionTypeId"></param>
        /// <param name="regionId"></param>
        /// <returns></returns>
        public HttpResponseMessage Get(Int16 regionTypeId, string regionId)
        {
            if (regionTypeId < 1)
                regionTypeId = Constants.DefaultStateType;

            if (regionId == null)
                regionId = Constants.DefaultRegionId.ToString();

            try
            {
                var cityJson = _repo.GetCityList(regionTypeId, ObjectId.Parse(regionId)).ToJson();

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(cityJson, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }

        //// GET: api/City/5
        //public string Get(int id)
        //{
        //    return "value";
        //}

        //// POST: api/City
        //public void Post([FromBody]string value)
        //{
        //}

        //// PUT: api/City/5
        //public void Put(int id, [FromBody]string value)
        //{
        //}

        //// DELETE: api/City/5
        //public void Delete(int id)
        //{
        //}
    }
}
