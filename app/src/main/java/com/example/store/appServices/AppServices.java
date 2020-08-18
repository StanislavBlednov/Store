package com.example.store.appServices;

import android.content.SharedPreferences;

import com.example.store.data.DataLoader;

public interface AppServices {
    AnimationService getAppAnimations();
    NavigationService getNavService();
    ProductRepository getProductRepo();
    DataLoader getLoader();
    SharedPreferences getPreference();
}
