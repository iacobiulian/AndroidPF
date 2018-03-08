package com.example.android.personalfinance_v01.MyClasses;

import java.io.Serializable;

/**
 * Created by iacob on 24-Feb
 */

public class Category implements Serializable {
    private String name;
    private int iconID;

    Category(String name, int iconId) {
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
