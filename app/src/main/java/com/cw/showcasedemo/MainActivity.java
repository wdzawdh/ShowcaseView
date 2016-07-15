package com.cw.showcasedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cw.showcasedemo.showcaseview.ShowcaseView;

public class MainActivity extends AppCompatActivity {

    private static final String SHOWCASE_ID = "001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        View btn = findViewById(R.id.btn);
        View btn2 = findViewById(R.id.btn2);
        ShowcaseView.resetAll(this);
        ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                .setTarget(btn,btn2)
                .setMainImage(R.mipmap.ic_launcher,100,100,100,100)
                .setViceImage(R.mipmap.ic_launcher,100,100,0,600)
                .setActOnDissmiss(SecondActivity.class)
                .setFadeDuration(2000)
                .setDismissOnTouch(true)
                .singleUse(SHOWCASE_ID)
                .withOvalShape()
                .setShapePadding(30)
                .show();
    }
}
