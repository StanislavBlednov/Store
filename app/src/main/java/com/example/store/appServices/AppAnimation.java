package com.example.store.appServices;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class AppAnimation implements AnimationService {
    private Context context;

    public AppAnimation(Context context) {
        this.context = context;
    }

    @Override
    public Animation apply(int anim) {
        return AnimationUtils.loadAnimation(context, anim);
    }
}
