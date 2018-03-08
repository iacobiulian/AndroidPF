package com.example.android.personalfinance_v01.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.personalfinance_v01.MyClasses.Category;
import com.example.android.personalfinance_v01.R;

import java.util.List;

/**
 * Created by iacob on 25-Feb
 */

public class CategoryAdapter extends ArrayAdapter<Category> {

    public CategoryAdapter(Context context, List<Category> categories) {
        super(context, 0, categories);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_category_spinner, parent, false);
        }

        Category currentCategory = getItem(position);

        if (currentCategory != null) {
            TextView nameTV = convertView.findViewById(R.id.categoryTv);
            nameTV.setText(currentCategory.getName());

            ImageView iconIv = convertView.findViewById(R.id.categoryImageView);
            iconIv.setImageResource(currentCategory.getIconID());
        }

        return convertView;
    }
}
