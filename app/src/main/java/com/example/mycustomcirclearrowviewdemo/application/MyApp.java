package com.example.mycustomcirclearrowviewdemo.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by pc on 2017/11/18.
 */

public class MyApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
