using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace WebApi.Examples
{
    /// <summary>
    /// Need description here...
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    public partial class Default : System.Web.UI.Page
    {
        /// <summary>
        /// Need description here...
        /// </summary>
        /// <param></param>
        /// <returns></returns>
        protected void Page_Load(object sender, EventArgs e)
        {
            // Repo _repo = new Repo();

            UsingLocalHost.InnerHtml = ConfigurationManager.AppSettings["UseLocalHost"].ToUpper();

            //_repo.CreateUserRoles("System Administrator");
            //_repo.CreateUserRoles("Site Administrator");
            //_repo.CreateUserRoles("Group Administrator");
            //_repo.CreateUserRoles("Consumer");

            //_repo.CreateAreaCodes();

            //var latitude = 34.46;
            //var longitude = -80.25;

            //var _user = _repo.LoginUser("alejandro.brzostek@gmail.com", ".|?;#@:%", latitude, longitude);

            //ObjectId userId = ObjectId.Parse("57b39bfcd00bd33954c8a203");

            //userId = _user._id;

            //if (_user != null)
            //_repo.LogOutUser(userId, latitude, longitude);

            //for (int i = 0; i < 1000; i++)
            //{
            //    _repo.CreateRandomUser("0.00", "0.00");
            //}
        }
    }
}