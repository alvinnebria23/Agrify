package com.example.agrify.MyDemandPackage;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrify.R;

import java.util.List;

public class ActiveDemandAdapter extends RecyclerView.Adapter<ActiveDemandAdapter.ActiveDemandViewHolder>{
    private Context mCtx;
    private List<MyDemand> activeDemandsList;
    private FragmentManager fragmentManager;
    public ActiveDemandAdapter(Context mCtx, List<MyDemand> activeDemandsList, FragmentManager fragmentManager){
        this.mCtx = mCtx;
        this.activeDemandsList = activeDemandsList;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public ActiveDemandAdapter.ActiveDemandViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        view = inflater.inflate(R.layout.my_demand_card_view, viewGroup, false);
        return new ActiveDemandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ActiveDemandAdapter.ActiveDemandViewHolder holder, final int position) {
        String demandKilograms = String.valueOf(activeDemandsList.get(position).getNeededKilograms() - activeDemandsList.get(position).getReceivedKilograms());
        String neededKilograms = String.valueOf(activeDemandsList.get(position).getNeededKilograms());

        Spannable word = new SpannableString(demandKilograms.replace(" KG", ""));
        word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.demandKilogramsTextView.setText(word);

        Spannable wordTwo = new SpannableString("/");
        wordTwo.setSpan(new ForegroundColorSpan(Color.GRAY), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.demandKilogramsTextView.append(wordTwo);

        Spannable wordThree = new SpannableString(neededKilograms.replace(" KG", ""));
        wordThree.setSpan(new ForegroundColorSpan(Color.GREEN), 0, wordThree.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.demandKilogramsTextView.append(wordThree);

        Glide.with (mCtx)
                .load (activeDemandsList.get(position).getProductImage())
                .into (holder.productImageView);
        holder.productNameTextView.setText(activeDemandsList.get(position).getProductName().toUpperCase());
        holder.dateDemandedTextView.setText(activeDemandsList.get(position).getDateDemanded());
        holder.removeButton.setVisibility(View.GONE);
        holder.deleteButton.setVisibility(View.GONE);
        holder.redemandButton.setVisibility(View.GONE);
        holder.manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManageDemandDialog manageDemandDialog = new ManageDemandDialog(fragmentManager);
                Bundle bundle = new Bundle();
                bundle.putString("demandID", String.valueOf(activeDemandsList.get(position).getDemandID()));
                bundle.putString("productName", activeDemandsList.get(position).getProductName());
                bundle.putInt("neededKilograms", activeDemandsList.get(position).getNeededKilograms());
                bundle.putInt("receivedKilograms", activeDemandsList.get(position).getReceivedKilograms());
                manageDemandDialog.setArguments(bundle);
                manageDemandDialog.show(fragmentManager, "manageDemandDialog");
            }
        });
        holder.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                Bundle bundle = new Bundle();
                bundle.putString("demandID", String.valueOf(activeDemandsList.get(position).getDemandID()));
                bundle.putString("message", "ARE YOU SURE YOU WANT TO MARK AS DONE?");
                bundle.putString("action", "markasdone");
                confirmDialog.setArguments(bundle);
                confirmDialog.show(fragmentManager, "confirmDialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return activeDemandsList.size();
    }

    public void updateList(List<MyDemand> activeDemandsList){
        this.activeDemandsList = activeDemandsList;
        notifyDataSetChanged();
    }

    public static class ActiveDemandViewHolder extends RecyclerView.ViewHolder{
        TextView productNameTextView, demandKilogramsTextView, dateDemandedTextView;
        ImageView productImageView;
        Button manageButton, doneButton, removeButton, deleteButton, redemandButton;
        public ActiveDemandViewHolder(@NonNull View itemView){
            super(itemView);

            productNameTextView             =   itemView.findViewById(R.id.productNameTextView);
            demandKilogramsTextView         =   itemView.findViewById(R.id.demandKilogramsTextView);
            dateDemandedTextView            =   itemView.findViewById(R.id.dateDemandedTextView);
            productImageView                =   itemView.findViewById(R.id.productImageView);
            redemandButton                  =   itemView.findViewById(R.id.redemandButton);
            removeButton                    =   itemView.findViewById(R.id.removeButton);
            deleteButton                    =   itemView.findViewById(R.id.deleteButton);
            manageButton                    =   itemView.findViewById(R.id.manageButton);
            doneButton                      =   itemView.findViewById(R.id.doneButton);
        }
    }
}
