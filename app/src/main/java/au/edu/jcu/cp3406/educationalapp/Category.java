package au.edu.jcu.cp3406.educationalapp;

import androidx.annotation.NonNull;

public class Category {
    public static final int IT = 1;
    public static final int SCIENCE = 2;
    public static final int JAPANESE = 3;

    // TODO: 24/05/2021 Set Quiz Question Categories.

    private int id;
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
