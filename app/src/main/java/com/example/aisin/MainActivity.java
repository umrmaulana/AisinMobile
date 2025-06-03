package com.example.aisin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
<<<<<<< HEAD
=======
import android.widget.Toast;
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
<<<<<<< HEAD
=======
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.aisin.Api.AuthHelper;
<<<<<<< HEAD
=======
import com.example.aisin.inventory.InventoryActivity;
import com.example.aisin.order.OrderActivity;
import com.example.aisin.production.ProductionActivity;
import com.example.aisin.receiving.ReceivingActivity;
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    Button btnLogout;
    TextView userNameTextView;
    CardView cardInventory, cardOrder, cardMetrics, cardReceiving;
<<<<<<< HEAD
=======
    SwipeRefreshLayout swipeRefreshLayout;
    private long lastLoginTimestamp = 0;
    private static final String PREF_LAST_LOGIN_TIME = "last_login_time";
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)

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

<<<<<<< HEAD
=======
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

>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)
        AuthHelper authHelper = new AuthHelper(this);
        authHelper.validateToken();
        userNameTextView = findViewById(R.id.userName);

<<<<<<< HEAD
        // Ambil username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest");

        // Tampilkan di TextView
        userNameTextView.setText(username);
=======
        // Load user data
        loadUserData();
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)

        cardInventory = findViewById(R.id.cardInventory);
        cardInventory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
            startActivity(intent);
        });

        cardOrder = findViewById(R.id.cardOrder);
        cardOrder.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            startActivity(intent);
        });

        cardMetrics = findViewById(R.id.cardMetrics);
        cardMetrics.setOnClickListener(v -> {
<<<<<<< HEAD
            Intent intent = new Intent(MainActivity.this, MetricsActivity.class);
=======
            Intent intent = new Intent(MainActivity.this, ProductionActivity.class);
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)
            startActivity(intent);
        });

        cardReceiving = findViewById(R.id.cardReceiving);
        cardReceiving.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReceivingActivity.class);
            startActivity(intent);
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());
    }

<<<<<<< HEAD
=======
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

            // Hide refresh indicator
            swipeRefreshLayout.setRefreshing(false);
            
            // Show refresh confirmation
            Toast.makeText(MainActivity.this, "Data refreshed", Toast.LENGTH_SHORT).show();
        }, 1000); // 1 second delay
    }

>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)
    public void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)
