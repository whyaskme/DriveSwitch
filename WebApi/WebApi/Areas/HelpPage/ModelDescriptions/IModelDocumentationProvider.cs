using System;
using System.Reflection;

namespace WebApi.Areas.HelpPage.ModelDescriptions
{
    /// <summary>
    /// Need description here...
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    public interface IModelDocumentationProvider
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        string GetDocumentation(MemberInfo member);

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        string GetDocumentation(Type type);
    }
}