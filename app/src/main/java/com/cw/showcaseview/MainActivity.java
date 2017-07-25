package com.cw.showcaseview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.cw.showcaseview.showcaseview.ShowcaseView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View textView = findViewById(R.id.tv_text);
        View textView2 = findViewById(R.id.tv_text2);


        new ShowcaseView.Builder(this)
                .setMaskColor("#88eecc33")
                .addTarget(textView, ShowcaseView.RECTANGLE_SHAPE)
                .addTarget(textView2, ShowcaseView.OVAL_SHAPE)
                .setDismissOnTouch(false)
                .setDuration(3000L, 3000L)
                .setTargetPadding(20)
                .addImage(R.mipmap.ic_launcher, 5.0f, 8.0f, 2.0f, true)
                .setListener(new ShowcaseView.ShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(ShowcaseView showcaseView) {
                        Toast.makeText(getApplication(), "onShowcaseDisplayed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onShowcaseDismissed(ShowcaseView showcaseView) {
                        Toast.makeText(getApplication(), "onShowcaseDismissed", Toast.LENGTH_SHORT).show();
                    }
                })
                .build().show();
    }
}
