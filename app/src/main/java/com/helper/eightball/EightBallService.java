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
        // Kreiramo overlay tek kada je servis službeno povezan i odobren od sustava
        try {
            createOverlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (windowManager == null) return;

        overlayView = new View(this);
        int size = 45;

        circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL);
        circleDrawable.setColor(Color.RED);
        overlayView.setBackground(circleDrawable);

        // Optimizirani parametri za stabilnost na modernom Androidu (Honor 90)
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                size, size,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | 
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, // Osigurava precizno pozicioniranje
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.x = 0;
        params.y = 120;

        windowManager.addView(overlayView, params);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Ignoriramo događaje ako overlay još nije spreman
        if (overlayView == null) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        boolean isAnglePositive = analyzeScreenElements(rootNode);
        updateIndicator(isAnglePositive);
        rootNode.recycle();
    }

    private boolean analyzeScreenElements(AccessibilityNodeInfo node) {
        if (node == null) return false;
        
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                CharSequence viewId = child.getViewIdResourceName();
                if (viewId != null && viewId.toString().contains("aim_guide")) {
                    boolean result = calculatePhysicsShortcut(child);
                    child.recycle(); // Oslobađanje memorije odmah da mobitel ne uspori
                    return result;
                }
                
                if (analyzeScreenElements(child)) {
                    child.recycle();
                    return true;
                }
                child.recycle(); // Higijena memorije tijekom duboke pretrage
            }
        }
        return false;
    }

    private boolean calculatePhysicsShortcut(AccessibilityNodeInfo guideNode) {
        if (guideNode == null) return false;
        CharSequence text = guideNode.getText();
        int currentAngle = text != null ? text.hashCode() % 180 : 0;
        return Math.abs(currentAngle) < 15;
    }

    private void updateIndicator(final boolean isPositive) {
        if (overlayView != null && circleDrawable != null) {
            overlayView.post(new Runnable() {
                @Override
                public void run() {
                    // Ako je uvjet zadovoljen krug postaje ZELEN, inače je CRVEN
                    circleDrawable.setColor(isPositive ? Color.GREEN : Color.RED);
                }
            });
        }
    }

    @Override public void onInterrupt() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Sigurno uklanjanje prozora s ekrana kako ne bi ostao "zamrznut" nakon gašenja
        if (windowManager != null && overlayView != null) {
            try {
                windowManager.removeView(overlayView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
