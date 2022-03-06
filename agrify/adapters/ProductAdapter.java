package com.example.agrify.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify.R;
import com.example.agrify.activities.ProductDescription;
import com.example.agrify.models.Product;
import com.example.agrify.models.Users;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context mContext;
    private List<Product> mData;
    private List<Users> mUsers;

    public ProductAdapter(Context mContext, List<Product> mData, List<Users> mUsers) {
        this.mContext = mContext;
        this.mData = mData;
        this.mUsers = mUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, final int position) {
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
//        holder.productImage.setImageBitmap();
        holder.productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ProductDescription.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("userId", mUsers.get(position).getUserId());
                i.putExtra("userName", mUsers.get(position).getUserName());
                i.putExtra("userLocation", mUsers.get(position).getUserLocation());
                i.putExtra("userNumber", mUsers.get(position).getUserNumber());
                i.putExtra("productName", mData.get(position).getProductName());
                i.putExtra("productPrice", mData.get(position).getProductPrice() + "");
                i.putExtra("productCategory", mData.get(position).getProductCategory());
                i.putExtra("productStocks", mData.get(position).getProductStocks() + "");
                i.putExtra("productDescription", mData.get(position).getProductDescription());
                i.putExtra("image1", mData.get(position).getProductImage1());
                i.putExtra("image2", mData.get(position).getProductImage2());
                i.putExtra("image3", mData.get(position).getProductImage3());
                i.putExtra("image4", mData.get(position).getProductImage4());
                i.putExtra("productStockId", mData.get(position).getProductStockId());
                i.putExtra("productRating", mData.get(position).getProductRating() + "");


                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void filterProduct(List<Product> holderProduct) {
        mData = holderProduct;
        notifyDataSetChanged();
    }
    public void filteredProducts(List<Product> filteredProductList, List<Users> filteredUsersList){
        mData = filteredProductList;
        mUsers  =   filteredUsersList;
        notifyDataSetChanged();
    }
    public class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productName, productPrice;
        CardView productCard;
        ImageView productImage;
        RatingBar productRating;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.image_product_id);
            productName = (TextView) itemView.findViewById(R.id.product_name_id);
            productPrice = (TextView) itemView.findViewById(R.id.product_price_id);
            productCard = (CardView) itemView.findViewById(R.id.card_product_id);
            productRating = (RatingBar) itemView.findViewById(R.id.ratingBar_product_card_id);
        }
    }
}