package com.nw.showcase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nw.widget.CustomTextViewLight;
import com.smartmanager.android.R;

import java.util.ArrayList;

public class ShowcaseLayout extends RelativeLayout {

	ImageView imageview;
	CustomTextViewLight textview;
	Context mContext;
	int baseId = 1234;
	int baseId2 = 4321;
	LayoutParams imageLayoutParams;
	LayoutParams layoutParams;
	LayoutParams textLayoutParams;

	int mLeftMargin;
	int mTopMargin;

	public enum ShowCaseType {
		Left, Right, SwipeRight, SwipeLeft, SwipeUpDown
	};

	public ShowcaseLayout(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public ShowcaseLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public ShowcaseLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {

		this.setBackgroundColor(Color.parseColor("#BF000000"));

		layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ShowcaseLayout.this.setVisibility(View.GONE);
				return true;
			}
		});
		this.setLayoutParams(layoutParams);
	}

	public void setShowcaseView(final ArrayList<TargetView> viewList){
		
			
			viewList.get(0).getView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
			{
				
				@SuppressWarnings("deprecation")
				@SuppressLint("NewApi")
				@Override
				public void onGlobalLayout() 
				{
					
					 if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						 viewList.get(0).getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
			            } else {
			            	viewList.get(0).getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
			            }
					
					for(int i=0; i<viewList.size();i++){
						imageview= new ImageView(mContext);
						textview= new CustomTextViewLight(mContext);
						imageview.setId(i+baseId);
						textview.setId(i+baseId2);
						
						textview.setTextColor(Color.parseColor("#ffffff"));
						imageLayoutParams=new LayoutParams(
					            LayoutParams.WRAP_CONTENT,
					            LayoutParams.WRAP_CONTENT);
					
						textLayoutParams =new LayoutParams(
					            LayoutParams.WRAP_CONTENT,
					            LayoutParams.WRAP_CONTENT);
						
						final TargetView targetView= viewList.get(i);
				
						int[] location = new int[2];
				        targetView.getView().getLocationInWindow(location);
				        mLeftMargin = location[0] + targetView.getView().getWidth() / 2;
				        mTopMargin = location[1] + targetView.getView().getHeight() / 2;
				       
				        textview.setText(targetView.getMessage());
				
					
					if(targetView.getShowcase_type()==ShowCaseType.Left)
					{
						 imageview.setImageResource(R.drawable.fig_left);
						// left side image is used
						if(targetView.getView() instanceof GridView || targetView.getView() instanceof ListView)
						{
							// given view is instance of grid view or list view
							View innerView=((AbsListView)targetView.getView()).getChildAt(0);
							int[] innerLocation = new int[2];
					        innerView.getLocationInWindow(innerLocation);
					        mLeftMargin = innerLocation[0] + innerView.getWidth() / 2;
					        mTopMargin = innerLocation[1] + innerView.getHeight() / 2;
							
							imageLayoutParams.leftMargin=mLeftMargin;
							imageLayoutParams.topMargin=mTopMargin;
							textLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, imageview.getId());
						}
						
						else{
							// given view is not action bar icon
							imageLayoutParams.leftMargin=mLeftMargin;
							imageLayoutParams.topMargin=mTopMargin;
							textLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, imageview.getId());
							
						}
						
						if(targetView.getView() instanceof RelativeLayout){
							imageLayoutParams.leftMargin=mLeftMargin-targetView.getView().getWidth()/3;
						}
						
					}
					else if(targetView.getShowcase_type()==ShowCaseType.Right){
						
						imageview.setImageResource(R.drawable.fig_right);
						if(targetView.getView() instanceof GridView || targetView.getView() instanceof ListView){
							
							View innerView=((AbsListView)targetView.getView()).getChildAt(0);
							imageLayoutParams.leftMargin=(int) (innerView.getX()+targetView.getView().getWidth()/3);
							imageLayoutParams.topMargin=(int) (innerView.getY()+innerView.getHeight());
							textLayoutParams.addRule(RelativeLayout.ALIGN_RIGHT, imageview.getId());
						}else{
							
								imageLayoutParams.leftMargin= mLeftMargin-50;
								imageLayoutParams.topMargin=  mTopMargin-10;
								textLayoutParams.addRule(RelativeLayout.ALIGN_RIGHT, imageview.getId());
						}	
					}
					
					else if(targetView.getShowcase_type()==ShowCaseType.SwipeUpDown){
						
						textview.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up_arrow, 0, R.drawable.down_arrow);
						textview.setGravity(Gravity.CENTER_HORIZONTAL);
						textLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
						
						if(targetView.getView() instanceof ListView || targetView.getView() instanceof GridView){
							View innerView=((AbsListView)targetView.getView()).getChildAt(0);
							int[] innerLocation = new int[2];
					        innerView.getLocationInWindow(innerLocation);
					        mLeftMargin = innerLocation[0] + innerView.getWidth() / 2;
					        mTopMargin = innerLocation[1] + innerView.getHeight() / 2;
					        
					        textLayoutParams.topMargin= mTopMargin;
						}else{
							
							textLayoutParams.topMargin=mTopMargin;
						}
					}
					else if(targetView.getShowcase_type()==ShowCaseType.SwipeLeft){
						
						imageview.setImageResource(R.drawable.swipe_left);
						imageLayoutParams.leftMargin= mLeftMargin;
						imageLayoutParams.topMargin=  mTopMargin;
						textLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, imageview.getId());
						if(targetView.getView() instanceof ViewPager){
							imageLayoutParams.topMargin=  mTopMargin-targetView.getView().getHeight()/2;
						}
						
					}
					
					else if(targetView.getShowcase_type()==ShowCaseType.SwipeRight){
						
						imageview.setImageResource(R.drawable.swipe_right);
						imageLayoutParams.leftMargin= mLeftMargin;
						imageLayoutParams.topMargin=  mTopMargin;
						textLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, imageview.getId());
						
					}
					textLayoutParams.addRule(RelativeLayout.BELOW,imageview.getId());
					
					textLayoutParams.leftMargin=10;
					textLayoutParams.rightMargin=10;
					
					imageview.setLayoutParams(imageLayoutParams);
					textview.setLayoutParams(textLayoutParams);
					
					Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/OpenSans-Semibold.ttf");
					textview.setTypeface(tf);
					
					ShowcaseLayout.this.addView(imageview);
					ShowcaseLayout.this.addView(textview);
				}
					
					
			
		}
				
	});
		
	}

}
