###  **这是一个Android的蒙版控件，其实以前也遇到过蒙版的需求，但是都是简单实现一下，效果看起来不好，代码可扩展性也不高。这次重新写了一版，自我感觉封装的还是不错，依旧还是希望能帮助到遇到类似需求的小伙伴们，如果觉得有不好的地方或者有没想到的地方也可以告诉我，我会认真采纳的。**
## 每天都要过得开心 ( ゜- ゜)つロ乾杯 ！


### 效果图
<img src="http://otjav6lvw.bkt.clouddn.com/17-7-29/92985778.jpg" width="300"/>
<img src="http://otjav6lvw.bkt.clouddn.com/17-7-29/66349083.jpg" width="300"/>
<img src="http://otjav6lvw.bkt.clouddn.com/17-7-29/75740996.jpg" width="300"/>
<img src="http://otjav6lvw.bkt.clouddn.com/17-7-29/3381654.jpg" width="300"/>

### 使用方法
#### 栗子一
> 最简单的使用方式，也就是 `图1` 的显示。</br> 解释一下：addImage方法四个参数分别是图片资源、x轴的位置（总共10.0f）、y轴的位置（总共10.0f）、图片的缩放比例、点击图片蒙版是否消失。addTarget方法是设置透明块要在那个View的位置上，可以有两个参数，第二个参数可以选择透明块的样式。

```
new ShowcaseView.Builder(this)
        .addTarget(mTextView)
        .addImage(R.mipmap.img_showcase, 5.0f, 5.0f, 1.5f, true)
        .addShowcaseListener(new ShowcaseView.ShowcaseListener() {
            @Override
            public void onDisplay(ShowcaseView showcaseView) {
            }

            @Override
            public void onDismiss(ShowcaseView showcaseView) {
                Intent intent = new Intent(
                  MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        })
        .build().show();
```
#### 栗子二
>  `图2 图3 图4` 是另一种添加到队列再显示的方式，也就是说当第一个蒙版消失后会接着显示第二个，接着第三个...直到把队列中的蒙版都显示完。addShowcaseQueue方法用来添加到队列，最后通过showQueue方法来依次显示。

```
new ShowcaseView.Builder(this)
        .setMaskColor("#88EECC33")
        .setDismissOnTouch(true)
        .setDuration(1000L, 1000L)
        .setTargetPadding(20)
        .addTarget(mTextView, ShowcaseView.CIRCLE_SHAPE)
        .addShowcaseQueue()
        .setMaskColor("#66FFB6C1")
        .addTarget(mTextView2, ShowcaseView.OVAL_SHAPE)
        .addShowcaseQueue()
        .setMaskColor("#D8BFD8")
        .setDismissOnTouch(false)
        .addTarget(mTextView3, ShowcaseView.RECTANGLE_SHAPE)
        .addImage(R.mipmap.img_showcase, 5.0f, 8.0f, 1.0f, true)
        .addShowcaseListener(new ShowcaseView.ShowcaseListener() {
            @Override
            public void onDisplay(ShowcaseView showcaseView) {
                Toast.makeText(getApplication(),
                "最后一个展示啦",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDismiss(ShowcaseView showcaseView) {
                Toast.makeText(getApplication(),
                "最后一个消失啦",Toast.LENGTH_SHORT).show();
            }
        })
        .addShowcaseQueue()
        .build().showQueue();
```

### PS: 我自我感觉代码写的还是比较好懂的，希望有小伙伴可以对我的实现方式提出意见。有一个问题还没想到比较好的解决方案，就是屏幕适配的问题，图片的位置可能在不同的手机上有偏差，想要图片显示全并且在理想的位置还是挺难的，希望有人给我一些提示吧。
