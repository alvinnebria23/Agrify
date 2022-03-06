package com.example.agrify.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class ProductViewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    List<Product> listProduct;
    List<Users> mUsers;
    StringRequest stringRequest = null;
    String category="",category2="", category3 = "";
    RecyclerView recyclerView = null;
    ProductAdapter productAdapter = null;
    SwipeRefreshLayout refreshLayout;
    EditText searchProduct;
    TextView emptyMessage1;
    Button filterButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent intent = getIntent();
        emptyMessage1 = (TextView) findViewById(R.id.empty_view_productview_id);
        category = intent.getExtras().getString("category");
        category2 = intent.getExtras().getString("category2");
        TextView categoryName = (TextView) findViewById(R.id.category_name_id);
        filterButton = (Button) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterButton.setEnabled(false);
                openNeedsFilterDialog();
            }
        });
        if (category2.isEmpty()) {
            category3 = intent.getExtras().getString("category3");
            categoryName.setText(category3);
            setTitle("Needs");
        }else{
            categoryName.setText("CATEGORY > "+category.toUpperCase()+" & "+category2.toUpperCase() );
            setTitle(category.toUpperCase().charAt(0) + category.toLowerCase()
                    .substring(1, category.length()) + " & " +
                    category2.toUpperCase().charAt(0) + category2.toLowerCase()
                    .substring(1, category2.length()));
        }
        searchProduct = (EditText) findViewById(R.id.editTextSearch_id);
        searchProduct.addTextChangedListener(new TextWatcher() {
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
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_productview_id);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        listProduct = new ArrayList<>();
        mUsers = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_product_id);
        recyclerView.setLayoutManager(new GridLayoutManager(ProductViewActivity.this,2));
        refreshLayout.post(new Runnable() {

            @Override
            public void run() {

                refreshLayout.setRefreshing(true);

                // Fetching data from server
                new LoadData().execute();


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
        new LoadData().execute();
    }

    public class LoadData extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(true);

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            listProduct.clear();
            mUsers.clear();
            stringRequest = new StringRequest(Request.Method.POST, Constants.URL_IP +"action.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.i("productFrontResponse", ">>" + response);

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
                            listProduct.add(
                                    new Product(id,name,category,price,stocks,rating,description,Constants.URL_IMAGE+image1,Constants.URL_IMAGE+image2,Constants.URL_IMAGE+image3,Constants.URL_IMAGE+image4,productStockId)
                            );
                            mUsers.add(
                                    new Users(userId,username,userLocation,userNumber)
                            );

                        }
                        productAdapter = new ProductAdapter(ProductViewActivity.this,listProduct,mUsers);
                        recyclerView.removeAllViews();
                        recyclerView.setAdapter(productAdapter);
                        if (listProduct.size()==0) {
                            recyclerView.setVisibility(View.GONE);
                            emptyMessage1.setVisibility(View.VISIBLE);
                        }
                        else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyMessage1.setVisibility(View.GONE);
                        }




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
                    params.put("action", "selecy_products_by_category");
                    params.put("category", category);
                    params.put("category2", category2);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(ProductViewActivity.this);
            requestQueue.add(stringRequest);




            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);


        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }
    }
    public void filterData(String text){
        List<Product> tempProduct = new ArrayList<>();
        List<Users> tempUsers = new ArrayList<>();
        for (Product item: listProduct) {
            if(item.getProductName().toLowerCase().contains(text.toLowerCase())){
                tempProduct.add(item);
            }
        }
        if (tempProduct.size()==0) {
            recyclerView.setVisibility(View.GONE);
            emptyMessage1.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessage1.setVisibility(View.GONE);
            productAdapter.filterProduct(tempProduct);
        }

    }
    public void openNeedsFilterDialog(){
        NeedsFilterDialog needsFilterDialog = new NeedsFilterDialog();
        needsFilterDialog.show(getSupportFragmentManager(), "Needs Filter Dialog");
        needsFilterDialog.setCancelable(false);
        needsFilterDialog.setDialogResult(new NeedsFilterDialog.OnMyDialogResult() {
            @Override
            public void finish(List<Product> filteredProductsList, List<Users> filteredUsersList) {
                filterButton.setEnabled(true);
                productAdapter.filteredProducts(filteredProductsList, filteredUsersList);
            }
        });
    }
    public List<Product> getProductList(){
        return listProduct;
    }
    public List<Users> getUsersList(){
        return mUsers;
    }
}
