package com.example.agrify.RegisterPackage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.agrify.LoginPackage.MainActivity;
import com.example.agrify.R;

public class successDialog extends AppCompatDialogFragment {
    Button loginButton;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());

        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        View view = inflater.inflate (R.layout.layout_successdialog, null);
        builder.setView (view)
                .setTitle ("SUCCESS");

        loginButton     =   (Button)    view.findViewById (R.id.loginButton);
        loginButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

        return builder.create ();


    }
    public void openLoginActivity(){
        Intent intent = new Intent (getContext (), MainActivity.class);
        startActivity (intent);
    }
}
