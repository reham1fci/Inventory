package com.max.rm.inventory;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class StoresList extends AppCompatActivity {
    ArrayList stores_list ;
    RecyclerView storesRc ;
    Api api ;
    SharedPreferences shared ;
    String org_id , user_id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_list);
        storesRc = (RecyclerView )findViewById(R.id.storesList) ;
        stores_list = new ArrayList<>(); api= new Api(this) ;
         shared = getSharedPreferences(keys.SharedPrefrenceName , 0 ) ;
         user_id = shared.getString(keys.USER_ID, "" ) ;
         org_id = shared.getString(keys.ORG_ID, "" ) ;

    }

}
