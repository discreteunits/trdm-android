package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.crashlytics.android.Crashlytics;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters.OrderListViewAdapter;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.OrderListItem;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OIDManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OrderManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.PaymentManager;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sean on 4/17/16.
 */
public class MyTabActivity extends AppCompatActivity {
    private static final String CURRENT_ACTIVITY = "My Tab";

    SwipeMenuListView orderListView;
    OrderListViewAdapter adapter;
    ArrayList<OrderListItem> orderListItems;

    ArrayList<String> tableNames;

    TextView mTVSubtotal;
    TextView mTVTax;
    TextView mTVTotal;

    ImageView mBGLogo;

    Dialog loadingDialog;

    Context mContext;

    Constants.CheckoutType checkoutType;
    String tableNumber;
    double subtotal;
    double tax;
    double tipAmount;
    double grandtotal;

    Dialog alertDialog;
    TextView alertTitle;
    TextView alertMsg;
    Button alertBtn;

    OrderListItem wineDiscountItem;
    OrderListItem beerDiscountItem;

    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tab);

        mContext = this;

        df.setRoundingMode(RoundingMode.HALF_UP);

        if (FontManager.getSingleton() == null) new FontManager(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        final ImageView mIVUp = (ImageView) findViewById(R.id.up_button);
        mIVUp.setVisibility(View.VISIBLE);
        mIVUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                onBackPressed();
            }
        });

        final TextView mTVPreviousActivityName = (TextView) findViewById(R.id.tv_prev_activity);
        final TextView mTVCurrentActivityName = (TextView) findViewById(R.id.tv_curr_activity);

        mTVPreviousActivityName.setText("Back");
        mTVPreviousActivityName.setTypeface(FontManager.nexa);
        mTVPreviousActivityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTVCurrentActivityName.setText(CURRENT_ACTIVITY);
        mTVCurrentActivityName.setTypeface(FontManager.nexa);

        final ImageButton mImageButtonDrawer = (ImageButton) findViewById(R.id.nav_button);
        final ImageButton mImageButtonTab = (ImageButton) findViewById(R.id.current_order);

        mImageButtonDrawer.setVisibility(View.GONE);
        mImageButtonTab.setVisibility(View.GONE);

        alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.layout_general_alert);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertTitle = (TextView) alertDialog.findViewById(R.id.tv_general_title);
        alertMsg = (TextView) alertDialog.findViewById(R.id.tv_general_msg);
        alertBtn = (Button) alertDialog.findViewById(R.id.btn_general_ok);
        alertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        /* fixed text views */
        final TextView qtyLabel = (TextView) findViewById(R.id.tv_qty_label);
        final TextView itemLabel = (TextView) findViewById(R.id.tv_item_label);
        final TextView priceLabel = (TextView) findViewById(R.id.tv_price_label);
        final TextView subtotalLabel = (TextView) findViewById(R.id.tv_subtotal_label);
        final TextView taxLabel = (TextView) findViewById(R.id.tv_tax_label);
        final TextView totalLabel = (TextView) findViewById(R.id.tv_total_label);
        final Button orderButton = (Button) findViewById(R.id.mytab_btn_order);

        qtyLabel.setTypeface(FontManager.bebasReg);
        itemLabel.setTypeface(FontManager.bebasReg);
        priceLabel.setTypeface(FontManager.bebasReg);
        subtotalLabel.setTypeface(FontManager.bebasReg);
        taxLabel.setTypeface(FontManager.bebasReg);
        totalLabel.setTypeface(FontManager.bebasBold);
        orderButton.setTypeface(FontManager.nexa);

        /* dynamic text views */
        mTVSubtotal = (TextView) findViewById(R.id.tv_subtotal );
        mTVTax = (TextView) findViewById(R.id.tv_tax);
        mTVTotal = (TextView) findViewById(R.id.tv_total);

        mTVSubtotal.setTypeface(FontManager.nexa);
        mTVTax.setTypeface(FontManager.nexa);
        mTVTotal.setTypeface(FontManager.nexa);
        mTVTotal.setTypeface(mTVTotal.getTypeface(), Typeface.BOLD);

        mBGLogo = (ImageView) findViewById(R.id.iv_logo_bg);

        final OrderManager orderManager = OrderManager.getSingleton();

        orderManager.setOrderCountListener(new OrderManager.OrderCountListener() {
            @Override
            public void onOrderCountChanged(int count) {
                if (count == 0)
                    mBGLogo.setVisibility(View.VISIBLE);
                else
                    mBGLogo.setVisibility(View.INVISIBLE);
            }
        });
        if (orderManager == null || orderManager.getOrderCount() == 0) {
            alertTitle.setText("Whoops!");
            alertMsg.setText("Looks like you don't have any items on your tab");
            alertDialog.show();
            alertDialog.setOnDismissListener(null);

            orderButton.setText("Back to menu");
            orderButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_soft_corner_button));
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return;
        } else {
            mBGLogo.setVisibility(View.INVISIBLE);
        }

        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_forever_white_48dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        orderListItems = orderManager.getOrderListItems();
        adapter = new OrderListViewAdapter(this, orderListItems);

        orderListView = (SwipeMenuListView) findViewById(R.id.mytab_listView);
        orderListView.setMenuCreator(swipeMenuCreator);

        orderListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        final OrderListItem item = orderListItems.get(position);
                        if (item.isDiscount()) break;

                        subtotal = subtotal - item.getItemPriceSum();
                        tax = tax - item.getItemTaxPrice();
                        grandtotal = subtotal + tax;

                        mTVSubtotal.setText(df.format(subtotal));
                        mTVTax.setText(df.format(tax));
                        mTVTotal.setText(df.format(grandtotal));

                        orderManager.addToSubTotal((-1) * item.getItemPriceSum());
                        orderManager.addToTax((-1) * item.getItemTaxPrice());

                        orderManager.removeItem(item.getIsDineIn(), item.getIsChoice(), item.getObjectId());
                        orderListItems.remove(position);
                        adapter.notifyDataSetChanged();

                        if (orderManager.getOrderCount() == 0) {
                            orderButton.setText("Back to menu");
                            orderButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_soft_corner_button));
                        }
                        break;
                }
                return true;
            }
        });

        orderListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        subtotal = orderManager.getSubTotalPrice();
        tax = orderManager.getTaxPrice();
        grandtotal = subtotal + tax;

        final String formattedSubtotal = df.format(subtotal);
        final String formattedTax = df.format(tax);
        final String formattedGrandtotal = df.format(grandtotal);

        mTVSubtotal.setText(formattedSubtotal);
        mTVTax.setText(formattedTax);
        mTVTotal.setText(formattedGrandtotal);

        final Dialog checkoutOptionDialog = new Dialog(mContext);
        checkoutOptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        checkoutOptionDialog.setContentView(R.layout.layout_checkout_options);
        checkoutOptionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView tvCloseoutTitle = (TextView) checkoutOptionDialog.findViewById(R.id.tv_closeout_title);
        final TextView tvCloseoutMsg = (TextView) checkoutOptionDialog.findViewById(R.id.tv_closeout_msg);
        final Button closeoutNow = (Button) checkoutOptionDialog.findViewById(R.id.btn_closeout_now);
        final Button closeoutServer = (Button) checkoutOptionDialog.findViewById(R.id.btn_closeout_server);
        final Button cancelCheckout = (Button) checkoutOptionDialog.findViewById(R.id.btn_closeout_cancel);
        tvCloseoutTitle.setTypeface(FontManager.bebasBold);
        tvCloseoutMsg.setTypeface(FontManager.openSansLight);
        closeoutNow.setTypeface(FontManager.openSansBold);
        closeoutServer.setTypeface(FontManager.openSansBold);
        cancelCheckout.setTypeface(FontManager.openSansBold);

        final Dialog tableDialog = new Dialog(mContext);
        tableDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tableDialog.setContentView(R.layout.layout_checkout_table);
        tableDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Dialog tipDialog = new Dialog(mContext);
        tipDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tipDialog.setContentView(R.layout.layout_checkout_tip);
        tipDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadingDialog = new Dialog(mContext);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.layout_checkout_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView tvPlacingOrder = (TextView) loadingDialog.findViewById(R.id.tv_placing_order);
        tvPlacingOrder.setTypeface(FontManager.nexa);

        final Button cancelTip = (Button) tipDialog.findViewById(R.id.btn_tip_cancel);
        final Button placeOrderTip = (Button) tipDialog.findViewById(R.id.btn_tip_place_order);
        final TextView tvTipTitle = (TextView) tipDialog.findViewById(R.id.tv_tip_title);
        final TextView tvTipMsg = (TextView) tipDialog.findViewById(R.id.tv_tip_msg);
        final TextView tvSubTotalLabel = (TextView) tipDialog.findViewById(R.id.tv_tip_subtotal_label);
        final TextView tvSubTotal = (TextView) tipDialog.findViewById(R.id.tv_tip_subtotal);
        final TextView tvTaxLabel = (TextView) tipDialog.findViewById(R.id.tv_tip_tax_label);
        final TextView tvTax = (TextView) tipDialog.findViewById(R.id.tv_tip_tax);
        final TextView tvGratuityLabel = (TextView) tipDialog.findViewById(R.id.tv_tip_tip_label);
        final TextView tvGratuity = (TextView) tipDialog.findViewById(R.id.tv_tip_tip);
        final TextView tvTotalLabel = (TextView) tipDialog.findViewById(R.id.tv_tip_total_label);
        final TextView tvTotal = (TextView) tipDialog.findViewById(R.id.tv_tip_total);
        final TextView tvGratuityGuide = (TextView) tipDialog.findViewById(R.id.tv_tip_guide_label);
        tvTipTitle.setTypeface(FontManager.bebasBold);
        tvTipMsg.setTypeface(FontManager.openSansLight);
        tvSubTotalLabel.setTypeface(FontManager.bebasReg);
        tvGratuityLabel.setTypeface(FontManager.bebasBold);
        tvTaxLabel.setTypeface(FontManager.bebasReg);
        tvTotalLabel.setTypeface(FontManager.bebasBold);
        tvGratuityGuide.setTypeface(FontManager.bebasReg);

        final DiscreteSeekBar seekBar = (DiscreteSeekBar) tipDialog.findViewById(R.id.seekbar_tip);
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                double afterTip = subtotal * value/100;
                tipAmount = value/100.0;

                double totalAfterTip = grandtotal + afterTip;
                final String gratuity = df.format(afterTip);
                final String grandTotal = df.format(totalAfterTip);
                tvGratuityLabel.setText("GRATUITY (" + value + "%)");
                tvGratuity.setText(gratuity);
                tvTotal.setText(grandTotal);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar){}

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar){}
        });

        closeoutNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutOptionDialog.dismiss();
                checkoutType = Constants.CheckoutType.STRIPE;
                tvTipTitle.setText("ADD GRATUITY");
                seekBar.setVisibility(View.VISIBLE);
                tvGratuityGuide.setVisibility(View.VISIBLE);
                tvGratuity.setVisibility(View.VISIBLE);
                tvGratuityLabel.setVisibility(View.VISIBLE);
                tipAmount = 0.18; // by default
                seekBar.setProgress(18); // default
                tableDialog.show();
            }
        });

        closeoutServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutOptionDialog.dismiss();
                checkoutType = Constants.CheckoutType.SERVER;
                tvTipTitle.setText("THANK YOU");
                seekBar.setVisibility(View.GONE);
                tvGratuityGuide.setVisibility(View.GONE);
                tvGratuity.setVisibility(View.GONE);
                tvGratuityLabel.setVisibility(View.GONE);
                tipAmount = 0; // by default
                seekBar.setProgress(0); // default
                tableDialog.show();
            }
        });

        cancelCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutOptionDialog.dismiss();
            }
        });

        final Button cancelTable = (Button) tableDialog.findViewById(R.id.btn_table_cancel);
        final Button placeOrderTable = (Button) tableDialog.findViewById(R.id.btn_table_place_order);
        final EditText etTableNumber = (EditText) tableDialog.findViewById(R.id.et_table_number);
        final TextView tvTableTitle = (TextView) tableDialog.findViewById(R.id.tv_table_title);
        final TextView tvTableMsg = (TextView) tableDialog.findViewById(R.id.tv_table_counter);

        cancelTable.setTypeface(FontManager.nexa);
        placeOrderTable.setTypeface(FontManager.nexa);
        etTableNumber.setTypeface(FontManager.bebasBold);
        tvTableTitle.setTypeface(FontManager.bebasBold);
        tvTableMsg.setTypeface(FontManager.openSansLight);

        etTableNumber.setHintTextColor(ContextCompat.getColor(this, R.color.light_grey));

        cancelTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableDialog.dismiss();
            }
        });

        placeOrderTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etTableNumber.getText().toString().isEmpty()) {
                    alertTitle.setText("Whoops!");
                    alertMsg.setText("Please enter table number.");
                    alertDialog.show();
                    alertDialog.setOnDismissListener(null);

                    return;
                }

                tableNames = new ArrayList<>();
                loadingDialog.show();
                ParseQuery<ParseObject> query = new ParseQuery<>("Table");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            for (ParseObject obj : objects) {
                                tableNames.add(obj.getString("name"));
                            }
                        } else {
                            e.printStackTrace();
                            alertTitle.setText("Whoops!");
                            alertMsg.setText("There was an error verifying table number. Please try again.");
                            alertDialog.show();
                            alertDialog.setOnDismissListener(null);

                            return;
                        }

                        loadingDialog.dismiss();

                        final String table = etTableNumber.getText().toString();

                        if ((tableNames != null && tableNames.size() != 0) || table.equals("99")) {
                            if (tableNames.contains(table) || table.equals("99")){
                                tableDialog.dismiss();
                                tableNumber = table;
                                tipDialog.show();
                            } else {
                                alertTitle.setText("Whoops!");
                                alertMsg.setText("Looks like that table does not exist. Please recheck your table number and try again, or contact your server for further assistance.");
                                tableDialog.dismiss();
                                alertDialog.show();
                                alertDialog.setOnDismissListener(null);

                                return;
                            }
                        } else {
                            alertTitle.setText("Whoops!");
                            alertMsg.setText("There was an error verifying table number. Please try again.");
                            alertDialog.show();
                            alertDialog.setOnDismissListener(null);

                            return;
                        }
                    }
                });


            }
        });

        cancelTip.setTypeface(FontManager.nexa);
        placeOrderTip.setTypeface(FontManager.nexa);
        tvSubTotal.setTypeface(FontManager.nexa);
        tvTax.setTypeface(FontManager.nexa);
        tvGratuity.setTypeface(FontManager.nexa);
        tvGratuity.setTypeface(mTVTotal.getTypeface(), Typeface.BOLD);
        tvTotal.setTypeface(FontManager.nexa);
        tvTotal.setTypeface(mTVTotal.getTypeface(), Typeface.BOLD);

        tvSubTotal.setText(formattedSubtotal);
        tvTax.setText(formattedTax);
        tvTotal.setText(formattedGrandtotal);

        if (OrderManager.getWineDiscount() != 0 || OrderManager.getBeerDiscount() != 0) {
            ParseQuery<ParseObject> query = new ParseQuery<>("Product");
            query.whereStartsWith("productType", "REDUCTIONONEPERCENTAGE");
            query.setLimit(1000);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        double totalWineDiscount = 0;
                        double totalBeerDiscount = 0;

                        if (OrderManager.getWineDiscount() != 0) {
                            final int count = OrderManager.getTakeawayWineBottleCount();
                            double discountBracket;

                            if (count == 0) discountBracket = 0;
                            else if (count > 0 && count < 3) discountBracket = 15;
                            else if (count >= 3 && count < 12) discountBracket = 20;
                            else discountBracket = 25;

                            for (ParseObject obj : objects) {
                                if (obj.getNumber("priceWithoutVat").doubleValue() == discountBracket) {
                                    if (wineDiscountItem == null)
                                        wineDiscountItem = new OrderListItem();

                                    boolean replaceFlag = false;
                                    int replaceIndex = 0;
                                    for (int i = 0; i < orderListItems.size(); i++) {
                                        OrderListItem listItem = orderListItems.get(i);
                                        if (listItem.isDiscount() && listItem.getDiscountType().equals("WINE")) {
                                            replaceFlag = true;
                                            replaceIndex = i;
                                        }

                                        if (!listItem.isDiscount() && listItem.getNeedDiscount() && listItem.getVineOrHop().equals("Vine")) {
                                            double basePrice = Double.parseDouble(listItem.getModPrices().split("\\n")[0]);
                                            double baseTaxRate = listItem.getBaseTaxRate()/100;

                                            if (!listItem.getDiscountApplied()) {
                                                orderManager.addToTax((-1) * ((basePrice + listItem.getWeightedCRV()) * baseTaxRate));
                                                orderManager.addToTax(((basePrice - (basePrice * (discountBracket / 100))) + listItem.getWeightedCRV()) * baseTaxRate);
                                                orderManager.addToSubTotal((-1) * (basePrice + listItem.getWeightedCRV()));
                                                orderManager.addToSubTotal(basePrice - (basePrice * (discountBracket / 100)) + listItem.getWeightedCRV());
                                            }
                                            totalWineDiscount += basePrice * (discountBracket/100);
                                            listItem.setDiscountApplied(true);
                                            //orderManager.addToSubTotal((basePrice + listItem.getWeightedCRV()) - (OrderManager.getWineDiscount() * Integer.parseInt(listItem.getQty())));
                                        }
                                    }

                                    wineDiscountItem.setName(obj.getString("name") + "(Vines)");
                                    wineDiscountItem.setBasePrice("(" + df.format(totalWineDiscount) + ")");
                                    wineDiscountItem.setQty(OrderManager.getTakeawayWineBottleCount() + "");
                                    wineDiscountItem.setIsDiscount(true, "WINE");
                                    wineDiscountItem.setIsDineIn(false);

                                    if (replaceFlag)
                                        orderListItems.set(replaceIndex, wineDiscountItem);
                                    else
                                        orderListItems.add(wineDiscountItem);

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        if (OrderManager.getBeerDiscount() != 0) {
                            final int count = OrderManager.getTakeawayBeerBottleCount();
                            double discountBracket;

                            if (count == 0) discountBracket = 0;
                            else discountBracket = 15;

                            for (ParseObject obj : objects) {
                                if (obj.getNumber("priceWithoutVat").doubleValue() == discountBracket) {
                                    if (beerDiscountItem == null)
                                        beerDiscountItem = new OrderListItem();

                                    boolean replaceFlag = false;
                                    int replaceIndex = 0;
                                    for (int i = 0; i < orderListItems.size(); i++) {
                                        OrderListItem listItem = orderListItems.get(i);
                                        if (listItem.isDiscount() && listItem.getDiscountType().equals("BEER")) {
                                            replaceFlag = true;
                                            replaceIndex = i;
                                        }

                                        if (!listItem.isDiscount() && listItem.getNeedDiscount() && listItem.getVineOrHop().equals("Hop")) {
                                            double basePrice = Double.parseDouble(listItem.getModPrices().split("\\n")[0]);
                                            double baseTaxRate = listItem.getBaseTaxRate()/100;

                                            if (!listItem.getDiscountApplied()) {
                                                orderManager.addToTax((-1) * ((basePrice + listItem.getWeightedCRV()) * baseTaxRate));
                                                orderManager.addToTax(((basePrice - (basePrice * (discountBracket / 100))) + listItem.getWeightedCRV()) * baseTaxRate);
                                                orderManager.addToSubTotal((-1) * (basePrice + listItem.getWeightedCRV()));
                                                orderManager.addToSubTotal(basePrice - (basePrice * (discountBracket / 100)) + listItem.getWeightedCRV());
                                            }
                                            totalBeerDiscount += basePrice * (discountBracket/100);
                                            listItem.setDiscountApplied(true);
                                        }

                                    }

                                    beerDiscountItem.setName(obj.getString("name") + "(Hops)");
                                    beerDiscountItem.setBasePrice("(" + df.format(totalBeerDiscount) + ")");
                                    beerDiscountItem.setQty(OrderManager.getTakeawayBeerBottleCount() + "");
                                    beerDiscountItem.setIsDiscount(true, "BEER");
                                    beerDiscountItem.setIsDineIn(false);

                                    //orderManager.addToSubTotal((-1) * OrderManager.getBeerDiscount());


                                    if (replaceFlag)
                                        orderListItems.set(replaceIndex, beerDiscountItem);
                                    else
                                        orderListItems.add(beerDiscountItem);

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        subtotal = orderManager.getSubTotalPrice();
                        tax = orderManager.getTaxPrice();
                        grandtotal = subtotal + tax;

                        final String formattedSubtotal = df.format(subtotal);
                        final String formattedTax = df.format(tax);
                        final String formattedGrandtotal = df.format(grandtotal);

                        mTVSubtotal.setText(formattedSubtotal);
                        mTVTax.setText(formattedTax);
                        mTVTotal.setText(formattedGrandtotal);

                        tvSubTotal.setText(formattedSubtotal);
                        tvTax.setText(formattedTax);
                        tvTotal.setText(formattedGrandtotal);

                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }

        final Dialog userDialog = new Dialog(mContext);
        userDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        userDialog.setContentView(R.layout.layout_checkout_no_user);
        userDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvUserTitle = (TextView) userDialog.findViewById(R.id.tv_checkout_user_title);
        TextView tvUserMsg = (TextView) userDialog.findViewById(R.id.tv_checkout_user_msg);
        Button loginButton = (Button) userDialog.findViewById(R.id.btn_closeout_user_login);
        Button createUserButton = (Button) userDialog.findViewById(R.id.btn_closeout_user_create_user);
        Button userCancelButton = (Button) userDialog.findViewById(R.id.btn_closeout_user_cancel);

        tvUserTitle.setTypeface(FontManager.bebasBold);
        tvUserMsg.setTypeface(FontManager.openSansLight);
        loginButton.setTypeface(FontManager.openSansBold);
        createUserButton.setTypeface(FontManager.openSansBold);
        userCancelButton.setTypeface(FontManager.openSansBold);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyTabActivity.this, LoginActivity.class);
                i.putExtra("ORIGIN", "MyTabActivity");

                startActivity(i);

                userDialog.dismiss();
            }
        });

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyTabActivity.this, SignUpLoginActivity.class);
                i.putExtra("LOGIN_OR_SIGNUP", Constants.SIGNUP_FLAG);
                i.putExtra("ORIGIN", "MyTabActivity");

                startActivity(i);

                userDialog.dismiss();
            }
        });

        userCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDialog.dismiss();
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderManager.getOrderCount() == 0) {
                    finish();
                } else {
                    checkoutOptionDialog.show();
                }
            }
        });

        cancelTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipDialog.dismiss();
            }
        });

        placeOrderTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertTitle.setText("Whoops!");
                alertMsg.setText("To place order, press and hold the Place Order button.");
                alertDialog.show();
                alertDialog.setOnDismissListener(null);
            }
        });

        placeOrderTip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (ParseUser.getCurrentUser() == null) {
                    userDialog.show();
                    return true;
                }

                try {
                    orderManager.setUser(ParseUser.getCurrentUser());
                    orderManager.saveCommons(checkoutType, tableNumber, tipAmount, ParseUser.getCurrentUser().getString("firstName") + " @ Table " + tableNumber );
                    orderManager.printOrder();

                    if (checkoutType == Constants.CheckoutType.STRIPE) {
                        if (PaymentManager.getSingleton().getPaymentMethod() == null) {
                            alertTitle.setText("Whoops!");
                            alertMsg.setText("Looks like you don’t have a credit card on file. Please add a card or checkout with your servers.");
                            alertDialog.show();

                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    Intent i = new Intent(MyTabActivity.this, PaymentActivity.class);
                                    startActivity(i);
                                }
                            });
                        } else {
                            placeOrder();
                        }
                    } else {
                        placeOrder();
                    }
                } catch (JSONException e) { e.printStackTrace(); }

                tipDialog.dismiss();

                return true;
            }
        });
    }


    public static class OrderItemViewHolder {
        public TextView tvQty;
        public TextView tvName;
        public TextView tvOption;
        public TextView tvBasePrice;
        public TextView tvModPrice;
    }

    private void placeOrder() {
        loadingDialog.show();
        Map<String, Object> params = OrderManager.getSingleton().getFinalizedOrderObject();
        ParseCloud.callFunctionInBackground("placeOrders", params, new FunctionCallback<String>() {
            @Override
            public void done(String object, ParseException e) {
                if (e == null) {
                    loadingDialog.dismiss();

                    OrderManager.clearOrders();
                    orderListItems.clear();

                    adapter.notifyDataSetChanged();

                    CategoryManager.setDinein(false);
                    CategoryManager.popAll();
                    OIDManager.popAll();

                    Log.d(CURRENT_ACTIVITY, object);

                    alertTitle.setText("Great Success!");
                    alertMsg.setText("Your order has been received. We’ll notify you once it’s been confirmed.");
                    alertDialog.show();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Intent intent = new Intent(MyTabActivity.this, Tier1Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                } else {
                    loadingDialog.dismiss();

                    alertTitle.setText("Whoops!");
                    alertMsg.setText("Unable to place this order at this time. Please try again later.");
                    alertDialog.show();
                    alertDialog.setOnDismissListener(null);

                    Crashlytics.log("MyTabActivity.placeOrder():" + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
