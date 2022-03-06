package com.example.agrify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.agrify.R;
import com.example.agrify.adapters.ProductAdapter;
import com.example.agrify.models.Product;
import com.example.agrify.models.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    LinearLayout fertilizer,machinery,medicine,feeds;
    SwipeRefreshLayout refreshLayout;
    EditText searhProduct;
    List<Product> mProduct,mProductRating;
    List<Users> mUsers,mUsersRating;
    private RecyclerView recyclerView,recyclerViewRating;
    ProductAdapter productAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        setTitle("Needs");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        fertilizer = (LinearLayout) findViewById(R.id.fertilizerCategory);
        machinery = (LinearLayout) findViewById(R.id.machineCategory);
        medicine = (LinearLayout) findViewById(R.id.medecineCategory);
        feeds = (LinearLayout) findViewById(R.id.feedsCategory);
        searhProduct = (EditText) findViewById(R.id.editTextSearch);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_product_front);
        recyclerViewRating = (RecyclerView) findViewById(R.id.recyclerview_product_ratings);
        recyclerViewRating.setHasFixedSize(true);//every item of the RecyclerView has a fix size
        recyclerViewRating.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setHasFixedSize(true);//every item of the RecyclerView has a fix size
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        mProduct = new ArrayList<>();
        mUsers = new ArrayList<>();
        mProductRating = new ArrayList<>();
        mUsersRating = new ArrayList<>();
        searhProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterData(editable.toString());
            }
        });
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_main_id);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        refreshLayout.post(new Runnable() {

            @Override
            public void run() {

                refreshLayout.setRefreshing(true);

                // Fetching data from server
                loadDataFirstProducts("select_products_recent_products",mProduct,mUsers,recyclerView);
                loadDataFirstProducts("select_products_ratings_products",mProductRating,mUsersRating,recyclerViewRating);
            }
        });

        fertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ProductViewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("category","fertilizer");
                i.putExtra("category2","pesticide");
                startActivity(i);
            }
        });
        machinery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ProductViewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("category","machines");
                i.putExtra("category2","equipments");
                startActivity(i);
            }
        });
        medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ProductViewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("category","medicine");
                i.putExtra("category2","");
                startActivity(i);
            }
        });
        feeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ProductViewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("category","feeds");
                i.putExtra("category2","");
                startActivity(i);
            }
        });
        TextView seeAllTextView = (TextView) findViewById(R.id.seeAll_product_id);
        seeAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ProductViewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("category","");
                i.putExtra("category2","");
                i.putExtra("category3", "SEE ALL PRODUCTS");
                startActivity(i);
            }
        });
        TextView seeAllTextView2 = (TextView) findViewById(R.id.seeAll2_product_id);
        seeAllTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ProductViewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("category","");
                i.putExtra("category2","");
                i.putExtra("category3", "RECOMMENDED PRODUCTS");
                startActivity(i);
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
    public void loadDataFirstProducts(final String action, final List<Product> tempProduct, final List<Users> tempUsers, final RecyclerView tempRecyclerView){
        refreshLayout.setRefreshing(true);
        tempProduct.clear();
        tempUsers.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_IP +"action.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                            if (dialog.isShowing()) {
//                                dialog.dismiss();
//                            }
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
                    productAdapter = new ProductAdapter(MainActivity.this,tempProduct,tempUsers);
                    tempRecyclerView.removeAllViews();
                    tempRecyclerView.setAdapter(productAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                refreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshLayout.setRefreshing(false);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();
                // Adding All values to Params.
                params.put("action", action);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);


    }


    @Override
    public void onRefresh() {
        loadDataFirstProducts("select_products_recent_products",mProduct,mUsers,recyclerView);
        loadDataFirstProducts("select_products_ratings_products",mProductRating,mUsersRating,recyclerViewRating);

    }
    public void filterData(String text) {
        List<Product> tempProduct = new ArrayList<>();
        for (Product item: mProduct) {
            if(item.getProductName().toLowerCase().contains(text.toLowerCase())){
                tempProduct.add(item);
            }
        }
        productAdapter.filterProduct(tempProduct);
    }
}
