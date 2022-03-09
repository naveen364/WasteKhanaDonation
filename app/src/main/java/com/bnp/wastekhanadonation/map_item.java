package com.bnp.wastekhanadonation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class map_item implements ClusterItem {
    LatLng mposition;

    public map_item(double lat,double lng) {
        this.mposition = new LatLng(lat,lng);
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return null;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }
}
