package com.mzs.guaji.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class GuaJiGridView extends GridView {

	public GuaJiGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GuaJiGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GuaJiGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		 int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
	                MeasureSpec.AT_MOST);  
	     super.onMeasure(widthMeasureSpec, expandSpec);  
//		 super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}