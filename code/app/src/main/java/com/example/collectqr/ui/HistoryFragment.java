package com.example.collectqr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.adapters.HistoryAdapter;
import com.example.collectqr.data.HistoryController;
import com.example.collectqr.model.QRCode;
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
    private QRCode selectedCode;
    private View selectedView;
    private FloatingActionButton moreInfoButton;
    private FloatingActionButton deleteButton;

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

        setUpRecyclerView();

        moreInfoButton = rootView.findViewById(R.id.info_history_fab);
        deleteButton = rootView.findViewById(R.id.delete_history_fab);
        setUpFabs();

        createSortSheetDialog();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId()==R.id.sort_history) {
                sortSheet.show();
                return true;
            }
            return false;
        });

        return rootView;
    }

    private void showfabs() {
        moreInfoButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
    }
    private void hidefabs() {
        moreInfoButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
    }
    private void setUpFabs() {
        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(rootView);
                Bundle bundle = new Bundle();
                bundle.putString("sha", selectedCode.getSha256());
                bundle.putInt("points", selectedCode.getPoints());
                navController.navigate(R.id.navigation_qr_code_details, bundle);
                refreshSelection();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.deleteCode(selectedCode);
                refreshSelection();
            }
        });
    }
    private void setUpRecyclerView() {
        /*
        https://youtu.be/17NbUcEts9c
        https://youtu.be/bhhs4bwYyhc
        YouTube, Author: Coding in Flow
         */
        recyclerView = rootView.findViewById(R.id.history_qr_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(controller.getAdapter());
        controller.getAdapter().setOnItemClickListener(new HistoryAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(int position, View view) {
                QRCode code = controller.getData().get(position);
                if (selectedCode==null) {
                    // select
                    view.setBackground(getResources().getDrawable(R.drawable.list_selector, getActivity().getTheme()));
                    selectedCode = code;
                    selectedView = view;
                    showfabs();
                } else if (selectedView==view) {
                    // unselect
                    view.setBackground(getResources().getDrawable(R.drawable.white_rounded_rectangle, getActivity().getTheme()));
                    selectedCode = null;
                    selectedView = null;
                    hidefabs();
                } else {
                    // select and unselect previous
                    selectedView.setBackgroundResource(R.drawable.white_rounded_rectangle);
                    view.setBackground(getResources().getDrawable(R.drawable.list_selector, getActivity().getTheme()));
                    selectedCode = code;
                    selectedView = view;
                }
            }
        });
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
                refreshSelection();
                sortSheet.dismiss();
            }
        });
        byPointsDescend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.sortQrData("points_descend");
                refreshSelection();
                sortSheet.dismiss();
            }
        });
        byDateDescend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.sortQrData("date_descend");
                refreshSelection();
                sortSheet.dismiss();
            }
        });
    }

    void refreshSelection() {
        if (selectedView!=null) {
            selectedView.setBackgroundResource(R.drawable.white_rounded_rectangle);
            selectedCode = null;
            selectedView = null;
        }
        hidefabs();
    }
}