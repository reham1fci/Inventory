package com.max.rm.inventory;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class keys {
     public static final String SharedPrefrenceName = "user" ;
     public static final String USER_ID = "user_id" ;
     public static final String USER_NAME = "user_name" ;
     public static final String USER_PASSWORD= "password" ;
     public static final String ORG_ID= "org_id" ;
     public static final String DESCRIPTION= "desc" ;
     public static final String EXPIRE_DATE= "expire_date" ;
     public static final String BACH_NUM= "batch_num" ;
     public static final String COST_PRICE= "cost_price" ;
     public static final String SELLING_PRICE= "selling_price" ;
     public static final String ITEM= "item" ;
     public static final String inv_table_name= "inventory" ;
     public static final String inv_id_column= "inv_id" ;
     public static final String inv_date_column= "inv_date" ;
     public static final String inv_store_num_column= "store_num" ;
     public static final String inv_desc_column= "inv_desc" ;
     public static final String inv_ref_num_column= "ref_num" ;
     public static final String done_column= "done" ;
     public static final String item_table_name= "items" ;
     public static final String item_record_id= "record_id" ;
     public static final String item_id= "item_id" ;
     public static final String item_store_num= "store_num" ;
     public static final String item_barcode= "barcode" ;
     public static final String item_name= "item_name" ;
     public static final String item_size= "item_size" ;
     public static final String item_unit= "item_unit" ;
     public static final String item_desc= "item_desc" ;
     public static final String item_count= "count" ;
     public static final String item_store_count= "store_count" ;
     public static final String item_inv_id= "inventory_inv_id" ;
     public static final String Edit= "edit" ;
     public static final String URl= "url" ;
     ////////////////////
     public static  final  String All_ITEMS_TableName = "all_items" ;
     public static final String _item_id= "item_id2" ;
     public static final String _item_store_num= "store_num2" ;
     public static final String _item_barcode= "barcode2" ;
     public static final String _item_name= "item_name2" ;
     public static final String _item_size= "item_size2" ;
     public static final String _item_unit= "item_unit2" ;
     public static final String _EXPIRE_DATE = "expire_date2" ;
     public static final String _BACH_NUM = "batch_num2" ;

    public static boolean isNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
     public static void log( String function_name , String Data){
         Log.d(function_name , Data) ;

     }
     public static void longInfo(String str) {
          if (str.length() > 4000) {
               Log.i("taggs", str.substring(0, 4000));
               longInfo(str.substring(4000));
          } else
               Log.i("taggs", str);
     }
}
