package com.example.librarylocator;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LibraryDataDownloader {

    private static final String TAG = "LibraryDataDownloader";
    private static RequestQueue queue;
    private static final String sourceURL = "https://data.cityofchicago.org/resource/x8fc-8rcq.json"; //url of library data
    public static MainActivity mainActivity;
    private static Library library;
    private static ArrayList<Library> lbList = new ArrayList<>();

    public static void downloadLibraryDetails(MainActivity mainActivityIn) {

        mainActivity = mainActivityIn;
        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(sourceURL).buildUpon();
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONArray> listener;
        listener = response -> parseJSON(response.toString());

        Log.d(TAG, "downloadSources: "+urlToUse);

        Response.ErrorListener error =
                error1 -> mainActivity.updateData(null);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlToUse, null, listener, error);

        queue.add(jsonArrayRequest);
    }
//parse json array
    private static void parseJSON(String s) {

        int id = 0;

        try {

            library = new Library();

            JSONArray jArrMain = new JSONArray(s);

            for (int i = 0; i < jArrMain.length() ; i++) {

                JSONObject jObjMain = jArrMain.getJSONObject(i);
                JSONObject jObjUrl = jObjMain.getJSONObject("website");

                String name = jObjMain.getString("name_");
                String hours_of_operation = jObjMain.getString("hours_of_operation");
                String address = jObjMain.getString("address");
                String city = jObjMain.getString("city");
                String state = jObjMain.getString("state");
                String zip = jObjMain.getString("zip");
                String phone = jObjMain.getString("phone");
                String url = jObjUrl.getString("url");

                library = new Library();

                library.setObjectID(id);
                id++;
                library.setName(name);
                library.setHoursOfOperation(hours_of_operation);
                library.setAddress(address);
                library.setCity(city);
                library.setState(state);
                library.setZip(zip);
                library.setPhone(phone);
                library.setUrl(url);
                lbList.add(library);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        mainActivity.updateData(lbList); //returns data stored in a list which contains library objects
    }

}
