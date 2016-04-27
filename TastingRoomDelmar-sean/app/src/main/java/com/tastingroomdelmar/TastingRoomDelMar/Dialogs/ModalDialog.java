package com.tastingroomdelmar.TastingRoomDelMar.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters.ModalListViewAdapter;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ItemListObject;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ModalListItem;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.OrderListItem;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ServingListItem;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OrderManager;

/**
 * Created by Sean on 4/14/16.
 */
public class ModalDialog extends Dialog implements android.view.View.OnClickListener {
    private static final String TAG = ModalDialog.class.getSimpleName();

    private AppCompatActivity mContext;

    private ItemListObject item;
    private boolean isEvent;

    private ArrayList<ModalListItem> modalItems;
    private ModalListViewAdapter adapter;
    private ListView modalListView;

    public ModalDialog(AppCompatActivity context, ItemListObject item, boolean isEvent) {
        super(context);
        mContext = context;
        this.item = item;
        this.isEvent = isEvent;

        modalItems = new ArrayList<>();

        adapter = new ModalListViewAdapter(context, modalItems);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_modal);

        if (FontManager.getSingleton() == null) new FontManager(mContext.getApplicationContext());

        final TextView name = (TextView) findViewById(R.id.modal_tv_name);
        final TextView info = (TextView) findViewById(R.id.modal_tv_info);
        final Button add = (Button) findViewById(R.id.modal_btn_add);
        final Button cancel = (Button) findViewById(R.id.modal_btn_cancel);

        name.setTypeface(FontManager.bebasReg);
        info.setTypeface(FontManager.openSansLight);
        add.setTypeface(FontManager.nexa);
        cancel.setTypeface(FontManager.nexa);

        name.setText(item.getName());
        info.setText(item.getAltName());

        add.setOnClickListener(this);
        cancel.setOnClickListener(this);

        modalListView = (ListView) findViewById(R.id.modal_listview);
        modalListView.setAdapter(adapter);

        String basePrice =  item.getPrices().split(",")[0];
        if (basePrice == null) basePrice = item.getPrices();

        basePrice = new DecimalFormat("0.00").format(Double.parseDouble(basePrice));

        if (!isEvent) {
            if (item.getServings() != null && !item.getServings().isEmpty()) {
                modalItems.add(new ModalListItem(item.getObjectId(), item.getName(), basePrice, item.getTaxRate(),Constants.Type.SERVING, item.getProductType(), Constants.SERVING, item.getServings()));
            }

            if (item.getAdditions() != null && !item.getAdditions().isEmpty()) {
                for (ModalListItem modalItem : item.getAdditions())
                    modalItems.add(modalItem);
            }

            modalItems.add(new ModalListItem(item.getName(), basePrice, item.getTaxRate(), item.getProductType(), item.getObjectId()));
        } else {
            if (item.getServings() != null && !item.getServings().isEmpty()) {
                modalItems.add(new ModalListItem(item.getObjectId(), item.getName(), basePrice, item.getTaxRate(), Constants.Type.SERVING, item.getProductType(), Constants.BOX_OFFICE, item.getServings()));
            }
            modalItems.add(new ModalListItem(item.getName(), basePrice, item.getTaxRate(), item.getProductType(), item.getObjectId()));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modal_btn_add:
                OrderManager orderManager;
                if (OrderManager.getSingleton() == null) {
                     orderManager = new OrderManager();
                } else {
                    orderManager = OrderManager.getSingleton();
                }

                try {
                    JSONObject orderItem = new JSONObject();
                    JSONArray modifiers = new JSONArray();

                    try {
                        boolean servingIdAdded = false;

                        OrderListItem orderListItem = new OrderListItem();

                        for (ModalListItem modalItem : modalItems) {
                            boolean servingExists = false;

                            if(modalItem.getProductType().equals("CHOICE")) {
                                JSONObject parentItem = new JSONObject();
                                parentItem.put("amount", 1);
                                parentItem.put("objectId", modalItem.getBaseObjectId());
                                parentItem.put("modifiers", new JSONArray());

                            }

                            if (modalItem.getType() == Constants.Type.QUANTITY) {
                                orderItem.put("amount", modalItem.getSelectedOptionItem().getOptionName());
                                orderListItem.setQty(modalItem.getSelectedOptionItem().getOptionName());
                            }

                            else if (modalItem.getType() == Constants.Type.SERVING) {
                                orderItem.put("objectId", modalItem.getSelectedOptionItem().getObjectId());

                                orderListItem.setOptions(modalItem.getSelectedOptionItem().getOptionName());
                                orderListItem.setModPrices(new DecimalFormat("0.00").format(modalItem.getSelectedOptionItem().getPrice()));
                                servingExists = true;
                            }

                            else {
                                JSONObject modifierObject = new JSONObject();
                                modifierObject.put("modifierId", modalItem.getSelectedOptionItem().getModifierId());
                                modifierObject.put("modifierValueId", modalItem.getSelectedOptionItem().getModifierValueId());
                                modifierObject.put("price", new DecimalFormat("0.00").format(modalItem.getSelectedOptionItem().getPrice()));
                                modifierObject.put("priceWithoutVat", modalItem.getSelectedOptionItem().getPriceWithoutVat());

                                orderListItem.setOptions(modalItem.getTitle() + " (" + modalItem.getSelectedOptionItem().getOptionName() + ")" + "\n");
                                orderListItem.setModPrices(modalItem.getSelectedOptionItem().getPrice() == 0 ?
                                        "\n" : new DecimalFormat("0.00").format(modalItem.getSelectedOptionItem().getPrice()) + "\n");

                                modifiers.put(modifierObject);
                            }

                            if (!servingExists && !servingIdAdded) {
                                orderItem.put("objectId", modalItem.getSelectedOptionItem().getObjectId());
                                servingIdAdded = true;
                            }

                            orderListItem.setName(modalItem.getBaseItemName());
                            orderListItem.setBasePrice(modalItem.getBaseItemPrice());
                            orderListItem.setProductType(modalItem.getProductType());
                            orderListItem.setBaseTaxRate(modalItem.getBaseTaxRate());
                        }

                        orderListItem.updatePrices();
                        orderManager.addToOrderList(orderListItem);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    orderItem.put("modifiers", modifiers);

                    if (CategoryManager.isDinein())
                        orderManager.addToDineIn(orderItem);
                    else
                        orderManager.addToTakeAway(orderItem);

                    orderManager.printOrder();

                    Toast.makeText(mContext, "Order Added", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.modal_btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
