package com.example.aisin.Api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ApiHelper {
    private static final String TAG = "ApiHelper";
    public static final String BASE_URL = "https://aisin.umrmaulana.my.id/api/";
    public static final String LOGIN = BASE_URL + "login";
    public static final String REGISTER = BASE_URL + "register";
    public static final String USERS = BASE_URL + "users";
    public static final String LOGOUT = BASE_URL + "logout";
    public static final String VALIDATE_TOKEN = BASE_URL + "validate-token";

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }
    
    // Check if user is logged in
    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
    
    // Create a custom HurlStack to work around SSL issues on some devices
    private static HurlStack createHurlStack() {
        return new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection connection = super.createConnection(url);
                
                if (connection instanceof HttpsURLConnection) {
                    try {
                        // Create a trust manager that does not validate certificate chains
                        TrustManager[] trustAllCerts = new TrustManager[] {
                            new X509TrustManager() {
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }
                                public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                                }
                                public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                                }
                            }
                        };

                        // Install the all-trusting trust manager
                        SSLContext sc = SSLContext.getInstance("TLS");
                        sc.init(null, trustAllCerts, new java.security.SecureRandom());
                        ((HttpsURLConnection) connection).setSSLSocketFactory(sc.getSocketFactory());
                        ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
                    } catch (Exception e) {
                        Log.e(TAG, "Error setting up SSL connection", e);
                    }
                }
                
                return connection;
            }
        };
    }
    
    // Create a RequestQueue with our custom HurlStack
    private static RequestQueue createRequestQueue(Context context) {
        return Volley.newRequestQueue(context, createHurlStack());
    }
    
    /**
     * Get username from SharedPreferences
     * @param context The application or activity context
     * @return The stored username or a default value
     */
    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "Guest");
    }
    
    // Login user
    public static void loginUser(Context context, String email, String password, 
                                 final LoginCallback callback) {
        RequestQueue requestQueue = createRequestQueue(context);
        
        JSONObject requestData = new JSONObject();
        try {
            // Ensure we're using the exact field names expected by the API
            requestData.put("email", email);
            requestData.put("password", password);
            
            // Log the request data for debugging (remove in production)
            Log.d(TAG, "Login Request: " + requestData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Failed to create request");
            return;
        }
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "Login Response: " + response.toString());
                            String token = response.getString("token");
                            
                            // Save to SharedPreferences
                            SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", token);
                            editor.putBoolean("isLoggedIn", true);
                            
                            // Save username from response if available
                            if (response.has("user")) {
                                try {
                                    JSONObject user = response.getJSONObject("user");
                                    if (user.has("name")) {
                                        editor.putString("username", user.getString("name"));
                                    } else if (user.has("username")) {
                                        editor.putString("username", user.getString("username"));
                                    } else {
                                        editor.putString("username", email.split("@")[0]); // Default to email username part
                                    }
                                } catch (JSONException e) {
                                    editor.putString("username", "admin"); // Fallback
                                }
                            } else {
                                editor.putString("username", "admin"); // Default value
                            }
                            
                            editor.apply();
                            
                            callback.onSuccess(token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Failed to parse response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Login failed";
                        
                        // Log the full error for debugging
                        Log.e(TAG, "Login Error", error);
                        
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            Log.e(TAG, "Status Code: " + statusCode);
                            
                            if (statusCode == 401) {
                                errorMsg = "Email atau password salah";
                            }
                            
                            if (error.networkResponse.data != null) {
                                try {
                                    String body = new String(error.networkResponse.data, "UTF-8");
                                    Log.e(TAG, "Error Response Body: " + body);
                                    
                                    JSONObject errorBody = new JSONObject(body);
                                    if (errorBody.has("message")) {
                                        errorMsg = errorBody.getString("message");
                                    } else if (errorBody.has("error")) {
                                        errorMsg = errorBody.getString("error");
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing error response", e);
                                }
                            }
                        } else if (error.getCause() != null) {
                            // Network connectivity issues
                            errorMsg = "Koneksi gagal: " + error.getCause().getMessage();
                        }
                        
                        callback.onError(errorMsg);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
            
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // Log successful responses for debugging
                if (response.statusCode >= 200 && response.statusCode < 300) {
                    try {
                        String jsonString = new String(response.data, "UTF-8");
                        Log.d(TAG, "Raw Success Response: " + jsonString);
                    } catch (Exception e) {
                        Log.e(TAG, "Error logging response", e);
                    }
                }
                return super.parseNetworkResponse(response);
            }
        };
        
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                0,     // no retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        
        requestQueue.add(request);
    }
    
    // Validate token
    public static void validateToken(Context context, final TokenValidationCallback callback) {
        // ... existing code ...
    }
    
    // Logout user
    public static void logoutUser(Context context, final LogoutCallback callback) {
        // ... existing code ...
    }
    
    // Clear user session data
    private static void clearUserSession(Context context) {
        // ... existing code ...
    }
    
    // Callback interfaces
    public interface LoginCallback {
        void onSuccess(String token);
        void onError(String errorMessage);
    }
    
    public interface TokenValidationCallback {
        void onValid();
        void onInvalid(String errorMessage);
    }
    
    public interface LogoutCallback {
        void onSuccess();
    }
}
