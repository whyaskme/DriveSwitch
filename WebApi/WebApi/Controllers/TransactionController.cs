using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using System.Web.Http;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;

using Newtonsoft;

using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;

using Data.Repositories.Models;
using Data.Repositories.MongoDB;

namespace WebApi.Controllers
{
    /// <summary>
    /// User account Transactions REQUIRE GPS coordinates: latitude and longitude as input parameters for logging purposes.
    /// </summary>
    /// <returns>JSON</returns>
    public class TransactionController : ApiController
    {
        Repo _repo = new Repo();

        // GET: api/Transaction
        /// <summary>
        /// General transaction sumaries. Type "RenewalPeriods" returns subscription periods.
        /// </summary>
        /// <param name="requestType"></param>
        /// <returns>JSON</returns>
        public HttpResponseMessage Get(string requestType)
        {
            var jsonResponse = "";

            try
            {
                switch(requestType)
                {
                    case "RenewalPeriods":
                        jsonResponse = _repo.GetSubscriptionRenewalPeriods().ToJson();
                        break;
                }

                jsonResponse = _repo.SanitizeJsonString(jsonResponse);

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(jsonResponse, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }

        // POST: api/Transaction
        /// <summary>
        /// User subscription renewal transaction. Pass Transaction object in post data.
        /// </summary>
        /// <param name="userId"></param>
        /// <param name="transaction"></param>
        /// <returns>JSON</returns>
        public HttpResponseMessage Post(string userId, [FromBody]string transaction)
        {
            try
            {
                User _user = _repo.GetUser(userId);

                dynamic deserializedTransaction = Newtonsoft.Json.JsonConvert.DeserializeObject(transaction);

                #region populate credit card

                    CreditCard myCard = new CreditCard();
                    myCard.FullName = deserializedTransaction.PaymentCard.FullName;
                    myCard.Number = deserializedTransaction.PaymentCard.Number;
                    myCard.Expires = deserializedTransaction.PaymentCard.Expires;
                    myCard.Zipcode = deserializedTransaction.PaymentCard.Zipcode;
                    myCard.CVVCode = deserializedTransaction.PaymentCard.CVVCode;

                #endregion

                #region Determine car type from number

                var cardType = _repo.GetCardType(myCard.Number);
                    switch (cardType.ToString())
                    {
                        case "Unknown":
                            myCard.CardTypeId = Constants.Transaction.CreditCard.Unknown.Item1;
                            myCard.CardTypeName = Constants.Transaction.CreditCard.Unknown.Item2;
                            break;

                        case "MasterCard":
                            myCard.CardTypeId = Constants.Transaction.CreditCard.MasterCard.Item1;
                            myCard.CardTypeName = Constants.Transaction.CreditCard.MasterCard.Item2;
                            break;

                        case "VISA":
                            myCard.CardTypeId = Constants.Transaction.CreditCard.VISA.Item1;
                            myCard.CardTypeName = Constants.Transaction.CreditCard.VISA.Item2;
                            break;

                        case "Amex":
                            myCard.CardTypeId = Constants.Transaction.CreditCard.Amex.Item1;
                            myCard.CardTypeName = Constants.Transaction.CreditCard.Amex.Item2;
                            break;

                        case "Discover":
                            myCard.CardTypeId = Constants.Transaction.CreditCard.Discover.Item1;
                            myCard.CardTypeName = Constants.Transaction.CreditCard.Discover.Item2;
                            break;

                        case "DinersClub":
                            myCard.CardTypeId = Constants.Transaction.CreditCard.DinersClub.Item1;
                            myCard.CardTypeName = Constants.Transaction.CreditCard.DinersClub.Item2;
                            break;

                        case "JCB":
                            myCard.CardTypeId = Constants.Transaction.CreditCard.JCB.Item1;
                            myCard.CardTypeName = Constants.Transaction.CreditCard.JCB.Item2;
                            break;

                        case "enRoute":
                            myCard.CardTypeId = Constants.Transaction.CreditCard.enRoute.Item1;
                            myCard.CardTypeName = Constants.Transaction.CreditCard.enRoute.Item2;
                            break;
                    }

                #endregion

                #region save credit card if requested

                    if (Convert.ToBoolean(deserializedTransaction.SavePaymentMethod))
                    {
                        Boolean cardExists = false;
                        if (_user.CreditCards.Count > 0)
                        {
                            foreach (CreditCard _card in _user.CreditCards)
                            {
                                if (_repo.NormalizeCardNumber(_card.Number) == _repo.NormalizeCardNumber(myCard.Number))
                                    cardExists = true;
                            }
                        }

                        if (!cardExists)
                            _user.CreditCards.Add(myCard);
                    }

                #endregion

                RenewalPeriod _renewalPeriod = _repo.GetRenewalPeriod(Convert.ToInt16(deserializedTransaction.RenewalPeriod));

                Transaction myTransaction = new Transaction();
                myTransaction.Date = DateTime.UtcNow;
                myTransaction.Type = _renewalPeriod.Name;
                myTransaction.Amount = _renewalPeriod.Amount;
                myTransaction.ProcessorId = Constants.Transaction.PaymentProcessor.PayPal.Item1; // PayPal
                myTransaction.PaymentMethodId = myCard._id;
                myTransaction.PaymentCard = myCard;

                // Submit to PayPal for processing here...
                myTransaction.ResultCode = 0;
                myTransaction.ResultName = "Success";
                myTransaction.ResultDetails = "Payment was successful";

                // If payment successful, renew user and expire date
                if (myTransaction.ResultName == "Success")
                {
                    _user.Expired = false;
                    switch (_renewalPeriod.Period)
                    {
                        case 1:
                            myTransaction.Type = "Month";
                            _user.ExpireDate = DateTime.UtcNow.AddDays(30); // Month
                            break;
                        case 2:
                            myTransaction.Type = "Quarter";
                            _user.ExpireDate = DateTime.UtcNow.AddDays(90); // Quarter
                            break;
                        case 3:
                            myTransaction.Type = "Annual";
                            _user.ExpireDate = DateTime.UtcNow.AddDays(365); // Annual
                            break;
                    }
                }

                _user.Transactions.Add(myTransaction);

                _repo.UpdateUser(_user, 0.00, 0.00);

                var jsonResponse = _repo.SanitizeJsonString(_user.ToJson());

                var response = Request.CreateResponse(HttpStatusCode.OK);
                response.Content = new StringContent(jsonResponse, System.Text.Encoding.UTF8, "application/json");

                return response;
            }
            catch (Exception ex)
            {
                var response = Request.CreateResponse(HttpStatusCode.NotFound);
                response.Content = new StringContent(ex.ToJson(), System.Text.Encoding.UTF8, "application/json");

                return response;
            }
        }

        // PUT: api/Transaction/5
        //public HttpResponseMessage Put(int id, [FromBody]string value)
        //{
        //}

        // DELETE: api/Transaction/5
        //public void Delete(int id)
        //{
        //}
    }
}
