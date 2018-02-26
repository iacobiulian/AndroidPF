package com.example.android.personalfinance_v01.MyClasses;

import java.io.Serializable;

/**
 * Created by iacob on 24-Feb-18.
 */

public class Category implements Serializable{
    private String name;
    private int iconID;

    public Category(String name, int iconId) {
        this.name = name;
        this.iconID = iconId;
    }

    public String getName() {
        return name;
    }

    public int getIconID() {
        return iconID;
    }

}
