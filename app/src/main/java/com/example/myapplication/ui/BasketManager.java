package com.example.myapplication.ui;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasketManager {
    private static BasketManager instance;
    private final Map<String, Integer> basketItems;
    private final Map<String, Double> itemPrices; // Map to store item prices
    private BasketUpdateListener updateListener;
    private List<OnBasketChangeListener> listeners = new ArrayList<>();


    private BasketManager() {
        basketItems = new HashMap<>();
        itemPrices = new HashMap<>();
        // Initialize prices for each item (example)
        itemPrices.put("Coke", 2.0);
        itemPrices.put("Water", 1.50);
        itemPrices.put("Juice", 3.0);
        itemPrices.put("Pepsi", 4.0);
        // Add more items and prices here
    }

    public static BasketManager getInstance() {
        if (instance == null) {
            instance = new BasketManager();
        }
        return instance;
    }

    // Add item or update quantity
    public void addItem(String itemName, int quantity) {
        Log.d("BasketManager", "Item Added: " + itemName + " Quantity: " + quantity);
        if (quantity > 0) {
            basketItems.put(itemName, quantity);
        } else {
            basketItems.remove(itemName);
        }
        notifyBasketChange();  // Notify listeners about the update
    }

    public Map<String, Integer> getBasketItems() {
        Log.d("BasketManager", "Basket Items: " + basketItems.toString());
        return new HashMap<>(basketItems);  // Return a copy of the items to prevent modification
    }

    // Remove item from basket
    public void removeItem(String itemName) {
        basketItems.remove(itemName);
        notifyUpdate();  // Notify all listeners
    }

    public Map<String, Integer> getItems() {
        return basketItems;
    }

    // Get item price
    public double getItemPrice(String itemName) {
        // Return the price for the item, or 0 if not found
        return itemPrices.getOrDefault(itemName, 0.0);
    }

    // Method to set listener
    public void setBasketUpdateListener(BasketActivity listener) {
        this.updateListener = listener;
    }
    // Notify the listener when basket is updated
    private void notifyUpdate() {
        if (updateListener != null) {
            updateListener.onBasketUpdated();
        }
    }
    public void registerBasketChangeListener(OnBasketChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyBasketChange() {
        for (OnBasketChangeListener listener : listeners) {
            listener.onBasketChanged();
        }
    }
    public interface BasketChangeListener {
        void onBasketChanged();
    }
    public interface OnBasketChangeListener {
        void onBasketChanged();
    }
    // Interface to notify UI components
    public interface BasketUpdateListener {
        void onBasketUpdated();
    }
}
