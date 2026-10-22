package com.smartpantry.manager.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/** Allows a mouse wheel to scroll a page in the emulator and on devices with a mouse. */
public class WheelScrollView extends ScrollView {
    public WheelScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_SCROLL
                && (event.getSource() & InputDevice.SOURCE_CLASS_POINTER) != 0) {
            float axis = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
            if (axis != 0f) {
                int distance = Math.round(-axis
                        * ViewConfiguration.get(getContext()).getScaledVerticalScrollFactor());
                if (distance == 0) distance = axis > 0 ? -1 : 1;
                if (canScrollVertically(Integer.signum(distance))) {
                    scrollBy(0, distance);
                    return true;
                }
            }
        }
        return super.onGenericMotionEvent(event);
    }
}
