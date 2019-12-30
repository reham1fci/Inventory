package com.max.rm.inventory;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.Type;


/**
 * Created by Eng.Reham Mokhtar
 */

public class itemDetails extends AppCompatActivity {
    TextView nameTv, unitTv, idTv, descTv, costPriceTv, sellingPriceTv, batchNumTv, expireDateTv, item_store_note, avl_quantity;
    ImageButton addCount, subCount;
    EditText expireDate, batchNumEd, countEd;
    Spinner storeNumEd;
    Button submit;
    ProgressDialog dialog;
    CardView costPriceCard, sellingPriceCard;
    SharedPreferences shared;
    int itemCount = 0;
    int batch_num, expire_date;
    int inv_id, store_num;
    inventoryObject inv;
    boolean Edit = false;
    Api api;
    ArrayList<String> ExpArr, batchArr;
    ArrayList<Integer> store_ids = new ArrayList<>() ;

    ArrayList<Integer> stores;
    HashMap<Integer, String> store_cost;
    HashMap<Integer, String> store_Avl_q;
    ArrayList<storeObject> stores_inf_list;
    itemObject item ;
    boolean edit_api_item = false; // item اترحل من قبل

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        nameTv = (TextView) findViewById(R.id.item_name);
        unitTv = (TextView) findViewById(R.id.item_unit);
        idTv = (TextView) findViewById(R.id.item_id);
        descTv = (TextView) findViewById(R.id.desc);
        costPriceTv = (TextView) findViewById(R.id.cost_price);
        sellingPriceTv = (TextView) findViewById(R.id.selling_price);
        batchNumTv = (TextView) findViewById(R.id.batch_num_tv);
        expireDateTv = (TextView) findViewById(R.id.expire_date_tv);
        item_store_note = (TextView) findViewById(R.id.item_store_note);
        avl_quantity = (TextView) findViewById(R.id.avl_quant);
        addCount = (ImageButton) findViewById(R.id.add);
        subCount = (ImageButton) findViewById(R.id.sub);
        storeNumEd = (Spinner) findViewById(R.id.store_num);
        expireDate = (EditText) findViewById(R.id.expire_date);
        batchNumEd = (EditText) findViewById(R.id.batch_num);
        countEd = (EditText) findViewById(R.id.count);
        submit = (Button) findViewById(R.id.submit);
        costPriceCard = (CardView) findViewById(R.id.costCard);
        sellingPriceCard = (CardView) findViewById(R.id.sellingCard);
        shared = getSharedPreferences(keys.SharedPrefrenceName, 0);
        api = new Api(this);
        ExpArr = new ArrayList<>();
        batchArr = new ArrayList<>();
        stores = new ArrayList<>();
        store_cost = new HashMap<>();
        store_Avl_q = new HashMap<>();
        stores_inf_list = new ArrayList<>();
        getAuthorities();
        final Bundle b = getIntent().getExtras();
         item = (itemObject) b.getSerializable(keys.ITEM);
        inv = (inventoryObject) b.getSerializable(keys.inv_table_name);
        inv_id = inv.getInv_id();
        store_num = b.getInt(keys.inv_store_num_column);
        getStoresList(storeNumEd);
        itemCount = item.getInventoryItemCount();
        if (b.containsKey(keys.Edit)) {
            Edit = true;
        }
       showData(item);

   //getitemData(item);
        //   showData(item);
        addCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCount = itemCount + 1;
                countEd.setText(String.valueOf(itemCount));
            }
        });
        subCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCount = itemCount - 1;
                countEd.setText(String.valueOf(itemCount));

            }
        });
        expireDate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showExpBatch(ExpArr, expireDate);

                return true;
            }
        });
        batchNumEd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showExpBatch(batchArr, batchNumEd);
                return true;

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String batch_num_txt = batchNumEd.getText().toString();
                String expireDate_txt = expireDate.getText().toString();
                //Log.d("tm", expireDate_txt) ;
                if (batch_num == 1 && batch_num_txt.isEmpty()) {
                    batchNumEd.setError(getString(R.string.enter) + getString(R.string.batch));
                    //
                } else if (expire_date == 1 && expireDate_txt.isEmpty()) {
                    //
                    expireDate.setError(getString(R.string.enter) + getString(R.string.expire_date));

                } else if (itemCount == 0) {
                    countEd.setError(getString(R.string.enter) + getString(R.string.count));

                } else {

                    if (inv.getInv_done() == 1) {
                        if (b.containsKey("edit_api")) {
                            saveEditToServer(item, "Edit_MACH_INV_OneItem");//   edit  item in server


                        } else {
                            int item_record = b.getInt("generate_id") + 1;
                            Log.d("iiiii", String.valueOf(item_record));
                            item.setRecord_id(item_record);
                            saveEditToServer(item, "AddNew_MACH_INV_OneItem");//   add new   item  to inv in server

                        }
                    } else if (Edit) {
                        editItem(item);                   // edit  item  in sqlite

                    } else {
                        addItem(item);                   // add to item table in sqlite

                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_details_menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.show_more ) {
            getitemData(item);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void showMore(itemObject item){
         costPriceTv.setText(getStoreCostPrice(store_cost, store_num));
         avl_quantity.setText(getStoreCostPrice(store_Avl_q, store_num));

         //  sellingPriceTv.setText(String.valueOf(item.getSellingPrice()));
         checkNull(sellingPriceTv, String.valueOf(item.getSellingPrice()));
         if (!item.getItemDesc().equals("null")) {
             descTv.setText(item.getItemDesc());
         } else {
             descTv.setVisibility(View.GONE);
         }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
     }

    public void showData(itemObject item) {
        nameTv.setText(item.getItemName());
        unitTv.setText(item.getUnit());
        idTv.setText(item.getItemId());
        //   countEd.selectAll();
        // costPriceTv.setText(String.valueOf(item.getCostPrice()));
        // checkNull(costPriceTv ,String.valueOf(item.getCostPrice()));
        costPriceTv.setText(getStoreCostPrice(store_cost, store_num));
        avl_quantity.setText(getStoreCostPrice(store_Avl_q, store_num));

        //  sellingPriceTv.setText(String.valueOf(item.getSellingPrice()));
        checkNull(sellingPriceTv, String.valueOf(item.getSellingPrice()));
        if (!item.getItemDesc().equals("null")) {
            descTv.setText(item.getItemDesc());
        } else {
            descTv.setVisibility(View.GONE);
        }
        // storeNumEd.setText(String.valueOf(store_num));
        countEd.setText(String.valueOf(itemCount));
        if (itemCount == 0) {
            countEd.requestFocus();
            countEd.clearFocus();
            countEd.setSelectAllOnFocus(true);
        }
       /*  countEd.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(countEd.isFocused()){
                     countEd.requestFocus();
                     countEd.clearFocus();
                     countEd.setSelection(countEd.getText().length(), 0);
                 }else{
                     countEd.requestFocus();
                     countEd.clearFocus();
                 }

             }
         });*/
        countEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!countEd.getText().toString().isEmpty()) {
                    itemCount = Integer.parseInt(countEd.getText().toString());

                }
            }
        });
        if (item.getExpire_date() != null) {
            expireDate.setText(item.getExpire_date());
        }
        if (item.getBatch_num() != null) {
            batchNumEd.setText(item.getBatch_num());
        }
     /* if (dialog.isShowing()) {
            dialog.dismiss();
        }*/
        //  expireDate user hyd5lo
        //  batch_num  user hyd5lo

    }

    public void getAuthorities() {
        int cost_price = shared.getInt(keys.COST_PRICE, 0);
        controlView(cost_price, costPriceCard);
        int selling_price = shared.getInt(keys.SELLING_PRICE, 1);
        controlView(selling_price, sellingPriceCard);

        expire_date = shared.getInt(keys.EXPIRE_DATE, 0);
        controlView(expire_date, expireDate);
        controlView(expire_date, expireDateTv);

        batch_num = shared.getInt(keys.BACH_NUM, 0);
        controlView(batch_num, batchNumEd);
        controlView(batch_num, batchNumTv);

        int description = shared.getInt(keys.DESCRIPTION, 0);
        controlView(description, descTv);

    }

    public void controlView(int check, View tv) {
        if (check == 0) {
            tv.setVisibility(View.GONE);
        }
    }

    public long addItem(itemObject item) {
        dataBase db = new dataBase(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(keys.item_id, item.getItemId());
        value.put(keys.item_name, item.getItemName());
        value.put(keys.item_barcode, item.getBarCode());
        value.put(keys.item_unit, item.getUnit());
        value.put(keys.item_store_num, String.valueOf(store_num));
        if (batch_num == 1) {
            value.put(keys.BACH_NUM, batchNumEd.getText().toString());
        }
        if (expire_date == 1) {
            // String d=getDate(expireDate.getText().toString());
            Log.d("dateeee", expireDate.getText().toString());

            value.put(keys.EXPIRE_DATE, expireDate.getText().toString());
        }
        value.put(keys.COST_PRICE, costPriceTv.getText().toString());
        value.put(keys.SELLING_PRICE, item.getSellingPrice());
        value.put(keys.item_size, item.getItemSize());
        value.put(keys.item_desc, item.getItemDesc());
        value.put(keys.item_count, itemCount);
        value.put(keys.item_store_count, avl_quantity.getText().toString());
        value.put(keys.item_inv_id, inv_id);
        long item_record_id = sql.insert(keys.item_table_name, null, value);
        if (item_record_id < 0) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            return item_record_id;
        } else {
            Toast.makeText(this, getString(R.string.Done), Toast.LENGTH_SHORT).show();
            finish();
            return item_record_id;
        }


    }

    public void saveEditToServer(itemObject item, String function_name) {
        ArrayList<itemObject> itemsArr = new ArrayList<>();
        item.setStoreCode(String.valueOf(store_num));
        item.setInventoryItemCount(itemCount);
        if (expire_date == 1) {
            item.setExpire_date(expireDate.getText().toString());
        }
        if (batch_num == 1) {
            item.setBatch_num(batchNumEd.getText().toString());
        }
        // batch num
        // birth day
        itemsArr.add(item);
        String phone_name = Login.getMacAddr();
        String post_data = api.createPointString(itemsArr, inv, phone_name, function_name, String.valueOf(inv_id));
        //  api.testVolley(post_data);
        Log.d("post_data", post_data);
        Api.SendInventoryPoint send = new Api.SendInventoryPoint(new RequestInterface() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Success")) {
                    Toast.makeText(itemDetails.this, getString(R.string.Done), Toast.LENGTH_SHORT).show();
                    finish();
                }
                Log.d("saveEditToServer", response);

            }

            @Override
            public void onError() {

            }
        });
        send.execute(post_data);


    }

    public void editItem(itemObject item) {
        dataBase db = new dataBase(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(keys.item_store_num, String.valueOf(store_num));
        value.put(keys.item_count, itemCount);
        if (batch_num == 1) {
            value.put(keys.BACH_NUM, batchNumEd.getText().toString());
        }
        if (expire_date == 1) {
            // String d=getDate(expireDate.getText().toString());
            Log.d("dateeee", expireDate.getText().toString());

            value.put(keys.EXPIRE_DATE, expireDate.getText().toString());
        }
        long update = sql.update(keys.item_table_name, value, keys.item_record_id + "=?", new String[]{String.valueOf(item.getRecord_id())});
        if (update > 0) {
            Toast.makeText(this, getString(R.string.Done), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void getStoresList(final Spinner sp) {
        final SharedPreferences sharedPreferences  =  getSharedPreferences("store" , 0) ;
        if(sharedPreferences.contains("ids")) {
            Log.d("from" , "shared") ;
        Gson gson = new Gson();
        String ids = sharedPreferences.getString("ids", "");
        String names = sharedPreferences.getString("names", "");
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        Type type2 = new TypeToken<ArrayList<String>>() {
        }.getType();
       store_ids= gson.fromJson(ids, type);
        ArrayList<String> store_names= gson.fromJson(names, type2);

       // final ArrayList<Integer> store_ids = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item , store_names);
        sp.setAdapter(adapter);
       int select_position =  store_ids.indexOf(store_num) ;
        Log.d("id", String.valueOf(select_position));
        sp.setSelection(select_position, true); }
        else {
             store_ids = new ArrayList<>();
             final ArrayList<String> store_names= new ArrayList<>();
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item );

            Api api = new Api(this);

        api.getStores(new RequestInterface() {
            @Override
            public void onResponse(String response) {
                Log.d("from" , "request") ;
                Log.d("response", response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray storesArray = object.getJSONArray("JS_WH_DTL");
                    int select_position = 0;

                    for (int i = 0; i < storesArray.length(); i++) {
                        JSONObject storeData = storesArray.getJSONObject(i);
                        String strore_name = storeData.getString("W_NAME");
                        int store_id = storeData.getInt("WH_CODE");
                        Log.d("storeId", String.valueOf(store_id));
                        Log.d("store_num", String.valueOf(store_num));

                        if (store_id == store_num) {
                            select_position = i;
                        }
                        adapter.add(strore_name + ":" + store_id);
                        store_names.add(strore_name + ":"+store_id);

                        store_ids.add(store_id);
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit() ;
                    Gson gson = new Gson();
                    String ids_gson = gson.toJson(store_ids);
                    String names_gson = gson.toJson(store_names);
                    editor.putString("ids",ids_gson );
                    editor.putString("names",names_gson );
                    editor.commit();
                    sp.setAdapter(adapter);
                    Log.d("id", String.valueOf(select_position));
                    sp.setSelection(select_position, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError() {

            }
        });}
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                store_num = store_ids.get(position);
                costPriceTv.setText(getStoreCostPrice(store_cost, store_num));
                avl_quantity.setText(getStoreCostPrice(store_Avl_q, store_num));
                Log.d("select_num", String.valueOf(store_num));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void checkNull(TextView tv, String text) {
        if (text.equals("null")) {
            tv.setText("");
        } else {
            tv.setText(text);
        }
    }

    public void getitemData(final itemObject item) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();
        api.getItemByUnit(item.getItemId(), item.getUnit(), new RequestInterface() {
            @Override
            public void onResponse(String response) {
                Log.d("responseddddd", response);
                try {
                    JSONObject ob = new JSONObject(response);
                    JSONArray array = ob.getJSONArray("JS_WH_DTL");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject mItem = array.getJSONObject(i);
                        String item_price = mItem.getString("ITM_PRICE");//selling price
                        String storeItemCount = mItem.getString("AVL_QTY"); //AVL_QTY
                        String storeCode = mItem.getString("WH_CODE"); //WH_CODE //رقم المخزن
                        String costPrice = mItem.getString("STK_NEW"); //STK_NEW
                           Log.d(" store_code" , storeCode ) ;
                        if (!Edit) {
                            item.setStoreItemCount(storeItemCount);
                            item.setSellingPrice(item_price);
                        }
                        if (!storeCode.equals("null")) {
                            if(stores.contains(Integer.parseInt(storeCode))) {
                                Log.d(" store_code in if " , storeCode ) ;

                                int index  =  stores.indexOf(Integer.parseInt(storeCode)) ;
                                 storeObject st =     stores_inf_list.get(index) ;
                                  double sum  = Double.parseDouble( st.AVL_QTY) + Double.parseDouble( storeItemCount) ;
                                 st.AVL_QTY = String.valueOf(sum) ;
                                Log.d(" store_code in if " , st.AVL_QTY ) ;

                                store_Avl_q.put(Integer.parseInt(storeCode),st.AVL_QTY);

                            }
                            else {
                                stores.add(Integer.parseInt(storeCode));
                                stores_inf_list.add(new storeObject(Integer.parseInt(storeCode), storeItemCount, costPrice));
                                store_cost.put(Integer.parseInt(storeCode), costPrice);
                                store_Avl_q.put(Integer.parseInt(storeCode), storeItemCount);

                            }
                              }

                        if (expire_date == 1) {
                            String exp_date = mItem.getString("EXP_DATE"); //WH_CODE
                            if (!exp_date.equals("null")) {
                                // int ind= exp_date.indexOf("T");
                                //   exp_date=exp_date.substring(0,ind) ;
                                //exp_date=new StringBuilder(exp_date).reverse().toString();
                                //String dd=    getDate(exp_date) ;
                                //  Log.d("dateeee" , dd) ;
                                ExpArr.add(exp_date);
                            }

                        }
                        if (batch_num == 1) {
                            String batch_num = mItem.getString("BATCH_ID"); //WH_CODE
                            if (!batch_num.equals("null")) {
                                batchArr.add(batch_num);
                            }

                        }
                    }
                    boolean f = checkStore(stores, store_num);
                    if (!f) {
                        item_store_note.setVisibility(View.VISIBLE);
                        item_store_note.setText(getString(R.string.notFoundInStore) + stores.toString());
                    }
                    showMore(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError() {

            }
        });


    }

    public void showExpBatch(final ArrayList<String> data, final EditText ed) {
        if (data.size() <= 0) {
            com.max.rm.inventory.Dialog.window(getString(R.string.not_found), this);
        } else {
            final Dialog d = new Dialog(this);
            d.setContentView(R.layout.show_list_window);
            ListView list = (ListView) d.findViewById(R.id.list);
            Button cancel = (Button) d.findViewById(R.id.cancel);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.brand_row, R.id.text, data);
            list.setAdapter(adapter);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ed.setText(data.get(position));
                    d.dismiss();
                }
            });
            d.show();
        }
    }

    public boolean checkStore(ArrayList<Integer> stores, int inv_store) {
        for (int i = 0; i < stores.size(); i++) {
            if (stores.get(i) == inv_store) {
                return true;
            }

        }
        return false;
    }

    public String getStoreCostPrice(HashMap<Integer, String> storeData, int Store_code) {
        if (store_cost.containsKey(Store_code)) {
            String cost = storeData.get(Store_code);
            return cost;
        } else return "0";
    }

}
