package com.example.aisin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.aisin.Api.AuthHelper;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    Button btnLogout;
    TextView userNameTextView;
    CardView cardInventory, cardOrder, cardMetrics, cardReceiving;

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

        AuthHelper authHelper = new AuthHelper(this);
        authHelper.validateToken();
        userNameTextView = findViewById(R.id.userName);

        // Ambil username dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest");

        // Tampilkan di TextView
        userNameTextView.setText(username);

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
            Intent intent = new Intent(MainActivity.this, MetricsActivity.class);
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