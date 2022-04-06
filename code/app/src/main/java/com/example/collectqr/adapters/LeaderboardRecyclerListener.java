package com.example.collectqr.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Listener for scrolling events in the leaderboard's Recycler View.
 *
 * Source:
 * https://mzgreen.github.io/2015/02/15/How-to-hideshow-Toolbar-when-list-is-scroling(part1)/
 */
public abstract class LeaderboardRecyclerListener extends RecyclerView.OnScrollListener {

    // Constants
    private static final int HIDE_THRESHOLD = 20;
    private static final int SHOW_THRESHOLD = -10;
    private int scrolledDistance = 0;
    private boolean rankBarVisible = true;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        /* If user scrolls down (finger swipes up), hide the persistent rank bar, else
           if user scrolls up, show it. */
        if (scrolledDistance > HIDE_THRESHOLD && rankBarVisible) {
            onHide();
            rankBarVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < SHOW_THRESHOLD && !rankBarVisible) {
            onShow();
            rankBarVisible = true;
            scrolledDistance = 0;
        }

        if ((rankBarVisible && dy > 0) || (!rankBarVisible && dy < 0)) {
            scrolledDistance += dy;
        }
    }


    public abstract void onHide();
    public abstract void onShow();
}
