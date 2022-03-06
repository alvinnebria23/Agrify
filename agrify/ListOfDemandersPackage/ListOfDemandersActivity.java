package com.example.agrify.ListOfDemandersPackage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.agrify.Constants;
import com.example.agrify.DemandPackage.Demand;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListOfDemandersActivity extends AppCompatActivity implements Serializable , SwipeRefreshLayout.OnRefreshListener{
    private ImageView productImageView, filterImageView;
    private TextView productNameTextView, demandTodayTextView, agriculturalSectorTextView,
            updatedTextView, peopleTextView, emptyTextView, demandsYouMayHaveTextView;
    private EditText searchEditText;
    List<Demanders> demandersList;
    List<Demand> dataAnalyticsList;
    private DemanderAdapter demanderAdapter;
    private AnalyticsDemandAdapter analyticsDemandAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView demanders, demands;
    private NestedScrollView horizontalNestedScrollView;
    String productImage = "", productName = "", demandToday = "", agriculturalSector = "";
    int productID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_demanders);

        productImage         = getIntent().getExtras().getString("productImage");
        productName          = getIntent().getExtras().getString("productName").toLowerCase();
        demandToday          = getIntent().getExtras().getString("demandToday");
        agriculturalSector   = getIntent().getExtras().getString("agriculturalSector");
        productID            = getIntent().getExtras().getInt("productID");
        String title = productName.substring(0, 1).toUpperCase() + productName.substring(1) + " " + "Demand";
        setTitle(title);
        demandersList                  =   new ArrayList<>();
        dataAnalyticsList              =   new ArrayList<>();
        demanders                      =   (RecyclerView)    findViewById(R.id.demanders);
        demands                        =   (RecyclerView)    findViewById(R.id.demands);
        searchEditText                 =   (EditText)        findViewById(R.id.searchEditText);
        demanderAdapter                =    new DemanderAdapter (getApplicationContext(), demandersList);
        analyticsDemandAdapter         =    new AnalyticsDemandAdapter(getApplicationContext(), dataAnalyticsList);
        filterImageView                =    (ImageView)     findViewById(R.id.filterImageView);
        updatedTextView                =    (TextView)      findViewById(R.id.updatedTextView);
        peopleTextView                 =    (TextView)      findViewById(R.id.peopleTextView);
        emptyTextView                  =    (TextView)      findViewById(R.id.emptyTextView);
        demandsYouMayHaveTextView      =    (TextView)      findViewById(R.id.demandsYouMayHaveTextView);
        swipeRefreshLayout             =    (SwipeRefreshLayout)    findViewById(R.id.swipeRefreshLayout);
        horizontalNestedScrollView     =    (NestedScrollView)      findViewById(R.id.horizontalNestedScrollView);
        loadDemanders();
        setUpDataAnalytics();
        demands.setLayoutManager(new GridLayoutManager(demands.getContext(), 1, GridLayoutManager.HORIZONTAL, false));
        demands.setAdapter(analyticsDemandAdapter);
        demanders.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        demanders.setAdapter(demanderAdapter);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        productImageView               =   (ImageView)       findViewById(R.id.productImageView);
        productNameTextView            =   (TextView)        findViewById(R.id.productNameTextView);
        demandTodayTextView            =   (TextView)        findViewById(R.id.demandTodayTextView);
        agriculturalSectorTextView     =   (TextView)        findViewById(R.id.typeTextView);
        searchEditText                 =   (EditText)        findViewById(R.id.searchEditText);

        Glide.with (getApplicationContext())
                .load (productImage)
                .into (productImageView);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c);
        updatedTextView.setText(formattedDate);
        productNameTextView.setText(productName.toUpperCase());
        demandTodayTextView.setText(demandToday);
        agriculturalSectorTextView.setText(agriculturalSector);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        filterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterImageView.setEnabled(false);
                openFilterDialog();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
    }
    private void filter(String text) {
        ArrayList<Demanders> filteredList = new ArrayList<>();

        for (Demanders demanders : demandersList ) {
            if (demanders.getFullName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(demanders);
            }
        }

        demanderAdapter.filterList(filteredList);
    }
    public List<Demanders> getDemandersList(){
        return demandersList;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            this.finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    public void loadDemanders(){
        swipeRefreshLayout.setRefreshing(true);
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_DEMANDERS,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray (response);

                            for (int i = 0; i < array.length (); i++) {
                                JSONObject demanders = array.getJSONObject (i);
                                demandersList.add(new Demanders(Constants.PATH_PROFILE_IMAGES_FOLDER + demanders.getString("profile_pic"), demanders.getInt("user_id"),
                                        demanders.getString("fullname"), demanders.getString("location"),
                                        demanders.getString("contact_no"), demanders.getInt("demand_id"),
                                        demanders.getInt("price"), demanders.getString("variety_name"),
                                        demanders.getInt("needed_kilograms"), demanders.getInt("received_kilograms"),
                                        demanders.getString("duration_end"), demanders.getString("description"),
                                        productName.toUpperCase(), agriculturalSector.toUpperCase()
                                ));
                            }
                            demanderAdapter.notifyDataSetChanged();
                            demanders.getRecycledViewPool().clear();
                            swipeRefreshLayout.setRefreshing(false);
                            String people = String.valueOf(demandersList.size());
                            peopleTextView.setText(people);

                        } catch (JSONException e) {
                            e.printStackTrace ();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<>();
                params.put ("agricultural_sector", agriculturalSector.toLowerCase());
                params.put ("product_id", String.valueOf(productID));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void loadDataAnalytics(){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_ANALYSED_DATA,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray (response);

                            for (int i = 0; i < array.length (); i++) {
                                JSONObject demands = array.getJSONObject (i);

                                retrieveDemands(demands.getString("product_id"), demands.getString("agricultural_sector"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }
                    }
                },
                new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<>();
                params.put ("agricultural_sector", agriculturalSector.toLowerCase());
                params.put ("product_id", String.valueOf(productID));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void retrieveDemands(final String productID, final String agriculturalSector){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_ANALYSED_INFO,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray (response);

                            for (int i = 0; i < array.length (); i++) {
                                JSONObject products = array.getJSONObject (i);

                                String productImage = "";

                                if(agriculturalSector.equals("crops"))
                                    productImage = Constants.PATH_CROPS_IMAGES_FOLDER+products.getString("product_image");
                                else if(agriculturalSector.equals("livestocks"))
                                    productImage = Constants.PATH_LIVESTOCKS_IMAGES_FOLDER+products.getString("product_image");
                                else if(agriculturalSector.equals("poultries"))
                                    productImage = Constants.PATH_POULTRIES_IMAGES_FOLDER+products.getString("product_image");
                                else if(agriculturalSector.equals("fisheries"))
                                    productImage = Constants.PATH_FISHERIES_IMAGES_FOLDER+products.getString("product_image");

                                dataAnalyticsList.add(new Demand(0,
                                        products.getInt("product_id"),
                                        productImage,
                                        products.getString("product_name"),
                                        products.getInt("demand_today"),
                                        0,
                                        agriculturalSector));
                            }
                            if(dataAnalyticsList.size() > 0) {
                                demandsYouMayHaveTextView.setVisibility(View.VISIBLE);
                                horizontalNestedScrollView.setVisibility(View.VISIBLE);
                                demands.setVisibility(View.VISIBLE);
                            }
                            analyticsDemandAdapter.updateList(dataAnalyticsList);
                            demanders.getRecycledViewPool().clear();
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }
                    }
                },
                new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<>();
                params.put ("agricultural_sector", agriculturalSector);
                params.put ("product_id", productID);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void openFilterDialog(){
        filterDialog fDialog = new filterDialog();
        fDialog.show(getSupportFragmentManager(), "Filter Dialog");
        fDialog.setCancelable(false);
        fDialog.setDialogResult(new filterDialog.OnMyDialogResult() {
            @Override
            public void finish(ArrayList<Demanders> filteredList) {
                filterImageView.setEnabled(true);
                demanderAdapter.filterList(filteredList);
                if(filteredList.size() == 0)
                    emptyTextView.setVisibility(View.VISIBLE);
                else
                    emptyTextView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRefresh() {
        demandersList.clear();
        loadDemanders();
    }
    public void setUpDataAnalytics(){
        if(SharedPrefManager.getInstance(getApplicationContext()).getUserType().equals("farmer"))
            loadDataAnalytics();
    }
}
