
var bDebug = false;

// Event constants
// Account
var Created = 1000;
var Updated = 1001;
var Deleted = 1002;
var Disabled = 1003;
var Enabled = 1004;
var Flagged = 1005;
var LoggedIn = 1006;
var LoggedOut = 1007;
var FailedLoginBadUserName = 1008;
var FailedLoginBadPwd = 1009;
var RandomUserGenerated = 1010;
var DisabledAccountAccess = 1011;

var viewPersonalSettings = 1012;
var viewAddressSettings = 1013;
var viewRideshareSettings = 1014;
var viewEventHistory = 1015;
var viewSwitchboard = 1016;
var viewSupport = 1017;

// TNC
var rideRequestAccepted = 3006;
var rideRequestIgnored = 3007;
var rideRequestRejected = 3008;

var serviceResponse;
var cookieName = "Account-User";
var cookieDurationDays = 30;

var eventsPerPage = 10;
var eventPageNumber = 1;
var eventsToSkip = 0;

var eventCount = 0;
var totalPages = 0;

var accountUser = JSON.parse(ReadCookie(cookieName));
var userEmail = "";

var activeView = "";

var timerId;
var setGPSReportInterval = false;
var GPSReportIntervalTime = 1000;

var isLatitudeUpdated = false;
var isLongitudeUpdated = false;

var cookieDomain = "";

var ServiceHost = window.location.toString();
var tmpLocation = ServiceHost.split('/');
for (i = 0; i < tmpLocation.length; i++) {
    if (i == 2) {
        cookieDomain = tmpLocation[i];
        ServiceHost = "http://" + tmpLocation[i] + "/api";
    }
}

$("#MenuIcon").show();

$(document).ready(function ()
{
    SetGeoLocation();
    GetTNCList(undefined);

    if (bDebug) {
        $("#jsonData").show();
        $("#Email").val("joe@baranauskas.com");
        $("#Pwd").val("joe2747");
    }
    else {
        $("#jsonData").hide();
        DisableAllControls();
    }

    // Check cookie to see if logged in. If not, show login screen
    if (accountUser != null) {
        $("#jsonData").html(SetJsonDisplay(document.cookie));

        userEmail = accountUser.Contact.Email[0].UserName + "@" + accountUser.Contact.Email[0].Domain;

        if (accountUser.IsLoggedIn) {

            if (accountUser.Enabled) {
                ToggleMobileMenu();
                ShowUserTNCButtons();
                $("#ViewLogin").hide();
                $("#ViewAccount").show();
                $("#AppMessage").html("Waiting for ride requests...");
            }
            else {
                ShowAccountDisabledStatus();
            }
        }
        else {

            ShowLoginView(true);
            $("#Email").val(userEmail);
            $("#Pwd").prop("disabled", false);
            $("#Pwd").focus();
        }
    }
    else {
        ShowLoginView(true);
    }
});

// Utility
function ShowDialog(ShowProgress) {

    //$('#UserForm').hide();

    // Scroll window to top. If not, then if the browser is towards the bottom of the window, the dialog is out of view at the top
    window.scrollTo(0, 0);

    var screenHeight = $(window).innerHeight();
    var screenWidth = $(window).innerWidth();

    var loaderMargin = (screenHeight / 2) - 18;

    $('#DialogContainer').css('margin-top', loaderMargin);
    $('#DialogContainer').show();

    if (ShowProgress) {
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
function CreateCookie(name, value, days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        var expires = "; expires=" + date.toGMTString();
    }
    else var expires = "";

    document.cookie = name + "=" + value + expires + "; path=/";

    alert(document.cookie);
}
function ReadCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}
function EraseCookie(name) {
    //alert("EraseCookie - " + name)
    CreateCookie(name, "", -1);
}
function DisableAllControls()
{
    $("#AccountContent").html("&nbsp;");

    // Disable buttons until validated
    $("#btnLoginUser").prop("disabled", true);
    $("#btnRegisterPersonal").prop("disabled", true);
    $("#btnRegisterAddress").prop("disabled", true);
    $("#btnRegisterTNC").prop("disabled", true);

    // Login view
    $("#Email").prop("disabled", true);
    $("#Email").val("");

    $("#Pwd").prop("disabled", true);
    $("#Pwd").val("");

    // Personal view
    $("#FirstName").prop("disabled", true);
    $("#FirstName").val("");

    $("#LastName").prop("disabled", true);
    $("#LastName").val("");

    $("#Phone").prop("disabled", true);
    $("#Phone").val("");

    $("#DeviceType").prop("disabled", true);
    $("#Gender").prop("disabled", true);

    // Address view
    $("#Address1").prop("disabled", true);
    $("#Address1").val("");

    $("#ZipCode").prop("disabled", true);
    $("#ZipCode").val("");


    // TNC view

    $("#ViewLogin").hide();
    $("#ViewPersonal").hide();
    $("#ViewAddress").hide();
    $("#ViewTNC").hide();
    $("#ViewAccount").hide();
    $("#ViewHistory").hide();
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
function ShowLoginView(showLogin) {
    if (showLogin) {

        DisableAllControls();

        $("#ViewLogin").show();
        $("#AppMessage").html("Enter a valid email address");

        $("#Pwd").val("");

        $("#Email").val(userEmail);
        $("#Email").prop("disabled", false);
        $("#Email").focus();
    }
}
function SetGeoLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(ShowPosition);
    } else {
        x.innerHTML = "Geolocation is not supported by this browser.";
    }
}
function ShowPosition(position) {

    isLatitudeUpdated = false;
    isLongitudeUpdated = false;

    //$('#AppFooter').hide();

    if ($("#Latitude").html() != position.coords.latitude.toString()) {

        $("#Latitude").html(position.coords.latitude.toString());
        isLatitudeUpdated = true;
    }

    if ($("#Longitude").html() != position.coords.longitude.toString()) {
        
        $("#Longitude").html(position.coords.longitude.toString());
        isLongitudeUpdated = true;
    }

    if (accountUser.IsLoggedIn) {

        //alert("Report location and check account status");

        ReportGPSPosition();

        setTimeout(SetGeoLocation, GPSReportIntervalTime);
    }

    //if (setGPSReportInterval)
    //{
    //    if (isLatitudeUpdated && isLongitudeUpdated) {
    //        if (accountUser.IsLoggedIn) {
    //            ReportGPSPosition();
    //        }
    //    }

    //    setTimeout(SetGeoLocation, GPSReportIntervalTime);
    //}
}
function CheckUserEnabled() {

    // Report GPS position to web service here...
    var serviceUrl = ServiceHost + "/User";
    serviceUrl += "?userId=" + accountUser._id.toString();

    $.get(serviceUrl, function (serviceResponse, status) {

        accountUser = serviceResponse;

        if (!accountUser.Enabled) {
            ShowAccountDisabledStatus();
        }
    })
}
function ReportDisabledAccountAccess()
{
    //alert("ReportDisabledAccountAccess service call");

    //var eventId = DisabledAccountAccess;
    var eventDetails = "Account: Disabled Attempt";

    // Report GPS position to web service here...
    var serviceUrl = ServiceHost + "/Event";
    serviceUrl += "?userId=" + accountUser._id.toString();
    serviceUrl += "&eventId=" + DisabledAccountAccess.toString();
    serviceUrl += "&latitude=" + $("#Latitude").html();
    serviceUrl += "&longitude=" + $("#Longitude").html();

    //alert(serviceUrl);

    $.ajax
    ({
        type: "Post",
        url: serviceUrl,
        contentType: 'application/x-www-form-urlencoded; charset=utf-8',
        data: '=' + eventDetails,
        success: function (serviceResponse) {
            //alert(serviceResponse.ResponseText);
        },
        failure: function (serviceResponse) {
        }
    })
}
function ShowAccountDisabledStatus()
{
    DisableAllControls();

    ReportDisabledAccountAccess();

    ShowLoginView(true);
    $("#Email").val(userEmail);
    $("#Pwd").prop("disabled", false);
    $("#Pwd").focus();
    $("#AppMessage").html("This account is disabled!");
}
function ReportGPSPosition()
{
    //$('#AppFooter').show();

    // Report GPS position to web service here...
    var serviceUrl = ServiceHost + "/GPS";
    serviceUrl += "?userId=" + accountUser._id.toString();
    serviceUrl += "&latitude=" + $("#Latitude").html();
    serviceUrl += "&longitude=" + $("#Longitude").html();

    //alert(serviceUrl);

    $.ajax
    ({
        type: "Post",
        url: serviceUrl,
        contentType: 'application/x-www-form-urlencoded; charset=utf-8',
        data: '=' + null,
        success: function (serviceResponse) {
            //alert(serviceResponse.ResponseText);
            accountUser = serviceResponse;

            if (!accountUser.Enabled) {
                ShowAccountDisabledStatus();
            }
        },
        failure: function (serviceResponse) {
        }
    });
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
function ToggleMobileMenu()
{
    //CheckUserEnabled();

    clearTimeout(timerId);

        accountUser = JSON.parse(ReadCookie(cookieName));

        if (accountUser.IsLoggedIn) {
            $("#MenuIcon").show();

            if (menuIsVisbile = $('#MenuPanel').is(':visible')) {

                $('#MenuPanel').hide();

                switch (activeView) {
                    case "ViewLogin":
                        $('#ViewLogin').show();
                        break;
                    case "ViewPersonal":
                        $('#ViewPersonal').show();
                        break;
                    case "ViewAddress":
                        $('#ViewAddress').show();
                        break;
                    case "ViewTNC":
                        $('#AppMessage').html("Waiting for ride requests...");
                        $('#ViewTNC').show();
                        break;
                    case "ViewAccount":
                        $('#ViewAccount').show();
                        break;
                    case "ViewHistory":
                        $('#ViewHistory').show();
                        break;
                }
                $('#AppMessage').show();
            }
            else {
                // Figure out which view is active and store value to show again once we hide the menu
                if ($('#ViewLogin').is(':visible')) {
                    activeView = "ViewLogin";
                    $('#ViewLogin').hide();
                }

                if ($('#ViewPersonal').is(':visible')) {
                    activeView = "ViewPersonal";
                    $('#ViewPersonal').hide();
                }

                if ($('#ViewAddress').is(':visible')) {
                    activeView = "ViewAddress";
                    $('#ViewAddress').hide();
                }

                if ($('#ViewTNC').is(':visible')) {
                    activeView = "ViewTNC";
                    $('#ViewTNC').hide();
                }

                if ($('#ViewAccount').is(':visible')) {
                    activeView = "ViewAccount";
                    $('#ViewAccount').hide();
                }

                if ($('#ViewHistory').is(':visible')) {
                    activeView = "ViewHistory";
                    $('#ViewHistory').hide();
                }

                $('#AppMessage').hide();
                $('#MenuPanel').show();
            }
        }
        else {
            $("#MenuIcon").show();
        }

}
function FormatNumberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
// Utility

// Security View
function ValidateEmail() {

    $("#Email").focus();

    var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    var isValidEmail = regex.test($("#Email").val());

    if (!isValidEmail) {
        $("#AppMessage").html("Enter a valid email address");
        $("#Pwd").prop("disabled", true);
        $("#Email").focus();
    }
    else {
        $("#AppMessage").html("Enter Password");
        $("#Pwd").prop("disabled", false);
        //$("#Pwd").focus();
    }
}
function ValidatePassword() {

    if ($("#Pwd").val().length < 5)
    {
        $("#btnLoginUser").prop("disabled", true);
        $("#AppMessage").html("Must be more than 4 characters");
        //$("#Pwd").prop("disabled", true);
        $("#Pwd").focus();
    }
    else
    {
        $("#btnLoginUser").prop("disabled", false);
        $("#AppMessage").html("If complete, click Login");
    }
}
function LoginUser()
{
    //ShowDialog(true);

    $("#MenuIcon").show();

    $("#ViewPersonal").hide();
    $("#ViewAddress").hide();
    $("#ViewTNC").hide();
    $("#ViewAccount").hide();

    // Login User. If not found, we need to register!
    var serviceUrl = ServiceHost + "/User";
    serviceUrl += "?";
    serviceUrl += "email=" + encodeURIComponent($("#Email").val());
    serviceUrl += "&password=" + encodeURIComponent($("#Pwd").val());
    serviceUrl += "&latitude=" + encodeURIComponent($("#Latitude").html());
    serviceUrl += "&longitude=" + encodeURIComponent($("#Longitude").html());

    alert(serviceUrl);

    $.get(serviceUrl, function (serviceResponse, status) {

        accountUser = serviceResponse;

        $("#jsonData").html(SetJsonDisplay(document.cookie));

        for (var i = 0; i < accountUser.EventHistory.length; i++)
        {
            $("#AccountContent").html("&nbsp;");

            accountUser = JSON.parse(ReadCookie(cookieName));

            if (accountUser.EventHistory[i].Name == "Account: Created")
            {
                $("#ViewLogin").hide();
                $("#ViewPersonal").show();
                $("#AppMessage").html("Easy 3 step registration");
                $("#FirstName").prop("disabled", false);
                $("#FirstName").focus();
            }
            else if (accountUser.EventHistory[i].Name == "Account: LogIn Failed - Bad Pwd")
            {
                $("#AppMessage").html("Login failed: Bad password!");
                $("#ViewLogin").show();
                $("#ViewPersonal").hide();
                $("#btnLoginUser").prop("disabled", true);
                $("#Pwd").val("");
                $("#Pwd").focus();
            }
            else // Successful login
            {
                if (accountUser.Enabled) {
                    setGPSReportInterval = true;
                    SetGeoLocation();

                    $("#MenuIcon").show();

                    $("#ViewLogin").hide();

                    ToggleMobileMenu();
                    ShowUserTNCButtons();
                    $("#ViewLogin").hide();
                    $("#ViewAccount").show();
                    $("#AppMessage").html("Waiting for ride requests...");
                }
                else {
                    ShowAccountDisabledStatus();
                }

                //var personalInfoComplete = IsPersonalViewComplete();
                //alert(personalInfoComplete);

                //if (personalInfoComplete)
                //{
                    //$("#ViewAccount").show();
                    //$("#AppMessage").html("Waiting for ride requests...");
                    //$("#ViewAccount").show();

                    //ShowUserTNCButtons();
                //}
                //else
                //{
                //    //alert("Must complete profile!");

                //    $("#AppMessage").html("Please complete registration");

                //    $("#ViewLogin").hide();
                //    $("#ViewPersonal").show();
                //    $("#ViewAddress").hide();
                //    $("#ViewTNC").hide();
                //    $("#ViewAccount").hide();

                //    $("#FirstName").val(accountUser.FirstName);
                //    $("#LastName").val(accountUser.LastName);

                //    $("#Phone").val("(" + accountUser.Contact.Phone[0].AreaCode + ") " + accountUser.Contact.Phone[0].Exchange + "-" + accountUser.Contact.Phone[0].Number);

                //    $("#DeviceType")[0].selectedIndex = accountUser.DeviceType;
                //    $("#Gender")[0].selectedIndex = accountUser.Gender;

                //    //$("#DeviceType").prop("disabled", true);

                //    //$("#Gender").prop("disabled", true);

                //    //$("#btnRegisterPersonal").prop("disabled", true);
                //}
            }
        }

        //HideDialog(true);
    })
}
function LogoutUser() {

    //ShowDialog(true);

    serviceBaseUrl = "/api/User";
    serviceBaseUrl += "?userId=" + accountUser._id.toString();
    serviceBaseUrl += "&userAction=" + 999;
    serviceBaseUrl += "&latitude=" + $("#Latitude").html();
    serviceBaseUrl += "&longitude=" + $("#Longitude").html();

    $.get(serviceBaseUrl, function (serviceResponse, status) {

        accountUser = serviceResponse;

        $("#jsonData").html(SetJsonDisplay(document.cookie));

        accountUser = JSON.parse(ReadCookie(cookieName));

        ShowLoginView(true);

        eventPageNumber = 1;
        eventsToSkip = 0;

        eventCount = 0;
        totalPages = 0;

        $("#EventContent").html("");

        // Reset all buttons
        $("#btnFirst").prop("disabled", true);
        $("#btnPrevious").prop("disabled", true);
        $("#btnNext").prop("disabled", true);
        $("#btnLast").prop("disabled", true);

        $("#jsonData").html("jsonData");

        if (bDebug) {
            $("#Email").val("joe@baranauskas.com");
            $("#Pwd").val("joe2747");
            $("#Pwd").prop("disabled", false);
            $("#btnLoginUser").prop("disabled", false);
        }
        else {
            $("#AppMessage").html("Please enter your password");
            $("#Email").val(accountUser.Contact.Email[0].UserName + "@" + accountUser.Contact.Email[0].Domain);
            $("#Pwd").prop("disabled", false);
            $("#Pwd").focus();
        }
    });
    $("#MenuIcon").show();
    $("#MenuPanel").hide();
    $("#AppMessage").show();
    //$("#MenuIcon").focus();

    setGPSReportInterval = false;

    //HideDialog(true);
}
// Security View

// Personal View
function ValidateFirstName() {

    if ($("#FirstName").val().length < 2) {
        $("#AppMessage").html("Must be at least 2 characters");
        $("#FirstName").focus();
        $("#LastName").prop("disabled", true);
        accountUser.FirstName = "";
    }
    else {
        $("#LastName").prop("disabled", false);
        $("#AppMessage").html("Enter last name if done");
        accountUser.FirstName = $("#FirstName").val();
    }
}
function ValidateLastName() {

    if ($("#LastName").val().length < 2) {
        $("#AppMessage").html("Must be at least 2 characters");
        $("#LastName").focus();
        $("#Phone").prop("disabled", true);
        accountUser.LastName = "";
    }
    else {
        $("#Phone").prop("disabled", false);
        $("#AppMessage").html("Enter phone number if done");
        accountUser.LastName = $("#LastName").val();
    }
}
function ValidatePhone() {

    var phoneInput = $.trim($("#Phone").val()).replace(/\D/g, '');

    var phoneno = /^\d{10}$/;  
    if((phoneInput.match(phoneno))) 
    {
        var phAreaCode = phoneInput.toString().substring(0, 3);
        var phExchange = phoneInput.toString().substring(3, 6);
        var phNumber = phoneInput.toString().substring(6, 10);

        var PhoneData = { "AreaCode": phAreaCode, "Exchange": phExchange, "Number": phNumber };
        accountUser.Phone = PhoneData;

        $("#AppMessage").html("Select your device type");
        $("#DeviceType").prop("disabled", false);
        $("#DeviceType").focus();
    }  
    else  
    {
        $("#btnRegisterPersonal").prop("disabled", true);
        $("#DeviceType").prop("disabled", true);
        $("#Gender").prop("disabled", true);
        $("#AppMessage").html("Enter valid phone number");
        $("#Phone").focus();
        var PhoneData = {};
        accountUser.Phone = PhoneData;
    } 
}
function ValidateDevice()
{
    if ($("#DeviceType option:selected").val() > 0) {
        $("#Gender").prop("disabled", false);
        $("#AppMessage").html("Select your gender");
        $("#Gender").focus();
        accountUser.DeviceType = $("#DeviceType option:selected").val();
    }
    else {
        $("#Gender").prop("disabled", true);
        $("#AppMessage").html("Select your device type");
        $("#DeviceType").focus();
        accountUser.DeviceType = 0;
    }
}
function ValidateGender() {
    if ($("#Gender option:selected").val() > 0) {
        $("#btnRegisterPersonal").prop("disabled", false);
        $("#AppMessage").html("If complete, click Step 2");
        accountUser.Gender = $("#Gender option:selected").val();
    }
    else {
        $("#btnRegisterPersonal").prop("disabled", true);
        $("#AppMessage").html("Select your gender");
        accountUser.Gender = 0;
    }
}
function RegisterPersonal()
{
    $("#ViewLogin").hide();
    $("#ViewPersonal").show();
    $("#ViewAddress").hide();
    $("#ViewTNC").hide();
    $("#ViewAccount").hide();

    // Update Personal info
    var serviceUrl = ServiceHost + "/User";
    serviceUrl += "?userId=" + accountUser._id;
    serviceUrl += "&latitude=" + $("#Latitude").html();
    serviceUrl += "&longitude=" + $("#Longitude").html();

    var serializedUser = JSON.stringify(accountUser);

    $.ajax
    ({
        type: "Post",
        url: serviceUrl,
        contentType: 'application/x-www-form-urlencoded; charset=utf-8',
        data: '=' + serializedUser,
        success: function (serviceResponse) {

            accountUser = serviceResponse;

            $("#jsonData").html(SetJsonDisplay(document.cookie));

            $("#ViewPersonal").hide();
            $("#ViewAddress").show();
            $("#AppMessage").html("Step 2: Address");
            $("#Address1").prop("disabled", false);
            $("#Address1").focus();
        },

        failure: function (serviceResponse) {
            $("#ViewAddress").hide();
            $("#AppMessage").html(serviceResponse);
        }
    });
}
// Personal View

// Address View
function ValidateAddress() {

    //accountUser.Contact.Address.Address1 = "";

    if ($("#Address1").val().length < 3) {
        $("#AppMessage").html("Must be at least 3 characters");
        $("#Address1").focus();
        $("#ZipCode").prop("disabled", true);
    }
    else {
        $("#ZipCode").prop("disabled", false);
        $("#AppMessage").html("Enter zip code if street done");

        //accountUser.Contact.Address.Address1 = $("#Address1").val();
    }
}
function RegisterAddress()
{
    // Update Personal info
    var serviceUrl = ServiceHost + "/User";
    serviceUrl += "?userId=" + accountUser._id;
    serviceUrl += "&latitude=" + $("#Latitude").html();
    serviceUrl += "&longitude=" + $("#Longitude").html();

    var serializedUser = JSON.stringify(accountUser);

    $.ajax
    ({
        type: "Post",
        url: serviceUrl,
        contentType: 'application/x-www-form-urlencoded; charset=utf-8',
        data: '=' + serializedUser,
        success: function (serviceResponse) {

            accountUser = serviceResponse;

            $("#jsonData").html(SetJsonDisplay(document.cookie));

            $("#ViewAddress").hide();
            GetTNCList(undefined);
            $("#ViewTNC").show();
            $("#AppMessage").html("Step 3: rideshare selection");
        },

        failure: function (serviceResponse) {
            $("#ViewTNC").hide();
            $("#AppMessage").html(serviceResponse);
        }
    });
}
function SearchStateCountyCity() {
    $("#AddressLocationInfo").html("&nbsp;");
    $("#btnRegisterAddress").prop("disabled", true);

    //alert(accountUser.Contact);

    if (accountUser.Contact.Address != null)
    {
        accountUser.Contact.Address.Address1 = "";
        accountUser.Contact.Address.StateId = "";
        accountUser.Contact.Address.CountyId = "";
        accountUser.Contact.Address.CityId = "";
        accountUser.Contact.Address.ZipCode = "";
        accountUser.Contact.Address.TimeZoneId = "";
    }

    var zipCode = $("#ZipCode").val();

    //alert(zipCode.length);

    if (zipCode.length == 5) {

        //alert(isNaN(zipCode));

        var IsNotANumber = isNaN(zipCode);
        if (!IsNotANumber) {
            GetZipCodeData(zipCode);
        }
        else {
            $("#AppMessage").html("Enter valid zip code");
        }
    }
    else
        $("#AppMessage").html("Enter valid zip code");

    if (zipCode.length > 5) {
        $("#AppMessage").html("Zip code not valid!");
    }
}
function GetZipCodeData(zipCode) {

    // Reset accountUser
    if (accountUser.Contact.Address != null) {
        accountUser.Contact.Address.Address1 = "";
        accountUser.Contact.Address.StateId = "";
        accountUser.Contact.Address.CountyId = "";
        accountUser.Contact.Address.CityId = "";
        accountUser.Contact.Address.ZipCode = "";
        accountUser.Contact.Address.TimeZoneId = "";
    }

    var serviceUrl = ServiceHost + "/ZipCode?zipCode=" + zipCode;

    //alert(serviceUrl);

    $.get(serviceUrl, function (zipCodeInfo, status) {
        if (zipCodeInfo == null) {
            var errMsg = "Alert: Zip code not valid!";
            $("#AppMessage").html(errMsg);
        }
        else {

            if (accountUser.Contact.Address != null) {
                accountUser.Contact.Address.Address1 = $("#Address1").val();
                accountUser.Contact.Address.StateId = zipCodeInfo.StateId;
                accountUser.Contact.Address.CountyId = zipCodeInfo.CountyId;
                accountUser.Contact.Address.CityId = zipCodeInfo.CityId;
                accountUser.Contact.Address.ZipCode = zipCode;
                accountUser.Contact.Address.TimeZoneId = zipCodeInfo.TimeZoneId;
            }

            $("#btnRegisterAddress").prop("disabled", false);

            $("#AddressLocationInfo").html("(" + zipCodeInfo.County + ") " + zipCodeInfo.City + ", " + zipCodeInfo.StateAbbr);

            $("#AppMessage").html("If complete, click Step 3");
        }
    });
}
// Address View

// Account View
function ValidateTNCForm() {

    var TNCs = "";
    var arrayData = "";
    var serializedTNCs = "";

    var inputTNCs = []; // initialise an empty array

    var atLeastOneTNCSelected = false;

    $('input[type=checkbox]').each(function ()
    {
        if (this.id.indexOf("tnc_") >= 0)
        {
            if (this.checked)
            {
                atLeastOneTNCSelected = true;
                inputTNCs.push(this.id.replace("tnc_", ""));  // the array will dynamically grow
            }
        }
    });

    if (!atLeastOneTNCSelected) {

        TNCs = "";
        arrayData = "";
        serializedTNCs = "";

        $("#AppMessage").html("Select at least 1 TNC");
        $("#btnRegisterTNC").prop("disabled", true);
    }
    else
    {
        accountUser.TNCs = inputTNCs;

        //alert(JSON.stringify(accountUser.TNCs));

        $("#AppMessage").html("If complete, click Finish");
        $("#btnRegisterTNC").prop("disabled", false);
    }
}
function RegisterTNC()
{
    $("#ViewLogin").hide();
    $("#ViewPersonal").hide();
    $("#ViewAddress").hide();
    $("#ViewTNC").hide();
    $("#ViewAccount").hide();

    // Update TNC info
    var serviceUrl = ServiceHost + "/User";
    serviceUrl += "?userId=" + accountUser._id;
    serviceUrl += "&latitude=" + $("#Latitude").html();
    serviceUrl += "&longitude=" + $("#Longitude").html();

    accountUser.IsLoggedIn = true;

    var serializedUser = JSON.stringify(accountUser);

    $.ajax
    ({
        type: "Post",
        url: serviceUrl,
        contentType: 'application/x-www-form-urlencoded; charset=utf-8',
        data: '=' + serializedUser,
        success: function (serviceResponse) {
            accountUser = serviceResponse;

            $("#jsonData").html(SetJsonDisplay(document.cookie));

            LoginUser();

            //ShowUserTNCButtons();
            //$("#ViewAccount").show();
            $("#AppMessage").html("Waiting for ride requests...");
        },
        failure: function (serviceResponse) {
            $("#ViewAccount").hide();
            $("#AppMessage").html("Registration Error 3!");
        }
    });
}
function GetTNCList(accountUser) {

    var serviceUrl = ServiceHost + "/TNC";

    //alert("serviceUrl - " + serviceUrl);
    //alert("accountUser - " + accountUser);

    var tncData = "";

    var IsChecked = "";
    var labelColor = "#c0c0c0;";
    var labelStyle = "normal";

    $.get(serviceUrl, function (data, status) {
        for (var i = 0; i < data.length; i++) {
            tncData += "<div class='TNCData'>";

            IsChecked = "";
            labelColor = "#a1a1a1;";
            labelStyle = "normal";

            //alert("accountUser - " + accountUser);

            if (accountUser != undefined) {
                for (var j = 0; j < accountUser.TNCs.length; j++) {
                    if (data[i].Value == accountUser.TNCs[j]) {
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
function ShowUserTNCButtons() {

    $("#AppMessage").show();
    $("#ViewAccount").show();
    $("#MenuPanel").hide();

    $("#AccountContent").html("&nbsp;");

    $("#AppMessage").html("Waiting for ride requests...");

    var serializedIds = JSON.stringify(accountUser.TNCs);

    var serviceUrl = ServiceHost + "/TNC";
    serviceUrl += "?tncIds=" + serializedIds;

    $.get(serviceUrl, function (tncData, status) {

        var tncButtons = "";
        for (var i = 0; i < tncData.length; i++) {
            tncButtons += "<div id='TNC_" + tncData[i]._id + "' onmouseover='javascript: TNCFocusButton(this.id);' onmouseout='javascript: TNCDeFocusButton(this.id);' onclick='javascript: TNCSelect(this.id);' class='AppButton-Off'>";
            tncButtons += "    <div class='ButtonContent'>";
            tncButtons += "        <img id='TNCLogo_" + tncData[i]._id + "' class='TNCLogo' src='" + tncData[i].LogoUrl + "' />";
            tncButtons += "        <div class='AcceptLabel'>Accept?</div>";
            tncButtons += "        <img id='TNCPower_" + tncData[i]._id + "' class='PowerButton' src='http://api.driveswitch.com/Images/PowerButton-Off.png' />";
            tncButtons += "    </div>";
            tncButtons += "</div>";
        }
        $("#AccountContent").html(tncButtons);
    });
}
function TNCFocusButton(id)
{
    $("#" + id).css('cursor', 'pointer');
    $("#" + id).addClass('AppButton-On').removeClass('AppButton-Off');

    // Update Logo image
    var imageId = id.replace("TNC_", "TNCLogo_");
    var imgSrc = $("#" + imageId).attr('src');
    $("#" + imageId).attr('src', imgSrc.replace("-Off", "-On"));

    // Update Power image
    imageId = id.replace("TNC_", "TNCPower_");
    imgSrc = $("#" + imageId).attr('src');
    $("#" + imageId).attr('src', imgSrc.replace("-Off", "-On"));
}
function TNCDeFocusButton(id) {

    $("#" + id).css('cursor', 'default');
    $("#" + id).addClass('AppButton-Off').removeClass('AppButton-On');

    // Update Logo image
    var imageId = id.replace("TNC_", "TNCLogo_");
    var imgSrc = $("#" + imageId).attr('src');
    $("#" + imageId).attr('src', imgSrc.replace("-On", "-Off"));

    // Update Power image
    imageId = id.replace("TNC_", "TNCPower_");
    imgSrc = $("#" + imageId).attr('src');
    $("#" + imageId).attr('src', imgSrc.replace("-On", "-Off"));
}
function TNCSelect(id)
{
    alert("TNCSelect");

    TNCFocusButton(id);
}
function ToggleTNCLabelColor(tncElement) {

    if (tncElement.checked)
        $("#" + tncElement.id + "_label").css('color', '#eb416b;');
    else
        $("#" + tncElement.id + "_label").css('color', '#a1a1a1;');

    ValidateTNCForm();
}
function ShowUserAccountInfo() {
    //alert("GetUserAccount");

    $("#AppMessage").html("Account Info");
    $("#AccountContent").html("&nbsp;");

}

// User Events
function AutoRefreshEvents()
{
    if ($('#ViewHistory').is(':visible')) {
        if (eventPageNumber == "1")
            timerId = setTimeout(ShowUserEventInfo, GPSReportIntervalTime);
    }
}
function ShowUserEventInfo() {

    $("#MenuPanel").hide();
    $("#AppMessage").show();
    $("#ViewAccount").hide();
    $('#ViewHistory').show();

    //alert("pageNum - " + pageNum);
    if (eventPageNumber == "1") {
        eventsToSkip = 0;
        eventPageNumber = 1;
        AutoRefreshEvents();
    }

    var serviceUrl = ServiceHost + "/Event";
    serviceUrl += "?userId=" + accountUser._id;
    serviceUrl += "&eventsPerPage=" + eventsPerPage;

    if (eventPageNumber >= 1 && eventPageNumber < totalPages)
        serviceUrl += "&eventsToSkip=" + eventsToSkip * eventsPerPage;
    else // This is last page
        serviceUrl += "&eventsToSkip=" + eventsToSkip;

    //alert(serviceUrl);

    $.get(serviceUrl, function (accountUser, status)
    {
        var eventTable = "";

        eventTable += "<div class='EventDate' style='font-weight: bold;'>Date</div>";
        eventTable += "<div class='EventName' style='font-weight: bold;'>Event</div>";
        eventTable += "<div class='EventLocation' style='font-weight: bold;'>Location</div>";

        // Container
        eventTable += "<div style='border: solid 0px #ff0000; height: 265px;'>";

        // Data rows
        for (var i = 0; i < accountUser.length; i++) {

            // EventCount
            if (accountUser[i].EventCount != null) {
                // Only need to process this once. Otherwise, header refreshes with paging...ugly!
                if (eventPageNumber == 1) {
                    eventCount = accountUser[i].EventCount;

                    var modulusVal = eventCount % eventsPerPage;
                    //alert("modulusVal - " + modulusVal);

                    if(modulusVal > 0)
                        totalPages = parseInt(eventCount / eventsPerPage) + 1;
                    else
                        totalPages = parseInt(eventCount / eventsPerPage);

                    $("#AppMessage").html(FormatNumberWithCommas(eventCount) + " events found");
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
                
                var eventNotes = "Note: " + accountUser[i].Details.replace("(","- ");

                eventTable += "<div id='Event_" + accountUser[i]._id + "' title='EventId: " + accountUser[i]._id + "'>";

                eventTable += "     <div class='EventDate'>" + ConvertUTCDateTimeToLocal(accountUser[i].DateTime) + "</div>";
                eventTable += "     <div class='EventName' title='" + eventNotes + "'>" + eventName + "</div>";
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
        if (eventPageNumber >= 1 && eventPageNumber < totalPages)
        {
            //alert("Why are these NOT disabled?");
            $("#btnNext").prop("disabled", false);
            $("#btnLast").prop("disabled", false);
        }

        // Set back button status
        if (eventPageNumber > 1)
        {
            $("#btnFirst").prop("disabled", false);
            $("#btnPrevious").prop("disabled", false);
        }

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
function NextEventRecords()
{
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

function IsPersonalViewComplete()
{
    //alert(accountUser.Contact.Phone[0].AreaCode);

    if (accountUser.FirstName == "")
        return false;

    if (accountUser.LastName == "")
        return false;

    if (accountUser.Contact.Phone == "")
        return false

    if (accountUser.DeviceType == "0")
        return false;

    if (accountUser.Gender == "0")
        return false;

    return true;
}
function IsAddressViewComplete() {

    if (accountUser.Contact.Address.Address1 == "")
        return false;

    if (accountUser.Contact.Address.StateId == "000000000000000000000000")
        return false;

    if (accountUser.Contact.Address.CountyId == "000000000000000000000000")
        return false

    if (accountUser.Contact.Address.CityId == "000000000000000000000000")
        return false

    if (accountUser.Contact.Address.ZipCode == 0)
        return false

    if (accountUser.Contact.Address.TimeZoneId == "000000000000000000000000")
        return false

    return true;
}
function IsTNCViewComplete() {

    if (accountUser.TNCs == "")
        return false;


    //alert(accountUser.TNCs);

    return true;
}
// Account View