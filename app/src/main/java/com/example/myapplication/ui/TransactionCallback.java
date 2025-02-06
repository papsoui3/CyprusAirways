package com.example.myapplication.ui;


import org.json.JSONObject;

public interface TransactionCallback {
    void onTransactionSuccess(JSONObject response);
    void onTransactionFailed(String error);
}
