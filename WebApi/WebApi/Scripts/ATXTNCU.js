
var formIsValid = false;

$("#FirstName").focus();
$("#LastName").prop("disabled", true);
$("#Email").prop("disabled", true);
$("#Mobile").prop("disabled", true);
$("#RefName").prop("disabled", true);
$("#Experience").prop("disabled", true);
$("#ContinueButton").prop("disabled", true);
//$("#RegisterBtn").prop("disabled", true);

function NavigateRegistration()
{
    window.location = "../Users/default.aspx";
}

//$(function () {
//    $("#ContinueButton").on({
//        mouseover: function () {
//            $(this).css({
//                left: (Math.random() * 600) + "px",
//                top: (Math.random() * 150) + "px",
//            });
//        }
//    });
//});

function ValidateForm() {

    ValidateFirstName();

    if (formIsValid)
        ValidateLastName();

    if (formIsValid)
        ValidateLastName();

    if (formIsValid)
        ValidateEmail();

    if (formIsValid)
        ValidateEmail();

    if (formIsValid)
        ValidatePhone();


    if (formIsValid)
        ValidateReferralName();

    if(formIsValid)
        ProcessRegistration();
}

function ValidateFirstName()
{
    if ($("#FirstName").val().length < 2) {
        $("#UpdateMsg").html("Must be at least 2 characters");
        $("#FirstName").focus();
        $("#LastName").prop("disabled", true);
        formIsValid = false;
    }
    else {
        $("#LastName").prop("disabled", false);
        $("#UpdateMsg").html("Enter Last name if First name complete");
        formIsValid = true;
    }
}

function ValidateLastName() {
    if ($("#LastName").val().length < 2) {
        $("#UpdateMsg").html("Must be at least 2 characters");
        $("#LastName").focus();
        $("#Email").prop("disabled", true);
        formIsValid = false;
    }
    else {
        $("#Email").prop("disabled", false);
        $("#UpdateMsg").html("Enter Email address if Last name complete");
        formIsValid = true;
    }
}

function ValidateEmail() {
    var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    var isValidEmail = regex.test($("#Email").val());

    if (!isValidEmail) {
        $("#UpdateMsg").html("Enter a valid Email address");
        $("#Pwd").prop("disabled", true);
        $("#Email").focus();
        formIsValid = false;
    }
    else {
        $("#UpdateMsg").html("Enter your Mobile number if Email complete");
        $("#Mobile").prop("disabled", false);
        formIsValid = true;
    }
}

function ValidatePhone() {

    var phoneInput = $.trim($("#Mobile").val()).replace(/\D/g, '');

    var phoneno = /^\d{10}$/;

    if ((phoneInput.match(phoneno))) {
        var phAreaCode = phoneInput.toString().substring(0, 3);
        var phExchange = phoneInput.toString().substring(3, 6);
        var phNumber = phoneInput.toString().substring(6, 10);

        $("#UpdateMsg").html("Please enter name of the person who referred you if Phone number complete");

        $("#RefName").prop("disabled", false);

        formIsValid = true;
    }
    else {
        $("#UpdateMsg").html("Please enter a valid Phone number");
        $("#Mobile").focus();

        $("#RefName").prop("disabled", true);

        formIsValid = false;
    }
}

function ValidateReferralName() {

    if ($("#RefName").val().length < 2) {
        $("#UpdateMsg").html("Referral name must be at least 2 characters");
        $("#RefName").focus();
        $("#Experience").prop("disabled", true);
        formIsValid = false;
    }
    else {
        $("#UpdateMsg").html("Select Driving Experience if Referral name complete");
        $("#Experience").prop("disabled", false);
        formIsValid = true;
    }
}

function ValidateExperience() {

    if ($("#Experience option:selected").val() > -1) {
        $("#UpdateMsg").html("Please click the Continue button if form is complete");
        $("#ContinueButton").prop("disabled", false);
        $("#Experience").focus();
    }
    else {
        $("#UpdateMsg").html("Select Driving Experience");
        $("#ContinueButton").prop("disabled", true);
        $("#Experience").focus();
    }
}

function ProcessRegistration()
{
    $("#form1").submit();
}
