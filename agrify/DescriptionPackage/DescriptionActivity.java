package com.example.agrify.DescriptionPackage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.agrify.ChatPackage.ChatActivity;
import com.example.agrify.Constants;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.ProfilePackage.ProfileActivity;
import com.example.agrify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DescriptionActivity extends AppCompatActivity {
    String  profilePicture, fullName, location, contactNumber, demandKilograms, neededKilograms,
            productName, price, varietyName, agriculturalSector, durationEnd, description, demand,
            demandID, userIDFromAnotherActivity, tempKey = "";

    private TextView fullNameTextView, locationTextView, contactNumberTextView,
            productNameTextView, priceTextView, varietyTextView, typeTextView,
            durationEndTextView, descriptionTextView, demandTextView,
            demandLabelTextView, viewProfileTextview;

    private ViewPager viewPager;
    private ImageView profilePictureImageView;
    private LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private Button messageButton;
    List<SliderUtils> sliderImg;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        setTitle("Description");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        sliderImg                   = new ArrayList<>();
        profilePicture              = getIntent().getExtras().getString("profilePicture");
        fullName                    = getIntent().getExtras().getString("fullName");
        location                    = getIntent().getExtras().getString("location");
        contactNumber               = getIntent().getExtras().getString("contactNumber");
        demandKilograms             = getIntent().getExtras().getString("demandKilograms");
        neededKilograms             = getIntent().getExtras().getString("neededKilograms");
        productName                 = getIntent().getExtras().getString("productName");
        price                       = getIntent().getExtras().getString("price");
        varietyName                 = getIntent().getExtras().getString("varietyName");
        agriculturalSector          = getIntent().getExtras().getString("agriculturalSector");
        durationEnd                 = getIntent().getExtras().getString("durationEnd");
        description                 = getIntent().getExtras().getString("description");
        demandID                    = getIntent().getExtras().getString("demandID");
        userIDFromAnotherActivity   = getIntent().getExtras().getString("userID");

        fullNameTextView        =   (TextView)  findViewById(R.id.fullNameTextView);
        locationTextView        =   (TextView)  findViewById(R.id.locationTextView);
        contactNumberTextView   =   (TextView)  findViewById(R.id.contactNumberTextView);
        productNameTextView     =   (TextView)  findViewById(R.id.productNameTextView);
        priceTextView           =   (TextView)  findViewById(R.id.priceTextView);
        varietyTextView         =   (TextView)  findViewById(R.id.varietyTextView);
        typeTextView            =   (TextView)  findViewById(R.id.typeTextView);
        durationEndTextView     =   (TextView)  findViewById(R.id.durationEndTextView);
        descriptionTextView     =   (TextView)  findViewById(R.id.descriptionTextView);
        demandTextView          =   (TextView)  findViewById(R.id.demandTextView);
        viewProfileTextview     =   (TextView)  findViewById(R.id.viewProfileTextView);
        demandLabelTextView     =   (TextView)  findViewById(R.id.demandLabelTextView);
        profilePictureImageView =   (ImageView) findViewById(R.id.profilePictureImageView);
        viewPager               =   (ViewPager) findViewById(R.id.viewPager);
        messageButton           =   (Button)    findViewById(R.id.messageButton);
        sliderDotspanel         =   (LinearLayout) findViewById(R.id.SliderDots);

        loadDemandImages();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i < dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewProfileTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userID", String.valueOf(userIDFromAnotherActivity));
                intent.putExtra("profileType", "viewProfile");
                startActivity(intent);
            }
        });
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareIntent();
            }
        });
        checkDemandLabel(demandKilograms);
        String tempDemandKilograms = demandKilograms.replace(" PCS", "");
        Spannable word = new SpannableString(tempDemandKilograms.replace(" KG", ""));
        word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        demandTextView.setText(word);

        Spannable wordTwo = new SpannableString("/");
        wordTwo.setSpan(new ForegroundColorSpan(Color.GRAY), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        demandTextView.append(wordTwo);

        Spannable wordThree = new SpannableString(neededKilograms.replace(" KG", ""));
        wordThree.setSpan(new ForegroundColorSpan(Color.GREEN), 0, wordThree.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        demandTextView.append(wordThree);

        Glide.with(getApplicationContext())
                .load(profilePicture)
                .into(profilePictureImageView);

        fullNameTextView.setText(fullName.toUpperCase());
        locationTextView.setText(location.toUpperCase());
        contactNumberTextView.setText(contactNumber);
        productNameTextView.setText(productName.toUpperCase());
        priceTextView.setText(price);
        varietyTextView.setText(varietyName.toUpperCase());
        typeTextView.setText(agriculturalSector);
        durationEndTextView.setText(durationEnd);
        descriptionTextView.setText(description);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            this.finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    public void loadDemandImages(){
        if(String.valueOf(userIDFromAnotherActivity).equals(SharedPrefManager.getInstance(getApplicationContext()).getUserID())){
            viewProfileTextview.setVisibility(View.GONE);
            messageButton.setVisibility(View.GONE);
        }
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_DEMAND_IMAGES,
            new Response.Listener<String> () {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray array = new JSONArray (response);
                        JSONObject images = array.getJSONObject (0);

                        for (int i = 1; i <= 4; i++) {
                            String columnName = "image_" + i;
                            SliderUtils sliderUtils = new SliderUtils();

                            sliderUtils.setSliderImageUrl(Constants.PATH_DEMAND_IMAGES_FOLDER+images.getString(columnName));
                            sliderImg.add(sliderUtils);
                        }
                        viewPagerAdapter = new ViewPagerAdapter(sliderImg, DescriptionActivity.this);
                        viewPager.setAdapter(viewPagerAdapter);
                        dotscount = viewPagerAdapter.getCount();
                        dots = new ImageView[dotscount];

                        for(int i = 0; i < dotscount; i++){
                            dots[i] = new ImageView(DescriptionActivity.this);
                            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(8, 0, 8, 0);
                            sliderDotspanel.addView(dots[i], params);

                        }
                        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
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
                params.put ("demand_id", demandID);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void prepareIntent(){
        final String myUserID =  String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUserID());
        final String otherUserID = userIDFromAnotherActivity;
        final DatabaseReference userIDRef = FirebaseDatabase.getInstance().getReference("userID");
        DatabaseReference reference = userIDRef.child(myUserID)
                .child(otherUserID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    DatabaseReference newReference = FirebaseDatabase.getInstance().getReference("userID")
                            .child(myUserID);
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put(userIDFromAnotherActivity, "");
                    newReference.updateChildren(map1);

                    Map<String, Object> map2 = new HashMap<String, Object>();
                    DatabaseReference conversationIDReference = FirebaseDatabase.getInstance().getReference("userID")
                            .child(myUserID).child(userIDFromAnotherActivity);
                    tempKey = conversationIDReference.push().getKey();
                    tempKey = removeUnwantedSymbols(tempKey);
                    if(tempKey != null){
                        map2.put(tempKey, "");
                    }
                    conversationIDReference.updateChildren(map2);

                    DatabaseReference newReference1 = FirebaseDatabase.getInstance()
                            .getReference("userID")
                            .child(userIDFromAnotherActivity);
                    Map<String, Object> map3 = new HashMap<String, Object>();
                    map3.put(myUserID, "");
                    newReference1.updateChildren(map3);

                    Map<String, Object> map4 = new HashMap<String, Object>();
                    DatabaseReference conversationIDReference1 = FirebaseDatabase.getInstance()
                            .getReference("userID")
                            .child(userIDFromAnotherActivity).child(myUserID);
                    if(tempKey != null){
                        map4.put(tempKey, "");
                    }
                    conversationIDReference1.updateChildren(map4);

                    DatabaseReference conversationIDThread = FirebaseDatabase.getInstance()
                            .getReference("conversationID");
                    conversationIDThread.updateChildren(map4);

                    Map<String, Object> map5= new HashMap<String, Object>();
                    map5.put(tempKey, "");
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                            .getReference("userID").child(myUserID).child(otherUserID).child(tempKey);
                    if(tempKey != null){
                        map5.put(tempKey, "");
                    }
                    databaseReference.child(tempKey).setValue(tempKey);
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance()
                            .getReference("userID").child(otherUserID).child(myUserID).child(tempKey);
                    if(tempKey != null){
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
        intent.putExtra("profilePicture", profilePicture);
        intent.putExtra("fullName", fullName);
        startActivity(intent);
    }
    public String removeUnwantedSymbols(String tempKey){
        tempKey = tempKey.replaceAll("[^a-zA-Z0-9]", "");
        return tempKey;
    }
    public void checkDemandLabel(String demandKilograms){
        if(demandKilograms.contains("PCS"))
            demandLabelTextView.setText("DEMAND (pcs)");
    }
}
