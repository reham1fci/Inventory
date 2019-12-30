package com.max.rm.inventory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class MainActivity extends AppCompatActivity {
SharedPreferences shared ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      /*  ScaleAnimation scal=new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, (float)0.5,Animation.RELATIVE_TO_SELF, (float)0.5);
        scal.setDuration(1000);
        scal.setFillAfter(true);
        ImageView image=(ImageView)findViewById(R.id.image);
        image.setAnimation(scal);*/
       shared= getSharedPreferences(keys.SharedPrefrenceName ,0);
        if(shared.contains(keys.USER_ID)) {
            Intent i= new Intent(MainActivity.this,Login.class)  ;
            startActivity(i);
            finish();
        }else {
            getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i= new Intent(MainActivity.this,Login.class)  ;
                startActivity(i);
                finish();
            }
        },3000);}
    }
}
