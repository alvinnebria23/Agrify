package com.example.agrify.LoginPackage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.agrify.AddDemandPackage.AddDemandActivity;
import com.example.agrify.ChatPackage.InboxActivity;
import com.example.agrify.Constants;
import com.example.agrify.DemandPackage.CropsFragment;
import com.example.agrify.DemandPackage.FisheriesFragment;
import com.example.agrify.DemandPackage.LivestocksFragment;
import com.example.agrify.DemandPackage.PoultryFragment;
import com.example.agrify.DemandPackage.ViewPagerAdapter;
import com.example.agrify.MyDemandPackage.MyDemandActivity;
import com.example.agrify.ProfilePackage.ProfileActivity;
import com.example.agrify.R;
import com.example.agrify.RequestHandler;
import com.example.agrify.activities.AddProductActivity;
import com.example.agrify.activities.MyProduct;
import com.example.agrify.activities.ProductFavoratesActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerLayout drawer;
    private ImageView profileImageView;
    private Button viewProfileButton;
    private String token = "", userIDString = "";
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    TextView nameTextView, locationTextView, emailAddressTextView;
    private String businessPermit;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_home);

        setTitle("Demand");
        userIDString = SharedPrefManager.getInstance(getApplicationContext()).getUserID();
        if (!SharedPrefManager.getInstance (this).isLoggedIn ()) {
            finish ();
            startActivity (new Intent (this, MainActivity.class));
        }
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userID");
        DatabaseReference userIDRef = rootRef.child(userIDString);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(userIDString, "");
                    rootRef.updateChildren(map);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        };
        userIDRef.addListenerForSingleValueEvent(eventListener);
        tabLayout       = (TabLayout)   findViewById (R.id.tabLayout);
        viewPager       = (ViewPager)   findViewById (R.id.viewPager);
        getToken();
        viewPager.setOffscreenPageLimit(4);
        ViewPagerAdapter viewPagerAdapter   = new ViewPagerAdapter (getSupportFragmentManager ());
        viewPagerAdapter.AddFragment (new CropsFragment (),      "CROPS");
        viewPagerAdapter.AddFragment (new LivestocksFragment (), "LIVESTOCKS");
        viewPagerAdapter.AddFragment (new PoultryFragment (),    "POULTRIES");
        viewPagerAdapter.AddFragment (new FisheriesFragment (),  "FISHERIES");
        viewPager.setAdapter (viewPagerAdapter);
        tabLayout.setupWithViewPager (viewPager);
        String userType = SharedPrefManager.getInstance (this).getUserType ();
        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);

        navigationView = (NavigationView) findViewById (R.id.nav_view);
        navigationView.setNavigationItemSelectedListener (this);

        drawer = findViewById (R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle (this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateNavigationViewHeader();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        if (userType.equals ("farmer")) {
            navigationView.getMenu ().clear ();
            navigationView.inflateMenu (R.menu.farmer_drawer_menu);
        } else if (userType.equals ("vendor")) {
            navigationView.getMenu ().clear ();
            navigationView.inflateMenu (R.menu.vendor_drawer_menu);
        }
        drawer.addDrawerListener (toggle);
        toggle.syncState ();
        updateDemand();
        setUpFireBase();
    }
    public void updateDemand(){
        StringRequest stringRequest = new StringRequest (
                Request.Method.POST,
                Constants.URL_UPDATE_DEMAND,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject (response);

                            if(jsonObject.getString("message").equals("success")){

                            }else{
                                Toast.makeText(getApplicationContext(), "Error updating demands.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }

                    }
                },
                new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText (
                                getApplicationContext (),
                                error.getMessage (),
                                Toast.LENGTH_LONG
                        ).show ();
                    }
                }
        );
        RequestHandler.getInstance (this).addToRequestQueue (stringRequest);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId ()) {
            case R.id.nav_create_demand:
                getPermitNumber();
                break;
            case R.id.nav_my_demand:
                intent = new Intent(this, MyDemandActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                SharedPrefManager.getInstance (this).logout ();
                startActivity (new Intent (this, MainActivity.class));
                break;
            case R.id.nav_needs:
                intent = new Intent(this, com.example.agrify.activities.MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.nav_post_product:
                intent = new Intent(this, AddProductActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.nav_my_product:
                intent = new Intent(this, MyProduct.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.nav_favorites:
                intent = new Intent(this, ProductFavoratesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.nav_inbox:
                intent = new Intent(this, InboxActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer (GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen (GravityCompat.START)) {
            drawer.closeDrawer (GravityCompat.START);
        } else {
            super.onBackPressed ();
        }

    }
    public void openDialog(){
        final Activity activity = this;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(Html.fromHtml("<font color='#696969'>PLEASE VERIFY THAT YOU ARE A LEGIT VENDOR</font>"));
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                Html.fromHtml("<font color='#00AA4E'>VERIFY</font>"),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        InputBusinessPermit inputBusinessPermit = new InputBusinessPermit();
                        inputBusinessPermit.show(getSupportFragmentManager(), "inputBusinessPermit");
                        inputBusinessPermit.setCancelable(false);
                        inputBusinessPermit.setDialogResult(new InputBusinessPermit.OnMyDialogResult() {
                            @Override
                            public void finish() {
                                IntentIntegrator integrator = new IntentIntegrator(activity);
                                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                                integrator.setPrompt("Scan");
                                integrator.setCameraId(0);
                                integrator.setBeepEnabled(false);
                                integrator.setBarcodeImageEnabled(false);
                                integrator.initiateScan();
                            }
                        });
                    }
                });

        builder1.setNegativeButton(
                Html.fromHtml("<font color='#00AA4E'>CANCEL</font>"),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(getApplicationContext(), "You cancelled scanning", Toast.LENGTH_LONG).show();
            }else{
                compareQRCode(result.getContents().toString());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void getPermitNumber(){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_STATUS,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            businessPermit = jsonObject.getString("permit_number");
                            if(businessPermit == null){
                                openDialog();
                            }else if(SharedPrefManager.getInstance(getApplicationContext()).getStatus().equals("UV")){
                                openDialog();
                            }else{
                                intent = new Intent(HomeActivity.this, AddDemandActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("activityType", "addDemand");
                                startActivity(intent);
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
                params.put("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void saveStatus(){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_SAVE_STATUS,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(getApplicationContext(), "YOU CAN NOW ADD DEMAND", Toast.LENGTH_LONG).show();
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
                params.put("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void openSuccessScanDialog(){
        SuccessScanDialog successScanDialog = new SuccessScanDialog ();
        successScanDialog.show (getSupportFragmentManager (), "Success Scan Dialog");
    }
    public void editSharedPreference(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences ("mysharedpref12", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();

        editor.putString("status", "V");
        editor.apply();
    }
    public void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                updateToken();
            }
        });
    }
    public void updateToken(){
        StringRequest stringRequest = new StringRequest (Request.Method.POST,
                Constants.URL_UPDATE_TOKEN,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject (response);
                            sendNotification();
                        } catch (JSONException e) {
                            e.printStackTrace ();
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
                params.put ("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                params.put ("token", token);
                return params;
            }
        };

        RequestHandler.getInstance (this).addToRequestQueue (stringRequest);
    }
    public void sendNotification(){
        StringRequest stringRequest = new StringRequest (Request.Method.POST,
                Constants.URL_SEND_NOTIFICATION,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject (response);

                            Toast.makeText(HomeActivity.this, response, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace ();
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
                params.put ("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                return params;
            }
        };

        RequestHandler.getInstance (this).addToRequestQueue (stringRequest);
    }
    public void setUpFireBase(){
        if(SharedPrefManager.getInstance(getApplicationContext()).getUserType().equals("vendor")) {
            final DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference("myDemands");
            final DatabaseReference myNotificationReference = notificationsReference.child(userIDString);
            myNotificationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(userIDString, "");
                        notificationsReference.updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    public void updateNavigationViewHeader(){
        View header = navigationView.getHeaderView (0);
        nameTextView = header.findViewById (R.id.nameTextView);
        locationTextView = header.findViewById (R.id.locationTextView);
        emailAddressTextView = header.findViewById (R.id.emailAddressTextView);
        profileImageView = header.findViewById(R.id.profileImageView);
        viewProfileButton = header.findViewById(R.id.viewProfileButton);
        getUserProfileInfo();
        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userID", String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUserID()));
                intent.putExtra("profileType", "myProfile");
                startActivity(intent);
            }
        });
    }
    public void getUserProfileInfo(){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_USER_PROFILE,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray array = new JSONArray (response);

                            JSONObject userInfo = array.getJSONObject(0);

                            Glide.with (getApplicationContext())
                                    .load (Constants.PATH_PROFILE_IMAGES_FOLDER+userInfo.getString("profile_pic"))
                                    .into (profileImageView);
                            String fullName = setName(userInfo.getString("fullname").toLowerCase());
                            nameTextView.setText (fullName);
                            locationTextView.setText (userInfo.getString("location"));
                            emailAddressTextView.setText (userInfo.getString("email_address"));
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
                params.put ("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public String setName(String name){
        String nameArray[] = name.split(" ");
        String firstLetterFirstName   = nameArray[0].charAt(0) + "";
        String firstLetterLastName    = nameArray[1].charAt(0) + "";
        String fullName = firstLetterFirstName.toUpperCase() + nameArray[0].substring(1, nameArray[0].length()) + " " + firstLetterLastName.toUpperCase() + nameArray[1].substring(1, nameArray[1].length());

        return fullName;
    }
    public void compareQRCode(final String qrCodeString){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_QRCODE,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray (response);

                            JSONObject object = array.getJSONObject(0);

                            if(object.getString("result").equals("success")){
                                editSharedPreference();
                                saveStatus();
                                Toast.makeText(getApplicationContext(), "YOU CAN NOW ADD DEMANDS.", Toast.LENGTH_LONG).show();
                                intent = new Intent(HomeActivity.this, AddDemandActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("activityType", "addDemand");
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "SCANNED QR CODE DOESN'T MATCH", Toast.LENGTH_LONG).show();
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
                params.put ("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                params.put("qrcode_string", qrCodeString);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
}
