package com.driveswitch;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import com.driveswitch.driveswitch.R;
import com.google.gson.Gson;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterTNCActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private RegisterTNCActivity.CheckBoxAdapter mCheckBoxAdapter;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Double userLatitude = 0.00;
    private Double userLongitude = 0.00;

    private View mProgressView;
    private View mFormView;

    User myUser = null;

    GridView mGridView;

    String[] tncItems = null;

    ArrayList<TNC> tncBaseCollection = new ArrayList<TNC>();
    ArrayList<String> tncItemsSelected = new ArrayList<String>();

    public Integer selectedTNCCount = 0;

    TextView selectedLabel;
    Button saveBtn;

    @Override
    protected void onPause()
    {
        // Stop location reporting
        //locationManager.removeUpdates(locationListener);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        // Resume location reporting
        //locationManager.requestLocationUpdates(getResources().getString(R.string.app_location_provider), Integer.parseInt(getResources().getString(R.string.app_location_refresh_interval)), 0, locationListener);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // Stop location reporting
        stopLocationReporting();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void stopLocationReporting()
    {
        // Stop location reporting
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        populateMyUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tnc);

        selectedLabel = (TextView) findViewById(R.id.label_tnc_instructions);
        saveBtn = (Button) findViewById(R.id.btnSave);

        Utilities.enableButton(saveBtn, false);

        // Set logo in ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo_driveswitch);

        mFormView = findViewById(R.id.mainContainer);
        mProgressView = findViewById(R.id.progress_bar);

        // Set Next button action
        Button mNextButton = (Button) findViewById(R.id.btnSave);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTNCSettings();
            }
        });

        // Set Cancel button action
        Button mCancelButton = (Button) findViewById(R.id.btnCancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRegistration();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();

                Utilities.logUserLocationAndValidateSubscription(RegisterTNCActivity.this, myUser._id, Constants.EmptyObjectId, userLatitude, userLongitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(getResources().getString(R.string.app_location_provider), Integer.parseInt(getResources().getString(R.string.app_location_refresh_interval)), 0, locationListener);

        populateMyUser();

        getTNCCheckboxGrid();
    }

    private void populateMyUser()
    {
        try
        {
            SharedPreferences prefs = this.getSharedPreferences(Constants.User.Profile, Context.MODE_PRIVATE);
            String serializedUser = prefs.getString(Constants.User.Profile, null);

            if (serializedUser != null)
            {
                JSONObject jsonUser = new JSONObject(serializedUser);
                myUser = new User(jsonUser);

                String wtf = "";
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
    }

    private void getTNCCheckboxGrid()
    {
        List<ApplicationInfo> installedApps = Utilities.listThirdPartyApps(this);

        Integer tncCount = installedApps.size();

        tncItems = new String[tncCount];

        for(int i = 0; i < tncCount; i++)
        {
            ApplicationInfo currentPackage = installedApps.get(i);

            String packageName = currentPackage.packageName;
            String appName = "";

            switch(packageName)
            {
                case Constants.TNCPackageNames.FARE:
                    appName = Constants.TNCList.FARE;
                    break;
                case Constants.TNCPackageNames.Fasten:
                    appName = Constants.TNCList.Fasten;
                    break;
                case Constants.TNCPackageNames.GetMe:
                    appName = Constants.TNCList.GetMe;
                    break;
                case Constants.TNCPackageNames.InstaRyde:
                    appName = Constants.TNCList.InstaRyde;
                    break;
                case Constants.TNCPackageNames.Lyft:
                    appName = Constants.TNCList.Lyft;
                    break;
                case Constants.TNCPackageNames.RideAustin:
                    appName = Constants.TNCList.RideAustin;
                    break;
                case Constants.TNCPackageNames.ScoopMe:
                    appName = Constants.TNCList.ScoopMe;
                    break;
                case Constants.TNCPackageNames.Tride:
                    appName = Constants.TNCList.Tride;
                    break;
                case Constants.TNCPackageNames.Uber:
                    appName = Constants.TNCList.Uber;
                    break;
                case Constants.TNCPackageNames.WingZ:
                    appName = Constants.TNCList.WingZ;
                    break;
                default:
                    appName = "Package " + i;
                    break;
            }

            TNC _tnc = new TNC();

            //_tnc._id = currentTNC.getString("_id");
            _tnc.Name = appName;

            tncBaseCollection.add(_tnc);

            tncItems[i] = appName;

            //String versionName = currentPackage.versionName;
            //ActivityInfo[] activities = currentPackage.activities;
            //ApplicationInfo appInfo = currentPackage.applicationInfo;
            //ServiceInfo[] serviceInfo = currentPackage.services;
        }

        if(tncItems != null)
        {
            if (tncItems.length > 0)
            {
                mGridView = (GridView) findViewById(R.id.tncList);
                mGridView.setTextFilterEnabled(true);
                mGridView.setOnItemClickListener(this);

                mCheckBoxAdapter = new RegisterTNCActivity.CheckBoxAdapter(this);
                mGridView.setAdapter(mCheckBoxAdapter);
            }
            else
            {
                // Cannot continue. Nothing installed.
                showInstallationError();
            }
        }
        else
        {
            // Cannot continue. Nothing installed.
            showInstallationError();
        }
    }

    private void showInstallationError()
    {
        Button saveButton = (Button) findViewById(R.id.btnSave);
        //Utilities.enableButton(saveButton, false);
        saveButton.setVisibility(View.GONE);

        TextView installMsg = (TextView) findViewById(R.id.label_tnc_instructions);
        installMsg.setText("No rideshare apps installed. Configuration cannot continue!");
        installMsg.setTextColor(Color.parseColor("#eb416b"));

        GridView tncList = (GridView) findViewById(R.id.tncList);
        tncList.setVisibility(View.GONE);
    }

    private void saveTNCSettings()
    {
        try {
            // Get selected TNCs and add them to user for upate

            SharedPreferences prefs = this.getSharedPreferences(Constants.User.Profile, Context.MODE_PRIVATE);
            String serializedUser = prefs.getString(Constants.User.Profile, null);

            if (serializedUser != null)
            {
                // Read response into Json object from preferences
                JSONObject jsonUser = null;

                try
                {
                    // Update User's TNCs. Clear first!
                    myUser.TNCs = new ArrayList<TNC>();

                    int selectedCount = tncItemsSelected.size();
                    for (Integer i = 0; i < selectedCount; i++)
                    {
                        String selectedTNCName = tncItemsSelected.get(i);

                        // Loop through base TNC array
                        Integer baseTNCSize = tncBaseCollection.size();
                        for(Integer j = 0; j < baseTNCSize; j++)
                        {
                            TNC currentTNC = (TNC)tncBaseCollection.get(j);

                            boolean namesMatch = currentTNC.Name.equals(selectedTNCName.trim());
                            if (namesMatch)
                                myUser.TNCs.add(currentTNC);
                        }
                    }

                    showProgress(true);

                    Gson gson = new Gson();
                    String jsonUpdatedUser = gson.toJson(myUser);

                    JSONArray updatedPersonal = Utilities.updateUser(this, myUser._id, jsonUpdatedUser);

                    if(updatedPersonal != null) {
                        stopLocationReporting();
                        startActivity(new Intent(this, TNCControlActivity.class));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
    }

    private void cancelRegistration()
    {
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void onItemClick(AdapterView parent, View view, int position, long id)
    {
        mCheckBoxAdapter.toggle(position);
    }

    class CheckBoxAdapter extends ArrayAdapter implements CompoundButton.OnCheckedChangeListener
    {
        private SparseBooleanArray mCheckStates;

        LayoutInflater mInflater;
        TextView textview;
        CheckBox cb;

        CheckBoxAdapter(RegisterTNCActivity context)
        {
            super(context,0);
            mCheckStates = new SparseBooleanArray(tncItems.length);
            mInflater = (LayoutInflater)RegisterTNCActivity.this.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            // TODO Auto-generated method stub
            View vi = convertView;
            if(convertView == null) {
                // Get layout xml file
                vi = mInflater.inflate(R.layout.tnc_item_template, null);
            }

            cb = (CheckBox) vi.findViewById(R.id.tncCheckBox);
            cb.setTag(position);

            // Set TNC name label
            String currentTNCName = tncItems[position].trim();
            cb.setText(currentTNCName);

            cb.setChecked(mCheckStates.get(position, false));
            cb.setOnCheckedChangeListener(this);

            return vi;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tncItems.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub

            ListAdapter myAdapter = mGridView.getAdapter();

            RelativeLayout myView = (RelativeLayout)myAdapter.getView(position, null, null);

            CheckBox tncItem = (CheckBox)myView.getChildAt(0);

            return tncItem;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub

            return 0;
        }

        public boolean isChecked(int position)
        {
            return mCheckStates.get(position, false);
        }

        public void setChecked(int position, boolean isChecked)
        {
            mCheckStates.put(position, isChecked);
        }

        public void toggle(int position)
        {
            setChecked(position, !isChecked(position));
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            // TODO Auto-generated method stub
            mCheckStates.put((Integer) buttonView.getTag(), isChecked);

            String selectedName = buttonView.getText().toString();

            setSelectionLabel(selectedName, isChecked);
        }
    }

    public void setSelectionLabel(String selectedName, Boolean isChecked)
    {
        try {
            if (isChecked) {
                selectedTNCCount++;
                // Add to array
                tncItemsSelected.add(selectedName);
            }
            else {
                selectedTNCCount--;
                // Remove from array
                tncItemsSelected.remove(selectedName);
            }

            selectedLabel = (TextView) findViewById(R.id.label_tnc_instructions);
            saveBtn = (Button) findViewById(R.id.btnSave);

            if (selectedTNCCount == 0) {
                selectedLabel.setText("Must have 1 selected");
                selectedLabel.setTextColor(Color.parseColor("#eb416b"));
                Utilities.enableButton(saveBtn, false);
            } else {
                selectedLabel.setText(selectedTNCCount + " rideshares selected");
                selectedLabel.setTextColor(Color.parseColor("#c0c0c0"));
                Utilities.enableButton(saveBtn, true);
            }
        }
        catch(Exception ex)
        {
            String errMsg = ex.toString();
        }
    }
}
