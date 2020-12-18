using System.Collections.ObjectModel;

namespace WebApi.Areas.HelpPage.ModelDescriptions
{
    /// <summary>
    /// Need description here...
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    public class ComplexTypeModelDescription : ModelDescription
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public ComplexTypeModelDescription()
        {
            Properties = new Collection<ParameterDescription>();
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public Collection<ParameterDescription> Properties { get; private set; }
    }
}