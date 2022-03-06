package com.example.agrify.ListOfDemandersPackage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrify.DemandPackage.Demand;
import com.example.agrify.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsDemandAdapter extends RecyclerView.Adapter<AnalyticsDemandAdapter.AnalyticsDemandViewHolder>{
    private Context mCtx;
    private List<Demand> dataAnalyticsList;
    public AnalyticsDemandAdapter(Context mCtx, List<Demand> dataAnalyticsList){
        this.mCtx = mCtx;
        this.dataAnalyticsList = dataAnalyticsList;
    }

    @NonNull
    @Override
    public AnalyticsDemandAdapter.AnalyticsDemandViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        view = inflater.inflate(R.layout.horizontal_demands_card_view, viewGroup, false);
        return new AnalyticsDemandAdapter.AnalyticsDemandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AnalyticsDemandAdapter.AnalyticsDemandViewHolder holder, final int position) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String overAllDemandToday       = formatter.format(dataAnalyticsList.get (position).getOverAllDemandToday ()) + " KG";
        Glide.with (mCtx)
                .load (dataAnalyticsList.get (position).getImage ())
                .into (holder.productImageView);

        holder.productNameTextView.setText(dataAnalyticsList.get (position).getProductName ().toUpperCase());
        holder.demandedKilogramsTextView.setText(overAllDemandToday);
        holder.seeMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.seeMoreButton.getContext(), ListOfDemandersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("productImage", dataAnalyticsList.get(position).getImage());
                intent.putExtra("productName", holder.productNameTextView.getText().toString());
                intent.putExtra("demandToday", holder.demandedKilogramsTextView.getText().toString());
                intent.putExtra("agriculturalSector", dataAnalyticsList.get(position).getAgriculturalSector().toUpperCase());
                intent.putExtra("productID", dataAnalyticsList.get(position).getProductID());
                holder.seeMoreButton.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataAnalyticsList.size ();
    }


    public AnalyticsDemandAdapter(ArrayList<Demand> dataAnalyticsList){
        dataAnalyticsList = dataAnalyticsList;

    }
    public void updateList(List<Demand> dataAnalyticsList){
        this.dataAnalyticsList = dataAnalyticsList;
        notifyDataSetChanged();
    }
    public static class AnalyticsDemandViewHolder extends RecyclerView.ViewHolder{
        ImageView productImageView;
        TextView productNameTextView, demandedKilogramsTextView;
        Button seeMoreButton;
        public AnalyticsDemandViewHolder(@NonNull View itemView) {
            super (itemView);

            productImageView        =   (ImageView) itemView.findViewById(R.id.productImageView);
            productNameTextView     =   (TextView)  itemView.findViewById(R.id.productNameTextView);
            demandedKilogramsTextView =   (TextView)  itemView.findViewById(R.id.demandedKilogramsTextView);
            seeMoreButton           =   (Button)    itemView.findViewById(R.id.seeMoreButton);
        }
    }
}
