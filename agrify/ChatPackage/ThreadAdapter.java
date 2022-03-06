package com.example.agrify.ChatPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Belal on 5/29/2016.
 */
//Class extending RecyclerviewAdapter
public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

    //user id
    private String myUserID;
    private Context context;

    //Tag for tracking self message
    private int SELF = 786;
    private int TEMP = 991;

    //ArrayList of messages object containing all the messages in the thread
    private ArrayList<Message> messages;

    //Constructor
    public ThreadAdapter(Context context, ArrayList<Message> messages, String myUserID) {
        this.myUserID = myUserID;
        this.messages = messages;
        this.context = context;
    }

    //IN this method we are tracking the self message
    @Override
    public int getItemViewType(int position) {
        //getting message object of current position
        Message message = messages.get(position);

        //If its owner  id is  equals to the logged in user id
        if (message.getSenderID().equals(myUserID)) {
            //Returning self
            return SELF;
        }
        if(message.getSenderID().equals("0")){
            return TEMP;
        }
        //else returning position
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creating view
        View itemView = null;
        //if view type is self
        if(viewType == TEMP){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_thread, parent, false);
            itemView.setVisibility(View.GONE);
        }else if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_thread, parent, false);
            itemView.setVisibility(View.VISIBLE);
        } else {
            //else inflating the layout others

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.other_thread, parent, false);
            itemView.setVisibility(View.VISIBLE);
        }
        //returing the view
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        final Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        Message message = messages.get(position);
        String[] stringArray = message.getDateTimeSent().split(", ");

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dateToday = df.format(Calendar.getInstance().getTime());
        String dateTimeString = "";
        Date dateTimeSent = null;
        try {
            dateTimeSent = df.parse(stringArray[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dateToday.equals(stringArray[0]))
            dateTimeString = dateTimeString + stringArray[4];
        else if (isDateInCurrentWeek(dateTimeSent))
            dateTimeString = dateTimeString + stringArray[1];
        else
            dateTimeString = dateTimeString + stringArray[2] + ", " + stringArray[3];
        if(message.getSameSenderID())
            holder.profilePictureImageView.setImageResource(android.R.color.transparent);
        else
            Glide.with(context)
                    .load(message.getProfilePicture())
                    .into(holder.profilePictureImageView);
        if(message.getSenderID().equals(SharedPrefManager.getInstance(context).getUserID()))
            holder.profilePictureImageView.setVisibility(View.GONE);
        else
            holder.profilePictureImageView.setVisibility(View.VISIBLE);
        holder.dateTimeSentTextView.setText(dateTimeString);
        holder.messageTextView.setText(message.getMessage());
        holder.dateTimeSentLinearLayout.setVisibility(View.GONE);
        holder.messageConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.dateTimeSentLinearLayout.getVisibility() == View.GONE){
                    holder.dateTimeSentLinearLayout.setVisibility(View.VISIBLE);
                    holder.dateTimeSentLinearLayout.setAnimation(slideUp);
                }else{
                    holder.dateTimeSentLinearLayout.setAnimation(slideDown);
                    holder.dateTimeSentLinearLayout.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    //Initializing views
    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilePictureImageView;
        private LinearLayout dateTimeSentLinearLayout;
        private TextView dateTimeSentTextView, messageTextView;
        private ConstraintLayout messageConstraintLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            profilePictureImageView     = (CircleImageView) itemView.findViewById(R.id.profilePictureImageView);
            dateTimeSentLinearLayout    = (LinearLayout) itemView.findViewById(R.id.dateTimeSentLinearLayout);
            dateTimeSentTextView        = (TextView) itemView.findViewById(R.id.dateTimeSentTextView);
            messageTextView             = (TextView) itemView.findViewById(R.id.messageTextView);
            messageConstraintLayout     = (ConstraintLayout) itemView.findViewById(R.id.messageConstraintLayout);

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
}
