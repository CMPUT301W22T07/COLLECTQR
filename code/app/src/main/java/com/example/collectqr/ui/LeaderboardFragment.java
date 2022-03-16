package com.example.collectqr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ApplicationProvider;

import com.example.collectqr.R;
import com.example.collectqr.adapters.LeaderboardAdapter;
import com.example.collectqr.data.LeaderboardController;
import com.example.collectqr.utilities.Preferences;
import com.example.collectqr.model.User;

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
    private ListView leaderboardList;
    private ArrayList<User> usersList;
    private LeaderboardAdapter usersAdapter;
    private TextView personalUsername;
    private TextView personalScore;
    private TextView personalRank;


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
        String username = Preferences.loadPreferences(leaderboardView.getContext());
        leaderboardController = new LeaderboardController(username);

        // save views as variables
        leaderboardList = leaderboardView.findViewById(R.id.leaderboard_list);
        personalUsername = leaderboardView.findViewById(R.id.personal_username_text);
        personalScore = leaderboardView.findViewById(R.id.personal_score_text);
        personalRank = leaderboardView.findViewById(R.id.personal_rank_text);

        usersList = new ArrayList<>();
        usersAdapter = new LeaderboardAdapter(container.getContext(), usersList);
        leaderboardList.setAdapter(usersAdapter);
        leaderboardController.downloadData(usersList, usersAdapter, personalScore, personalRank);
        personalUsername.setText(username);

        return leaderboardView;
    }
}