package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.List;

public class ChoosePassengerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PassengerAdapter passengerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_passanger);

        recyclerView = findViewById(R.id.recyclerView);

        List<String> passengers = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            int seatCount = 0;

            for (char letter = 'A'; letter <= 'F'; letter++) {
                passengers.add(i + String.valueOf(letter));
                seatCount++;

                if (seatCount == 3) {
                    passengers.add("");
                }
            }
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        passengerAdapter = new PassengerAdapter(passengers, passenger -> {
            Toast.makeText(this, passenger + " selected", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ChoosePassengerActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerView.setAdapter(passengerAdapter);
    }
}
