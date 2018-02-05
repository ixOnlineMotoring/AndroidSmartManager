package com.nw.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.nw.adapters.GridVideoAdapter;
import com.nw.adapters.GridVideoAdapter.AddPhotoListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.model.BaseImage;
import com.smartmanager.activity.VideoCaptureActivity;
import com.smartmanager.android.R;
import com.utils.Helper;

import org.askerov.dynamicgrid.DynamicGridView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DragableVideoGridView implements AddPhotoListener
{
	DynamicGridView gridView;
	RelativeLayout rlImages;

	private static final int PICK_FROM_CAMERA = 1888;
	private static final int PICK_FROM_GALLERY = 1891;
	Uri fileUri, mImageCaptureUri;

	ArrayList<BaseImage> deletedImages;
	ArrayList<BaseImage> gridImages;
	GridVideoAdapter adapter;
	ImageClickListener mListener;
	/**
	 * Image position changed in Gridview
	 */
	boolean priorityChanged = false;
	public boolean imageAdded = false;
	/**
	 * Image Deleted
	 */
	boolean imageDeleted = false;

	boolean isEnabled = true;
	boolean video = false;
	boolean showPlusIcon=true;

	boolean optionSelected = false;

	/*public DragableVideoGridView() {
		if (deletedImages == null)
			deletedImages = new ArrayList<BaseImage>();
	}*/

	public DragableVideoGridView(boolean isvideoGrid) {
		video = true;
		if (deletedImages == null)
			deletedImages = new ArrayList<BaseImage>();
	}

	public ArrayList<BaseImage> getImages() {
		return gridImages;
	}

	public ArrayList<BaseImage> getDeletedImages() {
		return deletedImages;
	}

	public ArrayList<BaseImage> getUpdatedImageList() {
		ArrayList<BaseImage> list = new ArrayList<BaseImage>();
		for (int i = 0; i < adapter.getCount(); i++) {
			list.add((BaseImage) adapter.getItem(i));
		}
		return list;
	}

	public void setImageList(ArrayList<BaseImage> list) {
		gridImages.clear();
		gridImages.addAll(list);

		imageAdded = false;
		priorityChanged = false;
		imageDeleted = false;
		isEnabled = true;
		video = false;
		deletedImages.clear();
		
		if(showPlusIcon)
			addPlusItem();

		addPlusItem();
		adapter = new GridVideoAdapter(gridView.getContext(), gridImages, 3);
		adapter.setAddPhotoListener(this);
		gridView.setAdapter(adapter);
		gridView.smoothScrollToPosition(adapter.getCount() - 1);
	}

	public void init(View view,ImageClickListener listener) {
		//	gridView = (DynamicGridView) view.findViewById(R.id.gvVideos);
			rlImages = (RelativeLayout) view.findViewById(R.id.rlVideo);
		
			mListener= listener;
		gridView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gridView.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View position,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(gridImages.get(arg2).getType()!=-1)
					mListener.onImageClick(arg2);
				gridView.stopEditMode();
				addPlusItem();
			}

		});
		

		gridView.setOnDropListener(new DynamicGridView.OnDropListener() {
			@Override
			public void onActionDrop() {
				priorityChanged = true;
				// stop edit mode immediately after drop item
				gridView.stopEditMode();
			}
		});

		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				removeLastItem();
				gridView.startEditMode(position);
				return true;
			}

		});
		
		showPlusIcon=true;

		if (gridImages == null) {
			gridImages = new ArrayList<BaseImage>();
			addPlusItem();
		} else {
			adapter = new GridVideoAdapter(gridView.getContext(), gridImages, 3);
			adapter.setAddPhotoListener(this);
			gridView.setAdapter(adapter);
			gridView.smoothScrollToPosition(adapter.getCount() - 1);
		}

		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gridView.stopEditMode();
				addPlusItem();

				return false;
			}
		});

	}

	public boolean isOptionSelected() {
		return optionSelected;
	}

	public void setOptionSelected(boolean optionSelected) {
		this.optionSelected = optionSelected;
	}

	private void addPlusItem() {
		if (isEnabled) {
			isEnabled = false;
			if (adapter == null) {
				gridImages.add(new BaseImage());
				adapter = new GridVideoAdapter(gridView.getContext(),gridImages, 3);
				adapter.setAddPhotoListener(new AddPhotoListener() {

					@Override
					public void onAddOptionSelected()
					{
						getVideoFromGalleryOrCamera();
					}

					@Override
					public void onRemoveOptionSelected(int position) {
						// TODO Auto-generated method stub
						// removeLastItem();
						removeItem(position);
					}
				});
				gridView.setAdapter(adapter);
			} else {
				BaseImage imBlog = new BaseImage();
				gridImages.add(imBlog);
				adapter.add(imBlog);
				adapter.notifyDataSetChanged();
			}
			gridView.smoothScrollToPosition(adapter.getCount() - 1);
		}
	}

	private void removeLastItem() {
		if (isEnabled == false) {
			isEnabled = true;
			int count = (gridImages.size() - 1);
			adapter.getCount();
			adapter.remove(adapter.getItem(count));
			gridImages.remove(count);

			if (gridImages.isEmpty()) {
				gridView.stopEditMode();
				addPlusItem();
			} else
				adapter.notifyDataSetChanged();
		}
	}

	private void removeItem(int position) {
		// check which image change
		// if last no need to update priority
		if (position == gridImages.size())
			priorityChanged = false;
		else
			priorityChanged = true;

		if (gridImages.get(position).getType() != -1) {
			deletedImages.add(gridImages.get(position));
			imageDeleted = true;
		}
		gridImages.remove(position);
		if (gridImages.isEmpty()) {
			gridView.stopEditMode();
			addPlusItem();
		} else
			adapter.notifyDataSetChanged();

	}

	public void updatePriority() {
		for (int i = 0; i < gridImages.size(); i++) {
			gridImages.get(i).setPriority(i + 1);
		}
	}

	@Override
	public void onAddOptionSelected() {
		getVideoFromGalleryOrCamera();
	}

	@Override
	public void onRemoveOptionSelected(int position)
	{
		removeItem(position);
		boolean isLocalImage = false;
		if(gridImages.get(position).isLocal())
			isLocalImage=true;
			
		removeItem(position);		
		if(isLocalImage)
		{
			if(showPlusIcon==false)
			{
				showPlusIcon=true;
				addPlusItem();
			}
		}
		if(mListener!=null)
			   mListener.onImageDeleted(position);
	}

	
	public void getVideoFromGalleryOrCamera() {
		optionSelected = false;
		final String[] items = new String[] { "Record from Camera",
				"Select from Gallery" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				gridView.getContext(), android.R.layout.select_dialog_item,
				items);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				gridView.getContext());
		builder.setTitle("Select the Video");
		builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				optionSelected = true;
				if (which == 0) {
					Intent intent = new Intent(gridView.getContext(),VideoCaptureActivity.class);
					/*fileUri = getOutputMediaFileUri();
					intent.putExtra("VIDEO_LOCATION", fileUri);*/
					// start the image capture Intent
					((FragmentActivity) gridView.getContext()).startActivityForResult(intent, PICK_FROM_CAMERA);
				} else {
					Intent pickVideo = new Intent(Intent.ACTION_PICK);
					pickVideo.setType("video/*");
					((FragmentActivity) gridView.getContext()).startActivityForResult(pickVideo, PICK_FROM_GALLERY);// one
				}

			}
		});
		final AlertDialog ad = builder.create();
		ad.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				optionSelected = false;
			}
		});
		ad.show();
	}
	
	
	

	public Uri getOutputMediaFileUri() {
		return Uri.fromFile(getOutputMediaFile());
	}

	private File getOutputMediaFile() {
		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),	gridView.getContext().getString(R.string.app_name));
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) 
		{
			if (!mediaStorageDir.mkdirs()) 
			{
				Helper.Log("Cropper", "Oops! Failed create " + "VideoDirectory"+ " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
		File mediaFile= new File(mediaStorageDir.getPath() + File.separator	+ "Video" + timeStamp + ".mp4");
		return mediaFile;
	}

	 String[] filePathColumn = { MediaStore.Video.Media.DATA };
	 String filePath="";
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			optionSelected = false;
			return;
		}
		if (optionSelected) 
		{
			switch (requestCode) 
			{
			case PICK_FROM_CAMERA:
				filePath=fileUri.getPath();
				Bitmap  bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
				saveBitmap(bmThumbnail, filePath);
				
				BaseImage gridImage = new BaseImage();
				gridImage.setType(1);
				gridImage.setPath(filePath);
				gridImage.setThumbPath(getVideoThumbFile(getVideoThumbFileName(filePath)).getPath());
				gridImage.setLocal(true);
				gridImage.setPriority(adapter.getCount());
				
				if (gridImages.size() == 1)
					gridImages.add(0, gridImage);
				else if (gridImages.size() >= 2)
					gridImages.add(gridImages.size() - 1, gridImage);
				if (adapter == null)
				{
					adapter = new GridVideoAdapter(gridView.getContext(),gridImages, 3);
					adapter.setAddPhotoListener(this);
					gridView.setAdapter(adapter);
				} else {
					adapter.add(adapter.getCount() - 1, gridImage);
					imageAdded = true;
					adapter.notifyDataSetChanged();
				}
				gridView.smoothScrollToPosition(adapter.getCount() - 1);
				optionSelected = false;
				
				
				break;
			case PICK_FROM_GALLERY:
				 Uri videoUri = data.getData();
				 Cursor cursor = gridView.getContext().getContentResolver().query(videoUri,filePathColumn, null, null, null);
				 if(cursor!=null)
				 {
					 cursor.moveToFirst();
					 int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					 filePath = cursor.getString(columnIndex);   
				 }
				bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
				saveBitmap(bmThumbnail, filePath);
				
				gridImage = new BaseImage();
				gridImage.setType(1);
				gridImage.setPath(filePath);
				gridImage.setThumbPath(getVideoThumbFile(getVideoThumbFileName(filePath)).getPath());
				gridImage.setLocal(true);
				gridImage.setPriority(adapter.getCount());
				
				if (gridImages.size() == 1)
					gridImages.add(0, gridImage);
				else if (gridImages.size() >= 2)
					gridImages.add(gridImages.size() - 1, gridImage);
				if (adapter == null)
				{
					adapter = new GridVideoAdapter(gridView.getContext(),gridImages, 3);
					adapter.setAddPhotoListener(this);
					gridView.setAdapter(adapter);
				} else {
					adapter.add(adapter.getCount() - 1, gridImage);
					imageAdded = true;
					adapter.notifyDataSetChanged();
				}
				gridView.smoothScrollToPosition(adapter.getCount() - 1);
				optionSelected = false;
				break;
			
			}
		}
	}

	public boolean isPriorityChanged() {
		return priorityChanged;
	}

	public boolean isImageDeleted() {
		return imageDeleted;
	}

	public GridVideoAdapter getAdapter() {
		return adapter;
	}
	
	
	private void createVideoThumbFolder()
	{
		File file=new File(gridView.getContext().getCacheDir(),"VideoThumb");
		if(!file.exists())
			file.mkdirs();
	}
	
	private File getVideoThumbFile(String fileName)
	{
		return new File(gridView.getContext().getCacheDir()+File.separator+"VideoThumb",fileName+".Jpeg");
	}
	
	private String getVideoThumbFileName(String filePath)
	{
		String fileName="";
		if(!TextUtils.isEmpty(filePath))
			fileName=filePath.substring(filePath.lastIndexOf("/")+1, filePath.lastIndexOf("."));
		return fileName;	
	}
	
	private void saveBitmap(Bitmap bitmap,String filePath)
	{
		try 
		{
			createVideoThumbFolder();
			File file=getVideoThumbFile(getVideoThumbFileName(filePath));
			if(file.exists())
				file.delete();
			FileOutputStream fOut = new FileOutputStream(file);
			if(bitmap!=null)
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
