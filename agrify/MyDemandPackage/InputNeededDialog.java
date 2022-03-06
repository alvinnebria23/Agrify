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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InputNeededDialog extends DialogFragment implements View.OnClickListener{
    EditText receivedKilogramsEditText;
    static EditText yearEditText, monthEditText, dayEditText;
    private TextView saveTextView, cancelTextView;
    private Bundle bundle;
    private String demandID = "", action = "", message = "", dateString = "";
    private int neededKilograms, receivedKilograms;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_input_needed_kilograms, container, false);
        bundle                      =   getArguments();
        demandID                    =   bundle.getString("demandID");
        neededKilograms             =   bundle.getInt("neededKilograms");
        receivedKilograms           =   bundle.getInt("receivedKilograms");
        yearEditText                =   (EditText)  view.findViewById(R.id.yearEditText);
        monthEditText               =   (EditText)  view.findViewById(R.id.monthEditText);
        dayEditText                 =   (EditText)  view.findViewById(R.id.dayEditText);
        receivedKilogramsEditText   =   (EditText)  view.findViewById(R.id.receivedKilogramsEditText);
        saveTextView                =   (TextView)   view.findViewById(R.id.saveTextView);
        cancelTextView              =   (TextView)   view.findViewById(R.id.cancelTextView);
        receivedKilogramsEditText.setHint("NEEDED KILOGRAMS");
        saveTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveTextView:
                try {
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateToday = df.parse(df.format(c));
                    String[] dateStringArray = new SimpleDateFormat("yyyy-MM-dd",
                            Locale.getDefault()).format(new Date()).split("-");
                    String dayDate = dateStringArray[2];
                    if(yearEditText.getText().toString().trim().length() != 0 &&
                        monthEditText.getText().toString().trim().length() != 0 &&
                        dayEditText.getText().toString().trim().length() != 0) {
                        String year = yearEditText.getText().toString();
                        String month = monthEditText.getText().toString();
                        String day = dayEditText.getText().toString();
                        dateString = year + "-" + month + "-" + day;
                        if(Integer.parseInt(year) >= 2019) {
                            if (Integer.parseInt(month) < 13) {
                                if (Integer.parseInt(day) < 32 && Integer.parseInt(day) != Integer.parseInt(dayDate)) {
                                    Date durationEndDate = df.parse(dateString);

                                    if (receivedKilogramsEditText.getText().toString().trim().length() == 0) {
                                        message = "Please input kilograms properly.";
                                        showToast();
                                    } else if (dateToday.after(durationEndDate)) {
                                        message = "Please set duration end date after the date today";
                                        showToast();
                                    } else {
                                        action = "redemand";
                                        saveNeededInputKilograms(demandID);
                                        triggerFireBase();
                                        this.dismiss();
                                    }
                                } else {
                                    message = "Please set day properly and must not the date today";
                                    showToast();
                                }
                            } else {
                                message = "Please set month properly.";
                                showToast();
                            }
                        }else{
                            message = "Please set year properly.";
                            showToast();
                        }
                    }else{
                        message = "Please set date properly.";
                        showToast();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cancelTextView:
                this.dismiss();
                break;
        }
    }
    public void saveNeededInputKilograms(final String demandID){
        StringRequest stringRequest = new StringRequest (
                Request.Method.POST, Constants.URL_MANAGE_DEMAND,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")){
                            Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_LONG).show();
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
                params.put("duration_end", dateString);
                params.put("needed_kilograms",  receivedKilogramsEditText.getText().toString());
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
    public void showToast(){
        Toast toast = Toast.makeText(getActivity(),message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
