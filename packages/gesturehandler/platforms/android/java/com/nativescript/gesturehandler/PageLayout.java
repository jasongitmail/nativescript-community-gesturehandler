package com.nativescript.gesturehandler;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import org.nativescript.widgets.ItemSpec;
import org.nativescript.widgets.GridUnitType;

import com.swmansion.gesturehandler.GestureHandlerOrchestrator;
import com.swmansion.gesturehandler.GestureHandlerRegistryImpl;
import com.swmansion.gesturehandler.PointerEventsConfig;
import com.swmansion.gesturehandler.GestureHandler;
import com.swmansion.gesturehandler.ViewConfigurationHelper;

import java.util.ArrayList;

public class PageLayout extends org.nativescript.widgets.GridLayout {
    public PageLayout(Context context, int rootGestureTag) {
        super(context);
        mRootGestureTag = rootGestureTag;
        addRow(new ItemSpec(1, GridUnitType.auto));
        addRow(new ItemSpec(1, GridUnitType.star));
    }

    private int mRootGestureTag;
    private GestureHandlerOrchestrator mOrchestrator;
    private GestureHandlerRegistryImpl mRegistry;
    private ViewConfigurationHelper configurationHelper;
    RootViewGestureHandler rootGestureHandler;

    private boolean mShouldIntercept = false;
    private boolean mPassingTouch = false;
    private boolean mDispatchToOrchestra = true;
    private boolean mShouldAddRootGesture = true;

    public void setShouldIntercept(boolean value) {
        if (GestureHandler.debug) {
            Log.d("JS", "PageLayout setShouldIntercept " + value);
        }
        this.mShouldIntercept = value;
    }

    public void setPassingTouch(boolean value) {
        this.mPassingTouch = value;
    }
    public void setShouldAddRootGesture(boolean value) {
        this.mShouldAddRootGesture = value;
    }

    public int getRootGestureTag() {
        return mRootGestureTag;
    }

    public void setDispatchToOrchestra(boolean value) {
        if (GestureHandler.debug) {
            Log.d("JS", "PageLayout setDispatchToOrchestra " + value);
        }
        this.mDispatchToOrchestra = value;
    }

    public GestureHandlerRegistryImpl registry() {
        return this.mRegistry;
    }
    // requestDisallowInterceptTouchEvent(disallowIntercept) {
    //     console.log('requestDisallowInterceptTouchEvent');
    //     if (this.mGestureRootHelper != null) {
    //         this.mGestureRootHelper.requestDisallowInterceptTouchEvent(disallowIntercept);
    //     }
    //     super.requestDisallowInterceptTouchEvent(disallowIntercept);
    // }

    // dispatchTouchEvent(ev) {
    //     if (this.mGestureRootHelper != null && this.mGestureRootHelper.dispatchTouchEvent(ev)) {
    //         return true;
    //     }
    //     return super.dispatchTouchEvent(ev);
    // }
    public void tryCancelAllHandlers() {
        if (GestureHandler.debug) {
            Log.d("JS", "PageLayout tryCancelAllHandlers ");
        }
        ArrayList<GestureHandler> handlers = this.mRegistry.getAllHandlers();
        if (handlers != null) {
            for(int i = 0; i < handlers.size(); i++) {
                GestureHandler handler = handlers.get(i);
                if (handler != this.rootGestureHandler) {
                    handler.cancel();
                }
            }
        }
        // In order to cancel handlers we activate handler that is hooked to the root view
        // if (this.rootGestureHandler != null && this.rootGestureHandler.getState() == com.swmansion.gesturehandler.GestureHandler.STATE_BEGAN) {
        //     // Try activate main JS handler
        //     this.rootGestureHandler.activate();
        //     this.rootGestureHandler.end();
        // }
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (GestureHandler.debug) {
            Log.d("JS", "PageLayout requestDisallowInterceptTouchEvent " + disallowIntercept + " " + this.mPassingTouch);
        }
        // If this method gets called it means that some native view is attempting to grab lock for
        // touch event delivery. In that case we cancel all gesture recognizers
        if (this.mOrchestrator != null && !this.mPassingTouch) {
            // if we are in the process of delivering touch events via GH orchestrator, we don't want to
            // treat it as a native gesture capturing the lock
            this.tryCancelAllHandlers();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public boolean dispatchTouchEventToOrchestrator(MotionEvent ev) {
        if (this.mOrchestrator != null) {
            this.mPassingTouch = true;
            this.mOrchestrator.onTouchEvent(ev);
            this.mPassingTouch = false;
        }
        if (GestureHandler.debug) {
            Log.d("JS", "PageLayout dispatchTouchEventToOrchestrator " + this.mShouldIntercept);
        }
        return this.mShouldIntercept;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (GestureHandler.debug) {
            Log.d("JS", "PageLayout dispatchTouchEvent " + this.mDispatchToOrchestra);
        }
        if (this.mDispatchToOrchestra && this.dispatchTouchEventToOrchestrator(ev)) {
            return true;
        }
        final boolean handled = super.dispatchTouchEvent(ev);
        if (GestureHandler.debug) {
            Log.d("JS", "PageLayout dispatchTouchEvent to children " + handled);
        }
        // we need to always return true or gestures wont work on layouts because they don't handle touch so dispatchTouchEvent returns false
        return true;
    }

    // onInterceptTouchEvent(ev: android.view.MotionEvent) {
    //     return this.mShouldIntercept;
    // }

    // onTouchEvent(ev: android.view.MotionEvent) {
    //     console.log('onTouchEvent', ev);
    //     this.mOrchestrator.onTouchEvent(ev);
    //     return super.onTouchEvent(ev);
    // }

    /**
     * This method is used to enable root view to start processing touch events through the gesture
     * handler library logic. Unless this method is called (which happens as a result of instantiating
     * new gesture handler from JS) the root view component will just proxy all touch related methods
     * to its superclass. Thus in the "disabled" state all touch related events will fallback to
     * default behavior.
     */
    public void initialize() {
        this.mRegistry = new com.swmansion.gesturehandler.GestureHandlerRegistryImpl();
        this.configurationHelper = new com.swmansion.gesturehandler.ViewConfigurationHelper() {
            public PointerEventsConfig getPointerEventsConfigForView(View view) {
                return view.isEnabled() ? com.swmansion.gesturehandler.PointerEventsConfig.AUTO : com.swmansion.gesturehandler.PointerEventsConfig.NONE;
            }

            public boolean isViewClippingChildren(ViewGroup parent) {
                return false;
            }

            public View getChildInDrawingOrderAtIndex(ViewGroup parent, int index) {
                return parent.getChildAt(index);
            }
        };
        this.mOrchestrator = new com.swmansion.gesturehandler.GestureHandlerOrchestrator(this, this.mRegistry, this.configurationHelper);
        this.mOrchestrator.setMinimumAlphaForTraversal(0.01f);

        this.rootGestureHandler = new RootViewGestureHandler();
        this.rootGestureHandler.setTag(mRootGestureTag);
        this.mRegistry.registerHandler(this.rootGestureHandler);
        this.mRegistry.attachHandlerToView(mRootGestureTag, this);
    }

    public void tearDown() {
        this.configurationHelper = null;
        this.mOrchestrator = null;
        this.mRegistry = null;
    }
}