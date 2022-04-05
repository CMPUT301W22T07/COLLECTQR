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
 * An array adapter that manages the ListView that displays the comments made on the QR code
 */
public class CommentsAdapter extends ArrayAdapter<ScanCommentItem> {
    private ArrayList<ScanCommentItem> data;
    private Context context;

    /**
     * A constructor for CommentsAdapter
     * @param context
     * @param data
     */
    public CommentsAdapter(Context context, ArrayList<ScanCommentItem> data) {
        super(context, 0, data);
        this.data = data;
        this.context = context;
    }


    /**
     * Creates the view for an item of the comment list
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment, parent, false);
        }
        // get the current item
        ScanCommentItem item = data.get(position);

        TextView username = view.findViewById(R.id.comment_username);
        TextView date = view.findViewById(R.id.comment_date);
        TextView content = view.findViewById(R.id.comment_content);

        // display the data
        username.setText(item.getUser());
        String scannedOn = item.getDate().toString();
        date.setText(String.format("%s %s %s",
                scannedOn.substring(11, 16),
                scannedOn.substring(4, 10),
                scannedOn.substring(23)));
        content.setText(item.getComment());

        return view;
    }
}