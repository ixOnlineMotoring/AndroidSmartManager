package com.nw.interfaces;

import com.nw.model.MyImage;

import java.util.ArrayList;

public interface AuditImageClickListener 
{

	public void onImageClick(int itemPosition, ArrayList<MyImage> images, int listPosition);
}
