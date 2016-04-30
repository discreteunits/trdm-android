package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.tastingroomdelmar.TastingRoomDelMar.BuildConfig;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.PaymentManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kor_s on 4/28/2016.
 */
public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = PaymentActivity.class.getSimpleName();

    private static final String PAYMENT = "Payment";
    private static final String ADD_PAYMENT = "Add Payment";

    private HashMap<String, String> cardObject;

    private Button mButtonPaymentMethod;
    private TextView mTVmessage;

    private Context mContext;

    TextView mTVCurrentActivityName;

    private TextView mTVCardNumberLabel;
    private EditText mETCardNumber;
    private TextView mTVEXPLabel;
    private EditText mETEXPMM;
    private EditText mETEXPYYYY;
    private TextView mTVCVCLabel;
    private EditText mETCVC;
    private String mCardBrand;

    private Button mButtonSave;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mContext = this;

        if (FontManager.getSingleton() == null) new FontManager(getApplicationContext());

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        final ImageView mIVUp = (ImageView) findViewById(R.id.up_button);
        mIVUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextView mTVPreviousActivityName = (TextView) findViewById(R.id.tv_prev_activity);
        mTVCurrentActivityName = (TextView) findViewById(R.id.tv_curr_activity);

        mTVPreviousActivityName.setText("Back");
        mTVPreviousActivityName.setTypeface(FontManager.nexa);

        mTVCurrentActivityName.setText(PAYMENT);
        mTVCurrentActivityName.setTypeface(FontManager.nexa);

        final ImageButton mImageButtonDrawer = (ImageButton) findViewById(R.id.nav_button);
        final ImageButton mImageButtonTab = (ImageButton) findViewById(R.id.current_order);

        mImageButtonDrawer.setVisibility(View.GONE);
        mImageButtonTab.setVisibility(View.GONE);

        mTVCardNumberLabel = (TextView) findViewById(R.id.tv_card_num);
        mETCardNumber = (EditText) findViewById(R.id.et_card_num);
        mTVEXPLabel = (TextView) findViewById(R.id.tv_card_exp);
        mETEXPMM = (EditText) findViewById(R.id.et_card_exp_mm);
        mETEXPYYYY = (EditText) findViewById(R.id.et_card_exp_yyyy);
        mTVCVCLabel = (TextView) findViewById(R.id.tv_card_cvc);
        mETCVC = (EditText) findViewById(R.id.et_card_cvc);
        mButtonSave = (Button) findViewById(R.id.btn_save_payment_method);

        mTVCardNumberLabel.setTypeface(FontManager.bebasReg);
        mETCardNumber.setTypeface(FontManager.bebasReg);
        mTVEXPLabel.setTypeface(FontManager.bebasReg);
        mETEXPMM.setTypeface(FontManager.bebasReg);
        mETEXPYYYY.setTypeface(FontManager.bebasReg);
        mTVCVCLabel.setTypeface(FontManager.bebasReg);
        mETCVC.setTypeface(FontManager.bebasReg);
        mButtonSave.setTypeface(FontManager.nexa);

        mButtonPaymentMethod = (Button) findViewById(R.id.btn_payment_method);
        mTVmessage = (TextView) findViewById(R.id.tv_payment);

        mButtonPaymentMethod.setTypeface(FontManager.bebasReg);
        mTVmessage.setTypeface(FontManager.openSansLight);

        modifyPaymentMethodMode(false);

        cardObject = PaymentManager.getSingleton().getPaymentMethod();

        if (cardObject != null) {
            updateCardButton();
        }

        mButtonPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPaymentMethodMode(true);
            }
        });

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mETCardNumber.getText().toString().isEmpty() && !mETEXPMM.getText().toString().isEmpty() &&
                    !mETEXPYYYY.getText().toString().isEmpty() && !mETCVC.getText().toString().isEmpty())
                    submitCard();
                else
                    Toast.makeText(mContext, "All fields are required!", Toast.LENGTH_SHORT).show();

            }
        });


        final ArrayList<String> listOfPattern=new ArrayList<>();

        final String ptVisa = "^4[0-9]{6,}$";
        listOfPattern.add(ptVisa);
        final String ptMasterCard = "^5[1-5][0-9]{5,}$";
        listOfPattern.add(ptMasterCard);
        final String ptAmeExp = "^3[47][0-9]{5,}$";
        listOfPattern.add(ptAmeExp);
        final String ptDiscover = "^6(?:011|5[0-9]{2})[0-9]{3,}$";
        listOfPattern.add(ptDiscover);

        mETCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("DEBUG", "afterTextChanged : "+s);
                String ccNum = s.toString();
                for(String p:listOfPattern) {
                    if(ccNum.matches(p)) {
                        Log.d("DEBUG", "afterTextChanged : " + p);
                        if (p.equals(ptVisa)) {
                            mETCardNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_visa, 0, 0, 0);
                            mCardBrand = Constants.VISA;
                        } else if (p.equals(ptMasterCard)) {
                            mETCardNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_master, 0, 0, 0);
                            mCardBrand = Constants.MASTER;
                        } else if (p.equals(ptAmeExp)) {
                            mETCardNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_amex, 0, 0, 0);
                            mCardBrand = Constants.AMEX;
                        } else if (p.equals(ptDiscover)) {
                            mETCardNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_discover, 0, 0, 0);
                            mCardBrand = Constants.DISCOVER;
                        } else {
                            mETCardNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_default, 0, 0, 0);
                            mCardBrand = "OTHER";
                        }

                        break;
                    } else {
                        mETCardNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_default, 0, 0, 0);
                        mCardBrand = "OTHER";
                    }
                }
            }
        });
    }

    private void modifyPaymentMethodMode(boolean isEditMode) {
        if (isEditMode) {
            mTVCardNumberLabel.setVisibility(View.VISIBLE);
            mETCardNumber.setVisibility(View.VISIBLE);
            mTVEXPLabel.setVisibility(View.VISIBLE);
            mETEXPMM.setVisibility(View.VISIBLE);
            mETEXPYYYY.setVisibility(View.VISIBLE);
            mTVCVCLabel.setVisibility(View.VISIBLE);
            mETCVC.setVisibility(View.VISIBLE);
            mButtonSave.setVisibility(View.VISIBLE);
            mTVCurrentActivityName.setText(ADD_PAYMENT);
        } else {
            mTVCardNumberLabel.setVisibility(View.GONE);
            mETCardNumber.setVisibility(View.GONE);
            mTVEXPLabel.setVisibility(View.GONE);
            mETEXPMM.setVisibility(View.GONE);
            mETEXPYYYY.setVisibility(View.GONE);
            mTVCVCLabel.setVisibility(View.GONE);
            mETCVC.setVisibility(View.GONE);
            mButtonSave.setVisibility(View.GONE);
            mTVCurrentActivityName.setText(PAYMENT);
        }
    }

    public void submitCard() {
        // TODO: replace with your own test key
        final String publishableApiKey = BuildConfig.DEBUG ?
                "pk_test_Ks6cqeQtnXJN0MQIkEOyAmKn " :
                getString(R.string.com_stripe_publishable_key);

        final Card card = new Card(mETCardNumber.getText().toString(),
                Integer.valueOf(mETEXPMM.getText().toString()),
                Integer.valueOf(mETEXPYYYY.getText().toString()),
                mETCVC.getText().toString());

        Stripe stripe = new Stripe();
        stripe.createToken(card, publishableApiKey, new TokenCallback() {
            public void onSuccess(Token token) {
                HashMap<String, String> map = new HashMap<>();
                map.put("brand", mCardBrand);
                map.put("last4", card.getLast4());
                PaymentManager.getSingleton().setPaymentMethod(map);
                addOrChangePaymentMethod(token.getId());
            }

            public void onError(Exception error) {
                Toast.makeText(mContext, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Stripe", error.getLocalizedMessage());
            }
        });
    }

    public void addOrChangePaymentMethod(String tokenId) {
        HashMap<String, String> params = new HashMap<>();
        String userObjectId = ParseUser.getCurrentUser().getObjectId();
        params.put("userId", userObjectId);
        params.put("stripeToken", tokenId);
        ParseCloud.callFunctionInBackground("addOrChangePaymentMethod", params, new FunctionCallback<String>() {
            @Override
            public void done(String object, ParseException e) {
                if (e == null) {
                    Log.d(TAG, object);
                    modifyPaymentMethodMode(false);
                    Toast.makeText(mContext, "Card information was successfully updated", Toast.LENGTH_SHORT).show();

                    cardObject = PaymentManager.getSingleton().getPaymentMethod();
                    updateCardButton();
                } else {
                    e.printStackTrace();
                    Toast.makeText(mContext, "There was an error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCardButton() {
        switch (cardObject.get("brand")) {
            case Constants.AMEX: mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_amex, 0, 0, 0); break;
            case Constants.VISA: mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_visa, 0, 0, 0); break;
            case Constants.MASTER: mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_master, 0, 0, 0); break;
            case Constants.DISCOVER: mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_discover, 0, 0, 0); break;
            default: mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_default, 0, 0, 0); break;
        }

        mButtonPaymentMethod.setText("PERSONAL ************" + cardObject.get("last4"));
    }

}
