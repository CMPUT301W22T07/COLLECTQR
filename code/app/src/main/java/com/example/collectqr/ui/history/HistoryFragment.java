package com.example.collectqr.ui.history;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;

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

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<HistoryItem> data;
    private String currentSort = "date_descend";
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

        TextView totalPoints = rootView.findViewById(R.id.history_total_points);
        TextView numCodes = rootView.findViewById(R.id.history_num_codes);
        //https://firebase.google.com/docs/firestore/query-data/listen
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(username);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    totalPoints.setText(snapshot.get("total_points") + "\nTotal Points");
                    numCodes.setText(snapshot.get("num_codes") + "\nQR Codes");
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        data = new ArrayList<>();
        // Code from Lab 5
        db.collection("Users").document(username).collection("ScannedCodes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                            FirebaseFirestoreException error) {
                        // Clear the old list
                        data.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.d(TAG, String.valueOf(doc.getData().get("hash")));
                            data.add(new HistoryItem(doc)); // Adding the cities and provinces from FireStore
                        }
                        sortDataSet();
                        adapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                    }
                });

        /*
        https://youtu.be/17NbUcEts9c
        YouTube, Author: Coding in Flow
         */
        recyclerView = rootView.findViewById(R.id.history_qr_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HistoryAdapter(username, data);
        recyclerView.setAdapter(adapter);

        createSortSheetDialog();
        FloatingActionButton fab = rootView.findViewById(R.id.sort_history_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortSheet.show();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

     void sortDataSet() {
        /*
        https://www.geeksforgeeks.org/how-to-sort-an-arraylist-of-objects-by-property-in-java/
        Article Contributed By: sparshgupta
        https://youtu.be/Mguw_TQBExo
        YouTube video, Author: RAJASEKHAR REDDY
         */
        if (currentSort.equals("points_ascend")) {
            data.sort(new Comparator<HistoryItem>() {
                @Override
                public int compare(HistoryItem historyItem, HistoryItem t1) {
                    return historyItem.getPoints()- t1.getPoints();
                }
            });
        } else if (currentSort.equals("points_descend")) {
            data.sort(new Comparator<HistoryItem>() {
                @Override
                public int compare(HistoryItem historyItem, HistoryItem t1) {
                    return t1.getPoints()- historyItem.getPoints();
                }
            });
        } else if (currentSort.equals("date_descend")) {
            data.sort(new Comparator<HistoryItem>() {
                @Override
                public int compare(HistoryItem historyItem, HistoryItem t1) {
                    return t1.getDate().compareTo(historyItem.getDate());
                }
            });
        }
        adapter.notifyDataSetChanged();
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
                currentSort = "points_ascend";
                sortDataSet();
                sortSheet.dismiss();
            }
        });
        byPointsDescend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSort = "points_descend";
                sortDataSet();
                sortSheet.dismiss();
            }
        });
        byDateDescend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSort = "date_descend";
                sortDataSet();
                sortSheet.dismiss();
            }
        });
    }
}