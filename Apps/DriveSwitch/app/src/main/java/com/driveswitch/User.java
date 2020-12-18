package com.driveswitch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class User
{
    public User(JSONObject serializedUserData)
    {
        _t = "User";

        try
        {
            _id = serializedUserData.getString("_id");

            UserId = _id;

            String regDate = serializedUserData.getString("RegistrationDate");
            RegistrationDate = serializedUserData.getString("RegistrationDate");
            Expired = serializedUserData.getBoolean("Expired");
            ExpireDate = serializedUserData.getString("ExpireDate");
            DeviceType = serializedUserData.getInt("DeviceType");
            IsLoggedIn = serializedUserData.getBoolean("Expired");
            FirstName = serializedUserData.getString("FirstName");
            LastName = serializedUserData.getString("LastName");
            Pwd = serializedUserData.getString("Pwd");
            Gender = serializedUserData.getInt("Gender");

            JSONObject contactObject = serializedUserData.getJSONObject("Contact");

            Contact = new ContactInfo(contactObject);

            // Set address info
            JSONObject addressObject = contactObject.getJSONObject("Address");
            Contact.Address.CountryId = addressObject.getString("CountryId");
            Contact.Address.StateId = addressObject.getString("StateId");
            Contact.Address.CountyId = addressObject.getString("CountyId");
            Contact.Address.CityId = addressObject.getString("CityId");
            Contact.Address.ZipCode = addressObject.getString("ZipCode");
            Contact.Address.TimeZoneId = addressObject.getString("TimeZoneId");
            Contact.Address.Address1 = addressObject.getString("Address1");
            Contact.Address.Address2 = addressObject.getString("Address2");

            // Set phone info
            JSONObject phoneObject = contactObject.getJSONObject("Phone");
            Contact.Phone.CountryCode = phoneObject.getString("CountryCode");
            Contact.Phone.PhoneType = phoneObject.getInt("PhoneType");
            Contact.Phone.AreaCode = phoneObject.getInt("AreaCode");
            Contact.Phone.Exchange = phoneObject.getString("Exchange");
            Contact.Phone.Number = phoneObject.getString("Number");

            // Set phone info
            JSONObject emailObject = contactObject.getJSONObject("Email");
            Contact.Email.UserName = emailObject.getString("UserName");
            Contact.Email.Domain = emailObject.getString("Domain");

            // Loop through TNCs
            TNCs = new ArrayList<TNC>();
            JSONArray tncs = serializedUserData.getJSONArray("TNCs");

            // Sort TNC list
            JSONArray tncSortedList = sortJsonArray(tncs);

            if(tncSortedList.length() > 0)
            {
                for(Integer i = 0; i < tncSortedList.length(); i++)
                {
                    JSONObject currentTNC = tncSortedList.getJSONObject(i);

                    TNC _tnc = new TNC();
                    _tnc._id = currentTNC.getString("_id");
                    _tnc.Name = currentTNC.getString("Name");

                    TNCs.add(_tnc);
                }
            }

            // Loop through Roles
            Roles = new ArrayList<UserRole>();
            JSONArray roles = serializedUserData.getJSONArray("Roles");
            if(roles.length() > 0)
            {
                for(Integer i = 0; i < roles.length(); i++)
                {
                    UserRole newUserRole = new UserRole();

                    JSONObject assignedRole = roles.getJSONObject(i);

                    newUserRole._id = assignedRole.getString("_id");
                    newUserRole.Name = assignedRole.getString("Name");

                    Roles.add(newUserRole);
                }
            }

            // Loop through Credit Cards
            CreditCards = new ArrayList<CreditCard>();
            JSONArray creditCards = serializedUserData.getJSONArray("CreditCards");
            if(creditCards.length() > 0)
            {
                for(Integer i = 0; i < creditCards.length(); i++)
                {
                    CreditCard newCreditCard = new CreditCard();

                    JSONObject assignedCreditCard = creditCards.getJSONObject(i);

                    newCreditCard._id = assignedCreditCard.getString("_id");
                    newCreditCard._t = assignedCreditCard.getString("_t");
                    newCreditCard.FullName = assignedCreditCard.getString("FullName");
                    newCreditCard.CardTypeId = assignedCreditCard.getString("CardTypeId");
                    newCreditCard.CardTypeName = assignedCreditCard.getString("CardTypeName");
                    newCreditCard.Number = assignedCreditCard.getString("Number");
                    newCreditCard.Expires = assignedCreditCard.getString("Expires");
                    newCreditCard.Zipcode = assignedCreditCard.getString("Zipcode");
                    newCreditCard.CVVCode = assignedCreditCard.getString("CVVCode");

                    CreditCards.add(newCreditCard);
                }
            }

            // Loop through Transactions
            Transactions = new ArrayList<Transaction>();
            JSONArray transactions = serializedUserData.getJSONArray("Transactions");
            if(transactions.length() > 0)
            {
                for(Integer i = 0; i < transactions.length(); i++)
                {
                    Transaction newTransaction = new Transaction();

                    JSONObject assignedTransaction = transactions.getJSONObject(i);

                    String transactionDate = Utilities.formatUTCDate(assignedTransaction.getString("Date"));

                    // Formatting problem
                    if(transactionDate == "")
                        transactionDate = assignedTransaction.getString("Date");

                    newTransaction.Date = new Date(transactionDate);
                    newTransaction.Type = assignedTransaction.getString("Type");
                    newTransaction.Amount = assignedTransaction.getString("Amount");

                    Transactions.add(newTransaction);
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public class TNCNameComparator implements Comparator<TNC>
    {
        public int compare(TNC left, TNC right) {
            return left.Name.compareTo(right.Name);
        }
    }

    public static String UserId;

    public String _id;
    public String _t;
    public String RegistrationDate;
    public Boolean Expired;
    public String ExpireDate;
    public Integer DeviceType;
    public Boolean IsLoggedIn;
    public String FirstName;
    public String LastName;
    public String Pwd;
    public Integer Gender;
    public ContactInfo Contact;
    public List<TNC> TNCs;
    public List<UserRole> Roles;
    public List<CreditCard> CreditCards;
    public List<Transaction> Transactions;

    public static JSONArray sortJsonArray(JSONArray array)
    {
        List<JSONObject> jsons = new ArrayList<JSONObject>();

        for (int i = 0; i < array.length(); i++) {
            try
            {
                jsons.add(array.getJSONObject(i));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject leftObject, JSONObject rightObject) {
                String lid = null;
                try
                {
                    lid = leftObject.getString("Name");
                    String rid = rightObject.getString("Name");
                    // Here you could parse string id to integer and then compare.
                    return lid.compareTo(rid);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        return new JSONArray(jsons);

    }
}
