using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Web.Http;

using Newtonsoft.Json.Serialization;

namespace WebApi
{
    /// <summary>
    /// Need description here...
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    public static class WebApiConfig
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public static void Register(HttpConfiguration config)
        {
            // Web API configuration and services

            // Remove XML response format
            //config.Formatters.Remove(config.Formatters.XmlFormatter);

            // Only returns XML if requested
            var appXmlType = config.Formatters.XmlFormatter.SupportedMediaTypes.FirstOrDefault(t => t.MediaType == "application/xml");
            config.Formatters.XmlFormatter.SupportedMediaTypes.Remove(appXmlType);

            // Web API routes
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );
        }
    }
}
