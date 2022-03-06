package com.example.agrify.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;
import com.example.agrify.adapters.ProductActiveAdapter;
import com.example.agrify.models.Product;
import com.example.agrify.models.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyProduct extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    List<Product> listProductActive,listProductDuplicate,listProductSold,listProductDeleted;
    List<Users> listUsers;
    ProductActiveAdapter productActiveAdapter;
    TextView emptyMessage1,emptyMessage2,emptyMessage3,emptyMessage4;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerViewActive,recyclerViewDuplicate,recyclerViewSold,recyclerViewDeleted;
    String userId = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);
        setTitle("My Product");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        userId = SharedPrefManager.getInstance(getApplicationContext()).getUserID();
        emptyMessage1 = (TextView) findViewById(R.id.empty_view_myproduct_active_id);
        emptyMessage2 = (TextView) findViewById(R.id.empty_view_myproduct_duplicate_id);
        emptyMessage3 = (TextView) findViewById(R.id.empty_view_myproduct_sold_id);
        emptyMessage4 = (TextView) findViewById(R.id.empty_view_myproduct_deleted_id);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_myproduct_id);
        recyclerViewActive = (RecyclerView) findViewById(R.id.recyclerview_active_product_id);
        recyclerViewActive.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDuplicate = (RecyclerView) findViewById(R.id.recyclerview_duplicate_product_id);
        recyclerViewDuplicate.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSold = (RecyclerView) findViewById(R.id.recyclerview_sold_product_id);
        recyclerViewSold.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDeleted = (RecyclerView) findViewById(R.id.recyclerview_deleted_product_id);
        recyclerViewDeleted.setLayoutManager(new LinearLayoutManager(this));
        listProductActive = new ArrayList<>();
        listProductDuplicate = new ArrayList<>();
        listProductSold = new ArrayList<>();
        listProductDeleted = new ArrayList<>();
        listUsers = new ArrayList<>();
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                loadDuplicated();
                loadDataProducts("select_products_by_status","active",listProductActive,listUsers, recyclerViewActive,emptyMessage1,"SOLD");
                loadDataProducts("select_products_by_status","duplicate",listProductDuplicate,listUsers, recyclerViewDuplicate,emptyMessage2,"UPDATE");
                loadDataProducts("select_products_by_status","sold",listProductSold,listUsers, recyclerViewSold,emptyMessage3,"DELETE");
                loadDataProducts("select_products_by_status","delete",listProductDeleted,listUsers, recyclerViewDeleted,emptyMessage4,"REMOVE");
            }
        });



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void loadDataProducts(final String action, final String status, final List<Product> tempProduct, final List<Users> tempUsers, final RecyclerView tempRecyclerView, final TextView emptyView,final String statusProduct){
        swipeRefreshLayout.setRefreshing(true);
        tempProduct.clear();
        tempUsers.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_IP +"action.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("productResponse", ">>" + response);

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject dataobj = array.getJSONObject(i);
                        String id = dataobj.getString("product_id");
                        String name = dataobj.getString("product_name");
                        String category = dataobj.getString("category_name");
                        String stocks = dataobj.getString("product_stocks");
                        double price = dataobj.getDouble("product_price");
                        double rating = dataobj.getDouble("product_rating");
                        String description = dataobj.getString("product_description");
                        String userId = dataobj.getString("user_id");
                        String username = dataobj.getString("user_name");
                        String userLocation = dataobj.getString("user_location");
                        String userNumber = dataobj.getString("user_number");
                        String image1 = dataobj.getString("image_1");
                        String image2 = dataobj.getString("image_2");
                        String image3 = dataobj.getString("image_3");
                        String image4 = dataobj.getString("image_4");
                        String productStockId = dataobj.getString("product_stock_id");
                        tempProduct.add(
                                new Product(id,name,category,price,stocks,rating,description,Constants.URL_IMAGE+image1,Constants.URL_IMAGE+image2,Constants.URL_IMAGE+image3,Constants.URL_IMAGE+image4,productStockId)
                        );
                        tempUsers.add(
                                new Users(userId,username,userLocation,userNumber)
                        );

                    }
                    productActiveAdapter = new ProductActiveAdapter(MyProduct.this,MyProduct.this.getSupportFragmentManager(),tempProduct,tempUsers,statusProduct);
                    tempRecyclerView.removeAllViews();
                    tempRecyclerView.setAdapter(productActiveAdapter);
                    if (tempProduct.size()==0) {
                        tempRecyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        tempRecyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();
                // Adding All values to Params.
                params.put("action", action);
                params.put("status",status);
                params.put("userid",userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MyProduct.this);
        requestQueue.add(stringRequest);


    }


    public void loadDuplicated(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_IP +"action.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("status", ">>" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();
                // Adding All values to Params.
                params.put("action", "detect_duplicate_image");
                params.put("userid",userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MyProduct.this);
        requestQueue.add(stringRequest);


    }

    @Override
    public void onRefresh() {
        loadDuplicated();
        loadDataProducts("select_products_by_status","active",listProductActive,listUsers, recyclerViewActive,emptyMessage1,"SOLD");
        loadDataProducts("select_products_by_status","duplicate",listProductDuplicate,listUsers, recyclerViewDuplicate,emptyMessage2,"UPDATE");
        loadDataProducts("select_products_by_status","sold",listProductSold,listUsers, recyclerViewSold,emptyMessage3,"DELETE");
        loadDataProducts("select_products_by_status","delete",listProductDeleted,listUsers, recyclerViewDeleted,emptyMessage4,"REMOVE");
    }
}
