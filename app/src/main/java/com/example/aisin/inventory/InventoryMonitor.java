package com.example.aisin.inventory;

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
import com.example.aisin.adapter.InventoryAdapter;
import com.example.aisin.model.PartModel;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryMonitor extends AppCompatActivity {
    private static final String TAG = "InventoryMonitor";
    private static final String API_URL = "https://aisin.umrmaulana.my.id/api/part";

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<PartModel> partList;
    private List<PartModel> filteredPartList;
    private ImageButton backButton;
    private LinearLayout emptyStateView;
    private TextInputEditText searchInput;
    private RequestQueue requestQueue;
    private TextView userNameTextView;
    private boolean isSearchVisible = false;
    private View searchInputContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.inventory_monitor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initViews();

        // Initialize data structures
        partList = new ArrayList<>();
        filteredPartList = new ArrayList<>();

        // Initialize adapter
        adapter = new InventoryAdapter(this, filteredPartList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Setup listeners
        setupListeners();

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Fetch data
        fetchInventoryData();
        
        // Display username from SharedPreferences
        displayUsername();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.inventoryRecyclerView);
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
                filterInventory(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }

    private void fetchInventoryData() {
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
                                parseInventoryData(dataArray);
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
                        Toast.makeText(InventoryMonitor.this,
                                "Failed to load inventory data", Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                        showLoading(false);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = ApiHelper.getToken(InventoryMonitor.this);
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void parseInventoryData(JSONArray dataArray) throws JSONException {
        partList.clear();

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject partObject = dataArray.getJSONObject(i);

            String noPart = partObject.getString("no_part");
            int stok = partObject.getInt("stok");
            String status = partObject.getString("status");

            PartModel part = new PartModel(noPart, stok, status);
            partList.add(part);
        }

        filteredPartList.clear();
        filteredPartList.addAll(partList);
        adapter.updateData(filteredPartList);
        
        Log.d(TAG, "Loaded " + partList.size() + " inventory items");
    }

    private void filterInventory(String query) {
        filteredPartList.clear();
        
        if (query.isEmpty()) {
            filteredPartList.addAll(partList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (PartModel part : partList) {
                if (part.getPart_number().toLowerCase().contains(lowerCaseQuery)) {
                    filteredPartList.add(part);
                }
            }
        }
        
        adapter.updateData(filteredPartList);
        showEmptyState(filteredPartList.isEmpty());
    }

    private void showLoading(boolean isLoading) {
        // You could add a progress indicator here
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
