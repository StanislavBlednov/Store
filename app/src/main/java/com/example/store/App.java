package com.example.store;

import android.app.Application;

import com.example.store.appServices.AnimationService;
import com.example.store.appServices.AppAnimation;
import com.example.store.appServices.AppNavigation;
import com.example.store.appServices.AppServices;
import com.example.store.appServices.DBRepository;
import com.example.store.appServices.DBService;
import com.example.store.appServices.NavigationService;

public class App extends Application implements AppServices {
    private AnimationService animationService;
    private NavigationService navigationService;
    private DBRepository dbRepository;

    @Override
    public AnimationService getAppAnimations() {
        if (animationService == null){
            animationService = new AppAnimation(getApplicationContext());
        }
        return animationService;
    }

    @Override
    public NavigationService getNavService() {
        if (navigationService == null){
            navigationService = new AppNavigation();
        }
        return navigationService;
    }

    @Override
    public DBService getDb() {
        if (dbRepository == null){
            dbRepository = new DBRepository(getApplicationContext());
        }
        return dbRepository;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (dbRepository != null) dbRepository.dispose();
    }
}
