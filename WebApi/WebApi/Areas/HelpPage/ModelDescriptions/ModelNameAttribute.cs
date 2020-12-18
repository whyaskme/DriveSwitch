using System;

namespace WebApi.Areas.HelpPage.ModelDescriptions
{
    /// <summary>
    /// Use this attribute to change the name of the <see cref="ModelDescription"/> generated for a type.
    /// </summary>
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Struct | AttributeTargets.Enum, AllowMultiple = false, Inherited = false)]
    public sealed class ModelNameAttribute : Attribute
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public ModelNameAttribute(string name)
        {
            Name = name;
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public string Name { get; private set; }
    }
}