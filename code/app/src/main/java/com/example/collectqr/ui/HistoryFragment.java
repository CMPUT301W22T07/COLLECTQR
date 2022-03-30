package com.example.collectqr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.data.HistoryController;
import com.example.collectqr.utilities.Preferences;
import com.example.collectqr.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass. Displays the history screen.
 */
public class HistoryFragment extends Fragment {
    private View rootView;
    private HistoryController controller;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BottomSheetDialog sortSheet;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Uses a Bundle to create an instance of a fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates the history Fragment view and handles the views within the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        https://stackoverflow.com/a/31096444
        StackOverflow, Author: The Dude
         */
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        String username = Preferences.loadUserName(rootView.getContext());
        controller = new HistoryController(username);

        TextView totalPoints = rootView.findViewById(R.id.history_total_points);
        TextView numCodes = rootView.findViewById(R.id.history_num_codes);
        controller.setStatsBarData(totalPoints, numCodes);

        /*
        https://youtu.be/17NbUcEts9c
        YouTube, Author: Coding in Flow
         */
        recyclerView = rootView.findViewById(R.id.history_qr_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(controller.getAdapter());

        createSortSheetDialog();
        FloatingActionButton fab = rootView.findViewById(R.id.sort_history_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortSheet.show();
            }
        });

        return rootView;
    }

    /**
     * Creates a SheetDialog containing the UI to sort the items displayed in the RecyclerView
     * Sets up onClickListeners to handle user input
     */
    private void createSortSheetDialog() {
        /*
        YouTube video
        Author: Code Vendanam
        https://youtu.be/sODN0SMiUhk

        Author: Joseph Chege
        https://www.section.io/engineering-education/bottom-sheet-dialogs-using-android-studio/
         */
        sortSheet = new BottomSheetDialog(rootView.getContext());
        sortSheet.setContentView(R.layout.history_sort_fragment);
        TextView byPointsDescend = sortSheet.findViewById(R.id.history_sort_points_descend);
        TextView byPointsAscend = sortSheet.findViewById(R.id.history_sort_points_ascend);
        TextView byDateDescend = sortSheet.findViewById(R.id.history_sort_date_descend);
        byPointsAscend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.sortQrData("points_ascend");
                sortSheet.dismiss();
            }
        });
        byPointsDescend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.sortQrData("points_descend");
                sortSheet.dismiss();
            }
        });
        byDateDescend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.sortQrData("date_descend");
                sortSheet.dismiss();
            }
        });
    }
}