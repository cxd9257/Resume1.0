package com.harlan.resume.widget;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class OnClickListenerForScrolling implements OnClickListener{
	private static final String TAG="OnClickListenerForScrolling";
	
	HomeHorizontalScrollView scrollView;
    View homeView;
    
    ImageView imgView;
      /**
       * Menu must NOT be out/shown to start with.
       */
      boolean menuOut = true;

      public OnClickListenerForScrolling(HomeHorizontalScrollView scrollView, View homeView,ImageView imgView) {
          super();
          this.scrollView = scrollView;
          this.homeView = homeView;
          this.imgView = imgView;
      }

      @Override
      public void onClick(View v) {
//          Context context = menu.getContext();

          int menuWidth = homeView.getMeasuredWidth();

          // Ensure menu is visible
          homeView.setVisibility(View.VISIBLE);

          if (!menuOut) {
              // Scroll to 0 to reveal menu
              int left = 0;
              Log.d(TAG,"menuOut == false");
              scrollView.smoothScrollTo(left, 0);
              scrollView.setIsIntercept(menuOut);
              imgView.setVisibility(View.VISIBLE);
          } else {
              // Scroll to menuWidth so menu isn't on screen.
              int left = menuWidth;
              Log.d(TAG,"menuOut == true");
              scrollView.smoothScrollTo(left, 0);
              scrollView.setIsIntercept(menuOut);
              imgView.setVisibility(View.GONE);
          }
          menuOut = !menuOut;
      }
      
      public void setMenuOut(){
    	  menuOut = !menuOut;
      }
      
      public void scrollToNeiborView(){
    	  int menuWidth = homeView.getMeasuredWidth();
          homeView.setVisibility(View.VISIBLE);
          int left = menuWidth;
          Log.d(TAG,"scrollToNeiborView");
          scrollView.smoothScrollTo(left, 0);
          scrollView.setIsIntercept(menuOut);
          imgView.setVisibility(View.GONE);
          menuOut = false;
      }
      
      public void scrollToHomeView(){
    	  int left = 0;
          Log.d(TAG,"scrollToHomeView");
          scrollView.smoothScrollTo(left, 0);
          scrollView.setIsIntercept(menuOut);
          imgView.setVisibility(View.VISIBLE);
          menuOut = true;
      }

}
