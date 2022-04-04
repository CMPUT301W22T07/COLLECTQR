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
public class CommentsAdapter extends ArrayAdapter<ScanCommentItem> {
    private ArrayList<ScanCommentItem> data;
    private Context context;

    public CommentsAdapter(Context context, ArrayList<ScanCommentItem> data) {
        super(context, 0, data);
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment, parent, false);
        }
        ScanCommentItem item = data.get(position);

        TextView username = view.findViewById(R.id.comment_username);
        TextView date = view.findViewById(R.id.comment_date);
        TextView content = view.findViewById(R.id.comment_content);

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