package com.example.store.utils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import io.reactivex.Observable;

public class ObservableFieldToRx {

    private ObservableFieldToRx() {
    }

    public static <T> Observable<T> observable(@NonNull final ObservableField<T> observableField) {
        return Observable.create(emitter -> {
            final androidx.databinding.Observable.OnPropertyChangedCallback callback = new androidx.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(androidx.databinding.Observable dataBindingObservable, int propertyId) {
                    if (dataBindingObservable == observableField) {
                        emitter.onNext(observableField.get());
                    }
                }
            };
            observableField.addOnPropertyChangedCallback(callback);
            emitter.setCancellable(() -> observableField.removeOnPropertyChangedCallback(callback));
        });
    }
}
