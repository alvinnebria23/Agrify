package com.example.agrify.MyDemandPackage;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

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

public class ConfirmDialog extends AppCompatDialogFragment {
    private String demandID, message, action;
    private Bundle bundle;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        bundle              = getArguments();
        demandID            = bundle.getString("demandID");
        message             = bundle.getString("message");
        action              = bundle.getString("action");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateDemand(demandID, action);
                        triggerFireBase();
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
    public void updateDemand(final String demandID, final String action){
        StringRequest stringRequest = new StringRequest (
                Request.Method.POST, Constants.URL_MANAGE_DEMAND,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")){
                            Toast.makeText(getActivity(), "ITEM SUCCESSFULLY MARKED AS DONE", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText (
                                getActivity(),
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
                return params;
            }
        };
        RequestHandler.getInstance (getActivity()).addToRequestQueue (stringRequest);
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
