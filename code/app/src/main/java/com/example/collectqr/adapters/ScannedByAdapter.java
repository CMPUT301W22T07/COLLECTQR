package com.example.collectqr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.collectqr.R;
import com.example.collectqr.model.ScanCommentItem;

import java.util.ArrayList;

/**
 * An array adapter that manages the ListView that displays the users who have scanned the QR code
 */
public class ScannedByAdapter extends ArrayAdapter<ScanCommentItem> {
    private ArrayList<ScanCommentItem> data;
    private Context context;

    /**
     * ScannedByAdapter constructor
     * @param context
     * @param data
     */
    public ScannedByAdapter(Context context, ArrayList<ScanCommentItem> data) {
        super(context, 0, data);
        this.data = data;
        this.context = context;
    }

    /**
     * Creates the view for an item of the scanned by list
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.scanned_by, parent, false);
        }
        ScanCommentItem item = data.get(position);

        // display the data
        TextView scannedBy = view.findViewById(R.id.scannedBy);
        scannedBy.setText(item.getUser());

        return view;
    }
}
