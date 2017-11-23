package com.example.mycustomcirclearrowviewdemo.model;

import com.example.mycustomcirclearrowviewdemo.apiservice.ApiService;
import com.example.mycustomcirclearrowviewdemo.bean.InfoBean;
import com.example.mycustomcirclearrowviewdemo.utils.OnNetListener;
import com.example.mycustomcirclearrowviewdemo.utils.RetrofitUtils;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by pc on 2017/11/18.
 */

public class InfoModel {
    public void infoGetData(final OnNetListener onNetListener){
        /*HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl("http://result.eolinker.com/")
                .build();
         ApiService apiService = retrofit.create(ApiService.class);*/
        ApiService apiService = RetrofitUtils.getInstance().getApiService("http://result.eolinker.com/", ApiService.class);
        Observable<InfoBean> observable = apiService.getinfo();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(InfoBean infoBean) {
                        onNetListener.onSuccess(infoBean);
                    }
                });

    }
}
