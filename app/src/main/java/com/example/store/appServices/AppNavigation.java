package com.example.store.appServices;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import io.reactivex.Observable;

public class AppNavigation implements NavigationService{
    private NavController.OnDestinationChangedListener listener;
    private Observable<Integer> navListener;

    public AppNavigation() {
        navListener = Observable.create(emitter ->
            listener = (NavController controller, NavDestination destination, Bundle arguments) ->
                    emitter.onNext(destination.getId())
        );
    }

    @Override
    public NavController.OnDestinationChangedListener getNavListener() {
        return listener;
    }

    @Override
    public Observable<Integer> getDestination() {
        return navListener;
    }
}
