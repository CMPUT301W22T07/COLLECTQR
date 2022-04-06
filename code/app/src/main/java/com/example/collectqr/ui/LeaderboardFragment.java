package com.example.collectqr.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.R;
import com.example.collectqr.adapters.LeaderboardRecyclerAdapter;
import com.example.collectqr.adapters.LeaderboardRecyclerListener;
import com.example.collectqr.data.LeaderboardController;
import com.example.collectqr.utilities.Preferences;
import com.example.collectqr.model.User;
import com.example.collectqr.viewmodels.LeaderboardViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.util.ArrayList;
import java.util.Locale;

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

    private FusedLocationProviderClient fusedLocationClient;
    private View rootView;
    private LeaderboardController leaderboardController;
    private RecyclerView leaderboardList;
    private String username;
    private ArrayMap<String, ArrayList<User>> dataLists;
    private ArrayMap<String, LeaderboardRecyclerAdapter> adapterLists;
    private LinearLayout persistentPlayerInfo;
    private TextView personalUsername;
    private TextView personalScore;
    private TextView personalRank;
    private TabLayout tabs;
    private LeaderboardViewModel viewModel;

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

        // Reference to view model for getting location data
        viewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);
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
        rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        // gets signed in user's username from shared preferences
        username = Preferences.loadUserName(rootView.getContext());
        leaderboardController = new LeaderboardController(username);

        // save views as variables
        leaderboardList = rootView.findViewById(R.id.leaderboard_list);
        personalUsername = rootView.findViewById(R.id.personal_username_text);
        personalScore = rootView.findViewById(R.id.personal_score_text);
        personalRank = rootView.findViewById(R.id.personal_rank_text);
        tabs = rootView.findViewById(R.id.leaderboard_tabs);

        //get access to persistent UI element
        persistentPlayerInfo = rootView.findViewById(R.id.persistent_user_score);

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

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.user_search) {
                    setUpSearchView(menuItem);
                }
                return false;
            }
        });

        persistentPlayerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //redirect the user to their own profile if the bottom persistent layout is clicked
                NavController navController = Navigation.findNavController(getView());
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                navController.navigate(R.id.navigation_user_profile, bundle);
            }
        });

        personalUsername.setText(username);

        setupLeaderboard();

        return rootView;
    }


    /**
     * Set the layout manager for the Recycler View and download data upon a non-null location.
     */
    private void setupLeaderboard() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        leaderboardList.setLayoutManager(layoutManager);
        leaderboardList.setAdapter(adapterLists.get("most_points"));
        setLeaderboardScrollListener(leaderboardList);

        // Observe for a non-null location, then download data
        viewModel.getLocationLiveData().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                leaderboardController.downloadData(dataLists,
                        adapterLists,
                        personalScore,
                        personalRank,
                        location.getLatitude(),
                        location.getLongitude());

                setOnRecyclerItemClickListener();
                setTabListeners();
            }
        });

    }


    /**
     * Set a listener for scrolling events of the passed in RecyclerView.
     * Source:
     * https://mzgreen.github.io/2015/02/15/How-to-hideshow-Toolbar-when-list-is-scroling(part1)/
     *
     * @param leaderboardList The Recycler View to attach a listener to
     */
    private void setLeaderboardScrollListener(RecyclerView leaderboardList) {
        leaderboardList.setOnScrollListener(new LeaderboardRecyclerListener() {

            @Override
            public void onHide() {
                persistentPlayerInfo.animate()
                        .translationY(persistentPlayerInfo.getHeight() + 69)
                        .setInterpolator(new AccelerateInterpolator(2))
                        .start();
            }

            @Override
            public void onShow() {
                persistentPlayerInfo.animate()
                        .translationY(0)
                        .setInterpolator(new DecelerateInterpolator(2))
                        .start();
            }

        });
    }

    private void setUpSearchView(MenuItem searchItem) {
        SearchView searchView = (SearchView) searchItem.getActionView();
        /*
        StackOverflow, Author CoolMind
        https://stackoverflow.com/a/26251197
         */
        EditText txtSearch = ((EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text));
        txtSearch.setHint("Enter a username");
        txtSearch.setHintTextColor(Color.LTGRAY);
        txtSearch.setTextColor(Color.WHITE);
        /*
        Youtube video, Author: Coding in Flow
        https://www.youtube.com/watch?v=sJ-Z9G0SDhc
         */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String correctQuery = "";
                query = query.trim().toLowerCase(Locale.ROOT);
                boolean valid = false;
                ArrayList<User> listToSearch = dataLists.get(leaderboardController.getCurrentCategory());
                for (int i = 0; i < listToSearch.size(); i++) {
                    if (listToSearch.get(i).getUsername().toLowerCase().equals(query)) {
                        valid = true;
                        correctQuery = listToSearch.get(i).getUsername();
                    }
                }
                if (valid) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_container_main);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", correctQuery);
                    searchItem.collapseActionView();
                    navController.navigate(R.id.navigation_user_profile, bundle);
                } else {
                    Toast toast = Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setOnRecyclerItemClickListener() {
        for (String key : adapterLists.keySet()) {
            adapterLists.get(key).setOnItemClickListener(new LeaderboardRecyclerAdapter.OnRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClick(int position, String key) {
                    String userToView = dataLists.get(key).get(position).getUsername();
                    //Navigate to the User Profile of the user that was clicked on
                    NavController navController = Navigation.findNavController(getView());
                    Bundle bundle = new Bundle();
                    bundle.putString("username", userToView);
                    navController.navigate(R.id.navigation_user_profile, bundle);
                }
            });
        }
    }

    public void updatePersonalCard(String currentCategory) {
        for (int i = 0; i < dataLists.get(currentCategory).size(); i++) {
            User item = dataLists.get(currentCategory).get(i);
            if (item.getUsername().equals(username)) {
                if (currentCategory.equals("most_points")) {
                    personalScore.setText(item.getStats().get("total_points") + " points");
                    String rankStr = Integer.toString(i + 1);
                    personalRank.setText("#" + rankStr);
                } else if (currentCategory.equals("most_codes")) {
                    personalScore.setText(item.getStats().get("num_codes") + " codes");
                    String rankStr = Integer.toString(i + 1);
                    personalRank.setText("#" + rankStr);
                } else if (currentCategory.equals("best_code")) {
                    personalScore.setText(item.getStats().get("best_code") + " points");
                    String rankStr = Integer.toString(i + 1);
                    personalRank.setText("#" + rankStr);
                } else if (currentCategory.equals("region_best")) {
                    personalScore.setText(item.getStats().get("region_best") + " points");
                    String rankStr = Integer.toString(i + 1);
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
                if (tab.getPosition() == 0) {
                    leaderboardList.setAdapter(adapterLists.get("most_points"));
                    leaderboardController.setCurrentCategory("most_points");
                    updatePersonalCard("most_points");
                } else if (tab.getPosition() == 1) {
                    leaderboardList.setAdapter(adapterLists.get("most_codes"));
                    leaderboardController.setCurrentCategory("most_codes");
                    updatePersonalCard("most_codes");
                } else if (tab.getPosition() == 2) {
                    leaderboardList.setAdapter(adapterLists.get("best_code"));
                    leaderboardController.setCurrentCategory("best_code");
                    updatePersonalCard("best_code");
                } else if (tab.getPosition() == 3) {
                    leaderboardList.setAdapter(adapterLists.get("region_best"));
                    leaderboardController.setCurrentCategory("region_best");
                    updatePersonalCard("region_best");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
    }
}