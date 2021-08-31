package com.crossand.zero;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class AppDataClass extends Application {
    boolean Organic = false;
    String company = "";

    @Override
    public void onCreate() {
        super.onCreate();
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {


            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
//                Log.e("LOG_TAG", "attribute: " + " = " + conversionData.toString());
                for (String attrName : conversionData.keySet()) {
                    if (attrName.equals("af_status") && Objects.requireNonNull(conversionData.get(attrName)).toString().equals("Non-organic")) {
//                        Log.e("Logs", "campaining : " + Objects.requireNonNull(conversionData.get(attrName)).toString() );
                        Organic = true;
                    }
                    if (attrName.equals("campaign")) {
//                        Log.e("Logs", "campaining : "+  Objects.requireNonNull(conversionData.get(attrName)).toString() );
                        company = Objects.requireNonNull(conversionData.get(attrName)).toString();
                    }
                }
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date strDate = null;
                try {
                    strDate = sdf.parse(Configuration.REALISE.getData());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (new Date().after(strDate)) {
                    if (Organic) {
                        MainActivity.activity.initData(company);
                    } else {
                        MainActivity.activity.initData("");
                    }
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {

            }

            @Override
            public void onAttributionFailure(String errorMessage) {
            }
        };

        AppsFlyerLib.getInstance().init(Configuration.DEV.getData(), conversionListener, this);
        AppsFlyerLib.getInstance().start(this);
    }
}