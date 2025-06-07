package com.example.mediquick.data.api;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private final String token;

    public AuthInterceptor(String token) {
        this.token = token;
        Log.d(TAG, "Token inicializado: " + (token != null ? "presente" : "null"));
        if (token != null) {
            Log.d(TAG, "Token length: " + token.length());
            Log.d(TAG, "Token preview: " + token.substring(0, Math.min(20, token.length())) + "...");
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Log.d(TAG, "=== INTERCEPTING REQUEST ===");
        Log.d(TAG, "Original URL: " + original.url());
        Log.d(TAG, "Method: " + original.method());
        Log.d(TAG, "Original Headers: " + original.headers());

        String fullToken = "Bearer " + token;
        Log.d(TAG, "Adding Authorization header: " + fullToken.substring(0, Math.min(30, fullToken.length())) + "...");

        Request request = original.newBuilder()
                .header("Authorization", fullToken)
                .method(original.method(), original.body())
                .build();

        Log.d(TAG, "Final Headers: " + request.headers());
        Log.d(TAG, "=== END INTERCEPTOR ===");

        Response response = chain.proceed(request);

        Log.d(TAG, "=== RESPONSE RECEIVED ===");
        Log.d(TAG, "Response Code: " + response.code());
        Log.d(TAG, "Response Headers: " + response.headers());

        return response;
    }
}