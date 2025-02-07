package com.example.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eft.libpositive.PosIntegrate;
import com.example.myapplication.R;
import com.example.myapplication.ui.weblink.WebLinkIntegrate;
import com.google.android.material.button.MaterialButton;
import com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BasketActivity extends AppCompatActivity implements BasketManager.BasketUpdateListener {
    private static final int VISA_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private BasketAdapter basketAdapter;
    private Map<String, Integer> basketItems;
    private TextView totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Basket");
        }

        recyclerView = findViewById(R.id.recyclerViewBasket);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get items from BasketManager
        basketItems = BasketManager.getInstance().getItems();

        // Initialize the total TextView
        totalTextView = findViewById(R.id.textViewTotal);
        updateTotal();

        basketAdapter = new BasketAdapter(basketItems, new BasketAdapter.OnItemClickListener() {
            @Override
            public void onQuantityChange(String itemName, int newQuantity) {
                // Update item quantity in the basket
                BasketManager.getInstance().addItem(itemName, newQuantity);
                basketAdapter.notifyDataSetChanged();
                updateTotal();  // Update total after quantity change
            }

            @Override
            public void onItemRemove(String itemName) {
                // Remove item from the basket
                BasketManager.getInstance().removeItem(itemName);
                basketAdapter.notifyDataSetChanged();
                updateTotal();  // Update total after item removal
            }
        });
        basketAdapter.refreshBasket();
        recyclerView.setAdapter(basketAdapter);
        MaterialButton buttonGoBack = findViewById(R.id.buttonGoBack);
        buttonGoBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        Button buttonVisa = findViewById(R.id.buttonVisa);
        buttonVisa.setOnClickListener(v -> {
            launchOtherApp();
        });

        BasketManager.getInstance().setBasketUpdateListener(this);
    }
    private void launchOtherApp() {
        Log.d("BasketActivity", "Visa button clicked - Starting Transaction");

        String totalAmountText = totalTextView.getText().toString().replace("$", "").trim();
        int totalAmount = (int) (Double.parseDouble(totalAmountText) * 100);
        String formattedAmount = String.valueOf(totalAmount);
        hideKeyboard();

        Log.d("Total:",formattedAmount);
        // Create argument map for transaction
        HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<>();
        args.put(PosIntegrate.CONFIG_TYPE.CT_AMOUNT, formattedAmount);
        args.put(PosIntegrate.CONFIG_TYPE.CT_LANGUAGE, "en_GB");

        Log.d("BasketActivity", "Transaction Args: " + args.toString());
        if (WebLinkIntegrate.enabled) {
            Log.d("BasketActivity", "Processing SALE transaction via WebLinkIntegrate...");
            TRANSACTION_TYPE transType = TRANSACTION_TYPE.getTransType("SaleAuto");
            WebLinkIntegrate.executeTransaction(this, String.valueOf(transType), args, new TransactionCallback() {
                @Override
                public void onTransactionSuccess(JSONObject response) {
                    Log.d("BasketActivity", "onTransactionSuccess() triggered!");

                    // Ensure Toast runs on UI thread
                    runOnUiThread(() -> {
                        Toast.makeText(BasketActivity.this, "Transaction Successful!", Toast.LENGTH_LONG).show();
                        Log.d("BasketActivity", "Toast for Transaction Success should be displayed now!");
                    });
                }
                @Override
                public void onTransactionFailed(String error) {
                    Log.e("BasketActivity", "onTransactionFailed() triggered!");

                    runOnUiThread(() -> {
                        Toast.makeText(BasketActivity.this, "Transaction Failed: " + error, Toast.LENGTH_LONG).show();
                        Log.e("BasketActivity", "Toast for Transaction Failure should be displayed now!");
                    });
                }

            });
        } else {
            TRANSACTION_TYPE transType = TRANSACTION_TYPE.getTransType("SaleAuto");
            PosIntegrate.executeTransaction(this, transType, args);
        }
        Log.d("BasketActivity", "📩 Transaction request sent");
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VISA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String transactionStatus = data.getStringExtra("TRANSACTION_STATUS");
                Toast.makeText(this, "Transaction Successful: " + transactionStatus, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Transaction Failed or Cancelled!", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onBasketUpdated() {
        updateTotal();
        basketAdapter.refreshBasket();
    }

    // Method to calculate and update the total price
    private void updateTotal() {
        double total = 0.0;
        for (Map.Entry<String, Integer> entry : basketItems.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();
            double price = BasketManager.getInstance().getItemPrice(itemName);
            total += price * quantity;
        }
        totalTextView.setText("$" + total);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        finish();
        return true;
    }
}

