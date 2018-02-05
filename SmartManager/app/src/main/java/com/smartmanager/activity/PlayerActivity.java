package com.smartmanager.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.smartmanager.android.R;

public class PlayerActivity extends Activity implements OnClickListener{
	ProgressBar progressBar;
	ImageView imgPlay;
	VideoView vvPreview;
	String video_url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent()!=null) 
		{
			video_url = getIntent().getExtras().getString("video_url");
			MediaMetadataRetriever mdr = new MediaMetadataRetriever();
			mdr.setDataSource(video_url);
			//int height = Integer.parseInt(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
			//int width = Integer.parseInt(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
			/*if (video_url.contains("https://www"))
			{
				//http://www.gobabyclub.com/admin/uploads/massage_videos/1434466498for%20parents.mp4
				vvPreview.setVideoPath(video_url);
			}else {

				vvPreview.setVideoURI(viduri);
			}*/
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_videoplayer);
		vvPreview = (VideoView) findViewById(R.id.vvPreview);
		progressBar = (ProgressBar) findViewById(R.id.prgIndicator); 
		imgPlay = (ImageView) findViewById(R.id.imgPlay);
		imgPlay.setOnClickListener(this);
		imgPlay.setVisibility(View.GONE);
		Uri viduri = Uri.parse(video_url);
		vvPreview.setVideoURI(viduri);
		MediaController controller = new MediaController(this);
		controller.setAnchorView(vvPreview);
		vvPreview.setMediaController(controller);
		vvPreview.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) 
			{
				progressBar.setVisibility(View.GONE);
			}
		});
		vvPreview.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) 
			{
				imgPlay.setVisibility(View.VISIBLE);
			}
		});
	}
	
	 @Override
	    public void onResume() {

	        super.onResume();
	        vvPreview.setVideoPath(video_url);
	        vvPreview.setMediaController(new MediaController(this));
	        vvPreview.requestFocus();
	        vvPreview.start();
	 }
	 
	 @Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	
		super.onConfigurationChanged(newConfig);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	 
	 @Override
	    public void onPause() {

	        super.onPause();
	        vvPreview.stopPlayback();

	        vvPreview.setMediaController(null);
	    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) 
		{
		case R.id.imgPlay:
			vvPreview.start();
			progressBar.setVisibility(View.VISIBLE);
			if (vvPreview.isPlaying()) {
				progressBar.setVisibility(View.GONE);
			}
			imgPlay.setVisibility(View.GONE);
			break;
		}
	}
}



