package com.example.agrify.MyDemandPackage;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.example.agrify.Constants;
import com.example.agrify.LoginPackage.HomeActivity;
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

public class MyDemandActivity extends AppCompatActivity{
    private boolean viewingActiveDemands = false, viewingDoneDemands = false, viewingDeletedDemands = false;
    private NestedScrollView activeDemandsScrollView, doneDemandsScrollView, deletedDemandsScrollView;
    private Button activeSeeAllButton, doneSeeAllButton, deletedSeeAllButton;
    private ActiveDemandAdapter activeDemandAdapter;
    private DoneDemandAdapter doneDemandAdapter;
    private DeletedDemandAdapter deletedDemandAdapter;
    private RecyclerView activeDemands, doneDemands, deletedDemands;
    private TextView activeTextView, doneTextView, deletedTextView;
    private View view1, view2, view3;
    List<MyDemand> activeDemandsList, doneDemandsList, deletedDemandsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_demand);

        setTitle("My Demand");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        activeDemandsList  =   new ArrayList<>();
        doneDemandsList    =   new ArrayList<>();
        deletedDemandsList =   new ArrayList<>();
        activeDemands                 = (RecyclerView)      findViewById(R.id.activeDemands);
        doneDemands                   = (RecyclerView)      findViewById(R.id.doneDemands);
        deletedDemands                = (RecyclerView)      findViewById(R.id.deletedDemands);
        activeSeeAllButton            = (Button)            findViewById(R.id.activeSeeAllButton);
        doneSeeAllButton              = (Button)            findViewById(R.id.doneSeeAllButton);
        deletedSeeAllButton           = (Button)            findViewById(R.id.deletedSeeAllButton);
        activeDemandsScrollView       = (NestedScrollView)        findViewById(R.id.activeDemandsScrollView);
        doneDemandsScrollView         = (NestedScrollView)        findViewById(R.id.doneDemandsScrollView);
        deletedDemandsScrollView      = (NestedScrollView)        findViewById(R.id.deletedDemandsScrollView);
        activeTextView                = (TextView)          findViewById(R.id.activeTextView);
        doneTextView                  = (TextView)          findViewById(R.id.doneTextView);
        deletedTextView               = (TextView)          findViewById(R.id.deleteTextView);
        view1                         = (View)              findViewById(R.id.view);
        view2                         = (View)              findViewById(R.id.view1);
        view3                         = (View)              findViewById(R.id.view2);
        activeDemandAdapter =   new ActiveDemandAdapter (getApplicationContext(), activeDemandsList, getSupportFragmentManager());
        doneDemandAdapter   =   new DoneDemandAdapter(getApplicationContext(), doneDemandsList, getSupportFragmentManager());
        deletedDemandAdapter=   new DeletedDemandAdapter(getApplicationContext(), deletedDemandsList, getSupportFragmentManager());
        activeDemands.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        activeDemands.setAdapter(activeDemandAdapter);
        doneDemands.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        doneDemands.setAdapter(doneDemandAdapter);
        deletedDemands.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        deletedDemands.setAdapter(deletedDemandAdapter);
        activeSeeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle("Active");
                activeSeeAllButton.setVisibility(View.GONE);
                doneDemandsScrollView.setVisibility(View.GONE);
                doneSeeAllButton.setVisibility(View.GONE);
                deletedDemandsScrollView.setVisibility(View.GONE);
                deletedSeeAllButton.setVisibility(View.GONE);
                activeTextView.setVisibility(View.GONE);
                doneTextView.setVisibility(View.GONE);
                deletedTextView.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.GONE);
                activeDemandsScrollView.getLayoutParams().height = ActionBar.LayoutParams.MATCH_PARENT;
                viewingActiveDemands = true;
            }
        });
        doneSeeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle("Done");
                activeDemandsScrollView.setVisibility(View.GONE);
                activeSeeAllButton.setVisibility(View.GONE);
                doneSeeAllButton.setVisibility(View.GONE);
                deletedDemandsScrollView.setVisibility(View.GONE);
                deletedSeeAllButton.setVisibility(View.GONE);
                doneDemandsScrollView.getLayoutParams().height = ActionBar.LayoutParams.MATCH_PARENT;
                activeTextView.setVisibility(View.GONE);
                doneTextView.setVisibility(View.GONE);
                deletedTextView.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.GONE);
                viewingDoneDemands = true;
            }
        });
        deletedSeeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle("Deleted");
                activeDemandsScrollView.setVisibility(View.GONE);
                activeSeeAllButton.setVisibility(View.GONE);
                doneDemandsScrollView.setVisibility(View.GONE);
                doneSeeAllButton.setVisibility(View.GONE);
                deletedSeeAllButton.setVisibility(View.GONE);
                activeTextView.setVisibility(View.GONE);
                doneTextView.setVisibility(View.GONE);
                deletedTextView.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.GONE);
                deletedDemandsScrollView.getLayoutParams().height = ActionBar.LayoutParams.MATCH_PARENT;
                viewingDeletedDemands = true;
            }
        });
        listenToFireBase();
    }
    private void loadDemands(){
        activeDemands.getRecycledViewPool().clear();
        doneDemands.getRecycledViewPool().clear();
        deletedDemands.getRecycledViewPool().clear();
        activeDemandAdapter.notifyDataSetChanged();
        doneDemandAdapter.notifyDataSetChanged();
        deletedDemandAdapter.notifyDataSetChanged();
        activeDemandsList.clear();
        doneDemandsList.clear();
        deletedDemandsList.clear();
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_MY_DEMANDS,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray (response);

                            for (int i = 0; i < array.length (); i++) {
                                JSONObject demand = array.getJSONObject (i);
                                String productImage = "";
                                if(demand.getString("agricultural_sector").equals("crops")) {
                                    productImage = Constants.PATH_CROPS_IMAGES_FOLDER+demand.getString("product_image");
                                }else if(demand.getString("agricultural_sector").equals("fisheries")){
                                    productImage = Constants.PATH_FISHERIES_IMAGES_FOLDER+demand.getString("product_image");
                                }else if(demand.getString("agricultural_sector").equals("livestocks")){
                                    productImage = Constants.PATH_LIVESTOCKS_IMAGES_FOLDER+demand.getString("product_image");
                                }else if(demand.getString("agricultural_sector").equals("poultries")){
                                    productImage = Constants.PATH_POULTRIES_IMAGES_FOLDER+demand.getString("product_image");
                                }
                                if(demand.getString("status").equals("INC")){
                                    activeDemandsList.add(new MyDemand(
                                            productImage,
                                            demand.getInt("demand_id"),
                                            demand.getString("agricultural_sector"),
                                            demand.getString("product_name"),
                                            demand.getString("product_variety_name"),
                                            demand.getInt("vendor_id"),
                                            demand.getInt("price"),
                                            demand.getInt("needed_kilograms"),
                                            demand.getInt("received_kilograms"),
                                            demand.getString("date_demanded"),
                                            demand.getString("duration_end"),
                                            demand.getString("description"),
                                            demand.getString("status")));
                                }else if(demand.getString("status").equals("COM")){
                                    doneDemandsList.add(new MyDemand(
                                            productImage,
                                            demand.getInt("demand_id"),
                                            demand.getString("agricultural_sector"),
                                            demand.getString("product_name"),
                                            demand.getString("product_variety_name"),
                                            demand.getInt("vendor_id"),
                                            demand.getInt("price"),
                                            demand.getInt("needed_kilograms"),
                                            demand.getInt("received_kilograms"),
                                            demand.getString("date_demanded"),
                                            demand.getString("duration_end"),
                                            demand.getString("description"),
                                            demand.getString("status")));
                                }else if(demand.getString("status").equals("DEL")){
                                    deletedDemandsList.add(new MyDemand(
                                            productImage,
                                            demand.getInt("demand_id"),
                                            demand.getString("agricultural_sector"),
                                            demand.getString("product_name"),
                                            demand.getString("product_variety_name"),
                                            demand.getInt("vendor_id"),
                                            demand.getInt("price"),
                                            demand.getInt("needed_kilograms"),
                                            demand.getInt("received_kilograms"),
                                            demand.getString("date_demanded"),
                                            demand.getString("duration_end"),
                                            demand.getString("description"),
                                            demand.getString("status")));
                                }
                            }
                            if(activeDemandsList.size() == 0 && doneDemandsList.size() == 0 && deletedDemandsList.size() == 0){
                                Toast toast = Toast.makeText(getApplicationContext(),"No data available", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }

                        activeDemandAdapter.updateList(activeDemandsList);
                        doneDemandAdapter.updateList(doneDemandsList);
                        deletedDemandAdapter.updateList(deletedDemandsList);
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
                params.put ("user_id", SharedPrefManager.getInstance(getApplicationContext()).getUserID());
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setTitle("My Demand");
        if(viewingActiveDemands){
            if (item.getItemId() == android.R.id.home) {
                activeSeeAllButton.setVisibility(View.VISIBLE);
                doneDemandsScrollView.setVisibility(View.VISIBLE);
                doneSeeAllButton.setVisibility(View.VISIBLE);
                deletedDemandsScrollView.setVisibility(View.VISIBLE);
                deletedSeeAllButton.setVisibility(View.VISIBLE);
                activeTextView.setVisibility(View.VISIBLE);
                doneTextView.setVisibility(View.VISIBLE);
                deletedTextView.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                view3.setVisibility(View.VISIBLE);
                activeDemandsScrollView.getLayoutParams().height = (int) getResources().getDimension(R.dimen.scrollview_height);
                viewingActiveDemands = false;
            }
        }else if(viewingDoneDemands) {
            if (item.getItemId() == android.R.id.home) {
                activeDemandsScrollView.setVisibility(View.VISIBLE);
                activeSeeAllButton.setVisibility(View.VISIBLE);
                doneSeeAllButton.setVisibility(View.VISIBLE);
                deletedDemandsScrollView.setVisibility(View.VISIBLE);
                deletedSeeAllButton.setVisibility(View.VISIBLE);
                activeTextView.setVisibility(View.VISIBLE);
                doneTextView.setVisibility(View.VISIBLE);
                deletedTextView.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                view3.setVisibility(View.VISIBLE);
                doneDemandsScrollView.getLayoutParams().height = (int) getResources().getDimension(R.dimen.scrollview_height);
                viewingDoneDemands = false;
            }

        }else if(viewingDeletedDemands) {
            if (item.getItemId() == android.R.id.home) {
                activeDemandsScrollView.setVisibility(View.VISIBLE);
                activeSeeAllButton.setVisibility(View.VISIBLE);
                doneDemandsScrollView.setVisibility(View.VISIBLE);
                doneSeeAllButton.setVisibility(View.VISIBLE);
                deletedSeeAllButton.setVisibility(View.VISIBLE);
                activeTextView.setVisibility(View.VISIBLE);
                doneTextView.setVisibility(View.VISIBLE);
                deletedTextView.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                view3.setVisibility(View.VISIBLE);
                deletedDemandsScrollView.getLayoutParams().height = (int) getResources().getDimension(R.dimen.scrollview_height);
                viewingDeletedDemands = false;
            }
        }else {
            // handle arrow click here
            if (item.getItemId() == android.R.id.home) {
                this.finish(); // close this activity and return to preview activity (if there is any)
                startActivity(new Intent(this, HomeActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void listenToFireBase(){
        String myUserID = SharedPrefManager.getInstance(getApplicationContext()).getUserID();
        DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference("myDemands");
        DatabaseReference myUserIDReference = notificationsReference.child(myUserID);
        myUserIDReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadDemands();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
