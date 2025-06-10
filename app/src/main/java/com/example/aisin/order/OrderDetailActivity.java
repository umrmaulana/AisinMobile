package com.example.aisin.order;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.example.aisin.adapter.OrderDetailAdapter;
import com.example.aisin.model.OrderDetailModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailActivity";
    private static final String API_URL = "https://aisin.umrmaulana.my.id/api/orderdetail";

    private RecyclerView recyclerView;
    private OrderDetailAdapter adapter;
    private List<OrderDetailModel> detailList;
    private ImageButton backButton;
    private LinearLayout emptyStateView;
    private ProgressBar loadingIndicator;
    private TextView tvOrderNo, tvOrderDate, tvTotalItems;
    private RequestQueue requestQueue;
    private String orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Get order number from intent
        orderNumber = getIntent().getStringExtra("order_number");
        if (orderNumber == null || orderNumber.isEmpty()) {
            Toast.makeText(this, "Invalid order number", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initViews();

        // Initialize data structures
        detailList = new ArrayList<>();
        adapter = new OrderDetailAdapter(this, detailList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Setup listeners
        backButton.setOnClickListener(v -> finish());

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Set order number
        tvOrderNo.setText(orderNumber);

        // Fetch data
        fetchOrderDetails();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.orderDetailsRecyclerView);
        backButton = findViewById(R.id.backButton);
        emptyStateView = findViewById(R.id.emptyStateView);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        tvOrderNo = findViewById(R.id.tvOrderNo);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvTotalItems = findViewById(R.id.tvTotalItems);
    }

    private void fetchOrderDetails() {
        showLoading(true);

        String url = API_URL + "?no_po=" + orderNumber;
        Log.d(TAG, "Fetching details from URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "API Response: " + response.toString());
                        try {
                            if (response.has("data")) {
                                JSONArray dataArray = response.getJSONArray("data");
                                parseOrderDetails(dataArray);
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
                        Toast.makeText(OrderDetailActivity.this,
                                "Failed to load order details", Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                        showLoading(false);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = ApiHelper.getToken(OrderDetailActivity.this);
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void parseOrderDetails(JSONArray dataArray) throws JSONException {
        detailList.clear();
        String latestDate = "";

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject detailObject = dataArray.getJSONObject(i);

            int id = detailObject.getInt("id");
            String noPo = detailObject.getString("no_po");
            String noPart = detailObject.getString("no_part");
            int qty = detailObject.getInt("qty");
            String createdAt = detailObject.getString("created_at");

            // Save the latest date for the order info
            if (latestDate.isEmpty() || createdAt.compareTo(latestDate) > 0) {
                latestDate = createdAt;
            }

            OrderDetailModel detail = new OrderDetailModel(id, noPo, noPart, qty, createdAt);
            detailList.add(detail);
        }

        // Update the adapter
        adapter.updateData(detailList);

        // Update order info
        updateOrderInfo(latestDate);

        Log.d(TAG, "Loaded " + detailList.size() + " order details");
    }

    private void updateOrderInfo(String dateStr) {
        // Format the date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            Date date = inputFormat.parse(dateStr);
            tvOrderDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            tvOrderDate.setText(dateStr);
        }

        // Update total items text
        tvTotalItems.setText(detailList.size() + " item" + (detailList.size() != 1 ? "s" : ""));
    }

    private void showLoading(boolean isLoading) {
        loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean isEmpty) {
        emptyStateView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
