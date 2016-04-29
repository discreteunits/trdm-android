package com.tastingroomdelmar.TastingRoomDelMar.utils;

import java.util.HashMap;

/**
 * Created by kor_s on 4/28/2016.
 */
public class PaymentManager {
    private static HashMap<String, String> paymentMethod;

    private static PaymentManager singleton;

    public PaymentManager() {
        singleton = this;
    }

    public static PaymentManager getSingleton() {
        if (singleton == null) singleton = new PaymentManager();

        return singleton;
    }

    public HashMap<String, String> getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(HashMap<String, String> object) {
        paymentMethod = object;
    }
}
