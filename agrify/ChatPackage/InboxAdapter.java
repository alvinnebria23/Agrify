package com.example.agrify.ChatPackage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.agrify.Constants;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder>{
    private Context mCtx;
    List<Inbox> inboxList;
    private Activity activity;
    private int TEMP = 786;
    public InboxAdapter(Context mCtx, List<Inbox> inboxList, Activity activity){
        this.mCtx = mCtx;
        this.inboxList = inboxList;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        Inbox inbox = inboxList.get(position);

        if(inbox.getUserID().equals("0")){
            return TEMP;
        }

        return  position;
    }

    @NonNull
    @Override
    public InboxAdapter.InboxViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        view = inflater.inflate(R.layout.inbox_card_view, viewGroup, false);
        if(viewType == TEMP)
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.VISIBLE);
        return new InboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InboxAdapter.InboxViewHolder holder, final int position) {
        String[] stringArray = inboxList.get(position).getDateTimeSent().split(", ");

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dateToday = df.format(Calendar.getInstance().getTime());
        String tempMessage = "";
        String bulletedDateTime = "\u2022 ";
        String fullName = setName(inboxList.get(position).getFullName());
        Date dateTimeSent = null;
        try {
            dateTimeSent = df.parse(stringArray[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(inboxList.get(position).getMessage().length() > 21){
            tempMessage = inboxList.get(position).getMessage().substring(0,20) + "...";
        }else{
            tempMessage = inboxList.get(position).getMessage();
        }
        if(inboxList.get(position).getUserID().equals(SharedPrefManager.getInstance(mCtx).getUserID())){
            holder.unreadImageView.setVisibility(View.GONE);
            holder.messageTextView.setTextColor(mCtx.getResources().getColor(R.color.DefaultColor));
            holder.fullNameTextView.setTypeface(null, Typeface.NORMAL);
            holder.messageTextView.setTypeface(null, Typeface.NORMAL);
        }else if(inboxList.get(position).getStatus().equals("seen") &&
                !inboxList.get(position).getUserID().equals(SharedPrefManager.getInstance(mCtx).getUserID())){
            holder.unreadImageView.setVisibility(View.GONE);
            holder.messageTextView.setTextColor(mCtx.getResources().getColor(R.color.DefaultColor));
            holder.fullNameTextView.setTypeface(null, Typeface.NORMAL);
            holder.messageTextView.setTypeface(null, Typeface.NORMAL);
        }else if(inboxList.get(position).getStatus().equals("unread") &&
                !inboxList.get(position).getUserID().equals(SharedPrefManager.getInstance(mCtx).getUserID())){
            holder.unreadImageView.setVisibility(View.GONE);
            holder.messageTextView.setTextColor(mCtx.getResources().getColor(R.color.Black));
            holder.fullNameTextView.setTypeface(null, Typeface.BOLD);
            holder.messageTextView.setTypeface(null, Typeface.BOLD);
            holder.unreadImageView.setVisibility(View.VISIBLE);
        }
        if (dateToday.equals(stringArray[0])) {
            bulletedDateTime = bulletedDateTime + stringArray[4];
        } else if (isDateInCurrentWeek(dateTimeSent)) {
            bulletedDateTime = bulletedDateTime + stringArray[1];
        } else {
            bulletedDateTime = bulletedDateTime + stringArray[2] + ", " + stringArray[3];
        }
        Glide.with(mCtx)
                .load(inboxList.get(position).getProfilePic())
                .into(holder.profilePictureImageView);

        holder.fullNameTextView.setText(fullName);
        holder.messageTextView.setText(tempMessage);
        holder.dateTimeSentTextView.setText(bulletedDateTime);

        holder.viewMessagesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMessageStatus(inboxList.get(position).getConversationID(), inboxList.get(position).getDateTimeSent());
                Intent intent = new Intent(mCtx, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("previousActivity", "InboxActivity");
                if(inboxList.get(position).getUserID().equals(SharedPrefManager.getInstance(mCtx).getUserID())){
                    intent.putExtra("userID", inboxList.get(position).getReceiverID());
                }else{
                    intent.putExtra("userID", inboxList.get(position).getUserID());
                }
                intent.putExtra("profilePicture", inboxList.get(position).getProfilePic());
                intent.putExtra("fullName", inboxList.get(position).getFullName());
                holder.viewMessagesImageView.getContext().startActivity(intent);
                activity.finish();
            }
        });
        holder.viewMessagesImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.deleteImageView.setVisibility(View.VISIBLE);
                holder.cancelImageView.setVisibility(View.VISIBLE);
                holder.viewMessagesImageView.setVisibility(View.GONE);
                holder.latestMessageConstraintLayout.setBackgroundResource(R.drawable.red_rectangle);
                return true;
            }
        });
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.viewMessagesImageView.setVisibility(View.VISIBLE);
                holder.deleteImageView.setVisibility(View.GONE);
                holder.cancelImageView.setVisibility(View.GONE);
                holder.latestMessageConstraintLayout.setBackgroundResource(0);
                openDialog(inboxList.get(position).getUserID(), inboxList.get(position).getReceiverID(), position);
            }
        });
        holder.cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.viewMessagesImageView.setVisibility(View.VISIBLE);
                holder.deleteImageView.setVisibility(View.GONE);
                holder.cancelImageView.setVisibility(View.GONE);
                holder.latestMessageConstraintLayout.setBackgroundResource(0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }
    public void updateList(List<Inbox> inboxList){
        this.inboxList = inboxList;
        notifyDataSetChanged();
    }
    public static class InboxViewHolder extends RecyclerView.ViewHolder{
        ImageView unreadImageView;
        CircleImageView profilePictureImageView;
        TextView fullNameTextView, messageTextView, dateTimeSentTextView;
        ImageView viewMessagesImageView, deleteImageView, cancelImageView;
        ConstraintLayout latestMessageConstraintLayout;
        public InboxViewHolder(@NonNull View itemView) {
            super(itemView);

            latestMessageConstraintLayout   =   (ConstraintLayout)  itemView.findViewById(R.id.lastestMessageConstraintLayout);
            profilePictureImageView         =   (CircleImageView) itemView.findViewById(R.id.profilePictureImageView);
            viewMessagesImageView           =   (ImageView)  itemView.findViewById(R.id.viewMessagesImageView);
            unreadImageView                 =   (ImageView) itemView.findViewById(R.id.unreadImageView);
            deleteImageView                 =   (ImageView) itemView.findViewById(R.id.deleteImageView);
            cancelImageView                 =   (ImageView) itemView.findViewById(R.id.cancelImageView);
            fullNameTextView                =   (TextView)  itemView.findViewById(R.id.fullNameTextView);
            messageTextView                 =   (TextView)  itemView.findViewById(R.id.messageTextView);
            dateTimeSentTextView            =   (TextView)  itemView.findViewById(R.id.dateTimeSentTextView);
        }
    }
    public static boolean isDateInCurrentWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        try{
            targetCalendar.setTime(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }
    public String setName(String name){
        String nameArray[] = name.split(" ");
        String firstLetterFirstName   = nameArray[0].charAt(0) + "";
        String firstLetterLastName    = nameArray[1].charAt(0) + "";
        String fullName = firstLetterFirstName.toUpperCase() + nameArray[0].substring(1, nameArray[0].length()) + " " + firstLetterLastName.toUpperCase() + nameArray[1].substring(1, nameArray[1].length());

        return fullName;
    }
    public void updateMessageStatus(final String conversationID, final String dateTimeSent){
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
                params.put ("action", "updateMessageStatus");
                params.put ("conversation_id", conversationID);
                params.put ("date_time_sent", dateTimeSent);
                return params;
            }
        };
        Volley.newRequestQueue(mCtx).add(stringRequest);
    }
    public void removeConversation(String senderID, String receiverID){
        String otherUserID = "";
        if(!senderID.equals(SharedPrefManager.getInstance(mCtx).getUserID()))
            otherUserID = senderID;
        else
            otherUserID = receiverID;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("userID")
                .child(String.valueOf(SharedPrefManager.getInstance(mCtx).getUserID()))
                .child(otherUserID);
        databaseReference.setValue(null);
    }
    public void openDialog(final String senderID, final String receiverID, final int position){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(Html.fromHtml("<font color='#696969'>ARE YOU SURE YOU WANT TO DELETE ENTIRE CONVERSATION?</font>"));
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                Html.fromHtml("<font color='#FF0000'>DELETE</font>"),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        inboxList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemChanged(position, inboxList.size());
                        notifyDataSetChanged();
                        removeConversation(senderID, receiverID);
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
