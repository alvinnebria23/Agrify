package com.example.agrify.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.agrify.ChatPackage.ChatActivity;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.ProfilePackage.ProfileActivity;
import com.example.agrify.R;
import com.example.agrify.RequestHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ProductDescription extends AppCompatActivity {
    Button messageButton, favoratesButton;
    TextView userNameTextView, userLocationTextView, userNumberTextView, userViewTextView,
            productNameTextView, productPriceTextView, productCategoryTextView,
            productStocksTextView, productDescriptionTextView;
    ImageView imageView, imageView2, imageView3, imageView4, profilePictureImageView;
    RatingBar ratingbar, ratingBarDescription;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    String productStockId;
    String userId;
    String tempKey = "", username = "";
    float rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        setTitle("Product Description");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent intent = getIntent();
        userId = intent.getExtras().getString("userId");
        username = intent.getExtras().getString("userName");
        String location = intent.getExtras().getString("userLocation");
        String userNumber = intent.getExtras().getString("userNumber");
        String productName = intent.getExtras().getString("productName");
        String productPrice = intent.getExtras().getString("productPrice");
        String productCategory = intent.getExtras().getString("productCategory");
        String productDescription = intent.getExtras().getString("productDescription");
        String productStocks = intent.getExtras().getString("productStocks");
        String image1 = intent.getExtras().getString("image1");
        String image2 = intent.getExtras().getString("image2");
        String image3 = intent.getExtras().getString("image3");
        String image4 = intent.getExtras().getString("image4");
        String rateProduct = intent.getExtras().getString("productRating");
        productStockId = intent.getExtras().getString("productStockId");
        ref();
        setData(username, location, userNumber, productName, productPrice, productCategory, productDescription, image1, productStocks, image2, image3, image4, rateProduct);
        messageButton = (Button) findViewById(R.id.messageButton);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareIntent();
            }
        });

        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                Toast.makeText(getApplicationContext(), "rating: " + rating, Toast.LENGTH_LONG).show();
                rate = rating;
                new LoadData(ProductDescription.this).execute();
            }
        });
        favoratesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InsertFavorates(ProductDescription.this).execute();
            }
        });
        userViewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDescription.this, ProfileActivity.class);
                intent.putExtra("userID", userId);
                intent.putExtra("profileType", "viewProfile");
                startActivity(intent);
            }
        });
        checkUserID();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void ref() {
        userNameTextView = (TextView) findViewById(R.id.description_username_id);
        userLocationTextView = (TextView) findViewById(R.id.description_location_id);
        userNumberTextView = (TextView) findViewById(R.id.description_number_id);
        productNameTextView = (TextView) findViewById(R.id.description_product_name_id);
        productCategoryTextView = (TextView) findViewById(R.id.description_product_category_id);
        productPriceTextView = (TextView) findViewById(R.id.description_product_price_id);
        productStocksTextView = (TextView) findViewById(R.id.description_product_stock_id);
        productDescriptionTextView = (TextView) findViewById(R.id.description_product_description_id);
        userViewTextView = (TextView) findViewById(R.id.description_view_id);
        imageView = (ImageView) findViewById(R.id.product_description_image_id);
        imageView2 = (ImageView) findViewById(R.id.product_description_image2_id);
        imageView3 = (ImageView) findViewById(R.id.product_description_image3_id);
        imageView4 = (ImageView) findViewById(R.id.product_description_image4_id);
        profilePictureImageView = (ImageView) findViewById(R.id.profilePictureImageView);
        ratingbar = (RatingBar) findViewById(R.id.ratingBar_user_id);
        ratingBarDescription = (RatingBar) findViewById(R.id.ratingBar_description_id);
        favoratesButton = (Button) findViewById(R.id.button_favorates_id);
    }

    public void setData(String username, String location, String number, String productName, String productPrice, String productCategory, String productDescription, String image1, String productStocks, String image2, String image3, String image4, String rateProduct) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String formattedProductPrice       = formatter.format(Double.parseDouble(productPrice));
        String formattedProductStocks      = formatter.format(Double.parseDouble(productStocks));
        userNameTextView.setText(username.toUpperCase());
        userLocationTextView.setText(location.toUpperCase());
        userNumberTextView.setText(number);
        retrieveUserProfilePic();
        productNameTextView.setText(productName.toUpperCase());
        productPriceTextView.setText("P " + formattedProductPrice);
        productCategoryTextView.setText(productCategory.toUpperCase());
        productDescriptionTextView.setText(productDescription.toUpperCase());
        productStocksTextView.setText(formattedProductStocks + " STOCKS LEFT");
        ratingBarDescription.setRating(Float.parseFloat(rateProduct));
        Picasso.get()
                .load(image1)
                .resize(200, 200)
                .centerCrop()
                .into(imageView);
        Picasso.get()
                .load(image2)
                .resize(200, 200)
                .centerCrop()
                .into(imageView2);
        Picasso.get()
                .load(image3)
                .resize(200, 200)
                .centerCrop()
                .into(imageView3);
        Picasso.get()
                .load(image4)
                .resize(200, 200)
                .centerCrop()
                .into(imageView4);
    }

    public class LoadData extends AsyncTask {
        private ProgressDialog dialog;
        private AppCompatActivity activity;
        private Context context;

        //private List<Message> messages;
        public LoadData(AppCompatActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Loading Product Description");
            this.dialog.show();


        }


        @Override
        protected Object doInBackground(Object[] objects) {
            stringRequest = new StringRequest(Request.Method.POST, Constants.URL_IP + "action.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Log.i("updateRatingResponse", ">>" + response);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Creating Map String Params.
                    Map<String, String> params = new HashMap<String, String>();
                    // Adding All values to Params.
                    params.put("action", "update_product_rating");
                    params.put("rating", rate + "");
                    params.put("productStockId", productStockId + "");
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(ProductDescription.this);
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

    public class InsertFavorates extends AsyncTask {
        private ProgressDialog dialog;
        private AppCompatActivity activity;
        private Context context;

        //private List<Message> messages;
        public InsertFavorates(AppCompatActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Adding to favorites");
            this.dialog.show();


        }


        @Override
        protected Object doInBackground(Object[] objects) {
            stringRequest = new StringRequest(Request.Method.POST, Constants.URL_IP + "action.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Log.i("addToFavoratesResponse", ">>" + response);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Creating Map String Params.
                    Map<String, String> params = new HashMap<String, String>();
                    // Adding All values to Params.
                    params.put("action", "insert_product_favorates");
                    params.put("userid", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                    params.put("productStockId", productStockId + "");
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(ProductDescription.this);
            requestQueue.add(stringRequest);


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Toast.makeText(ProductDescription.this, "ADDED TO FAVORATE", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }
    }

    public void prepareIntent() {
        final String myUserID = String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUserID());
        final String otherUserID = userId;
        final DatabaseReference userIDRef = FirebaseDatabase.getInstance().getReference("userID");
        DatabaseReference reference = userIDRef.child(myUserID)
                .child(otherUserID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    DatabaseReference newReference = FirebaseDatabase.getInstance().getReference("userID")
                            .child(myUserID);
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put(userId, "");
                    newReference.updateChildren(map1);

                    Map<String, Object> map2 = new HashMap<String, Object>();
                    DatabaseReference conversationIDReference = FirebaseDatabase.getInstance().getReference("userID")
                            .child(myUserID).child(userId);
                    tempKey = conversationIDReference.push().getKey();
                    tempKey = removeUnwantedSymbols(tempKey);
                    if (tempKey != null) {
                        map2.put(tempKey, "");
                    }
                    conversationIDReference.updateChildren(map2);

                    DatabaseReference newReference1 = FirebaseDatabase.getInstance()
                            .getReference("userID")
                            .child(userId);
                    Map<String, Object> map3 = new HashMap<String, Object>();
                    map3.put(myUserID, "");
                    newReference1.updateChildren(map3);

                    Map<String, Object> map4 = new HashMap<String, Object>();
                    DatabaseReference conversationIDReference1 = FirebaseDatabase.getInstance()
                            .getReference("userID")
                            .child(userId).child(myUserID);
                    if (tempKey != null) {
                        map4.put(tempKey, "");
                    }
                    conversationIDReference1.updateChildren(map4);

                    DatabaseReference conversationIDThread = FirebaseDatabase.getInstance()
                            .getReference("conversationID");
                    conversationIDThread.updateChildren(map4);

                    Map<String, Object> map5 = new HashMap<String, Object>();
                    map5.put(tempKey, "");
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                            .getReference("userID").child(myUserID).child(otherUserID).child(tempKey);
                    if (tempKey != null) {
                        map5.put(tempKey, "");
                    }
                    databaseReference.child(tempKey).setValue(tempKey);
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance()
                            .getReference("userID").child(otherUserID).child(myUserID).child(tempKey);
                    if (tempKey != null) {
                        map5.put(tempKey, "");
                    }
                    databaseReference1.child(tempKey).setValue(tempKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("previousActivity", "DescriptionActivity");
        intent.putExtra("userID", otherUserID);
        intent.putExtra("profilePicture", "sample");
        intent.putExtra("fullName", username);
        startActivity(intent);
    }

    public String removeUnwantedSymbols(String tempKey) {
        tempKey = tempKey.replaceAll("[^a-zA-Z0-9]", "");
        return tempKey;
    }

    public void checkUserID() {
        if (userId.equals(SharedPrefManager.getInstance(getApplicationContext()).getUserID()))
            messageButton.setVisibility(View.GONE);
    }

    public void retrieveUserProfilePic() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, com.example.agrify.Constants.URL_RETRIEVE_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject profile = array.getJSONObject(0);

                            Glide.with(getApplicationContext())
                                    .load(com.example.agrify.Constants.PATH_PROFILE_IMAGES_FOLDER +
                                            profile.getString("profile_pic"))
                                    .into(profilePictureImageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
