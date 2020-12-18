using System;

namespace WebApi.Areas.HelpPage
{
    /// <summary>
    /// This represents an invalid sample on the help page. There's a display template named InvalidSample associated with this class.
    /// </summary>
    public class InvalidSample
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public InvalidSample(string errorMessage)
        {
            if (errorMessage == null)
            {
                throw new ArgumentNullException("errorMessage");
            }
            ErrorMessage = errorMessage;
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public string ErrorMessage { get; private set; }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public override bool Equals(object obj)
        {
            InvalidSample other = obj as InvalidSample;
            return other != null && ErrorMessage == other.ErrorMessage;
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public override int GetHashCode()
        {
            return ErrorMessage.GetHashCode();
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public override string ToString()
        {
            return ErrorMessage;
        }
    }
}