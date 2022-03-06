package com.example.agrify.MyDemandPackage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.agrify.AddDemandPackage.AddDemandActivity;
import com.example.agrify.Constants;
import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;
import com.example.agrify.RequestHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ManageDemandDialog extends BottomSheetDialogFragment implements View.OnClickListener{
    private View markAsDoneLayout, deleteItemLayout, updateDemandLayout, inputReceivedKilogramsLayout;
    private String demandID, productName;
    private Bundle bundle;
    private ImageView closeImageView;
    private TextView productNameTextView;
    private FragmentManager fragmentManager;
    private int neededKilograms, receivedKilograms;
    public ManageDemandDialog(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_manage, container, false);
        bundle                          = getArguments();
        demandID                        = bundle.getString("demandID");
        productName                     = bundle.getString("productName").toUpperCase();
        neededKilograms                 = bundle.getInt("neededKilograms");
        receivedKilograms                = bundle.getInt("receivedKilograms");
        deleteItemLayout                =   (ConstraintLayout)  view.findViewById(R.id.deleteItemLayout);
        markAsDoneLayout                =   (ConstraintLayout)  view.findViewById(R.id.markAsDoneLayout);
        updateDemandLayout              =   (ConstraintLayout)  view.findViewById(R.id.updateDemandLayout);
        inputReceivedKilogramsLayout    =   (ConstraintLayout)  view.findViewById(R.id.inputReceivedKilogramsLayout);
        closeImageView                  =   (ImageView)         view.findViewById(R.id.closeImageView);
        productNameTextView             =   (TextView)          view.findViewById(R.id.productNameTextView);

        productNameTextView.setText(productName);
        closeImageView.setOnClickListener(this);
        markAsDoneLayout.setOnClickListener(this);
        deleteItemLayout.setOnClickListener(this);
        updateDemandLayout.setOnClickListener(this);
        inputReceivedKilogramsLayout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.markAsDoneLayout:
                markAsDone(demandID,view);
                triggerFireBase();
                this.dismiss();
                break;
            case R.id.updateDemandLayout:
                this.dismiss();
                Intent intent = new Intent(view.getContext(), AddDemandActivity.class);
                intent.putExtra("activityType", "update");
                intent.putExtra("demandID", demandID);
                startActivity(intent);
                break;
            case R.id.deleteItemLayout:
                deleteItem(demandID,view);
                triggerFireBase();
                this.dismiss();
                break;
            case R.id.inputReceivedKilogramsLayout:
                openInputReceivedKilogramsDialog(demandID, view);
                this.dismiss();
                break;
            case R.id.closeImageView:
                this.dismiss();
                break;
        }
    }
    public void markAsDone(final String demandID, final View view){
        StringRequest stringRequest = new StringRequest (
                Request.Method.POST, Constants.URL_MANAGE_DEMAND,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")){
                            Toast.makeText(view.getContext(), "ITEM SUCCESSFULLY MARKED AS DONE", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText (
                                view.getContext (),
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
                params.put("action", "markasdone");
                return params;
            }
        };
        RequestHandler.getInstance (getContext()).addToRequestQueue (stringRequest);
    }
    public void deleteItem(final String demandID, final View view){
        StringRequest stringRequest = new StringRequest (
                Request.Method.POST, Constants.URL_MANAGE_DEMAND,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")){
                            Toast.makeText(getContext(), "ITEM SUCCESSFULLY DELETED", Toast.LENGTH_LONG).show();
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
                params.put("action", "markasdelete");
                return params;
            }
        };
        RequestHandler.getInstance (getContext()).addToRequestQueue (stringRequest);
    }
    public void openInputReceivedKilogramsDialog(final String demandID, View view){
        InputReceivedDialog inputReceivedDialog = new InputReceivedDialog();
        Bundle bundle = new Bundle();
        bundle.putString("demandID", demandID);
        bundle.putInt("neededKilograms", neededKilograms);
        bundle.putInt("receivedKilograms", receivedKilograms);
        inputReceivedDialog.setArguments(bundle);
        inputReceivedDialog.show(fragmentManager, "inputReceivedDialog");
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
