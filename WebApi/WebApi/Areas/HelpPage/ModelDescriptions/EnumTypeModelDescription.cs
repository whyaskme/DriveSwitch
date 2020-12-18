using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace WebApi.Areas.HelpPage.ModelDescriptions
{
    /// <summary>
    /// Need description here...
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    public class EnumTypeModelDescription : ModelDescription
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public EnumTypeModelDescription()
        {
            Values = new Collection<EnumValueDescription>();
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public Collection<EnumValueDescription> Values { get; private set; }
    }
}