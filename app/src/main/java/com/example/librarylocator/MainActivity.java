package com.example.librarylocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";  //to get log
    private String zipEntered;                   //to store manually entered zip code
    private Library newLb = new Library();       // new library object based on which data will process for UI
    TextView name, hoa, address, website, phone; // Textviews for UI
    //To save lat long of library locations and user location
    Double[] latlonUser;
    Double[] latlonFromList;
    Double latUser, lonUser;
    Double latLoc, lonLoc;

    private ArrayList<Library> libraryList = new ArrayList<>();
    private ArrayList<Double> distanceList = new ArrayList<Double>();
    private ArrayList<HistoryZip> historyList = new ArrayList<>();
    private ArrayList<String> historyListDisplay = new ArrayList<>();
    HistoryZip hz;
//To create a drawer layout to store history list
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter arrayAdapter;

    //binds the views
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        hoa = findViewById(R.id.hoa);
        address = findViewById(R.id.address);
        website = findViewById(R.id.website);
        phone = findViewById(R.id.phone);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mDrawerList = findViewById(R.id.left_drawer);

        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    int hisZip = historyList.get(position).gethZipId();
                    newLb = libraryList.get(hisZip);
                    dataToUI();
                    mDrawerLayout.closeDrawer(mDrawerList);
                } );

        if(historyList != null)
        {  arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, historyListDisplay);
            mDrawerList.setAdapter(arrayAdapter); }

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                /* host Activity */
                mDrawerLayout,             /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }
//starts process to download data from API
    @Override
    protected void onResume() {
        super.onResume();
        LibraryDataDownloader.downloadLibraryDetails(this); //call to download data from API
    }

    //To inflate menu on title bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // to choose search menu button to enter new zip and to open drawer layout which shows current search history
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.search){
                searchDialouge();
        }
        else if(mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " +historyList.size());
            return true;
        }
        return super.onOptionsItemSelected(item);
     }

     //it gets the parsed json in a list from LibraryDataDownloader class

    public void updateData(ArrayList<Library> lbListfromDownld){

        libraryList.addAll(lbListfromDownld);

    }

    //to open a dialougue box to enter new zip manually
    private boolean searchDialouge(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et1 = new EditText(this);
        et1.setInputType(InputType.TYPE_CLASS_TEXT);
        et1.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et1);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    zipEntered = et1.getText().toString();
                     uploadData(zipEntered);
            }
        });

        builder.setNegativeButton("NO WAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setTitle("Enter a Zip Code");

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    // it begins the process to upload data on UI
    private boolean uploadData(String zip){
        this.zipEntered = zip;

        latlonUser = getLatLon(this.zipEntered);     //gets the lat long of user's zip
        if(latlonUser != null)
        { latUser = latlonUser[0];
        lonUser = latlonUser[1]; }
        else { return false; }

        distanceList.clear();

        hz = new HistoryZip();               //create a new history zip object. this object stores the id of the zip which is being used to fetch the data from drawer list
        hz.sethZip(this.zipEntered);         // this list allows to not to search for the closest location again for the same zip and instead directly shows the previous search result


        //to check if user location is same as any zip provided by API data
        for(int i = 0; i < libraryList.size() ; i++){
            if(zipEntered.equalsIgnoreCase(libraryList.get(i).getZip())) {
                newLb = libraryList.get(i);
                hz.sethZipId(i);
                if (!historyList.contains(hz)) {
                    historyList.add(hz);
                    if(!historyListDisplay.contains(hz.gethZip()))
                    { historyListDisplay.add(hz.gethZip()); }
                    arrayAdapter.notifyDataSetChanged();
                }
                dataToUI();
                return true;

                // if not then this method shall calculate closest distance to libraries
            } else {
                latlonFromList = getLatLon(libraryList.get(i).getZip());
                latLoc = latlonFromList[0];
                lonLoc = latlonFromList[1];
                Double distance = FindClosestLocation.distance(latUser, lonUser, latLoc, lonLoc, this);
                distanceList.add(distance);
                Log.d(TAG, "uploadData: distance from list" +distanceList.get(i));
            }
        }

        int indexOfMinimum = distanceList.indexOf(Collections.min(distanceList));
        Log.d(TAG, "uploadData: minimum index" +indexOfMinimum);
        newLb = libraryList.get(indexOfMinimum);
        hz.sethZipId(indexOfMinimum);

        //to update drawer list to maintain search history of zip

        if (!historyList.contains(hz)) {
            historyList.add(hz);
            if(!historyListDisplay.contains(hz.gethZip()))
            { historyListDisplay.add(hz.gethZip()); }
            arrayAdapter.notifyDataSetChanged();
        }

        dataToUI();
        return true;
    }

    // sends data of closest library to UI
    private boolean dataToUI(){

        name.setText(newLb.getName());
        hoa.setText(newLb.getHoursOfOperation());
        address.setText(newLb.getAddress() + newLb.getCity() + newLb.getState() + newLb.getZip());
        website.setText(newLb.getUrl());
        phone.setText(newLb.getPhone());
        return true;
    }

    //get lat long of the zip
    private Double[] getLatLon(String userProvidedLocation) {
        Geocoder geocoder = new Geocoder(this); // Here, “this” is an Activity
        try {
            List<Address> address =
                    geocoder.getFromLocationName(userProvidedLocation, 1);
            if (address == null || address.isEmpty()) {
                Toast.makeText(this,"Please Enter a valid Zip Code", Toast.LENGTH_LONG).show();
                return null;
            }
            Double lat = address.get(0).getLatitude();
            Double lon = address.get(0).getLongitude();

            return new Double[] {lat, lon};
        } catch (IOException e) {
            // Failure to get an Address object
            return null;
        }
    }
}