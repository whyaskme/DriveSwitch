using System;
using System.Web.Http;
using System.Web.Mvc;
using WebApi.Areas.HelpPage.ModelDescriptions;
using WebApi.Areas.HelpPage.Models;

namespace WebApi.Areas.HelpPage.Controllers
{
    /// <summary>
    /// The controller that will handle requests for the help page.
    /// </summary>
    public class HelpController : Controller
    {
        private const string ErrorViewName = "Error";

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public HelpController()
            : this(GlobalConfiguration.Configuration)
        {
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public HelpController(HttpConfiguration config)
        {
            Configuration = config;
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public HttpConfiguration Configuration { get; private set; }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public ActionResult Index()
        {
            ViewBag.DocumentationProvider = Configuration.Services.GetDocumentationProvider();
            return View(Configuration.Services.GetApiExplorer().ApiDescriptions);
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public ActionResult Api(string apiId)
        {
            if (!String.IsNullOrEmpty(apiId))
            {
                HelpPageApiModel apiModel = Configuration.GetHelpPageApiModel(apiId);
                if (apiModel != null)
                {
                    return View(apiModel);
                }
            }

            return View(ErrorViewName);
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public ActionResult ResourceModel(string modelName)
        {
            if (!String.IsNullOrEmpty(modelName))
            {
                ModelDescriptionGenerator modelDescriptionGenerator = Configuration.GetModelDescriptionGenerator();
                ModelDescription modelDescription;
                if (modelDescriptionGenerator.GeneratedModels.TryGetValue(modelName, out modelDescription))
                {
                    return View(modelDescription);
                }
            }

            return View(ErrorViewName);
        }
    }
}