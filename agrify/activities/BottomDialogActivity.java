package com.example.agrify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.agrify.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Map;

public class BottomDialogActivity extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_modal_layout,container,false);
        final String userId=getArguments().getString("userId");
        final String userName=getArguments().getString("userName");
        final String userLocation=getArguments().getString("userLocation");
        final String userNumber=getArguments().getString("userNumber");
        final String productName=getArguments().getString("productName");
        final String productPrice=getArguments().getString("productPrice");
        final String productCategory=getArguments().getString("productCategory");
        final String productStocks=getArguments().getString("productStocks");
        final String productDescription=getArguments().getString("productDescription");
        final String image1=getArguments().getString("image1");
        final String image2=getArguments().getString("image2");
        final String image3=getArguments().getString("image3");
        final String image4=getArguments().getString("image4");
        final String productStockId=getArguments().getString("productStockId");
        final String productRating=getArguments().getString("productRating");
        Button markSoldButton = (Button) v.findViewById(R.id.bottom_mark_sold_id);
        Button updateProductButton = (Button) v.findViewById(R.id.bottom_update_product_id);
        Button deleteProductButton = (Button) v.findViewById(R.id.button_delete_product_id);
        Button viewProductButton = (Button) v.findViewById(R.id.bottom_view_product_id);
        Button activeButton = (Button) v.findViewById(R.id.bottm_active_product);
        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProductStatus(productStockId,"active");
                Toast.makeText(getActivity(),"Active Successfully",Toast.LENGTH_SHORT).show();
                BottomDialogActivity.this.dismiss();
            }
        });
        markSoldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProductStatus(productStockId,"sold");
                Toast.makeText(getActivity(),"Mark as Sold",Toast.LENGTH_SHORT).show();
                BottomDialogActivity.this.dismiss();
            }
        });
        updateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),UpdateProductActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("userId",userId);
                i.putExtra("userName",userName);
                i.putExtra("userLocation",userLocation);
                i.putExtra("userNumber",userNumber);
                i.putExtra("productName",productName);
                i.putExtra("productPrice",productPrice);
                i.putExtra("productCategory",productCategory);
                i.putExtra("productStocks",productStocks);
                i.putExtra("productDescription",productDescription);
                i.putExtra("image1",image1);
                i.putExtra("image2",image2);
                i.putExtra("image3",image3);
                i.putExtra("image4",image4);
                i.putExtra("productStockId",productStockId);
                i.putExtra("productRating",productRating);
                getActivity().startActivity(i);
            }
        });
        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProductStatus(productStockId,"delete");
                Toast.makeText(getActivity(),"delete successfully",Toast.LENGTH_SHORT).show();
                BottomDialogActivity.this.dismiss();
            }
        });


        viewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ProductDescription.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("userId",userId);
                i.putExtra("userName",userName);
                i.putExtra("userLocation",userLocation);
                i.putExtra("userNumber",userNumber);
                i.putExtra("productName",productName);
                i.putExtra("productPrice",productPrice);
                i.putExtra("productCategory",productCategory);
                i.putExtra("productStocks",productStocks);
                i.putExtra("productDescription",productDescription);
                i.putExtra("image1",image1);
                i.putExtra("image2",image2);
                i.putExtra("image3",image3);
                i.putExtra("image4",image4);
                i.putExtra("productStockId",productStockId);
                i.putExtra("productRating",productRating);


                getActivity().startActivity(i);
            }
        });
        return v;
    }
    public void updateProductStatus(final String productStockId, final String status){
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


                params.put("product_stock_id", productStockId);
                params.put("status", status);

                params.put("action", "update_status");

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

}
