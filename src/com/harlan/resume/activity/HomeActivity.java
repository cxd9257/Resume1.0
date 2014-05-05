package com.harlan.resume.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.harlan.resume.adapter.FlingGalleryAdapter;
import com.harlan.resume.adapter.HomeListAdapter;
import com.harlan.resume.manager.NetworkCallback;
import com.harlan.resume.manager.SweepCallback;
import com.harlan.resume.util.ConstValue;
import com.harlan.resume.widget.FlingGallery;
import com.harlan.resume.widget.HomeHorizontalScrollView;
import com.harlan.resume.widget.OnClickListenerForScrolling;
import com.harlan.resume.widget.SizeCallbackForMenu;
import com.harlan.resume.widget.SweepItemView;

public class HomeActivity extends Activity implements OnItemClickListener,
		OnTouchListener, OnClickListener {

	private static final String TAG = "HomeActivity";

	ListView homeList;

	HomeHorizontalScrollView scrollView;
	View homeView;
	View neiborView;
	View transparent;

	View sweepHintView;
	FrameLayout sweepFM;
	ImageView exitView;
	ToggleButton mToggleButton;
	LinearLayout btnSlide;
	ViewGroup tabBar;
	LayoutInflater inflater;
	OnClickListenerForScrolling mScrolling;
	private FlingGallery mGallery;
	
	boolean isFirst;
	boolean menuOut = false;
	boolean isSweepAnim;
	Handler handler = new Handler();
	int btnWidth;
	int sweepCode = 0;

	final int[] LayoutIds = new int[] { R.layout.activity_baseinfo_layout,
			R.layout.activity_eduinfo_layout,

			R.layout.activity_rtconn_layout, R.layout.activity_blog_layout,
			R.layout.activity_workintent_layout,
			R.layout.activity_workable_layout, R.layout.activity_proexp_layout,
			R.layout.activity_salary_layout,
			R.layout.activity_otherability_layout,

			R.layout.activity_copyright_layout };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFirst = true;
		setContentView(R.layout.activity_home_scroll_layout);
		scrollView = (HomeHorizontalScrollView) findViewById(R.id.home_scrollview);
		exitView = (ImageView) findViewById(R.id.img_exit);
		exitView.setOnClickListener(this);
		exitView.setVisibility(View.VISIBLE);
		homeView = findViewById(R.id.home_context_layout);
		homeList = (ListView) findViewById(R.id.home_list);
		HomeListAdapter homeListAdapter = new HomeListAdapter(this);
		homeList.setAdapter(homeListAdapter);
		homeList.setOnItemClickListener(this);
		transparent = new TextView(this);
		transparent.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		inflater = LayoutInflater.from(this);
		initNeiborView(0);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.d(TAG, "onItemClick arg2:" + arg2);
		isFirst = false;
		exitView.setVisibility(View.GONE);
		scrollView.setIsIntercept(true);
		initNeiborView(arg2);
	}

	private void initNeiborView(int id) {
		Log.d(TAG, "initNeiborView id:" + id);
		neiborView = null;
		tabBar = null;
		btnSlide = null;
		neiborView = inflater.inflate(LayoutIds[id], null);
		tabBar = (ViewGroup) neiborView.findViewById(R.id.tabBar);
		btnSlide = (LinearLayout) tabBar.findViewById(R.id.BtnSlide);
		doNeiborViewById(id);
		mScrolling = new OnClickListenerForScrolling(scrollView, homeView,
				exitView);
		btnSlide.setOnClickListener(mScrolling);
		// Create a transparent view that pushes the other views in the HSV to
		// the right.
		// This transparent view allows the menu to be shown when the HSV is
		// scrolled.
		final View[] children = new View[] { transparent, neiborView };
		// Scroll to app (view[1]) when layout finished.
		int scrollToViewIdx;
		if (!isFirst) {
			scrollView.removeAllChildView();
			mScrolling.setMenuOut();
			scrollToViewIdx = 1;
		} else {
			scrollToViewIdx = 0;
		}
		scrollView.initViews(children, scrollToViewIdx,
				new SizeCallbackForMenu(btnSlide));
	}

	private void doNeiborViewById(int id) {
		switch (id) {
		case 0:
			RelativeLayout baseInfoLay = (RelativeLayout) neiborView
					.findViewById(R.id.baseinfo_content_view);
			baseInfoLay.setOnClickListener(null);
			break;
		case 1:
			RelativeLayout eduInfoLay = (RelativeLayout) neiborView
					.findViewById(R.id.eduinfo_content_view);
			eduInfoLay.setOnClickListener(null);
			break;

		case 2:
			RelativeLayout rtconnLay = (RelativeLayout) neiborView
					.findViewById(R.id.rtconn_content_view);
			LayoutInflater lf = LayoutInflater.from(HomeActivity.this);
			View tv = lf.inflate(R.layout.activity_rtconn_sweepitem, null);
			SweepItemView sw = new SweepItemView(HomeActivity.this, tv,
					sweepcall);
			LinearLayout.LayoutParams lfpara = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			sw.setLayoutParams(lfpara);
			sweepFM = (FrameLayout) neiborView
					.findViewById(R.id.rtconn_frm_sweep);
			isSweepAnim = true;
			sweepHintView = sweepFM.findViewById(R.id.rtconn_frm_sweep_hint);
			sweepFM.addView(sw);
			handler.removeCallbacks(sweepHintRun);
			handler.post(sweepHintRun);
			rtconnLay.setOnClickListener(null);
			break;
		case 3:
			RelativeLayout blogLay = (RelativeLayout) neiborView
					.findViewById(R.id.blog_content_view);
			blogLay.setOnClickListener(null);
			break;
		case 4:
			LinearLayout workintentLay = (LinearLayout) neiborView
					.findViewById(R.id.workintent_content_view);
			workintentLay.setOnClickListener(null);
			break;
		case 5:
			LinearLayout workableLay = (LinearLayout) neiborView
					.findViewById(R.id.workable_content_view);
			workableLay.setOnClickListener(null);
			break;
		case 6:
			mGallery = new FlingGallery(this);
			mGallery.setPaddingWidth(5);
			FlingGalleryAdapter mAdapter = new FlingGalleryAdapter(this);
			mGallery.setAdapter(mAdapter);
			LinearLayout proexpLay = (LinearLayout) neiborView
					.findViewById(R.id.proexp_content_view);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layoutParams.setMargins(5, 5, 5, 5);
			layoutParams.weight = 1.0f;
			proexpLay.addView(mGallery, layoutParams);
			proexpLay.setOnTouchListener(this);
			mToggleButton = (ToggleButton) neiborView
					.findViewById(R.id.proexp_tglbtn);
			mToggleButton.setOnClickListener(this);
			break;
		case 7:
			RelativeLayout salaryLay = (RelativeLayout) neiborView
					.findViewById(R.id.salary_content_view);
			salaryLay.setOnClickListener(null);
			break;
		case 8:
			LinearLayout otherabilityLay = (LinearLayout) neiborView
					.findViewById(R.id.otherability_content_view);
			otherabilityLay.setOnClickListener(null);
			break;
		
		case 9:
			LinearLayout copyrightLay = (LinearLayout) neiborView
					.findViewById(R.id.copyright_content_view);
			copyrightLay.setOnClickListener(null);
			break;
		}
	}

	SweepCallback sweepcall = new SweepCallback() {

		@Override
		public void responseSweep(int sweepcode) {
			switch (sweepcode) {
			case ConstValue.SWEEP_HIDE:
				sweepCode = ConstValue.SWEEP_HIDE;
				if (sweepFM.getChildCount() > 1) {
					sweepFM.removeViewAt(0);
					isSweepAnim = false;
				}
				break;
			case ConstValue.SWEEP_MAKECALL:
				sweepCode = ConstValue.SWEEP_MAKECALL;
				Intent callIt = new Intent(Intent.ACTION_CALL,
						Uri.parse("tel:10010"));
				HomeActivity.this.startActivity(callIt);
				break;
			case ConstValue.SWEEP_MAKEMSG:
				sweepCode = ConstValue.SWEEP_MAKEMSG;
				Intent msgIt = new Intent(Intent.ACTION_SENDTO,
						Uri.parse("smsto:10010"));
				HomeActivity.this.startActivity(msgIt);
				break;
			default:
				break;
			}
		}
	};

	Runnable sweepHintRun = new Runnable() {

		@Override
		public void run() {
			int vi;
			if (sweepHintView.getVisibility() == View.VISIBLE) {
				vi = View.INVISIBLE;
			} else {
				vi = View.VISIBLE;
			}
			sweepHintView.setVisibility(vi);
			if (isSweepAnim)
				handler.postDelayed(sweepHintRun, 600);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		switch (v.getId()) {
		case R.id.proexp_content_view:
			return mGallery.onGalleryTouchEvent(arg1);
		default:
			return super.onTouchEvent(arg1);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_exit:
			HomeActivity.this.finish();
			break;
		case R.id.proexp_tglbtn:
			mGallery.setIsGalleryCircular(mToggleButton.isChecked());
			break;
		
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		mScrolling.scrollToHomeView();
		// super.onBackPressed();
	}

	

}
