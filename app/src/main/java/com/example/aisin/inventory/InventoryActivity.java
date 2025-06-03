package com.example.aisin.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aisin.Api.ApiHelper;
import com.example.aisin.R;

public class InventoryActivity extends AppCompatActivity {

    private CardView cardInventoryMonitor;
    private ImageButton backButton;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize views
        initViews();
        
        // Set click listeners
        setupClickListeners();
        
        // Display username from SharedPreferences
        displayUsername();
    }
    
    private void initViews() {
        cardInventoryMonitor = findViewById(R.id.cardInventoryMonitor);
        backButton = findViewById(R.id.backButton);
        userNameTextView = findViewById(R.id.userName);
    }
    
    private void setupClickListeners() {
        // Back button
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        
        // Navigate to Inventory Monitor
        if (cardInventoryMonitor != null) {
            cardInventoryMonitor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create and start intent to open InventoryMonitor
                    Intent intent = new Intent(InventoryActivity.this, InventoryMonitor.class);
                    startActivity(intent);
                }
            });
        }
    }
    
    private void displayUsername() {
        if (userNameTextView != null) {
            String username = ApiHelper.getUsername(this);
            userNameTextView.setText(username);
        }
    }
}
