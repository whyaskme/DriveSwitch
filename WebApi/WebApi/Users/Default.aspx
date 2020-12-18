<%@ Page Language="C#" AutoEventWireup="true" CodeFile="Default.aspx.cs" Inherits="Members_Default" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width" />
    <title>ATXTNCU New Driver Application</title>
    <link href="../Content/bootstrap.css" rel="stylesheet"/>
    <link href="../ZurbFoundation/css/foundation.css" rel="stylesheet" />
    <link href="../Content/ATXTNCU.css" rel="stylesheet" />
    <script src="../Scripts/modernizr-2.8.3.js"></script>
</head>
<body>
    
    <form id="form1" runat="server">
    
    <div class="container body-content">
        
        <div class="jumbotron" style="height: auto; text-align: center;">

            <div class="row" style="margin-bottom: 0px;">
                <div class="large-12 medium-12 small-12 columns">
                    Registration is a simple 3-step process. Please fill out the form below and click continue.
                </div>
            </div>

            <div class="row" style="margin-bottom: 15px;">
                <div id="UpdateMsg" class="large-12 medium-12 small-12 columns">
                    &nbsp;
                </div>
            </div>

            <div class="row">
                <div class="large-12 medium-12 small-12 columns" style="font-weight: bold; color: #fea108; text-align: left;">
                    STEP 1
                </div>
            </div>

            <div class="row">
                <div class="large-6 medium-6 small-12 columns">
                    <label>First Name
                        <input type="text" id="FirstName" runat="server" onkeyup="javascript: ValidateFirstName();" value="" />
                    </label>
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>Last Name
                        <input type="text" id="LastName" runat="server" onkeyup="javascript: ValidateLastName();" value="" />
                    </label>
                </div>
            </div>

            <div class="row">
                <div class="large-6 medium-6 small-12 columns">
                    <label>Email
                        <input type="text" id="Email" runat="server" onkeyup="javascript: ValidateEmail();" value="" />
                    </label>
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>Mobile #
                        <input type="text" id="Mobile" runat="server" onkeyup="javascript: ValidatePhone();" value="" />
                    </label>
                </div>
            </div>

            <div class="row">
                <div class="large-6 medium-6 small-12 columns">
                    <label>Who referred you?
                        <input type="text" id="RefName" runat="server" onkeyup="javascript: ValidateReferralName();" value="" />
                    </label>
                </div>
                <div class="large-6 medium-6 small-12 columns">
                    <label>Driving Experience
                        <select id="Experience" runat="server" onchange="javascript: ValidateExperience();">
                            <option value="-1">Yrs of Experience</option>
                            <option value="0">&lt; 1 yr</option>
                            <option value="1">1 yr</option>
                            <option value="2">2 yrs</option>
                            <option value="3">3 yrs</option>
                            <option value="4">4 yrs</option>
                            <option value="5">5 yrs</option>
                            <option value="6">&gt; 5 yrs</option>
                        </select>
                    </label>
                </div>
            </div>

            <div class="row" style="margin-bottom: 50px;">
                <div class="large-12 medium-12 small-12 columns" style="padding: 25px; margin-bottom: 50px; position: relative; top: 25px; text-align: center; width: 100%;">
                    <input id="ContinueButton" type="button" onclick="javascript: ValidateForm();" value="Continue" />
<%--                    <button id="RegisterBtn" onclick="javascript: ValidateForm();">Continue</button>--%>
                </div>
            </div>

<%--            <div class="row" style="border-top: solid 1px #c0c0c0; padding-top: 25px;">
                <div class="large-12 medium-12 small-12 columns">
                    <footer>
                        <div style="width: 100%; text-align: center;">&copy; 2016 - ATXTNCU. All rights reserved.</div>
                    </footer>
                </div>
            </div>--%>

        </div>

    </div>

    <script src="../Scripts/bootstrap.js"></script>
    <script src="../Scripts/respond.js"></script>
    <script src="../Scripts/JQuery/jquery-3.1.0.js"></script>
    <script src="../Scripts/ATXTNCU.js"></script>
    <script>
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

      ga('create', 'UA-84296399-1', 'auto');
      ga('send', 'pageview');
    </script>

    </form>

</body>
</html>


