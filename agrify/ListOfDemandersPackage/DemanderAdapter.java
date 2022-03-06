package com.example.agrify.ListOfDemandersPackage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.agrify.ChatPackage.ChatActivity;
import com.example.agrify.Constants;
import com.example.agrify.DescriptionPackage.DescriptionActivity;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.ProfilePackage.ProfileActivity;
import com.example.agrify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class    DemanderAdapter  extends RecyclerView.Adapter<DemanderAdapter.DemanderViewHolder>{
    private Context mCtx;
    private String tempKey;
     List<Demanders> demandersList;
    public DemanderAdapter(Context mCtx, List<Demanders> demandersList){
        this.mCtx = mCtx;
        this.demandersList = demandersList;
    }

    @NonNull
    @Override
    public DemanderAdapter.DemanderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        view = inflater.inflate(R.layout.demander_card_view, viewGroup, false);
        return new DemanderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DemanderAdapter.DemanderViewHolder holder, final int position) {
        String userLocation = demandersList.get(position).getLocation().toUpperCase();
        String name = demandersList.get(position).getFullName().toUpperCase();
        if(name.length() >= 17){
            name = name.substring(0,13) + "...";
        }
        if(userLocation.length() >= 30){
            userLocation = userLocation.substring(0,30) + " ...";
        }
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        final String formattedNeededKilograms       = formatter.format((Integer)demandersList.get (position).getNeededKilograms ()) + " KG";
        String formattedDemandKilograms       = formatter.format((Integer)demandersList.get(position).getNeededKilograms() -
                                                                (Integer)demandersList.get(position).getReceivedKilograms()) + " KG";
        String formattedPrice                 = "P "+formatter.format((Integer)demandersList.get(position).getPrice());

        if(demandersList.get(position).getProductName().toLowerCase().contains("eggs"))
            formattedDemandKilograms = formattedDemandKilograms.replace("KG", "PCS");
        Glide.with(mCtx)
                .load(demandersList.get(position).getProfilePic())
                .into(holder.profilePictureImageView);

        holder.fullNameTextView.setText(name);
        holder.locationTextView.setText(userLocation);
        holder.contactNumberTextView.setText(demandersList.get(position).getContactNumber());
        holder.demandKilogramsTextView.setText(formattedDemandKilograms);
        holder.neededKilogramsTextView.setText(formattedPrice);
        holder.viewProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.viewProfileTextView.getContext(), ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userID", String.valueOf(demandersList.get(position).getUserID()));
                intent.putExtra("profileType", "viewProfile");
                mCtx.startActivity(intent);
            }
        });
        holder.seeMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.seeMoreButton.getContext(), DescriptionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("profilePicture", demandersList.get(position).getProfilePic());
                intent.putExtra("fullName", demandersList.get(position).getFullName().toUpperCase());
                intent.putExtra("location", demandersList.get(position).getLocation().toUpperCase());
                intent.putExtra("contactNumber", holder.contactNumberTextView.getText().toString());
                intent.putExtra("demandKilograms", holder.demandKilogramsTextView.getText().toString());
                intent.putExtra("neededKilograms", formattedNeededKilograms);
                intent.putExtra("productName", demandersList.get(position).getProductName());
                intent.putExtra("price",  "P " + String.valueOf(demandersList.get(position).getPrice()));
                intent.putExtra("varietyName", demandersList.get(position).getVarietyName());
                intent.putExtra("agriculturalSector", demandersList.get(position).getAgriculturalSector());
                intent.putExtra("durationEnd", demandersList.get(position).getDurationEnd());
                intent.putExtra("description", demandersList.get(position).getDescription());
                intent.putExtra("demandID", String.valueOf(demandersList.get(position).getDemandID()));
                intent.putExtra("userID", String.valueOf(demandersList.get(position).getUserID()));
                holder.seeMoreButton.getContext().startActivity(intent);
            }
        });
        holder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String myUserID =  String.valueOf(SharedPrefManager.getInstance(mCtx).getUserID());
                final String otherUserID = String.valueOf(demandersList.get(position).getUserID());
                final DatabaseReference userIDRef = FirebaseDatabase.getInstance().getReference("userID");
                DatabaseReference reference = userIDRef.child(myUserID)
                                                .child(otherUserID);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            DatabaseReference newReference = FirebaseDatabase.getInstance().getReference("userID")
                                                            .child(myUserID);
                            Map<String, Object> map1 = new HashMap<String, Object>();
                            map1.put(otherUserID, "");
                            newReference.updateChildren(map1);

                            Map<String, Object> map2 = new HashMap<String, Object>();
                            DatabaseReference conversationIDReference = FirebaseDatabase.getInstance().getReference("userID")
                                                                        .child(myUserID).child(otherUserID);
                            tempKey = conversationIDReference.push().getKey();
                            tempKey = removeUnwantedSymbols(tempKey);
                            saveTempMessage(tempKey);
                            if(tempKey != null){
                                map2.put(tempKey, "");
                            }
                            conversationIDReference.updateChildren(map2);

                            DatabaseReference newReference1 = FirebaseDatabase.getInstance()
                                    .getReference("userID")
                                    .child(otherUserID);
                            Map<String, Object> map3 = new HashMap<String, Object>();
                            map3.put(myUserID, "");
                            newReference1.updateChildren(map3);

                            Map<String, Object> map4 = new HashMap<String, Object>();
                            DatabaseReference conversationIDReference1 = FirebaseDatabase.getInstance()
                                    .getReference("userID")
                                    .child(otherUserID).child(myUserID);
                            if(tempKey != null){
                                map4.put(tempKey, "");
                            }
                            conversationIDReference1.updateChildren(map4);

                            DatabaseReference conversationIDThread = FirebaseDatabase.getInstance()
                                    .getReference("conversationID");
                            conversationIDThread.updateChildren(map4);

                            Map<String, Object> map5= new HashMap<String, Object>();
                            map5.put(tempKey, "");
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                    .getReference("userID").child(myUserID).child(otherUserID).child(tempKey);
                            if(tempKey != null){
                                map5.put(tempKey, "");
                            }
                            databaseReference.child(tempKey).setValue(tempKey);
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance()
                                    .getReference("userID").child(otherUserID).child(myUserID).child(tempKey);
                            if(tempKey != null){
                                map5.put(tempKey, "");
                            }
                            databaseReference1.child(tempKey).setValue(tempKey);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Intent intent = new Intent(mCtx, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("previousActivity", "ListOfDemandersActivity");
                intent.putExtra("userID", String.valueOf(demandersList.get(position).getUserID()));
                intent.putExtra("profilePicture", demandersList.get(position).getProfilePic());
                intent.putExtra("fullName", demandersList.get(position).getFullName());
                holder.messageButton.getContext().startActivity(intent);
            }
        });
        if(String.valueOf(demandersList.get(position).getUserID()).equals(String.valueOf(SharedPrefManager.getInstance(mCtx).getUserID()))){
            holder.viewProfileTextView.setVisibility(View.GONE);
            holder.messageButton.setVisibility(View.GONE);
        }
    }
    public void filterList(ArrayList<Demanders> filteredList) {
        demandersList = filteredList;
        notifyDataSetChanged();
    }
    public void updateFilterList(ArrayList<Demanders> filteredList){
        demandersList = filteredList;
        notifyDataSetChanged();
    }
    public DemanderAdapter(ArrayList<Demanders> demanderFilteredList){
        demandersList = demanderFilteredList;
    }
    @Override
    public int getItemCount() {
        return demandersList.size();
    }

    public static class DemanderViewHolder extends RecyclerView.ViewHolder{
        TextView fullNameTextView, locationTextView, contactNumberTextView, viewProfileTextView, demandKilogramsTextView, neededKilogramsTextView;
        Button messageButton;
        LinearLayout seeMoreButton;
        ImageView profilePictureImageView;

        public DemanderViewHolder(@NonNull View itemView) {
            super(itemView);

            fullNameTextView                    =   itemView.findViewById(R.id.fullNameTextView);
            locationTextView                    =   itemView.findViewById(R.id.locationTextView);
            contactNumberTextView               =   itemView.findViewById(R.id.contactNumberTextView);
            viewProfileTextView                 =   itemView.findViewById(R.id.viewProfileTextView);
            demandKilogramsTextView             =   itemView.findViewById(R.id.demandKilogramsTextView);
            neededKilogramsTextView             =   itemView.findViewById(R.id.neededKilogramsTextView);
            profilePictureImageView             =   itemView.findViewById(R.id.profilePictureImageView);
            seeMoreButton                       =   itemView.findViewById(R.id.seeMoreButton);
            messageButton                       =   itemView.findViewById(R.id.messageButton);
        }
    }
    public String removeUnwantedSymbols(String tempKey){
        tempKey = tempKey.replaceAll("[^a-zA-Z0-9]", "");
        return tempKey;
    }
    public void saveTempMessage(final String tempKey){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy, EEE, MMM d, yyyy, h:mm a");
        final String dateTimeSent = df.format(Calendar.getInstance().getTime());
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_PROCESS_MESSAGE,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {


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
                params.put("date_time_sent", dateTimeSent);
                params.put ("conversation_id", tempKey);
                params.put("action", "saveTempMessage");
                return params;
            }
        };
        Volley.newRequestQueue(mCtx).add(stringRequest);
    }
}
