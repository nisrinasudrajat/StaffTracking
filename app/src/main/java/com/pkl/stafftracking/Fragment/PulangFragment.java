package com.pkl.stafftracking.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pkl.stafftracking.Adapter.PulangAdapter;
import com.pkl.stafftracking.Model.Pulang;
import com.pkl.stafftracking.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PulangFragment extends Fragment {

    private static final String URL_PRODUCTS = "https://trackingforadmin.000webhostapp.com/rekap_user_pulang.php";

    //a list to store all the products
    List<Pulang> pulangList;

    //the recyclerview
    RecyclerView recyclerView;

    SharedPreferences sharedpreferences;
    Boolean session = false;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    public final static String TAG_ID = "id";
    String user_id;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pulang, container, false);

        sharedpreferences = getActivity().getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        //session = sharedpreferences.getBoolean(session_status, false);
        user_id = sharedpreferences.getString(TAG_ID, null);

        //getting the recyclerview from xml
        recyclerView = v.findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //initializing the pulanglist
        pulangList = new ArrayList<>();


        //this method will fetch and parse json
        //to display it in recyclerview

        loadData();
        return v;
    }

    private void loadData() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject pulang = array.getJSONObject(i);

                                    //adding the product to product list
                                    pulangList.add(new Pulang(
                                            //pulang.getString("user_id"),
                                            pulang.getString("hari"),
                                            pulang.getString("tanggal"),
                                            pulang.getString("jam"),
                                            pulang.getString("nama")
                                    ));
                                }


                                        PulangAdapter adapter = new PulangAdapter(getActivity(), pulangList);
                                        recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                //adding our stringrequest to queue
                Volley.newRequestQueue(getActivity()).add(stringRequest);
    }
}
