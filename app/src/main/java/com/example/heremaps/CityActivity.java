package com.example.heremaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.heremaps.Common.Common;
import com.example.heremaps.Model.City_Info;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

public class CityActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        listView = findViewById(R.id.listview);
        String[] cities = new String[] { "Душанбе","Худжанд","Алматы","Нур-Султан","Шымкент","Дубай","Истамбул","Тблиси"};
        final List<City_Info> info = new ArrayList<>();
        info.add(new City_Info(new LatLng(38.469311,68.689332),new LatLng(38.658229,68.861238),new LatLng(38.57306,68.78639),"Душанбе"));
        info.add(new City_Info(new LatLng(40.268524,69.604393),new LatLng(40.301655,69.631004),new LatLng(40.285713,69.619375),"Худжанд"));
        info.add(new City_Info(new LatLng(43.0332628,76.7382789),new LatLng(43.4025533,77.1672819),new LatLng(43.25,76.9),"Алматы"));
        info.add(new City_Info(new LatLng(50.974697,71.244094),new LatLng(51.305896,71.607216),new LatLng(51.18333,71.4),"Нур-Султан"));
        info.add(new City_Info(new LatLng(42.299854,69.55719),new LatLng(42.33596,69.610573),new LatLng(42.320732,69.584115),"Шымкент"));
        info.add(new City_Info(new LatLng(24.7949359747316,54.8851666467671),new LatLng(25.3576302551773,55.5678239668802),new LatLng(25.2684,55.2962),"Дубай"));
        info.add(new City_Info(new LatLng(40.749834,28.149321),new LatLng(41.62477,29.94719),new LatLng(41.01,28.96028),"Истамбул"));
        info.add(new City_Info(new LatLng(41.625031,44.680715),new LatLng(41.79533,45.009274),new LatLng(41.71667,44.78333),"Тблиси"));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,cities);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Common.city_info = info.get(i);
                Common.name = info.get(i).getName();
                Intent intent = new Intent(CityActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
