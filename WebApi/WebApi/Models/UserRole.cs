using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

namespace WebApi.Models
{
    public class UserRole : Base
    {
        public UserRole(string roleName)
        {
            _t = "UserRole";

            if (roleName == null || roleName == "")
                roleName = Constants.UserRoles.Consumer.Item2;

            Name = roleName;

            switch (roleName)
            {
                case "System Administrator":
                    _id = Constants.UserRoles.SystemAdministrator.Item1;
                    break;
                case "Site Administrator":
                    _id = Constants.UserRoles.SiteAdministrator.Item1;
                    break;
                case "Group Administrator":
                    _id = Constants.UserRoles.GroupAdministrator.Item1;
                    break;
                default:
                    _id = Constants.UserRoles.Consumer.Item1;
                    break;
            }

            Enabled = true;
        }
    }
}