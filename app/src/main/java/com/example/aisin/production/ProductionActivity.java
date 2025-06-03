package com.example.aisin.production;

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

public class ProductionActivity extends AppCompatActivity {
    private CardView cardMakeProduction, cardProductionHistory;
    private ImageButton backButton;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_production);
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
        cardMakeProduction = findViewById(R.id.cardMakeProduction);
        cardProductionHistory = findViewById(R.id.cardProductionHistory);
        backButton = findViewById(R.id.backButton);
        userNameTextView = findViewById(R.id.userName);
    }
    
    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());
        
        // Production History card
        cardProductionHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ProductionActivity.this, ProductionHistory.class);
            startActivity(intent);
        });
        
        // Make Production card
        cardMakeProduction.setOnClickListener(v -> {
            // This would navigate to a Make Production screen
            Toast.makeText(ProductionActivity.this, "Make Production feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void displayUsername() {
        if (userNameTextView != null) {
            String username = ApiHelper.getUsername(this);
            userNameTextView.setText(username);
        }
    }
}
