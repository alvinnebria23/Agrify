package com.example.agrify.LoginPackage;

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
import com.example.agrify.R;
import com.example.agrify.RequestHandler;

import java.util.HashMap;
import java.util.Map;
public class InputBusinessPermit extends DialogFragment implements View.OnClickListener{
    private EditText businessPermitEditText;
    private TextView saveTextView, cancelTextView;
    private String message = "";
    OnMyDialogResult mDialogResult;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_input_business_permit, container, false);

        businessPermitEditText      =   (EditText)  view.findViewById(R.id.businessPermitEditText);
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
                if(businessPermitEditText.getText().toString().length() > 0) {
                    saveBusinessPermit();
                }else{
                    showToast();
                }
                break;
            case R.id.cancelTextView:
                this.dismiss();
                break;
        }
    }
    public void saveBusinessPermit(){
        StringRequest stringRequest = new StringRequest (
                Request.Method.POST, Constants.URL_MANAGE_DEMAND,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        if (mDialogResult != null) {
                            mDialogResult.finish();
                            InputBusinessPermit.this.dismiss();
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
                params.put ("demand_id", "1");
                params.put("action", "savebusinesspermit");
                params.put("user_id", SharedPrefManager.getInstance(getContext()).getUserID());
                params.put("business_permit",  businessPermitEditText.getText().toString());
                return params;
            }
        };
        RequestHandler.getInstance (getContext()).addToRequestQueue (stringRequest);
    }
    public void showToast(){
        Toast toast = Toast.makeText(getActivity(),message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }
    public interface OnMyDialogResult{
        void finish();
    }
}
