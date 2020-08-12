package com.example.store.appServices;

import androidx.navigation.NavController;
import io.reactivex.Observable;

public interface NavigationService {
    NavController.OnDestinationChangedListener getNavListener();
    Observable<Integer> getDestination();
}
