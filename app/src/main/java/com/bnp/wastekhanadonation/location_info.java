package com.bnp.wastekhanadonation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class location_info extends AppCompatActivity {

    String fname,phone,url,desc,type,time;
    ImageView tpic;
    TextView tfname,tphone,tdesc,ttype,ttime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);
        Intent intent = getIntent();
        fname = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        url = intent.getStringExtra("url");
        desc = intent.getStringExtra("desc");
        type = intent.getStringExtra("type");
        //time = intent.getStringExtra("time");

        tfname = findViewById(R.id.fname);
        tphone = findViewById(R.id.fphone);
        tpic = findViewById(R.id.fpic);
        tdesc = findViewById(R.id.fdesc);
        //ttime = findViewById(R.id.ftime);
        ttype = findViewById(R.id.ftype);

        tfname.setText("Food:"+fname);
        tphone.setText("Phone:"+phone);
        tdesc.setText("Description:"+desc);
        ttype.setText("Type:"+type);
       // ttime.setText(time);

        if(url == null){
            tpic.setVisibility(View.GONE);
        }else {
            tpic.setVisibility(View.VISIBLE);
            Glide.with(location_info.this).load(url).into(tpic);
        }

    }
}