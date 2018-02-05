package com.smartmanager.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.edmodo.cropper.CropImageView;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCropActivity extends BaseActivity implements	OnClickListener 
{
	CropImageView civ;
	Button btnSave, btnDelete;
	File file = null;	
	String fileName;
	String filePath;
	
	public static final int Height=600;
	public static final int Width=1200;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		try{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_image_crop);
				civ = (CropImageView) findViewById(R.id.CropImageView);					
				if (getIntent().getBooleanExtra("isCamera", true)) 
				{			
					int[]arr=getImageDetails(getIntent().getStringExtra("filepath"));			
					// height and width minimum
					if(arr[0]>=200) // height
					{
						if(arr[1]>=400) //width
						{
							previewCapturedImage(getIntent().getStringExtra("filepath"));
						}					
						else
						{
							Helper.showToast("Width is too small", this);
							finish();
						}
					}
					else
					{
						Helper.showToast("Height is too small", this);
						finish();
					}
				} 
				else 
				{
					previewPickedImage(getIntent().getStringExtra("filepath"));
				}
				btnSave = (Button) findViewById(R.id.btnSave);
				btnDelete = (Button) findViewById(R.id.btnDelete);
				btnSave.setOnClickListener(this);
				btnDelete.setOnClickListener(this);
				civ.setAspectRatio(81, 57);
				civ.setFixedAspectRatio(true);
				
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Display image from a path to ImageView
	 */
	private void previewCapturedImage(String path) 
	{
		try 
		{
			ExifInterface ei = null;
			try 
			{
				ei = new ExifInterface(path);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			filePath=path;
			int index = path.lastIndexOf("/");
			fileName = path.substring(index + 1);
			civ.setImageBitmap(decodeScaledBitmapFromSdCard(path, Width, Height), ei);
		}		
		catch (OutOfMemoryError e) 
		{
			e.printStackTrace();
			Helper.showToast("Failed to load image data,Please try again later", this);
		}
		catch (NullPointerException e) 
		{
			e.printStackTrace();
			Helper.showToast("Failed to load image data,Please try again later", this);
		}catch (Exception e) {
			e.printStackTrace();
			Helper.showToast("Failed to load image data,Please try again later", this);
		}
	}
	
	
	/*private void setPic(String path) 
	{

		int index = path.lastIndexOf("/");
		fileName = path.substring(index + 1);		
		 There isn't enough memory to open up more than a couple camera photos 
		 So pre-scale the target bitmap into which the file is decoded 
		 Get the size of the ImageView 
		int targetW = civ.getWidth();
		int targetH = civ.getHeight();

		 Get the size of the image 
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		 Figure out which way needs to be reduced less 
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) 
		{
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		 Set bitmap options to scale the image decode target 
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		 Decode the JPEG file into a Bitmap 
		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);		
		 Associate the Bitmap to the ImageView 
		civ.setImageBitmap(bitmap);
	}
	*/

	private void previewPickedImage(String path) 
	{
		try
		{
			String[] filePathColumn = { MediaColumns.DATA };
			Cursor cursor = getContentResolver().query(Uri.parse(path),	filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();

			ExifInterface ei = null;
			try
			{
				ei = new ExifInterface(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			int index = filePath.lastIndexOf("/");
			fileName = filePath.substring(index + 1);
			
			int[]arr=getImageDetails(filePath);		
			// height and width minimum
			if(arr[0]>=200) // height
			{
				if(arr[1]>=400) //width
				{
					civ.setImageBitmap(decodeScaledBitmapFromSdCard(filePath, Width, Height),ei);				
					//setPic(filePath);
				}
				else
				{
					Helper.showToast("Width is too small", this);
					finish();
				}
			}
			else
			{
				Helper.showToast("Height is too small", this);
				finish();
			}
		}
		catch (OutOfMemoryError e) 
		{
			e.printStackTrace();
			Helper.showToast("Failed to load image data,Please try again later", this);
		}
		catch (NullPointerException e) 
		{
			e.printStackTrace();
			Helper.showToast("Failed to load image data,Please try again later", this);
		}catch (Exception e) {
			e.printStackTrace();
			Helper.showToast("Failed to load image data,Please try again later", this);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId()) 
		{
		case R.id.btnSave:
			//imageLoader.loadImageSync(imageUri, targetSize, displayOptions);			
			Bitmap croppedImage = civ.getCroppedImage();			
			/*SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",Locale.getDefault());
			Date now = new Date();
			String fileName = formatter.format(now) + ".jpeg";*/	
			
			
			file = new File(filePath);		
			if (file.exists()) 
				file.delete();		
			
			writeExternalToCache(croppedImage, file);
			galleryAddPic();	
			
			double bytes = file.length();
			double kilobytes = (bytes / 1024);
			if(kilobytes>350)
				Helper.showToast("Image larger than 350Kb", this);
			else				
			{
				setResult(RESULT_OK, new Intent().putExtra("filepath", file.getPath()));
				finish();
			}			
			break;

		case R.id.btnDelete:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
	}
	
	private void galleryAddPic() 
	{
		
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {*/
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
       /* } else {
         sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }*/
		
		
		/*getContentResolver().notifyChange(Uri.parse("file://" + file.getPath()),null);
		
		    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		   // File f = new File(mCurrentPhotoPath);
		    Uri contentUri = Uri.fromFile(file);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);*/
		
		//sendBroadcast(new Intent(Intent.METADATA_DOCK_HOME, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
		/*SingleMediaScanner mediaScanner=new SingleMediaScanner(this, file);
		mediaScanner.*/
	}

	public static Bitmap decodeScaledBitmapFromSdCard(String filePath,int reqWidth, int reqHeight) throws OutOfMemoryError
	{
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,	reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.outWidth=reqWidth;
		options.outHeight=reqHeight;		
		return BitmapFactory.decodeFile(filePath, options);
	}
	

	public static final int BUFFER_SIZE = 1024 * 8;

	void writeExternalToCache(Bitmap bitmap, File file)
	{
		try 
		{			
			FileOutputStream fos = new FileOutputStream(file);
			final BufferedOutputStream bos = new BufferedOutputStream(fos,BUFFER_SIZE);
			bitmap.compress(CompressFormat.JPEG, 95, bos);
			bos.flush();
			bos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

		}
	}
	

	public Bitmap compressImage(Bitmap bitmap) 
	{
		
		Bitmap scaledBitmap = null;			
		int actualHeight = bitmap.getHeight();
		int actualWidth = bitmap.getWidth();
		float maxHeight = 600.0f;
		float maxWidth = 1200.0f;
		
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		if (actualHeight > maxHeight || actualWidth > maxWidth) 
		{
			if (imgRatio < maxRatio)
			{
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			}
			else if (imgRatio > maxRatio) 
			{
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} 
			else 
			{
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;				
			}
		}		
		try
		{
			//scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, actualWidth, actualHeight, true);
		}
		catch(OutOfMemoryError exception)
		{
			exception.printStackTrace();
		}
						
		return scaledBitmap;
	}
	
	/*public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    final float totalPixels = width * height;
	    final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) 
        {
            inSampleSize++;
        }

	    return inSampleSize;
	}
	*/
	
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) 
	{
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) 
		{
			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height	/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	
	
	void writeExternalToCard(Bitmap bitmap, File file) {
		try 
		{
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			final BufferedOutputStream bos = new BufferedOutputStream(fos,	BUFFER_SIZE);
			Bitmap.createScaledBitmap(bitmap, 648, 452, true).compress(	CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

		}
	}

	
	private int[] getImageDetails(String path)
	{
		int[] result=new int[2];
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		result[0]= options.outHeight;
		result[1]= options.outWidth;
		Helper.Log("Dimenstion", "H"+result[0]+" W"+result[0]);
		return result;
	}
}
