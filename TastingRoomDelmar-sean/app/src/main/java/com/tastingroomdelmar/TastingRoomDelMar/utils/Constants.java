package com.tastingroomdelmar.TastingRoomDelMar.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Sean on 2/8/16.
 */
public class Constants {
    public static final int SIGNUP_FLAG = 0;
    public static final int LOGIN_FLAG = 1;
    public static final String SERVING = "SERVING";
    public static final String BOX_OFFICE = "BOX OFFICE";
    public static final String QUANTITY = "QUANTITY";
    public static final String GUESTS = "GUESTS";

    public enum Type {SERVING, ADDITION, QUANTITY}

    public static final String CHOICE = "CHOICE";
    public static final String GROUP = "CHOICE";
    public static final String OTHER = "OTHER";

    public static final String NEXA = "nexa";
    public static final String BEBA = "bebasReg";
    public static final String OPENSANS_LIGHT = "openSansLight";
    public static final String OPENSANS_ITALIC = "openItalic";

    public enum CheckoutType {STRIPE, SERVER}

    public static final String VISA = "Visa";
    public static final String MASTER = "MasterCard";
    public static final String DISCOVER = "Discover";
    public static final String AMEX = "American Express";
}
