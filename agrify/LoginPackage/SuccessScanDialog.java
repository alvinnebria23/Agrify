package com.example.agrify.LoginPackage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.agrify.AddDemandPackage.AddDemandActivity;
import com.example.agrify.R;

public class SuccessScanDialog extends AppCompatDialogFragment {
    Button addDemandButton;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());

        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        View view = inflater.inflate (R.layout.layout_success_scan_dialog, null);
        builder.setView (view)
                .setTitle ("SUCCESS");

        addDemandButton     =   (Button)    view.findViewById (R.id.addDemandButton);
        addDemandButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                openAddDemandActivity();
            }
        });

        return builder.create ();


    }
    public void openAddDemandActivity(){
        Intent intent = new Intent(getContext(), AddDemandActivity.class);
        intent.putExtra("activityType", "addDemand");
        startActivity(intent);
    }
}
