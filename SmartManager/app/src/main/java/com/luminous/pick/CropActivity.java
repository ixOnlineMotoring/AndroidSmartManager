package com.luminous.pick;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.nw.adapters.GridImageAdapter.AddPhotoListener;
import com.nw.adapters.ListImageAdapter;
import com.nw.model.BaseImage;
import com.nw.widget.HorizontalListView;
import com.smartmanager.android.R;

import java.util.ArrayList;

public class CropActivity extends Activity implements AddPhotoListener
{
	ImageView imageView;
	HorizontalListView horizontalListView;
	EditText edtCaption;
	ListImageAdapter adapter;
	ArrayList<BaseImage>images;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop);
		imageView=(ImageView) findViewById(R.id.imageView1);
		edtCaption=(EditText) findViewById(R.id.edtCaption);
		horizontalListView=(HorizontalListView) findViewById(R.id.horizontallistview);
		shoData();
	}
	
	
	private void shoData()
	{
		images=new ArrayList<BaseImage>();
		if(getIntent().getExtras().containsKey("multiple"))
		{
			// show image to list
			String[] all_path = getIntent().getStringArrayExtra("all_path");
			//ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();			
			int priority=0;
			for (String string : all_path) 
			{
				//CustomGallery item = new CustomGallery();
				//item.sdcardPath = string;
				//dataT.add(item);			
				BaseImage gridImage = new BaseImage();
				gridImage.setType(1);
				gridImage.setPath(string);
				gridImage.setLocal(true);				
				gridImage.setPriority(priority);
				images.add(gridImage);
				priority++;
			}			
			if (adapter == null)
			{
				adapter = new ListImageAdapter(this,R.layout.list_item_image,images);
				adapter.setAddPhotoListener(this);
				horizontalListView.setAdapter(adapter);
			}			
			//adapter.addAll(dataT);
		}
	}


	@Override
	public void onAddOptionSelected()
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onRemoveOptionSelected(int postition)
	{
		// TODO Auto-generated method stub
		
	}
}
