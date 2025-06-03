package com.example.aisin;

<<<<<<< HEAD
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
=======
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

<<<<<<< HEAD
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aisin.Api.ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button loginButton;
    RequestQueue requestQueue;
=======
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.aisin.Api.ApiHelper;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    EditText email, password;
    Button loginButton;
    RequestQueue requestQueue;
    private ProgressDialog progressDialog;
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
<<<<<<< HEAD
=======
        
        // Initialize UI components
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnLogin);
        requestQueue = Volley.newRequestQueue(this);
<<<<<<< HEAD
=======
        
        // Setup the progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        // Check if already logged in
        if (ApiHelper.isLoggedIn(this)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

<<<<<<< HEAD
    private void login() {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("email", email.getText().toString());
            requestData.put("password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ApiHelper.LOGIN, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ambil token dari respons API
                            String token = response.getString("token");

                            // Simpan token dan data pengguna ke SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", token);
                            editor.putBoolean("isLoggedIn", true); // Tandai bahwa pengguna sudah login
                            editor.apply();

                            // Pindah ke MainActivity
                            Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish(); // Tutup LoginActivity agar tidak bisa kembali ke halaman login
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Gagal memproses respons", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Login Gagal", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }
}
=======
    private boolean validateInput() {
        boolean isValid = true;
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        // Validate email
        if (TextUtils.isEmpty(emailInput)) {
            email.setError("Email tidak boleh kosong");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Format email tidak valid");
            isValid = false;
        } else {
            email.setError(null);
        }

        // Validate password
        if (TextUtils.isEmpty(passwordInput)) {
            password.setError("Password tidak boleh kosong");
            isValid = false;
        } else {
            password.setError(null);
        }

        return isValid;
    }

    private void login() {
        if (!validateInput()) {
            return;
        }

        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        
        // Show loading state
        progressDialog.show();
        loginButton.setEnabled(false);
        
        Log.d(TAG, "Attempting login with email: " + emailInput);
        
        ApiHelper.loginUser(this, emailInput, passwordInput, new ApiHelper.LoginCallback() {
            @Override
            public void onSuccess(String token) {
                // Reset UI state
                progressDialog.dismiss();
                loginButton.setEnabled(true);
                
                Log.d(TAG, "Login successful, token: " + token.substring(0, Math.min(10, token.length())) + "...");
                
                // Reset button state
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                
                // Save current timestamp to indicate recent login
                SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("last_login_time", System.currentTimeMillis());
                editor.apply();
                
                // Show success message and navigate to MainActivity
                Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish(); // Close LoginActivity
            }
            
            @Override
            public void onError(String errorMessage) {
                // Reset UI state
                progressDialog.dismiss();
                loginButton.setEnabled(true);
                
                Log.e(TAG, "Login error: " + errorMessage);
                
                // Show error message
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
>>>>>>> a57a957 (menampilkan data dari inventory sampai receiving)
