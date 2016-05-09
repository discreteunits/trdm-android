package com.tastingroomdelmar.TastingRoomDelMar.Dialogs;

import android.app.Dialog;
import android.content.Intent;
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

import com.tastingroomdelmar.TastingRoomDelMar.Activities.MyTabActivity;
import com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters.ModalListViewAdapter;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ItemListObject;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ModalListItem;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.OptionListItem;
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

    Dialog alertDialog;
    Button alertBtnBackToMenu;
    Button alertBtnGoToTab;

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

        alertDialog = new Dialog(mContext);
        alertDialog.setContentView(R.layout.layout_added_to_tab);
        alertBtnBackToMenu = (Button) alertDialog.findViewById(R.id.btn_added_tab_back_to_menu);
        alertBtnGoToTab = (Button) alertDialog.findViewById(R.id.btn_added_tab_go_to_tab);
        alertBtnBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertBtnGoToTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyTabActivity.class);
                mContext.startActivity(intent);
                alertDialog.dismiss();
            }
        });

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

        String[] basePrice = item.getPriceArray();

        ArrayList<OptionListItem> servings = item.getServings();
        if (!isEvent) {
            if (servings != null && !servings.isEmpty()) {
                modalItems.add(new ModalListItem(item.getObjectId(), item.getName(), basePrice, item.getTaxRate(),Constants.Type.SERVING, item.getProductType(), Constants.SERVING, servings));
            }

            if (item.getAdditions() != null && !item.getAdditions().isEmpty()) {
                for (ModalListItem modalItem : item.getAdditions())
                    modalItems.add(modalItem);
            }

            modalItems.add(new ModalListItem(item.getName(), basePrice, item.getTaxRate(), item.getProductType(), item.getObjectId()));
        } else {
            if (servings != null && !servings.isEmpty()) {
                modalItems.add(new ModalListItem(item.getObjectId(), item.getName(), basePrice, item.getTaxRate(), Constants.Type.SERVING, item.getProductType(), Constants.BOX_OFFICE, servings));
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
                        boolean parentAdded = false;
                        boolean parentOrderIdAdded = false;

                        OrderListItem orderListItem = new OrderListItem();
                        boolean servingExists = false;

                        for (ModalListItem modalItem : modalItems) {

                            if(modalItem.getProductType().equals("CHOICE") && !parentAdded) {
                                JSONObject parentItem = new JSONObject();
                                parentItem.put("amount", 1);
                                parentItem.put("objectId", modalItem.getBaseObjectId());
                                parentItem.put("modifiers", new JSONArray());

                                if (CategoryManager.isDinein())
                                    orderManager.addToDineIn(true, parentItem);
                                else
                                    orderManager.addToTakeAway(true, parentItem);

                                orderListItem.setIsChoice(true);
                                orderListItem.setObjectId(modalItem.getBaseObjectId());
                                parentOrderIdAdded = true;
                                parentAdded = true;
                            }

                            if (modalItem.getType() == Constants.Type.QUANTITY) {
                                orderItem.put("amount", Integer.parseInt(modalItem.getSelectedOptionItem().getOptionName()));
                                orderListItem.setQty(modalItem.getSelectedOptionItem().getOptionName());
                            }

                            else if (modalItem.getType() == Constants.Type.SERVING) {
                                if (modalItem.getSelectedOptionItem() instanceof ServingListItem) {
                                    orderItem.put("objectId", ((ServingListItem) modalItem.getSelectedOptionItem()).getObjectId());
                                    if (!parentOrderIdAdded)
                                        orderListItem.setObjectId(((ServingListItem) modalItem.getSelectedOptionItem()).getObjectId());
                                }
                                else {
                                    orderItem.put("objectId", modalItem.getSelectedOptionItem().getBaseObjectId());
                                    if (!parentOrderIdAdded)
                                        orderListItem.setObjectId(modalItem.getSelectedOptionItem().getBaseObjectId());
                                }

                                orderListItem.setOptions(modalItem.getSelectedOptionItem().getOptionName());
                                orderListItem.setModPrices(new DecimalFormat("0.00").format(modalItem.getSelectedOptionItem().getPrice()));
                                servingExists = true;
                            }

                            else {
                                JSONObject modifierObject = new JSONObject();
                                modifierObject.put("modifierId", modalItem.getSelectedOptionItem().getModifierId());
                                modifierObject.put("modifierValueId", modalItem.getSelectedOptionItem().getModifierValueId());
                                modifierObject.put("price", modalItem.getSelectedOptionItem().getPrice());
                                modifierObject.put("priceWithoutVat", modalItem.getSelectedOptionItem().getPriceWithoutVat()[0]);

                                orderListItem.setOptions(modalItem.getTitle() + " (" + modalItem.getSelectedOptionItem().getOptionName() + ")" + "\n");
                                orderListItem.setModPrices(modalItem.getSelectedOptionItem().getPrice() == 0 ?
                                        "\n" : new DecimalFormat("0.00").format(modalItem.getSelectedOptionItem().getPrice()) + "\n");

                                modifiers.put(modifierObject);
                            }

                            if (!servingExists && !servingIdAdded) {
                                orderItem.put("objectId", modalItem.getSelectedOptionItem().getBaseObjectId());
                                if (!parentOrderIdAdded)
                                    orderListItem.setObjectId(modalItem.getSelectedOptionItem().getBaseObjectId());
                                servingIdAdded = true;
                            }

                            orderListItem.setName(modalItem.getBaseItemName());
                            orderListItem.setProductType(modalItem.getProductType());
                            if (CategoryManager.isDinein()) {
                                orderListItem.setBasePrice(modalItem.getBaseItemPrice()[0]);
                                orderListItem.setBaseTaxRate(modalItem.getBaseTaxRate()[0]);
                            } else {
                                orderListItem.setBasePrice(modalItem.getBaseItemPrice()[1]);
                                orderListItem.setBaseTaxRate(modalItem.getBaseTaxRate()[1]);
                            }

                        }

                        if (CategoryManager.isDinein())
                            orderListItem.setIsDineIn(true);
                        else
                            orderListItem.setIsDineIn(false);

                        orderListItem.updatePrices();
                        orderManager.addToOrderList(orderListItem);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    orderItem.put("modifiers", modifiers);

                    if (CategoryManager.isDinein())
                        orderManager.addToDineIn(false, orderItem);
                    else
                        orderManager.addToTakeAway(false, orderItem);

                    orderManager.printOrder();

                    alertDialog.show();
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
