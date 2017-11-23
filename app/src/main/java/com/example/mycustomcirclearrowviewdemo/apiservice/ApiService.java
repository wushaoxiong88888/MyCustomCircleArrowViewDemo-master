package com.example.mycustomcirclearrowviewdemo.apiservice;

import com.example.mycustomcirclearrowviewdemo.bean.InfoBean;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by pc on 2017/11/18.
 */

public interface ApiService {
    /**
     *http://result.eolinker.com/
     * iYXEPGn4e9c6dafce6e5cdd23287d2bb136ee7e9194d3e9
     * ?uri=vedio
     *
     */
    @GET("iYXEPGn4e9c6dafce6e5cdd23287d2bb136ee7e9194d3e9?uri=vedio")
    Observable<InfoBean> getinfo();
}
