package com.example.agrify.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;
import com.example.agrify.activities.ProductDescription;
import com.example.agrify.models.Product;
import com.example.agrify.models.Users;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductFavorateAdapter extends RecyclerView.Adapter<ProductFavorateAdapter.MyViewHolder> {
    private Context mContext;
    private List<Product> mData;
    private List<Users> mUsers;

    public ProductFavorateAdapter(Context mContext, List<Product> mData, List<Users> mUsers) {
        this.mContext = mContext;
        this.mData = mData;
        this.mUsers = mUsers;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.favorate_product_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String formattedProductPrice       = formatter.format(mData.get(position).getProductPrice());
        holder.productName.setText(mData.get(position).getProductName().toUpperCase());
        holder.productPrice.setText("P " + formattedProductPrice);
        holder.productRating.setRating((float) mData.get(position).getProductRating());
        Picasso.get()
                .load(mData.get(position).getProductImage1())
                .resize(120, 120)
                .centerCrop()
                .into(holder.productImage);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(position);
            }
        });
        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ProductDescription.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("userId",mUsers.get(position).getUserId());
                i.putExtra("userName",mUsers.get(position).getUserName());
                i.putExtra("userLocation",mUsers.get(position).getUserLocation());
                i.putExtra("userNumber",mUsers.get(position).getUserNumber());
                i.putExtra("productName",mData.get(position).getProductName());
                i.putExtra("productPrice",mData.get(position).getProductPrice()+"");
                i.putExtra("productCategory",mData.get(position).getProductCategory());
                i.putExtra("productStocks",mData.get(position).getProductStocks()+"");
                i.putExtra("productDescription",mData.get(position).getProductDescription());
                i.putExtra("image1",mData.get(position).getProductImage1());
                i.putExtra("image2",mData.get(position).getProductImage2());
                i.putExtra("image3",mData.get(position).getProductImage3());
                i.putExtra("image4",mData.get(position).getProductImage4());
                i.putExtra("productStockId",mData.get(position).getProductStockId());
                i.putExtra("productRating",mData.get(position).getProductRating()+"");


                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView productName,productPrice;
        ImageView productImage;
        RatingBar productRating;
        Button deleteButton,viewButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteButton = (Button) itemView.findViewById(R.id.myfavorate_delete_button_id);
            productImage = (ImageView) itemView.findViewById(R.id.myfavorate_product_image_id);
            productName = (TextView) itemView.findViewById(R.id.myfavorate_product_name_id);
            productPrice = (TextView) itemView.findViewById(R.id.myfavorate_product_price_id);
            productRating = (RatingBar) itemView.findViewById(R.id.myfavorate_ratingBar_product_id);
            viewButton = (Button) itemView.findViewById(R.id.myfavorate_view_button_id);
        }
    }
    public void removeFavoriteItem(final String productFavoriteID, final int position){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, com.example.agrify.activities.Constants.URL_IP +"action.php",
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("deleteResponse "+response);
                        Log.i("deleteResponse", ">>" + response);
                        mData.remove(position);
                        notifyItemRemoved(position);
                        notifyItemChanged(position, mData.size());
                        notifyDataSetChanged();
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
                params.put("action", "remove_favorite_item");
                params.put ("user_id", SharedPrefManager.getInstance(mContext).getUserID());
                params.put ("product_favorate_id", productFavoriteID);
                return params;
            }
        };
        Volley.newRequestQueue(mContext).add(stringRequest);
    }
    public void openDialog(final int position){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage(Html.fromHtml("<font color='#696969'>ARE YOU SURE YOU WANT TO " +
                "REMOVE ITEM FROM YOUR FAVORITES?</font>"));
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                Html.fromHtml("<font color='#FF0000'>REMOVE</font>"),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeFavoriteItem(mData.get(position).getProductFavorateId() , position);
                        dialog.dismiss();
                    }
                });

        builder1.setNegativeButton(
                Html.fromHtml("<font color='#FF0000'>CANCEL</font>"),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
