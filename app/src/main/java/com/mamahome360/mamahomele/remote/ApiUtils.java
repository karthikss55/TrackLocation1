package com.mamahome360.mamahomele.remote;

import android.content.Context;

public class ApiUtils {
    public static final String BASE_URL = "https://mamahome360.com/webapp/api/";
    public static UserService getUserService(Context nContext){
        return com.mamahome360.mamahomele.remote.RetrofitClient.getClient(BASE_URL,nContext).create(UserService.class);
    }
}
