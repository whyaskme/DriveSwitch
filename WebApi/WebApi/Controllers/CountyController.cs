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
    /// County operations
    /// </summary>
    public class CountyController : ApiController
    {
        // Repo _repo = new Repo();

        // GET: api/County
        /// <summary>
        /// Returns a list of Counties by stateId.
        /// </summary>
        /// <returns></returns>
        /// 

        Repo _repo = new Repo();

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public HttpResponseMessage Get(string stateId)
        {
            try
            {
                var countyJson = _repo.GetCountyList(stateId).ToJson();

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(countyJson, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }

        //public List<ListItem> Get(string stateId)
        //{
        //    try
        //    {
        //        //Utils _util = new Utils();
        //        //return _util.GetCountyList(stateId);

        //        // Get Counties
        //        List<ListItem> _counties = _repo.GetCountyList(stateId);

        //        return _counties;
        //    }
        //    catch (Exception ex)
        //    {
        //        var errMsg = ex.ToString();
        //        return null;
        //    }
        //}

        //// POST: api/County
        //public void Post([FromBody]string value)
        //{
        //}

        //// PUT: api/County/5
        //public void Put(int id, [FromBody]string value)
        //{
        //}

        //// DELETE: api/County/5
        //public void Delete(int id)
        //{
        //}
    }
}
