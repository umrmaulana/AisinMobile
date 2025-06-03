package com.example.aisin.receiving;

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
import com.example.aisin.adapter.ReceivingAdapter;
import com.example.aisin.model.ReceivingModel;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceivingHistory extends AppCompatActivity {
    private static final String TAG = "ReceivingHistory";
    private static final String API_URL = "https://aisin.umrmaulana.my.id/api/receiving";
    
    private RecyclerView recyclerView;
    private ReceivingAdapter adapter;
    private List<ReceivingModel> receivingList;
    private List<ReceivingModel> filteredReceivingList;
    private ImageButton backButton;
    private LinearLayout emptyStateView;
    private TextInputEditText searchInput;
    private TextView userNameTextView;
    private RequestQueue requestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.receiving_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize views
        initViews();
        
        // Initialize data structures
        receivingList = new ArrayList<>();
        filteredReceivingList = new ArrayList<>();
        
        // Initialize adapter
        adapter = new ReceivingAdapter(this, filteredReceivingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        // Setup listeners
        setupListeners();
        
        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);
        
        // Fetch data
        fetchReceivingHistory();
        
        // Display username from SharedPreferences
        displayUsername();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.receivingRecyclerView);
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
                filterReceivings(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }
    
    private void fetchReceivingHistory() {
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
                                parseReceivingData(dataArray);
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
                        Toast.makeText(ReceivingHistory.this, 
                                "Failed to load receiving history", Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                        showLoading(false);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = ApiHelper.getToken(ReceivingHistory.this);
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        
        requestQueue.add(request);
    }
    
    private void parseReceivingData(JSONArray dataArray) throws JSONException {
        receivingList.clear();
        
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject receivingObject = dataArray.getJSONObject(i);
            
            int id = receivingObject.getInt("id");
            String noPkb = receivingObject.getString("no_pkb");
            String receivedAt = receivingObject.getString("received_at");
            
            ReceivingModel receiving = new ReceivingModel(id, noPkb, receivedAt);
            receivingList.add(receiving);
        }
        
        // Update the filtered list and adapter
        filteredReceivingList.clear();
        filteredReceivingList.addAll(receivingList);
        adapter.updateData(filteredReceivingList);
        
        Log.d(TAG, "Loaded " + receivingList.size() + " receiving records");
    }
    
    private void filterReceivings(String query) {
        filteredReceivingList.clear();
        
        if (query.isEmpty()) {
            filteredReceivingList.addAll(receivingList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ReceivingModel receiving : receivingList) {
                if (receiving.getNo_pkb().toLowerCase().contains(lowerCaseQuery)) {
                    filteredReceivingList.add(receiving);
                }
            }
        }
        
        adapter.updateData(filteredReceivingList);
        showEmptyState(filteredReceivingList.isEmpty());
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
