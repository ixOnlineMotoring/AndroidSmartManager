package com.nw.widget;




import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.smartmanager.android.R;


public class CustomTextView extends TextView 
{
	private Typeface tf;
	public  final String DEFAULT_FONT = "fonts/OpenSans-Semibold.ttf";
	public CustomTextView(Context context) 
	{
		super(context);
		if (!isInEditMode())
			init(null);
	}
	public CustomTextView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		if (!isInEditMode())
			init(attrs);
	}
	
	public CustomTextView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		if (!isInEditMode())
			init(attrs);
	}

	private void init(AttributeSet attrs) 
	{
		if(attrs!=null)
		{
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
			String fontName = a.getString(R.styleable.CustomTextView_font);
				if (fontName != null)
					tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/"+fontName+".ttf");
				else
					tf = Typeface.createFromAsset(getContext().getAssets(),DEFAULT_FONT);  //default font for app
				
			a.recycle();
			if(tf!=null)
				setTypeface(tf);
		}
	}
}
