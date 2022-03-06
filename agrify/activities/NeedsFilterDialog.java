package com.example.agrify.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.agrify.R;
import com.example.agrify.adapters.ProductAdapter;
import com.example.agrify.models.Product;
import com.example.agrify.models.Users;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeedsFilterDialog extends AppCompatDialogFragment {
    private View view;
    private AutocompleteSupportFragment autocompleteFragment;
    private String address = "0", ratingString = "";
    private EditText priceMinimumEditText, priceMaximumEditText;
    private TextView cancelTextView, applyTextView;
    private RatingBar ratingBar;
    private List<Product> productList;
    private List<Product> filteredProductList;
    private List<Users> usersList;
    private List<Users> filteredUsersList;
    private ProductAdapter productAdapter;
    OnMyDialogResult mDialogResult;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        if(view == null) {
            view = inflater.inflate(R.layout.needs_filter_layout, null);
        }
        Places.initialize(getActivity(), "AIzaSyCb4h9xwGSZTiTH83sLALrjh_Dfn8XIWK0");
        PlacesClient placesClient = Places.createClient(getActivity());
        priceMinimumEditText    =   (EditText)  view.findViewById(R.id.priceMinimumEditText);
        priceMaximumEditText    =   (EditText)  view.findViewById(R.id.priceMaximumEditText);
        cancelTextView          =   (TextView)  view.findViewById(R.id.cancelTextView);
        applyTextView           =   (TextView)  view.findViewById(R.id.applyTextView);
        ratingBar               =   (RatingBar) view.findViewById(R.id.ratingBar);
        productList             =   ((ProductViewActivity) getActivity()).getProductList();
        usersList               =   ((ProductViewActivity) getActivity()).getUsersList();
        productAdapter          =   new ProductAdapter(getActivity(), productList, usersList);
        filteredProductList     =   new ArrayList<>();
        filteredUsersList       =   new ArrayList<>();
        autocompleteFragment = (AutocompleteSupportFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        builder.setView(view);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        autocompleteFragment.setCountry ("PH");
        autocompleteFragment.setOnPlaceSelectedListener (new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                address = place.getAddress();
            }
            @Override
            public void onError(@NonNull Status status) {

            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                ratingString = String.valueOf(rating);
            }
        });
        applyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidInputs()) {
                    processFilter();
                    updateList();
                    if (mDialogResult != null) {
                        mDialogResult.finish(filteredProductList, filteredUsersList);
                        NeedsFilterDialog.this.dismiss();
                    }
                }else
                    Toast.makeText(getContext(), "Please input price range correctly.", Toast.LENGTH_SHORT).show();
            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogResult != null) {
                    mDialogResult.finish(productList, usersList);
                    NeedsFilterDialog.this.dismiss();
                }
            }
        });
        return builder.create ();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(autocompleteFragment != null && getActivity() != null && !getActivity().isFinishing()) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
        }
    }
    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(List<Product> filteredProductList, List<Users> filteredUsersList);
    }
    public boolean isValidInputs(){
        boolean isValid = true;
        if(!priceMinimumEditText.getText().toString().matches("") ||
                !priceMaximumEditText.getText().toString().matches("")){
            if(Integer.parseInt(priceMinimumEditText.getText().toString()) >
                Integer.parseInt(priceMaximumEditText.getText().toString()))
                isValid = false;
            else
                isValid = true;
        }

        return isValid;
    }
    public void processFilter(){
        List<Product> tempProductsList = new ArrayList<>();
        List<Users> tempUsersList = new ArrayList<>();
        if(address.equals("0")){
            filteredProductList = productList;
            filteredUsersList = usersList;
        }else{
             for(int i = 0; i <= usersList.size() - 1; i++){
                 if(usersList.get(i).getUserLocation().toLowerCase()
                    .contains(address.toLowerCase())){
                     tempProductsList.add(productList.get(i));
                     tempUsersList.add(usersList.get(i));
                 }
             }
             filteredProductList = tempProductsList;
             filteredUsersList = tempUsersList;
        }
        if(!priceMinimumEditText.getText().toString().matches("") ||
                !priceMaximumEditText.getText().toString().matches("")) {
            tempProductsList = new ArrayList<>();
            tempUsersList = new ArrayList<>();
            for (int i = 0; i <= filteredProductList.size() - 1; i++) {
                double price = filteredProductList.get(i).getProductPrice();
                if (price >= Double.parseDouble(priceMinimumEditText.getText().toString()) &&
                        price <= Double.parseDouble(priceMaximumEditText.getText().toString())) {
                    tempProductsList.add(filteredProductList.get(i));
                    tempUsersList.add(filteredUsersList.get(i));
                }
            }
            filteredProductList = tempProductsList;
            filteredUsersList = tempUsersList;
        }
        if (!String.valueOf(ratingBar.getRating()).equals("0.0")) {
            tempProductsList = new ArrayList<>();
            tempUsersList = new ArrayList<>();
            for (int i = 0; i <= filteredProductList.size() - 1; i++) {
                if (String.valueOf(filteredProductList.get(i).getProductRating())
                        .equals(ratingString)) {
                    tempProductsList.add(filteredProductList.get(i));
                    tempUsersList.add(filteredUsersList.get(i));
                }
            }
            filteredProductList = tempProductsList;
            filteredUsersList = tempUsersList;
        }
    }
    public void updateList(){
        productAdapter.filteredProducts(filteredProductList, filteredUsersList);
    }
}
