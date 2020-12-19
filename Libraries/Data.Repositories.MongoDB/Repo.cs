using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Core;

using Data.Repositories.Models;
using TimeZone = Data.Repositories.Models.TimeZone;

using System.Web.UI.WebControls;

using System.Text.RegularExpressions;
using System.Net.Mail;

namespace Data.Repositories.MongoDB
{
    public class Repo
    {

        #region Properties

        Utils _utils = new Utils();

        IMongoDatabase _mongoDatabase;
        IMongoClient _mongoClient;

        IMongoCollection<SourceData> _mongoSourceDataCollection;

        IMongoCollection<Data.Repositories.Models.TimeZone> _mongoTimeZoneCollection;
        IMongoCollection<Country> _mongoCountryCollection;
        IMongoCollection<State> _mongoStateCollection;
        IMongoCollection<County> _mongoCountyCollection;
        IMongoCollection<City> _mongoCityCollection;
        IMongoCollection<ZipCode> _mongoZipCodeCollection;
        IMongoCollection<AreaCode> _mongoAreaCodeCollection;
        IMongoCollection<Event> _mongoEventCollection;
        IMongoCollection<Location> _mongoLocationCollection;
        IMongoCollection<User> _mongoUserCollection;
        IMongoCollection<Name> _mongoNameCollection;
        IMongoCollection<Notification> _mongoNotificationCollection;
        IMongoCollection<TNC> _mongoTNCCollection;
        IMongoCollection<UserRole> _mongoUserRoleCollection;
        IMongoCollection<RenewalPeriod> _mongoRenewalPeriodsCollection;
        IMongoCollection<Message> _mongoMessageCollection;

        string _dbConnectionString = "";

        #endregion

        public Repo()
        {
            try
            {
                bool _useLocalHostDB = Convert.ToBoolean(ConfigurationManager.AppSettings["UseLocalHost"]);

                //_dbConnectionString = ConfigurationManager.ConnectionStrings["MongoServer"].ConnectionString;


                var client = new MongoClient("mongodb://DriveSwitch:%2113324BossWood@localhost:27017/?serverSelectionTimeoutMS=5000&connectTimeoutMS=10000&authSource=DriveSwitch&authMechanism=SCRAM-SHA-256");
                var database = client.GetDatabase("DriveSwitch");


                if (_useLocalHostDB)
                    _dbConnectionString = ConfigurationManager.ConnectionStrings["LocalMongoServer"].ConnectionString;
                else
                    _dbConnectionString = ConfigurationManager.ConnectionStrings["MongoServer"].ConnectionString;

                _mongoClient = new MongoClient(_dbConnectionString);
                _mongoDatabase = _mongoClient.GetDatabase(ConfigurationManager.AppSettings["MongoDbName"]);
            }
            catch(Exception ex)
            {
                var errMsg = ex.Message;
            }
        }

        #region MongoDB Methods

        public List<Country> GetCountryList()
        {
            try
            {
                _mongoCountryCollection = _mongoDatabase.GetCollection<Country>("Countries");

                List<Country> _countryList = _mongoCountryCollection.Find<Country>(s => s.Enabled == true).SortBy(s => s.Name).ToListAsync<Country>().Result;

                return _countryList;
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
                return null;
            }
        }

        public List<ListItem> GetStateList(string countryId)
        {
            List<ListItem> _stateList = new List<ListItem>();
            try
            {
                _mongoStateCollection = _mongoDatabase.GetCollection<State>("States");

                var _states = _mongoStateCollection.Find<State>(s => s.CountryId == ObjectId.Parse(countryId)).SortBy(s => s.Name).ToListAsync<State>().Result;
                foreach (State _state in _states)
                {
                    ListItem _li = new ListItem();
                    _li.Text = _state.Name;
                    _li.Value = _state._id.ToString();
                    _stateList.Add(_li);
                }
            }
            catch (Exception ex)
            {
                ListItem _li = new ListItem();
                _li.Text = ex.ToString();
                _li.Value = ObjectId.Empty.ToString();
                _stateList.Add(_li);
            }
            return _stateList;
        }

        public List<ListItem> GetCountyList(string stateId)
        {
            List<ListItem> _countyList = new List<ListItem>();

            try
            {
                _mongoCountyCollection = _mongoDatabase.GetCollection<County>("Counties");

                var _counties = _mongoCountyCollection.Find<County>(s => s.StateId == ObjectId.Parse(stateId)).SortBy(s => s.Name).ToListAsync<County>().Result;
                foreach (County _county in _counties)
                {
                    ListItem _li = new ListItem();
                    _li.Text = _county.Name;
                    _li.Value = _county._id.ToString();
                    _countyList.Add(_li);
                }
            }
            catch (Exception ex)
            {
                ListItem _li = new ListItem();
                _li.Text = ex.ToString();
                _li.Value = ObjectId.Empty.ToString();
                _countyList.Add(_li);
            }
            return _countyList;
        }

        public List<ListItem> GetCityList(Int16 regionTypeId, ObjectId regionId)
        {
            if (regionId == null)
                regionId = Constants.DefaultRegionId;

            List<ListItem> _cityList = new List<ListItem>();
            List<City> _cities = new List<City>();

            try
            {
                _mongoCityCollection = _mongoDatabase.GetCollection<City>("Cities");

                if (regionTypeId == Constants.DefaultStateType)
                    _cities = _mongoCityCollection.Find<City>(s => s.StateId == regionId).SortBy(s => s.Name).ToListAsync<City>().Result;
                else
                    _cities = _mongoCityCollection.Find<City>(s => s.CountyId == regionId).SortBy(s => s.Name).ToListAsync<City>().Result;

                foreach (City _city in _cities)
                {
                    ListItem _li = new ListItem();
                    _li.Text = _city.Name;
                    _li.Value = _city._id.ToString();
                    _cityList.Add(_li);
                }
            }
            catch (Exception ex)
            {
                ListItem _li = new ListItem();
                _li.Text = ex.ToString();
                _li.Value = ObjectId.Empty.ToString();
                _cityList.Add(_li);
            }
            return _cityList;
        }

        public ZipCode SearchStateCountyCityByZipCode(string searchZip)
        {
            List<ZipCode> _zipCodes = new List<ZipCode>();
            try
            {
                _mongoZipCodeCollection = _mongoDatabase.GetCollection<ZipCode>("ZipCodes");

                _zipCodes = _mongoZipCodeCollection.Find<ZipCode>(s => s.Zip == Convert.ToInt32(searchZip)).ToListAsync<ZipCode>().Result;

                foreach (ZipCode _zipCode in _zipCodes)
                {
                    return _zipCode;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
                return null;
            }
            return null;
        }

        public List<ListItem> GetZipCodeList(Int16 regionTypeId, ObjectId regionId)
        {
            List<ListItem> _zipCodeList = new List<ListItem>();
            List<ZipCode> _zipCodes = new List<ZipCode>();

            try
            {
                _mongoZipCodeCollection = _mongoDatabase.GetCollection<ZipCode>("ZipCodes");

                if (regionTypeId == Constants.DefaultStateType)
                    _zipCodes = _mongoZipCodeCollection.Find<ZipCode>(s => s.StateId == regionId).SortBy(s => s.Zip).ToListAsync<ZipCode>().Result;
                else if (regionTypeId == Constants.DefaultStateType)
                    _zipCodes = _mongoZipCodeCollection.Find<ZipCode>(s => s.CountyId == regionId).SortBy(s => s.Zip).ToListAsync<ZipCode>().Result;
                else
                    _zipCodes = _mongoZipCodeCollection.Find<ZipCode>(s => s.CityId == regionId).SortBy(s => s.Zip).ToListAsync<ZipCode>().Result;

                foreach (ZipCode _zipCode in _zipCodes)
                {
                    ListItem _li = new ListItem();
                    var _zipStr = _zipCode.Zip.ToString();
                    if (_zipStr.Length < 5)
                        _zipStr = "0" + _zipStr;

                    _li.Text = _zipStr;
                    _li.Value = _zipStr;
                    _zipCodeList.Add(_li);
                }
            }
            catch (Exception ex)
            {
                ListItem _li = new ListItem();
                _li.Text = ex.ToString();
                _li.Value = ObjectId.Empty.ToString();
                _zipCodeList.Add(_li);
            }
            return _zipCodeList;
        }

        public List<TNCList> GetTNCList()
        {
            List<TNCList> _tncList = new List<TNCList>();
            List<TNC> _tncs = new List<TNC>();

            try
            {
                _mongoTNCCollection = _mongoDatabase.GetCollection<TNC>("TNCs");

                _tncs = _mongoTNCCollection.Find<TNC>(s => s.Enabled == true).SortBy(s => s.Name).ToListAsync<TNC>().Result;

                foreach (TNC _tnc in _tncs)
                {
                    TNCList _li = new TNCList();
                    _li._id = _tnc._id;
                    _li.Name = _tnc.Name;
                    _tncList.Add(_li);
                }
            }
            catch (Exception ex)
            {
                TNCList _li = new TNCList();
                _li._id = ObjectId.Empty;
                _li.Name = ex.ToString();
                _tncList.Add(_li);
            }
            return _tncList;
        }

        public TNC GetTNCById(string tncId)
        {
            List<TNC> _tncs = new List<TNC>();
            //List<TNC> _tncList = new List<TNC>();

            try
            {
                _mongoTNCCollection = _mongoDatabase.GetCollection<TNC>("TNCs");
                _tncs = _mongoTNCCollection.Find<TNC>(s => s._id == ObjectId.Parse(tncId) && s.Enabled == true).SortBy(s => s.Name).ToListAsync<TNC>().Result;
                foreach (TNC _tnc in _tncs)
                {
                    //_tncList.Add(_tnc);
                    return _tnc;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
                return null;
            }

            return null;
        }

        public TNC GetTNCByName(string tncName)
        {
            List<TNC> _tncs = new List<TNC>();

            try
            {
                _mongoTNCCollection = _mongoDatabase.GetCollection<TNC>("TNCs");
                _tncs = _mongoTNCCollection.Find<TNC>(s => s.Name == tncName.Trim() && s.Enabled == true).SortBy(s => s.Name).ToListAsync<TNC>().Result;

                foreach (TNC _tnc in _tncs)
                {
                    //_tncList.Add(_tnc);
                    return _tnc;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
                return null;
            }
            return null;
        }

        public void UpdateCounties()
        {
            try
            {
                _mongoCountyCollection = _mongoDatabase.GetCollection<County>("Counties");
                var _counties = _mongoCountyCollection.Find<County>(s => s._t == "County").SortBy(s => s.State).ToListAsync<County>().Result;
                foreach (County _county in _counties)
                {
                    _mongoStateCollection = _mongoDatabase.GetCollection<State>("States");
                    var _states = _mongoStateCollection.Find<State>(s => s.Name == _county.State).ToListAsync<State>().Result;
                    foreach (State _state in _states)
                    {
                        _county.StateId = _state._id;
                        _county.TimeZoneId = ObjectId.Empty;

                        var replaceOneResult2 = _mongoCountyCollection.ReplaceOneAsync(s => s._id == _county._id, _county);
                    }
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
            }
        }

        public void CreateAreaCodes()
        {
            try
            {
                _mongoZipCodeCollection = _mongoDatabase.GetCollection<ZipCode>("ZipCodes");
                _mongoAreaCodeCollection = _mongoDatabase.GetCollection<AreaCode>("AreaCodes");

                var _zipcodes = _mongoZipCodeCollection.Find<ZipCode>(s => s._t == "ZipCode").SortBy(s => s.Zip).ToListAsync<ZipCode>().Result;
                foreach (ZipCode _zipCode in _zipcodes)
                {
                    if (_zipCode.AreaCodes != null)
                    {
                        if (_zipCode.AreaCodes.Contains(","))
                        {
                            var _tmpAreaCodes = _zipCode.AreaCodes.Split(',');
                            foreach (string _tmpAreaCode in _tmpAreaCodes)
                            {
                                var _areaCodes = _mongoAreaCodeCollection.Find<AreaCode>(s => s.AreaCodeNumber == Convert.ToInt16(_tmpAreaCode) && s.CityId == _zipCode.CityId).ToListAsync<AreaCode>().Result;
                                if (_areaCodes.Count < 1)
                                {
                                    // Create the AreaCode
                                    AreaCode _newAreaCode = new AreaCode();
                                    _newAreaCode.AreaCodeNumber = Convert.ToInt16(_tmpAreaCode);
                                    _newAreaCode.CityId = _zipCode.CityId;
                                    _newAreaCode.CountryId = _zipCode.CountryId;
                                    _newAreaCode.CountyId = _zipCode.CountyId;
                                    _newAreaCode.EstimatedPopulation = _zipCode.EstimatedPopulation;
                                    _newAreaCode.Latitude = _zipCode.Latitude;
                                    _newAreaCode.Longitude = _zipCode.Longitude;
                                    _newAreaCode.StateId = _zipCode.StateId;
                                    _newAreaCode.TimeZoneId = _zipCode.TimeZoneId;

                                    _mongoAreaCodeCollection.InsertOne(_newAreaCode);
                                }
                            }
                        }
                        else
                        {
                            var _areaCodes = _mongoAreaCodeCollection.Find<AreaCode>(s => s.AreaCodeNumber == Convert.ToInt16(_zipCode.AreaCodes) && s.CityId == _zipCode.CityId).ToListAsync<AreaCode>().Result;
                            if (_areaCodes.Count < 1)
                            {
                                // Create the AreaCode
                                AreaCode _newAreaCode = new AreaCode();
                                _newAreaCode.AreaCodeNumber = Convert.ToInt16(_zipCode.AreaCodes);
                                _newAreaCode.CityId = _zipCode.CityId;
                                _newAreaCode.CountryId = _zipCode.CountryId;
                                _newAreaCode.CountyId = _zipCode.CountyId;
                                _newAreaCode.EstimatedPopulation = _zipCode.EstimatedPopulation;
                                _newAreaCode.Latitude = _zipCode.Latitude;
                                _newAreaCode.Longitude = _zipCode.Longitude;
                                _newAreaCode.StateId = _zipCode.StateId;
                                _newAreaCode.TimeZoneId = _zipCode.TimeZoneId;

                                _mongoAreaCodeCollection.InsertOne(_newAreaCode);
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
            }
        }

        public ZipCode GetZipByValue(Int32 zipCodeValue)
        {
            try
            {
                _mongoZipCodeCollection = _mongoDatabase.GetCollection<ZipCode>("ZipCodes");
                var _zipCodes = _mongoZipCodeCollection.Find<ZipCode>(s => s.Zip == zipCodeValue).ToListAsync<ZipCode>().Result;
                foreach (ZipCode _zipCode in _zipCodes)
                {
                    return _zipCode;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
            }

            return null;
        }

        public ZipCode GetTimeZoneIdByZipCodeId(ObjectId zipCodeId)
        {
            try
            {
                _mongoZipCodeCollection = _mongoDatabase.GetCollection<ZipCode>("ZipCodes");
                var _zipCodes = _mongoZipCodeCollection.Find<ZipCode>(s => s._id == zipCodeId).ToListAsync<ZipCode>().Result;
                foreach (ZipCode _zipCode in _zipCodes)
                {
                    return _zipCode;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;

                return null;
            }
            return null;
        }

        public void FixCountyStateIds()
        {
            try
            {
                _mongoStateCollection = _mongoDatabase.GetCollection<State>("States");
                _mongoCountyCollection = _mongoDatabase.GetCollection<County>("Counties");

                var _states = _mongoStateCollection.Find<State>(s => s._t == "State").SortBy(s => s.Name).ToListAsync<State>().Result;
                foreach (State _state in _states)
                {
                    var _counties = _mongoCountyCollection.Find<County>(s => s.State == _state.Name).ToListAsync<County>().Result;
                    foreach (County _county in _counties)
                    {
                        _county.StateId = _state._id;
                        var replaceOneResult = _mongoCountyCollection.ReplaceOneAsync(s => s._id == _county._id, _county);
                    }
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
            }
        }

        public void UpdateZipCodeCityIds()
        {
            var stateId = ObjectId.Empty;
            var stateTimeZoneId = ObjectId.Empty;
            var stateName = "";
            var stateAbbr = "";

            try
            {
                _mongoSourceDataCollection = _mongoDatabase.GetCollection<SourceData>("New_ZipCodes_Source");
                _mongoStateCollection = _mongoDatabase.GetCollection<State>("States");
                _mongoCountyCollection = _mongoDatabase.GetCollection<County>("Counties");
                _mongoCityCollection = _mongoDatabase.GetCollection<City>("Cities");

                var _sourceData = _mongoSourceDataCollection.Find<SourceData>(s => s._t == "SourceData").SortBy(s => s.StateAbbr).ToListAsync<SourceData>().Result;
                foreach (SourceData _source in _sourceData)
                {
                    // Get State name from abbr
                    var _states = _mongoStateCollection.Find<State>(s => s.Abbr == _source.StateAbbr).ToListAsync<State>().Result;
                    foreach (State _state in _states)
                    {
                        stateId = _state._id;
                        stateTimeZoneId = _state.TimeZoneId;
                        stateName = _state.Name;
                        stateAbbr = _state.Abbr;
                    }

                    // Fix CountyId
                    var _counties = _mongoCountyCollection.Find<County>(s => s.Name == _source.County && s.StateId == stateId).ToListAsync<County>().Result;
                    if (_counties.Count > 0)
                    {
                        foreach (County _county in _counties)
                        {
                            _source.CountyId = _county._id;
                        }
                    }
                    else
                    {
                        // Create the missing County
                        County _newCounty = new County();
                        _newCounty.Name = _source.County;
                        _newCounty.State = stateName;
                        _newCounty.StateId = stateId;
                        _newCounty.TimeZoneId = stateTimeZoneId;

                        _mongoCountyCollection.InsertOne(_newCounty);

                        _source.CountyId = _newCounty._id;
                    }

                    // Fix CityId
                    var _cities = _mongoCityCollection.Find<City>(s => s.Name == _source.City && s.StateId == _source.StateId).ToListAsync<City>().Result;
                    foreach (City _city in _cities)
                    {
                        _city.CountyId = _source.CountyId;
                        var replaceCityResult = _mongoCityCollection.ReplaceOneAsync(s => s._id == _city._id, _city);

                        _source.CityId = _city._id;
                    }

                    var replaceOneResult = _mongoSourceDataCollection.ReplaceOneAsync(s => s._id == _source._id, _source);
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
            }
        }

        public ObjectId GetCountryIdByName(string _name)
        {
            ObjectId countryId = ObjectId.Empty;

            return countryId;
        }

        public ObjectId GetStateIdByName(string _name)
        {
            ObjectId stateId = ObjectId.Empty;

            return stateId;
        }

        public ObjectId GetCountyIdByName(string _name)
        {
            ObjectId countyId = ObjectId.Empty;

            return countyId;
        }

        public ObjectId GetCityIdByName(string _name)
        {
            ObjectId cityId = ObjectId.Empty;

            return cityId;
        }

        public City GetCityByZipCode(Int32 zipCode)
        {
            _mongoZipCodeCollection = _mongoDatabase.GetCollection<ZipCode>("ZipCodes");
            var _zipCodes = _mongoZipCodeCollection.Find<ZipCode>(s => s.Zip == zipCode).ToListAsync<ZipCode>().Result;
            foreach (ZipCode _zipCode in _zipCodes)
            {
                _mongoCityCollection = _mongoDatabase.GetCollection<City>("Cities");
                var _cities = _mongoCityCollection.Find<City>(s => s._id == _zipCode.CityId).ToListAsync<City>().Result;
                foreach (City _city in _cities)
                {
                    return _city;
                }
            }

            return null;
        }

        public City GetCityByName(string _name)
        {
            _mongoCityCollection = _mongoDatabase.GetCollection<City>("Cities");
            var _cities = _mongoCityCollection.Find<City>(s => s.Name == _name).ToListAsync<City>().Result;
            foreach (City _city in _cities)
            {
                return _city;
            }

            return null;
        }

        public ObjectId CreateCity(ObjectId countryId, string timeZoneName, string stateName, string countyName, string cityName)
        {
            ObjectId _timeZoneId = ObjectId.Empty;
            ObjectId _stateId = ObjectId.Empty;
            ObjectId _countyId = ObjectId.Empty;
            ObjectId _cityId = ObjectId.Empty;

            try
            {
                _mongoTimeZoneCollection = _mongoDatabase.GetCollection<TimeZone>("TimeZones");
                var _timezones = _mongoTimeZoneCollection.Find<TimeZone>(s => s.Name == timeZoneName).ToListAsync<TimeZone>().Result;
                foreach (TimeZone _timezone in _timezones)
                    _timeZoneId = _timezone._id;

                _mongoStateCollection = _mongoDatabase.GetCollection<State>("States");
                var _states = _mongoStateCollection.Find<State>(s => s.Abbr == stateName).ToListAsync<State>().Result;
                foreach (State _state in _states)
                    _stateId = _state._id;

                _mongoCountyCollection = _mongoDatabase.GetCollection<County>("Counties");
                var _counties = _mongoCountyCollection.Find<County>(s => s.Name == countyName && s.StateId == _stateId).ToListAsync<County>().Result;
                foreach (County _county in _counties)
                {
                    _countyId = _county._id;

                    // Update County TimeZoneId
                    _county.TimeZoneId = _timeZoneId;
                    var replaceOneResult = _mongoCountyCollection.ReplaceOneAsync(s => s._id == _county._id, _county);
                }

                // Get City by Name and CountyId to see if it exists, if not create it and return ObjectId
                _mongoCityCollection = _mongoDatabase.GetCollection<City>("Cities");
                var _cities = _mongoCityCollection.Find<City>(s => s.Name == cityName && s.CountyId == _countyId).ToListAsync<City>().Result;
                if (_cities.Count < 1)
                {
                    City _newCity = new City();

                    _cityId = _newCity._id;

                    _newCity.Name = cityName;

                    _newCity.CountryId = countryId;
                    _newCity.StateId = _stateId;
                    _newCity.CountyId = _countyId;
                    _newCity.TimeZoneId = _timeZoneId;

                    _mongoCityCollection.InsertOne(_newCity);
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
            }

            return _cityId;
        }

        public void CreateSampleData()
        {
            try
            {
                _mongoSourceDataCollection = _mongoDatabase.GetCollection<SourceData>("New_ZipCodes_Source");

                var _sourceData = _mongoSourceDataCollection.Find<SourceData>(s => s._t == "SourceData").SortBy(s => s.StateAbbr).ToListAsync<SourceData>().Result;
                foreach (SourceData _source in _sourceData)
                {
                    var cityId = CreateCity(_source.CountryId, _source.TimeZone, _source.StateAbbr, _source.County, _source.City);

                    // Handle Countries from SourceData
                    //var _countries = _mongoCountryCollection.Find<Country>(s => s.Name == _source.Country).ToListAsync<Country>().Result;
                    //{
                    //    //var _countryName = "";

                    //}
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
            }
        }

        public List<User> GetUsers()
        {
            _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

            List<User> myUsers = _mongoUserCollection.Find<User>(s => s._t == "User").SortBy(s => s.FirstName).ToListAsync<User>().Result;

            return myUsers;
        }

        public List<ListItem> GetUserList()
        {
            List<ListItem> _userList = new List<ListItem>();

            _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

            List<User> _myUsers = _mongoUserCollection.Find<User>(s => s._t == "User").SortBy(s => s.FirstName).ToListAsync<User>().Result;
            foreach (User _user in _myUsers)
            {
                User _currUser = _user;

                var encryptUser = Convert.ToBoolean(ConfigurationManager.AppSettings["EncryptUsers"]);
                if (encryptUser)
                    _currUser = DecryptUserData(_user);

                ListItem _li = new ListItem();
                _li.Text = UppercaseFirstLetter(_currUser.FirstName) + " " + UppercaseFirstLetter(_currUser.LastName);
                _li.Value = _currUser._id.ToString();

                _userList.Add(_li);
            }

            _userList = SortListItems(_userList, false);

            return _userList;
        }

        public List<Event> SortEventsList(List<Event> events, bool Descending)
        {
            List<Event> list = new List<Event>();
            foreach (Event i in events)
            {
                list.Add(i);
            }
            if (Descending)
            {
                list.Sort(delegate (Event x, Event y) { return y.DateTime.CompareTo(x.DateTime); });
            }
            else
            {
                list.Sort(delegate (Event x, Event y) { return x.DateTime.CompareTo(y.DateTime); });
            }
            events.Clear();
            events.AddRange(list.ToArray());

            return list;
        }

        public List<ListItem> SortListItems(List<ListItem> items, bool Descending)
        {
            List<ListItem> list = new List<ListItem>();
            foreach (ListItem i in items)
            {
                list.Add(i);
            }
            if (Descending)
            {
                list.Sort(delegate (ListItem x, ListItem y) { return y.Text.CompareTo(x.Text); });
            }
            else
            {
                list.Sort(delegate (ListItem x, ListItem y) { return x.Text.CompareTo(y.Text); });
            }
            items.Clear();
            items.AddRange(list.ToArray());

            return list;
        }

        public string ParseUserJsonInput(string userId, string jsonInput, string latitude, string longitude)
        {
            dynamic jsonObject = Newtonsoft.Json.JsonConvert.DeserializeObject(jsonInput);
            User _user = new User();
            var userOriginallyEnabled = false;

            try
            {
                if (userId != ObjectId.Empty.ToString()) // Existing User update
                {
                    _user = GetUser(userId);
                    userOriginallyEnabled = _user.Enabled;
                }
                else // New User registration
                {
                    _user.Enabled = true;
                    userOriginallyEnabled = _user.Enabled;
                }

                // Update TNC section
                // Clear existing TNCS and reset from input
                if (jsonObject.TNCs.Count != 0)
                {
                    _user.TNCs.Clear();
                    foreach (var _tncId in jsonObject.TNCs)
                    {
                        _user.TNCs.Add(ObjectId.Parse(_tncId.ToString()));
                    }
                }

                // Update Personal section
                if (jsonObject.Enabled == null)
                    _user.Enabled = false;
                else
                    _user.Enabled = jsonObject.Enabled;

                if (!_user.Enabled)
                {
                    _user = LogOutUser(_user._id, Convert.ToDouble(latitude), Convert.ToDouble(longitude));

                    if (userOriginallyEnabled)
                        _user = DisableUser(_user, Convert.ToDouble(latitude), Convert.ToDouble(longitude));
                }
                else
                {
                    // Only enable user if it's changed
                    if (!userOriginallyEnabled)
                        _user = EnableUser(_user, Convert.ToDouble(latitude), Convert.ToDouble(longitude));
                }

                //if (jsonObject.IsLoggedIn == null)
                //    _user.IsLoggedIn = false;
                //else
                //    _user.IsLoggedIn = jsonObject.IsLoggedIn;

                _user.DeviceType = jsonObject.DeviceType;
                _user.Gender = jsonObject.Gender;

                _user.FirstName = UppercaseFirstLetter(jsonObject.FirstName.ToString());
                _user.LastName = UppercaseFirstLetter(jsonObject.LastName.ToString());

                // Update Contact section
                // Parse Email
                if (jsonObject.Email != null)
                {
                    var tmpEmail = jsonObject.Email.Value.ToString().Split('@');
                    Email _email = new Email();
                    _email.UserName = tmpEmail[0];
                    _email.Domain = tmpEmail[1];

                    _user.Contact.Email = _email;
                }
                else if (jsonObject.Contact.Email[0] != null)
                {
                    Email _email = new Email();
                    _email.UserName = jsonObject.Contact.Email[0].UserName;
                    _email.Domain = jsonObject.Contact.Email[0].Domain;

                    _user.Contact.Email = _email;
                }

                // Parse Phone
                if (jsonObject.Phone != null)
                {
                    Phone _phone = new Phone();
                    _phone.AreaCode = Convert.ToInt16(jsonObject.Phone.AreaCode);
                    _phone.Exchange = jsonObject.Phone.Exchange;
                    _phone.Number = jsonObject.Phone.Number;

                    _user.Contact.Phone = _phone;
                }
                else if (jsonObject.Contact.Phone[0] != null)
                {
                    Phone _phone = new Phone();
                    _phone.AreaCode = Convert.ToInt16(jsonObject.Contact.Phone[0].AreaCode);
                    _phone.Exchange = jsonObject.Contact.Phone[0].Exchange;
                    _phone.Number = jsonObject.Contact.Phone[0].Number;

                    _user.Contact.Phone = _phone;
                }

                // Update Address section
                if (jsonObject.Contact.Address.Address1.Value.Length > 0)
                {
                    var address1 = "";
                    var inputAddress1 = jsonObject.Contact.Address.Address1.Value.ToString();
                    if (inputAddress1.Contains(" "))
                    {
                        var tmpVal = inputAddress1.Split(' ');
                        foreach (var element in tmpVal)
                        {
                            address1 += UppercaseFirstLetter(element) + " ";
                        }
                    }
                    _user.Contact.Address.Address1 = address1.Trim();
                }

                if (jsonObject.Contact.Address.Address2.Value.Length > 0)
                {
                    var address2 = "";
                    var inputAddress2 = jsonObject.Contact.Address.Address2.Value.ToString();
                    if (inputAddress2.Contains(" "))
                    {
                        var tmpVal = inputAddress2.Split(' ');
                        foreach (var element in tmpVal)
                        {
                            address2 += UppercaseFirstLetter(element) + " ";
                        }
                    }
                    _user.Contact.Address.Address2 = address2.Trim();
                }

                if (jsonObject.Contact.Address.CountryId != null)
                    _user.Contact.Address.CountryId = ObjectId.Parse(jsonObject.Contact.Address.CountryId.ToString());

                if (jsonObject.Contact.Address.StateId != null)
                    _user.Contact.Address.StateId = ObjectId.Parse(jsonObject.Contact.Address.StateId.ToString());

                if (jsonObject.Contact.Address.CountyId != null)
                    _user.Contact.Address.CountyId = ObjectId.Parse(jsonObject.Contact.Address.CountyId.ToString());

                if (jsonObject.Contact.Address.CityId != null)
                    _user.Contact.Address.CityId = ObjectId.Parse(jsonObject.Contact.Address.CityId.ToString());

                if (jsonObject.Contact.Address.ZipCode != null)
                {
                    var _zipCodeInput = jsonObject.Contact.Address.ZipCode.ToString();

                    if (_zipCodeInput.Length == 5)
                    {
                        if (_zipCodeInput.StartsWith("0"))
                            _zipCodeInput = _zipCodeInput.SubString(1, _zipCodeInput.Length);

                        _user.Contact.Address.ZipCode = Convert.ToInt32(_zipCodeInput);

                        // Lookup TimeZoneId by Zip Code
                        var _zipCode = GetZipByValue(_user.Contact.Address.ZipCode);
                        _user.Contact.Address.TimeZoneId = _zipCode.TimeZoneId;
                    }
                }

                // Update Security section
                _user.Pwd = jsonObject.Pwd.ToString().ToLower();

                // Encrypt User if required
                var encryptUser = Convert.ToBoolean(ConfigurationManager.AppSettings["EncryptUsers"]);
                if (encryptUser)
                    _user = EncryptUserData(_user);

                var usersJson = "";

                if (userId != ObjectId.Empty.ToString()) // Existing User update
                    usersJson = UpdateUser(_user, Convert.ToDouble(latitude), Convert.ToDouble(longitude)).ToJson();
                else // New User registration
                    usersJson = CreateUser(_user, Convert.ToDouble(latitude), Convert.ToDouble(longitude)).ToJson();


                // Retrun Decrypted User
                if (encryptUser)
                    _user = DecryptUserData(_user);

                // Get rid of object identifiers and dates as they break json formatting
                var jsonResponse = SanitizeJsonString(_user.ToJson());

                return jsonResponse;
            }
            catch (Exception ex)
            {
                return ex.ToJson();
            }
        }

        public User GetUserByEmailAddress(string userEmail)
        {
            try
            {
                var tmpEmail = userEmail.Split('@');
                var userName = tmpEmail[0].Trim();
                var userDomain = tmpEmail[1].Trim();

                _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

                List<User> _users = _mongoUserCollection.Find<User>(s => s.Contact.Email.UserName == userName && s.Contact.Email.Domain == userDomain).ToListAsync<User>().Result;
                foreach (User _user in _users)
                {
                    return _user;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();

                return null;
            }
            return null;
        }

        public User GetUser(string userId)
        {
            try
            {
                _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

                List<User> _users = _mongoUserCollection.Find<User>(s => s._id == ObjectId.Parse(userId)).ToListAsync<User>().Result;
                foreach (User _user in _users)
                {
                    var encryptUser = Convert.ToBoolean(ConfigurationManager.AppSettings["EncryptUsers"]);
                    if (encryptUser)
                        return DecryptUserData(_user);
                    else
                        return _user;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();

                return null;
            }
            return null;
        }

        public List<RenewalPeriod> GetSubscriptionRenewalPeriods()
        {
            try
            {
                _mongoRenewalPeriodsCollection = _mongoDatabase.GetCollection<RenewalPeriod>("RenewalPeriods");

                List<RenewalPeriod> _renewalPeriods = _mongoRenewalPeriodsCollection.Find<RenewalPeriod>(s => s._t == "RenewalPeriod").ToListAsync<RenewalPeriod>().Result;

                return _renewalPeriods;
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();

                return null;
            }
        }

        #region Credit Card processing

        public enum CardType
        {
            Unknown = 0,
            MasterCard = 1,
            VISA = 2,
            Amex = 3,
            Discover = 4,
            DinersClub = 5,
            JCB = 6,
            enRoute = 7
        }

        // Class to hold credit card type information
        private class CardTypeInfo
        {
            public CardTypeInfo(string regEx, int length, CardType type)
            {
                RegEx = regEx;
                Length = length;
                Type = type;
            }

            public string RegEx { get; set; }
            public int Length { get; set; }
            public CardType Type { get; set; }
        }

        // Array of CardTypeInfo objects.
        // Used by GetCardType() to identify credit card types.
        private static CardTypeInfo[] _cardTypeInfo =
        {
                new CardTypeInfo("^(51|52|53|54|55)", 16, CardType.MasterCard),
                new CardTypeInfo("^(4)", 16, CardType.VISA),
                new CardTypeInfo("^(4)", 13, CardType.VISA),
                new CardTypeInfo("^(34|37)", 15, CardType.Amex),
                new CardTypeInfo("^(6011)", 16, CardType.Discover),
                new CardTypeInfo("^(300|301|302|303|304|305|36|38)",14, CardType.DinersClub),
                new CardTypeInfo("^(3)", 16, CardType.JCB),
                new CardTypeInfo("^(2131|1800)", 15, CardType.JCB),
                new CardTypeInfo("^(2014|2149)", 15, CardType.enRoute),
            };

        public Boolean IsCardNumberValid(string cardNumber)
        {
            Boolean cardNumberIsValid = false;

            int i, checkSum = 0;

            // Compute checksum of every other digit starting from right-most digit
            for (i = cardNumber.Length - 1; i >= 0; i -= 2)
                checkSum += (cardNumber[i] - '0');

            // Now take digits not included in first checksum, multiple by two,
            // and compute checksum of resulting digits
            for (i = cardNumber.Length - 2; i >= 0; i -= 2)
            {
                int val = ((cardNumber[i] - '0') * 2);
                while (val > 0)
                {
                    checkSum += (val % 10);
                    val /= 10;
                }
            }

            // Number is valid if sum of both checksums MOD 10 equals 0
            cardNumberIsValid = ((checkSum % 10) == 0);

            return cardNumberIsValid;
        }

        public string NormalizeCardNumber(string cardNumber)
        {
            if (cardNumber == null)
                cardNumber = String.Empty;

            StringBuilder sb = new StringBuilder();

            foreach (char c in cardNumber)
            {
                if (Char.IsDigit(c))
                    sb.Append(c);
            }

            return sb.ToString();
        }

        public CardType GetCardType(string cardNumber)
        {
            // Normalize card number
            cardNumber = NormalizeCardNumber(cardNumber);

            foreach (CardTypeInfo info in _cardTypeInfo)
            {
                if (cardNumber.Length == info.Length &&
                    Regex.IsMatch(cardNumber, info.RegEx))
                    return info.Type;
            }

            return CardType.Unknown;
        }

        public RenewalPeriod GetRenewalPeriod(Int16 renewalPeriod)
        {
            try
            {
                _mongoRenewalPeriodsCollection = _mongoDatabase.GetCollection<RenewalPeriod>("RenewalPeriods");

                List<RenewalPeriod> _renewalPeriods = _mongoRenewalPeriodsCollection.Find<RenewalPeriod>(s => s.Period == renewalPeriod).ToListAsync<RenewalPeriod>().Result;
                if (_renewalPeriods.Count > 0)
                {
                    foreach (var _renewalPeriod in _renewalPeriods)
                    {
                        return _renewalPeriod;
                    }
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
                return null;
            }
            return null;
        }

        #endregion

        public User LoginUser(string userEmail, string userPassword, double userLatitude, double userLongitude)
        {
            try
            {
                var encryptUser = Convert.ToBoolean(ConfigurationManager.AppSettings["EncryptUsers"]);

                _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

                var _parsedEmailComponents = userEmail.Split('@');

                var userEmailUserName = _parsedEmailComponents[0];
                var userEmailDomain = _parsedEmailComponents[1];

                if (encryptUser)
                {
                    userEmailUserName = _utils.EncryptString(_parsedEmailComponents[0], Constants.EncryptionKey.ToString());
                    userEmailDomain = _utils.EncryptString(_parsedEmailComponents[1], Constants.EncryptionKey.ToString());
                }

                List<User> _users = _mongoUserCollection.Find<User>(s => s.Contact.Email.UserName == userEmailUserName && s.Contact.Email.Domain == userEmailDomain).ToListAsync<User>().Result;
                if (_users.Count > 0)
                {
                    Reference _ref = new Reference(_users[0]._id, Constants.Reference.TypeUser);

                    if (encryptUser)
                    {
                        userPassword = _utils.EncryptString(userPassword.Trim(), _users[0]._id.ToString());

                        if (userPassword.Trim().ToLower() == _users[0].Pwd.Trim().ToLower())
                        {
                            // Create Login Event
                            Event loginEvent = new Event(userLatitude, userLongitude);
                            loginEvent.TypeId = Constants.Event.Account.LoggedIn.Item1;
                            loginEvent.Name = Constants.Event.Account.LoggedIn.Item2;
                            loginEvent.Reference = _ref;

                            _mongoEventCollection.InsertOne(loginEvent);

                            // Update User
                            _users[0].IsLoggedIn = true;
                            var replaceOneResult = _mongoUserCollection.ReplaceOneAsync(s => s._id == _users[0]._id, _users[0]);

                            return DecryptUserData(_users[0]);
                        }
                        else
                        {
                            // Create Failed Login Event
                            Event loginEvent = new Event(userLatitude, userLongitude);
                            loginEvent.TypeId = Constants.Event.Account.FailedLoginBadPwd.Item1;
                            loginEvent.Name = Constants.Event.Account.FailedLoginBadPwd.Item2;
                            loginEvent.Reference = _ref;

                            loginEvent.Details = "User tried to login with Pwd (" + userPassword.Trim() + ")";

                            _mongoEventCollection.InsertOne(loginEvent);

                            // Update User
                            _users[0].IsLoggedIn = false;
                            var replaceOneResult = _mongoUserCollection.ReplaceOneAsync(s => s._id == _users[0]._id, _users[0]);

                            return DecryptUserData(_users[0]);
                        }
                    }
                    else
                    {
                        if (userPassword.Trim().ToLower() == _users[0].Pwd.Trim().ToLower())
                        {
                            // Create Login Event
                            Event loginEvent = new Event(userLatitude, userLongitude);
                            loginEvent.TypeId = Constants.Event.Account.LoggedIn.Item1;
                            loginEvent.Name = Constants.Event.Account.LoggedIn.Item2;
                            loginEvent.Reference = _ref;

                            _mongoEventCollection.InsertOne(loginEvent);

                            // Update User
                            _users[0].IsLoggedIn = true;
                            var replaceOneResult = _mongoUserCollection.ReplaceOneAsync(s => s._id == _users[0]._id, _users[0]);

                            // Check if subscription valid and set event accordingly
                            DateTime currentDate = DateTime.Now;
                            DateTime expiresDate = Convert.ToDateTime(_users[0].ExpireDate);

                            DateTime currentUtcDate = TimeZoneInfo.ConvertTimeToUtc(currentDate);
                            DateTime expireUtcDate = TimeZoneInfo.ConvertTimeToUtc(expiresDate);

                            TimeSpan difference = currentUtcDate - expireUtcDate;
                            if (difference.TotalSeconds > 0)
                            {
                                // Account expired
                                _users[0].Expired = true;
                                replaceOneResult = _mongoUserCollection.ReplaceOneAsync(s => s._id == _users[0]._id, _users[0]);
                            }

                            return _users[0];
                        }
                        else
                        {
                            // Create Failed Login Event
                            Event loginEvent = new Event(userLatitude, userLongitude);
                            loginEvent.TypeId = Constants.Event.Account.FailedLoginBadPwd.Item1;
                            loginEvent.Name = Constants.Event.Account.FailedLoginBadPwd.Item2;
                            loginEvent.Reference = _ref;

                            loginEvent.Details = "User tried to login with Pwd (" + userPassword.Trim() + ")";

                            _mongoEventCollection.InsertOne(loginEvent);

                            // Update User
                            _users[0].IsLoggedIn = false;
                            var replaceOneResult = _mongoUserCollection.ReplaceOneAsync(s => s._id == _users[0]._id, _users[0]);

                            return _users[0];
                        }
                    }
                }
                else
                {
                    // User not found!
                    Event loginEvent = new Event(userLatitude, userLongitude);
                    loginEvent.TypeId = Constants.Event.Account.FailedLoginBadUserName.Item1;
                    loginEvent.Name = Constants.Event.Account.FailedLoginBadUserName.Item2;
                    //loginEvent.Reference = _ref;

                    loginEvent.Details = "User tried to login with Invalid Email (" + userEmailUserName.Trim() + "@" + userEmailDomain.Trim() + ")";

                    _mongoEventCollection.InsertOne(loginEvent);

                    return null;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();

                return null;
            }
        }

        public string UppercaseFirstLetter(string s)
        {
            if (string.IsNullOrEmpty(s))
            {
                return string.Empty;
            }
            char[] a = s.ToCharArray();
            a[0] = char.ToUpper(a[0]);
            return new string(a);
        }

        public User LogOutUser(ObjectId userId, Double userLatitude, Double userLongitude)
        {
            var encryptUser = Convert.ToBoolean(ConfigurationManager.AppSettings["EncryptUsers"]);

            try
            {
                _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

                List<User> _users = _mongoUserCollection.Find<User>(s => s._id == userId).ToListAsync<User>().Result;
                if (_users.Count > 0)
                {
                    Reference _ref = new Reference(userId, Constants.Reference.TypeUser);

                    // Create Login Event
                    Event loginEvent = new Event(userLatitude, userLongitude);
                    loginEvent.TypeId = Constants.Event.Account.LoggedOut.Item1;
                    loginEvent.Name = Constants.Event.Account.LoggedOut.Item2;
                    loginEvent.Reference = _ref;

                    _mongoEventCollection.InsertOne(loginEvent);

                    // Update User
                    _users[0].IsLoggedIn = false;
                    var replaceOneResult = _mongoUserCollection.ReplaceOneAsync(s => s._id == _users[0]._id, _users[0]);

                    if (encryptUser)
                    {
                        return DecryptUserData(_users[0]);
                    }
                    else
                        return _users[0];
                }
                else
                {
                    return null;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
                return null;
            }
        }

        public User EncryptUserData(User _user)
        {
            // Encrypt needed fields
            _user.Pwd = _utils.EncryptString(_user.Pwd.ToLower(), _user._id.ToString());

            _user.FirstName = _utils.EncryptString(UppercaseFirstLetter(_user.FirstName), _user._id.ToString());
            _user.LastName = _utils.EncryptString(UppercaseFirstLetter(_user.LastName), _user._id.ToString());

            _user.Contact.Address.Address1 = _utils.EncryptString(_user.Contact.Address.Address1, _user._id.ToString());
            _user.Contact.Address.Address2 = _utils.EncryptString(_user.Contact.Address.Address2, _user._id.ToString());

            // Encrypt email with DefaultCountryId for standard account lookup
            _user.Contact.Email.UserName = _utils.EncryptString(_user.Contact.Email.UserName, Constants.EncryptionKey.ToString());
            _user.Contact.Email.Domain = _utils.EncryptString(_user.Contact.Email.Domain, Constants.EncryptionKey.ToString());

            _user.Contact.Phone.Exchange = _utils.EncryptString(_user.Contact.Phone.Exchange, _user._id.ToString());
            _user.Contact.Phone.Number = _utils.EncryptString(_user.Contact.Phone.Number, _user._id.ToString());

            return _user;
        }

        public User DecryptUserData(User _user)
        {
            // Encrypt needed fields
            _user.Pwd = _utils.DecryptString(_user.Pwd.ToLower(), _user._id.ToString());

            _user.FirstName = _utils.DecryptString(UppercaseFirstLetter(_user.FirstName), _user._id.ToString());
            _user.LastName = _utils.DecryptString(UppercaseFirstLetter(_user.LastName), _user._id.ToString());

            _user.Contact.Address.Address1 = _utils.DecryptString(_user.Contact.Address.Address1, _user._id.ToString());
            _user.Contact.Address.Address2 = _utils.DecryptString(_user.Contact.Address.Address2, _user._id.ToString());

            // Decrypt email with Constants.DefaultCountryId for stanrd account lookup
            _user.Contact.Email.UserName = _utils.DecryptString(_user.Contact.Email.UserName, Constants.EncryptionKey.ToString());
            _user.Contact.Email.Domain = _utils.DecryptString(_user.Contact.Email.Domain, Constants.EncryptionKey.ToString());

            _user.Contact.Phone.Exchange = _utils.DecryptString(_user.Contact.Phone.Exchange, _user._id.ToString());
            _user.Contact.Phone.Number = _utils.DecryptString(_user.Contact.Phone.Number, _user._id.ToString());

            return _user;
        }

        public string SanitizeJsonString(string _jsonInputString)
        {
            var _cleanedJsonString = _jsonInputString;

            try
            {
                // Get rid of object identifiers and dates as they break json formatting
                _cleanedJsonString = _cleanedJsonString.Replace("ObjectId(", "");
                _cleanedJsonString = _cleanedJsonString.Replace("ISODate(", "");
                _cleanedJsonString = _cleanedJsonString.Replace(")", "");
            }
            catch (Exception ex)
            {
                _cleanedJsonString = ex.ToJson();
            }
            return _cleanedJsonString;
        }

        public void SaveEvent(Event _event)
        {
            try
            {
                _mongoEventCollection = _mongoDatabase.GetCollection<Event>("Events");

                _mongoEventCollection.InsertOne(_event);

            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
            }
        }

        public string CreateUserEvent(string _userId, string _eventId, string _eventDetails, double _latitude, double _longitude)
        {
            var reportStatus = "";

            var eventTypeId = Convert.ToInt32(_eventId);
            var eventName = _eventDetails;
            var eventNotes = _eventDetails;

            try
            {
                _mongoEventCollection = _mongoDatabase.GetCollection<Event>("Events");

                Event userEvent = new Event(_latitude, _longitude);

                switch (eventTypeId)
                {
                    case 1011:
                        eventTypeId = Constants.Event.Account.DisabledAccountAccess.Item1;
                        eventName = Constants.Event.Account.DisabledAccountAccess.Item2;
                        break;
                    default:

                        break;
                }

                userEvent.TypeId = eventTypeId;
                userEvent.Name = eventName;

                userEvent.Details = eventNotes;

                userEvent.Reference.ReferenceId = ObjectId.Parse(_userId);

                Reference _ref = new Reference(ObjectId.Parse(_userId), Constants.Reference.TypeUser);

                userEvent.Reference = _ref;

                _mongoEventCollection.InsertOne(userEvent);

                reportStatus = "Event reported!";
            }
            catch (Exception ex)
            {
                reportStatus = ex.ToString();
            }
            return reportStatus;
        }

        public string CreateNotification(Notification _notification)
        {
            var notificationResponse = "";

            try
            {
                _mongoNotificationCollection = _mongoDatabase.GetCollection<Notification>("Notifications");
                _mongoNotificationCollection.InsertOne(_notification);

                notificationResponse = "{'Result':'Notification logged'}";
            }
            catch (Exception ex)
            {
                notificationResponse = ex.ToString();
            }

            return notificationResponse;
        }

        public string CreateUser(User _user, double _latitude, double _longitude)
        {
            var registrationResponse = "";

            try
            {
                var encryptUser = Convert.ToBoolean(ConfigurationManager.AppSettings["EncryptUsers"]);
                if (encryptUser)
                    _user = EncryptUserData(_user);

                _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

                Event registrationEvent = new Event(_latitude, _longitude);
                registrationEvent.TypeId = Constants.Event.Account.Created.Item1;
                registrationEvent.Name = Constants.Event.Account.Created.Item2;
                registrationEvent.Reference.ReferenceId = _user._id;

                Reference _ref = new Reference(_user._id, Constants.Reference.TypeUser);

                registrationEvent.Reference = _ref;

                _mongoEventCollection.InsertOne(registrationEvent);

                _user.IsLoggedIn = false;
                _mongoUserCollection.InsertOne(_user);

                registrationResponse = "User account successfully created.";
            }
            catch (Exception ex)
            {
                registrationResponse = ex.ToString();
            }

            return registrationResponse;
        }

        public void CreatePaymentPeriod(RenewalPeriod _renewalPeriod)
        {
            try
            {
                _mongoRenewalPeriodsCollection = _mongoDatabase.GetCollection<RenewalPeriod>("RenewalPeriods");
                _mongoRenewalPeriodsCollection.InsertOne(_renewalPeriod);
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
            }
        }

        public bool phoneIsUnique(User _user)
        {
            bool phoneIsUnique = true;

            try
            {
                _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

                List<User> _testUser = _mongoUserCollection.Find<User>(s => s.Contact.Phone.AreaCode == _user.Contact.Phone.AreaCode && s.Contact.Phone.Exchange == _user.Contact.Phone.Exchange && s.Contact.Phone.Number == _user.Contact.Phone.Number).ToListAsync<User>().Result;
                if (_testUser.Count > 0)
                {
                    // Make sure it's not the same User
                    if (_testUser[0]._id != _user._id)
                        phoneIsUnique = false;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
            }

            return phoneIsUnique;
        }

        public bool emailIsUnique(User _user)
        {
            bool emailIsUnique = true;

            try
            {
                List<User> _testUser = _mongoUserCollection.Find<User>(s => s.Contact.Email.UserName == _user.Contact.Email.UserName && s.Contact.Email.Domain == _user.Contact.Email.Domain).ToListAsync<User>().Result;
                // Make sure it's not the same User
                if (_testUser[0]._id != _user._id)
                    emailIsUnique = false;
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
            }

            return emailIsUnique;
        }

        public User UpdateUser(User _user, double _latitude, double _longitude)
        {
            try
            {
                _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

                var replaceOneResult = _mongoUserCollection.ReplaceOneAsync(s => s._id == _user._id, _user);

                Reference _ref = new Reference(_user._id, Constants.Reference.TypeUser);

                // Update Event
                Event updateEvent = new Event(_latitude, _longitude);
                updateEvent.TypeId = Constants.Event.Account.Updated.Item1;
                updateEvent.Name = Constants.Event.Account.Updated.Item2;
                updateEvent.Reference.ReferenceId = _user._id;
                updateEvent.Reference = _ref;

                _mongoEventCollection.InsertOne(updateEvent);

                return _user;
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
                return null;
            }
        }

        public void UpdateUserLocation(Location _location)
        {
            try
            {
                _mongoLocationCollection = _mongoDatabase.GetCollection<Location>("Locations");

                _mongoLocationCollection.InsertOne(_location);
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
            }
        }

        [Obsolete]
        public Int64 GetUserEventsCount(string _userId)
        {
            Int64 eventCount = 0;

            try
            {
                _mongoEventCollection = _mongoDatabase.GetCollection<Event>("Events");

                //eventCount = _mongoEventCollection.Find<Event>(s => s.Reference.ReferenceId == ObjectId.Parse(_userId)).Count();

                eventCount = _mongoEventCollection.Find(s => s.Reference.ReferenceId == ObjectId.Parse(_userId)).CountDocuments();

                return eventCount;
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
                return 0;
            }
        }

        public string GetUserLastActivityDate(string _userId)
        {
            string _lastActiveDate = "";

            try
            {
                _mongoEventCollection = _mongoDatabase.GetCollection<Event>("Events");

                var _lastActiveRecord = _mongoEventCollection.Find<Event>(s => s.Reference.ReferenceId == ObjectId.Parse(_userId)).Limit(1).Skip(0).SortByDescending(s => s.DateTime).ToListAsync<Event>().Result;

                foreach (Event _event in _lastActiveRecord)
                {
                    _lastActiveDate = _event.DateTime.ToLocalTime().ToString();
                }

                return _lastActiveDate;
            }
            catch (Exception ex)
            {
                return ex.ToString();
            }
        }

        public List<Event> GetUserEvents(string _userId, int eventsPerPage, int eventsToSkip)
        {
            List<Event> _userEvents = new List<Event>();

            try
            {
                _mongoEventCollection = _mongoDatabase.GetCollection<Event>("Events");

                _userEvents = _mongoEventCollection.Find<Event>(s => s.Reference.ReferenceId == ObjectId.Parse(_userId)).Limit(eventsPerPage).Skip(eventsToSkip).SortByDescending(s => s.DateTime).ToListAsync<Event>().Result;
                if (_userEvents.Count > 0)
                {
                    //_userEvents = SortEventsList(_userEvents, true);

                    return _userEvents;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
                return null;
            }

            return _userEvents;
        }

        public void CreateUserRoles(string roleName)
        {
            try
            {
                _mongoUserRoleCollection = _mongoDatabase.GetCollection<UserRole>("UserRoles");

                //UserRole _newRole = new UserRole(Constants.UserRoles.Consumer.Item2);
                //_newRole.Name = roleName;

                //_mongoUserRoleCollection.InsertOne(_newRole);
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
            }
        }

        public List<Event> GetUserEvents(User _user)
        {
            List<Event> _userEvents = new List<Event>();

            try
            {
                _mongoEventCollection = _mongoDatabase.GetCollection<Event>("Events");

                _userEvents = _mongoEventCollection.Find<Event>(s => s.Reference.ReferenceId == _user._id).ToListAsync<Event>().Result;
                if (_userEvents.Count > 0)
                {
                    _userEvents = SortEventsList(_userEvents, true);

                    return _userEvents;
                }
            }
            catch (Exception ex)
            {
                var errMsg = ex.Message;
                return null;
            }

            return _userEvents;
        }

        public User DisableUser(User _user, double _latitude, double _longitude)
        {
            try
            {
                _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

                _user.Enabled = false;

                var replaceOneResult = _mongoUserCollection.ReplaceOneAsync(s => s._id == _user._id, _user);

                Reference _ref = new Reference(_user._id, Constants.Reference.TypeUser);

                // Update Event
                Event updateEvent = new Event(_latitude, _longitude);
                updateEvent.TypeId = Constants.Event.Account.Disabled.Item1;
                updateEvent.Name = Constants.Event.Account.Disabled.Item2;
                updateEvent.Reference.ReferenceId = _user._id;
                updateEvent.Reference = _ref;

                _mongoEventCollection.InsertOne(updateEvent);

                return _user;
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
                return null;
            }
        }

        public User EnableUser(User _user, double _latitude, double _longitude)
        {
            try
            {
                _mongoUserCollection = _mongoDatabase.GetCollection<User>("Users");

                _user.Enabled = true;

                var replaceOneResult = _mongoUserCollection.ReplaceOneAsync(s => s._id == _user._id, _user);

                Reference _ref = new Reference(_user._id, Constants.Reference.TypeUser);

                // Update Event
                Event updateEvent = new Event(_latitude, _longitude);
                updateEvent.TypeId = Constants.Event.Account.Enabled.Item1;
                updateEvent.Name = Constants.Event.Account.Enabled.Item2;
                updateEvent.Reference.ReferenceId = _user._id;
                updateEvent.Reference = _ref;

                _mongoEventCollection.InsertOne(updateEvent);

                return _user;
            }
            catch (Exception ex)
            {
                var errMsg = ex.ToString();
                return null;
            }
        }

        public string CreateMessage(Message _message)
        {
            var sendResult = "";

            try
            {
                _mongoMessageCollection = _mongoDatabase.GetCollection<Message>("Messages");

                _message.Subject = _message.Subject + " from (" + _message.FromName + " - " + _message.FromEmail + ")";
                _message.Body = "<b>Message from (" + _message.FromName + " - " + _message.FromEmail + ")</b><p>" + _message.Body + "</p>";

                _message.Status = Constants.Messaging.Status.Sent;

                _mongoMessageCollection.InsertOne(_message);

                sendResult = SendEmail(_message);

                //if (sendResult == "Message sent")
                //{

                //}
            }
            catch (Exception ex)
            {
                sendResult = ex.ToString();
            }

            return sendResult;
        }

        public string SendEmail(Message _message)
        {
            string sendResult = "";

            try
            {
                //Set up message
                var message = new MailMessage { From = new MailAddress(_message.ToEmail) };
                message.IsBodyHtml = _message.isHtml;

                message.To.Add(new MailAddress(_message.ToEmail));

                message.Subject = _message.Subject;
                message.Body = _message.Body;

                message.Priority = MailPriority.High;

                message.DeliveryNotificationOptions = DeliveryNotificationOptions.OnFailure;

                // setup Smtp Client
                var smtp = new SmtpClient();

                smtp.Port = Convert.ToInt16(ConfigurationManager.AppSettings["Port"]);
                smtp.Host = ConfigurationManager.AppSettings["Host"];
                smtp.EnableSsl = Convert.ToBoolean(ConfigurationManager.AppSettings["EnableSsl"]);
                smtp.UseDefaultCredentials = Convert.ToBoolean(ConfigurationManager.AppSettings["UseDefaultCredentials"]);
                smtp.Credentials = new System.Net.NetworkCredential(ConfigurationManager.AppSettings["LoginUserName"], ConfigurationManager.AppSettings["LoginPassword"]);
                smtp.DeliveryMethod = SmtpDeliveryMethod.Network;

                smtp.Send(message);

                sendResult = "Message sent";
            }
            // ReSharper disable once EmptyGeneralCatchClause
            catch (Exception ex)
            {
                sendResult = ex.ToString();
            }
            return sendResult;
        }

        public string CreateTNCs()
        {
            var registrationResponse = "";

            try
            {
                _mongoTNCCollection = _mongoDatabase.GetCollection<TNC>("TNCs");

                TNC _tnc = new TNC();

                Event registrationEvent = new Event(Convert.ToDouble("30.3811997"), Convert.ToDouble("-97.65724329999999"));
                registrationEvent.Name = Constants.Event.TNC.Created.Item2;
                registrationEvent.TypeId = Constants.Event.TNC.Created.Item1;

                string[] _tncNames = new string[] { "FARE", "Fasten", "GetMe", "InstaRyde", "Lyft", "RideAustin", "ScoopMe", "Uber", "WingZ" };
                foreach (string _tncName in _tncNames)
                {
                    _tnc.Name = _tncName;

                    Email _email = new Email();
                    _email.UserName = "admin";
                    _email.Domain = _tnc.Name.ToLower() + "com";
                    _tnc.Contact.Email = _email;

                    Phone _phone = new Phone();
                    _phone.CountryCode = "1";
                    _phone.AreaCode = 512;
                    _phone.Exchange = "";
                    _phone.Number = "";
                    _tnc.Contact.Phone = _phone;

                    _tnc.Contact.Address.Address1 = "123 Some St.";
                    _tnc.Contact.Address.Address2 = "Unit #567-A";
                    _tnc.Contact.Address.CountryId = Constants.DefaultCountryId;
                    _tnc.Contact.Address.StateId = Constants.DefaultStateId;
                    _tnc.Contact.Address.CountyId = Constants.DefaultCountyId;
                    _tnc.Contact.Address.CityId = Constants.DefaultCityId;
                    _tnc.Contact.Address.ZipCode = 78753;
                    _tnc.Contact.Address.TimeZoneId = Constants.DefaultTimeZoneId;

                    _mongoTNCCollection.InsertOne(_tnc);
                }

                registrationResponse = "Successfully created new TNC.";
            }
            catch (Exception ex)
            {
                registrationResponse = ex.ToString();
            }

            return registrationResponse;
        }

        public void UpdateSourceData(SourceData _source)
        {
            // Update Local object
            var replaceOneResult2 = _mongoSourceDataCollection.ReplaceOneAsync(s => s._id == _source._id, _source);
        }

        public interface IIdentified
        {
            ObjectId _id { get; }
        }

        #endregion
    }
}
