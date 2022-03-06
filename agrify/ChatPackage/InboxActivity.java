package com.example.agrify.ChatPackage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import com.example.agrify.Constants;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxActivity extends AppCompatActivity{
    List<Inbox> inboxList;
    List<ConversationIDAndUserID> conversationIDAndUserIDList;
    List<ConversationIDAndUserProfile> conversationIDAndUserProfileList;
    ArrayList<String> conversationIDArrayList;
    private EditText searchEditText;
    private InboxAdapter inboxAdapter;
    private String myUserID = "", status = "";
    private int counter = 0, messageCounter = 0;
    private TextView emptyTextView;
    RecyclerView messages;
    private NestedScrollView inboxScrollView;
    private final String TAG = "DEBUG_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        setTitle("Inbox");

        inboxList                           = new ArrayList<>();
        conversationIDAndUserIDList         = new ArrayList<>();
        conversationIDAndUserProfileList    = new ArrayList<>();
        conversationIDArrayList             = new ArrayList<>();
        messages                            = (RecyclerView)        findViewById(R.id.messages);
        inboxScrollView                     = (NestedScrollView)    findViewById(R.id.inboxScrollView);
        emptyTextView                       = (TextView)            findViewById(R.id.emptyTextView);
        searchEditText                      = (EditText)            findViewById(R.id.searchEditText);
        inboxAdapter                        = new InboxAdapter(getApplicationContext(), inboxList, InboxActivity.this);
        messages.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        messages.setAdapter(inboxAdapter);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        myUserID = SharedPrefManager.getInstance(getApplicationContext()).getUserID();
        retrieveUserIDs();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            this.finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    public void retrieveUserIDs(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userID");
        final DatabaseReference myReference = reference.child(myUserID);
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversationIDAndUserIDList.clear();
                counter = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        conversationIDAndUserIDList.add(new ConversationIDAndUserID(String.valueOf(ds1.getKey()),
                                String.valueOf(ds.getKey())));
                    }
                }
                if(String.valueOf(conversationIDAndUserIDList.size())
                        .equals(String.valueOf(dataSnapshot.getChildrenCount()))){
                    retrieveUserProfile();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void retrieveUserProfile(){
        conversationIDAndUserProfileList.clear();
        for(int i = 0; i <= conversationIDAndUserIDList.size() - 1; i++)
            getProfileInfo(conversationIDAndUserIDList.get(i).getConversationID(),String.valueOf(conversationIDAndUserIDList.get(i).getUserID()));
    }
    public void getProfileInfo(final String conversationID, final String userID){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_USER_PROFILE,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray (response);
                            for (int i = 0; i < array.length (); i++) {
                                JSONObject profile = array.getJSONObject (i);

                                conversationIDAndUserProfileList.add(new ConversationIDAndUserProfile(conversationID,
                                        Constants.PATH_PROFILE_IMAGES_FOLDER+profile.getString("profile_pic"), profile.getString("fullname"),
                                        profile.getString("user_id")));
                            }
                            counter++;
                            if(counter == conversationIDAndUserIDList.size()){
                                inboxList.clear();
                                messageCounter = 0;
                                for(int i = 0; i <= conversationIDAndUserProfileList.size() - 1; i++) {
                                    messageCounter++;
                                    getMessages(conversationIDAndUserProfileList.get(i).getConversationID(),
                                            conversationIDAndUserProfileList.get(i).getProfilePic(),
                                            conversationIDAndUserProfileList.get(i).getFullName());
                                }
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
                params.put ("user_id", userID);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void getMessages(final String conversationID, final String profilePicture, final String fullName){
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_PROCESS_MESSAGE,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray (response);
                            for (int i = 0; i < array.length (); i++) {
                                JSONObject message = array.getJSONObject (i);

                                if(message.getString("sender_id").equals("0"));
                                else
                                inboxList.add(new Inbox(message.getString("conversation_id"),
                                        message.getString("sender_id"),
                                        profilePicture,
                                        fullName,
                                        message.getString("message"),
                                        message.getString("date_time_sent"),
                                        message.getString("status"),
                                        message.getString("receiver_id")));
                            }
                            if(messageCounter == conversationIDAndUserProfileList.size()){
                                inboxAdapter.updateList(inboxList);
                            }
                            if(inboxList.size() == 0)
                                emptyTextView.setVisibility(View.VISIBLE);
                            else
                                emptyTextView.setVisibility(View.GONE);
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
                params.put ("conversation_id", conversationID);
                params.put("action", "retrieveMessages");
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    public void filter(String text){
        ArrayList<Inbox> filteredList = new ArrayList<>();

        for (Inbox inbox : inboxList ) {
            if(inbox.getFullName().toLowerCase().contains(text.toLowerCase()))
                filteredList.add(inbox);
        }

        inboxAdapter.updateList(filteredList);
    }
}
