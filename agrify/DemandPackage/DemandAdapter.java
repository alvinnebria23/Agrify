package com.example.agrify.DemandPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrify.ListOfDemandersPackage.ListOfDemandersActivity;
import com.example.agrify.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DemandAdapter extends RecyclerView.Adapter<DemandAdapter.DemandViewHolder>{
    private Context mCtx;
    private List<Demand> demandList;
    public DemandAdapter(Context mCtx, List<Demand> demandList){
        this.mCtx = mCtx;
        this.demandList = demandList;
    }

    @NonNull
    @Override
    public DemandAdapter.DemandViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        view = inflater.inflate(R.layout.demand_card_view, viewGroup, false);
        return new DemandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DemandAdapter.DemandViewHolder holder, final int position) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String overAllDemandToday       = formatter.format(demandList.get (position).getOverAllDemandToday ()) + " KG";
        String overAllDemandYesterday = "";
        overAllDemandYesterday = formatter.format(demandList.get(position).getOverAllDemandYesterday()) + " KG +";
        holder.overAllDemandYesterdayTextView.setTextColor(Color.GREEN);
        holder.overAllDemandYesterdayTextView.setVisibility(View.VISIBLE);
        if(demandList.get(position).getOverAllDemandYesterday() == 0)
            holder.overAllDemandYesterdayTextView.setVisibility(View.GONE);
        if(demandList.get(position).getProductName().contains("eggs")){
            holder.overAllDemandTodayTextView.setText(overAllDemandToday.replace("KG", "PCS"));
            holder.overAllDemandYesterdayTextView.setText(overAllDemandYesterday.replace("KG", "PCS"));
        }else{
            holder.overAllDemandTodayTextView.setText(overAllDemandToday);
            holder.overAllDemandYesterdayTextView.setText(overAllDemandYesterday);
        }
        Glide.with (mCtx)
                .load (demandList.get (position).getImage ())
                .into (holder.productImageView);
        holder.productNameTextView.setText(demandList.get (position).getProductName ().toUpperCase());
        holder.seeMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.seeMoreButton.getContext(), ListOfDemandersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("productImage", demandList.get(position).getImage());
                intent.putExtra("productName", holder.productNameTextView.getText().toString());
                intent.putExtra("demandToday", holder.overAllDemandTodayTextView.getText().toString());
                intent.putExtra("agriculturalSector", demandList.get(position).getAgriculturalSector().toUpperCase());
                intent.putExtra("productID", demandList.get(position).getProductID());
                holder.seeMoreButton.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return demandList.size ();
    }

    public void filterList(ArrayList<Demand> filteredList) {
        demandList = filteredList;
        notifyDataSetChanged();
    }

    public DemandAdapter(ArrayList<Demand> demandFilteredList){
        demandList = demandFilteredList;

    }
    public void updateList(List<Demand> demandList){
        this.demandList = demandList;
        notifyDataSetChanged();
    }
    public static class DemandViewHolder extends RecyclerView.ViewHolder{
        TextView productNameTextView, overAllDemandTodayTextView,
                    overAllDemandYesterdayTextView;
        ImageView productImageView;
        CardView demandCardView;
        Button seeMoreButton;
        public DemandViewHolder(@NonNull View itemView) {
            super (itemView);

            productNameTextView             =   itemView.findViewById (R.id.productNameTextView);
            overAllDemandTodayTextView      =   itemView.findViewById (R.id.overAllDemandTodayTextView);
            overAllDemandYesterdayTextView  =   itemView.findViewById (R.id.overAllDemandYesterdayTextView);
            productImageView                =   itemView.findViewById (R.id.productImageView);
            demandCardView                  =   itemView.findViewById (R.id.demandCardView);
            seeMoreButton                   =   itemView.findViewById (R.id.seeMoreButton);
        }
    }
}
