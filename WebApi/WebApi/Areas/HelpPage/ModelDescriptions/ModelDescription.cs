using System;

namespace WebApi.Areas.HelpPage.ModelDescriptions
{
    /// <summary>
    /// Describes a type model.
    /// </summary>
    public abstract class ModelDescription
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public string Documentation { get; set; }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public Type ModelType { get; set; }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public string Name { get; set; }
    }
}