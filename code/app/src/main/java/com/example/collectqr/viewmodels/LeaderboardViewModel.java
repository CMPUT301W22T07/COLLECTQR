package com.example.collectqr.viewmodels;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.collectqr.data.LocationRepository;

import org.apache.commons.lang3.mutable.Mutable;

/**
 * A view model for the leaderboard, primarily for location.
 * For attribution and full implementation details:
 * @see com.example.collectqr.data.LocationRepository
 */
public class LeaderboardViewModel extends AndroidViewModel {

    // Class variables
    private LiveData<Location> locationLiveData;

    public LeaderboardViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new LocationRepository(application);
    }

    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }
}
