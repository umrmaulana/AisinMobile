package com.example.aisin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.aisin.Api.AuthHelper;
import com.example.aisin.inventory.InventoryActivity;
import com.example.aisin.inventory.InventoryMonitor;
import com.example.aisin.order.OrderActivity;
import com.example.aisin.order.OrderHistory;
import com.example.aisin.production.ProductionActivity;
import com.example.aisin.production.ProductionHistory;
import com.example.aisin.receiving.ReceivingActivity;
import com.example.aisin.receiving.ReceivingHistory;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    Button btnLogout;
    TextView userNameTextView;
    CardView cardInventory, cardOrder, cardMetrics, cardReceiving;
    SwipeRefreshLayout swipeRefreshLayout;
    private long lastLoginTimestamp = 0;
    private static final String PREF_LAST_LOGIN_TIME = "last_login_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        requestQueue = Volley.newRequestQueue(this);

        // Get the last stored login timestamp
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        lastLoginTimestamp = sharedPreferences.getLong(PREF_LAST_LOGIN_TIME, 0);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        AuthHelper authHelper = new AuthHelper(this);
        authHelper.validateToken();
        userNameTextView = findViewById(R.id.userName);

        // Load user data
        loadUserData();

        cardInventory = findViewById(R.id.cardInventory);
        cardOrder = findViewById(R.id.cardOrder);
        cardMetrics = findViewById(R.id.cardMetrics);
        cardReceiving = findViewById(R.id.cardReceiving);

        // Show/hide cards based on role
        setMenuVisibility();

        cardInventory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InventoryMonitor.class);
            startActivity(intent);
        });

        cardOrder.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrderHistory.class);
            startActivity(intent);
        });

        cardMetrics = findViewById(R.id.cardProduction);

        cardMetrics.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductionHistory.class);
            startActivity(intent);
        });

        cardReceiving.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReceivingHistory  .class);
            startActivity(intent);
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Check if we're coming back from a recent login
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        long storedLoginTime = sharedPreferences.getLong(PREF_LAST_LOGIN_TIME, 0);
        
        // If the stored login time is newer than what we have, refresh the data
        if (storedLoginTime > lastLoginTimestamp) {
            lastLoginTimestamp = storedLoginTime;
            // Trigger refresh automatically after login
            swipeRefreshLayout.post(() -> {
                swipeRefreshLayout.setRefreshing(true);
                refreshData();
            });
        }
    }

    private void loadUserData() {
        // Get username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest");

        // Display in TextView
        userNameTextView.setText(username);
    }

    private void refreshData() {
        // Show refresh indicator if not already showing
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        // Validate token again
        AuthHelper authHelper = new AuthHelper(this);
        authHelper.validateToken();

        // If you have an API endpoint to get user profile, you can call it here
        // For now, just reload the data from SharedPreferences after a delay to simulate network call
        new android.os.Handler().postDelayed(() -> {
            // Reload user data
            loadUserData();

            // Update menu visibility after refresh
            setMenuVisibility();

            // Hide refresh indicator
            swipeRefreshLayout.setRefreshing(false);
            
            // Show refresh confirmation
            Toast.makeText(MainActivity.this, "Data refreshed", Toast.LENGTH_SHORT).show();
        }, 1000); // 1 second delay
    }

    // Add this method to control menu visibility
    private void setMenuVisibility() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", ""); // default empty
        
        // Add debug log to see what role we're getting
        Log.d("MainActivity", "Current user role: " + role);

        if ("RECEIVING".equalsIgnoreCase(role)) {
            cardInventory.setVisibility(android.view.View.GONE);
            cardOrder.setVisibility(android.view.View.GONE);
            cardMetrics.setVisibility(android.view.View.GONE);
            cardReceiving.setVisibility(android.view.View.VISIBLE);
            Log.d("MainActivity", "Setting visibility for RECEIVING role");
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            cardInventory.setVisibility(android.view.View.VISIBLE);
            cardOrder.setVisibility(android.view.View.VISIBLE);
            cardMetrics.setVisibility(android.view.View.VISIBLE);
            cardReceiving.setVisibility(android.view.View.VISIBLE);
            Log.d("MainActivity", "Setting visibility for ADMIN role");
        } else {
            // Default: hide all except receiving
            cardInventory.setVisibility(android.view.View.GONE);
            cardOrder.setVisibility(android.view.View.GONE);
            cardMetrics.setVisibility(android.view.View.GONE);
            cardReceiving.setVisibility(android.view.View.VISIBLE);
            Log.d("MainActivity", "Setting default visibility, role not recognized: '" + role + "'");
        }
    }

    public void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}