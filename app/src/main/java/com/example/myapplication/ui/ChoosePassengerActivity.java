package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
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

        // Sample list of passengers (added more to fill multiple rows)
        List<String> passengers = new ArrayList<>();
        // Outer loop for numbers

        for (int i = 1; i <= 30; i++) { // 30 rows
            int seatCount = 0; // Track seats in a row

            for (char letter = 'A'; letter <= 'F'; letter++) { // 6 seats per row
                passengers.add(i + String.valueOf(letter)); // Add seat
                seatCount++;

                // Add an empty space after 3 seats to represent the aisle
                if (seatCount == 3) {
                    passengers.add(""); // Empty space for aisle
                }
            }
        }


        // Set GridLayoutManager with 3 columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        // Set Adapter
        // Set Adapter with click listener
        passengerAdapter = new PassengerAdapter(passengers, passenger -> {
            // Handle seat selection
            Toast.makeText(this, passenger + " selected", Toast.LENGTH_SHORT).show();

            // Navigate back to home
            Intent intent = new Intent(ChoosePassengerActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        });

        recyclerView.setAdapter(passengerAdapter);
    }
}
