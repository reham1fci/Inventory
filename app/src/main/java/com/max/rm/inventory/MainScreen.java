package com.max.rm.inventory;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class MainScreen extends AppCompatActivity {
    public static final int NUM = 5000;
    RecyclerView points_list, point_saved;
    FloatingActionButton add_point;
    ArrayList<String> inventory_points;
    ArrayList<String> inventory_points2;
    TextView user_welcome;
    SharedPreferences shared, shared2;
    Api api;
    int store_id;
    Inventory_points_adapter adapter;
    Inventory_points_adapter adapter2;
    TextView savedTitle, notSavedTitle;
    String org_id;
    int numOfLoop = 0;
    int remindItems = 0;
    ProgressDialog progressDialog ;
    dataBase db ;
    SQLiteDatabase sql ;
    ArrayList<Integer> store_ids = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        points_list = (RecyclerView) findViewById(R.id.points);
        point_saved = (RecyclerView) findViewById(R.id.points_save);
        add_point = (FloatingActionButton) findViewById(R.id.addPoint);
        user_welcome = (TextView) findViewById(R.id.user_name);
        savedTitle = (TextView) findViewById(R.id.savedTitle);
        notSavedTitle = (TextView) findViewById(R.id.notSavedTitle);
        shared = getSharedPreferences(keys.SharedPrefrenceName, 0);
        shared2 = getSharedPreferences("store", 0);
        api = new Api(MainScreen.this);
        String user_name = shared.getString(keys.USER_NAME, "");
        user_welcome.setText(getString(R.string.welcome) + " " + user_name);
        org_id = shared.getString(keys.ORG_ID, "");
        progressDialog = new ProgressDialog(MainScreen.this);
        add_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open window to insert inventory  point data
                createInventoryPoint(null);

            }
        });
         db = new dataBase(this);
         sql = db.getWritableDatabase();
       Cursor mc=   sql.query(keys.All_ITEMS_TableName , null , null , null , null , null  , null ) ;
 int  count   = mc.getCount() ;
  Log.d("counttable" , String.valueOf(count));
   if(mc.moveToFirst()){
 do{
     Log.d("name" ,  mc.getString(mc.getColumnIndex(keys._item_name)) );
 }
 while (mc.moveToNext()) ;
    }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getInventoryList();
        getInvListApi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            Intent i = new Intent(this, Setting.class);
            startActivity(i);
        }
        if (id == R.id.logout) {
            shared.edit().clear().commit();
            shared2.edit().clear().commit();
            Intent i = new Intent(this, Login.class);
            startActivity(i);
            finish();
        }
        if (id == R.id.getItems) {

            api.getItemsCount(org_id, new RequestInterface() {
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);
                    dataBase db = new dataBase(MainScreen.this);
                    SQLiteDatabase sql = db.getWritableDatabase();
                    sql.delete(keys.All_ITEMS_TableName , null , null ) ;
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("JS_WH_DTL");
                        JSONObject CountObj = array.getJSONObject(0);
                        int numOfItems = (int) CountObj.getDouble("COUNT(*)");
                        itemsCount = numOfItems ;
                       if (numOfItems > NUM) {
                            getNumOfLoop(numOfItems);
                        } else {
                            getAllItems(org_id, String.valueOf(0), String.valueOf(numOfItems));
                            progressDialog.setTitle("download all items ");
                            progressDialog.setMessage("Downloading in Progress...");
                            progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
                            progressDialog.setCancelable(false);

                            progressDialog.show();
                            //   getItems direct
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError() {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    // if  inv not equal null mean edit data else if inv null create new inv
    public void createInventoryPoint(final inventoryObject inv) {

        final Dialog d = new Dialog(MainScreen.this);
        d.setContentView(R.layout.create_inv_point);
        final Spinner storesSp = (Spinner) d.findViewById(R.id.storesSp);
        final EditText desc = (EditText) d.findViewById(R.id.desc);
        final EditText ref_num = (EditText) d.findViewById(R.id.ref_no);

        Button add = (Button) d.findViewById(R.id.add);
        getStoresList(storesSp, inv);
        if (inv != null) {
            if (inv.getInv_desc() != null) {
                desc.setText(inv.getInv_desc());
            }
            if (inv.getInv_ref() != null) {
                desc.setText(inv.getInv_ref());

            }

        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descTxt = desc.getText().toString();
                String refNumTxt = ref_num.getText().toString();
                if (inv != null) {
                    insertInventoryRow(descTxt, store_id, refNumTxt, d, inv);// edit inv point
                } else {
                    insertInventoryRow(descTxt, store_id, refNumTxt, d, null);
                } // save new inv point
            }
        });
        d.show();

    }


//        keys.log("countColumn" , String.valueOf(c.getInt(c.getColumnIndex(keys.inv_store_num_column))));

    public void insertInventoryRow(String description, int Store_num, String ref_num, Dialog d, inventoryObject invOb) {
        dataBase db = new dataBase(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH);
        String currentDate = sdf.format(new Date());
        ContentValues value = new ContentValues();
        if (!description.isEmpty()) {
            value.put(keys.inv_desc_column, description);
        }
        if (!ref_num.isEmpty()) {
            value.put(keys.inv_ref_num_column, ref_num);
        }
        value.put(keys.inv_date_column, currentDate);
        value.put(keys.inv_store_num_column, Store_num);
        value.put(keys.done_column, 0);
        if (invOb != null) {
            long edit_result = sql.update(keys.inv_table_name, value, keys.inv_id_column + "=?", new String[]{String.valueOf(invOb.getInv_id())});
            if (edit_result > 0) {
                d.dismiss();
                onStart();
            }

        } else {
            long insert_result = sql.insert(keys.inv_table_name, null, value);
            inventoryObject inv = new inventoryObject((int) insert_result, description, currentDate, ref_num, 0, Store_num);
            if (insert_result >= 0) {
                Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainScreen.this, InventoryPoint.class);
                i.putExtra(keys.inv_table_name, inv);
                startActivity(i);
                d.dismiss();
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                d.dismiss();
            }
        }


    }

    public void getInventoryList() { // get inv list from sqlite
        final ArrayList<inventoryObject> inventoryArr = new ArrayList<>();
        dataBase db = new dataBase(this);
        SQLiteDatabase sql = db.getReadableDatabase();
        Cursor c = sql.query(keys.inv_table_name, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            notSavedTitle.setVisibility(View.VISIBLE);
            points_list.setVisibility(View.VISIBLE);
            do {
                int inv_id = c.getInt(c.getColumnIndex(keys.inv_id_column));
                String inv_desc = c.getString(c.getColumnIndex(keys.inv_desc_column));
                String inv_date = c.getString(c.getColumnIndex(keys.inv_date_column));
                String inv_ref = c.getString(c.getColumnIndex(keys.inv_ref_num_column));
                int inv_done = c.getInt(c.getColumnIndex(keys.done_column));
                int inv_store = c.getInt(c.getColumnIndex(keys.inv_store_num_column));
                inventoryObject inv = new inventoryObject(inv_id, inv_desc, inv_date, inv_ref, inv_done, inv_store);
                inventoryArr.add(inv);
            } while (c.moveToNext());

            adapter = new Inventory_points_adapter(this, inventoryArr, new rec_interface() {
                @Override
                public void onRecItemSelected(int position, View view) {
                    Intent toInventory = new Intent(MainScreen.this, InventoryPoint.class);
                    toInventory.putExtra(keys.inv_table_name, inventoryArr.get(position));
                    startActivity(toInventory);
                }
            }, new rec_interface.longClickInf() {
                @Override
                public void onRecItemLongClick(final int position, View view) {
                    PopupMenu delete = new PopupMenu(MainScreen.this, view);

                    delete.getMenuInflater().inflate(R.menu.delete_edit, delete.getMenu());
                    delete.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // delete_window
                            int id = item.getItemId();
                            if (id == R.id.edit) {
                                createInventoryPoint(inventoryArr.get(position)); // edit
                                return true;
                            } else if (id == R.id.delete) {
                                deleteInvSqliteWindow(inventoryArr, inventoryArr.get(position).getInv_id(), position);
                            }
                            return true;
                        }
                    });
                    delete.show();
                }
            });
            GridLayoutManager lLayout = new GridLayoutManager(this, 1);
            points_list.setLayoutManager(lLayout);
            points_list.setHasFixedSize(true);
            points_list.setAdapter(adapter);

        } else {
            notSavedTitle.setVisibility(View.GONE);
            points_list.setVisibility(View.GONE);
        }
    }

    public void getInvListApi() {
        final ArrayList<inventoryObject> inventoryArr = new ArrayList<>();

        api.getInvList(new RequestInterface() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray invArr = obj.getJSONArray("JS_MACH_INV_MST");
                    if (invArr.length() <= 0) {
                        savedTitle.setVisibility(View.GONE);
                        point_saved.setVisibility(View.GONE);

                    } else {
                        savedTitle.setVisibility(View.VISIBLE);
                        point_saved.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < invArr.length(); i++) {
                        JSONObject invObj = invArr.getJSONObject(i);
                        String inv_desc = invObj.getString("INV_DESC");
                        String inv_date = invObj.getString("INV_DATE");
                        int ind = inv_date.indexOf("T");
                        inv_date = inv_date.substring(0, ind);
                        // inv_date=new StringBuilder(inv_date).reverse().toString();
                        int inv_id = invObj.getInt("INV_NO");
                        String inv_ref = invObj.getString("REF_NO");
                        inventoryObject inv = new inventoryObject(inv_id, inv_desc, inv_date, inv_ref, 1);
                        inventoryArr.add(inv);
                    }
                    adapter2 = new Inventory_points_adapter(MainScreen.this, inventoryArr, new rec_interface() {
                        @Override
                        public void onRecItemSelected(int position, View view) {
                            Intent toInventory = new Intent(MainScreen.this, InventoryPoint.class);
                            toInventory.putExtra(keys.inv_table_name, inventoryArr.get(position));
                            startActivity(toInventory);
                        }
                    }, new rec_interface.longClickInf() {
                        @Override
                        public void onRecItemLongClick(final int position, View view) {
                            final PopupMenu delete = new PopupMenu(MainScreen.this, view);
                            delete.getMenuInflater().inflate(R.menu.delte_menu, delete.getMenu());
                            delete.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    // delete_window
                                    deleteInvApiWindow(inventoryArr, inventoryArr.get(position).getInv_id(), position);
                                    return false;
                                }
                            });
                            delete.show();
                        }
                    });
                    GridLayoutManager lLayout = new GridLayoutManager(MainScreen.this, 1);
                    point_saved.setLayoutManager(lLayout);
                    point_saved.setHasFixedSize(true);
                    point_saved.setAdapter(adapter2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError() {

            }
        }, Login.getMacAddr());
    }

    public void getStoresList(final Spinner sp, final inventoryObject inv) {
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
            if (inv != null) {
                int ind = inv.getInv_store() - 1;
                Log.d("ind", String.valueOf(inv.getInv_store()));
                sp.setSelection(ind, true);

            }
          //  int select_position =  store_ids.indexOf(store_num) ;
         //   Log.d("id", String.valueOf(select_position));
          //  sp.setSelection(select_position, true);
        }
         else{
      store_ids = new ArrayList<>();
        final ArrayList<String> store_names = new ArrayList<>();
        final SharedPreferences.Editor editor = getSharedPreferences("store", 0).edit();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        api.getStores(new RequestInterface() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray storesArray = object.getJSONArray("JS_WH_DTL");
                    for (int i = 0; i < storesArray.length(); i++) {
                        JSONObject storeData = storesArray.getJSONObject(i);
                        String strore_name = storeData.getString("W_NAME");
                        int store_id = storeData.getInt("WH_CODE");
                        adapter.add(strore_name + ":" + store_id);
                        store_names.add(strore_name + ":" + store_id);
                        store_ids.add(store_id);

                    }
                    Gson gson = new Gson();
                    String ids_gson = gson.toJson(store_ids);
                    String names_gson = gson.toJson(store_names);
                    editor.putString("ids", ids_gson);
                    editor.putString("names", names_gson);
                    editor.commit();
                    sp.setAdapter(adapter);
                    if (inv != null) {
                        int ind = inv.getInv_store() - 1;
                        Log.d("ind", String.valueOf(inv.getInv_store()));
                        sp.setSelection(ind, true);

                    }
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
                store_id = store_ids.get(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void deleteInvSqliteWindow(final ArrayList<inventoryObject> list, final int inv_id, final int position) {
        com.max.rm.inventory.Dialog.dialogOkCancel(getString(R.
                string.confirm_delete), new dialog_interface() {
            @Override
            public void onDialogOkClick(AlertDialog alertDialog) {
                deleteInvFromSqlite(list, inv_id, position);
            }

            @Override
            public void onDialogCancelClick(AlertDialog alertDialog) {

            }
        }, MainScreen.this);
    }

    public void deleteInvApiWindow(final ArrayList<inventoryObject> list, final int inv_id, final int position) {
        com.max.rm.inventory.Dialog.dialogOkCancel(getString(R.
                string.confirm_delete), new dialog_interface() {
            @Override
            public void onDialogOkClick(AlertDialog alertDialog) {
                deleteInvFromApi(list, inv_id, position);
            }

            @Override
            public void onDialogCancelClick(AlertDialog alertDialog) {

            }
        }, MainScreen.this);
    }

    public void deleteInvFromSqlite(ArrayList<inventoryObject> list, int inv_id, int position) {
        // delte inv w kman mn item table
        dataBase db = new dataBase(this);
        SQLiteDatabase sql = db.getWritableDatabase();
        long delete = sql.delete(keys.inv_table_name, keys.inv_id_column + "=?", new String[]{String.valueOf(inv_id)});
        if (delete > 0) {
            long deleteitems = sql.delete(keys.item_table_name, keys.item_inv_id + "=?", new String[]{String.valueOf(inv_id)});
            if (delete > 0 || deleteitems > 0) {
                list.remove(position);
                adapter.notifyDataSetChanged();
            }

        }

    }

    public void deleteInvFromApi(final ArrayList<inventoryObject> list, int inv_id, final int position) {
        String post_data = api.delete_inv_body(String.valueOf(inv_id));
        //  api.testVolley(post_data);
        Log.d("post_data", post_data);
        Api.SendInventoryPoint send = new Api.SendInventoryPoint(new RequestInterface() {
            @Override
            public void onResponse(String response) {
                list.remove(position);
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onError() {

            }
        });
        send.execute(post_data);

    }
 int itemsCount = 0 ;
    public void getNumOfLoop(int numOfItems) {
        double div = numOfItems / NUM;
        numOfLoop = (int) div;
        remindItems = numOfItems - (numOfLoop * NUM);
        Log.d("numOfloops", String.valueOf(numOfLoop));
        Log.d("remind", String.valueOf(remindItems));
        //int skip = 0;
        //int take = NUM;
        progressDialog.setTitle("download all items ");
        progressDialog.setMessage("Downloading in Progress...");
        progressDialog.setIndeterminate(false);
       // progressDialog.setMax(100);
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        progressDialog.show();
         for( int i  =  0  ;   i  <  numOfLoop  ;  i++) {
              int skip  =  i * NUM  ;
              int take  =   NUM ;
             getAllItems(org_id, String.valueOf(skip), String.valueOf(take));

         } if (remindItems>0) {
            int skip = numOfLoop * NUM;
            int take = remindItems;
            getAllItems(org_id, String.valueOf(skip), String.valueOf(take));

        }




    }
    int i = 1;
    public void getAllItems(final String org_id, final String skip, final String take) {

        Api.downloadItems send = new Api.downloadItems(new requestDownload() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject ob = new JSONObject(response) ;
                    JSONArray arr  = ob.getJSONArray("JS_WH_DTL") ;
                     for(int i  =  0  ; i< arr.length()  ;  i++){
                          JSONObject item  = arr.getJSONObject(i) ;
                          InventoryPoint in = new InventoryPoint()  ;
                          itemObject itemObject =   in.createItemObject(item) ;
                          itemObject.storeCode = item.getString("WH_CODE");
                         // save to sql lite table
                       addItem(itemObject) ;

                     }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

              //  keys.longInfo(response);
               /*  int c  = Integer.parseInt(skip)+Integer.parseInt(take) ;

                progressDialog.setMessage("downloaded " + c);
                if (i > numOfLoop) {
                    progressDialog.dismiss();

                   /// com.max.rm.inventory.Dialog.window("downloaded " + itemsCount  , MainScreen.this);

                    //return;
                    //  progressDialog.setTitle();

                }
              else  if (i < numOfLoop) {
                    int skip = i * NUM;
                    int take = NUM;
                    getAllItems(org_id, String.valueOf(skip), String.valueOf(take));
                   i= i+1;
                }

               else if (i == numOfLoop && remindItems > 0) {
                    int skip = numOfLoop * NUM;
                    int take = remindItems;
                    getAllItems(org_id, String.valueOf(skip), String.valueOf(take));
                     i= i+1 ;
                }*/


            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onProgress( Integer [] values) {
                progressDialog.setProgress(values[0]);

            }
        });
        String body = "{\n" +
                "\"FuncationType\":\"GetItemList\",\n" +
                "\"Org_id\":\"" + org_id + "\",\n" +
                "\"skip\":\"" + skip + "\",\n" +
                "\"take\":\"" + take + "\",\n"
                +
                "}";
         Log.d("body" , body) ;
      //send.execute(body , String.valueOf(100));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            send.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, body , String.valueOf(100));
        else
            send.execute(body , String.valueOf(100));
           }
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    void startMyTask(AsyncTask asyncTask) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }*/
    public long addItem(itemObject item) {

        ContentValues value = new ContentValues();
        value.put(keys._item_id, item.getItemId());
        value.put(keys._item_name, item.getItemName());
        value.put(keys._item_barcode, item.getBarCode());
        value.put(keys._item_unit, item.getUnit());
        value.put(keys._item_store_num,item.storeCode);
        value.put(keys._item_size, item.getItemSize());
        value.put(keys._BACH_NUM, item.batch_num);
        value.put(keys._EXPIRE_DATE, item.getExpire_date());



        long item_record_id = sql.insert(keys.All_ITEMS_TableName, null, value);
        if (item_record_id < 0) {
            //Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            return item_record_id;
        } else {
          //  Toast.makeText(this, getString(R.string.Done), Toast.LENGTH_SHORT).show();
           // finish();
            return item_record_id;
        }


    }


}
