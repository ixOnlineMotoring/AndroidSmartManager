package com.nw.adapters;

/**
 * Author: alex askerov
 * Date: 9/9/13
 * Time: 10:52 PM
 */

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nw.model.BaseImage;
import com.smartmanager.android.R;
import com.smartmanager.android.live.SmartManagerApplication;
import com.utils.Constants;
import com.utils.Helper;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: alex askerov Date: 9/7/13 Time: 10:56 PM
 */
public class GridVideoAdapter extends BaseDynamicGridAdapter {
	AddPhotoListener addPhotoListener;
	DisplayImageOptions options;
	
	ArrayList<BaseImage> mList;
	@SuppressWarnings("deprecation")
	public GridVideoAdapter(Context context, List<BaseImage> items,
			int columnCount) {
		super(context, items, columnCount);
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.resetViewBeforeLoading(false)
				
				.displayer(new SimpleBitmapDisplayer()).build();
		mList = (ArrayList<BaseImage>) items;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		CheeseViewHolder holder;
		final BaseImage gridImage = (BaseImage) getItem(position);
		if (gridImage.getType() != -1) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_video, null);
			holder = new CheeseViewHolder(convertView);
			holder.build((BaseImage) getItem(position));
			holder.close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					remove(gridImage);
					if (addPhotoListener != null)
						addPhotoListener.onRemoveOptionSelected(position);
					notifyDataSetChanged();
				}
			});
		} else {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_blog_default, null);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//
					if (addPhotoListener != null)
						addPhotoListener.onAddOptionSelected();
				}
			});
		}
		return convertView;
	}

	private class CheeseViewHolder {
		private ImageView close;
		private ImageView image;

		private CheeseViewHolder(View view) {
			image = (ImageView) view.findViewById(R.id.ivPicture);
			close = (ImageView) view.findViewById(R.id.ivClose);
		}

		void build(BaseImage gridImage) {
			if (gridImage.isLocal()) 
			{
				image.setImageBitmap(BitmapFactory.decodeFile(gridImage.getThumbPath()));
			} 
			else 
			{
				if (gridImage.getThumbPath() == null ||gridImage.getThumbPath().equals("")){
					SmartManagerApplication.getImageLoader().displayImage(gridImage.getLink(), image, options);
					Helper.Log("Link", gridImage.getLink());
				}
				else{
					SmartManagerApplication.getImageLoader().displayImage(Constants.IMAGE_BASE_URL+ gridImage.getThumbPath(), image,options);
				}
				
			}
		}
	}

	public void setAddPhotoListener(AddPhotoListener addPhotoListener) {
		this.addPhotoListener = addPhotoListener;
	}

	public interface AddPhotoListener {
		public void onAddOptionSelected();

		public void onRemoveOptionSelected(int postition);
	}

	
}
