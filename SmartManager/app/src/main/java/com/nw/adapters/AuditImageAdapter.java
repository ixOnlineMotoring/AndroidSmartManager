package com.nw.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nw.model.MyImage;
import com.smartmanager.android.R;

import java.util.ArrayList;

public class AuditImageAdapter extends ArrayAdapter<MyImage>{

	ImageLoader loader;
	private DisplayImageOptions options;
	public AuditImageAdapter(Context c,ArrayList<MyImage> list){
		
		super(c, R.layout.item_image, list);
		loader= ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(Color.TRANSPARENT)
		.showImageOnFail(R.drawable.noimage)
		.showImageForEmptyUri(R.drawable.noimage)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.build();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null)
		{
			convertView= LayoutInflater.from(getContext()).inflate(R.layout.item_image, null);
		}
		ImageView iv= (ImageView) convertView.findViewById(R.id.ivItemImage);
		loader.displayImage(getItem(position).getThumb(), iv, options);
		
		return convertView;
	}

}