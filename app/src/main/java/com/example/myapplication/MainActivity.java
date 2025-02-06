package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.myapplication.ui.FlightSelectionHelper;
import com.example.myapplication.ui.HomeActivity;
import com.example.myapplication.ui.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.ui.HomeFragment;
import com.example.myapplication.databinding.ActivityMainBinding;

import java.io.Console;

public class MainActivity extends AppCompatActivity {

//    private ActivityMainBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//    }

    private Spinner spinnerFlight;
    private EditText editTextDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        // Initialize UI elements
        spinnerFlight = findViewById(R.id.spinner_flight);
        editTextDate = findViewById(R.id.editText_date);
        // Find Submit button
        Button btnSubmit = findViewById(R.id.btn_submit);
        // Set click listener to navigate to HomeFragment
        btnSubmit.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity so the user cannot go back to login
        });


        // Use the helper class to manage UI logic
        new FlightSelectionHelper(this, spinnerFlight, editTextDate);
    }

}