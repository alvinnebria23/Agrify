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

public class DoneDemandAdapter extends RecyclerView.Adapter<DoneDemandAdapter.DoneDemandViewHolder>{
    private Context mCtx;
    private List<MyDemand> doneDemandsList;
    private FragmentManager fragmentManager;
    public DoneDemandAdapter(Context mCtx, List<MyDemand> doneDemandsList, FragmentManager fragmentManager){
        this.mCtx = mCtx;
        this.doneDemandsList = doneDemandsList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public DoneDemandAdapter.DoneDemandViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        view = inflater.inflate(R.layout.my_demand_card_view, viewGroup, false);
        return new DoneDemandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DoneDemandAdapter.DoneDemandViewHolder holder, final int position) {
        String demandKilograms = String.valueOf(doneDemandsList.get(position).getNeededKilograms() -
                doneDemandsList.get(position).getReceivedKilograms());
        final String neededKilograms = String.valueOf(doneDemandsList.get(position).getNeededKilograms());

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
                .load (doneDemandsList.get(position).getProductImage())
                .into (holder.productImageView);
        holder.productNameTextView.setText(doneDemandsList.get(position).getProductName().toUpperCase());
        holder.dateDemandedTextView.setText(doneDemandsList.get(position).getDateDemanded());
        holder.manageButton.setVisibility(View.GONE);
        holder.doneButton.setVisibility(View.GONE);
        holder.removeButton.setVisibility(View.GONE);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                Bundle bundle = new Bundle();
                bundle.putString("demandID", String.valueOf(doneDemandsList.get(position).getDemandID()));
                bundle.putString("message", "ARE YOU SURE YOU WANT TO DELETE ITEM?");
                bundle.putString("action", "markasdelete");
                confirmDialog.setArguments(bundle);
                confirmDialog.show(fragmentManager, "confirmDialog");
            }
        });
        holder.redemandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputNeededDialog inputNeededDialog = new InputNeededDialog();
                Bundle bundle = new Bundle();
                bundle.putString("demandID", String.valueOf(doneDemandsList.get(position).getDemandID()));
                bundle.putInt("neededKilograms", Integer.parseInt(neededKilograms));
                bundle.putInt("receivedKilograms", doneDemandsList.get(position).getReceivedKilograms());
                inputNeededDialog.setArguments(bundle);
                inputNeededDialog.show(fragmentManager, "inputNeededDialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return doneDemandsList.size();
    }
    public void updateList(List<MyDemand> doneDemandsList){
        this.doneDemandsList = doneDemandsList;
        notifyDataSetChanged();
    }

    public static  class DoneDemandViewHolder extends RecyclerView.ViewHolder{
        TextView productNameTextView, demandKilogramsTextView, dateDemandedTextView;
        ImageView productImageView;
        Button manageButton, doneButton, deleteButton, removeButton, redemandButton;
        public DoneDemandViewHolder(@NonNull View itemView){
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
