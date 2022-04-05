package com.example.collectqr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.collectqr.R;
import com.example.collectqr.model.AchievementItem;

import java.util.ArrayList;

/**
 * A class that overwrites the default ArrayAdapter, to be used
 * with the custom AchievementItem class
 */
public class AchievementsAdapter extends ArrayAdapter<AchievementItem> {
    private ArrayList<AchievementItem> achievements;
    private Context context;

    public AchievementsAdapter(Context context, ArrayList<AchievementItem> achievements) {
        super(context, 0, achievements);
        this.achievements = achievements;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.achievement_list_item, parent, false);
        }

        AchievementItem item = achievements.get(position);

        ImageView image = view.findViewById(R.id.achievement_image);
        TextView text = view.findViewById(R.id.achievement_text);

        image.setImageResource(item.getImage());
        text.setText(item.getAchievement());

        return view;
    }
}
