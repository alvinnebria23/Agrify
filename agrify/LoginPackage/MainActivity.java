package com.example.agrify.LoginPackage;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.agrify.Constants;
import com.example.agrify.R;
import com.example.agrify.RegisterPackage.CreateAccountActivity;
import com.example.agrify.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button createAccountButton, loginButton;
    EditText usernameEditText, passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        if(SharedPrefManager.getInstance (this).isLoggedIn ()){
            finish ();
            startActivity (new Intent (this, HomeActivity.class));
            return;
        }

        usernameEditText        =   (EditText)  findViewById (R.id.usernameEditText);
        passwordEditText        =   (EditText)  findViewById (R.id.passwordEditText);
        createAccountButton     =   (Button)    findViewById (R.id.createAccountButton);
        loginButton             =   (Button)    findViewById (R.id.loginButton);
        createAccountButton.setOnClickListener (this);
        loginButton.setOnClickListener (this);
        usernameEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.GreenGrass), PorterDuff.Mode.SRC_ATOP);
        passwordEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.GreenGrass), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId ()){
            case R.id.createAccountButton:
                openCreateAccountActivity ();
                break;
            case R.id.loginButton:
                userLogin ();
                break;
        }
    }
    public void openCreateAccountActivity(){
        Intent intent = new Intent (MainActivity.this, CreateAccountActivity.class);
        startActivity (intent);
    }
    public void userLogin(){
        final String username = usernameEditText.getText ().toString ().trim ();
        final String password = passwordEditText.getText ().toString ().trim ();

        StringRequest stringRequest = new StringRequest (
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject (response);
                            if(!jsonObject.getBoolean ("error")){
                                SharedPrefManager.getInstance (getApplicationContext ())
                                        .userLogin (
                                                jsonObject.getString ("user_id"),
                                                jsonObject.getString ("username"),
                                                jsonObject.getString ("first_name"),
                                                jsonObject.getString ("last_name"),
                                                jsonObject.getString ("location"),
                                                jsonObject.getString ("email_address"),
                                                jsonObject.getString ("user_type"),
                                                Constants.PATH_PROFILE_IMAGES_FOLDER+jsonObject.getString ("profile_pic"),
                                                jsonObject.getString ("status")
                                        );
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish ();
                            }else{
                                usernameEditText.setError("Invalid Username Or Password");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }

                    }
                },
                new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText (
                                getApplicationContext (),
                                error.getMessage (),
                                Toast.LENGTH_LONG
                        ).show ();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<> ();
                params.put ("username", username);
                params.put ("password", password);
                return params;
            }
        };
        RequestHandler.getInstance (this).addToRequestQueue (stringRequest);
    }
}
