package com.example.heremaps.Fragments;

import android.content.Intent;
import android.database.Cursor;
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
import com.example.heremaps.DbHelper;
import com.example.heremaps.MainActivity;
import com.example.heremaps.Model.Category;
import com.example.heremaps.R;
import com.example.heremaps.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;


    List<Category> categories = new ArrayList<>();

    public static HistoryFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ListView lv = view.findViewById(R.id.lv_history);
        DbHelper helper = new DbHelper(getActivity());
        if (helper.getData() != null) {
            Cursor data = helper.getData();
            while (data.moveToNext()) {

                categories.add(new Category(R.drawable.ic_restaurant_black_24dp, data.getString(1)));
            }
        }
        if(categories.size()!=0) {
            categories.add(new Category(R.drawable.ic_clear_black_24dp, "Clear All"));
        }
        ListViewAdapter adapter = new ListViewAdapter(getContext(), categories);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == categories.size() - 1) {
                    helper.clearDatabase();
                    categories.clear();
                    adapter.notifyDataSetChanged();
                }else{
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("message", categories.get(i).getName());
                    startActivity(intent);
                }
            }
        });
        return view;
    }

}
