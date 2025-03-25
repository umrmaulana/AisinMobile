package com.example.aisin.Api;

import android.content.Context;
import android.content.SharedPreferences;

public class ApiHelper {
    public static final String BASE_URL = "https://f86f-36-72-254-201.ngrok-free.app/api/";
    public static final String LOGIN = BASE_URL + "login";
    public static final String REGISTER = BASE_URL + "register";
    public static final String USERS = BASE_URL + "users";
    public static final String LOGOUT = BASE_URL + "logout";
    public static final String VALIDATE_TOKEN = BASE_URL + "validate-token";

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }
}