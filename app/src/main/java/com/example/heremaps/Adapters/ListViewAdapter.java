package com.example.heremaps.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heremaps.Model.Category;
import com.example.heremaps.R;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Category> {


    private Context mContext;
    private List<Category> categories = new ArrayList<>();

    public ListViewAdapter(Context context,List<Category>categories) {
        super(context,0);
        this.mContext = context;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_category,parent,false);

        ImageView imageView = v.findViewById(R.id.image_category);
        imageView.setImageResource(categories.get(position).getDrawable());
        TextView text = v.findViewById(R.id.text_category);
        text.setText(categories.get(position).getName());

        return v;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }
}
