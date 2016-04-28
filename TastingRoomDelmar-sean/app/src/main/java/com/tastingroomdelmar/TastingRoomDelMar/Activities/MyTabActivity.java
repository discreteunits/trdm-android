package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters.OrderListViewAdapter;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.OrderListItem;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OrderManager;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Sean on 4/17/16.
 */
public class MyTabActivity extends AppCompatActivity {

    SwipeMenuListView orderListView;
    OrderListViewAdapter adapter;
    ArrayList<OrderListItem> orderListItems;

    TextView mTVSubtotal;
    TextView mTVTax;
    TextView mTVTotal;

    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tab);

        mContext = this;

        if (FontManager.getSingleton() == null) new FontManager(this);

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

        OrderManager orderManager = OrderManager.getSingleton();

        if (orderManager == null) {
            Toast.makeText(this, "There was an error retrieving your order. Please try again", Toast.LENGTH_SHORT).show();
            return;
        }

        orderListItems = orderManager.getOrderListItems();
        adapter = new OrderListViewAdapter(this, orderListItems);

        orderListView = (SwipeMenuListView) findViewById(R.id.mytab_listView);
        orderListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final double subtotal = orderManager.getSubTotalPrice();
        final double tax = orderManager.getTaxPrice();
        final double grandtotal = subtotal + tax;

        final String formattedSubtotal = new DecimalFormat("0.00").format(subtotal);
        final String formattedTax = new DecimalFormat("0.00").format(tax);
        final String formattedGrandtotal = new DecimalFormat("0.00").format(grandtotal);

        mTVSubtotal.setText(formattedSubtotal);
        mTVTax.setText(formattedTax);
        mTVTotal.setText(formattedGrandtotal);

        final Dialog checkoutOptionDialog = new Dialog(mContext);
        checkoutOptionDialog.setContentView(R.layout.layout_checkout_options);

        final Button closeoutNow = (Button) checkoutOptionDialog.findViewById(R.id.btn_closeout_now);
        final Button closeoutServer = (Button) checkoutOptionDialog.findViewById(R.id.btn_closeout_server);
        final Button cancelCheckout = (Button) checkoutOptionDialog.findViewById(R.id.btn_closeout_cancel);

        final Dialog tableDialog = new Dialog(mContext);
        tableDialog.setContentView(R.layout.layout_checkout_table);

        final Dialog tipDialog = new Dialog(mContext);
        tipDialog.setContentView(R.layout.layout_checkout_tip);

        closeoutNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutOptionDialog.dismiss();
                tableDialog.show();
            }
        });

        closeoutServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutOptionDialog.dismiss();
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

        cancelTable.setTypeface(FontManager.nexa);
        placeOrderTable.setTypeface(FontManager.nexa);

        cancelTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableDialog.dismiss();
            }
        });

        placeOrderTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableDialog.dismiss();
                tipDialog.show();
            }
        });


        final Button cancelTip = (Button) tipDialog.findViewById(R.id.btn_tip_cancel);
        final Button placeOrderTip = (Button) tipDialog.findViewById(R.id.btn_tip_place_order);
        final TextView tvSubTotal = (TextView) tipDialog.findViewById(R.id.tv_tip_subtotal);
        final TextView tvTax = (TextView) tipDialog.findViewById(R.id.tv_tip_tax);
        final TextView tvGratuity = (TextView) tipDialog.findViewById(R.id.tv_tip_tip);
        final TextView tvTotal = (TextView) tipDialog.findViewById(R.id.tv_tip_total);
        final DiscreteSeekBar seekBar = (DiscreteSeekBar) tipDialog.findViewById(R.id.seekbar_tip);

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

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                double afterTip = subtotal * value/100;
                double totalAfterTip = grandtotal + afterTip;
                final String gratuity = new DecimalFormat("0.00").format(afterTip);
                final String grandTotal = new DecimalFormat("0.00").format(totalAfterTip);
                tvGratuity.setText(gratuity);
                tvTotal.setText(grandTotal);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar){}

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar){}
        });

        seekBar.setProgress(15); // default

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutOptionDialog.show();
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
                Toast.makeText(mContext, "To place order, tab the button and HOLD it!", Toast.LENGTH_SHORT).show();
            }
        });

        placeOrderTip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


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
}
