package com.positivemind.newsapp;

import android.app.Application;

import com.positivemind.newsapp.di.AppComponent;
import com.positivemind.newsapp.di.AppModule;
import com.positivemind.newsapp.di.DaggerAppComponent;

/**
 * Created by Rajeev Tomar on 21,December,2019
 */
public class NewsApplication extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        // init appcomponent
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
