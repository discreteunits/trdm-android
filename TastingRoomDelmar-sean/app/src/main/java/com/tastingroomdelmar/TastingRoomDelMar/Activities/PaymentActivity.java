package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.RelativeLayout;
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
import com.tastingroomdelmar.TastingRoomDelMar.utils.OrderManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.PaymentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * Created by kor_s on 4/28/2016.
 */
public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = PaymentActivity.class.getSimpleName();

    private static final int MY_SCAN_REQUEST_CODE = 1;
    private static final String PAYMENT = "Payment";
    private static final String ADD_PAYMENT = "Add Payment";

    Dialog loadingDialog;

    private HashMap<String, String> cardObject;

    private Button mButtonPaymentMethod;
    private TextView mTVmessage;

    private Context mContext;

    TextView mTVCurrentActivityName;

    private RelativeLayout mEditLayout;
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

        loadingDialog = new Dialog(mContext);
        loadingDialog.setContentView(R.layout.layout_loading_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        final TextView tvPleaseWait = (TextView) loadingDialog.findViewById(R.id.tv_please_wait);
        tvPleaseWait.setTypeface(FontManager.nexa);

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
        mTVPreviousActivityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTVCurrentActivityName.setText(PAYMENT);
        mTVCurrentActivityName.setTypeface(FontManager.nexa);

        final ImageButton mImageButtonDrawer = (ImageButton) findViewById(R.id.nav_button);
        final ImageButton mImageButtonTab = (ImageButton) findViewById(R.id.current_order);

        mImageButtonDrawer.setVisibility(View.GONE);
        mImageButtonTab.setVisibility(View.GONE);

        mButtonSave = (Button) findViewById(R.id.btn_save_payment_method);
        mButtonSave.setTypeface(FontManager.nexa);

        mEditLayout = (RelativeLayout) findViewById(R.id.rl_payment_edit);

        mTVCardNumberLabel = (TextView) findViewById(R.id.tv_card_num);
        mETCardNumber = (EditText) findViewById(R.id.et_card_num);
        mTVEXPLabel = (TextView) findViewById(R.id.tv_card_exp);
        mETEXPMM = (EditText) findViewById(R.id.et_card_exp_mm);
        mETEXPYYYY = (EditText) findViewById(R.id.et_card_exp_yyyy);
        mTVCVCLabel = (TextView) findViewById(R.id.tv_card_cvc);
        mETCVC = (EditText) findViewById(R.id.et_card_cvc);

        mTVCardNumberLabel.setTypeface(FontManager.bebasReg);
        mETCardNumber.setTypeface(FontManager.bebasReg);
        mTVEXPLabel.setTypeface(FontManager.bebasReg);
        mETEXPMM.setTypeface(FontManager.bebasReg);
        mETEXPYYYY.setTypeface(FontManager.bebasReg);
        mTVCVCLabel.setTypeface(FontManager.bebasReg);
        mETCVC.setTypeface(FontManager.bebasReg);

        mButtonPaymentMethod = (Button) findViewById(R.id.btn_payment_method);
        mTVmessage = (TextView) findViewById(R.id.tv_payment);

        final Button mCameraButton = (Button) findViewById(R.id.btn_use_camera);
        final Button mRemoveCard = (Button) findViewById(R.id.btn_remove_card);

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanPress(v);
            }
        });

        mRemoveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "To remove card, press the button and HOLD it", Toast.LENGTH_SHORT).show();
            }

        });

        mRemoveCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loadingDialog.show();
                Map<String, Object> params = new HashMap<>();
                params.put("userId", ParseUser.getCurrentUser().getObjectId());
                ParseCloud.callFunctionInBackground("deleteCardFromStripeCustomer", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String object, ParseException e) {
                        if (e == null) {
                            Toast.makeText(mContext, "Card successfully removed", Toast.LENGTH_SHORT).show();
                            modifyPaymentMethodMode(false);
                            mButtonPaymentMethod.setText("+Add Payment Method");
                            mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_default, 0, 0, 0);
                            PaymentManager.getSingleton().clearPaymentMethod();
                        } else {
                            e.printStackTrace();
                            Toast.makeText(mContext, "There was an error!", Toast.LENGTH_SHORT).show();
                        }

                        loadingDialog.dismiss();
                    }
                });

                return true;
            }
        });

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

        if (ParseUser.getCurrentUser() == null) {
            Toast.makeText(mContext, "You must be logged in to add payment", Toast.LENGTH_SHORT).show();
            mButtonPaymentMethod.setEnabled(false);
            mButtonSave.setText("Back");
            mButtonSave.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_soft_corner_button));
            mButtonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

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
            mEditLayout.setVisibility(View.VISIBLE);
            mTVCurrentActivityName.setText(ADD_PAYMENT);
            mButtonSave.setText("Save");
            mButtonSave.setBackground(ContextCompat.getDrawable(mContext, R.drawable.green_soft_corner_button));
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
        } else {
            mEditLayout.setVisibility(View.GONE);
            mTVCurrentActivityName.setText(PAYMENT);
            mButtonSave.setText("Back");
            mButtonSave.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_soft_corner_button));
            mButtonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public void submitCard() {
        loadingDialog.show();
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
                loadingDialog.dismiss();
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
                    loadingDialog.dismiss();
                } else {
                    e.printStackTrace();
                    Toast.makeText(mContext, "There was an error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void updateCardButton() {
        if (cardObject != null) {
            switch (cardObject.get("brand")) {
                case Constants.AMEX:
                    mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_amex, 0, 0, 0);
                    break;
                case Constants.VISA:
                    mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_visa, 0, 0, 0);
                    break;
                case Constants.MASTER:
                    mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_master, 0, 0, 0);
                    break;
                case Constants.DISCOVER:
                    mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_discover, 0, 0, 0);
                    break;
                default:
                    mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_default, 0, 0, 0);
                    break;
            }

            mButtonPaymentMethod.setText("PERSONAL ************" + cardObject.get("last4"));
        } else {
            mButtonPaymentMethod.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_default, 0, 0, 0);
            mButtonPaymentMethod.setText("+ADD PAYMENT METHOD");
        }
    }

    private void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );
                mETCardNumber.setText(scanResult.cardNumber);

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                    mETEXPMM.setText(scanResult.expiryMonth+"");
                    mETEXPYYYY.setText(scanResult.expiryYear+"");
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                    mETCVC.setText(scanResult.cvv);
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
                Toast.makeText(mContext, resultDisplayStr, Toast.LENGTH_SHORT).show();
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }
}
