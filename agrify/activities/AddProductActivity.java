package com.example.agrify.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;
import com.example.agrify.models.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {
    private AwesomeValidation awesomeValidation;
    Spinner productCategory;
    List<Product> mProduct  = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<String>();
    List<Bitmap> bitmaps = new ArrayList<>();
    EditText productPrice,productName,productDescription,productQuantity;
    Button addProductButton;
    RequestQueue requestQueue;
    ImageView imageUpload;
    ImageView[] imageViews = new ImageView[4];
    final int CODE_GALLERY_REQUEST = 1;
    String categoryId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setTitle("Add Product");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        requestQueue = Volley.newRequestQueue(this);
        ref();
        new RetriveData(AddProductActivity.this).execute();
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityCompat.requestPermissions(AddProductActivity.this,new String[]
                        {Manifest.permission.READ_EXTERNAL_STORAGE},CODE_GALLERY_REQUEST);

            }
        });
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate()) {
                    if(bitmaps.size() == 4){
                        addProductButton.setEnabled(false);
                        new LoadDataProcess(AddProductActivity.this).execute();

                    }else{
                        Toast.makeText(getApplicationContext(),"Please Select 4 images",Toast.LENGTH_LONG).show();
                    }


                }
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
    public void ref(){
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        productCategory = (Spinner) findViewById(R.id.add_product_category_id);
        productPrice = (EditText) findViewById(R.id.add_price_id);
        productName = (EditText) findViewById(R.id.add_product_name_id);
        productDescription = (EditText) findViewById(R.id.add_description_id);
        productQuantity = (EditText) findViewById(R.id.add_quantity_id);
        imageUpload = (ImageView) findViewById(R.id.image_upload_id);
        addProductButton = (Button) findViewById(R.id.add_product_button_id);
        imageViews[0]  = (ImageView) findViewById(R.id.image_1_id);
        imageViews[1]  = (ImageView) findViewById(R.id.image_2_id);
        imageViews[2]  = (ImageView) findViewById(R.id.image_3_id);
        imageViews[3]  = (ImageView) findViewById(R.id.image_4_id);
        awesomeValidation.addValidation(this, R.id.add_price_id,
                "^(\\d{0,9}\\.\\d{1,4}|\\d{1,9})$", R.string.errorProductPrice);
        awesomeValidation.addValidation(this, R.id.add_quantity_id,
                "^[0-9]+$", R.string.errorProductQuantity);
        awesomeValidation.addValidation(this, R.id.add_product_name_id,
                "^[0-9a-zA-Z ]+$", R.string.errorProductName);
        awesomeValidation.addValidation(this, R.id.add_description_id,
                "^[0-9a-zA-Z ]+$", R.string.errorProductDescription);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CODE_GALLERY_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),CODE_GALLERY_REQUEST);
            }else{
                Toast.makeText(getApplicationContext(),"You don't have permission to access Gallery",Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==CODE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null){

            ClipData clipData = data.getClipData();
            if(clipData!=null){
                if(clipData.getItemCount()!=4){
                    Toast.makeText(getApplicationContext(),"Please select only 4 images",Toast.LENGTH_LONG).show();
                }else{
                    bitmaps.clear();
                    for(int i = 0;i<clipData.getItemCount();i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            bitmaps.add(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    for(int x= 0;x<bitmaps.size();x++){
                        Bitmap b = bitmaps.get(x);
                        imageViews[x].setImageBitmap(b);
                    }

                }

            }else{
                Toast.makeText(getApplicationContext(),"Please select only 4 images",Toast.LENGTH_LONG).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class RetriveData extends AsyncTask {
        private ProgressDialog dialog;
        private AppCompatActivity activity;
        private Context context;
        //private List<Message> messages;
        public RetriveData(AppCompatActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Progress start");
            this.dialog.show();

        }

        @Override
        protected Object doInBackground(Object[] objects) {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_IP +"action.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    System.out.println("helloResponse "+response);
                    Log.i("helloResponse", ">>" + response);
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject dataobj = array.getJSONObject(i);
                            String id = dataobj.getString("category_id");
                            String category = dataobj.getString("category_name");
                            mProduct.add(
                                    new Product(id+"",category+"")
                            );
                        }
                        for (int i = 0; i < mProduct.size(); i++){
                            names.add(mProduct.get(i).getProductCategory().toString());
                        }
                        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.spinner_item_product, names);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                        productCategory.setAdapter(spinnerArrayAdapter);
                        productCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                String selectedItemText = (String) adapterView.getItemAtPosition(position);
                                for(int x =0;x<mProduct.size();x++) {
                                    if (selectedItemText.equalsIgnoreCase(mProduct.get(x).getProductCategory().toString())) {
                                        categoryId = mProduct.get(x).getProductId().toString();
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Creating Map String Params.
                    Map<String, String> params = new HashMap<String, String>();

                    // Adding All values to Params.
                    params.put("action", "fetch_product_data_categories");
                    return params;
                }
            };
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
    private String imageToString(Bitmap imgBitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }
    public class LoadDataProcess extends AsyncTask {
        private ProgressDialog dialog;
        private AppCompatActivity activity;
        private Context context;
        //private List<Message> messages;
        public LoadDataProcess(AppCompatActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Saving Product");
            this.dialog.show();

        }

        @Override
        protected Object doInBackground(Object[] objects) {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_IP +"action.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        addProductButton.setEnabled(true);
                    }
                    Log.i("saveResponse", ">>" + response);
                    Toast.makeText(AddProductActivity.this,"Product Successfully Added",Toast.LENGTH_SHORT).show();
                    imageViews[0].setImageBitmap(null);
                    imageViews[1].setImageBitmap(null);
                    imageViews[2].setImageBitmap(null);
                    imageViews[3].setImageBitmap(null);
                    productName.setText(null);
                    productPrice.setText(null);
                    productDescription.setText(null);
                    productQuantity.setText(null);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Creating Map String Params.
                    Map<String, String> params = new HashMap<String, String>();

                    // Adding All values to Params.

                    params.put("image1", imageToString(getResizedBitmap(bitmaps.get(0),400)));
                    params.put("image2", imageToString(getResizedBitmap(bitmaps.get(1),400)));
                    params.put("image3", imageToString(getResizedBitmap(bitmaps.get(2),400)));
                    params.put("image4", imageToString(getResizedBitmap(bitmaps.get(3),400)));
                    params.put("category_id", categoryId);
                    params.put("provider_id", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                    params.put("product_description", productDescription.getText().toString());
                    params.put("product_price", productPrice.getText().toString());
                    params.put("product_stocks", productQuantity.getText().toString());
                    params.put("product_name", productName.getText().toString());
                    params.put("action", "insert_product");
                    return params;
                }
            };
            requestQueue.add(stringRequest);

            return null;
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

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);


        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }
    }



}
