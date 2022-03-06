package com.example.agrify.ChatPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.agrify.ProfilePackage.ProfileActivity;
import com.example.agrify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private String previousActivity = "", fullName = "", profilePicture = "", userID = "",
            tempFullName = "", tempKey = "", myUserID = "", dateTimeSent = "",
            conversationID = "", profilePictureString = "", temp;
    private ImageView sendImageView, profilePictureImageView;
    private TextView fullNameTextView;
    private EditText messageEditText;
    private NestedScrollView scrollView;
    private Button backButton;
    private float messageEditTextHeight;
    private int newLineCounter = 0, imageViewMargin = 4, counter = 0;
    private Boolean isSameUserID = false;
    private RecyclerView chats;
    private ThreadAdapter threadAdapter;
    private ArrayList<Message> messages;
    private ArrayList<Message> processedMessage;
    private DateFormat df;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messages = new ArrayList<>();
        processedMessage = new ArrayList<>();
        previousActivity            = getIntent().getExtras().getString("previousActivity");
        fullName                    = getIntent().getExtras().getString("fullName");
        profilePicture              = getIntent().getExtras().getString("profilePicture");
        userID                      = getIntent().getExtras().getString("userID");
        myUserID                    = SharedPrefManager.getInstance(getApplicationContext()).getUserID();
        sendImageView                   =   (ImageView)     findViewById(R.id.sendImageView);
        profilePictureImageView         =   (ImageView)     findViewById(R.id.profilePictureImageView);
        backButton                      =   (Button)        findViewById(R.id.backButton);
        messageEditText                 =   (EditText)      findViewById(R.id.messageEditText);
        fullNameTextView                =   (TextView)      findViewById(R.id.fullNameTextView);
        scrollView                      =   (NestedScrollView)    findViewById(R.id.scrollView3);
        chats                           =   (RecyclerView)  findViewById(R.id.chats);
        progressDialog                  =    new ProgressDialog(ChatActivity.this);
        df = new SimpleDateFormat("dd/MM/yyyy, EEE, MMM d, yyyy, h:mm a");
        dateTimeSent = df.format(Calendar.getInstance().getTime());
        chats.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        threadAdapter = new ThreadAdapter(getApplicationContext(),messages,myUserID);
        messageEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.White), PorterDuff.Mode.SRC_ATOP);
        messageEditText.addTextChangedListener( new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged( CharSequence txt, int start, int before, int count ) {
                if(messageEditText.getText().toString().length() % 30 == 0 && newLineCounter <= 2) {
                    newLineCounter++;
                    imageViewMargin += 45;
                    messageEditTextHeight = messageEditTextHeight + convertDpToPx(getApplicationContext(), 25);
                    messageEditText.getLayoutParams().height = (int) messageEditTextHeight;
                    scrollView.refreshDrawableState();
                    messageEditText.refreshDrawableState();
                    setMargin(imageViewMargin);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().length() == 0){
                    newLineCounter = 0;
                    imageViewMargin = 4;
                    messageEditTextHeight = convertDpToPx(getApplicationContext(), 45);
                    messageEditText.getLayoutParams().height = (int)messageEditTextHeight;
                    setMargin(imageViewMargin);
                }
            }
        } );
        sendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(messageEditText.getText().toString().trim().length() != 0) {
                    sendImageView.setEnabled(false);
                    sendNotification();
                    triggerFireBase();
                    saveMessage();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(previousActivity.equals("InboxActivity")){
                    startActivity(new Intent(ChatActivity.this, InboxActivity.class));
                    finish();
                }else{
                    finish();
                }
            }
        });
        fullNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("profileType", "viewProfile");
                startActivity(intent);
            }
        });
        if(fullName.length() >= 15){
            tempFullName = fullName.substring(0,15) + "...";
        }else{
            tempFullName = fullName;
        }
        tempFullName = setName(tempFullName.toLowerCase());
        messageEditText.requestFocus();
        Glide.with(getApplicationContext())
                .load(profilePicture)
                .into(profilePictureImageView);

        fullNameTextView.setText(tempFullName);
        getConversationID();
        listenToFireBase();
    }
    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    public void setMargin(int imageViewMargin){
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) sendImageView.getLayoutParams();
        params.topMargin = imageViewMargin;
        sendImageView.setLayoutParams(params);
    }
    public String setName(String name){
        String nameArray[] = name.split(" ");
        String firstLetterFirstName   = nameArray[0].charAt(0) + "";
        String firstLetterLastName    = nameArray[1].charAt(0) + "";
        String fullName = firstLetterFirstName.toUpperCase() + nameArray[0].substring(1, nameArray[0].length()) + " " + firstLetterLastName.toUpperCase() + nameArray[1].substring(1, nameArray[1].length());
        return fullName;
    }
    public void sendNotification(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEND_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userID);
                params.put("my_user_id", String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUserID()));
                params.put("message", messageEditText.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void triggerFireBase(){
        Map<String, Object> map = new HashMap<String, Object>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userID").child(userID);
        DatabaseReference databaseReference = rootRef.child(myUserID).child(conversationID);
        tempKey = rootRef.push().getKey();
        tempKey = removeUnwantedSymbols(tempKey);
        if(tempKey != null){
            map.put(conversationID, tempKey);
        }
        databaseReference.setValue(map);
    }
    public void saveMessage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_PROCESS_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        processMessage(myUserID, SharedPrefManager.getInstance(getApplicationContext()).getProfilePic(),
                                SharedPrefManager.getInstance(getApplicationContext()).getFirstName()  + " " +
                                        SharedPrefManager.getInstance(getApplicationContext()).getLastName(),
                                messageEditText.getText().toString());
                        messageEditText.getText().clear();
                        sendImageView.setEnabled(true);
                        messageEditText.requestFocus();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("action", "saveMessage");
                params.put("conversation_id", conversationID);
                params.put("receiver_id", userID);
                params.put("message", messageEditText.getText().toString());
                params.put("sender_id", String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getUserID()));
                params.put("date_time_sent", dateTimeSent);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void getConversationID(){
        progressDialog.setMessage (" ");
        progressDialog.show ();
        progressDialog.setCanceledOnTouchOutside (false);
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference("userID").child(myUserID);
        DatabaseReference databaseReference = rootReference.child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    conversationID = String.valueOf(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void fetchMessages(){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_PROCESS_MESSAGE,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            temp = null;
                            for (int i = 0; i < array.length (); i++) {
                                isSameUserID = false;
                                JSONObject thread = array.getJSONObject (i);
                                if(thread.getString("sender_id").equals(myUserID)){
                                    profilePictureString = String.valueOf(SharedPrefManager.getInstance(getApplicationContext())
                                                            .getProfilePic());
                                }else{
                                    profilePictureString = profilePicture;
                                }
                                Message messageObject = new Message(conversationID,
                                        thread.getString("sender_id"),
                                        fullName,
                                        thread.getString("message"),
                                        thread.getString("date_time_sent"),
                                        profilePictureString,
                                        isSameUserID);
                                messages.add(messageObject);
                            }
                            processThread();
                            threadAdapter = new ThreadAdapter(getApplicationContext(), processedMessage, myUserID);

                            chats.setAdapter(threadAdapter);
                            scrollView.fullScroll(View.FOCUS_DOWN);
                            counter = 1;
                            if(processedMessage.size() == 0)
                                showToast();
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }
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
                params.put ("action", "populateThread");
                params.put ("conversation_id", conversationID);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    private void processThread(){
        boolean isSameID = false;
        for(int i = 0; i <= messages.size() - 1;i++){
            if(i+1 != messages.size()) {
                if (messages.get(i).getSenderID().equals(messages.get(i + 1).getSenderID())) {
                    isSameID = true;
                }else{
                    isSameID = false;
                }
            }else{
                isSameID = false;
            }
            processedMessage.add(new Message(messages.get(i).getConversationID(),
                    messages.get(i).getSenderID(),
                    messages.get(i).getFullName(),
                    messages.get(i).getMessage(),
                    messages.get(i).getDateTimeSent(),
                    messages.get(i).getProfilePicture(),
                    isSameID));
        }
        progressDialog.dismiss ();
    }
    public void processMessage(String userID, String profilePicture, String fullName, String message){
        if(processedMessage.size() != 0) {
            int index = processedMessage.size() - 1;
            String tempConversationID = processedMessage.get(index).getConversationID();
            String tempSenderID = processedMessage.get(index).getSenderID();
            String tempFullName = processedMessage.get(index).getFullName();
            String tempMessage = processedMessage.get(index).getMessage();
            String tempDateTimeSent = processedMessage.get(index).getDateTimeSent();
            String tempProfilePicture = processedMessage.get(index).getProfilePicture();
            if(userID.equals(tempSenderID))
                processedMessage.set(index,new Message(tempConversationID, tempSenderID, tempFullName, tempMessage,tempDateTimeSent,tempProfilePicture, true));
            else
                processedMessage.set(index,new Message(tempConversationID, tempSenderID, tempFullName, tempMessage,tempDateTimeSent,tempProfilePicture, false));
        }
        Message m = new Message(conversationID, userID, fullName, message, dateTimeSent, profilePicture, false);

        processedMessage.add(m);
        threadAdapter = new ThreadAdapter(getApplicationContext(), processedMessage, myUserID);
        chats.setAdapter(threadAdapter);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
    public void listenToFireBase(){
        DatabaseReference myUserIDReference = FirebaseDatabase.getInstance().getReference("userID").child(myUserID);
        DatabaseReference otherIDReference = myUserIDReference.child(userID);
        otherIDReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(counter > 0){
                        fetchLatestMessage();
                    }else {
                        fetchMessages();
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void fetchLatestMessage(){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_PROCESS_MESSAGE,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            temp = null;
                            for (int i = 0; i < array.length (); i++) {
                                isSameUserID = false;
                                JSONObject thread = array.getJSONObject (i);
                                if(thread.getString("sender_id").equals(myUserID)){
                                    profilePictureString = String
                                            .valueOf(SharedPrefManager
                                                    .getInstance(getApplicationContext())
                                            .getProfilePic());
                                }else{
                                    profilePictureString = profilePicture;
                                }
                                processMessage(thread.getString("sender_id"),
                                        profilePictureString, fullName,
                                        thread.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }
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
                params.put ("action", "retrieveMessages");
                params.put ("conversation_id", conversationID);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void showToast(){
        Toast toast = Toast.makeText(getApplicationContext(), "CONVERSATION IS EMPTY", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public String removeUnwantedSymbols(String tempKey){
        tempKey = tempKey.replaceAll("[^a-zA-Z0-9]", "");
        return tempKey;
    }
}
