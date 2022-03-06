package com.example.agrify.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.agrify.R;
import com.example.agrify.activities.BottomDialogActivity;
import com.example.agrify.activities.Constants;
import com.example.agrify.models.Product;
import com.example.agrify.models.Users;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActiveAdapter extends RecyclerView.Adapter<ProductActiveAdapter.MyViewHolder> {
    private Context mContext;
    private List<Product> mData;
    private FragmentManager manager;
    private List<Users> mUsers;
    private String statusProduct;
    public ProductActiveAdapter(Context mContext,FragmentManager manager, List<Product> mData,List<Users> mUsers,String statusProduct) {
        this.mContext = mContext;
        this.mData = mData;
        this.manager = manager;
        this.mUsers = mUsers;
        this.statusProduct = statusProduct;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_quantity_view,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.productName.setText(mData.get(position).getProductName());
        holder.productPrice.setText("P "+mData.get(position).getProductPrice()+"");
        holder.productRating.setRating((float) mData.get(position).getProductRating());
        Picasso.get()
                .load(mData.get(position).getProductImage1())
                .resize(80, 80)
                .centerCrop()
                .into(holder.productImage);
        holder.processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProductStatus(mData.get(position).getProductStockId(),"sold");
                Toast.makeText(mContext,"Mark as Sold",Toast.LENGTH_SHORT).show();
            }
        });
        holder.manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomDialogActivity bottomDialogActivity = new BottomDialogActivity();
                Bundle b=new Bundle();
                b.putString("userId",mUsers.get(position).getUserId());
                b.putString("userName",mUsers.get(position).getUserName());
                b.putString("userLocation",mUsers.get(position).getUserLocation());
                b.putString("userNumber",mUsers.get(position).getUserNumber());
                b.putString("productName",mData.get(position).getProductName());
                b.putString("productPrice",mData.get(position).getProductPrice()+"");
                b.putString("productCategory",mData.get(position).getProductCategory());
                b.putString("productStocks",mData.get(position).getProductStocks()+"");
                b.putString("productDescription",mData.get(position).getProductDescription());
                b.putString("image1",mData.get(position).getProductImage1());
                b.putString("image2",mData.get(position).getProductImage2());
                b.putString("image3",mData.get(position).getProductImage3());
                b.putString("image4",mData.get(position).getProductImage4());
                b.putString("productStockId",mData.get(position).getProductStockId());
                b.putString("productRating",mData.get(position).getProductRating()+"");
                bottomDialogActivity.setArguments(b);
                bottomDialogActivity.show(manager,"process");

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView productName,productPrice;
        Button manageButton,processButton;
        ImageView productImage;
        RatingBar productRating;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.myproduct_name_id);
            productPrice = (TextView) itemView.findViewById(R.id.myproduct_price_id);
            manageButton = (Button) itemView.findViewById(R.id.manage_button_id);
            processButton = (Button) itemView.findViewById(R.id.sold_button_id);
            productImage = (ImageView) itemView.findViewById(R.id.myproduct_image_id);
            productRating = (RatingBar) itemView.findViewById(R.id.ratingBar_myproduct_id);
        }
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
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }
}
