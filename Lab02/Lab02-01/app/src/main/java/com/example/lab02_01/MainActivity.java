package com.example.lab02_01;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ListView lvPerson;
    TextView tvPerson;
    ArrayList<String> arrayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPerson = findViewById(R.id.tv_person);
        lvPerson = findViewById(R.id.lv_person);

        tvPerson.setTextColor(Color.GREEN);

        arrayName = new ArrayList<>();
        arrayName.add("Tèo");
        arrayName.add("Tý");
        arrayName.add("Bin");
        arrayName.add("Bo");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayName);
        lvPerson.setAdapter(adapter);

        lvPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = lvPerson.getItemAtPosition(position).toString();
                tvPerson.setText("position :" + position + " ; value =" + value);
            }
        });
    }
}
