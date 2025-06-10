package com.example.aisin.receiving;

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
import com.example.aisin.adapter.ReceivingDetailAdapter;
import com.example.aisin.model.ReceivingDetailModel;

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

public class ReceivingDetailActivity extends AppCompatActivity {
    private static final String TAG = "ReceivingDetailActivity";
    private static final String API_URL = "https://aisin.umrmaulana.my.id/api/receivingdetail";

    private RecyclerView recyclerView;
    private ReceivingDetailAdapter adapter;
    private List<ReceivingDetailModel> detailList;
    private ImageButton backButton;
    private LinearLayout emptyStateView;
    private ProgressBar loadingIndicator;
    private TextView tvPkbNumber, tvReceivingDate, tvTotalParts, tvStatus;
    private RequestQueue requestQueue;
    private String pkbNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_detail);

        // Get PKB number from intent
        pkbNumber = getIntent().getStringExtra("pkb_number");
        if (pkbNumber == null || pkbNumber.isEmpty()) {
            Toast.makeText(this, "Invalid PKB number", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initViews();

        // Initialize data structures
        detailList = new ArrayList<>();
        adapter = new ReceivingDetailAdapter(this, detailList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Setup listeners
        backButton.setOnClickListener(v -> finish());

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Set PKB number
        tvPkbNumber.setText(pkbNumber);
        tvStatus.setText("Received");
        tvStatus.setTextColor(getResources().getColor(R.color.green));

        // Fetch data
        fetchReceivingDetails();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.receivingDetailsRecyclerView);
        backButton = findViewById(R.id.backButton);
        emptyStateView = findViewById(R.id.emptyStateView);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        tvPkbNumber = findViewById(R.id.tvPkbNumber);
        tvReceivingDate = findViewById(R.id.tvReceivingDate);
        tvTotalParts = findViewById(R.id.tvTotalParts);
        tvStatus = findViewById(R.id.tvStatus);
    }

    private void fetchReceivingDetails() {
        showLoading(true);

        String url = API_URL + "?no_pkb=" + pkbNumber;
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
                                parseReceivingDetails(dataArray);
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
                        Toast.makeText(ReceivingDetailActivity.this,
                                "Failed to load receiving details", Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                        showLoading(false);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = ApiHelper.getToken(ReceivingDetailActivity.this);
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void parseReceivingDetails(JSONArray dataArray) throws JSONException {
        detailList.clear();
        String latestDate = "";

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject detailObject = dataArray.getJSONObject(i);

            int id = detailObject.getInt("id");
            String noPkb = detailObject.getString("no_pkb");
            String noPart = detailObject.getString("no_part");
            int qty = detailObject.getInt("qty");
            String createdAt = detailObject.getString("created_at");

            // Save the latest date for the receiving info
            if (latestDate.isEmpty() || createdAt.compareTo(latestDate) > 0) {
                latestDate = createdAt;
            }

            ReceivingDetailModel detail = new ReceivingDetailModel(id, noPkb, noPart, qty, createdAt);
            detailList.add(detail);
        }

        // Update the adapter
        adapter.updateData(detailList);

        // Update receiving info
        updateReceivingInfo(latestDate);

        Log.d(TAG, "Loaded " + detailList.size() + " receiving details");
    }

    private void updateReceivingInfo(String dateStr) {
        // Format the date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            Date date = inputFormat.parse(dateStr);
            tvReceivingDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            // If parsing fails, try alternative format
            try {
                SimpleDateFormat altInputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                Date date = altInputFormat.parse(dateStr);
                tvReceivingDate.setText(outputFormat.format(date));
            } catch (ParseException e2) {
                tvReceivingDate.setText(dateStr);
            }
        }

        // Update total parts text
        tvTotalParts.setText(detailList.size() + " part" + (detailList.size() != 1 ? "s" : ""));
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
