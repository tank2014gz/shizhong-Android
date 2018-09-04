package com.shizhong.view.ui.base.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.os.Build;

/**
 * Created by yuliyan on 15/12/29.
 */
public class AnimatorPlayer extends AnimatorListenerAdapter {

    private boolean interrupted = false;
    private Animator[] animators;

    public AnimatorPlayer(Animator[] animators) {
        this.animators = animators;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!interrupted) animate();
    }

    public void play() {
        animate();
    }

    public void stop() {
        interrupted = true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animate() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.addListener(this);
        set.start();
    }
}
