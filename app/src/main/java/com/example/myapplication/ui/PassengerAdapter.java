package com.example.myapplication.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder> {

    private List<String> passengerList;
    private OnPassengerClickListener listener;

    public PassengerAdapter(List<String> passengerList, OnPassengerClickListener listener) {
        this.passengerList = passengerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PassengerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_passenger, parent, false);
        return new PassengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerViewHolder holder, int position) {
        String passenger = passengerList.get(position);
        holder.passengerButton.setText(passenger);
        if (passenger.equals("")) { // This is an aisle space
            holder.passengerButton.setBackgroundColor(holder.itemView.getContext().getColor(R.color.cyprus_background));// Set aisle color to white
            holder.passengerButton.setEnabled(false); // Disable clicking for aisles
            holder.passengerButton.setVisibility(View.VISIBLE); // Keep aisle visible
            holder.passengerButton.setText(""); // Remove text for aisles
        } else {
            holder.passengerButton.setText(passenger);
            holder.passengerButton.setBackgroundColor(holder.itemView.getContext().getColor(R.color.seat_background)); // Keep seats blue

            // Handle click event
            holder.passengerButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPassengerClick(passenger);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return passengerList.size();
    }

    public static class PassengerViewHolder extends RecyclerView.ViewHolder {
        Button passengerButton;

        public PassengerViewHolder(View itemView) {
            super(itemView);
            passengerButton = itemView.findViewById(R.id.btnSeat);
        }
    }

    // Define an interface for click handling
    public interface OnPassengerClickListener {
        void onPassengerClick(String passenger);
    }
}
