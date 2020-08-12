package com.example.store.utils;

import com.example.store.R;

public enum MenuItemVisibility {
    ADD(new int[]{ R.id.toolbarMenu__add}, new int[]{ R.id.toolbarMenu__save}),
    SAVE(new int[]{ R.id.toolbarMenu__save}, new int[]{ R.id.toolbarMenu__add}),
    HIDE_ADD_SAVE(new int[]{}, new int[]{ R.id.toolbarMenu__add, R.id.toolbarMenu__save});

    private int[] show;
    private int[] hide;

    MenuItemVisibility(int[] show, int[] hide) {
        this.show = show;
        this.hide = hide;
    }

    public int[] getShow() {
        return show;
    }

    public int[] getHide() {
        return hide;
    }
}
