package com.example.agrify.ProfilePackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.agrify.AddDemandPackage.ImageRequestHandler;
import com.example.agrify.ChatPackage.ChatActivity;
import com.example.agrify.Constants;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.MyDemandPackage.MyDemandActivity;
import com.example.agrify.R;
import com.example.agrify.activities.MyProduct;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    public static final int PICK_IMAGE_REQUEST = 1;
    private TextView locationTextView;
    private EditText fullNameEditText, descriptionEditText, userTypeEditText,
                    contactNumberEditText, emailAddressEditText;
    private Button editProfileButton, myDemandButton, messageButton,
                    updateButton, myProductButton;
    private ImageView profilePictureImageView, updatePhotoImageView;
    private LinearLayout contactNumberLinearLayout, emailAddressLinearLayout, locationLinearLayout;
    private Bitmap bitmap;
    private AutocompleteSupportFragment autocompleteFragment;
    String userIDString, userIDFromAnotherActivity, profileType, action = "nonewprofilepicture",
            profilePicture = "", fullName = "", tempKey = "", locationString = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userIDFromAnotherActivity       = getIntent().getExtras().getString("userID");
        profileType                     = getIntent().getExtras().getString("profileType");
        setTitle("Profile");
        Places.initialize(getApplicationContext(), "AIzaSyCb4h9xwGSZTiTH83sLALrjh_Dfn8XIWK0");
        PlacesClient placesClient = Places.createClient(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        fullNameEditText        = (EditText) findViewById(R.id.fullNameEditText);
        descriptionEditText     = (EditText) findViewById(R.id.descriptionEditText);
        userTypeEditText        = (EditText) findViewById(R.id.userTypeEditText);
        contactNumberEditText   = (EditText) findViewById(R.id.contactNumberEditText);
        emailAddressEditText    = (EditText) findViewById(R.id.emailAddressEditText);
        locationTextView        = (TextView) findViewById(R.id.locationTextView);
        editProfileButton       = (Button) findViewById(R.id.editProfileButton);
        myProductButton         = (Button)  findViewById(R.id.myProductButton);
        myDemandButton          = (Button) findViewById(R.id.myDemandButton);
        messageButton           = (Button) findViewById(R.id.messageButton);
        updateButton            = (Button) findViewById(R.id.updateButton);
        profilePictureImageView = (ImageView) findViewById(R.id.profilePictureImageView);
        updatePhotoImageView    = (ImageView) findViewById(R.id.updatePhotoImageView);
        contactNumberLinearLayout = (LinearLayout) findViewById(R.id.contactNumberLinearLayout);
        emailAddressLinearLayout =  (LinearLayout) findViewById(R.id.emailAddressLinearLayout);
        locationLinearLayout    =   (LinearLayout)  findViewById(R.id.locationLinearLayout);
        contactNumberEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.White), PorterDuff.Mode.SRC_ATOP);
        emailAddressEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.White), PorterDuff.Mode.SRC_ATOP);

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        autocompleteFragment.setCountry ("PH");
        autocompleteFragment.setOnPlaceSelectedListener (new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                locationTextView.setText(place.getAddress());
            }
            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText (getApplicationContext(), status.toString (), Toast.LENGTH_LONG).show ();
            }
        });
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDemandButton.setVisibility(View.GONE);
                myProductButton.setVisibility(View.GONE);
                updateButton.setVisibility(View.VISIBLE);
                editProfileButton.setVisibility(View.GONE);
                updatePhotoImageView.setVisibility(View.VISIBLE);
                fullNameEditText.setEnabled(true);
                descriptionEditText.setEnabled(true);
                contactNumberEditText.setEnabled(true);
                emailAddressEditText.setEnabled(true);
                profilePictureImageView.setEnabled(true);
                fullNameEditText.setBackgroundResource(R.drawable.greenrectangle);
                descriptionEditText.setBackgroundResource(R.drawable.greenrectangle);
                contactNumberLinearLayout.setBackgroundResource(R.drawable.greenrectangle);
                emailAddressLinearLayout.setBackgroundResource(R.drawable.greenrectangle);
                locationLinearLayout.setBackgroundResource(R.drawable.green_rectangle);
                locationTextView.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().show(autocompleteFragment).commit();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputCorrect()) {
                    updateButton.setVisibility(View.GONE);
                    updatePhotoImageView.setVisibility(View.GONE);
                    editProfileButton.setVisibility(View.VISIBLE);
                    if(SharedPrefManager.getInstance(getApplicationContext())
                            .getUserType().equals("vendor"))
                        myDemandButton.setVisibility(View.VISIBLE);
                    else if(SharedPrefManager.getInstance(getApplicationContext())
                            .getUserType().equals("provider"))
                        myProductButton.setVisibility(View.VISIBLE);
                    fullNameEditText.setEnabled(false);
                    descriptionEditText.setEnabled(false);
                    contactNumberEditText.setEnabled(false);
                    emailAddressEditText.setEnabled(false);
                    profilePictureImageView.setEnabled(false);
                    fullNameEditText.setBackgroundResource(0);
                    descriptionEditText.setBackgroundResource(0);
                    contactNumberLinearLayout.setBackgroundResource(R.drawable.button_shadow);
                    emailAddressLinearLayout.setBackgroundResource(R.drawable.button_shadow);
                    locationLinearLayout.setBackgroundResource(R.drawable.button_shadow);
                    getSupportFragmentManager().beginTransaction().hide(autocompleteFragment).commit();
                    locationTextView.setVisibility(View.VISIBLE);
                    uploadImages();
                    updateSharedPreference();
                }
            }
        });
        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareIntent();
            }
        });
        myDemandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), MyDemandActivity.class));
            }
        });
        myProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), MyProduct.class));
            }
        });
        setProfileType(profileType);
        getUserProfileInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            this.finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                profilePictureImageView.setImageBitmap(bitmap);
                action = "newprofilepicture";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void getUserProfileInfo(){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_USER_PROFILE,
            new Response.Listener<String> () {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONArray array = new JSONArray (response);

                        for(int i = 0; i < array.length(); i++) {
                            JSONObject userInfo = array.getJSONObject(i);
                            profilePicture = Constants.PATH_PROFILE_IMAGES_FOLDER+userInfo.getString("profile_pic");
                            fullName = userInfo.getString("fullname").toUpperCase();
                            Glide.with (getApplicationContext()).load (profilePicture).into (profilePictureImageView);
                            checkLocationTextSize(userInfo.getString("location").toUpperCase());
                            userIDString = userInfo.getString("user_id");
                            fullNameEditText.setText(fullName);
                            descriptionEditText.setText(userInfo.getString("profile_description"));
                            locationTextView.setText(userInfo.getString("location").toUpperCase());
                            userTypeEditText.setText(userInfo.getString("user_type").toUpperCase());
                            contactNumberEditText.setText(userInfo.getString("contact_no"));
                            emailAddressEditText.setText(userInfo.getString("email_address"));
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
                params.put ("user_id", userIDFromAnotherActivity);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void setProfileType(String profileType){
        if(profileType.equals("viewProfile")){
            editProfileButton.setVisibility(View.GONE);
            myDemandButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.GONE);
            updatePhotoImageView.setVisibility(View.GONE);
            messageButton.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().hide(autocompleteFragment).commit();
            profilePictureImageView.setEnabled(false);
        }else if(profileType.equals("myProfile")){
            if(SharedPrefManager.getInstance(getApplicationContext())
                    .getUserType().toLowerCase().equals("vendor"))
                myDemandButton.setVisibility(View.VISIBLE);
            else if(SharedPrefManager.getInstance(getApplicationContext())
                    .getUserType().toLowerCase().equals("provider"))
                myProductButton.setVisibility(View.VISIBLE);
            else if(SharedPrefManager.getInstance(getApplicationContext())
                    .getUserType().toLowerCase().equals("farmer")){
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) editProfileButton.getLayoutParams();
                params.setMarginStart(240);
                editProfileButton.setLayoutParams(params);
            }
            editProfileButton.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.GONE);
            updatePhotoImageView.setVisibility(View.GONE);
            messageButton.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().hide(autocompleteFragment).commit();
            profilePictureImageView.setEnabled(false);

        }
    }
    private void uploadImages() {
        final String[] nameArray = fullNameEditText.getText().toString().split(" ");
        String tempImage = "";
        if(action.equals("nonewprofilepicture")){
            tempImage = "temp";
        }else if(action.equals("newprofilepicture")){
            tempImage = getStringImage(bitmap);
        }
        final String profileImage = tempImage;

        class UploadImage extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;
            ImageRequestHandler rh = new ImageRequestHandler();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfileActivity.this,null,"Uploading . . .",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                action = "nonewprofilepicture";
                Toast.makeText(getApplicationContext(), "USER PROFILE UPDATED SUCCESSFULLY", Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> param = new HashMap<>();
                param.put("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                param.put("profile_image", profileImage);
                param.put("description", descriptionEditText.getText().toString());
                param.put("firstName", nameArray[0].toLowerCase());
                param.put("lastName", nameArray[1].toLowerCase());
                param.put("contactNumber", contactNumberEditText.getText().toString());
                param.put("emailAddress", emailAddressEditText.getText().toString());
                param.put("location", locationTextView.getText().toString());
                param.put("action", action);
                String result = rh.sendPostRequest(Constants.URL_UPDATE_USER_PROFILE, param);
                return result;
            }
        }
        UploadImage u = new UploadImage();
        u.execute();
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void updateSharedPreference(){
        final String[] nameArray = fullNameEditText.getText().toString().split(" ");
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_USER_PROFILE,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray array = new JSONArray (response);

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject userInfo = array.getJSONObject(i);

                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences ("mysharedpref12", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit ();

                                editor.putString("profilePic", Constants.PATH_PROFILE_IMAGES_FOLDER+userInfo.getString("profile_pic"));
                                editor.putString("userFirstName", nameArray[0]);
                                editor.putString("userLastName", nameArray[1]);
                                editor.putString("userEmailAddress", emailAddressEditText.getText().toString());
                                editor.putString("userLocation", locationTextView.getText().toString());
                                editor.apply();

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
                params.put ("user_id", userIDFromAnotherActivity);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public boolean isInputCorrect(){
        boolean correct = true;
        String[] nameArray = fullNameEditText.getText().toString().split(" ");
        if(nameArray.length != 2){
            correct = false;
            Toast.makeText(getApplicationContext(), "Please input your first name and last name only", Toast.LENGTH_SHORT).show();
        }else{
            correct = true;
        }
        return correct;
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
                    map1.put(otherUserID, "");
                    newReference.updateChildren(map1);

                    Map<String, Object> map2 = new HashMap<String, Object>();
                    DatabaseReference conversationIDReference = FirebaseDatabase.getInstance().getReference("userID")
                            .child(myUserID).child(otherUserID);
                    tempKey = conversationIDReference.push().getKey();
                    tempKey = removeUnwantedSymbols(tempKey);
                    if(tempKey != null){
                        map2.put(tempKey, "");
                    }
                    conversationIDReference.updateChildren(map2);

                    DatabaseReference newReference1 = FirebaseDatabase.getInstance()
                            .getReference("userID")
                            .child(otherUserID);
                    Map<String, Object> map3 = new HashMap<String, Object>();
                    map3.put(myUserID, "");
                    newReference1.updateChildren(map3);

                    Map<String, Object> map4 = new HashMap<String, Object>();
                    DatabaseReference conversationIDReference1 = FirebaseDatabase.getInstance()
                            .getReference("userID")
                            .child(otherUserID).child(myUserID);
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
        intent.putExtra("previousActivity", "ProfileActivity");
        intent.putExtra("userID", otherUserID);
        intent.putExtra("profilePicture", profilePicture);
        intent.putExtra("fullName", fullName);
        startActivity(intent);
    }
    public void checkLocationTextSize(String location){
        if(location.length() > 35)
            locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        else
            locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    }
    public String removeUnwantedSymbols(String tempKey){
        tempKey = tempKey.replaceAll("[^a-zA-Z0-9]", "");
        return tempKey;
    }
}
