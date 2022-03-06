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

public class DeletedDemandAdapter extends RecyclerView.Adapter<DeletedDemandAdapter.DeletedDemandViewHolder> {
    private Context mCtx;
    private List<MyDemand> deletedDemandsList;
    private FragmentManager fragmentManager;
    public DeletedDemandAdapter(Context mCtx, List<MyDemand> deletedDemandsList, FragmentManager fragmentManager){
        this.mCtx = mCtx;
        this.deletedDemandsList = deletedDemandsList;
        this.fragmentManager = fragmentManager;
    }
    @NonNull
    @Override
    public DeletedDemandAdapter.DeletedDemandViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        view = inflater.inflate(R.layout.my_demand_card_view, viewGroup, false);
        return new DeletedDemandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeletedDemandAdapter.DeletedDemandViewHolder holder, final int position) {
        String demandKilograms = String.valueOf(deletedDemandsList.get(position).getNeededKilograms() -
                deletedDemandsList.get(position).getReceivedKilograms());
        final String neededKilograms = String.valueOf(deletedDemandsList.get(position).getNeededKilograms());

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
                .load (deletedDemandsList.get(position).getProductImage())
                .into (holder.productImageView);
        holder.productNameTextView.setText(deletedDemandsList.get(position).getProductName().toUpperCase());
        holder.dateDemandedTextView.setText(deletedDemandsList.get(position).getDateDemanded());
        holder.manageButton.setVisibility(View.GONE);
        holder.doneButton.setVisibility(View.GONE);
        holder.deleteButton.setVisibility(View.GONE);
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                Bundle bundle = new Bundle();
                bundle.putString("demandID", String.valueOf(deletedDemandsList.get(position).getDemandID()));
                bundle.putString("message", "ARE YOU SURE YOU WANT TO REMOVE ITEM?");
                bundle.putString("action", "markasremove");
                confirmDialog.setArguments(bundle);
                confirmDialog.show(fragmentManager, "confirmDialog");
            }
        });
        holder.redemandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputNeededDialog inputNeededDialog = new InputNeededDialog();
                Bundle bundle = new Bundle();
                bundle.putString("demandID", String.valueOf(deletedDemandsList.get(position).getDemandID()));
                bundle.putInt("neededKilograms", Integer.parseInt(neededKilograms));
                bundle.putInt("receivedKilograms", deletedDemandsList.get(position).getReceivedKilograms());
                inputNeededDialog.setArguments(bundle);
                inputNeededDialog.show(fragmentManager, "inputNeededDialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return deletedDemandsList.size();
    }
    public void updateList(List<MyDemand> deletedDemandsList){
        this.deletedDemandsList = deletedDemandsList;
        notifyDataSetChanged();
    }
    public static class DeletedDemandViewHolder extends RecyclerView.ViewHolder{
        TextView productNameTextView, demandKilogramsTextView, dateDemandedTextView;
        ImageView productImageView;
        Button manageButton, doneButton, removeButton, deleteButton, redemandButton;

        public DeletedDemandViewHolder(@NonNull View itemView){
            super(itemView);

            productNameTextView             =   itemView.findViewById(R.id.productNameTextView);
            demandKilogramsTextView         =   itemView.findViewById(R.id.demandKilogramsTextView);
            dateDemandedTextView            =   itemView.findViewById(R.id.dateDemandedTextView);
            productImageView                =   itemView.findViewById(R.id.productImageView);
            removeButton                    =   itemView.findViewById(R.id.removeButton);
            deleteButton                    =   itemView.findViewById(R.id.deleteButton);
            manageButton                    =   itemView.findViewById(R.id.manageButton);
            redemandButton                  =   itemView.findViewById(R.id.redemandButton);
            doneButton                      =   itemView.findViewById(R.id.doneButton);
        }
    }
}
