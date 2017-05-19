package com.gonzasestopal.petshelter;

/**
 * Created by gonza on 10/05/17.
 */

public class Pet {
    /**
     * Pet class used in the first version of the app, used as ArrayList in
     * {@link MyAdapter} for displaying results inside a {@link android.widget.ListView},
     */
    private String mName;
    private String mBreed;
    private int mGender;
    private int mWeight;

    public Pet(String name, String breed, int gender, int weight) {
        mName = name;
        mBreed = breed;
        mGender = gender;
        mWeight = weight;
    }

    public String getName() {
        return mName;
    }

    public String getBreed() {
        return mBreed;
    }

    public int getGender() {
        return mGender;
    }

    public int getWeight() {
        return mWeight;
    }
}
