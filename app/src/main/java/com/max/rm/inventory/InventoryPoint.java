package com.max.rm.inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class InventoryPoint extends AppCompatActivity {
    ImageView barCode;
    RecyclerView itemsRec;
    SearchView searchView;
    Api api;
    String store_num;
    dataBase db;
    int inv_id, inv_store_num;
    inventoryObject inv;
    items_adapter adapter;
    ArrayList<itemObject> itemsArr;
    Button saveBtn;
    int genrate_item_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_point);
        barCode = (ImageView) findViewById(R.id.barCode);
        itemsRec = (RecyclerView) findViewById(R.id.brands_list);
        searchView = (SearchView) findViewById(R.id.nameSearch);
        saveBtn = (Button) findViewById(R.id.save);
        api = new Api(InventoryPoint.this);
        db = new dataBase(this);
        inv = (inventoryObject) getIntent().getExtras().getSerializable(keys.inv_table_name);
        inv_id = inv.getInv_id();
        Log.d("inv_id", String.valueOf(inv_id));
        if (inv.getInv_done() == 0) { // لو نقطة الجرد لم يتم ترحلها
            inv_store_num = inv.getInv_store();
        } else {
            saveBtn.setVisibility(View.GONE);
            inv_store_num = 1;
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query=   query.toUpperCase(); //change all char to capital
                query = replaceArabicNumbers(query); // change arabic num to english num
                getItemByID(query, String.valueOf(inv_store_num));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // adapter.getFilter().filter(newText);
                return false;

            }
        });
        barCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent barcodeScreen = new Intent(InventoryPoint.this, BarCode.class);
                barcodeScreen.putExtra(keys.inv_table_name, inv);
                barcodeScreen.putExtra("items_arr", itemsArr);
                barcodeScreen.putExtra("generate_id", genrate_item_id);
                startActivity(barcodeScreen);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {  // ترحيل الجرد
            @Override
            public void onClick(View v) {
                if (itemsArr.size() > 0) {
                    com.max.rm.inventory.Dialog.dialogOkCancel(getString(R.string.finish_inventory), new dialog_interface() {
                        @Override
                        public void onDialogOkClick(AlertDialog alertDialog) {
                            alertDialog.dismiss();
                            String phone_name = Login.getMacAddr();
                            String post_data = api.createPointString(itemsArr, inv, phone_name, "AddNew_MACH_INV", "");
                            //  api.testVolley(post_data);
                            Log.d("post_data", post_data);
                            Api.SendInventoryPoint send = new Api.SendInventoryPoint(new RequestInterface() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("Success")) {
                                        dataBase db = new dataBase(InventoryPoint.this);
                                        SQLiteDatabase sql = db.getWritableDatabase();
                                        long delete = sql.delete(keys.inv_table_name, keys.inv_id_column + "=?", new String[]{String.valueOf(inv_id)});
                                        if (delete > 0) {
                                            long deleteitems = sql.delete(keys.item_table_name, keys.item_inv_id + "=?", new String[]{String.valueOf(inv_id)});
                                            if (deleteitems > 0) {
                                                window(getString(R.string.done_saved), InventoryPoint.this);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });
                            send.execute(post_data);
                        }

                        @Override
                        public void onDialogCancelClick(AlertDialog alertDialog) {
                            alertDialog.dismiss();
                        }
                    }, InventoryPoint.this);

                } else {
                    com.max.rm.inventory.Dialog.window(getString(R.string.not_found_inv), InventoryPoint.this);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchView.setQuery("",true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        if (inv.getInv_done() == 1) { // point have been save to server
            getItemByInvIdApi();
        } else {
            getItemsByInvId();
        }


    }

    public void getItemsByInvId() {
        itemsArr = new ArrayList<>();
        dataBase db = new dataBase(this);
        SQLiteDatabase sql = db.getReadableDatabase();
        Log.d("inv", String.valueOf(inv_id));

        Cursor c = sql.query(keys.item_table_name, null, keys.item_inv_id + "=?", new String[]{String.valueOf(inv_id)}, null, null, null);
        if (c.moveToFirst()) {
            do {
                String item_name = c.getString(c.getColumnIndex(keys.item_name));
                String item_size = c.getString(c.getColumnIndex(keys.item_size));
                String item_unit = c.getString(c.getColumnIndex(keys.item_unit));
                String item_barcode = c.getString(c.getColumnIndex(keys.item_barcode));
                int item_count = c.getInt(c.getColumnIndex(keys.item_count));
                String item_id = c.getString(c.getColumnIndex(keys.item_id));
                int item_record_id = c.getInt(c.getColumnIndex(keys.item_record_id));
                String item_desc = c.getString(c.getColumnIndex(keys.item_desc));
                String cost_price = c.getString(c.getColumnIndex(keys.COST_PRICE));
                String selling_price = c.getString(c.getColumnIndex(keys.SELLING_PRICE));
                String batch_num = c.getString(c.getColumnIndex(keys.BACH_NUM));
                String expire_date = c.getString(c.getColumnIndex(keys.EXPIRE_DATE));
                String item_store_num = c.getString(c.getColumnIndex(keys.item_store_num));
                String item_store_count_item = c.getString(c.getColumnIndex(keys.item_store_count));
                itemObject item = new itemObject(item_id, item_barcode, item_name, item_size, item_unit, item_desc, item_store_num, item_store_count_item, item_count, selling_price, cost_price, batch_num, expire_date, item_record_id);
                itemsArr.add(item);
                Log.d("itemsarr", itemsArr.toString());
            } while (c.moveToNext());
            adapter = new items_adapter(itemsArr, InventoryPoint.this, new rec_interface() {
                @Override
                public void onRecItemSelected(int position, View view) {
                    // go to details screen and edit item in sqlite
                    Intent i = new Intent(InventoryPoint.this, itemDetails.class);
                    i.putExtra(keys.ITEM, itemsArr.get(position));
                    i.putExtra(keys.inv_table_name, inv);
                    i.putExtra(keys.inv_store_num_column, Integer.parseInt(itemsArr.get(position).getStoreCode()));
                    i.putExtra(keys.Edit, true);
                    startActivity(i);

                }
            }, new rec_interface.longClickInf() {
                @Override
                public void onRecItemLongClick(final int position, View view) {
                    // delete if click delte or cancel
                    PopupMenu delete = new PopupMenu(InventoryPoint.this, view);
                    delete.getMenuInflater().inflate(R.menu.delte_menu, delete.getMenu());
                    delete.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // delete_window
                            delete_window(itemsArr.get(position).getRecord_id(), position);
                            return false;
                        }
                    });
                    delete.show();
                }
            });
            GridLayoutManager lLayout = new GridLayoutManager(this, 1);
            itemsRec.setLayoutManager(lLayout);
            itemsRec.setHasFixedSize(true);
            itemsRec.setAdapter(adapter);
        }
    }

    public void getItemByID(final String itemID, String storeNum) {

        // progress dialog visible
        // check if item bar code found or not
        //go to details item Screen with object of item
        final ArrayList<itemObject> itemsArray = new ArrayList<>();

     /*   final ProgressDialog dialog = ProgressDialog.show(this, getString(R.string.app_name),
                getString(R.string.Search)
                , true);*/
    ArrayList<itemObject> list  =      api.getItemByIdBarCodeSqlite(itemID) ;
     ItemsWindow(list);

     /*   api.getItemByBarCode(itemID, storeNum, new RequestInterface() {
            @Override
            public void onResponse(String response) {
                keys.log("getItem", response);
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray items = object.getJSONArray("JS_WH_DTL");
                    if (items.length() <= 0) {
                        com.max.rm.inventory.Dialog.window(getString(R.string.not_found), InventoryPoint.this);
                    } else {
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            itemObject itemData = createItemObject(item);
                            itemsArray.add(itemData);
                        }
                        keys.log("itemarr", itemsArray.toString());

                        ItemsWindow(itemsArray);
                    }
                    // to Itemlist window
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {

            }
        });*/
    }

    public itemObject createItemObject(JSONObject item) {
        try {
        String expireDate,batchNum ;
            String itemId = item.getString("ITEM_ID");
            String barCode = item.getString("BARCODE");
            String itemName = item.getString("I_NAME");
            String itemSize = item.getString("I_SIZE");
            String unit = item.getString("UNIT");
            String itemDesc = item.getString("I_DESC");
         //   String storeCode = item.getString("WH_CODE"); //WH_CODE
            String storeCode = "null"; //WH_CODE
           // String storeItemCount = item.getString("AVL_QTY"); //AVL_QTY
            String storeItemCount = "null"; //AVL_QTY
            String sellingPrice = "null"; //ITM_PRICE
            String costPrice = item.getString("ITM_STK_NEW"); //STK_NEW
            int inventoryItemCount = 0;
            itemObject itemObject = new itemObject(itemId, barCode, itemName, itemSize, unit, itemDesc, storeCode, storeItemCount, inventoryItemCount, sellingPrice, costPrice);

            if(item.getInt("USE_EXP_DATE")==1){
               expireDate = item.getString("EXP_DATE");
               itemObject.setExpire_date(expireDate);
}
            if(item.getInt("USE_BATCH_ID")==1){
                batchNum = item.getString("BATCH_ID");
                itemObject.setBatch_num(batchNum);

}

    /*int storeItemCount=0 ;
     if (!storeItemCountSt.equals("null")){
      storeItemCount=Integer.parseInt(storeItemCountSt) ;
     }

    if(sellingPrice.equals("null")){
        sellingPrice="0" ;
    }
    if(costPrice.equals("null")){
        costPrice="0" ;
    }*/


            keys.log("item", itemObject.toString());

            return itemObject;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void ItemsWindow(final ArrayList<itemObject> items) {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.item_search_list);
        ListView list = (ListView) d.findViewById(R.id.items_list);
        d.setTitle(getString(R.string.choose_item));
        itemSearchAdapater adapater = new itemSearchAdapater(items, InventoryPoint.this);
        list.setAdapter(adapater);
        d.show();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              //    search item in list
            if(!isItemFound(items.get(position) ,inv.getInv_done())){
                Intent i = new Intent(InventoryPoint.this, itemDetails.class);
                i.putExtra(keys.ITEM, items.get(position));
                i.putExtra(keys.inv_table_name, inv);
                i.putExtra("generate_id", genrate_item_id);
                i.putExtra(keys.inv_store_num_column, inv_store_num);
                startActivity(i);
                d.dismiss();
            }
            else {

           d.dismiss();

            }

            }
        });


    }

    public void delete_window(final int itemId, final int position) {
        com.max.rm.inventory.Dialog.dialogOkCancel(getString(R.
                string.confirm_delete), new dialog_interface() {
            @Override
            public void onDialogOkClick(AlertDialog alertDialog) {
                if (inv.getInv_done() == 0) {
                    deleteItem(itemId, position);
                } else {
                    deleteItemFromApi(itemId, position);
                }
            }

            @Override
            public void onDialogCancelClick(AlertDialog alertDialog) {

            }
        }, InventoryPoint.this);
    }

    public void deleteItem(int itemId, int position) {
        dataBase db = new dataBase(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        long delete = sql.delete(keys.item_table_name, keys.item_record_id + "=?", new String[]{String.valueOf(itemId)});
        if (delete > 0) {
            itemsArr.remove(position);
            adapter.notifyDataSetChanged();
        }

    }

    public void getItemByInvIdApi() {
        itemsArr = new ArrayList<>();
        SharedPreferences shared = getSharedPreferences(keys.SharedPrefrenceName, 0);
        String org_id = shared.getString(keys.ORG_ID, "");
        String post_data = "{\n" +
                "\"FuncationType\":\"GET_MACH_INV_DTL\",\n" +
                "\n" +
                "\"Org_id\":\"" + org_id + "\",\n" +
                "\n" +
                "\"MACH_INV\":\n" +
                "       {\n" +
                "           \"INV_NO\":\"" + inv_id + "\"\n" +
                "       }\n" +
                "\n" +
                "}";

        adapter = new items_adapter(itemsArr, InventoryPoint.this, new rec_interface() {
            @Override
            public void onRecItemSelected(int position, View view) {
                // go to details screen and edit item in sqlite
                Intent i = new Intent(InventoryPoint.this, itemDetails.class);
                i.putExtra(keys.ITEM, itemsArr.get(position));
                i.putExtra(keys.inv_table_name, inv);
                i.putExtra(keys.inv_store_num_column, Integer.parseInt(itemsArr.get(position).getStoreCode()));
                i.putExtra("edit_api", true);
                startActivity(i);

            }
        }, new rec_interface.longClickInf() {
            @Override
            public void onRecItemLongClick(final int position, View view) {
                PopupMenu delete = new PopupMenu(InventoryPoint.this, view);
                delete.getMenuInflater().inflate(R.menu.delte_menu, delete.getMenu());
                delete.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // delete_window
                        delete_window(itemsArr.get(position).getRecord_id(), position);
                        return false;
                    }
                });
                delete.show();
            }
        });
        GridLayoutManager lLayout = new GridLayoutManager(InventoryPoint.this, 1);
        itemsRec.setLayoutManager(lLayout);
        itemsRec.setHasFixedSize(true);
        itemsRec.setAdapter(adapter);
        Api.SendInventoryPoint send = new Api.SendInventoryPoint(new RequestInterface() {
            @Override
            public void onResponse(String response) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                    JSONArray invArr = obj.getJSONArray("JS_MACH_INV_DTL");
                    for (int i = 0; i < invArr.length(); i++) {
                        final JSONObject invObj = invArr.getJSONObject(i);
                        final int item_record_id = invObj.getInt("RCRD_NO");
                        final String unit = invObj.getString("UNIT");
                        String item_id = invObj.getString("ITEM_ID");
                        final int store_num = invObj.getInt("WH_CODE");
                        final int inventoryItemCount = invObj.getInt("ITM_QUANTY");
                        final String storeItemCount = invObj.getString("AVL_QTY"); //AVL_QTY
                        String barCode = invObj.getString("BARCODE");
                        String itemName = invObj.getString("I_NAME");
                        String itemSize = invObj.getString("P_SIZE");
                        String itemDesc = invObj.getString("ITM_DESCRPT");
                        itemObject itemObject = new itemObject(item_id, barCode, itemName, itemSize, unit, itemDesc,String.valueOf(store_num), storeItemCount, inventoryItemCount, "null", "null");
                        itemObject.setRecord_id(item_record_id);
                        if(invObj.has("BATCH_ID") ){
                          String patch_id =invObj.getString("BATCH_ID");
                            itemObject.setBatch_num(patch_id);
                        }
                        if( invObj.has("EXP_DATE") ){
                            String exp_date = invObj.getString("EXP_DATE2");
                            itemObject.setExpire_date(exp_date);
                            }
                        if (item_record_id > genrate_item_id) {
                            genrate_item_id = item_record_id;
                        }
                        itemsArr.add(itemObject);
                        adapter.notifyDataSetChanged();
                        keys.log("testtttttttta", response);
                   //     CommentedRequest();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {

            }
        });
        send.execute(post_data);
    }

    public void deleteItemFromApi(int item_record_id, final int position) {
        String delete_post_param = api.deleteBody("Delete_MACH_INV_OneItem", String.valueOf(inv_id), String.valueOf(item_record_id));
        Log.d("bodydelte", delete_post_param);
        Api.SendInventoryPoint send = new Api.SendInventoryPoint(new RequestInterface() {
            @Override
            public void onResponse(String response) {
                Log.d("delete_out_put", response);
                 if(response.equals("Success")){
                itemsArr.remove(position);
                adapter.notifyDataSetChanged();
                 }
                 else {
                     Toast.makeText(InventoryPoint.this, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                 }
            }

            @Override
            public void onError() {

            }
        });
        send.execute(delete_post_param);


    }

    public void window(String msg, final Activity activity) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                activity.finish();
            }
        });
        alertDialog.show();
    }
     public boolean isItemFound(itemObject item , final int type  ){
        for(int j = 0 ; j <itemsArr.size() ; j++) {
             final itemObject itemSaved = itemsArr .get(j) ;
            if(itemSaved.getItemId().equals(item.getItemId())&&itemSaved.getUnit().equals(item.getUnit())){
                editFoundItem(itemSaved , type);
           return true ;
            }
        }
    return false ;
    }


      public void editFoundItem(final itemObject item  , final int type){
          com.max.rm.inventory.Dialog.dialogOkCancel(getString(R.string.inv_before)+"\n"+ getString(R.string.count)+"="+item.getInventoryItemCount(), new dialog_interface() {
              @Override
              public void onDialogOkClick(AlertDialog alertDialog) {
                  if (type==1){ // api
                      Intent i = new Intent(InventoryPoint.this, itemDetails.class);
                      i.putExtra(keys.ITEM, item);
                      i.putExtra(keys.inv_table_name, inv);
                      i.putExtra(keys.inv_store_num_column, Integer.parseInt(item.getStoreCode()));
                      i.putExtra("edit_api", true);
                      startActivity(i);
                  }
                  else{
                      Intent i = new Intent(InventoryPoint.this, itemDetails.class);
                      i.putExtra(keys.ITEM, item);
                      i.putExtra(keys.inv_table_name, inv);
                      i.putExtra(keys.inv_store_num_column, Integer.parseInt(item.getStoreCode()));
                      i.putExtra(keys.Edit, true);
                      startActivity(i);

                  }
                  // go edit this item
              }

              @Override
              public void onDialogCancelClick(AlertDialog alertDialog) {

              }
          }, this);
      }
    public String replaceArabicNumbers(String original) {
        return original.replaceAll("١","1")
                .replaceAll("٢","2")
                .replaceAll("٣","3")
                .replaceAll("٤","4")
                .replaceAll("٥","5")
                .replaceAll("٦","6")
                .replaceAll("٧","7")
                .replaceAll("٨","8")
                .replaceAll("٩","9")
                .replaceAll("٠","0") ;
    }
    public  void CommentedRequest(){
          /*  api.getItemByBarCode(item_id, String.valueOf(store_num), new RequestInterface() {
                            @Override
                            public void onResponse(String response) {

                                keys.log("getItem", response);
                                try {
                                    JSONObject object = new JSONObject(response);
                                    JSONArray items = object.getJSONArray("JS_WH_DTL");
                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject item = items.getJSONObject(i);
                                        if( invObj.has("BATCH_ID") ){
                                                item.put("BATCH_ID",invObj.getString("BATCH_ID"));
                                        }
                                        if( invObj.has("EXP_DATE") ){

                                                String exp_date = invObj.getString("EXP_DATE2");
                                                item.put("EXP_DATE",exp_date);
                                        }
                                        String item_unit = item.getString("UNIT");
                                        if (item_unit.equals(unit)) {
                                            itemObject itemob = createItemObject(item);
                                            itemob.setRecord_id(item_record_id);
                                            if (item_record_id > genrate_item_id) {
                                                genrate_item_id = item_record_id;
                                            }
                                            itemob.setInventoryItemCount(inventoryItemCount);
                                            itemob.setStoreCode(String.valueOf(store_num));
                                            itemob.setStoreItemCount(storeItemCount);
                                            itemsArr.add(itemob);
                                            adapter.notifyDataSetChanged();
                                        }


                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError() {

                            }
                        });*/
    }


}
