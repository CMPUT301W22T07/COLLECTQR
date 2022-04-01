package com.example.collectqr.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.R;
import com.example.collectqr.adapters.LeaderboardRecyclerAdapter;
import com.example.collectqr.data.LeaderboardController;
import com.example.collectqr.utilities.Preferences;
import com.example.collectqr.model.User;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LeaderboardController leaderboardController;
    private RecyclerView leaderboardList;
    private String username;
    private ArrayMap<String, ArrayList<User>> dataLists;
    private ArrayMap<String, LeaderboardRecyclerAdapter> adapterLists;
    private TextView personalUsername;
    private TextView personalScore;
    private TextView personalRank;
    private TabLayout tabs;


    public LeaderboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapScreenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderboardFragment newInstance(String param1, String param2) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * When the fragment is called to be created,
     * use savedInstanceState to create the fragment
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Creates the views for the UI elements,
     * inflates fragment to proper container,
     * and calls helper methods to get data into UI
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return leaderboardView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View leaderboardView = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        // gets signed in user's username from shared preferences
        username = Preferences.loadUserName(leaderboardView.getContext());
        leaderboardController = new LeaderboardController(username);

        // save views as variables
        leaderboardList = leaderboardView.findViewById(R.id.leaderboard_list);
        personalUsername = leaderboardView.findViewById(R.id.personal_username_text);
        personalScore = leaderboardView.findViewById(R.id.personal_score_text);
        personalRank = leaderboardView.findViewById(R.id.personal_rank_text);
        tabs = leaderboardView.findViewById(R.id.leaderboard_tabs);

        dataLists = new ArrayMap<>();
        dataLists.put("most_points", new ArrayList<>());
        dataLists.put("most_codes", new ArrayList<>());
        dataLists.put("best_code", new ArrayList<>());
        dataLists.put("region_best", new ArrayList<>());

        adapterLists = new ArrayMap<>();
        adapterLists.put("most_points", new LeaderboardRecyclerAdapter(dataLists.get("most_points"), "most_points"));
        adapterLists.put("most_codes", new LeaderboardRecyclerAdapter(dataLists.get("most_codes"), "most_codes"));
        adapterLists.put("best_code", new LeaderboardRecyclerAdapter(dataLists.get("best_code"), "best_code"));
        adapterLists.put("region_best", new LeaderboardRecyclerAdapter(dataLists.get("region_best"), "region_best"));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        leaderboardList.setLayoutManager(layoutManager);
        leaderboardList.setAdapter(adapterLists.get("most_points"));

        leaderboardController.downloadData(dataLists, adapterLists, personalScore, personalRank);

        setTabListeners();

        personalUsername.setText(username);

        return leaderboardView;
    }

    public void updatePersonalCard(String currentCategory) {
        for (int i=0; i<dataLists.get(currentCategory).size(); i++) {
            User item = dataLists.get(currentCategory).get(i);
            if (item.getUsername().equals(username)) {
                if (currentCategory.equals("most_points")) {
                    personalScore.setText(item.getStats().get("total_points") + " points");
                    String rankStr = Integer.toString(i+1);
                    personalRank.setText("#" + rankStr);
                } else if (currentCategory.equals("most_codes")) {
                    personalScore.setText(item.getStats().get("num_codes") + " codes");
                    String rankStr = Integer.toString(i+1);
                    personalRank.setText("#" + rankStr);
                } else if (currentCategory.equals("best_code")) {
                    personalScore.setText(item.getStats().get("best_code") + " points");
                    String rankStr = Integer.toString(i+1);
                    personalRank.setText("#" + rankStr);
                } else if (currentCategory.equals("region_best")){
                    personalScore.setText(item.getStats().get("region_points") + " points");
                    String rankStr = Integer.toString(i+1);
                    personalRank.setText("#" + rankStr);
                }
            }
        }
    }

    /**
     * Sets listeners on the tabs and handles the events
     */
    private void setTabListeners() {
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0) {
                    leaderboardList.setAdapter(adapterLists.get("most_points"));
                    leaderboardController.setCurrentCategory("most_points");
                    updatePersonalCard("most_points");
                } else if (tab.getPosition()==1) {
                    leaderboardList.setAdapter(adapterLists.get("most_codes"));
                    leaderboardController.setCurrentCategory("most_codes");
                    updatePersonalCard("most_codes");
                } else if (tab.getPosition()==2) {
                    leaderboardList.setAdapter(adapterLists.get("best_code"));
                    leaderboardController.setCurrentCategory("best_code");
                    updatePersonalCard("best_code");
                } else if (tab.getPosition()==3) {
                    leaderboardList.setAdapter(adapterLists.get("region_best"));
                    leaderboardController.setCurrentCategory("region_best");
                    updatePersonalCard("region_best");
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
    }
}