package com.example.collectqr.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.R;

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

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<HistoryItem> qrHistoryList;

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
        qrHistoryList = new ArrayList<>();
        qrHistoryList.add(new HistoryItem(100, R.drawable.ic_baseline_map, "example hash"));
        qrHistoryList.add(new HistoryItem(200, R.drawable.ic_baseline_history, "example hash"));
        qrHistoryList.add(new HistoryItem(300, R.drawable.ic_baseline_leaderboard, "example hash"));
        qrHistoryList.add(new HistoryItem(100, R.drawable.ic_baseline_map, "example hash"));
        qrHistoryList.add(new HistoryItem(200, R.drawable.ic_baseline_history, "example hash"));
        qrHistoryList.add(new HistoryItem(300, R.drawable.ic_baseline_leaderboard, "example hash"));
        qrHistoryList.add(new HistoryItem(100, R.drawable.ic_baseline_map, "example hash"));
        qrHistoryList.add(new HistoryItem(200, R.drawable.ic_baseline_history, "example hash"));
        qrHistoryList.add(new HistoryItem(300, R.drawable.ic_baseline_leaderboard, "example hash"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        https://stackoverflow.com/a/31096444
        StackOverflow, Author: The Dude
         */

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = rootView.findViewById(R.id.history_qr_recycler_view);

        //recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), 2);
        adapter = new HistoryAdapter(qrHistoryList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



        // Inflate the layout for this fragment
        return rootView;

    }
}