package com.max.rm.inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class BarCode extends AppCompatActivity {
     ZXingScannerView scannerView;
    final int RequestCameraPermissionID = 1001;
    Api api ;
    int inv_id  ,inv_store_num;
    inventoryObject inv ;
     int generate_id;
     ArrayList<itemObject> itemsArr ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);
        Bundle b =  getIntent().getExtras();
        inv= (inventoryObject) b.getSerializable(keys.inv_table_name);
        itemsArr = (ArrayList<itemObject>)b.getSerializable("items_arr");
        Log.d("hhh" ,itemsArr.toString());
        inv_id = inv.getInv_id() ;
        inv_store_num = inv.getInv_store() ;
        generate_id=b.getInt("generate_id");
         api = new Api(BarCode.this) ;
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //Request permission
                ActivityCompat.requestPermissions(BarCode.this,
                        new String[]{android.Manifest.permission.CAMERA},RequestCameraPermissionID);
                return;
            }
             else {
        scannCode();

            }
    }
     public void scannCode(){
         scannerView= new ZXingScannerView(this);
         scannerView.setResultHandler( new zxinghandel());
          setContentView(scannerView);
          scannerView.startCamera();

     }

    @Override
    protected void onPause() {
        super.onPause();
        //scannerView.stopCamera();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==RequestCameraPermissionID){
if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
    scannCode();
}
        }

    }
    class zxinghandel implements ZXingScannerView.ResultHandler {

          @Override
          public void handleResult(Result result) {
            String result_txt= result.getText();
              Toast.makeText(BarCode.this, result_txt, Toast.LENGTH_SHORT).show();
              getItemByBarCode(result_txt ,String.valueOf( inv_store_num));
             /* setContentView(R.layout.activity_bar_code);
              scannerView.stopCamera();*/

          }
      }
       public void getItemByBarCode(String barCode , String storeNum){

        // progress dialog visible
           // check if item bar code found or not
            //go to details item Screen with object of item
           final ArrayList<itemObject> itemsArray= new ArrayList<>() ;

           final ProgressDialog dialog = ProgressDialog.show(BarCode.this, getString(R.string.app_name),
           getString(R.string.Search)
                            , true);

             api.getItemByBarCode(barCode,storeNum ,new RequestInterface() {
                 @Override
                 public void onResponse(String response) {
                     dialog.dismiss();
                     try {
                         JSONObject object = new JSONObject(response);
                         JSONArray items = object.getJSONArray("JS_WH_DTL");
                         if(items.length()<=0){
                             com.max.rm.inventory.Dialog.window(getString(R.string.not_found),BarCode.this);
                         }
                         else {
                         for (int i = 0; i < items.length(); i++) {
                             JSONObject item = items.getJSONObject(i);
                             itemObject itemData= createItemObject(item);
                             itemsArray.add(itemData) ;
                         }
                         keys.log("itemarr", itemsArray.toString());

                         ItemsWindow(itemsArray);}
                         // to Itemlist window}
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }






                 }

                 @Override
                 public void onError() {

                 }
             });
       }
  public itemObject  createItemObject(JSONObject item) {
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

            if(item.has("EXP_DATE")){
                expireDate = item.getString("EXP_DATE");
                itemObject.setExpire_date(expireDate);
            }
            if(item.has("BATCH_ID")){
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
    public void ItemsWindow(final ArrayList<itemObject> items){
        final android.app.Dialog d= new Dialog(this) ;
        d.setContentView(R.layout.item_search_list);
        ListView list = (ListView)d.findViewById(R.id.items_list) ;
        d.setTitle(getString(R.string.choose_item));
        itemSearchAdapater adapater =  new itemSearchAdapater(items ,BarCode.this) ;
        list.setAdapter(adapater);
        d.show();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //    search item in list
                if(!isItemFound(items.get(position) ,inv.getInv_done())){
                    Intent i = new Intent(BarCode.this , itemDetails.class) ;
                    i .putExtra(keys.ITEM ,items.get(position) ) ;
                    i .putExtra(keys.inv_table_name ,inv ) ;
                    i .putExtra(keys.inv_store_num_column ,inv_store_num ) ;
                    i .putExtra("generate_id" ,generate_id ) ;
                    startActivity(i);
                    finish();
                    d.dismiss();
                }
                else {

                    d.dismiss();

                }




            }
        });


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
                    Intent i = new Intent(BarCode.this, itemDetails.class);
                    i.putExtra(keys.ITEM, item);
                    i.putExtra(keys.inv_table_name, inv);
                    i.putExtra(keys.inv_store_num_column, Integer.parseInt(item.getStoreCode()));
                    i.putExtra("edit_api", true);
                    startActivity(i);
                    finish();
                }
                else{
                    Intent i = new Intent(BarCode.this, itemDetails.class);
                    i.putExtra(keys.ITEM, item);
                    i.putExtra(keys.inv_table_name, inv);
                    i.putExtra(keys.inv_store_num_column, Integer.parseInt(item.getStoreCode()));
                    i.putExtra(keys.Edit, true);
                    startActivity(i);
                    finish();

                }
                // go edit this item
            }

            @Override
            public void onDialogCancelClick(AlertDialog alertDialog) {

            }
        }, this);
    }
 /*   public void findItem(ArrayList<itemObject> items ,itemObject item){
         for(int i = 0 ; i <items.size() ; i++) {
             String item_unit = items.get(i).getUnit();
             String item_id = items.get(i).getItemId();
              if(item.getItemId().equals(item_id)&&)
         }

    }*/
}
