package com.example.aisin.production;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aisin.Api.ApiHelper;
import com.example.aisin.R;
import com.example.aisin.adapter.ProductionAdapter;
import com.example.aisin.model.ProductionModel;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionHistory extends AppCompatActivity {
    private static final String TAG = "ProductionHistory";
    private static final String API_URL = "https://aisin.umrmaulana.my.id/api/production";
    
    private RecyclerView recyclerView;
    private ProductionAdapter adapter;
    private List<ProductionModel> productionList;
    private List<ProductionModel> filteredProductionList;
    private ImageButton backButton;
    private LinearLayout emptyStateView;
    private TextInputEditText searchInput;
    private RequestQueue requestQueue;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.production_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize views
        initViews();
        
        // Initialize data structures
        productionList = new ArrayList<>();
        filteredProductionList = new ArrayList<>();
        
        // Initialize adapter
        adapter = new ProductionAdapter(this, filteredProductionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        // Setup listeners
        setupListeners();
        
        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);
        
        // Fetch data
        fetchProductionHistory();
        
        // Display username from SharedPreferences
        displayUsername();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.productionRecyclerView);
        backButton = findViewById(R.id.backButton);
        emptyStateView = findViewById(R.id.emptyStateView);
        searchInput = findViewById(R.id.searchInput);
        userNameTextView = findViewById(R.id.userName);
    }
    
    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());
        
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProductions(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }
    
    private void fetchProductionHistory() {
        showLoading(true);
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "API Response: " + response.toString());
                        try {
                            if (response.has("data")) {
                                JSONArray dataArray = response.getJSONArray("data");
                                parseProductionData(dataArray);
                                showEmptyState(false);
                            } else {
                                Log.e(TAG, "Invalid API response format: no 'data' field");
                                showEmptyState(true);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                            showEmptyState(true);
                        }
                        showLoading(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "API Error: " + error.toString());
                        Toast.makeText(ProductionHistory.this, 
                                "Failed to load production history", Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                        showLoading(false);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = ApiHelper.getToken(ProductionHistory.this);
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        
        requestQueue.add(request);
    }
    
    private void parseProductionData(JSONArray dataArray) throws JSONException {
        productionList.clear();
        
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject productionObject = dataArray.getJSONObject(i);
            
            String id = productionObject.getString("id");
            String noFg = productionObject.getString("no_fg");
            int qty = productionObject.getInt("qty");
            String createdAt = productionObject.getString("created_at");
            
            ProductionModel production = new ProductionModel(id, noFg, qty, createdAt);
            productionList.add(production);
        }
        
        // Update the filtered list and adapter
        filteredProductionList.clear();
        filteredProductionList.addAll(productionList);
        adapter.updateData(filteredProductionList);
        
        Log.d(TAG, "Loaded " + productionList.size() + " production records");
    }
    
    private void filterProductions(String query) {
        filteredProductionList.clear();
        
        if (query.isEmpty()) {
            filteredProductionList.addAll(productionList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ProductionModel production : productionList) {
                if (production.getNo_fg().toLowerCase().contains(lowerCaseQuery)) {
                    filteredProductionList.add(production);
                }
            }
        }
        
        adapter.updateData(filteredProductionList);
        showEmptyState(filteredProductionList.isEmpty());
    }
    
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            emptyStateView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void displayUsername() {
        if (userNameTextView != null) {
            String username = ApiHelper.getUsername(this);
            userNameTextView.setText(username);
        }
    }
}
