
package com.smartmanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmanager.android.R;
import com.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoCaptureActivity extends Activity implements OnClickListener,
		SurfaceHolder.Callback
{

	public static final String LOGTAG = "VIDEOCAPTURE";
	private MediaRecorder recorder;
	private SurfaceHolder holder;
	private CamcorderProfile camcorderProfile;
	private Camera camera;
	ImageView ivRecord;
	boolean recording = false;
	boolean usecamera = true;
	boolean previewRunning = false;
	private long timeElapsed;
	private final long startTime = 2 * 60 * 1000; // countdown for 3 mins
	private final long interval = 1000;
	TextView tvCameraTimer;
	CountDownTimer countDownTimer;
	File newFile = null;
	int numberOfCameras=0;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
		setContentView(R.layout.activity_video_recording);
		SurfaceView cameraView = (SurfaceView) findViewById(R.id.CameraView);
		ivRecord = (ImageView) findViewById(R.id.ivRecord);
		tvCameraTimer = (TextView) findViewById(R.id.tvCameraTimer);
		countDownTimer = new ImageCountDownTimer(startTime, interval);
		ivRecord.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if (recording)
				{
					countDownTimer.cancel();
					try
					{
						ivRecord.setOnClickListener(null);
						try
						{
							recorder.stop();
						} catch (RuntimeException e)
						{
							e.printStackTrace();
						}
						recorder.release();
					} catch (IllegalStateException e1)
					{
						Intent returnIntent = new Intent();
						returnIntent.putExtra("result",newFile.getAbsolutePath());
						setResult(RESULT_OK, returnIntent);
						finish();
						e1.printStackTrace();
					}
					ivRecord.setImageResource(R.drawable.camera_button_grey);
					if (usecamera)
					{
						try
						{
							camera.reconnect();
						} catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					// recorder.release();
					recording = false;
					Log.v(LOGTAG, "Recording Stopped");
					Intent returnIntent = new Intent();
					returnIntent.putExtra("result", newFile.getAbsolutePath());
					setResult(RESULT_OK, returnIntent);
					finish();
					// Let's prepareRecorder so we can record again
					// prepareRecorder();
				} else
				{
					countDownTimer.start();
					ivRecord.setImageResource(R.drawable.camera_button_red);
					recording = true;
					try
					{
						recorder.start();
					} catch (IllegalStateException e)
					{
						Intent returnIntent = new Intent();
						returnIntent.putExtra("result",
								newFile.getAbsolutePath());
						setResult(RESULT_OK, returnIntent);
						finish();
						e.printStackTrace();
					}
					Log.v(LOGTAG, "Recording Started");
				}

			}
		});
		holder = cameraView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		cameraView.setClickable(true);
		cameraView.setOnClickListener(this);
	}

	public class ImageCountDownTimer extends CountDownTimer
	{
		public ImageCountDownTimer(long startTime, long interval)
		{

			super(startTime, interval);
		}

		@Override
		public void onFinish()
		{

			ivRecord.performClick();
		}

		@Override
		public void onTick(long millisUntilFinished)
		{

			DecimalFormat decimalFormat = new DecimalFormat("00");
			Log.v("Time remain", "Time remain:" + millisUntilFinished);
			Log.v("Time Elapsed: ",
					"Time Elapsed: " + String.valueOf(timeElapsed));
			int days = (int) ((millisUntilFinished / 1000) / 86400);
			int hours = (int) (((millisUntilFinished / 1000) - (days * 86400)) / 3600);
			int minutes = (int) (((millisUntilFinished / 1000) - ((days * 86400) + (hours * 3600))) / 60);
			int seconds = (int) ((millisUntilFinished / 1000) % 60);
			tvCameraTimer.setText("Time left: " + decimalFormat.format(minutes)
					+ ":" + decimalFormat.format(seconds));
			timeElapsed = startTime - millisUntilFinished;
		}
	}

	private void prepareRecorder()
	{

		recorder = new MediaRecorder();
		recorder.setPreviewDisplay(holder.getSurface());
		if (usecamera)
		{
			camera.unlock();
			recorder.setCamera(camera);
		}
		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		 if (this.getResources().getConfiguration().orientation !=Configuration.ORIENTATION_LANDSCAPE)
		 {
			 recorder.setOrientationHint(0);
		 }
		  else
		 {
			  recorder.setOrientationHint(0);
		 }
	//	int rotation = getPreviewOrientation(VideoCaptureActivity.this, getCamaraBackId());
	//	recorder.setOrientationHint(rotation);
	//	recorder.setOrientationHint(90);
		recorder.setProfile(camcorderProfile);

		// This is all very sloppy
		if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP)
		{
			newFile = getOutputMediaFile(".3gp");
			recorder.setOutputFile(newFile.getAbsolutePath());
			setResult(RESULT_OK);
		} else if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4)
		{
			newFile = getOutputMediaFile(".mp4");
			recorder.setOutputFile(newFile.getAbsolutePath());
		} else
		{
			newFile = getOutputMediaFile(".mp4");
			recorder.setOutputFile(newFile.getAbsolutePath());

		}
		recorder.setMaxDuration(120000); // 120 seconds
		recorder.setMaxFileSize(50000000L); // Approximately 5 megabytes
		
		try
		{
			recorder.prepare();
		} catch (IllegalStateException e)
		{
			e.printStackTrace();
			finish();
		} catch (IOException e)
		{
			e.printStackTrace();
			finish();
		}
	}

	public Uri getOutputMediaFileUri(String type)
	{

		return Uri.fromFile(getOutputMediaFile(type));
	}

	private File getOutputMediaFile(String type)
	{

		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				this.getString(R.string.app_name));
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists())
		{
			if (!mediaStorageDir.mkdirs())
			{
				Helper.Log("Cropper", "Oops! Failed create " + "VideoDirectory"
						+ " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "Video" + timeStamp + type);
		return mediaFile;
	}

	public void onClick(View v)
	{

	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.v(LOGTAG, "surfaceCreated");
		if (usecamera)
		{
			camera = Camera.open();
			Camera.Parameters params = camera.getParameters();
			if (this.getResources().getConfiguration().orientation!=Configuration.ORIENTATION_LANDSCAPE)
			{   
			  camera.setDisplayOrientation(90);
			}
			else
			{   
			  camera.setDisplayOrientation(0);                  
			}
			 params.setRotation(90);
	//		camera.setDisplayOrientation(90);
			try
			{
				camera.setPreviewDisplay(holder);
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				camera.startPreview();
				camera.setParameters(params);
				previewRunning = true;
			} catch (IOException e)
			{
				Log.e(LOGTAG, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height)
	{
		Log.v(LOGTAG, "surfaceChanged");
		if (!recording && usecamera)
		{
			if (previewRunning)
			{
				camera.stopPreview();
			}
			try
			{
				Camera.Parameters p = camera.getParameters();
				p.setPreviewSize(camcorderProfile.videoFrameWidth,camcorderProfile.videoFrameHeight);
				p.setPreviewFrameRate(camcorderProfile.videoFrameRate);
				camera.setParameters(p);
				camera.setPreviewDisplay(holder);
				camera.startPreview();
				previewRunning = true;
			} catch (IOException e)
			{
				Log.e(LOGTAG, e.getMessage());
				e.printStackTrace();
			}
			prepareRecorder();
		}
	}
	
	private int getDeviceOrientation(Context context) {

	    int degrees = 0;
	    WindowManager windowManager =
	            (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    int rotation = windowManager.getDefaultDisplay().getRotation();

	    switch(rotation) {
	        case Surface.ROTATION_0:
	            degrees = 0;
	            break;
	        case Surface.ROTATION_90:
	            degrees = 90;
	            break;
	        case Surface.ROTATION_180:
	            degrees = 180;
	            break;
	        case Surface.ROTATION_270:
	            degrees = 270;
	            break;
	    }

	    return degrees;
	}  
	
	public int getPreviewOrientation(Context context, int cameraId) {

		   int temp = 0;
		   int previewOrientation = 0;

		   CameraInfo cameraInfo = new CameraInfo();
		   Camera.getCameraInfo(cameraId, cameraInfo);

		   int deviceOrientation = getDeviceOrientation(context);
		   temp = cameraInfo.orientation - deviceOrientation + 360;
		   previewOrientation = temp % 360;

		    return previewOrientation;
		}
	
	public int getCamaraBackId(){

	    numberOfCameras = Camera.getNumberOfCameras();

	    CameraInfo cameraInfo = new CameraInfo();
	    for (int i = 0; i < numberOfCameras; i++) {
	        Camera.getCameraInfo(i, cameraInfo);
	        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
	            return i;
	        }
	    }
	    return -1; // Device do not have back camera !!!!???
	}
	
	public void surfaceDestroyed(SurfaceHolder holder)
	{

		Log.v(LOGTAG, "surfaceDestroyed");
		if (recording)
		{
			recorder.stop();
			recording = false;
		}
		recorder.release();
		if (usecamera)
		{
			previewRunning = false;
			// camera.lock();
			camera.release();
		}
		finish();
	}
}
