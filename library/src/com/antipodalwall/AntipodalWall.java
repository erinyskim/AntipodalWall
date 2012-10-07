package com.antipodalwall;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


public class AntipodalWall extends ViewGroup {
	
	private int columns;
	private float columnWidth = 0;
	private int paddingL = 0;
	private int paddingT = 0;
	private int paddingR = 0;
	private int paddingB = 0;

	public AntipodalWall(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//Load the attrs from the XML
		final TypedArray customAttrsArray = context.obtainStyledAttributes(attrs, R.styleable.AntipodalWallAttrs);
		//- number of columns
		columns = customAttrsArray.getInt(R.styleable.AntipodalWallAttrs_columns, 1);
        if(columns < 1)
        	columns = 1;
        //- general padding (padding was not being handled correctly)
        setGeneralPadding(customAttrsArray.getDimensionPixelSize(R.styleable.AntipodalWallAttrs_android_padding, 0));
        //- specific paddings
        paddingL = customAttrsArray.getDimensionPixelSize(R.styleable.AntipodalWallAttrs_android_paddingLeft, paddingL);
        paddingT = customAttrsArray.getDimensionPixelSize(R.styleable.AntipodalWallAttrs_android_paddingTop, paddingT);
        paddingR = customAttrsArray.getDimensionPixelSize(R.styleable.AntipodalWallAttrs_android_paddingRight, paddingR);
        paddingB = customAttrsArray.getDimensionPixelSize(R.styleable.AntipodalWallAttrs_android_paddingBottom, paddingB);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentUsableWidth = parentWidth - paddingL - paddingR; //Usable width for children once padding is removed
		if(parentUsableWidth < 0)
			parentUsableWidth = 0;
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		int parentUsableHeight = parentHeight - paddingT - paddingB; //Usable height for children once padding is removed
		if(parentUsableHeight < 0)
			parentUsableHeight = 0;
		columnWidth = parentUsableWidth / columns;
		
		for(int i=0;i<getChildCount();i++) {
			View child = getChildAt(i);
			 //force the width of the children to be that of the columns...
			int childWidthSpec = MeasureSpec.makeMeasureSpec((int)columnWidth, MeasureSpec.EXACTLY);
			 //... but let them grow vertically
			int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			child.measure(childWidthSpec, childHeightSpec);
		}
		
		setMeasuredDimension(parentWidth, parentHeight);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int[] columns_t = new int[columns];
		
		for(int i=0;i<getChildCount();i++) {
			View view = getChildAt(i);
			//We place each child  in the column that has the less height to the moment
			int column = findLowerColumn(columns_t);
			int left = paddingL + l + (int)(columnWidth * column);
			view.layout(left, columns_t[column] + paddingT, left + view.getMeasuredWidth(), columns_t[column] + view.getMeasuredHeight() + paddingT);
			columns_t[column] = columns_t[column] + view.getMeasuredHeight();
		}
	}
	
	private int findLowerColumn(int[] columns) {
		int minValue = columns[0];
		int column = 0;
		for(int i=1;i<columns.length;i++){  
			if(columns[i] < minValue){  
				minValue = columns[i];
				column = i;
			}  
		}  
		return column;  
	}
	
	private void setGeneralPadding(int padding) {
		paddingL = padding;
		paddingT = padding;
		paddingR = padding;
		paddingB = padding;
	}
}