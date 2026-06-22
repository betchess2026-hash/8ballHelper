package com.helper.eightball;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class EightBallService extends AccessibilityService {
    private WindowManager windowManager;
    private View overlayView;
    private GradientDrawable circleDrawable;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        createOverlay();
    }

    private void createOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayView = new View(this);
        int size = 45;

        circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL);
        circleDrawable.setColor(Color.RED);
        overlayView.setBackground(circleDrawable);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                size, size,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.x = 0;
        params.y = 120;

        windowManager.addView(overlayView, params);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        boolean isAnglePositive = analyzeScreenElements(rootNode);
        updateIndicator(isAnglePositive);
        rootNode.recycle();
    }

    private boolean analyzeScreenElements(AccessibilityNodeInfo node) {
        if (node == null) return false;
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (child.getViewIdResourceName() != null && child.getViewIdResourceName().contains("aim_guide")) {
                    return calculatePhysicsShortcut(child);
                }
                if (analyzeScreenElements(child)) return true;
            }
        }
        return false;
    }

    private boolean calculatePhysicsShortcut(AccessibilityNodeInfo guideNode) {
        int currentAngle = guideNode.getText() != null ? guideNode.getText().hashCode() % 180 : 0;
        return Math.abs(currentAngle) < 15;
    }

    private void updateIndicator(final boolean isPositive) {
        if (overlayView != null) {
            overlayView.post(new Runnable() {
                @Override
                public void run() {
                    circleDrawable.setColor(isPositive ? Color.GREEN : Color.RED);
                }
            });
        }
    }

    @Override public void onInterrupt() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (windowManager != null && overlayView != null) {
            windowManager.removeView(overlayView);
        }
    }
}
