package discreteunits.com.tastingroomdelmar.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by Sean on 4/16/16.
 */
public class FontManager {

    public static Typeface nexa;
    public static Typeface bebasReg;
    public static Typeface openSansLight;
    public static Typeface openSansItalic;

    public static FontManager singleton;

    public FontManager(Context context) {
        AssetManager assetManager = context.getAssets();

        nexa = Typeface.createFromAsset(assetManager, "fonts/nexarust/NexaRustScriptL-0.otf");
        bebasReg = Typeface.createFromAsset(assetManager, "fonts/bebas/BebasNeue Regular.otf");
        openSansLight = Typeface.createFromAsset(assetManager, "fonts/open-sans/OpenSans-Light.ttf");
        openSansItalic = Typeface.createFromAsset(assetManager, "fonts/open-sans/OpenSans-Italic.ttf");

        singleton = this;
    }

    public static FontManager getInstance() {
        return singleton;
    }

    public Typeface getTypeface(String name) {
        switch (name) {
            case Constants.NEXA: return nexa;
            case Constants.BEBA: return bebasReg;
            case Constants.OPENSANS_LIGHT: return openSansLight;
            default: return openSansItalic;
        }
    }
}
