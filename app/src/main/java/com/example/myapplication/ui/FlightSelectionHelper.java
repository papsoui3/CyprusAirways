package com.example.myapplication.ui;


import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class FlightSelectionHelper {

    private Context context;
    private Spinner spinnerFlight;
    private EditText editTextDate;

    public FlightSelectionHelper(Context context, Spinner spinnerFlight, EditText editTextDate) {
        this.context = context;
        this.spinnerFlight = spinnerFlight;
        this.editTextDate = editTextDate;
        setupFlightSpinner();
        setupDatePicker();
    }

    private void setupFlightSpinner() {
        String[] flightNumbers = {"CY101", "CY202", "CY303", "CY404", "CY505"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, flightNumbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFlight.setAdapter(adapter);

        spinnerFlight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFlight = parent.getItemAtPosition(position).toString();
                Toast.makeText(context, "Selected: " + selectedFlight, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupDatePicker() {
        editTextDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    editTextDate.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}
