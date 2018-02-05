package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nw.adapters.GridImageAdapter.AddPhotoListener;
import com.nw.model.BaseImage;
import com.smartmanager.android.R;
import com.smartmanager.android.live.SmartManagerApplication;
import com.utils.Constants;
import com.utils.Helper;

import java.util.List;

public class ListImageAdapter extends ArrayAdapter<BaseImage>
{
	AddPhotoListener addPhotoListener;
	DisplayImageOptions options;
	
	int selected_position=-1;
	
	
	public ListImageAdapter(Context context, int resource, List<BaseImage> objects)
	{
		super(context, resource, objects);		
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		final BaseImage gridImage = (BaseImage) getItem(position);
		if (gridImage.getType() != -1) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_image, parent,false);
			holder = new ViewHolder(convertView);			
			holder.build((BaseImage) getItem(position));
			
		} else {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_blog_default, parent,false);
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

	private class ViewHolder {
		private ImageView image;

		private ViewHolder(View view) 
		{
			image = (ImageView) view.findViewById(R.id.ivPicture);
		}

		void build(BaseImage gridImage) 
		{
			if (gridImage.isLocal()) 
			{
				SmartManagerApplication.getImageLoader().displayImage("file://"+gridImage.getPath(), image, options);
				
			} else {

				if (gridImage.getThumbPath() == null ||gridImage.getThumbPath().equals("")){
					SmartManagerApplication.getImageLoader().displayImage(gridImage.getLink(), image, options);
					Helper.Log("Link", gridImage.getLink());
				}
				else{
					SmartManagerApplication.getImageLoader().displayImage(Constants.IMAGE_BASE_URL+ gridImage.getThumbPath(), image,options);
					Helper.Log("thumbpath", gridImage.getThumbPath());
				}
			}
		}
	}
	
	public void setSelected(int selected)
	{
	}
	public int getSelected()
	{
		return selected_position;
	}
	public void setAddPhotoListener(AddPhotoListener addPhotoListener) {
		this.addPhotoListener = addPhotoListener;
	}
}
