package com.example.heremaps.Model;

import android.graphics.drawable.Drawable;

public class Category {
    int drawable;
    String name;

    public Category(int drawable, String name) {
        this.drawable = drawable;
        this.name = name;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
