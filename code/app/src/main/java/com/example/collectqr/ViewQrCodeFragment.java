package com.example.collectqr;

import static android.content.ContentValues.TAG;

import static java.lang.Thread.sleep;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.collectqr.adapters.CommentsAdapter;
import com.example.collectqr.adapters.ScannedByAdapter;
import com.example.collectqr.model.ScanCommentItem;
import com.example.collectqr.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Comparator;

public class ViewQrCodeFragment extends Fragment {
    private boolean isAdmin;
    private View rootView;
    ArrayList<ScanCommentItem> scannedByData;
    ArrayList<ScanCommentItem> commentsData;
    ImageView image;
    TextView pointsView;
    Button delete;
    ListView scannedBy;
    ListView comments;

    public ViewQrCodeFragment() {}

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
        rootView = inflater.inflate(R.layout.fragment_view_qr_code, container, false);

        String sha = getArguments().getString("sha");
        int points = getArguments().getInt("points");

        image = rootView.findViewById(R.id.qrcode_image);
        pointsView = rootView.findViewById(R.id.qrcode_points);
        delete = rootView.findViewById(R.id.code_delete);
        scannedBy = rootView.findViewById(R.id.code_scanned_by);
        comments = rootView.findViewById(R.id.code_comments);

        boolean isAdmin = Preferences.loadAdminStatus(rootView.getContext());
        if (isAdmin) {
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QRCodeController qrCodeController = new QRCodeController();
                    qrCodeController.deleteCodeFromEverywhere(sha, points, scannedByData);
                    getActivity().onBackPressed();
                }
            });
        }

        downloadAndDisplayInfo(sha);

        return rootView;
    }

    private void setUpScannedByList() {
        scannedByData.sort(new Comparator<ScanCommentItem>() {
            @Override
            public int compare(ScanCommentItem scanCommentItem, ScanCommentItem t1) {
                return t1.getDate().compareTo(scanCommentItem.getDate());
            }
        });
        scannedBy.setAdapter(new ScannedByAdapter(getContext(), scannedByData));
    }

    private void setUpCommentsByList() {
        commentsData.sort(new Comparator<ScanCommentItem>() {
            @Override
            public int compare(ScanCommentItem scanCommentItem, ScanCommentItem t1) {
                return t1.getDate().compareTo(scanCommentItem.getDate());
            }
        });
        comments.setAdapter(new CommentsAdapter(getContext(), commentsData));
    }

    private void downloadAndDisplayInfo(String sha) {
        scannedByData = new ArrayList<>();
        commentsData = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference codeDoc = db.collection("QRCodes").document(sha);
        codeDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                pointsView.setText(documentSnapshot.get("points").toString() + " points");
                String imageName = documentSnapshot.getString("qr_image");
                // https://firebase.google.com/docs/storage/android/download-files#downloading_images_with_firebaseui
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://collectqr7.appspot.com");
                StorageReference storageReference = storage.getReferenceFromUrl("gs://collectqr7.appspot.com/"+imageName);
                Glide.with(getContext())
                        .load(storageReference)
                        .placeholder(R.drawable.ic_baseline_image_not_supported_24)
                        .into(image);
                // TODO: get location
            }
        });
        // https://firebase.google.com/docs/firestore/query-data/get-data#get_multiple_documents_from_a_collection
        CollectionReference scanCol = codeDoc.collection("ScannedBy");
        scanCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        scannedByData.add(new ScanCommentItem(document.getString("username"), document.getDate("date")));
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                CollectionReference commentCol = codeDoc.collection("Comments");
                commentCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                for (int i=0; i<scannedByData.size(); i++) {
                                    if (scannedByData.get(i).getUser().equals(document.getId())) {
                                        scannedByData.get(i).setComment(document.getString(document.getId()));
                                        commentsData.add(scannedByData.get(i));
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        setUpScannedByList();
                        setUpCommentsByList();
                    }
                });
            }
        });
    }
}
