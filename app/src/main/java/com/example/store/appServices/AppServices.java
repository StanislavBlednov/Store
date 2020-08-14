package com.example.store.appServices;

import com.example.store.data.DataLoader;

public interface AppServices {
    AnimationService getAppAnimations();
    NavigationService getNavService();
    DBService getDb();
    DataLoader getLoader();
}
