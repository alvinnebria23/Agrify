package com.example.agrify.MyDemandPackage;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.agrify.Constants;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;
import com.example.agrify.RequestHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class InputReceivedDialog extends DialogFragment implements View.OnClickListener {
    private EditText receivedKilogramsEditText;
    private TextView saveTextView, cancelTextView;
    private Bundle bundle;
    private String demandID = "", action = "";
    private int neededKilograms, receivedKilograms;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_input_received_kilograms, container, false);
        bundle                      =   getArguments();
        demandID                    =   bundle.getString("demandID");
        neededKilograms             =   bundle.getInt("neededKilograms");
        receivedKilograms           =   bundle.getInt("receivedKilograms");
        receivedKilogramsEditText   =   (EditText)  view.findViewById(R.id.receivedKilogramsEditText);
        saveTextView                =   (TextView)   view.findViewById(R.id.saveTextView);
        cancelTextView              =   (TextView)   view.findViewById(R.id.cancelTextView);

        saveTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveTextView:
                if(receivedKilograms + Integer.parseInt(receivedKilogramsEditText.getText().toString())  > neededKilograms){
                    Toast toast = Toast.makeText(getActivity(),"The number of kilograms you input exceeds to your demanded kilograms", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else if((receivedKilograms + Integer.parseInt(receivedKilogramsEditText.getText().toString())) - neededKilograms == 0 ){
                    action = "completed";
                    saveReceivedInputKilograms(demandID);
                    triggerFireBase();
                    this.dismiss();
                }else{
                    action = "savereceivedkilograms";
                    saveReceivedInputKilograms(demandID);
                    triggerFireBase();
                    this.dismiss();
                }
                break;
            case R.id.cancelTextView:
                this.dismiss();
                break;
        }
    }
    public void saveReceivedInputKilograms(final String demandID){
        StringRequest stringRequest = new StringRequest (
                Request.Method.POST, Constants.URL_MANAGE_DEMAND,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")){
                            Toast.makeText(getContext(), "INPUT SUCCESSFULLY SAVED", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText (
                                getContext (),
                                error.getMessage (),
                                Toast.LENGTH_LONG
                        ).show ();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<>();
                params.put ("demand_id", demandID);
                params.put("action", action);
                params.put("received_kilograms", String.valueOf(receivedKilograms + Integer.parseInt(receivedKilogramsEditText.getText().toString())));
                return params;
            }
        };
        RequestHandler.getInstance (getContext()).addToRequestQueue (stringRequest);
    }
    public void triggerFireBase(){
        String userID = SharedPrefManager.getInstance(getActivity()).getUserID();
        final Map<String, Object> map = new HashMap<String ,Object>();
        DatabaseReference myDemandsReference = FirebaseDatabase.getInstance().getReference("myDemands");
        final DatabaseReference myUserIDReference =  myDemandsReference.child(userID);
        myUserIDReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                String lastChildNodeKey = "";
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    lastChildNodeKey = String.valueOf(ds.getKey());
                    counter++;
                    break;
                }
                if(counter > 0){
                    String tempKey = myUserIDReference.push().getKey();
                    if (tempKey != null)
                        map.put(tempKey, "");
                    //myUserIDReference.updateChildren(map);
                    myUserIDReference.child(lastChildNodeKey).setValue(tempKey);
                }else{
                    String tempKey = myUserIDReference.push().getKey();
                    if (tempKey != null)
                        map.put(tempKey, "");
                    myUserIDReference.updateChildren(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
