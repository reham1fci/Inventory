package com.max.rm.inventory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class Login extends AppCompatActivity implements TextWatcher, View.OnKeyListener {
    EditText userNameEd, passwordEd,orgCodeEd;
    Button loginBtn;
    ProgressBar progress;
    SharedPreferences shared;
    Api api;
    LinearLayout layoutScreen ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userNameEd=(EditText)findViewById(R.id.user_name) ;
        passwordEd=(EditText)findViewById(R.id.password) ;
        orgCodeEd=(EditText)findViewById(R.id.org_code) ;
        loginBtn=(Button) findViewById(R.id.login) ;
        progress=(ProgressBar) findViewById(R.id.progress);
        layoutScreen=(LinearLayout) findViewById(R.id.screen);
        orgCodeEd.addTextChangedListener(this);
        orgCodeEd.setOnKeyListener(this) ;
        userNameEd.setOnKeyListener(this) ;
      /*  layoutScreen.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeUrlWindow();
                return true;
            }
        });*/
    /*   orgCodeEd.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //...
                    // Perform your action on key press here
                    // ...
                //    Selection.setSelection((Editable) userNameEd.getText(),orgCodeEd.getSelectionStart());
                    userNameEd.requestFocus();
                    return true;
                }
                return false;
            }
        });
        userNameEd.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //...
                    // Perform your action on key press here
                    // ...
                    //    Selection.setSelection((Editable) userNameEd.getText(),orgCodeEd.getSelectionStart());
                    passwordEd.requestFocus();
                    return true;
                }
                return false;
            }
        });
    /*   orgCodeEd.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {
                if(orgCodeEd.getText().toString().equals("change_url")){
                    changeUrlWindow();

                }

           }
       });*/
        api= new Api(this);
        shared = getSharedPreferences(keys.SharedPrefrenceName,0);
             if(shared.contains(keys.USER_ID)){
             String user_id = shared.getString(keys.USER_ID , "") ;
             String user_name = shared.getString(keys.USER_NAME , "") ;
             String user_password = shared.getString(keys.USER_PASSWORD , "") ;
             String org_id = shared.getString(keys.ORG_ID , "") ;
              if(keys.isNetworkConnected(Login.this)){
                  progress.setVisibility(View.VISIBLE);
                 /* userNameEd.setVisibility(View.GONE);
                  passwordEd.setVisibility(View.GONE);
                  orgCodeEd.setVisibility(View.GONE);
                  loginBtn.setVisibility(View.GONE);*/
                  checkLogin(user_id , user_password , org_id);
              }
              else {
                  Dialog.window(getString(R.string.no_internet), this);
                  userNameEd.setText(user_name);
                  passwordEd.setText(user_password);
                  orgCodeEd.setText(org_id);
              }


         }
          loginBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  validation();

              }
          });
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
                .replaceAll("٠","0")
                .replaceAll(" ", "");

    }
    public void  validation (){
        if (!keys.isNetworkConnected(Login.this)){
            Dialog.window(getString(R.string.no_internet), this);
        }
        else {
             String orgId_txt = orgCodeEd.getText().toString();
             String userName_txt = userNameEd.getText().toString();
             String password_txt = passwordEd.getText().toString();
             orgId_txt =     replaceArabicNumbers(orgId_txt) ;
            userName_txt =     replaceArabicNumbers(userName_txt) ;
            password_txt =     replaceArabicNumbers(password_txt) ;

            if (orgId_txt.isEmpty()) {
                orgCodeEd.setError(getString(R.string.enter) +" "+ getString(R.string.org_code));
            } else if (userName_txt.isEmpty()) {
                userNameEd.setError(getString(R.string.enter) + " "+getString(R.string.user_name));

            } else if (password_txt.isEmpty()) {
                passwordEd.setError(getString(R.string.enter) + ""+ getString(R.string.password));
            } else {
              //  Dialog.window("code "+ orgId_txt  +  "\n username" + userName_txt + "\n password" + password_txt,Login.this);

                progress.setVisibility(View.VISIBLE);
                checkLogin(userName_txt,password_txt,orgId_txt);
    }}}



     public void checkLogin(final String user_id , final String password , final String org_id) {
     String mobileAddress =   getMacAddr();
      Log.d("mobile" , mobileAddress) ;
         api.login(org_id, user_id, password,mobileAddress ,new RequestInterface() {
             @Override
             public void onResponse(String response) {
                 keys.log("login" , response );
                // Dialog.window("login :" + response ,Login.this);
                 try {
                     JSONObject object = new JSONObject(response);
                     String loginMsg = object.getString("Msg");
                     if (loginMsg.equals("Success")) {
                         String user_name = object.getString("User_Name");
                         int description = object.getInt("VW_IC_DESCPT");
                         int expire_date = object.getInt("USE_EXPR_ITM");
                         int batch_num = object.getInt("USE_BTCH_ITM");
                         saveToShared(org_id ,user_id ,user_name,password, description , expire_date , batch_num);
                        progress.setVisibility(View.GONE);
                         // get all item function if for first time only


                        /// getAllItems(org_id , user_id , "1");
                        Intent i = new Intent(Login.this , MainScreen.class);
                         startActivity(i);
                         finish();
                     }
                      else{
                         Dialog.window(loginMsg ,Login.this);
                         progress.setVisibility(View.GONE);
                     }
                     } catch (JSONException e) {
                     e.printStackTrace();
                 }


             }

             @Override
             public void onError() {
                 Dialog.window( "cannot connect " ,Login.this);
                 progress.setVisibility(View.GONE);
                 Log.d("error" , "errorLogin") ;
             }
         });
     }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString()+android.os.Build.MODEL;
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }
     public void saveToShared( String org_id , String  user_id , String user_name , String password
             , int description , int expire_date , int batch_num){
         SharedPreferences.Editor editor = shared.edit() ;
         editor.putString(keys.ORG_ID , org_id) ;
         editor.putString(keys.USER_ID ,  user_id) ;
         editor.putString(keys.USER_NAME, user_name) ;
         editor.putString(keys.USER_PASSWORD , password) ;
         editor.putInt(keys.DESCRIPTION , description) ;
         editor.putInt(keys.EXPIRE_DATE , expire_date) ;
         editor.putInt(keys.BACH_NUM , batch_num) ;
          if(!shared.contains(keys.COST_PRICE)){
         editor.putInt(keys.COST_PRICE , 0) ;
         editor.putInt(keys.SELLING_PRICE , 1) ;
          }
         editor.commit() ;
     }
  /*  @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.:
                Toast.makeText(this, "KEYCODE_D", Toast.LENGTH_SHORT).show();
                return true;

            case KeyEvent.KEYCODE_F:
                Toast.makeText(this, "KEYCODE_D", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_J:
                Toast.makeText(this, "KEYCODE_D", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_K:
                Toast.makeText(this, "KEYCODE_D", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }*/
   public void changeUrlWindow(){
       final android.app.Dialog d=new android.app.Dialog(this) ;
       d.setContentView(R.layout.change_url);
       final EditText url =(EditText)d.findViewById(R.id.url);
       final EditText pass =(EditText)d.findViewById(R.id.password);
       Button changeBtn =(Button)d.findViewById(R.id.change);
       changeBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String url_txt=url.getText().toString();
               String pass_txt=pass.getText().toString();
               if(pass_txt.isEmpty()){
                   pass.setError("enter password");

               }
               else if (!pass_txt.equals("js$2014#acc")) {
                   pass.setError("password_error");

               }
               else if(url_txt.isEmpty()){
                 url.setError("enter new url");
               }
               else{
                   String full_url ="http://"+url_txt+"/api/";
                 shared.edit().putString(keys.URl, full_url) .commit();
                 api= new Api(Login.this);
                 Toast.makeText(Login.this, "done", Toast.LENGTH_SHORT).show();
                 d.dismiss();
               }

           }
       });
d.show();
   }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(orgCodeEd.getText().toString().equals("change_url")){
            changeUrlWindow();

        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent keyevent) {
        int id = v.getId() ;

        if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            if(id==R.id.org_code){
            //...
            // Perform your action on key press here
            // ...
            //    Selection.setSelection((Editable) userNameEd.getText(),orgCodeEd.getSelectionStart());
            userNameEd.requestFocus();
            return true;
        }
         else  if(id==R.id.user_name){
                //...
                // Perform your action on key press here
                // ...
                //    Selection.setSelection((Editable) userNameEd.getText(),orgCodeEd.getSelectionStart());
                passwordEd.requestFocus();
                return true;
            }


        }
        return false;
    }

}
