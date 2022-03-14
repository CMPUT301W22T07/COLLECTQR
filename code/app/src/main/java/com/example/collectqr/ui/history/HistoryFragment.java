package com.example.collectqr.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View rootView;

    private String username = "realishUser"; // TODO: make username be retrieved from a parameter

    private HistoryController controller;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HistoryAdapter adapter;
    private ArrayList<HistoryItem> data;
    private BottomSheetDialog sortSheet;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        https://stackoverflow.com/a/31096444
        StackOverflow, Author: The Dude
         */
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
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