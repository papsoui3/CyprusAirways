package com.example.myapplication.ui;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasketManager {
    private static BasketManager instance;
    private final Map<String, Integer> basketItems;
    private final Map<String, Double> itemPrices;
    private BasketUpdateListener updateListener;
    private List<OnBasketChangeListener> listeners = new ArrayList<>();


    private BasketManager() {
        basketItems = new HashMap<>();
        itemPrices = new HashMap<>();

        itemPrices.put("Coke", 2.0);
        itemPrices.put("Water", 1.50);
        itemPrices.put("Juice", 3.0);
        itemPrices.put("Pepsi", 4.0);

    }

    public static BasketManager getInstance() {
        if (instance == null) {
            instance = new BasketManager();
        }
        return instance;
    }

    public void addItem(String itemName, int quantity) {
        Log.d("BasketManager", "Item Added: " + itemName + " Quantity: " + quantity);
        if (quantity > 0) {
            basketItems.put(itemName, quantity);
        } else {
            basketItems.remove(itemName);
        }
        notifyBasketChange();
    }

    public Map<String, Integer> getBasketItems() {
        Log.d("BasketManager", "Basket Items: " + basketItems.toString());
        return new HashMap<>(basketItems);
    }
    public void removeItem(String itemName) {
        basketItems.remove(itemName);
        notifyUpdate();
    }

    public Map<String, Integer> getItems() {
        return basketItems;
    }
    public double getItemPrice(String itemName) {
        return itemPrices.getOrDefault(itemName, 0.0);
    }

    public void setBasketUpdateListener(BasketActivity listener) {
        this.updateListener = listener;
    }
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
    public interface OnBasketChangeListener {
        void onBasketChanged();
    }
    public interface BasketUpdateListener {
        void onBasketUpdated();
    }
}
