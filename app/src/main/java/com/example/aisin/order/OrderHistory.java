package com.example.aisin.order;

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
import com.example.aisin.adapter.OrderAdapter;
import com.example.aisin.model.OrderModel;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistory extends AppCompatActivity {
    private static final String TAG = "OrderHistory";
    private static final String API_URL = "https://aisin.umrmaulana.my.id/api/order";
    
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<OrderModel> orderList;
    private List<OrderModel> filteredOrderList;
    private ImageButton backButton;
    private LinearLayout emptyStateView;
    private TextInputEditText searchInput;
    private TextView userNameTextView;
    private RequestQueue requestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize views
        initViews();
        
        // Initialize data structures
        orderList = new ArrayList<>();
        filteredOrderList = new ArrayList<>();
        
        // Initialize adapter
        adapter = new OrderAdapter(this, filteredOrderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        // Setup listeners
        setupListeners();
        
        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);
        
        // Fetch data
        fetchOrderHistory();
        
        // Display username from SharedPreferences
        displayUsername();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.orderRecyclerView);
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
                filterOrders(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }
    
    private void fetchOrderHistory() {
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
                                parseOrderData(dataArray);
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
                        Toast.makeText(OrderHistory.this, 
                                "Failed to load order history", Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                        showLoading(false);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = ApiHelper.getToken(OrderHistory.this);
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        
        requestQueue.add(request);
    }
    
    private void parseOrderData(JSONArray dataArray) throws JSONException {
        orderList.clear();
        
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject orderObject = dataArray.getJSONObject(i);
            
            int id = orderObject.getInt("id");
            String noPo = orderObject.getString("no_po");
            String createdAt = orderObject.getString("created_at");
            
            OrderModel order = new OrderModel(id, noPo, createdAt);
            orderList.add(order);
        }
        
        // Update the filtered list and adapter
        filteredOrderList.clear();
        filteredOrderList.addAll(orderList);
        adapter.updateData(filteredOrderList);
        
        Log.d(TAG, "Loaded " + orderList.size() + " orders");
    }
    
    private void filterOrders(String query) {
        filteredOrderList.clear();
        
        if (query.isEmpty()) {
            filteredOrderList.addAll(orderList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (OrderModel order : orderList) {
                if (order.getNo_po().toLowerCase().contains(lowerCaseQuery)) {
                    filteredOrderList.add(order);
                }
            }
        }
        
        adapter.updateData(filteredOrderList);
        showEmptyState(filteredOrderList.isEmpty());
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
