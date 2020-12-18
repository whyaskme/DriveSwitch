using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using WebApi.Controllers;

/// <summary>
/// Need description here...
/// </summary>
/// <param></param>
/// <returns></returns>
public partial class Members_Default : System.Web.UI.Page
{
    /// <summary>
    /// Need description here...
    /// </summary>
    /// <param></param>
    /// <returns></returns>
    protected void Page_Load(object sender, EventArgs e)
    {
        if(IsPostBack)
        {
            //WebApi.Models.Utils _repo = new WebApi.Models.Utils();
            //WebApi.Models.User _newUser = new WebApi.Models.User();

            //_newUser.FirstName = Request["FirstName"];
            //_newUser.LastName = Request["LastName"];

            //WebApi.Models.Email _email = new WebApi.Models.Email();

            //var email = Request["Email"];
            //if(email.Contains("@"))
            //{

            //}

            //_newUser.Contact.Email.Add(_email);

            //WebApi.Models.Phone _phone = new WebApi.Models.Phone();

            //var phone = Request["Mobile"];

            //_newUser.Contact.Phone.Add(_phone);

            //_repo.CreateTNCUUser(_newUser);

            //Response.Redirect("/Users/Fuck-You.aspx?FirstName=" + _newUser.FirstName + "&RefName=" + _newUser.RefrerredBy);

            //Response.Redirect("/Users/Fuck-You.aspx?FirstName=" + firstName + "&RefName=" + refName);
        }
    }
}