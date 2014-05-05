package com.harlan.resume.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harlan.resume.activity.R;
import com.harlan.resume.manager.SweepCallback;
import com.harlan.resume.util.ConstValue;

@SuppressLint({ "DrawAllocation", "Recycle"})
public class SweepItemView extends FrameLayout {

	FrameLayout.LayoutParams mainfr ;
	FrameLayout.LayoutParams txtfr = null;
	
	private FrameLayout rootView = null;
//	private FrameLayout callView = null;
//	private FrameLayout msgView = null;
	private FrameLayout mCallFrame = null;
	private FrameLayout mMsgFrame = null;
	
	private FrameLayout mainView = null;
	private View mContentView = null;
	
	ImageView callHintView = null;
	ImageView msgHintView = null;
	TextView hintText = null;
	TextView telTxt = null;
	private int width;
	private int height;
	private Context mContext;
	
	SweepCallback sweepcall;

	private float mLastMotionX;
	// the position of drag item in the list
	public static int sDeltaX = 0;
	public static int sDeltaY = 0;
	// the position of drag item in the screen
	public static int selectedItemNum;
	// the max speed in the direction of the X,Y axis
	public static float sMaxVelX = 0;
	public static float sMaxVelY = 0;
	// the first coordinate of touch in he direction of the X,Y axis
	private static float sFirstMotionX;
	// the last coordinate of touch in he direction of the X,Y axis
	private static float sLastMotionX;
	public static boolean isLongClickEnabled = true;
	public static boolean isDragEnabled = true;
	// set the flag to do something when user dragging
	private static int WHAT_TO_DO = 0;
	final static int MAKE_NOTHING = 0;
	final static int MAKE_A_CALL = 1;
	final static int MAKE_A_MSG = 2;
	// the parameter of flying off animation
	public static int testX = 0;
	public static boolean isStatusStatic;

	// the speed of touch
	private VelocityTracker mVelocityTracker = null;

	public SweepItemView(Context context) {
		super(context);
		mContext = context;
	}

	public SweepItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public SweepItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public SweepItemView(Activity context,View view,SweepCallback sweepcall) {
		super(context);
		mContentView = view;
		mContext = context;
		this.sweepcall = sweepcall;
		DisplayMetrics dMetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
		width = dMetrics.widthPixels;
		height = dMetrics.heightPixels;
		isStatusStatic = false;
		Log.e("sjy", "SweepItemView width: "+this.width);
		Log.e("sjy", "SweepItemView height: "+height);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		rootView = this;
		setMeasuredDimension(width, height/8);
		FrameLayout.LayoutParams fr = new LayoutParams(width*2, 150);
		fr.gravity = Gravity.RIGHT;
		setLayoutParams(fr);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mainView == null) {
			
			mainView = new FrameLayout(mContext);
			mainfr = new LayoutParams(width * 2, height/8);
			mainView.setLayoutParams(mainfr);
			
			FrameLayout.LayoutParams callfr = new LayoutParams(width * 2, height/8);
			callfr.gravity = Gravity.RIGHT;
			mCallFrame = new FrameLayout(mContext);
			mCallFrame.setLayoutParams(callfr);
			mCallFrame.setBackgroundResource(R.drawable.sweeplist_call_tab);
			mCallFrame.setVisibility(View.INVISIBLE);
			mainView.addView(mCallFrame);
			
			FrameLayout.LayoutParams msgfr = new LayoutParams(width*2, height/8);
			msgfr.gravity = Gravity.RIGHT;
			mMsgFrame = new FrameLayout(mContext);
			mMsgFrame.setLayoutParams(msgfr);
			mMsgFrame.setBackgroundResource(R.drawable.sweeplist_message_tab);
			mMsgFrame.setVisibility(View.INVISIBLE);
			mainView.addView(mMsgFrame);
			
			rootView.addView(mainView);
			
			FrameLayout.LayoutParams contentfr = new LayoutParams(width, height/8);
			contentfr.gravity = Gravity.CENTER;
			rootView.addView(mContentView,contentfr);
			
			Log.e("sjy", "mContentView.getMeasuredHeight()111: "+mContentView.getMeasuredHeight());
			Log.e("sjy", "mContentView.getHeight()1111: "+mContentView.getHeight());
			
			callHintView = new ImageView(mContext);
			callHintView.setImageResource(R.drawable.contact_icon_makecall);
			callHintView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			callHintView.setLayoutParams(new Gallery.LayoutParams(height/8, height/8));
			callHintView.setVisibility(View.INVISIBLE);
			rootView.addView(callHintView);

			msgHintView = new ImageView(mContext);
			msgHintView.setImageResource(R.drawable.contact_icon_makemsg);
			msgHintView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			msgHintView.setLayoutParams(new Gallery.LayoutParams(width * 2 - height/8, height/8));
			msgHintView.setVisibility(View.INVISIBLE);
			rootView.addView(msgHintView);

			hintText = new TextView(mContext);
			hintText.setText(R.string.hello_world);
			hintText.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams hintTxtParams = new LinearLayout.LayoutParams(
					width, height/8);
			hintText.setLayoutParams(hintTxtParams);
			hintText.setVisibility(View.INVISIBLE);
			hintText.setShadowLayer(1, 2, 2, Color.BLACK);
			hintText.setTextColor(Color.WHITE);
			hintText.setTextSize(25);
			rootView.addView(hintText);
		}
	}

	final Handler myHandler = new Handler();
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final float x = event.getX();
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.e("myview", "MotionEvent.ACTION_DOWN  mainView.getScrollX():"+mainView.getScrollX());
			if(mainView.getScrollX()!=0) {
				isStatusStatic = false;
				myHandler.postDelayed(invalidateRun, 100);
				break;
			}
			isStatusStatic = true;
			Log.e("myview", "dispatchTouchEvent  ACTION_DOWN");
			sMaxVelX = 0;
			sMaxVelY = 0;
			sFirstMotionX = 0;
			sLastMotionX = 0;
			WHAT_TO_DO = MAKE_NOTHING;
			sFirstMotionX = event.getX();
			mLastMotionX = sFirstMotionX;
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			
			break;
		case MotionEvent.ACTION_MOVE:
			if(!isStatusStatic)  break;
			sweepcall.responseSweep(ConstValue.SWEEP_HIDE);
			try {
				mVelocityTracker.addMovement(event);
			} catch (NullPointerException e) {
				e.printStackTrace();
				return true;
			}
			final VelocityTracker velocityTracker = mVelocityTracker;
			// get the the value of sMaxVelX ,sMaxVelY
			velocityTracker.computeCurrentVelocity(1000);
			float tempVelX = velocityTracker.getXVelocity();
			float tempVelY = velocityTracker.getYVelocity();
			
			sMaxVelX = Math.abs(sMaxVelX) > Math.abs(tempVelX) ? Math
					.abs(sMaxVelX) : Math.abs(tempVelX);
			sMaxVelY = Math.abs(sMaxVelY) > Math.abs(tempVelY) ? Math
					.abs(sMaxVelY) : Math.abs(tempVelY);
			sLastMotionX = x;
			// get the offset distance in the direction of the X axis
			final int deltaX = (int) (mLastMotionX - x);
			mLastMotionX = x;
				if (mainView.getScrollX() < 0) {
					rootView.setBackgroundResource(R.drawable.sweeplist_call_tab);
					mainfr.gravity = Gravity.RIGHT;
					mainView.setLayoutParams(mainfr);
					mMsgFrame.setVisibility(View.INVISIBLE);
					mCallFrame.setVisibility(View.VISIBLE);
				} else if (mainView.getScrollX() > 0) {
					rootView.setBackgroundResource(R.drawable.sweeplist_message_tab);
					mainfr.gravity = Gravity.LEFT;
					mainView.setLayoutParams(mainfr);
					mCallFrame.setVisibility(View.INVISIBLE);
					mMsgFrame.setVisibility(View.VISIBLE);
				} else {
					mMsgFrame.setVisibility(View.INVISIBLE);
					mCallFrame.setVisibility(View.INVISIBLE);
					rootView.setBackgroundResource(0);
				}
				try {
					// if the drag is from left to right
					if (sLastMotionX - sFirstMotionX > 80) {
//						rootView.setBackgroundResource(R.drawable.green);
						hintText.setText(R.string.sweep_call);
						hintText.setVisibility(View.VISIBLE);
						callHintView.setVisibility(View.VISIBLE);
						msgHintView.setVisibility(View.INVISIBLE);
						
					} else if (sLastMotionX - sFirstMotionX < -80) {
//						rootView.setBackgroundResource(R.drawable.yellow);
						hintText.setText(R.string.sweep_msg);
						hintText.setVisibility(View.VISIBLE);
						callHintView.setVisibility(View.INVISIBLE);
						msgHintView.setVisibility(View.VISIBLE);
						
					}
					mainView.scrollBy(deltaX, 0);
					mContentView.scrollBy(deltaX, 0);
					return true;
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				
			break;
		case MotionEvent.ACTION_UP:
			isStatusStatic = false;
			Log.e("myview", "dispatchTouchEvent  ACTION_UP");
					// the offset distance in the direction of the X axis
					if (sLastMotionX - sFirstMotionX < -width/3 && sLastMotionX!=0) {
						Log.e("myview", "dispatchTouchEvent  sLastMotionX:"+ sLastMotionX);
						Log.e("myview", "dispatchTouchEvent  sFirstMotionX:"+ (sFirstMotionX));
						isStatusStatic = true;
						// to avoid the background is black
						rootView.setBackgroundResource(R.drawable.yellow);
						// the thread is for the flying off animation
						myHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								testX += 5;
								mainView.scrollBy(testX, 0);
								mContentView.scrollBy(testX, 0);
								if (testX < 100 && isStatusStatic) {
									myHandler.postDelayed(this, 10);
								} else {
									if (isStatusStatic) {
										WHAT_TO_DO = MAKE_A_MSG;
										sweepcall.responseSweep(ConstValue.SWEEP_MAKEMSG);
									}
									mCallFrame.setVisibility(View.INVISIBLE);
									mMsgFrame.setVisibility(View.INVISIBLE);
									mainView.scrollTo(0, 0);
									mContentView.scrollTo(0, 0);
									rootView.setBackgroundColor(0);
//									isStatusStatic = false;
//									myHandler.postDelayed(invalidateRun, 100);
									try {
										// telTxt.setAlpha((float) 1.0);
										hintText.setVisibility(View.INVISIBLE);
										callHintView.setVisibility(View.INVISIBLE);
										msgHintView.setVisibility(View.INVISIBLE);
									} catch (NullPointerException e) {
										e.printStackTrace();
									}
								}
							}
						}, 100);
					} else if (sLastMotionX - sFirstMotionX > width/3) {
						isStatusStatic = true;
						rootView.setBackgroundResource(R.drawable.green);
						myHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								testX -= 5;
								mainView.scrollBy(testX, 0);
								mContentView.scrollBy(testX, 0);
								if (testX > -100 && isStatusStatic) {
									myHandler.postDelayed(this, 10);
								} else {
									if (isStatusStatic) {
										WHAT_TO_DO = MAKE_A_CALL;
										sweepcall.responseSweep(ConstValue.SWEEP_MAKECALL);
									}
									mCallFrame.setVisibility(View.INVISIBLE);
									mMsgFrame.setVisibility(View.INVISIBLE);
									mainView.scrollTo(0, 0);
									mContentView.scrollTo(0, 0);
									rootView.setBackgroundColor(0);
//									isStatusStatic = false;
//									myHandler.postDelayed(invalidateRun, 100);
									try {
										// telTxt.setAlpha((float) 1.0);
										hintText.setVisibility(View.INVISIBLE);
										callHintView.setVisibility(View.INVISIBLE);
										msgHintView.setVisibility(View.INVISIBLE);
									} catch (NullPointerException e) {
										e.printStackTrace();
									}
								}
							}
						}, 10);
					} else if (sLastMotionX - sFirstMotionX > -width/3
							&& sLastMotionX - sFirstMotionX < 0) {
						myHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mainView.scrollBy(-10, 0);
								mContentView.scrollBy(-10, 0);
								if (mainView.getScrollX() > 0) {
									myHandler.postDelayed(this, 10);
								} else {
									mCallFrame.setVisibility(View.INVISIBLE);
									mMsgFrame.setVisibility(View.INVISIBLE);
									mainView.scrollTo(0, 0);
									mContentView.scrollTo(0, 0);
									rootView.setBackgroundColor(0);
//									isStatusStatic = false;
//									myHandler.postDelayed(invalidateRun, 100);
									try {
										// telTxt.setAlpha((float) 1.0);
										hintText.setVisibility(View.INVISIBLE);
										callHintView.setVisibility(View.INVISIBLE);
										msgHintView.setVisibility(View.INVISIBLE);
									} catch (NullPointerException e) {
										e.printStackTrace();
									}
								}
							}
						}, 100);
					} else if (sLastMotionX - sFirstMotionX < width/3
							&& sLastMotionX - sFirstMotionX > 0) {
						myHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mainView.scrollBy(10, 0);
								mContentView.scrollBy(10, 0);
								if (mainView.getScrollX() < 0) {
									myHandler.postDelayed(this, 10);
								} else {
									mCallFrame.setVisibility(View.INVISIBLE);
									mMsgFrame.setVisibility(View.INVISIBLE);
									mainView.scrollTo(0, 0);
									mContentView.scrollTo(0, 0);
									rootView.setBackgroundColor(0);
//									isStatusStatic = false;
//									myHandler.postDelayed(invalidateRun, 100);
									try {
										// telTxt.setAlpha((float) 1.0);
										hintText.setVisibility(View.INVISIBLE);
										callHintView.setVisibility(View.INVISIBLE);
										msgHintView.setVisibility(View.INVISIBLE);
									} catch (NullPointerException e) {
										e.printStackTrace();
									}
								}
							}
						}, 100);
					}
//					event.setAction(MotionEvent.ACTION_CANCEL);
					testX = 0;
//					myHandler.postDelayed(invalidateRun, 2000);
			break;
		}
		return true;
	}
	
	
	Runnable invalidateRun = new Runnable() {
	
	@Override
		public void run() {
		Log.e("myview","invalidateRuninvalidateRun");
		mCallFrame.setVisibility(View.INVISIBLE);
		mMsgFrame.setVisibility(View.INVISIBLE);
		mainView.scrollTo(0, 0);
		mContentView.scrollTo(0, 0);
		rootView.setBackgroundColor(0);
		try {
			// telTxt.setAlpha((float) 1.0);
			hintText.setVisibility(View.INVISIBLE);
			callHintView.setVisibility(View.INVISIBLE);
			msgHintView.setVisibility(View.INVISIBLE);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
	}
};

	// the methods for activity to get
	public int getStatus() {
		return WHAT_TO_DO;
	}

	public void setStatus(int x) {
		WHAT_TO_DO = x;
	}

//	public void setisLongClickEnabled(boolean flag) {
//		isLongClickEnabled = flag;
//	}
//
//	public boolean isLongClickEnabled() {
//		return isLongClickEnabled;
//	}
//
//	public void setisDragEnabled(boolean flag) {
//		isDragEnabled = flag;
//	}
//
//	public boolean isDragEnabled() {
//		return isDragEnabled;
//	}
//
//	public int getSelectedPos() {
//		return selectedItemNum;
//	}
//
//	public boolean isVertDrag() {
//		return isVerDrag;
//	}

}
