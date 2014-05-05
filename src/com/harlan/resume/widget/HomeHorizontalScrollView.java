package com.harlan.resume.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;

/**
 * HorizontalScrollView(HSV)实现,不允许触摸事件(所以没有滚动可以通过用户)。
 * 
 * This HSV MUST contain a single ViewGroup as its only child, and this ViewGroup will be used to display the children Views
 * passed in to the initViews() method.
 */
public class HomeHorizontalScrollView extends HorizontalScrollView {
	
	private static final String TAG ="HomeHorizontalScrollView";
	
	ViewGroup mParent;
	
	View[] mChildren;
	
	boolean isIntercept;
	
    public HomeHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public HomeHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeHorizontalScrollView(Context context) {
        super(context);
        init(context);
    }
    
    public void setIsIntercept(boolean isIntercept){
    	this.isIntercept = isIntercept;
    }

    void init(Context context) {
        // remove the fading as the HSV looks better without it
        setHorizontalFadingEdgeEnabled(false);
        setVerticalFadingEdgeEnabled(false);
    }
    
    
    /**
     * @param children
     *            The child Views to add to parent.
     * @param scrollToViewIdx
     *            The index of the View to scroll to after initialisation.
     * @param sizeCallback
     *            A SizeCallback to interact with the HSV.
     */
    public void initViews(View[] children, int scrollToViewIdx, SizeCallback sizeCallback) {
        // A ViewGroup MUST be the only child of the HSV
    	mParent = (ViewGroup) getChildAt(0);
    	mChildren = children;
        // Add all the children, but add them invisible so that the layouts are calculated, but you can't see the Views
        for (int i = 0; i < children.length; i++) {
            children[i].setVisibility(View.INVISIBLE);
            mParent.addView(children[i]);
        }

        // Add a layout listener to this HSV
        // This listener is responsible for arranging the child views.
        OnGlobalLayoutListener listener = new MyOnGlobalLayoutListener(mParent, children, scrollToViewIdx, sizeCallback);
        getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Do not allow touch events.
    	Log.d(TAG,"onTouchEvent+ev.getRawY():"+ev.getRawY());
//    	Log.d(TAG,"onTouchEvent+isIntercept:"+isIntercept);
//    	return isIntercept;
    	return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Do not allow touch events.
    	Log.d(TAG,"onInterceptTouchEvent");
//    	if (ev.getRawY()>150){
//            return true;
//        }else{
//        	return false;
//        	}
    	return false;
    }
    
	public void removeAllChildView() {
		mParent.removeViewsInLayout(0, mChildren.length);
	}

	public void addAllChildView() {
		for (int i = 0; i < mChildren.length; i++) {
			mChildren[i].setVisibility(View.INVISIBLE);
			mParent.addView(mChildren[i]);
		}
	}

    /**
     * An OnGlobalLayoutListener impl that passes on the call to onGlobalLayout to a SizeCallback, before removing all the Views
     * in the HSV and adding them again with calculated widths and heights.
     */
    class MyOnGlobalLayoutListener implements OnGlobalLayoutListener {
        ViewGroup parent;
        View[] children;
        int scrollToViewIdx;
        int scrollToViewPos = 0;
        SizeCallback sizeCallback;

        /**
         * @param parent
         *            The parent to which the child Views should be added.
         * @param children
         *            The child Views to add to parent.
         * @param scrollToViewIdx
         *            The index of the View to scroll to after initialisation.
         * @param sizeCallback
         *            A SizeCallback to interact with the HSV.
         */
        public MyOnGlobalLayoutListener(ViewGroup parent, View[] children, int scrollToViewIdx, SizeCallback sizeCallback) {
            this.parent = parent;
            this.children = children;
            this.scrollToViewIdx = scrollToViewIdx;
            this.sizeCallback = sizeCallback;
        }

        @Override
        public void onGlobalLayout() {
        	Log.d(TAG,"onGlobalLayout");

            final HorizontalScrollView me = HomeHorizontalScrollView.this;

            // The listener will remove itself as a layout listener to the HSV
            me.getViewTreeObserver().removeGlobalOnLayoutListener(this);

            // Allow the SizeCallback to 'see' the Views before we remove them and re-add them.
            // This lets the SizeCallback prepare View sizes, ahead of calls to SizeCallback.getViewSize().
            sizeCallback.onGlobalLayout();

            parent.removeViewsInLayout(0, children.length);

            final int w = me.getMeasuredWidth();
            final int h = me.getMeasuredHeight();

            // System.out.println("w=" + w + ", h=" + h);

            // Add each view in turn, and apply the width and height returned by the SizeCallback.
            int[] dims = new int[2];
            scrollToViewPos = 0;
            for (int i = 0; i < children.length; i++) {
                sizeCallback.getViewSize(i, w, h, dims);
                // System.out.println("addView w=" + dims[0] + ", h=" + dims[1]);
                children[i].setVisibility(View.VISIBLE);
                parent.addView(children[i], dims[0], dims[1]);
                if (i < scrollToViewIdx) {
                    scrollToViewPos += dims[0];
                }
            }

            // For some reason we need to post this action, rather than call immediately.
            // If we try immediately, it will not scroll.
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    me.scrollBy(scrollToViewPos, 0);
                }
            });
        }
    }

    /**
     * Callback interface to interact with the HSV.
     */
    public interface SizeCallback {
        /**
         * Used to allow clients to measure Views before re-adding them.
         */
        public void onGlobalLayout();

        /**
         * Used by clients to specify the View dimensions.
         * 
         * @param idx
         *            Index of the View.
         * @param w
         *            Width of the parent View.
         * @param h
         *            Height of the parent View.
         * @param dims
         *            dims[0] should be set to View width. dims[1] should be set to View height.
         */
        public void getViewSize(int idx, int w, int h, int[] dims);
    }
}
