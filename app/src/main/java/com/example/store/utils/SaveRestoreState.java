package com.example.store.utils;

import android.os.Bundle;

public interface SaveRestoreState {
    void save(Bundle bundle);
    void restore(Bundle bundle);
}
