using System.Web.Http;
using System.Web.Mvc;

namespace WebApi.Areas.HelpPage
{
    /// <summary>
    /// Need description here...
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    public class HelpPageAreaRegistration : AreaRegistration
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public override string AreaName
        {
            get
            {
                return "HelpPage";
            }
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public override void RegisterArea(AreaRegistrationContext context)
        {
            context.MapRoute(
                "HelpPage_Default",
                "Help/{action}/{apiId}",
                new { controller = "Help", action = "Index", apiId = UrlParameter.Optional });

            HelpPageConfig.Register(GlobalConfiguration.Configuration);
        }
    }
}