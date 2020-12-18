using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace WebApi.Controllers
{
    /// <summary>
    /// Need description here...
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    public class HomeController : Controller
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public ActionResult Index()
        {
            ViewBag.Title = "Home Page";

            return View();
        }
    }
}
