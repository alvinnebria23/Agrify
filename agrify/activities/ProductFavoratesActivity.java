package com.example.agrify.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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
import com.example.agrify.adapters.ProductFavorateAdapter;
import com.example.agrify.models.Product;
import com.example.agrify.models.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductFavoratesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    List<Product> listProduct;
    List<Users> mUsers;
    RequestQueue requestQueue = null;
    StringRequest stringRequest = null;
    RecyclerView recyclerView = null;
    ProductFavorateAdapter productFavoratesAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_favorates);
        setTitle("My Favorites");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_favorates_product_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductFavoratesActivity.this));
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                new LoadDataProcess(ProductFavoratesActivity.this).execute();
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

    @Override
    public void onRefresh() {
        new LoadDataProcess(ProductFavoratesActivity.this).execute();
    }

    public class LoadDataProcess extends AsyncTask {
        private AppCompatActivity activity;
        private Context context;
        //private List<Message> messages;
        public LoadDataProcess(AppCompatActivity activity) {
            this.activity = activity;
            context = activity;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            listProduct = new ArrayList<>();
            mUsers = new ArrayList<>();
            stringRequest = new StringRequest(Request.Method.POST, Constants.URL_IP +"action.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("productFavorateResponse", ">>" + response);

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
                            String product_favorate_id = dataobj.getString("product_favorate_id");
                            listProduct.add(
                                    new Product(id,name,category,price,stocks,rating,description,Constants.URL_IMAGE+image1,Constants.URL_IMAGE+image2,Constants.URL_IMAGE+image3,Constants.URL_IMAGE+image4,productStockId,product_favorate_id)
                            );
                            mUsers.add(
                                    new Users(userId,username,userLocation,userNumber)
                            );

                        }


                        productFavoratesAdapter = new ProductFavorateAdapter(ProductFavoratesActivity.this,listProduct,mUsers);
                        recyclerView.setAdapter(productFavoratesAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
                    params.put("action", "select_product_favorates");
                    params.put("userid", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(ProductFavoratesActivity.this);
            requestQueue.add(stringRequest);




            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }
    }
}
