package com.example.agrify.RegisterPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.agrify.Constants;
import com.example.agrify.LoginPackage.HomeActivity;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;
import com.example.agrify.RequestHandler;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ProgressDialog progressDialog;
    String typeOfUserString = "", locationString = "0";
    EditText usernameEditText, passwordEditText, firstNameEditText, lastNameEditText, emailAddressEditText, contactNoEditText;
    Button createAccountButton;
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_create_account);

        if(SharedPrefManager.getInstance (this).isLoggedIn ()){
            finish ();
            startActivity (new Intent (this, HomeActivity.class));
            return;
        }
        Places.initialize(getApplicationContext(), "AIzaSyCb4h9xwGSZTiTH83sLALrjh_Dfn8XIWK0");
        PlacesClient placesClient = Places.createClient(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        progressDialog=new ProgressDialog(this);
        Spinner typeOfUserSpinner   =   findViewById (R.id.typeOfUserSpinner);
        usernameEditText            =   (EditText)  findViewById (R.id.usernameEditText);
        passwordEditText            =   (EditText)  findViewById (R.id.passwordEditText);
        firstNameEditText           =   (EditText)  findViewById (R.id.firstNameEditText);
        lastNameEditText            =   (EditText)  findViewById (R.id.lastNameEditText);
        emailAddressEditText        =   (EditText)  findViewById (R.id.emailAddressEditText);
        contactNoEditText           =   (EditText)  findViewById (R.id.contactNoEditText);
        createAccountButton         =   (Button)    findViewById (R.id.createAccountButton);

        typeOfUserSpinner.getBackground().setColorFilter(getResources().getColor(R.color.Black), PorterDuff.Mode.SRC_ATOP);
        usernameEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.GreenGrass), PorterDuff.Mode.SRC_ATOP);
        passwordEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.GreenGrass), PorterDuff.Mode.SRC_ATOP);
        firstNameEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.GreenGrass), PorterDuff.Mode.SRC_ATOP);
        lastNameEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.GreenGrass), PorterDuff.Mode.SRC_ATOP);
        emailAddressEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.GreenGrass), PorterDuff.Mode.SRC_ATOP);
        contactNoEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.GreenGrass), PorterDuff.Mode.SRC_ATOP);

        ArrayAdapter<CharSequence> typeOfUserAdapter = ArrayAdapter.createFromResource (this, R.array.typeofuserarray, R.layout.color_spinner_layout);
        typeOfUserAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        typeOfUserSpinner.setAdapter (typeOfUserAdapter);
        typeOfUserSpinner.setOnItemSelectedListener (this);


        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        autocompleteFragment.setCountry ("PH");
        autocompleteFragment.setOnPlaceSelectedListener (new PlaceSelectionListener () {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                    locationString = place.getAddress();
            }
            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText (CreateAccountActivity.this, status.toString (), Toast.LENGTH_LONG).show ();
            }
        });

        createAccountButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId ();

        if(id == android.R.id.home){
            this.finish ();
        }

        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         String typeOfUserNameString = parent.getItemAtPosition (position).toString ();
         if(typeOfUserNameString.equals ("Farmer"))
             typeOfUserString = "farmer";
         else if(typeOfUserNameString.equals ("Vendor"))
             typeOfUserString = "vendor";
         else
             typeOfUserString = "provider";
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void registerUser(){
        if(TextUtils.isEmpty (usernameEditText.getText ()) ||
                usernameEditText.getText().toString().length() == 0 ||
                usernameEditText.getText().toString().contains(" ")){
            usernameEditText.setError ("Please input the username correctly.");
            usernameEditText.requestFocus ();
        }
        else if(Character.isDigit (usernameEditText.getText ().toString ().charAt (0))){
            usernameEditText.setError ("The first character must be alphabet.");
            usernameEditText.requestFocus ();
        }
        else if(usernameEditText.getText().toString().length() < 6){
            usernameEditText.setError("Username must be atleast 6 characters.");
            usernameEditText.requestFocus();
        }
        else if(TextUtils.isEmpty (passwordEditText.getText ()) ||
                passwordEditText.getText().toString().length() == 0 ||
                usernameEditText.getText().toString().contains(" ")){
            passwordEditText.setError ("Please input the password correctly.");
            passwordEditText.requestFocus ();
        }
        else if(passwordEditText.getText().toString().length() < 5){
            passwordEditText.setError("Password must be atleast 5 characters");
            passwordEditText.requestFocus();
        }
        else if(TextUtils.isEmpty (firstNameEditText.getText ()) ||
                firstNameEditText.getText().toString().length() == 0 ||
                usernameEditText.getText().toString().contains(" ")){
            firstNameEditText.setError ("Please input the first name correctly.");
            firstNameEditText.requestFocus ();
        }
        else if(TextUtils.isEmpty (lastNameEditText.getText ()) ||
                lastNameEditText.getText().toString().length() == 0 ||
                usernameEditText.getText().toString().contains(" ")){
            lastNameEditText.setError ("Please input the last name correctly.");
            lastNameEditText.requestFocus ();
        }
        else if(!isValidEmail() || emailAddressEditText.getText().toString().length() == 0 ||
                usernameEditText.getText().toString().contains(" ")){
            emailAddressEditText.setError ("Please input a valid email.");
            emailAddressEditText.requestFocus ();
        }
        else if(TextUtils.isEmpty (contactNoEditText.getText ())){
            contactNoEditText.setError ("Contact No is required.");
            contactNoEditText.requestFocus ();
        }
        else if(contactNoEditText.getText().toString().length() != 11 &&
                contactNoEditText.getText().toString().length() != 7){
            contactNoEditText.setError("Please input valid contact number");
            contactNoEditText.requestFocus();
        }else if(locationString.equals("0")){
            Toast.makeText(getApplicationContext(), "Please input your location.", Toast.LENGTH_LONG).show();
        }
        else {
            progressDialog.setMessage ("Checking inputs...");
            progressDialog.show ();
            progressDialog.setCanceledOnTouchOutside (false);

            userInfo = new UserInfo (usernameEditText.getText ().toString (),
                    passwordEditText.getText ().toString (),
                    firstNameEditText.getText ().toString (),
                    lastNameEditText.getText ().toString (),
                    emailAddressEditText.getText ().toString (),
                    locationString,
                    contactNoEditText.getText ().toString(),
                    typeOfUserString);

            StringRequest stringRequest = new StringRequest (Request.Method.POST,
                    Constants.URL_REGISTER,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressDialog.dismiss ();
                                JSONObject jsonObject = new JSONObject (response);
                                if(jsonObject.getString ("message").equals ("User registered successfully")){
                                    openDialog ();
                                }
                                else if(jsonObject.getString ("message").equals("Username is unavailable")){
                                    usernameEditText.setError ("The username you have entered is already registered, " +
                                            "please choose different username");
                                    usernameEditText.requestFocus ();
                                }
                                else if(jsonObject.getString ("message").equals("Email address is unavailable")){
                                    emailAddressEditText.setError ("The email address you have entered is already registered," +
                                            "please choose different email address");
                                    emailAddressEditText.requestFocus ();
                                }
                                else{
                                    Toast.makeText (getApplicationContext (), "Some error occurred please try again",
                                            Toast.LENGTH_LONG).show ();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace ();
                                Toast.makeText (getApplicationContext (), e.getMessage (),
                                                    Toast.LENGTH_LONG).show ();
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText (getApplicationContext (), error.getMessage (), Toast.LENGTH_LONG).show ();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put ("username", userInfo.getUsername ());
                    params.put ("password", userInfo.getPassword ());
                    params.put ("first_name", userInfo.getFirstname ().toLowerCase());
                    params.put ("last_name", userInfo.getLastname ().toLowerCase());
                    params.put ("email_address", userInfo.getEmailAddress ());
                    params.put ("location", userInfo.getLocation ());
                    params.put ("contact_no", userInfo.getContactNo ());
                    params.put ("user_type", userInfo.getTypeOfUser ());
                    return params;
                }
            };

            RequestHandler.getInstance (this).addToRequestQueue (stringRequest);
        }
    }

    public void openDialog(){
        successDialog sDialog = new successDialog ();
        sDialog.show (getSupportFragmentManager (), "Success Dialog");
    }
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    public boolean isValidEmail(){
        boolean isValid = false;
        if(emailAddressEditText.getText().toString().contains("@gmail.com") || emailAddressEditText.getText().toString().contains("@yahoo.com"))
            isValid = true;
        else
            isValid = false;
        return isValid;
    }
}
