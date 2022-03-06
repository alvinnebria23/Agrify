package com.example.agrify.AddDemandPackage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.agrify.Constants;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;
import com.example.agrify.RequestHandler;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddDemandActivity extends AppCompatActivity {

    private ImageView addPhotoImageView, demandImageView1, demandImageView2, demandImageView3, demandImageView4;
    private Spinner agriculturalSectorSpinner, productSpinner, varietySpinner;
    private Bitmap bitmap1, bitmap2, bitmap3, bitmap4;
    private EditText durationEndEditText, priceEditText, demandEditText, descriptionEditText;
    private Button demandButton, updateButton, editDemandButton;
    private TextView demandTextView, priceTextView;
    private boolean imagesChanged = false;
    private String agriculturalSectorString = "", productString = "", productIDString = "",
            varietyIDString = "", varietyString = "", vendorIDString = "", demandIDString = "", dateTodayString = "",
            message = "", activityType = "", action = "";
    int pickedPhotosSuccessfully = 0, productSpinnerPosition = 0, varietySpinnerPosition = 0, receivedKilograms = 0, initialNeededKilograms = 0;
    List<String> productList;
    List<String> varietyList;
    List<Product> productInfoList = new ArrayList<>();
    List<Variety> varietyInfoList = new ArrayList<>();
    ArrayAdapter<String> productSpinnerAdapter;
    ArrayAdapter<String> varietySpinnerAdapter;
    ArrayAdapter<CharSequence> agriculturalSectorAdapter;
    int PICK_IMAGE_MULTIPLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_demand);

        activityType   = getIntent().getExtras().getString("activityType");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        addPhotoImageView           =   (ImageView) findViewById(R.id.addPhotoImageView);
        demandImageView1            =   (ImageView) findViewById(R.id.demandImage1);
        demandImageView2            =   (ImageView) findViewById(R.id.demandImage2);
        demandImageView3            =   (ImageView) findViewById(R.id.demandImage3);
        demandImageView4            =   (ImageView) findViewById(R.id.demandImage4);
        demandButton                =   (Button)    findViewById(R.id.demandButton);
        updateButton                =   (Button)    findViewById(R.id.updateButton);
        editDemandButton            =   (Button)    findViewById(R.id.editDemandButton);
        agriculturalSectorSpinner   =   (Spinner)   findViewById(R.id.agriculturalSectorSpinner);
        productSpinner              =   (Spinner)   findViewById(R.id.productSpinner);
        varietySpinner              =   (Spinner)   findViewById(R.id.varietySpinner);
        durationEndEditText         =   (EditText)  findViewById(R.id.durationEndEditText);
        priceEditText               =   (EditText)  findViewById(R.id.priceEditText);
        demandEditText              =   (EditText)  findViewById(R.id.demandEditText);
        descriptionEditText         =   (EditText)  findViewById(R.id.descriptionEditText);
        demandTextView              =   (TextView)  findViewById(R.id.demandTextView);
        priceTextView               =   (TextView)  findViewById(R.id.priceTextView);
        productList = new ArrayList<>();
        varietyList = new ArrayList<>();
        getVendorID(SharedPrefManager.getInstance(getApplicationContext()).getUserID());

        agriculturalSectorAdapter = ArrayAdapter.createFromResource(this,
                R.array.agriculturalsectorarray, R.layout.spinner_item);
        agriculturalSectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        agriculturalSectorSpinner.setAdapter(agriculturalSectorAdapter);

        productSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, productList);
        productSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(productSpinnerAdapter);

        varietySpinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, varietyList);
        varietySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        varietySpinner.setAdapter(varietySpinnerAdapter);
        setActivity();
        agriculturalSectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(adapterView.getSelectedItemPosition() == 0){
                    productList.clear();
                    varietyList.clear();
                    productList.add(" ");
                    varietyList.add(" ");
                    productSpinnerAdapter.notifyDataSetChanged();
                    varietySpinnerAdapter.notifyDataSetChanged();
                }else{
                    agriculturalSectorString = adapterView.getSelectedItem().toString().toLowerCase();
                    populateProductSpinner(agriculturalSectorString);
                    productSpinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItemPosition() == 0){
                    varietyList.clear();
                    varietyList.add(" ");
                    varietySpinnerAdapter.notifyDataSetChanged();
                }else{
                    productString = adapterView.getSelectedItem().toString().toLowerCase();
                    String changeDemandTextView = "Demand (by pcs)";
                    String changePriceTextView = "Price (per pc)";
                    if(productString.contains("eggs")) {
                        demandTextView.setText(changeDemandTextView);
                        priceTextView.setText(changePriceTextView);
                    }else{
                        changeDemandTextView = "Demand (by kilo)";
                        changePriceTextView = "Price (per kilo)";
                        demandTextView.setText(changeDemandTextView);
                        priceTextView.setText(changePriceTextView);
                    }
                    populateVarietySpinner(agriculturalSectorString, productString);
                    varietySpinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        varietySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItemPosition() == 0){

                }else{
                    varietyString = adapterView.getSelectedItem().toString().toLowerCase();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        addPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
        durationEndEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogfragment = new DatePickerDialogTheme();

                dialogfragment.show(getSupportFragmentManager(), "Theme");
            }
        });
        demandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidInputs()) {
                    saveDemandInfo();

                }else{
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEditText();
                if(imagesChanged){
                    uploadImages();
                    imagesChanged = false;
                }
                if(isValidInputs()){
                    updateDemandInfo();
                }
                triggerFireBase();
            }
        });
        editDemandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditText();
                setUpSpinner();
            }
        });
        getDateToday();
    }
    public void triggerFireBase(){
        String userID = SharedPrefManager.getInstance(getApplicationContext()).getUserID();
        final Map<String, Object> map = new HashMap<String ,Object>();
        DatabaseReference myDemandsReference = FirebaseDatabase.getInstance().getReference("myDemands");
        final DatabaseReference myUserIDReference =  myDemandsReference.child(userID);
        myUserIDReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                String lastChildNodeKey = "";
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    lastChildNodeKey = String.valueOf(ds.getKey());
                    counter++;
                    break;
                }
                if(counter > 0){
                    String tempKey = myUserIDReference.push().getKey();
                    if (tempKey != null)
                        map.put(tempKey, "");
                    //myUserIDReference.updateChildren(map);
                    myUserIDReference.child(lastChildNodeKey).setValue(tempKey);
                }else{
                    String tempKey = myUserIDReference.push().getKey();
                    if (tempKey != null)
                        map.put(tempKey, "");
                    myUserIDReference.updateChildren(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            if(activityType.equals("addDemand")) {
                this.finish(); // close this activity and return to preview activity (if there is any)
            }else if(activityType.equals("update")){
                this.finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                if(data.getData() != null){
                    showToast();
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        if(mArrayUri.size() == 4) {
                            for (int i = 0; i < mArrayUri.size(); i++) {
                                if (i == 0) {
                                    try {
                                        bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), mArrayUri.get(i));
                                        bitmap1 = getResizedBitmap(bitmap1,400);
                                        demandImageView1.setImageBitmap(bitmap1);
                                        pickedPhotosSuccessfully = 1;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if (i == 1) {
                                    try {
                                        bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), mArrayUri.get(i));
                                        bitmap2 = getResizedBitmap(bitmap2,400);
                                        demandImageView2.setImageBitmap(bitmap2);
                                        pickedPhotosSuccessfully = 1;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if (i == 2) {
                                    try {
                                        bitmap3 = MediaStore.Images.Media.getBitmap(getContentResolver(), mArrayUri.get(i));
                                        bitmap3 = getResizedBitmap(bitmap3,400);
                                        demandImageView3.setImageBitmap(bitmap3);
                                        pickedPhotosSuccessfully = 1;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if (i == 3) {
                                    try {
                                        bitmap4 = MediaStore.Images.Media.getBitmap(getContentResolver(), mArrayUri.get(i));
                                        bitmap4 = getResizedBitmap(bitmap4,400);
                                        demandImageView4.setImageBitmap(bitmap4);
                                        pickedPhotosSuccessfully = 1;
                                        if(activityType.equals("update")){
                                            imagesChanged = true;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        else{
                            showToast();
                        }
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void uploadImages() {
        final String image1 = getStringImage(bitmap1);
        final String image2 = getStringImage(bitmap2);
        final String image3 = getStringImage(bitmap3);
        final String image4 = getStringImage(bitmap4);

        class UploadImage extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;
            ImageRequestHandler rh = new ImageRequestHandler();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddDemandActivity.this,null,"Uploading . . .",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(activityType.equals("addDemand")) {
                    clearActivity();
                    Toast.makeText(getApplicationContext(), "DEMAND SUCCESSFULLY ADDED", Toast.LENGTH_LONG).show();
                }
                if(activityType.equals("update"))
                {
                    Toast.makeText(getApplicationContext(), "DEMAND IMAGES SUCCESSFULLY UPDATED", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> param = new HashMap<>();
                param.put("demand_id", demandIDString);
                param.put("image_1", image1);
                param.put("image_2", image2);
                param.put("image_3", image3);
                param.put("image_4", image4);
                param.put("action", action);
                String result = rh.sendPostRequest(Constants.URL_UPLOAD_DEMAND_IMAGES, param);
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
    public void populateProductSpinner(final String agriculturalSector){
        productList.clear();
        productInfoList.clear();
        productList.add(" ");
        StringRequest stringRequest = new StringRequest (
            Request.Method.POST,Constants.URL_POPULATE_PRODUCT_SPINNER,
            new Response.Listener<String> () {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray array = new JSONArray (response);

                        for (int i = 0; i < array.length (); i++) {
                            JSONObject product = array.getJSONObject (i);

                            productList.add(product.getString("product_name").toUpperCase());
                            productInfoList.add(new Product(product.getString("product_id"), product.getString("product_name").toUpperCase()));
                        }
                        if(activityType.equals("update")){
                            for(Product product : productInfoList){
                                if(product.getProductID().equals(productIDString)){
                                    productSpinnerPosition = productSpinnerAdapter.getPosition(product.getProductName().toUpperCase());
                                }
                            }
                            productSpinner.setSelection(productSpinnerPosition);
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
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<> ();
                params.put ("agricultural_sector", agriculturalSector);
                return params;
            }
        };
        RequestHandler.getInstance (this).addToRequestQueue (stringRequest);
    }
    public void populateVarietySpinner(final String agriculturalSectorString, String productString){
        varietyList.clear();
        varietyInfoList.clear();
        varietyList.add(" ");
        for (Product product : productInfoList) {
            if (product.getProductName().toLowerCase().equals(productString)) {
                productIDString = product.getProductID();
            }
        }
        StringRequest stringRequest = new StringRequest (
            Request.Method.POST,Constants.URL_POPULATE_VARIETY_SPINNER,
            new Response.Listener<String> () {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray array = new JSONArray (response);

                        for (int i = 0; i < array.length (); i++) {
                            JSONObject variety = array.getJSONObject (i);

                            varietyList.add(variety.getString("variety_name").toUpperCase());
                            varietyInfoList.add(new Variety(variety.getString("variety_id"), variety.getString("variety_name").toUpperCase()));
                        }
                        if(activityType.equals("update")){
                            for(Variety variety : varietyInfoList){
                                if(variety.getVarietyID().equals(varietyIDString)){
                                    varietySpinnerPosition = varietySpinnerAdapter.getPosition(variety.getVarietyName().toUpperCase());
                                }
                            }
                            varietySpinner.setSelection(varietySpinnerPosition);
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
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<> ();
                params.put ("agricultural_sector", agriculturalSectorString);
                params.put ("product_id", productIDString);
                return params;
            }
        };
        RequestHandler.getInstance (this).addToRequestQueue (stringRequest);
    }
    public static class DatePickerDialogTheme extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_DARK,this,year,month,day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            EditText durationEndEditText = (EditText) getActivity().findViewById(R.id.durationEndEditText);
            String dateString = year + "-" + (month+1) + "-" + day;
            durationEndEditText.setText(dateString);
        }
    }
    public void saveDemandInfo(){
        for(Variety variety : varietyInfoList){
            if(variety.getVarietyName().toLowerCase().equals(varietyString)){
                varietyIDString = variety.getVarietyID();
            }
        }
        StringRequest stringRequest = new StringRequest (
            Request.Method.POST,Constants.URL_SAVE_DEMAND_INFO,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject (response);

                            if(!jsonObject.getBoolean("error")){
                                getLatestDemandID();
                            }else{
                                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
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
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<> ();
                params.put ("agricultural_sector", agriculturalSectorString);
                params.put ("product_id", productIDString);
                params.put ("variety_id", varietyIDString);
                params.put ("vendor_id", vendorIDString);
                params.put ("price", priceEditText.getText().toString());
                params.put ("needed_kilograms", demandEditText.getText().toString());
                params.put ("duration_end", durationEndEditText.getText().toString());
                params.put ("description", descriptionEditText.getText().toString());
                return params;
            }
        };
        RequestHandler.getInstance (this).addToRequestQueue (stringRequest);
    }
    public void getLatestDemandID(){
        StringRequest stringRequest = new StringRequest (
                Request.Method.POST,Constants.URL_GET_LATEST_DEMAND_ID,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray (response);

                            for (int i = 0; i < array.length (); i++) {
                                JSONObject demand = array.getJSONObject (i);

                                demandIDString = demand.getString("demand_id");
                                if(demandIDString != null){
                                    uploadImages();
                                }
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
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<> ();
                params.put ("vendor_id", vendorIDString);
                return params;
            }
        };
        RequestHandler.getInstance (this).addToRequestQueue (stringRequest);
    }
    public void getVendorID(final String userIDString){
        StringRequest stringRequest = new StringRequest (
                Request.Method.POST,Constants.URL_RETRIEVE_VENDOR_ID,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray (response);

                            for (int i = 0; i < array.length (); i++) {
                                JSONObject vendor = array.getJSONObject (i);

                                vendorIDString = vendor.getString("vendor_id");
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
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<> ();
                params.put ("user_id", userIDString);
                return params;
            }
        };
        RequestHandler.getInstance (this).addToRequestQueue (stringRequest);
    }
    public void clearActivity(){
        demandImageView1.setImageResource(0);
        demandImageView2.setImageResource(0);
        demandImageView3.setImageResource(0);
        demandImageView4.setImageResource(0);
        productList.clear();
        productInfoList.clear();
        varietyList.clear();
        varietyInfoList.clear();
        productList.add(" ");
        varietyList.add(" ");
        agriculturalSectorSpinner.setSelection(0);
        productSpinner.setSelection(0);
        varietySpinner.setSelection(0);
        descriptionEditText.getText().clear();
        priceEditText.getText().clear();
        demandEditText.getText().clear();
        durationEndEditText.getText().clear();
        pickedPhotosSuccessfully = 0;
    }
    public void getDateToday(){
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        dateTodayString = df.format(c);
    }
    public boolean isValidInputs(){
        boolean valid = true;
        try {
            SimpleDateFormat df     = new SimpleDateFormat("yyyy-MM-dd");
            Date dateToday          = df.parse(dateTodayString);
            Date durationEndDate    = df.parse(durationEndEditText.getText().toString());

            if(dateToday.after(durationEndDate)){
                valid = false;
                message = "Please set the duration end date correctly.";
            }else if(dateToday.compareTo(durationEndDate) == 0){
                valid = false;
                message = "Duration end date must not the date today.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(durationEndEditText.getText().toString().trim().length() == 0 || TextUtils.isEmpty(durationEndEditText.getText().toString())){
            valid = false;
            message = "Please set the duration end date.";
        }
        if(varietySpinner.getSelectedItemPosition() == 0){
            valid = false;
            message = "Please select a specific variety of the product.";
        }
        if(priceEditText.getText().toString().trim().length() == 0){
            valid = false;
            message = "Please input the price per kilo.";
        }
        if(demandEditText.getText().toString().trim().length() == 0){
            valid = false;
            message = "Please input the demand by kilo.";
        }
        if(descriptionEditText.getText().toString().trim().length() == 0){
            valid = false;
            message = "Please input a description.";
        }
        if(productSpinner.getSelectedItemPosition() == 0){
            valid = false;
            message = "Please select a specific product.";
        }
        if(agriculturalSectorSpinner.getSelectedItemPosition() == 0){
            valid = false;
            message = "Please select a specific agricultural sector.";
        }
        if(!activityType.equals("update")) {
            if (pickedPhotosSuccessfully == 0) {
                valid = false;
                message = "Strictly needed 4 photos.";
            }
        }
        return valid;
    }
    public void retrieveDemand(final String demandIDString){
        productList.clear();
        productInfoList.clear();
        productList.add(" ");
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_DEMAND_INFO,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray (response);

                            for(int i = 0; i < array.length(); i++){
                                JSONObject demand = array.getJSONObject(i);

                                Glide.with (getApplicationContext())
                                        .load (Constants.PATH_DEMAND_IMAGES_FOLDER+demand.getString("image_1"))
                                        .into (demandImageView1);
                                Glide.with (getApplicationContext())
                                        .load (Constants.PATH_DEMAND_IMAGES_FOLDER+demand.getString("image_2"))
                                        .into (demandImageView2);
                                Glide.with (getApplicationContext())
                                        .load (Constants.PATH_DEMAND_IMAGES_FOLDER+demand.getString("image_3"))
                                        .into (demandImageView3);
                                Glide.with (getApplicationContext())
                                        .load (Constants.PATH_DEMAND_IMAGES_FOLDER+demand.getString("image_4"))
                                        .into (demandImageView4);

                                agriculturalSectorSpinner.setSelection(agriculturalSectorAdapter.getPosition(demand.getString("agricultural_sector").toUpperCase()));
                                receivedKilograms = demand.getInt("received_kilograms");
                                initialNeededKilograms = demand.getInt("needed_kilograms");
                                productIDString = demand.getString("product_id");
                                varietyIDString = demand.getString("variety_id");
                                descriptionEditText.setText(demand.getString("description"));
                                priceEditText.setText(String.valueOf(demand.getInt("price")));
                                demandEditText.setText(String.valueOf(demand.getInt("needed_kilograms")));
                                durationEndEditText.setText(demand.getString("duration_end"));
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
                params.put ("demand_id", demandIDString);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void setActivity(){
        if(activityType.equals("update")){
            setTitle("Update");
            demandButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.GONE);
            agriculturalSectorSpinner.setEnabled(false);
            addPhotoImageView.setEnabled(false);
            productSpinner.setEnabled(false);
            descriptionEditText.setEnabled(false);
            priceEditText.setEnabled(false);
            demandEditText.setEnabled(false);
            varietySpinner.setEnabled(false);
            durationEndEditText.setEnabled(false);
            demandIDString = getIntent().getExtras().getString("demandID");
            action = "update";
            retrieveDemand(demandIDString);
        }else{
            setTitle("Add demand");
            action = "addDemand";
            updateButton.setVisibility(View.GONE);
            editDemandButton.setVisibility(View.GONE);
        }
    }
    public void updateDemandInfo(){
        for(Variety variety : varietyInfoList){
            if(variety.getVarietyName().toLowerCase().equals(varietyString)){
                varietyIDString = variety.getVarietyID();
            }
        }
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_UPDATE_DEMAND_INFO,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("message").equals("success")){
                                Toast.makeText(getApplicationContext(), "DEMAND SUCCESSFULLY UPDATED", Toast.LENGTH_SHORT).show();
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
                params.put("demand_id", demandIDString);
                params.put ("agricultural_sector", agriculturalSectorString);
                params.put ("product_id", productIDString);
                params.put ("variety_id", varietyIDString);
                params.put ("price", priceEditText.getText().toString());
                params.put ("needed_kilograms", demandEditText.getText().toString());
                params.put ("duration_end", durationEndEditText.getText().toString());
                params.put ("description", descriptionEditText.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void enableEditText(){
        editDemandButton.setVisibility(View.GONE);
        updateButton.setVisibility(View.VISIBLE);
        agriculturalSectorSpinner.setEnabled(true);
        productSpinner.setEnabled(true);
        descriptionEditText.setEnabled(true);
        priceEditText.setEnabled(true);
        demandEditText.setEnabled(true);
        varietySpinner.setEnabled(true);
        durationEndEditText.setEnabled(true);
        addPhotoImageView.setEnabled(true);
        agriculturalSectorSpinner.setBackgroundResource(R.drawable.greenrectangle);
        productSpinner.setBackgroundResource(R.drawable.greenrectangle);
        descriptionEditText.setBackgroundResource(R.drawable.greenrectangle);
        priceEditText.setBackgroundResource(R.drawable.greenrectangle);
        demandEditText.setBackgroundResource(R.drawable.greenrectangle);
        varietySpinner.setBackgroundResource(R.drawable.greenrectangle);
        durationEndEditText.setBackgroundResource(R.drawable.greenrectangle);
    }
    public void disableEditText(){
        editDemandButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.GONE);
        agriculturalSectorSpinner.setEnabled(false);
        productSpinner.setEnabled(false);
        descriptionEditText.setEnabled(false);
        priceEditText.setEnabled(false);
        demandEditText.setEnabled(false);
        varietySpinner.setEnabled(false);
        durationEndEditText.setEnabled(false);
        addPhotoImageView.setEnabled(false);
        agriculturalSectorSpinner.setBackgroundResource(R.drawable.rounded_rectangle);
        productSpinner.setBackgroundResource(R.drawable.rounded_rectangle);
        descriptionEditText.setBackgroundResource(R.drawable.rounded_rectangle);
        priceEditText.setBackgroundResource(R.drawable.rounded_rectangle);
        demandEditText.setBackgroundResource(R.drawable.rounded_rectangle);
        varietySpinner.setBackgroundResource(R.drawable.rounded_rectangle);
        durationEndEditText.setBackgroundResource(R.drawable.rounded_rectangle);
    }
    public void setUpSpinner(){
        int  index  = productList.indexOf(" ");
        Set<String> uniqueList = new HashSet<String>(productList);
        productList.clear();
        productList.addAll(uniqueList);
        productList.remove(productList.indexOf(" "));
        productList.add(0, " ");
        productSpinnerAdapter.notifyDataSetChanged();
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    public void showToast(){
        Toast toast = Toast.makeText(getApplicationContext(),"Strictly needed 4 photos to upload.", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
