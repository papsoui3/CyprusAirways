package com.example.myapplication.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<String> itemList;
    private OnItemInteractionListener onItemInteractionListener;

    // Constructor
    public ItemAdapter(List<String> itemList, OnItemInteractionListener onItemInteractionListener) {
        this.itemList = itemList;
        this.onItemInteractionListener = onItemInteractionListener;
        BasketManager.getInstance().registerBasketChangeListener(this::refreshQuantities);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (CardView)
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String itemName = itemList.get(position);

        // Get the quantity for this item directly from BasketManager
        int quantity = BasketManager.getInstance().getBasketItems().getOrDefault(itemName, 0);

        holder.itemNameTextView.setText(itemName);
        holder.itemPriceTextView.setText("$" + String.valueOf(getPriceForItem(itemName)));
        holder.itemQuantityTextView.setText(String.valueOf(quantity));  // Set the quantity from BasketManager

        int imageResId = getImageForItem(itemName);
        holder.itemImageView.setImageResource(imageResId);
        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = Integer.parseInt(holder.itemQuantityTextView.getText().toString()) + 1;
            holder.itemQuantityTextView.setText(String.valueOf(newQuantity));
            BasketManager.getInstance().addItem(itemName, newQuantity);
        });

        holder.decreaseButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.itemQuantityTextView.getText().toString());
            if (currentQuantity > 0) {
                int newQuantity = currentQuantity - 1;
                holder.itemQuantityTextView.setText(String.valueOf(newQuantity));
                BasketManager.getInstance().addItem(itemName, newQuantity);
            }
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
        return itemList.size(); // Return the size of the list
    }
    // Method to get image resource ID based on item name
    private int getImageForItem(String itemName) {
        switch (itemName) {
            case "Coke":
                return R.mipmap.coke;  // Add actual drawable resource
            case "Juice":
                return R.mipmap.juice;  // Add actual drawable resource
            case "Water":
                return R.mipmap.water;
            case "Pepsi":
                return R.mipmap.pepsi;
            default:
                return R.mipmap.coke;  // Default image
        }
    }

    public void refreshQuantities() {
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView itemPriceTextView;
        TextView itemQuantityTextView;
        Button decreaseButton;
        Button increaseButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.imageViewItem);
            itemNameTextView = itemView.findViewById(R.id.textViewItemName);
            itemPriceTextView = itemView.findViewById(R.id.textViewItemPrice);
            itemQuantityTextView = itemView.findViewById(R.id.textViewQuantity);
            decreaseButton = itemView.findViewById(R.id.buttonDecrease);
            increaseButton = itemView.findViewById(R.id.buttonIncrease);
        }
    }

    // Interface for item interaction (item addition/removal)
    public interface OnItemInteractionListener {
        void onItemAdd(String itemName, int quantity);
    }
    // Data class for an Item
    public  class Item {
        private String name;
        private double price;
        private int imageResId;

        public Item(String name, double price, int imageResId) {
            this.name = name;
            this.price = price;
            this.imageResId = imageResId;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getImageResId() {
            return imageResId;
        }

    }


}
