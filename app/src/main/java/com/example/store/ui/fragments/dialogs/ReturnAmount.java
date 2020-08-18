package com.example.store.ui.fragments.dialogs;

public class ReturnAmount {
    private final int pcs;
    private final DialogButtons db;

    private ReturnAmount(int pcs, DialogButtons db) {
        this.pcs = pcs;
        this.db = db;
    }

    public static ReturnAmount of(int pcs, DialogButtons db) {
        return new ReturnAmount(pcs, db);
    }

    public static ReturnAmount empty(){
        return new ReturnAmount(0, DialogButtons.CANCEL);
    }

    public int getPcs() {
        return pcs;
    }

    public DialogButtons getDb() {
        return db;
    }
}
