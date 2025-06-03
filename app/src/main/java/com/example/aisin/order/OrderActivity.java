package com.example.aisin.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aisin.Api.ApiHelper;
import com.example.aisin.R;

public class OrderActivity extends AppCompatActivity {
    private CardView cardPlaceOrder, cardOrderHistory;
    private ImageButton backButton;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize views
        initViews();
        
        // Set up click listeners
        setupClickListeners();
        
        // Display username from SharedPreferences
        displayUsername();
    }
    
    private void initViews() {
        cardPlaceOrder = findViewById(R.id.cardPlaceOrder);
        cardOrderHistory = findViewById(R.id.cardOrderHistory);
        backButton = findViewById(R.id.backButton);
        userNameTextView = findViewById(R.id.userName);
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        cardOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, OrderHistory.class);
            startActivity(intent);
        });
        
        // Here you would also handle the Place Order navigation
        cardPlaceOrder.setOnClickListener(v -> {
            // Intent for place order screen when ready
            Toast.makeText(OrderActivity.this, "Place Order feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void displayUsername() {
        if (userNameTextView != null) {
            String username = ApiHelper.getUsername(this);
            userNameTextView.setText(username);
        }
    }
}
