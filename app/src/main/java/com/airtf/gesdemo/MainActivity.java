package com.airtf.gesdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airtf.geslib.GesContract;
import com.airtf.geslib.GesProxy;

public class MainActivity extends Activity implements View.OnClickListener {

    Button btnNew,btnShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNew = findViewById(R.id.btnNew);
        btnShow = findViewById(R.id.btnShow);

        btnNew.setOnClickListener(this);
        btnShow.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnShow){
            GesProxy.getInstance().showPassword(this, new GesContract.Callback() {
                @Override
                public void onCallback(boolean bResult, String msg) {
                    Log.e("---", "-----" + msg);
                }
            });
        }else if(v.getId() == R.id.btnNew){
            GesProxy.getInstance().resetPassword(this, new GesContract.Callback() {
                @Override
                public void onCallback(boolean bResult, String msg) {
                    Log.e("---", "-----" + msg);
                }
            });
        }
    }
}
