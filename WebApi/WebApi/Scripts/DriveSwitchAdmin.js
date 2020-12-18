// Constants
var DefaultCountryType = 1;
var DefaultStateType = 2;
var DefaultCountyType = 3;
var DefaultCityType = 4;

var bDebug = false;

var timerId;
var setGPSReportInterval = false;
var GPSReportIntervalTime = 1000;

var eventsPerPage = 10;
var eventPageNumber = 1;
var eventsToSkip = 0;

var eventCount = 0;
var totalPages = 0;

var EmptyObjectId = "000000000000000000000000";

var serviceBaseUrl = "/api/";

var iRandomNumber = 0;

var selectedUserListValue;
var userData;

var ServiceHost = window.location.toString();
var tmpLocation = ServiceHost.split('/');
for (i = 0; i < tmpLocation.length; i++) {
    if (i == 2) {
        cookieDomain = tmpLocation[i];
        ServiceHost = "http://" + tmpLocation[i] + "/api";
    }
}

ShowDialog(true);

$(document).ready(function ()
{
    //if (bDebug)
    //    $("#jsonData").show();
    //else
    //    $("#jsonData").hide();

    serviceBaseUrl = "/api/";

    ClearForm();
    SetGeoLocation();

    GetTNCList(undefined);
    GetUserList("");
    GetStateList();

    //$("#UserList").chosen();

    //$('.chosen-select').chosen({ disable_search_threshold: 1 });
    //var config = {
    //    '.chosen-select': {},
    //    '.chosen-select-deselect': { allow_single_deselect: true },
    //    '.chosen-select-no-single': { disable_search_threshold: 10 },
    //    '.chosen-select-no-results': { no_results_text: 'Oops, nothing found!' },
    //    '.chosen-select-width': { width: "100%" }
    //}
    //for (var selector in config) {
    //    $(selector).chosen(config[selector]);
    //}

});

function GetUserObject()
{
    var tmpEmail = $("#Email").val().split('@');
    var _userName = tmpEmail[0];
    var _domain = tmpEmail[1];

    var tmpPhone = $("#Phone").val().split(' ');

    var _areaCode = tmpPhone[0].replace("(", "").replace(")", "");

    var tmpElements = tmpPhone[1].split('-');
    var _exchange = tmpElements[0];
    var _number = tmpElements[1];

    var isLoggedOn = "false";
    if ($("#LoggedIn").html() == "Yes")
        isLoggedOn = "true";

    var _userObject = {

        "_id": $("#UserList option:selected").val(),
        "_t": "User",
        "Enabled": $("#Enabled").prop('checked'),
        "Name": "",
        "RegistrationDate": $("#RegistrationDate").html(),
        "DeviceType": $("#DeviceType option:selected").val(),
        "IsLoggedIn": isLoggedOn,
        "FirstName": $("#FirstName").val(),
        "LastName": $("#LastName").val(),
        "Pwd": $("#Pwd").val(),
        "Gender": $("#Gender option:selected").val(),
        "Contact": {
            "Address": {
                "CountryId": "57acbfeea388c60af8d4965a",
                "StateId": $("#State option:selected").val(),
                "CountyId": $("#County option:selected").val(),
                "CityId": $("#City option:selected").val(),
                "ZipCode": $("#ZipCode option:selected").val(),
                "TimeZoneId": "",
                "Address1": $("#Address1").val(),
                "Address2": $("#Address2").val()
            },
            "Email": [
                {
                    "UserName": _userName,
                    "Domain": _domain
                }
            ],
            "Phone": [
                {
                    "PhoneType": "0",
                    "CountryCode": "1",
                    "AreaCode": _areaCode,
                    "Exchange": _exchange,
                    "Number": _number
                }
            ]
        },
        "TNCs":
            ValidateTNCs()
    }

    return _userObject;
}

function SetJsonDisplay(json) {
    if (typeof json != 'string') {
        json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        //return '<span class="' + cls + '">' + match + '</span>';
        return '<span style="color: #000000;">' + match + '</span>';
    });
}

function ToggleTNCLabelColor(tncId)
{
    if(tncId.checked)
        $("#" + tncId.id + "_label").css('color', '#eb416b;');
    else
        $("#" + tncId.id + "_label").css('color', '#a1a1a1;');
}

function ShowDialog(ShowProgress) {

    //$('#UserForm').hide();

    // Scroll window to top. If not, then if the browser is towards the bottom of the window, the dialog is out of view at the top
    window.scrollTo(0, 0);

    var screenHeight = $(window).innerHeight();
    var screenWidth = $(window).innerWidth();

    var loaderMargin = (screenHeight / 2) - 18;

    $('#DialogContainer').css('margin-top', loaderMargin);
    $('#DialogContainer').show();

    if (ShowProgress)
    {
        $('#PleaseWaitProcessing').css("height", screenHeight);
        $('#PleaseWaitProcessing').css("width", screenWidth);
        $('#PleaseWaitProcessing').show();
    }
}

function HideDialog(hideProgresss) {

    $('#DialogContainer').hide();

    if (hideProgresss)
        $('#PleaseWaitProcessing').hide();
}

function LoginUser() {
    serviceBaseUrl = "/api/User";
    serviceBaseUrl += "?";
    serviceBaseUrl += "email=" + encodeURIComponent($("#Email").val());
    serviceBaseUrl += "&password=" + encodeURIComponent($("#Pwd").val());
    serviceBaseUrl += "&latitude=" + encodeURIComponent($("#Latitude").val());
    serviceBaseUrl += "&longitude=" + encodeURIComponent($("#Longitude").val());

    $.get(serviceBaseUrl, function (serviceResponse, status) {
        userData = serviceResponse;
        SetUserData(userData);
        HideDialog(true);
    });
}

function LogoutUser() {
    serviceBaseUrl = "/api/User";
    serviceBaseUrl += "?userId=" + $("#UserList option:selected").val();
    serviceBaseUrl += "&userAction=" + 999;
    serviceBaseUrl += "&latitude=" + $("#Latitude").val();
    serviceBaseUrl += "&longitude=" + $("#Longitude").val();

    $.get(serviceBaseUrl, function (serviceResponse, status)
    {
        userData = serviceResponse;
        SetUserData(userData);
        HideDialog(true);
    });
}

function RegisterUser() {
    var formIsValid = ValidateUserForm();

    //alert("formIsValid - " + formIsValid);

    if (formIsValid)
    {
        ShowDialog(true);

        serviceBaseUrl = "/api/User?";
        serviceBaseUrl += "latitude=" + $("#Latitude").val();
        serviceBaseUrl += "&longitude=" + $("#Longitude").val();

        var _user = GetUserObject();

        $("#jsonData").html(SetJsonDisplay(_user));

        $.ajax
        ({
            type: "Post",
            url: serviceBaseUrl,
            contentType: 'application/x-www-form-urlencoded; charset=utf-8',
            data: '=' + JSON.stringify(_user),
            success: function (serviceResponse) {
                userData = serviceResponse;
                SetUserData(userData);
                GetUserList(userData._id);
                $("#UpdateStatus").html("New User registered!");
            },

            failure: function (response) {
                alert(serviceResponse);
                $("#UpdateStatus").html(serviceResponse);
            }
        });
        HideDialog(true);
    }
}

function ValidateTNCs()
{
    var serializedTNCs = "";
    var inputTNCs = []; // initialise an empty array
    var atLeastOneTNCSelected = false;

    $('input[type=checkbox]').each(function () {
        if (this.id.indexOf("tnc_") >= 0) {
            if (this.checked) {
                atLeastOneTNCSelected = true;
                inputTNCs.push(this.id.replace("tnc_", ""));  // the array will dynamically grow
            }
        }
    });

    if (!atLeastOneTNCSelected) {

        TNCs = "";
        arrayData = "";
        serializedTNCs = "";

        alert("Select at least 1 TNC!");

        return inputTNCs;
    }
    else {
        return inputTNCs;
    }
}

function UpdateUser() {
    //alert('Update User!');

    var formIsValid = ValidateUserForm();

    //alert("formIsValid - " + formIsValid);

    if (formIsValid) {

        ShowDialog(true);

        serviceBaseUrl = "/api/User";
        serviceBaseUrl += "?userId=" + $("#UserList option:selected").val();
        serviceBaseUrl += "&latitude=" + $("#Latitude").val();
        serviceBaseUrl += "&longitude=" + $("#Longitude").val();

        var _user = GetUserObject();

        $("#jsonData").html(SetJsonDisplay(_user));

        $.ajax
        ({
            type: "Post",
            url: serviceBaseUrl,
            contentType: 'application/x-www-form-urlencoded; charset=utf-8',
            data: '=' + JSON.stringify(_user),
            success: function (serviceResponse) {
                userData = serviceResponse;
                SetUserData(userData);
                GetUserList(userData._id);
                HideDialog(true);
                $("#UpdateStatus").html("User Updated!");
            },

            failure: function (response) {
                alert(serviceResponse);
                $("#UpdateStatus").html(serviceResponse);
            }
        });
    }
}

function ValidateUserForm()
{
    var atLeastOneTNCSelected = false;

    //alert("ValidateUserForm");

    // Validate TNCs
    $('input[type=checkbox]').each(function ()
    {
        if(this.id.indexOf("tnc_") >= 0)
            if (this.checked)
                atLeastOneTNCSelected = true;
    });

    if (!atLeastOneTNCSelected) {
        alert("You must select at least 1 TNC!");
        return false;
    }

    // Validate Personal
    if ($("#DeviceType option:selected").val() < 1) {
        alert("Please select a Device type!");
        $("#DeviceType").focus();
        return false;
    }
    if ($("#Gender option:selected").val() < 1) {
        alert("Please select a Gender!");
        $("#Gender").focus();
        return false;
    }
    if ($("#FirstName").val() == "") {
        alert("Please enter a First Name!");
        $("#FirstName").focus();
        return false;
    }
    if ($("#LastName").val() == "") {
        alert("Please enter a Last Name!");
        $("#LastName").focus();
        return false;
    }

    // Validate Address
    if ($("#Address1").val() == "") {
        alert("Please enter a Street address!");
        $("#Address1").focus();
        return false;
    }
    if ($("#State option:selected").val() < 1) {
        alert("Please select a State!");
        $("#State").focus();
        return false;
    }
    if ($("#County option:selected").val() < 1) {
        alert("Please select a County!");
        $("#County").focus();
        return false;
    }
    if ($("#City option:selected").val() < 1) {
        alert("Please select a City!");
        $("#City").focus();
        return false;
    }
    if ($("#ZipCode option:selected").val() < 1) {
        alert("Please select a ZipCode!");
        $("#ZipCode").focus();
        return false;
    }

    // Validate Contact
    if ($("#Email").val() == "") {
        alert("Please enter an Email address!");
        $("#Email").focus();
        return false;
    }
    if ($("#Phone").val() == "") {
        alert("Please enter a Phone number!");
        $("#Phone").focus();
        return false;
    }

    // Validate Security
    if ($("#Pwd").val() == "") {
        alert("Please enter a password!");
        $("#Pwd").focus();
        return false;
    }
    if ($("#PwdConfirm").val() == "") {
        alert("Please confirm your password!");
        $("#PwdConfirm").focus();
        return false;
    }
    if ($("#PwdConfirm").val() != $("#Pwd").val()) {
        alert("Please confirm your password!");
        $("#PwdConfirm").focus();
        return false;
    }

    return true;
}

function GetUserJsonObject() {

    var userObject = { "FirstName": $("#FirstName").val(), "LastName": $("#LastName").val() };

    return userObject;
}

function SelectUserAction()
{
    $("#btnUpdateUser").hide();

    selectedUserListValue = $("#UserList option:selected").val();

    switch (selectedUserListValue)
    {
        case "0": // Select User
            ClearForm();
            SetGeoLocation();
            break;
        case "1": // Clear form
            ClearForm();
            SetGeoLocation();
            break;
        case "2": // Create random User
            ClearForm();
            GetRandomUser();
            break;
        case "3":
            ClearForm();
            SetGeoLocation();
            $("#btnRegisterUser").show();
            break;
        case "4": // Line separator
            alert("Please select a valid item!");
            $("#UserList")[0].selectedIndex = 0;
            break;
        default: // Get User data
            ClearForm();
            GetStateList();
            GetUserData();
            break;
    }
}

function ConvertUTCDateTimeToLocal(timeToConvert) {

    //alert("timeToConvert - " + timeToConvert);

    var d = new Date(timeToConvert);
    var date = d.toLocaleDateString();
    var time = d.toLocaleTimeString();

    var convertedDate = date + " " + time;

    // No builtin JS methods for short conversion
    convertedDate = convertedDate.replace("Sunday", "Sun");
    convertedDate = convertedDate.replace("Monday", "Mon");
    convertedDate = convertedDate.replace("Tuesday", "Tue");
    convertedDate = convertedDate.replace("Wednesday", "Wed");
    convertedDate = convertedDate.replace("Thursday", "Thur");
    convertedDate = convertedDate.replace("Friday", "Fri");
    convertedDate = convertedDate.replace("Saturday", "Sat");

    convertedDate = convertedDate.replace("January", "Jan");
    convertedDate = convertedDate.replace("February", "Feb");
    convertedDate = convertedDate.replace("March", "Mar");
    convertedDate = convertedDate.replace("April", "Apr");
    convertedDate = convertedDate.replace("May", "May");
    convertedDate = convertedDate.replace("June", "Jun");
    convertedDate = convertedDate.replace("July", "Jul");
    convertedDate = convertedDate.replace("August", "Aug");
    convertedDate = convertedDate.replace("September", "Sep");
    convertedDate = convertedDate.replace("October", "Oct");
    convertedDate = convertedDate.replace("November", "Nov");
    convertedDate = convertedDate.replace("December", "Dec");

    //alert("convertedDate - " + convertedDate.toString());

    return convertedDate.toString();
}

function ClearForm()
{
    userData = null;

    $("#UpdateStatus").html("&nbsp;");
    $("#EnabledLabel").attr('style', 'color: #a1a1a1;');
    $("#LoggedIn").attr('style', 'color: #eb416b;');
    $("#LoggedIn").html("No");
    $("#RegistrationDate").html("&nbsp;");


    $("#btnRegisterUser").hide();
    $("#btnUpdateUser").hide();

    $("#Enabled").prop('checked', true);

    // Set ListBoxes
    $("#DeviceType")[0].selectedIndex = 0;
    $("#Gender")[0].selectedIndex = 0;

    $("#State")[0].selectedIndex = 0;

    var ddl = $("#County");
    ddl.html('');
    ddl.append("<option value='000000000000000000000000'>Select from 0 Counties</option>");
    $("#County")[0].selectedIndex = 0;

    ddl = $("#City");
    ddl.html('');
    ddl.append("<option value='000000000000000000000000'>Select from 0 Cities</option>");
    $("#City")[0].selectedIndex = 0;

    ddl = $("#ZipCode");
    ddl.html('');
    ddl.append("<option value='000000000000000000000000'>Select from 0 Zip Codes</option>");
    $("#ZipCode")[0].selectedIndex = 0;

    $("#FirstName").val("");
    $("#LastName").val("");

    $("#Address1").val("");
    $("#Address2").val("");

    $("#Email").val("");
    $("#Phone").val("");

    $("#Pwd").val("");
    $("#PwdConfirm").val("");

    $("#EventHistory").html("None");

    $("#PersonalHeader").html("Personal");

    $("#TNCContainer").html("");

    $("#btnLoginUser").hide();
    $("#btnLogoutUser").hide();

    GetTNCList(undefined);
}

function GetRandomUser()
{
    ShowDialog(true);

    serviceBaseUrl = "/api/User?userId=" + EmptyObjectId;

    $.get(serviceBaseUrl, function (data, status) {
        userData = data;
        SetUserData(userData);
        $("#UpdateStatus").html("Random User from service ready to register!");

        $("#btnRegisterUser").show();
        $("#btnUpdateUser").hide();
        $("#btnLoginUser").hide();
        $("#btnLogoutUser").hide();

        // Set location
        $("#Latitude").val(userData.EventHistory[0].Location.Latitude);
        $("#Longitude").val(userData.EventHistory[0].Location.Longitude);
    });
}

function SetGeoLocation()
{
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(ShowPosition);
    } else {
        x.innerHTML = "Geolocation is not supported by this browser.";
    }
}

function ShowPosition(position) {
    var latitude = $("#Latitude");
    latitude.val(position.coords.latitude);

    var longitude = $("#Longitude");
    longitude.val(position.coords.longitude);
}

function GetTNCList(userData) {
    serviceBaseUrl = "/api/tnc";

    var tncData = "";

    var IsChecked = "";
    var labelColor = "#a1a1a1;";
    var labelStyle = "normal";

    $.get(serviceBaseUrl, function (data, status)
    {
        for (var i = 0; i < data.length; i++)
        {
            tncData += "<div class='TNCData'>";

            IsChecked = "";
            labelColor = "#a1a1a1;";
            labelStyle = "normal";

            if (userData != undefined)
            {
                for (var j = 0; j < userData.TNCs.length; j++)
                {
                    if (data[i].Value == userData.TNCs[j]) {
                        IsChecked = "checked";
                        labelColor = "#eb416b;";
                        labelStyle = "normal";
                    }
                }
            }
            else
                IsChecked = "";

            tncData += "<label id='tnc_" + data[i].Value + "_label' style='color: " + labelColor + "'>" + data[i].Text + "</label>";
            tncData += "<input id='tnc_" + data[i].Value + "' name='tnc_" + data[i].Value + "' onclick='javascript: ToggleTNCLabelColor(this);' " + IsChecked + " type='checkbox' class='TNCCheckboxes' />";

            tncData += "</div>";
        }
        $("#TNCContainer").html(tncData);
    });
}

function GetCountryList()
{
    //serviceBaseUrl = "/api/country";
    ////alert('Get Country list - ' + serviceBaseUrl);

    //var ddl = $("#lstCountries");
    //ddl.html('');

    //$.get(serviceBaseUrl, function (data, status)
    //{
    //    // Set default item
    //    ddl.append("<option value='000000000000000000000000'>Select from " + data.length + " Countries</option>");

    //    for (var k = 0; k < data.length; k++) {
    //        ddl.append("<option value='" + data[k]._id + "'>" + data[k].Name + "</option>");
    //    }
    //});
}

function GetUserList(userId) {

    serviceBaseUrl = "/api/User";

    var userList = $("#UserList");
    userList.html('');

    $.get(serviceBaseUrl, function (data, status)
    {
        // Set default items
        userList.append("<option value='0'>Select from " + FormatNumberWithCommas(data.length) + " Users</option>");
        userList.append("<option value='1'>Clear Registration Form</option>");
        userList.append("<option value='2'>Get Random User</option>");
        userList.append("<option value='3'>Register New User</option>");
        userList.append("<option value='4'>---------------------------------------</option>");

        for (var k = 0; k < data.length; k++) {
            if (data[k].Value == userId) {
                userList.append("<option selected='selected' value='" + data[k].Value + "'>" + data[k].Text + "</option>");
                userList.val(userId).change();
            }
            else
                userList.append("<option value='" + data[k].Value + "'>" + data[k].Text + "</option>");
        }

        HideDialog(true);
    });

}

function GetUserData()
{
    ShowDialog(true);

    selectedUserListValue = $("#UserList option:selected").val();
    serviceBaseUrl = "/api/User?userId=" + selectedUserListValue;

    $.get(serviceBaseUrl, function (data, status) {
        userData = data;
        SetUserData(userData);
    });
}

function SetUserData(userData) {

    $("#Enabled").prop('checked', userData.Enabled);

    if (!userData.Enabled)
        $("#EnabledLabel").attr('style', 'color: #eb416b;');
    else
        $("#EnabledLabel").attr('style', 'color: #a1a1a1;');

    if (!userData.IsLoggedIn) {
        $("#LoggedIn").attr('style', 'color: #eb416b;');
        $("#LoggedIn").html("No");
    }
    else {
        $("#LoggedIn").attr('style', 'color: #197b30;');
        $("#LoggedIn").html("Yes");
    }

    // Set ListBoxes
    $("#DeviceType")[0].selectedIndex = userData.DeviceType;
    $("#Gender")[0].selectedIndex = userData.Gender;
    $("#State").val(userData.Contact.Address.StateId).change();

    $("#FirstName").val(userData.FirstName);
    $("#LastName").val(userData.LastName);

    $("#Address1").val(userData.Contact.Address.Address1);
    $("#Address2").val(userData.Contact.Address.Address2);

    $("#Email").val(userData.Contact.Email[0].UserName + "@" + userData.Contact.Email[0].Domain);
    $("#Phone").val("(" + userData.Contact.Phone[0].AreaCode + ") " + userData.Contact.Phone[0].Exchange + "-" + userData.Contact.Phone[0].Number);

    $("#Pwd").val(userData.Pwd);
    $("#PwdConfirm").val(userData.Pwd);

    $("#RegistrationDate").html(ConvertUTCDateTimeToLocal(userData.RegistrationDate));

    //alert("Here");

    $("#PersonalHeader").html("Personal - <span style='font-size: 16px; font-weight: normal;'>" + userData._id + "</span>");

    GetTNCList(userData);

    $("#btnUpdateUser").show();

    if (userData.IsLoggedIn) {
        $("#btnLoginUser").hide();
        $("#btnLogoutUser").show();
    }
    else {
        $("#btnLoginUser").show();
        $("#btnLogoutUser").hide();
    }

    if (!userData.Enabled) {
        $("#btnLoginUser").hide();
        $("#btnLogoutUser").hide();
    }

    ShowUserEventInfo();

    HideDialog(true);
}

function GetStateList() {
    var selectedCountryId = "57acbfeea388c60af8d4965a";

    serviceBaseUrl = "/api/State?countryId=57acbfeea388c60af8d4965a"; // + selectedCountryId;

    var stateList = $("#State");
    stateList.html('');

    $.get(serviceBaseUrl, function (data, status)
    {
        // Set default item
        stateList.append("<option value='000000000000000000000000'>Select from " + data.length + " States</option>");

        for (var k = 0; k < data.length; k++)
        {
            stateList.append("<option value='" + data[k].Value + "'>" + data[k].Text + "</option>");
        }
    });
}

function GetCountyList() {
    var selectedStateId = $("#State").val();

    serviceBaseUrl = "/api/County?stateId=" + selectedStateId;

    //alert('Get County list - ' + userData);

    var ddl = $("#County");
    ddl.html('');

    $.get(serviceBaseUrl, function (data, status) {
        // Set default item
        ddl.append("<option value='000000000000000000000000'>Select from " + data.length + " Counties</option>");

        for (var k = 0; k < data.length; k++)
        {
            if (userData != undefined)
            {
                if (data[k].Value == userData.Contact.Address.CountyId) {
                    ddl.append("<option selected='selected' value='" + data[k].Value + "'>" + data[k].Text + "</option>");
                    ddl.val(userData.Contact.Address.CountyId).change();
                }
                else
                    ddl.append("<option value='" + data[k].Value + "'>" + data[k].Text + "</option>");
            }
            else
                ddl.append("<option value='" + data[k].Value + "'>" + data[k].Text + "</option>");
        }
    });
}

function GetCityList() {
    var selectedCountyId = $("#County").val();

    serviceBaseUrl = "/api/City?regionTypeId=" + DefaultCountyType + "&regionId=" + selectedCountyId;

    var ddl = $("#City");
    ddl.html('');

    $.get(serviceBaseUrl, function (data, status) {
        // Set default item
        ddl.append("<option value='000000000000000000000000'>Select from " + data.length + " Cities</option>");

        for (var k = 0; k < data.length; k++)
        {
            if (userData != undefined) {
                if (data[k].Value == userData.Contact.Address.CityId) {
                    ddl.append("<option selected='selected' value='" + data[k].Value + "'>" + data[k].Text + "</option>");
                    ddl.val(userData.Contact.Address.CityId).change();
                }
                else
                    ddl.append("<option value='" + data[k].Value + "'>" + data[k].Text + "</option>");
            }
            else
                ddl.append("<option value='" + data[k].Value + "'>" + data[k].Text + "</option>");
        }
    });
}

function GetZipCodeList() {
    var selectedCityId = $("#City").val();

    serviceBaseUrl = "/api/ZipCode?regionTypeId=" + DefaultCityType + "&regionId=" + selectedCityId;

    //alert('Get ZipCode list - ' + serviceBaseUrl);

    var ddl = $("#ZipCode");
    ddl.html('');

    $.get(serviceBaseUrl, function (data, status) {
        // Set default item
        ddl.append("<option value='000000000000000000000000'>Select from " + data.length + " Zip Codes</option>");

        for (var k = 0; k < data.length; k++)
        {
            if (userData != undefined) {
                if (data[k].Value == userData.Contact.Address.ZipCode) {
                    ddl.append("<option selected='selected' value='" + data[k].Value + "'>" + data[k].Text + "</option>");
                    //ddl.val(userData.Contact.Address.CityId).change();
                }
                else
                    ddl.append("<option value='" + data[k].Value + "'>" + data[k].Text + "</option>");
            }
            else
                ddl.append("<option value='" + data[k].Value + "'>" + data[k].Text + "</option>");
        }
    });
}

// User Events
function AutoRefreshEvents() {
    if (eventPageNumber == "1")
        setTimeout(ShowUserEventInfo, GPSReportIntervalTime);
}

function ShowUserEventInfo() {

    //alert("eventsToSkip - " + eventsToSkip);

    if (eventPageNumber == "1") {
        eventsToSkip = 0;
        eventPageNumber = 1;
        AutoRefreshEvents();
    }

    var serviceUrl = ServiceHost + "/Event";
    serviceUrl += "?userId=" + $("#UserList option:selected").val();
    serviceUrl += "&eventsPerPage=" + eventsPerPage;

    if (eventPageNumber >= 1 && eventPageNumber < totalPages)
        serviceUrl += "&eventsToSkip=" + eventsToSkip * eventsPerPage;
    else // This is last page
        serviceUrl += "&eventsToSkip=" + eventsToSkip;

    //alert("serviceUrl - " + serviceUrl);

    $.get(serviceUrl, function (accountUser, status) {
        var eventTable = "";

        eventTable += "<div class='EventDate' style='font-weight: bold;'>Date</div>";
        eventTable += "<div class='EventName' style='font-weight: bold;'>Event</div>";
        eventTable += "<div class='EventNotes' style='font-weight: bold;'>Notes</div>";
        eventTable += "<div class='EventLocation' style='font-weight: bold;'>Location</div>";

        // Container
        eventTable += "<div style='border: solid 0px #ff0000; height: 265px;'>";

        // Data rows
        for (var i = 0; i < accountUser.length; i++) {

            if (accountUser[i].LastActivityDate != null)
                $("#LastActivityDate").html(ConvertUTCDateTimeToLocal(accountUser[i].LastActivityDate.toString()));

            // EventCount
            if (accountUser[i].EventCount != null) {
                // Only need to process this once. Otherwise, header refreshes with paging...ugly!
                if (eventPageNumber == 1) {
                    eventCount = accountUser[i].EventCount;

                    var modulusVal = eventCount % eventsPerPage;
                    //alert("modulusVal - " + modulusVal);

                    if (modulusVal > 0)
                        totalPages = parseInt(eventCount / eventsPerPage) + 1;
                    else
                        totalPages = parseInt(eventCount / eventsPerPage);

                    $("#EventCount").html(FormatNumberWithCommas(eventCount) + " events found");
                }
            }
            else {
                var eventName = accountUser[i].Name;

                eventName = eventName.replace("LogIn Failed - ", "");
                eventName = eventName.replace("Account: ", "");

                var latitude = accountUser[i].Location.Latitude.toString();
                var lat = latitude.substring(0, 8);

                var longitude = accountUser[i].Location.Longitude.toString();
                var long = longitude.substring(0, 8);

                var mapUrl = "http://maps.google.com/?q=" + accountUser[i].Location.Latitude + "," + accountUser[i].Location.Longitude;

                var eventNotes = accountUser[i].Details.replace("(", "- ");

                eventTable += "<div id='Event_" + accountUser[i]._id + "' title='EventId: " + accountUser[i]._id + "'>";

                eventTable += "     <div class='EventDate'>" + ConvertUTCDateTimeToLocal(accountUser[i].DateTime) + "</div>";
                //eventTable += "     <div class='EventDate'>" + accountUser[i].DateTime + "</div>";
                eventTable += "     <div class='EventName'>" + eventName + "</div>";
                eventTable += "     <div class='EventNotes'>" + eventNotes + "</div>";
                eventTable += "     <a href='" + mapUrl + "' target='_blank' title='Show location using Google Maps'>";
                eventTable += "         <div class='EventLocation'>";
                eventTable += "             <span style='color: #eb416b;'>Show</span>";
                eventTable += "         </div>";
                eventTable += "     </a>";

                eventTable += "</div>";
            }
        }
        eventTable += "</div>";

        eventTable += "<div style=''>&nbsp;</div>";

        $("#CurrentPageNumber").html(FormatNumberWithCommas(eventPageNumber));
        $("#TotalPageCount").html(FormatNumberWithCommas(totalPages));

        $("#btnPrevious").val("Prev " + eventsPerPage);
        $("#btnNext").val("Next " + eventsPerPage);

        $("#EventContent").html(eventTable);

        // Reset all buttons
        $("#btnFirst").prop("disabled", true);
        $("#btnPrevious").prop("disabled", true);
        $("#btnNext").prop("disabled", true);
        $("#btnLast").prop("disabled", true);

        // Set forward button states
        if (eventPageNumber >= 1 && eventPageNumber < totalPages) {
            //alert("Why are these NOT disabled?");
            $("#btnNext").prop("disabled", false);
            $("#btnLast").prop("disabled", false);
        }

        // Set back button status
        if (eventPageNumber > 1) {
            $("#btnFirst").prop("disabled", false);
            $("#btnPrevious").prop("disabled", false);
        }

        HideDialog(true);

    });
}
function FirstEventRecords() {

    eventsToSkip = 0;
    eventPageNumber = 1;

    ShowUserEventInfo();
}
function PreviousEventRecords() {

    eventsToSkip--;
    eventPageNumber--;

    ShowUserEventInfo();
}
function NextEventRecords() {
    eventsToSkip++;
    eventPageNumber++;

    ShowUserEventInfo();
}
function LastEventRecords() {

    eventsToSkip = (eventCount - eventsPerPage);
    eventPageNumber = parseInt($("#TotalPageCount").html());

    ShowUserEventInfo();
}
// User Events

function FormatNumberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}