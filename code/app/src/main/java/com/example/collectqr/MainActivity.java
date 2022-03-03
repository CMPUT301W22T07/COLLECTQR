package com.example.collectqr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Declare the variables so that you will be able to reference it later.
    ListView cityList;
    ArrayAdapter<City> cityAdapter;
    ArrayList<City> cityDataList;
    int selectedPosition=-1;

    CustomList customList;

    final String TAG = "Sample";
    Button deleteCityButton;
    Button addCityButton;
    EditText addCityEditText;
    EditText addProvinceEditText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        // Get a top level reference to the collection
        final CollectionReference collectionReference = db.collection("Cities");

        cityList = findViewById(R.id.city_list);

        String []cities ={"Edmonton", "Vancouver", "Toronto", "Hamilton", "Denver", "Los Angeles"};
        String []provinces = {"AB", "BC", "ON", "ON", "CO", "CA"};


        cityDataList = new ArrayList<>();

        for(int i=0;i<cities.length;i++){
            cityDataList.add((new City(cities[i], provinces[i])));
        }

        cityAdapter = new CustomList(this, cityDataList);

        cityList.setAdapter(cityAdapter);

//        dataList = new ArrayList<>();
//        dataList.addAll(Arrays.asList(cities));
//
//        cityAdapter = new ArrayAdapter<>(this, R.layout.content, dataList);
//
//        cityList.setAdapter(cityAdapter);

        deleteCityButton = findViewById(R.id.delete_button);
        addCityButton = findViewById(R.id.add_button);
        addCityEditText = findViewById(R.id.add_city_field);
        addProvinceEditText = findViewById(R.id.add_province_field);

        addCityButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cityName = addCityEditText.getText().toString();
                final String provinceName = addProvinceEditText.getText().toString();
                HashMap<String, String> data = new HashMap<>();
                if (cityName.length()>0 && provinceName.length()>0) {
                    // If there’s some data in the EditText field, then we create a new key-value pair.
                    data.put("Province Name", provinceName);

                    // The set method sets a unique id for the document
                    collectionReference
                            .document(cityName)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // These are a method which gets executed when the task is succeeded
                                    Log.d(TAG, "Data has been added successfully!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // These are a method which gets executed if there’s any problem
                                    Log.d(TAG, "Data could not be added!" + e.toString());
                                }
                            });
                }
                // Setting the fields to null so that user can add a new city
                addCityEditText.setText("");
                addProvinceEditText.setText("");
            }
        });

        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPosition = i;
            }
        });

        deleteCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition!=-1) {
                    String city = cityAdapter.getItem(selectedPosition).getCityName();
                    // Code from https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
                    db.collection("Cities").document(city)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    if (selectedPosition>=cityAdapter.getCount()) {
                                        selectedPosition = -1;
                                    }
                                    cityList.clearChoices();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document", e);
                                }
                            });
                }
            }});

        // Real time updates from cloud
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                // Clear the old list
                cityDataList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    Log.d(TAG, String.valueOf(doc.getData().get("Province Name")));
                    String city = doc.getId();
                    String province = (String) doc.getData().get("Province Name");
                    cityDataList.add(new City(city, province)); // Adding the cities and provinces from FireStore
                }
                cityAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
            }
        });


    }

}