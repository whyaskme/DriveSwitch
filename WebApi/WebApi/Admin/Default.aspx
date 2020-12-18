<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="Default.aspx.cs" Inherits="WebApi.Examples.Default" %>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>DriveSwitch Web API Admin</title>
    <link href="../ZurbFoundation/css/foundation.css" rel="stylesheet" />
    <link href="../ZurbFoundation/css/app.css" rel="stylesheet" />
    <link href="../Content/DriveSwitchAdmin.css" rel="stylesheet" />
</head>
<body id="docBody">

    <%--loading lightbox--%>
    <div id="PleaseWaitProcessing" class="lightbox_background">
        <div id="DialogContainer" class="lightbox_content">
            <div id="circularG">
                <div id="circularG_1" class="circularG"></div>
                <div id="circularG_2" class="circularG"></div>
                <div id="circularG_3" class="circularG"></div>
                <div id="circularG_4" class="circularG"></div>
                <div id="circularG_5" class="circularG"></div>
                <div id="circularG_6" class="circularG"></div>
                <div id="circularG_7" class="circularG"></div>
                <div id="circularG_8" class="circularG"></div>
            </div>
        </div>
    </div>
    <%--loading lightbox--%>


    <form id="UserForm" method="post">

        <div id="jsonData">jsonData</div>

        <div id="Header" class="row">
            <div class="large-12 medium-12 small-12 columns">
                <div class="primary callout" style="padding: 25px; padding-bottom: 10px;">
                    <div>
                        <a href="../"><img src="../Images/DriveSwitch-The-Ultimate-Driver’s-Switchboard.png" class="HeaderLogo" /></a>
                    </div>
                    <div id="UpdateStatus" class="UpdateStatus">&nbsp;</div>

                    <select id="UserList" name="UserList" class="UserList" onchange="javascript: SelectUserAction();"></select>
                    
                    <input id="btnRegisterUser" type="button" value="Register" style="display: none; width: 100px; height: 40px;" onclick="javascript: RegisterUser();" />
                    <input id="btnUpdateUser" type="button" value="Update" style="display: none; width: 75px; height: 40px;" onclick="javascript: UpdateUser();" />

                    <input id="btnLoginUser" type="button" value="Login" style="display: none; width: 75px; height: 40px; background-color: #c0c0c0; border-color: #c0c0c0; color: #707070;" onclick="javascript: LoginUser();" />
                    <input id="btnLogoutUser" type="button" value="Logout" style="display: none; width: 75px; height: 40px; background-color: #197b30; border-color: #197b30;" onclick="javascript: LogoutUser();" />
                </div>
            </div>
        </div>

        <div id="LocalHost" class="large-12 medium-12 small-12 columns">
            <div>Using LocalHost DB?
                <span id="UsingLocalHost" runat="server" style="color: #eb416b; padding-left: 15px; font-weight: bold;">false</span>
            </div>
        </div>

        <div id="TNCs" class="row">
            <div class="large-12 medium-12 small-12 columns">
                <h5 class="primary callout">TNCs <span style="color: #808080; font-size: 18px; font-weight: normal; font-style: italic;"> - 1 TNC required! <span class="RequiredLabel">*</span></span></h5>
            </div>
            <div class="large-12 medium-12 small-12 columns">
                <div id="TNCContainer"></div>
            </div>
        </div>

        <div id="Personal" class="row">

            <div class="large-12 medium-12 small-12 columns">
                <h5 id="PersonalHeader" class="primary callout">Personal</h5>
            </div>

            <div class="large-12 medium-12 small-12 columns" style="margin-bottom: 15px;">
                <div class="large-6 medium-6 small-12 columns">
                    <label id="EnabledLabel">Enabled?</label>
                    <input id="Enabled" name="Enabled" checked="checked" type="checkbox" style="position: relative; top: 3px;" />
                </div>
                <div class="large-6 medium-6 small-12 columns">
                   <label id="LoggedInLabel">LoggedIn?</label>
                    <label id="LoggedIn">No</label>
                </div>
            </div>

            <div class="large-12 medium-12 small-12 columns" style="margin-bottom: 25px;">
                <div class="large-6 medium-6 small-12 columns">
                     <label>Registered</label>
                    <span id="RegistrationDate" style="position: relative; top: 3px;">&nbsp;</span>
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>Last Active</label>
                    <span id="LastActivityDate" style="position: relative; top: 3px;">&nbsp;</span>
                </div>
            </div>

            <div class="large-12 medium-12 small-12 columns">
                <div class="large-6 medium-6 small-12 columns">
                    <label>Device <span class="RequiredLabel">*</span></label>
                    <select id="DeviceType" name="DeviceType" style="left: 0px;">
                        <option value="0">Select a Device type</option>
                        <option value="1">Android (Phone)</option>
                        <option value="2">Android (Tablet)</option>
                        <option value="3">IOS (Phone)</option>
                        <option value="4">IOS (Tablet)</option>
                    </select>
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>Gender <span class="RequiredLabel">*</span></label>
                    <select id="Gender" name="Gender" style="left: 0px;">
                        <option value="0" selected="selected">Select a Gender</option>
                        <option value="1">Female</option>
                        <option value="2">Male</option>
                    </select>
                </div>
            </div>

            <div class="large-12 medium-12 small-12 columns">
                <div class="large-6 medium-6 small-12 columns">
                    <label>First Name <span class="RequiredLabel">*</span></label>
                    <input id="FirstName" name="FirstName" type="text" />
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>Last Name <span class="RequiredLabel">*</span></label>
                    <input id="LastName" name="LastName" type="text" />
                </div>
            </div>

        </div>

        <div id="Contact" class="row">
            <div class="large-12 medium-12 small-12 columns">
                <h5 class="primary callout">Contact</h5>
            </div>
            <div class="large-12 medium-12 small-12 columns">
                <div class="large-6 medium-6 small-12 columns">
                    <label>Email <span class="RequiredLabel">*</span></label>
                    <input id="Email" name="Email" type="text" />
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>Phone <span class="RequiredLabel">*</span></label>
                    <input id="Phone" name="Phone" type="text" />
                </div>
            </div>
        </div>

        <div id="Address" class="row">
            <div class="large-12 medium-12 small-12 columns">
                <h5 class="primary callout">Address</h5>
            </div>
            <div class="large-12 medium-12 small-12 columns">
                <div class="large-6 medium-6 small-12 columns">
                    <label>Street 1 <span class="RequiredLabel">*</span></label>
                    <input id="Address1" name="Address1" type="text" />
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>Street 2</label>
                    <input id="Address2" name="Address2" type="text" />
                </div>
            </div>
            <div class="large-12 medium-12 small-12 columns">
                <div class="large-6 medium-6 small-12 columns">
                    <label>State <span class="RequiredLabel">*</span></label>
                    <select id="State" name="State" onchange="javascript: GetCountyList();">
                        <option value="000000000000000000000000">Select from 0 States</option>
                    </select>
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>County <span class="RequiredLabel">*</span></label>
                    <select id="County" name="County" onchange="javascript: GetCityList();">
                        <option value="000000000000000000000000">Select from 0 Counties</option>
                    </select>
                </div>
            </div>
            <div class="large-12 medium-12 small-12 columns">
                <div class="large-6 medium-6 small-12 columns">
                    <label>City <span class="RequiredLabel">*</span></label>
                    <select id="City" name="City" onchange="javascript: GetZipCodeList();">
                        <option value="000000000000000000000000">Select from 0 Cities</option>
                    </select>
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>Zip <span class="RequiredLabel">*</span></label>
                    <select id="ZipCode" name="ZipCode" onchange="javascript: getZipCodeData();">
                        <option value="000000000000000000000000">Select from 0 Zip Codes</option>
                    </select>
                </div>
            </div>
        </div>

        <div id="Security" class="row">
            <div class="large-12 medium-12 small-12 columns">
                <h5 class="primary callout">Security</h5>
            </div>
            <div class="large-12 medium-12 small-12 columns">
                <div class="large-6 medium-6 small-12 columns">
                        <label>Password <span class="RequiredLabel">*</span></label>
                        <input id="Pwd" name="Pwd" type="text" />
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>Confirm <span class="RequiredLabel">*</span></label>
                    <input id="PwdConfirm" name="PwdConfirm" type="text" />
                </div>
            </div>
        </div>

        <div id="Events" class="row">
            <div class="large-12 medium-12 small-12 columns">
                <h5 class="primary callout">Events</h5>
            </div>
            <div id="ViewHistory" class="large-12 medium-12 small-12 columns" style="text-align: center;">
                <div id="EventCount" style="text-align: left; font-weight: bold; padding-left: 25px;"></div>
                <div id="EventContent"></div>
                <div id="PaginationControls">
                    <input id='btnFirst' type='button' disabled="disabled" value='<' onclick='javascript: FirstEventRecords();' />
                    <input id='btnPrevious' type='button' disabled="disabled" value='Prev 0' onclick='javascript: PreviousEventRecords();' />
                    <input id='btnNext' type='button' disabled="disabled" value='Next 0' onclick='javascript: NextEventRecords();' />
                    <input id='btnLast' type='button' disabled="disabled" value='>' onclick='javascript: LastEventRecords();' />
                </div>
            <div id="PaginationInfo">Page <span id="CurrentPageNumber">0</span> of <span id="TotalPageCount">0</span></div>
            </div>
        </div>

        <div id="Debug" class="row">
            <div class="large-12 medium-12 small-12 columns">
                <h5 class="primary callout">Location</h5>
            </div>
            <div class="large-6 medium-6 small-12 columns">
                <label>Latitude</label>
                <input id="Latitude" name="Latitude" type="text" value="" />
            </div>
            <div class="large-6 medium-6 small-12 columns">
                <label>Longitude</label>
                <input id="Longitude" name="Longitude" type="text" value="" />
            </div>
        </div>

    </form>

    <script src="../Scripts/JQuery/jquery-3.1.0.js"></script>
    <script src="../Scripts/JQuery/UI/jquery-ui.js"></script>
    <script src="../Scripts/JQuery/Validate/jquery.validate.js"></script>

    <script src="../Scripts/Chosen/chosen.jquery.js"></script>
    <script src="../Scripts/Chosen/DocSupport/prism.js"></script>

    <script src="../ZurbFoundation/js/vendor/what-input.js"></script>
    <script src="../ZurbFoundation/js/vendor/foundation.js"></script>
    <script src="../ZurbFoundation/js/app.js"></script>

    <script src="../Scripts/DriveSwitchAdmin.js"></script>

</body>
</html>
