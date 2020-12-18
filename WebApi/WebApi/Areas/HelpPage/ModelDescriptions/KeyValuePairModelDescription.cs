namespace WebApi.Areas.HelpPage.ModelDescriptions
{
    /// <summary>
    /// Need description here...
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    public class KeyValuePairModelDescription : ModelDescription
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public ModelDescription KeyModelDescription { get; set; }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public ModelDescription ValueModelDescription { get; set; }
    }
}