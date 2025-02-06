package com.example.myapplication.ui.weblink;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.eft.libpositive.PosIntegrate;
import com.eft.libpositive.wrappers.PositiveError;
import com.example.myapplication.ui.TransactionCallback;
import com.example.myapplication.ui.TransactionResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.*;
import static com.eft.libpositive.wrappers.PositiveError.INVALID_ARG;
import static com.eft.libpositive.wrappers.PositiveError.NO_ERROR;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class WebLinkIntegrate {

    private static final String TAG = "WebLinkTransaction";
    public static boolean enabled;

    public static PositiveError executeTransaction(Activity activity, String transactionType, HashMap<PosIntegrate.CONFIG_TYPE, String> args, TransactionCallback transactionCallback) {

        JSONObject j = new JSONObject();

        try {
            j.put("transType", transactionType);

            String ammountString =args.get(CT_AMOUNT);
            int amountinCents = getIntFromString(ammountString,100);
            j.put("amountTrans", amountinCents);
            j.put("amountGratuity", getIntFromString(args.get(CT_AMOUNT_GRATUITY),0));
            j.put("amountCashback", getIntFromString(args.get(CT_AMOUNT_CASHBACK),0));

            if (args.get(CT_UTI) != null && args.get(CT_UTI).length() > 0)
                j.put("uti", getIntFromString(args.get(CT_UTI),0));
            j.put("retrievalReferenceNumber", args.get(CT_RRN));
            j.put("language", args.get(CT_LANGUAGE));
            j.put("reference", "Tst Transaction");

            performPost(activity, j);

        } catch ( Exception e) {
            e.printStackTrace();
        }
        return NO_ERROR;
    }


    public static PositiveError queryTransaction(Activity activity, HashMap<PosIntegrate.CONFIG_TYPE, String> args) {

        JSONObject j = new JSONObject();
        String uti = args.get(PosIntegrate.CONFIG_TYPE.CT_UTI);
        if (uti == null || uti.isEmpty())
            return INVALID_ARG;

        performGet(activity, uti, false);

        return NO_ERROR;
    }
    private static String lastReceivedRRN;
    private static String lastReceivedAmount;
    private class StatusEvent {
        public String statusDescription;
        public int statusValue;

        public StatusEvent(String statusDescription, int statusValue) {
            this.statusDescription = statusDescription;
            this.statusValue = statusValue;
        }
    }

    private static void jsonDebug(Activity activity, JSONObject json) {
        try {
            Log.i(TAG, "POST RESPONSE:" + json.toString(3));

            ArrayList responseList = new ArrayList<TransactionResponse>();
            for(int i = 0; i<json.names().length(); i++){
                String keyname = json.names().getString(i);
                String keyvalue = json.getString(json.names().getString(i));
                Log.v(TAG, "key = " + json.names().getString(i) + " value = " + keyvalue);
                if (keyname.contains("Statuses")) {

                    Gson gson = new Gson();
                    ArrayList<StatusEvent> events = gson.fromJson(keyvalue, new TypeToken<ArrayList<StatusEvent> >(){}.getType());
                    for (StatusEvent e : events ) {
                        responseList.add(new TransactionResponse("Status:" + e.statusValue, e.statusDescription));
                    }
                } else {
                    responseList.add(new TransactionResponse(keyname, keyvalue));
                }
            }
            //updateUI(activity, "JSON Debug", responseList);

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static void processPostResult(Activity activity, JSONObject postResult) {

        try {
            String uti = postResult.getString("uti");
            //lastReceivedUTI = uti;
            if (uti != null && uti.length() > 0 ) {
                performGet(activity, uti, true);
            }

        } catch ( Exception e) {

        }

    }

    private static void processGetResult(Activity activity, JSONObject getResult, String uti, boolean recursive) {
        try {
            if (getResult != null) {
                if (getResult.has("retrievalReferenceNumber")) {
                    lastReceivedRRN = getResult.getString("retrievalReferenceNumber");
                    Log.d(TAG, "RRN: " + lastReceivedRRN);
                }
                if (getResult.has("amountTrans")) {
                    lastReceivedAmount = getResult.getString("amountTrans");
                    Log.d(TAG, "Transaction Amount: $" + lastReceivedAmount);
                }

                // Check if transaction is approved
                if (getResult.has("transApproved") && getResult.getBoolean("transApproved")) {
                    Log.i(TAG, "Transaction Finished Successfully");
                } else if (recursive) {
                    Thread.sleep(500);
                    performGet(activity, uti, true);
                }
            } else {
                Log.e(TAG, "processGetResult: Null response received.");
            }
        } catch (Exception e) {
            Log.e(TAG, "processGetResult: Exception occurred.", e);
        }
    }

    private static void performGet(Activity activity, String uti, boolean recursive) {

        try {

            // to prevent recursive calls to get we put it on a new thread, so the last thread can die without hanging around
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AndroidNetworking.get("https://localhost:8080/POSitiveWebLink/1.0.0/rest/transaction?disablePrinting=true&uti=" + uti + "&tid=" + Build.SERIAL)
                            .setPriority(Priority.MEDIUM)
                            .addHeaders("Authorization", "Bearer 6945595921271780")
                            .setOkHttpClient(getLocalHttpClient())
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    jsonDebug(activity, response);
                                    processGetResult(activity, response, uti, recursive);
                                }

                                @Override
                                public void onError(ANError error) {
                                   // updateUI(activity, "Network Error", null);
                                }
                            });
                }
            }).start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static void performPost(Activity activity, JSONObject request) {

        try {
            Log.d(TAG, "Simulating POS transaction request: " + request.toString(3));
            AndroidNetworking.post("https://localhost:8080/POSitiveWebLink/1.0.0/rest/transaction?disablePrinting=true&tid=" + Build.SERIAL)
                    .addStringBody(request.toString(3))
                    .addHeaders("Authorization", "Bearer 6945595921271780")
                    .setPriority(Priority.MEDIUM)
                    .setOkHttpClient(getLocalHttpClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            jsonDebug(activity, response);
                            processPostResult(activity, response);
                        }

                        @Override
                        public void onError(ANError error) {
                           // updateUI(activity, "Network Error", null);
                        }
                    });
        } catch ( Exception e ) {

        }

    }
//    private static void performPost(Activity activity, JSONObject request, TransactionCallback callback) {
//        try {
//            Log.d(TAG, "Sending transaction request: " + request.toString(3));
//
//            // Simulated delay (remove this if using real API)
//            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
//                try {
//                    JSONObject response = new JSONObject();
//                    response.put("transApproved", true); // Simulated transaction success
//                    response.put("retrievalReferenceNumber", "123456789"); // Fake reference number
//                    response.put("amountTrans", request.getString("amountTrans"));
//
//                    Log.d(TAG, "✅ Transaction Success: " + response.toString());
//
//                    if (callback != null) {
//                        Log.d(TAG, "🔔 Calling onTransactionSuccess()");
//                        callback.onTransactionSuccess(response);
//                    } else {
//                        Log.e(TAG, "⚠️ Callback is NULL!");
//                    }
//
//                } catch (Exception e) {
//                    Log.e(TAG, "❌ Exception in performPost: " + e.getMessage());
//                }
//            }, 3000); // Simulated delay of 3 seconds
//
//        } catch (Exception e) {
//            Log.e(TAG, "❌ Exception in performPost: " + e.getMessage());
//            if (callback != null) {
//                callback.onTransactionFailed("Transaction failed due to an exception: " + e.getMessage());
//            }
//        }
//    }

    private static int getIntFromString(String str, int defaultValue) {
        if (str != null && !str.trim().isEmpty()) {
            try {
                // Convert decimal amount (e.g., "50.00") into cents (e.g., 5000)
                return (int) (Double.parseDouble(str) * 100);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing amount: " + str, e);
            }
        }
        return defaultValue;
    }

    private static boolean isValidString(String text) {
        return !(text == null || text.trim().isEmpty());
    }

    private static OkHttpClient getLocalHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(120, TimeUnit.SECONDS);
            builder.readTimeout(120, TimeUnit.SECONDS);
            builder.writeTimeout(120, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
