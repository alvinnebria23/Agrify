package com.example.agrify.ListOfDemandersPackage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.agrify.LoginPackage.SharedPrefManager;
import com.example.agrify.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class filterDialog extends AppCompatDialogFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView minusImageView, plusImageView;
    private EditText radiusEditText, demandMinimumEditText, demandMaximumEditText, priceMinimumEditText, priceMaximumEditText;
    private Button radiusSwitchButton, demandSwitchButton, priceSwitchButton;
    private LatLng center;
    private CircleOptions circleOptions;
    private Marker marker;
    private Circle drawnCircle;
    private int radius = 1300;
    private String radiusEditTextHolder = "", message = "";
    private List<Demanders> demandersList;
    private ArrayList<Demanders> tempList;
    private ArrayList<Demanders> filteredList;
    private ArrayList<Demanders> insideCircumferenceList;
    private ArrayList<Demanders> betweenDemandRangeList;
    private DemanderAdapter demanderAdapter;
    private View view;
    private TextView cancelTextView, applyTextView;
    private boolean geofence = true, includeDemandRange = true, includePriceRange = true, isValid = false;
    OnMyDialogResult mDialogResult;
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
        demanderAdapter = new DemanderAdapter(getActivity(), demandersList);
        demandersList = ((ListOfDemandersActivity) getActivity()).getDemandersList();
        filteredList = new ArrayList<>();
        insideCircumferenceList = new ArrayList<>();
        betweenDemandRangeList = new ArrayList<>();
        tempList = new ArrayList<>();
        center = getLocationFromAddress(getContext(), SharedPrefManager.getInstance(getActivity()).getLocation().toString());
        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        if(view == null) {
            view = inflater.inflate(R.layout.filter_layout, null);
        }
        setUpMap();
        minusImageView          =   (ImageView) view.findViewById(R.id.minusImageView);
        plusImageView           =   (ImageView) view.findViewById(R.id.plusImageView);
        radiusEditText          =   (EditText)  view.findViewById(R.id.radiusEditText);
        demandMinimumEditText   =   (EditText)  view.findViewById(R.id.demandMinimumEditText);
        demandMaximumEditText   =   (EditText)  view.findViewById(R.id.demandMaximumEditText);
        priceMinimumEditText    =   (EditText)  view.findViewById(R.id.priceMinimumEditText);
        priceMaximumEditText    =   (EditText)  view.findViewById(R.id.priceMaximumEditText);
        cancelTextView          =   (TextView)  view.findViewById(R.id.cancelTextView);
        applyTextView           =   (TextView)  view.findViewById(R.id.applyTextView);
        radiusSwitchButton      =   (Button)    view.findViewById(R.id.radiusSwitchButton);
        demandSwitchButton      =   (Button)    view.findViewById(R.id.demandSwitchButton);
        priceSwitchButton       =   (Button)    view.findViewById(R.id.priceSwitchButton);
        radiusEditText.setText(String.valueOf(radius));
        builder.setView (view);
        minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radius - 300 > 0) {
                    radius -= 300;
                    editMap(radius);
                }
            }
        });
        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radius+=300;
                editMap(radius);
            }
        });
        radiusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (radiusEditText.getText().length() == 0) {
                    radiusEditText.append(radiusEditTextHolder);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable != null) {
                    radius = Integer.parseInt(editable.toString());
                    if (drawnCircle != null) {
                        drawnCircle.remove();
                    }
                    drawnCircle = mMap.addCircle(circleOptions.center(center).radius(radius).strokeColor(Color.GREEN).fillColor(0x220000FF).strokeWidth(5));
                    radiusEditTextHolder = editable.toString();
                }
            }
        });
        radiusSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(geofence){
                    geofence = false;
                    radius = 0;
                    radiusSwitchButton.setText("OFF");
                    radiusSwitchButton.setBackgroundResource(R.drawable.rounded_red_button);
                    minusImageView.setVisibility(View.GONE);
                    plusImageView.setVisibility(View.GONE);
                    radiusEditText.setText(String.valueOf(radius));
                    radiusEditText.setVisibility(View.GONE);
                }else{
                    geofence = true;
                    radius = 1300;
                    radiusSwitchButton.setText("ON");
                    radiusSwitchButton.setBackgroundResource(R.drawable.rounded_button);
                    minusImageView.setVisibility(View.VISIBLE);
                    plusImageView.setVisibility(View.VISIBLE);
                    radiusEditText.setText(String.valueOf(radius));
                    radiusEditText.setVisibility(View.VISIBLE);
                }
            }
        });
        demandSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(includeDemandRange){
                    includeDemandRange = false;
                    demandSwitchButton.setText("OFF");
                    demandSwitchButton.setBackgroundResource(R.drawable.rounded_red_button);
                    demandMinimumEditText.setBackgroundColor(getResources().getColor(R.color.Gray));
                    demandMaximumEditText.setBackgroundColor(getResources().getColor(R.color.Gray));
                    demandMinimumEditText.setEnabled(false);
                    demandMaximumEditText.setEnabled(false);
                }else{
                    includeDemandRange = true;
                    demandSwitchButton.setText("ON");
                    demandSwitchButton.setBackgroundResource(R.drawable.rounded_button);
                    demandMinimumEditText.setBackgroundColor(getResources().getColor(R.color.White));
                    demandMaximumEditText.setBackgroundColor(getResources().getColor(R.color.White));
                    demandMinimumEditText.setEnabled(true);
                    demandMaximumEditText.setEnabled(true);
                }
            }
        });
        priceSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(includePriceRange){
                    includePriceRange = false;
                    priceSwitchButton.setText("OFF");
                    priceSwitchButton.setBackgroundResource(R.drawable.rounded_red_button);
                    priceMinimumEditText.setBackgroundColor(getResources().getColor(R.color.Gray));
                    priceMaximumEditText.setBackgroundColor(getResources().getColor(R.color.Gray));
                    priceMinimumEditText.setEnabled(false);
                    priceMaximumEditText.setEnabled(false);
                }else{
                    includePriceRange = true;
                    priceSwitchButton.setText("ON");
                    priceSwitchButton.setBackgroundResource(R.drawable.rounded_button);
                    priceMinimumEditText.setBackgroundColor(getResources().getColor(R.color.White));
                    priceMaximumEditText.setBackgroundColor(getResources().getColor(R.color.White));
                    priceMinimumEditText.setEnabled(true);
                    priceMaximumEditText.setEnabled(true);
                }
            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogResult != null) {
                    tempList.addAll(demandersList);
                    mDialogResult.finish(tempList);
                    filterDialog.this.dismiss();
                }
            }
        });
        applyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()) {
                    filteredList.clear();
                    findInsideCircumference();
                    findDemandAndPriceRange();
                    if (isValid) {
                        updateFilteredList();
                        if (mDialogResult != null) {
                            mDialogResult.finish(filteredList);
                            filterDialog.this.dismiss();
                        }
                    } else
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
        return builder.create ();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 8));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        marker = mMap.addMarker(new MarkerOptions().position(center).title("YOUR REGISTERED LOCATION")
            .snippet("here"));
        CameraPosition cp = CameraPosition.builder()
                .target(center)
                .zoom(13)
                .bearing(300)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 2000, null);
        drawnCircle = mMap.addCircle(circleOptions.center(center).radius(radius).strokeColor(Color.GREEN).fillColor(0x220000FF).strokeWidth(5));
    }
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }
        return p1;
    }
    public void setUpMap(){
        circleOptions = new CircleOptions();
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    public void editMap(int radius){
        if(drawnCircle != null){
            drawnCircle.remove();
        }
        drawnCircle = mMap.addCircle(circleOptions.center(center).radius(radius).strokeColor(Color.GREEN).fillColor(0x220000FF).strokeWidth(5));
        radiusEditText.setText(String.valueOf(radius));
    }
    public void findInsideCircumference(){
        if(geofence){
            insideCircumferenceList.clear();
            for(Demanders demanders : demandersList){
                if(geofence) {
                    LatLng placeCoordinates = getLocationFromAddress(getActivity(), demanders.getLocation());
                    String[] coordinatesArray = String.valueOf(placeCoordinates).substring(10, String.valueOf(placeCoordinates).length() - 1).replaceAll(" ", "").split(",");
                    float[] distance = new float[2];

                    Location.distanceBetween(Double.parseDouble(coordinatesArray[0]), Double.parseDouble(coordinatesArray[1]),
                            drawnCircle.getCenter().latitude, drawnCircle.getCenter().longitude, distance);

                    if (distance[0] < drawnCircle.getRadius()) {
                        insideCircumferenceList.add(demanders);
                    }
                }else{
                    insideCircumferenceList.add(demanders);
                }
            }
        }else
            insideCircumferenceList.addAll(demandersList);
    }
    public void findDemandAndPriceRange(){
        if(includeDemandRange){
            betweenDemandRangeList.clear();
                    for (Demanders demanders : insideCircumferenceList) {
                        int neededKilograms = demanders.getNeededKilograms() - demanders.getReceivedKilograms();
                        if (neededKilograms >= Integer.parseInt(demandMinimumEditText.getText().toString()) && neededKilograms <= Integer.parseInt(demandMaximumEditText.getText().toString()))
                            betweenDemandRangeList.add(demanders);
                    }
                    isValid = true;
        }else
            betweenDemandRangeList.addAll(insideCircumferenceList);
        if(includePriceRange){
            filteredList.clear();
            if(betweenDemandRangeList.size() != 0){
                   for (Demanders demanders : betweenDemandRangeList) {
                       int price = demanders.getPrice();
                       if (price >= Integer.parseInt(priceMinimumEditText.getText().toString()) &&
                               price <= Integer.parseInt(priceMaximumEditText.getText().toString())) {
                           filteredList.add(demanders);
                       }
                   }
                   isValid = true;
            }else
                isValid = true;
        }else{
            filteredList.addAll(betweenDemandRangeList);
            isValid = true;
        }
    }
    public void updateFilteredList(){
        demanderAdapter.updateFilterList(filteredList);
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(ArrayList<Demanders> filteredList);
    }
    @Override
    public void onDestroyView() {
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            getFragmentManager().beginTransaction().remove(mapFragment).commit();
        }
        super.onDestroyView();
    }
    public boolean isValid(){
        boolean valid = true;
        if(includeDemandRange){
            if(demandMaximumEditText.getText().toString().trim().length() == 0 || demandMinimumEditText.getText().toString().trim().length() == 0){
                valid = false;
                message = "Please input demand range properly.";
            }else if(Integer.parseInt(demandMinimumEditText.getText().toString()) > Integer.parseInt(demandMaximumEditText.getText().toString())){
                valid = false;
                message = "Please input demand range properly.";
            }
        }
        if(includePriceRange){
            if(priceMinimumEditText.getText().toString().trim().length() == 0 || priceMaximumEditText.getText().toString().trim().length() == 0){
                valid = false;
                message = "Please input price range properly.";
            }else if(priceMinimumEditText.getText().toString().trim().length() == 0 || priceMaximumEditText.getText().toString().trim().length() == 0){
                valid = false;
                message = "Please input price range properly.";
            }
        }
        return valid;
    }
}
