package com.example.agrify.DemandPackage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.agrify.Constants;
import com.example.agrify.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CropsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private EditText searchEditText;
    List<Demand> demandList;
    private RecyclerView demands;
    private DemandAdapter demandAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyTextView, nameTextView, demandTextView;
    private boolean demandAscending = true, nameAscending = true;
    public CropsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate (R.layout.demand_fragment, container, false);

        demandList  =   new ArrayList<> ();
        swipeRefreshLayout      = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        demands                 = (RecyclerView)    view.findViewById (R.id.demands);
        searchEditText          = (EditText)        view.findViewById(R.id.searchEditText);
        emptyTextView           = (TextView)        view.findViewById(R.id.emptyTextView);
        nameTextView            = (TextView)        view.findViewById(R.id.nameTextView);
        demandTextView          = (TextView)        view.findViewById(R.id.demandTextView);
        demandAdapter           =   new DemandAdapter (getActivity (), demandList);
        demands.setLayoutManager(new GridLayoutManager(getActivity(),1));
        demands.setAdapter(demandAdapter);
        loadDemands();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                    filter(editable.toString());
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        nameTextView.setOnClickListener(this);
        demandTextView.setOnClickListener(this);
        return view;
    }
    private void filter(String text) {
        ArrayList<Demand> filteredList = new ArrayList<>();

        for (Demand demand : demandList ) {
            if (demand.getProductName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(demand);
            }
        }

        demandAdapter.filterList(filteredList);
    }
    private void loadDemands(){
        swipeRefreshLayout.setRefreshing(true);
        demandList.clear();
        StringRequest stringRequest =   new StringRequest (Request.Method.POST, Constants.URL_RETRIEVE_DEMAND,
            new Response.Listener<String> () {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray (response);
                    for (int i = 0; i < array.length (); i++) {
                        JSONObject demand = array.getJSONObject (i);

                        demandList.add (new Demand (
                                demand.getInt ("row_number"),
                                demand.getInt("product_id"),
                                Constants.PATH_CROPS_IMAGES_FOLDER+demand.getString ("product_image"),
                                demand.getString ("product_name"),
                                demand.getInt ("demand_today"),
                                demand.getInt ("demand_yesterday"),
                                "crops"
                        ));
                    }
                    if(demandList.isEmpty())
                        emptyTextView.setVisibility(View.VISIBLE);
                    else
                        emptyTextView.setVisibility(View.GONE);
                    demandAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
            }
            },
            new Response.ErrorListener () {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<>();
                params.put ("agricultural_sector", "crops");
                return params;
            }
        };
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    @Override
    public void onRefresh() {
        loadDemands();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nameTextView:
                if(nameAscending)
                    sortNameDescending();
                else
                    sortNameAscending();
                demands.getRecycledViewPool().clear();
                demandAdapter.notifyDataSetChanged();
                break;
            case R.id.demandTextView:
                if(demandAscending)
                    sortDemandDescending();
                else
                    sortDemandAscending();
                demands.getRecycledViewPool().clear();
                demandAdapter.notifyDataSetChanged();
                break;
        }
    }
    public void sortNameAscending(){
        nameAscending = true;
        Collections.sort(demandList, new Comparator<Demand>() {

            @Override
            public int compare(Demand demand, Demand t1) {
                return demand.getProductName().compareTo(t1.getProductName());
            }
        });
    }
    public void sortNameDescending(){
        nameAscending = false;
        Collections.sort(demandList, new Comparator<Demand>() {

            @Override
            public int compare(Demand demand, Demand t1) {
                return t1.getProductName().compareTo(demand.getProductName());
            }
        });
    }
    public void sortDemandDescending(){
        demandAscending = false;
        Collections.sort(demandList, new Comparator<Demand>() {
            @Override
            public int compare(Demand demand, Demand t1) {
                return Integer.compare(t1.getOverAllDemandToday(), demand.getOverAllDemandToday());
            }
        });
    }
    public void sortDemandAscending(){
        demandAscending = true;
        Collections.sort(demandList, new Comparator<Demand>() {
            @Override
            public int compare(Demand demand, Demand t1) {
                return Integer.compare(demand.getOverAllDemandToday(), t1.getOverAllDemandToday());
            }
        });
    }
}
