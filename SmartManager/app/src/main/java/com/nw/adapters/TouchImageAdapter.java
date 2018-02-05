package com.nw.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nw.model.BaseImage;
import com.nw.model.MyImage;
import com.nw.widget.TouchImageView;
import com.smartmanager.android.R;
import com.utils.Constants;

import java.util.ArrayList;

public class TouchImageAdapter  extends PagerAdapter 
{
	Context mContext;
	ArrayList<BaseImage> mList;
	ArrayList<String> urlList;
	ImageLoader loader;
	private DisplayImageOptions options;
	public TouchImageAdapter(Context c, ArrayList<BaseImage> list) {
		mContext=c;
		mList=list;
		loader= ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(Color.TRANSPARENT)
		.showImageOnFail(R.drawable.noimage)
		.showImageForEmptyUri(R.drawable.noimage)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.build();
	}
	public TouchImageAdapter(Context c, ArrayList<String> list, boolean flag){
		mContext=c;
		urlList=list;
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
	public int getCount() 
	{
		if(mList!=null)
			return mList.size();
		else if(urlList!=null)
			return urlList.size();
		else return 0;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) 
	{
		View view =  View.inflate(mContext, R.layout.custom_image_view, null);
		final TouchImageView touchImageView= (TouchImageView) view.findViewById(R.id.touchImageView);
		final ProgressBar pBar= (ProgressBar) view.findViewById(R.id.progressBar1);
		pBar.setVisibility(View.VISIBLE);
		
		SimpleImageLoadingListener listener= new SimpleImageLoadingListener(){
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				pBar.setVisibility(View.GONE);
				touchImageView.setVisibility(View.VISIBLE);
				touchImageView.setImageBitmap(loadedImage);
			}
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				touchImageView.setVisibility(View.GONE);
			}
		};
		if(mList!=null){
			if(mList.get(position) instanceof MyImage){
				MyImage myImage= (MyImage) mList.get(position);
				loader.loadImage(myImage.getFull(), options,listener);
			}
			else
			{
				if(mList.get(position).isLocal())
					loader.displayImage("File://"+mList.get(position).getPath(), touchImageView, options,listener);
				else
                {
                    if(mList.get(position).getLink().contains("http://"))
                    {
                        loader.displayImage(mList.get(position).getLink(), touchImageView, options,listener);
                    }else
                    {
                        loader.displayImage(Constants.IMAGE_BASE_URL + mList.get(position).getLink(), touchImageView, options,listener);
                    }
                }
			}
		
		}else if(urlList!=null){
			loader.loadImage(urlList.get(position), options,listener);
		}
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) 
	{
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}
