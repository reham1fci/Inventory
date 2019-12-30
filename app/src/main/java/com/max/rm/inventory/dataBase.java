package com.max.rm.inventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class dataBase extends SQLiteOpenHelper {
    public static final String  databaseName = "dataBase" ;
    public static final int  version = 4 ;




    public dataBase(Context context) {
        super(context, databaseName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         String create_inv ="CREATE TABLE inventory (\n" +
                 "  inv_id     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                 "  inv_desc  varchar(255), \n" +
                 "  inv_date  varchar(200) NOT NULL, \n" +
                 "  store_num integer(200) NOT NULL, \n" +
                 "  ref_num   varchar(200), \n" +
                 "  done      integer(10) NOT NULL);" ;
         String create_items = "CREATE TABLE items (\n" +
                 "  record_id                INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                 "  item_id                 varchar(200) NOT NULL, \n" +
                 "  barcode                 varchar(200) NOT NULL, \n" +
                 "  item_name               varchar(200) NOT NULL, \n" +
                 "  item_size               varchar(100), \n" +
                 "  item_unit               varchar(200), \n" +
                 "  item_desc               varchar(200), \n" +
                 "  store_num               varchar(100) NOT NULL, \n" +
                 "  count                   integer(200) DEFAULT 0 NOT NULL, \n" +
                 "  selling_price           varchar(200), \n" +
                 "  cost_price              varchar(255), \n" +
                 "  expire_date             varchar(200), \n" +
                 "  batch_num               varchar(200), \n" +
                 "  store_count             varchar(200), \n" +
                 "  inventory_inv_id        integer(200) NOT NULL, \n" +
                 "  FOREIGN KEY(\"inventory_inv_id\") REFERENCES \"inventory\"(inv_id));" ;
        String create_allItems    ="CREATE TABLE all_items (\n" +
                "  item_id2         varchar(200) NOT NULL , \n" +
                "  item_name2       varchar(255), \n" +
                "  barcode2         varchar(200) NOT NULL, \n" +
                "  expire_date2     varchar(200), \n" +
                "  batch_num2       varchar(200), \n" +
                "  store_num2       varchar(200), \n" +
                "  item_unit2       varchar(200) NOT NULL, \n" +
                "  item_size2       varchar(200));" ;
        db.execSQL(create_allItems );
         db.execSQL(create_inv );
         db.execSQL(create_items );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("onupgrade" , "on") ;



    }
}
