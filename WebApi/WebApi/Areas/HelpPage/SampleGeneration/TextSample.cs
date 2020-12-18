using System;

namespace WebApi.Areas.HelpPage
{
    /// <summary>
    /// This represents a preformatted text sample on the help page. There's a display template named TextSample associated with this class.
    /// </summary>
    public class TextSample
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public TextSample(string text)
        {
            if (text == null)
            {
                throw new ArgumentNullException("text");
            }
            Text = text;
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public string Text { get; private set; }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public override bool Equals(object obj)
        {
            TextSample other = obj as TextSample;
            return other != null && Text == other.Text;
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public override int GetHashCode()
        {
            return Text.GetHashCode();
        }

        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        public override string ToString()
        {
            return Text;
        }
    }
}