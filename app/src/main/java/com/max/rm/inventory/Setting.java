package com.max.rm.inventory;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class Setting extends AppCompatActivity {
    CheckBox cost_price , selling_price  , description , batch , expire_date ;
    SharedPreferences shared ;
    SharedPreferences.Editor editor ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        cost_price = (CheckBox) findViewById(R.id.cost_price) ;
        selling_price = (CheckBox) findViewById(R.id.selling_price) ;
        description = (CheckBox) findViewById(R.id.desc) ;
        batch = (CheckBox) findViewById(R.id.batch) ;
        expire_date = (CheckBox) findViewById(R.id.expire) ;
        shared = getSharedPreferences(keys.SharedPrefrenceName , 0 );
        editor = shared.edit() ;
        startFun();
        cost_price.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if(isChecked){
                 editor.putInt(keys.COST_PRICE ,1)  ;
                  }
                  else {editor.putInt(keys.COST_PRICE ,0)  ;}
                  editor.commit();
             }
         });
          selling_price.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if(isChecked){
                      editor.putInt(keys.SELLING_PRICE ,1)  ;}
                  else {editor.putInt(keys.SELLING_PRICE ,0)  ;}
                  editor.commit();

              }
          });


    }
     public void startFun(){
         if(shared.contains(keys.COST_PRICE)){
             int cost_price_v= shared.getInt(keys.COST_PRICE,0 ) ;
             int selling_price_v= shared.getInt(keys.SELLING_PRICE,0 ) ;
             check(cost_price , cost_price_v);
             check(selling_price , selling_price_v);
         }
         int desc= shared.getInt(keys.DESCRIPTION,0 ) ;
         checkReadOnly(description , desc);
         int batchNum= shared.getInt(keys.BACH_NUM,0 ) ;
         checkReadOnly(batch , batchNum);

         int expireDate= shared.getInt(keys.EXPIRE_DATE,0 ) ;
         checkReadOnly(expire_date , expireDate);



     }
     public void check(CheckBox checkBox , int value ){
         if(value==0){
             checkBox.setChecked(false);
         }
         else {
             checkBox.setChecked(true);

         }
     }
    public void checkReadOnly(CheckBox checkBox , int value ){
        if(value==0){
            checkBox.setVisibility(View.GONE);
        }

    }
}
