package com.cw.showcaseview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cw.showcase.showcaseview.ShowcaseView;


public class MainActivity extends AppCompatActivity {

    private View mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.tv_text);
        show();

    }

    public void show() {
        new ShowcaseView.Builder(this)
                .addTarget(mTextView)
                .addImage(R.mipmap.img_showcase, 5.0f, 5.0f, 1.0f, true)
                .addShowcaseListener(new ShowcaseView.ShowcaseListener() {
                    @Override
                    public void onDisplay(ShowcaseView showcaseView) {
                    }

                    @Override
                    public void onDismiss(ShowcaseView showcaseView) {
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(intent);
                    }
                })
                .build().show();
    }
}
