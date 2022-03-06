package com.example.agrify.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.agrify.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateProductActivity extends AppCompatActivity {
    private AwesomeValidation awesomeValidation;
    EditText productNameTextView,productPriceTextView,productCategoryTextView,
            productBrandTextView,productStocksTextView,productDescriptionTextView;
    ImageView imageUpload;
    String productStockId;
    Button updateButton;
    ImageView[] imageViews = new ImageView[4];
    final int CODE_GALLERY_REQUEST = 1;
    List<Bitmap> bitmaps = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        setTitle("Update Product");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent intent = getIntent();
        String productName = intent.getExtras().getString("productName");
        String productPrice = intent.getExtras().getString("productPrice");
        String productCategory = intent.getExtras().getString("productCategory");
        String productDescription = intent.getExtras().getString("productDescription");
        String productStocks = intent.getExtras().getString("productStocks");
        String image1 = intent.getExtras().getString("image1");
        String image2 = intent.getExtras().getString("image2");
        String image3 = intent.getExtras().getString("image3");
        String image4 = intent.getExtras().getString("image4");
        productStockId = intent.getExtras().getString("productStockId");
        ref();
        setData(productName,productPrice,productCategory,productDescription,image1,productStocks,image2,image3,image4);
        imageUpload = (ImageView) findViewById(R.id.update_image_upload_id);
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(UpdateProductActivity.this,new String[]
                        {Manifest.permission.READ_EXTERNAL_STORAGE},CODE_GALLERY_REQUEST);
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate()) {

                    saveData();


                    Toast.makeText(getApplicationContext(),"Data updated",Toast.LENGTH_LONG).show();



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
        productNameTextView = (EditText) findViewById(R.id.update_product_name_id);
        productCategoryTextView = (EditText) findViewById(R.id.update_category_id);
        productPriceTextView = (EditText) findViewById(R.id.update_price_id);
        productStocksTextView = (EditText) findViewById(R.id.update_quantity_id);
        productDescriptionTextView = (EditText) findViewById(R.id.update_description_id);
        imageViews[0] = (ImageView) findViewById(R.id.update_image_1_id);
        imageViews[1] = (ImageView) findViewById(R.id.update_image_2_id);
        imageViews[2] = (ImageView) findViewById(R.id.update_image_3_id);
        imageViews[3] = (ImageView) findViewById(R.id.update_image_4_id);
        updateButton = (Button) findViewById(R.id.update_product_button_id);
        awesomeValidation.addValidation(this, R.id.update_price_id,
                "^(\\d{0,9}\\.\\d{1,4}|\\d{1,9})$", R.string.errorProductPrice);
        awesomeValidation.addValidation(this, R.id.update_quantity_id,
                "^[0-9]+$", R.string.errorProductQuantity);
        awesomeValidation.addValidation(this, R.id.update_description_id,
                "^[0-9a-zA-Z ]+$", R.string.errorProductDescription);
        awesomeValidation.addValidation(this, R.id.update_product_name_id,
                "^[0-9a-zA-Z ]+$", R.string.errorProductName);
    }
    public void setData(String productName,String productPrice,String productCategory,String productDescription,String image1,String productStocks,String image2,String image3,String image4){
        productNameTextView.setText(productName);
        productPriceTextView.setText(productPrice);
        productCategoryTextView.setText(productCategory);
        productDescriptionTextView.setText(productDescription);
        productStocksTextView.setText(productStocks);
        Picasso.get()
                .load(image1)
                .resize(300, 300)
                .centerCrop()
                .into(imageViews[0]);
        Picasso.get()
                .load(image2)
                .resize(300, 300)
                .centerCrop()
                .into( imageViews[1]);
        Picasso.get()
                .load(image3)
                .resize(300, 300)
                .centerCrop()
                .into( imageViews[2]);
        Picasso.get()
                .load(image4)
                .resize(300, 300)
                .centerCrop()
                .into( imageViews[3]);


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
            System.out.println("Clip Data:"+clipData);
            if(clipData!=null){
                if(clipData.getItemCount()!=4){
                    Toast.makeText(getApplicationContext(),"Please select only 4 images",Toast.LENGTH_LONG).show();
                }else{
                    bitmaps.clear();
                    for(int i = 0;i<clipData.getItemCount();i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        System.out.println("Image Uri"+(i+1)+" :"+imageUri);
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);

                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            System.out.println("bitmap "+(i+1)+" :"+bitmap);
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
    public void saveData(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_IP +"action.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("saveResponse", ">>" + response);
                System.out.println("Response: "+ response);

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
                if(bitmaps.size()==0){
                    params.put("product_stock_id", productStockId);
                    params.put("product_name", productNameTextView.getText().toString());
                    params.put("product_description", productDescriptionTextView.getText().toString());
                    params.put("product_price", productPriceTextView.getText().toString());
                    params.put("product_stocks", productStocksTextView.getText().toString());
                    params.put("action", "update_product");
                }else{
                    params.put("image1", imageToString(bitmaps.get(0)));
                    params.put("image2", imageToString(bitmaps.get(1)));
                    params.put("image3", imageToString(bitmaps.get(2)));
                    params.put("image4", imageToString(bitmaps.get(3)));
                    params.put("product_stock_id", productStockId);
                    params.put("product_name", productNameTextView.getText().toString());
                    params.put("product_description", productDescriptionTextView.getText().toString());
                    params.put("product_price", productPriceTextView.getText().toString());
                    params.put("product_stocks", productStocksTextView.getText().toString());
                    params.put("action", "update_product");
                }
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    private String imageToString(Bitmap imgBitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }

}
