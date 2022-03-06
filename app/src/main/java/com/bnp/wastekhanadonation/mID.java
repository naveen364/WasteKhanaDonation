package com.bnp.wastekhanadonation;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class mID {
    @Exclude
    public String mID;
    public <T extends mID> T withId(@NonNull final String id){
        this.mID = id;
        return (T)this;
    }
}
