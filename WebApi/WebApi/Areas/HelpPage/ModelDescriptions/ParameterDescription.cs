using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace WebApi.Areas.HelpPage.ModelDescriptions
{
    /// <summary>
    /// Need description here...
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    public class ParameterDescription
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public ParameterDescription()
        {
            Annotations = new Collection<ParameterAnnotation>();
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public Collection<ParameterAnnotation> Annotations { get; private set; }

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
        public string Name { get; set; }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public ModelDescription TypeDescription { get; set; }
    }
}