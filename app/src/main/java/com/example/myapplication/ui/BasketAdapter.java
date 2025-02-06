package com.example.myapplication.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.Map;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.BasketViewHolder> {
    private Map<String, Integer> basketItems;
    private OnItemClickListener listener;
    private final Map<String, Integer> itemImages = Map.of(
            "Coke", R.mipmap.coke,
            "Pepsi", R.mipmap.pepsi,
            "Water", R.mipmap.water,
            "Juice", R.mipmap.juice
    );
    public BasketAdapter(Map<String, Integer> basketItems, OnItemClickListener listener) {
        this.basketItems = basketItems;
        this.listener = listener;
        BasketManager.getInstance().registerBasketChangeListener(this::refreshBasket);
    }

    @NonNull
    @Override
    public BasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_basket, parent, false);
        return new BasketViewHolder(view);
    }
    public void refreshBasket() {
        this.basketItems = BasketManager.getInstance().getBasketItems();
        notifyDataSetChanged(); // Refreshes the entire list
    }


    @Override
    public void onBindViewHolder(@NonNull BasketViewHolder holder, int position) {
        String itemName = (String) basketItems.keySet().toArray()[position];
        int quantity = basketItems.get(itemName);

        holder.textViewItemName.setText(itemName);
        holder.textViewItemPrice.setText(String.format("$ %s", getPriceForItem(itemName)));
        holder.textViewQuantity.setText(String.valueOf(quantity));

        // Set the correct image based on the item name
        if (itemImages.containsKey(itemName)) {
            holder.imageViewItem.setImageResource(itemImages.get(itemName));
        } else {
            holder.imageViewItem.setImageResource(R.mipmap.coke);
        }

        holder.buttonIncrease.setOnClickListener(v -> {
            int newQuantity = quantity + 1;
            BasketManager.getInstance().addItem(itemName, newQuantity);
            notifyItemChanged(position);  // Update only the affected item
            listener.onQuantityChange(itemName, newQuantity);
        });

        holder.buttonDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                int newQuantity = quantity - 1;
                BasketManager.getInstance().addItem(itemName, newQuantity);
                notifyItemChanged(position);
                listener.onQuantityChange(itemName, newQuantity);
            } else {
                BasketManager.getInstance().removeItem(itemName);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, basketItems.size());  // Update list

            }
        });
        // Handle delete button click
        holder.imageViewDelete.setOnClickListener(v -> {
            BasketManager.getInstance().removeItem(itemName);
            basketItems.remove(itemName);  // Remove item from the local basketItems map
            notifyItemRemoved(position);  // Remove item from RecyclerView
            notifyItemRangeChanged(position, basketItems.size());  // Update list after removal
        });
        
    }

    private double getPriceForItem(String itemName) {
        switch (itemName) {
            case "Coke":
                return 2;  // Add actual drawable resource
            case "Juice":
                return 3;  // Add actual drawable resource
            case "Water":
                return 1.5;
            case "Pepsi":
                return 4;
            default:
                return 0;  // Default image
        }
    }

    @Override
    public int getItemCount() {
        return basketItems.size();
    }

    public static class BasketViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItemName, textViewItemPrice, textViewQuantity;
        Button buttonIncrease, buttonDecrease;
        ImageView imageViewItem, imageViewDelete;

        public BasketViewHolder(View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewItemPrice = itemView.findViewById(R.id.textViewItemPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);  // Ensure correct ID
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);  // Ensure correct ID
        }
    }


    public interface OnItemClickListener {
        void onQuantityChange(String itemName, int newQuantity);
        void onItemRemove(String itemName);
    }
}

