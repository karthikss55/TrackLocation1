package com.mamahome360.mamahomele.remote;

import android.content.Context;

import com.mamahome360.mamahomele.utils.selfSigningClientBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginRertofitClient {
    private static Retrofit retrofit = null;

    // OkHttpClient client = new OkHttpClient();
    public static Retrofit getLoginClient(String url, Context mContext){
        if(retrofit == null){
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(selfSigningClientBuilder.createClient(mContext))
                    .build();
        }
        return retrofit;
    }
}
