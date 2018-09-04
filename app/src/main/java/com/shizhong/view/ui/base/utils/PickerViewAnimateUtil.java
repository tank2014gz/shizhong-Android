package com.shizhong.view.ui.base.utils;

import android.view.Gravity;

import com.shizhong.view.ui.R;


/**
 * Created by Sai on 15/8/9.
 */
public class PickerViewAnimateUtil {
    private static final int INVALID = -1;
    /**
     * Get default animation resource when not defined by the user
     *
     * @param gravity       the gravity of the dialog
     * @param isInAnimation determine if is in or out animation. true when is is
     * @return the id of the animation resource
     */
    public static int getAnimationResource(int gravity, boolean isInAnimation) {
        switch (gravity) {
            case Gravity.BOTTOM:
                return isInAnimation ? R.anim.umeng_socialize_slide_in_from_bottom : R.anim.umeng_socialize_slide_out_from_bottom;
        }
        return INVALID;
    }
}
