package com.cw.showcaseview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.cw.showcase.showcaseview.ShowcaseView;


/**
 * @author Cw
 * @date 2017/7/26
 */
public class SecondActivity extends AppCompatActivity {

    private View mTextView;
    private View mTextView2;
    private View mTextView3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mTextView = findViewById(R.id.tv_text);
        mTextView2 = findViewById(R.id.tv_text2);
        mTextView3 = findViewById(R.id.tv_text3);
        show();
    }

    public void show() {
        new ShowcaseView.Builder(this)
                .setMaskColor("#88EECC33")
                .setDismissOnTouch(true)
                .setDuration(1000L, 1000L)
                .setTargetPadding(20)
                .addTarget(mTextView, ShowcaseView.CIRCLE_SHAPE)
                .addShowcaseListener(new ShowcaseView.ShowcaseListener() {
                    @Override
                    public void onDisplay(ShowcaseView showcaseView) {
                        Toast.makeText(getApplication(), "第一个展示啦", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismiss(ShowcaseView showcaseView) {
                        Toast.makeText(getApplication(), "第一个消失啦", Toast.LENGTH_SHORT).show();
                    }
                })
                .addShowcaseQueue()
                .setMaskColor("#66FFB6C1")
                .addTarget(mTextView2, ShowcaseView.OVAL_SHAPE)
                .addShowcaseListener(new ShowcaseView.ShowcaseListener() {
                    @Override
                    public void onDisplay(ShowcaseView showcaseView) {
                        Toast.makeText(getApplication(), "第二个展示啦", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismiss(ShowcaseView showcaseView) {
                        Toast.makeText(getApplication(), "第二个消失啦", Toast.LENGTH_SHORT).show();
                    }
                })
                .addShowcaseQueue()
                .setMaskColor("#D8BFD8")
                .setDismissOnTouch(false)
                .addTarget(mTextView3, ShowcaseView.RECTANGLE_SHAPE)
                .addImage(R.mipmap.img_showcase, 5.0f, 8.0f, 1.0f, true)
                .addShowcaseListener(new ShowcaseView.ShowcaseListener() {
                    @Override
                    public void onDisplay(ShowcaseView showcaseView) {
                        Toast.makeText(getApplication(), "最后一个展示啦", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismiss(ShowcaseView showcaseView) {
                        Toast.makeText(getApplication(), "最后一个消失啦", Toast.LENGTH_SHORT).show();
                    }
                })
                .addShowcaseQueue()
                .build().showQueue();
    }
}
