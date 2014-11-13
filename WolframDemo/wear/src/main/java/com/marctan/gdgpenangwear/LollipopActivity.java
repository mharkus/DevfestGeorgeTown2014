package com.marctan.gdgpenangwear;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class LollipopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lollipop);

        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.droidballoons);
        addContentView(iv, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setupAnimation(iv);
    }

    private void setupAnimation(ImageView iv) {
        final ObjectAnimator yAnim = ObjectAnimator.ofFloat(iv, "y", -20f);
        final ObjectAnimator xAnim = ObjectAnimator.ofFloat(iv, "x", 200);
        final ObjectAnimator rotAnim = ObjectAnimator.ofFloat(iv, "rotation", 10);

        yAnim.setDuration(800);
        yAnim.setRepeatCount(ObjectAnimator.INFINITE);
        yAnim.setRepeatMode(ObjectAnimator.REVERSE);
        yAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        xAnim.setDuration(5000);
        xAnim.setRepeatCount(ObjectAnimator.INFINITE);
        xAnim.setRepeatMode(ObjectAnimator.REVERSE);
        xAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        rotAnim.setDuration(1400);
        rotAnim.setRepeatCount(ObjectAnimator.INFINITE);
        rotAnim.setRepeatMode(ObjectAnimator.REVERSE);


        yAnim.start();
        xAnim.start();
        rotAnim.start();
    }
}
