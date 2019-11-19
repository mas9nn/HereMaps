package com.example.heremaps.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.heremaps.Adapters.ListViewAdapter;
import com.example.heremaps.MainActivity;
import com.example.heremaps.Model.Category;
import com.example.heremaps.R;
import com.example.heremaps.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private ListView listView;

    List<Category> categories = new ArrayList<>();
    public static CategoryFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        listView = view.findViewById(R.id.list);
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Рестораны"));
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Гостиницы"));
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Продукты"));
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Достопримечательности"));
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Заправки"));
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Банкоматы"));
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Банки"));
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Здания больницы"));
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Аптеки"));
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Полиция"));
        categories.add(new Category(R.drawable.ic_restaurant_black_24dp,"Туалеты"));
        ListViewAdapter adapter = new ListViewAdapter(getContext(),categories);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("message", categories.get(i).getName());
                startActivity(intent);
            }
        });
        return view;
    }

}
