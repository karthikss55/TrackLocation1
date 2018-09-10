package com.mamahome360.mamahomele.remote;

import android.content.Context;

import com.mamahome360.mamahomele.utils.selfSigningClientBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    // OkHttpClient client = new OkHttpClient();
    public static Retrofit getClient(String url, Context mContext){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(selfSigningClientBuilder.createClient(mContext))
                    .build();
        }
        return retrofit;
    }
}