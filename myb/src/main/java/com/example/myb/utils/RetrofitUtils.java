package com.example.myb.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pc on 2017/11/18.
 */

public class RetrofitUtils {
    private static RetrofitUtils retrofitUtils;
    public RetrofitUtils(){

    }
    public static RetrofitUtils getInstance(){
        if(retrofitUtils==null){
            synchronized (RetrofitUtils.class){
                if(retrofitUtils==null){
                    retrofitUtils = new RetrofitUtils();
                }
            }
        }
        return retrofitUtils;
    }

    //封装Rettrofit
    private static Retrofit retrofit;
    public static synchronized Retrofit getRetrofit(String url){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .baseUrl(url)
                    .build();
        }
        return retrofit;
    }

    public <T>T getApiService(String url,Class<T> clazz){
        Retrofit retrofit = getRetrofit(url);
        return retrofit.create(clazz);
    }
}
