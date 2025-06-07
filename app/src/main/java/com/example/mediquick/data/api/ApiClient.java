package com.example.mediquick.data.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Cliente con autenticación (para endpoints protegidos)
    public static Retrofit getAuthenticatedClient(String baseUrl, String token) {
        // Interceptor de logging para debug - MUY IMPORTANTE para ver qué se envía
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            android.util.Log.d("HTTP_LOG", message);
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(token))
                .addInterceptor(loggingInterceptor) // Añadir logging DESPUÉS del auth
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Cliente sin autenticación (para login, registro, etc.)
    public static Retrofit getUnauthenticatedClient(String baseUrl) {
        // Logging también para requests no autenticados
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}