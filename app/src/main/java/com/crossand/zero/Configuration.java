package com.crossand.zero;

import static android.util.Base64.DEFAULT;
import static android.util.Base64.decode;

public enum Configuration {
    PREF_NAME {
        @Override
        public String getData() {
            return "preferance file";
        }
    },
    STRING {
        @Override
        public String getData() {
            String cd = "";
            String decodedString;
            byte[] decodedBytes = decode(cd, DEFAULT);
            decodedString = new String(decodedBytes);
            return decodedString;
        }
    },
    ONESIGNAL_APP_ID {
        @Override
        public String getData() {
            return "";
        }
    },
    DEV {
        @Override
        public String getData() {
            return "";
        }
    },
    SERV {
        @Override
        public String getData() {
            String cd = "";
            String decodedString;
            byte[] decodedBytes = decode(cd, DEFAULT);
            decodedString = new String(decodedBytes);
            return decodedString;
        }
    },
    REALISE {
        @Override
        public String getData() {
            return "28/04/2021";
        }
    };


    public abstract String getData();
}
