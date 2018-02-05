package com.nw.fragments;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify.IconValue;
import com.nw.adapters.CustomGridAdapter;
import com.nw.adapters.TouchImageAdapter;
import com.nw.model.BaseImage;
import com.nw.model.MyImage;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.ExtendedViewPager;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ImageDetailFragment extends BaseFragement implements OnItemClickListener, OnClickListener{
	
	private ExtendedViewPager mViewPager;
	TouchImageAdapter adapter;
	ArrayList<BaseImage> mList;
	ArrayList<String> urlList;
	int index=0, currentPageNo;
	GridView gvImages;
	TableRow trFooterButton;
	ImageButton ibPrev, ibNext;
	LinearLayout linearLayout;
	String prevDownloadedUrl="";
	String prevSharedPath="";
	TextView tvShare;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_large_image, container,false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
		setHasOptionsMenu(true);
		
		prevSharedPath=Environment.getExternalStorageDirectory()+"/downloadedFile.png";
		linearLayout=(LinearLayout) getActivity().findViewById(R.id.Container);
		linearLayout.setPadding(0, 0, 0, 0);
		
		//get arguments from previous fragment
		if(getArguments().containsKey("imagelist"))
			mList= getArguments().getParcelableArrayList("imagelist");
		if(getArguments().containsKey("urllist"))
			urlList= getArguments().getStringArrayList("urllist");
		index= getArguments().getInt("index");
		currentPageNo=index;
		
		//set title to action bar
	//	getActivity().getActionBar().setTitle(getArguments().getString("vehicleName"));
		if(mList!=null)
			showActionBarForGallery((index+1)+" of "+ mList.size());
		else if(urlList!=null)
			showActionBarForGallery((index+1)+" of "+ urlList.size());
		
		trFooterButton= (TableRow) view.findViewById(R.id.trFooterButtons);
		ibPrev= (ImageButton) view.findViewById(R.id.ibPrev);
		ibNext= (ImageButton) view.findViewById(R.id.ibNext);
		ibPrev.setOnClickListener(this);
		ibNext.setOnClickListener(this);
		
		checkFooterStatus();
		
		// initialise view pager
		mViewPager = (ExtendedViewPager) view.findViewById(R.id.evpImages);
		
		if(mList!=null)
			adapter= new TouchImageAdapter(getActivity(), mList);
		else if(urlList!=null)
			adapter= new TouchImageAdapter(getActivity(), urlList,true);
		
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				//indexing of gallery
				if(mList!=null)
					showActionBarForGallery((position+1)+" of "+mList.size());
				else if(urlList!=null)
					showActionBarForGallery((position+1)+" of "+urlList.size());
				currentPageNo=position;
				checkFooterStatus();
				index=position;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		mViewPager.setCurrentItem(index);
		
		// initialise grid view. and set visibility to gone by default
		gvImages= (GridView) view.findViewById(R.id.gvImages);
		if(mList!=null)
			gvImages.setAdapter(new CustomGridAdapter(getActivity(), mList));
		else if(urlList!=null)
			gvImages.setAdapter(new CustomGridAdapter(getActivity(), urlList,true));
		gvImages.setOnItemClickListener(this);
		
		return view;

	}
	
	//function checks the index of current image in view pager and changes footer button status accordingly
	//if current position is 0 hide previous button
	//if current position is last hide next button
	// otherwise show both buttons
	private void checkFooterStatus(){
		if(mList!=null){
			if(currentPageNo==mList.size()-1){
				ibNext.setVisibility(View.INVISIBLE);
				if(mList.size()>=1)
					ibPrev.setVisibility(View.VISIBLE);
			}else if(currentPageNo==0){
				ibPrev.setVisibility(View.INVISIBLE);
				if(mList.size()>=1)
					ibNext.setVisibility(View.VISIBLE);
			}else if(mList.size()==1||mList.size()==0){
				ibNext.setVisibility(View.INVISIBLE);
				ibPrev.setVisibility(View.INVISIBLE);
			}else{
				ibPrev.setVisibility(View.VISIBLE);
				ibNext.setVisibility(View.VISIBLE);
			}
			
			if(mList.size()==1||mList.size()==0){
				ibNext.setVisibility(View.INVISIBLE);
				ibPrev.setVisibility(View.INVISIBLE);
			}
		}else if(urlList!=null){
			
			if(currentPageNo==urlList.size()-1){
				ibNext.setVisibility(View.INVISIBLE);
				if(urlList.size()>=1)
					ibPrev.setVisibility(View.VISIBLE);
			}else if(currentPageNo==0){
				ibPrev.setVisibility(View.INVISIBLE);
				if(urlList.size()>=1)
					ibNext.setVisibility(View.VISIBLE);
			}else if(urlList.size()==1||urlList.size()==0){
				ibNext.setVisibility(View.INVISIBLE);
				ibPrev.setVisibility(View.INVISIBLE);
			}else{
				ibPrev.setVisibility(View.VISIBLE);
				ibNext.setVisibility(View.VISIBLE);
			}
			
			if(urlList.size()==1||urlList.size()==0){
				ibNext.setVisibility(View.INVISIBLE);
				ibPrev.setVisibility(View.INVISIBLE);
			}
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// on grid view item click display images in large
		
		mViewPager.setVisibility(View.VISIBLE);
		gvImages.setVisibility(View.GONE);
		mViewPager.setCurrentItem(position);
		if(mList!=null)
			showActionBarForGallery((position+1)+" of "+ mList.size());
		else if(urlList!=null)
			showActionBarForGallery((position+1)+" of "+ urlList.size());
		trFooterButton.setVisibility(View.VISIBLE);
		index=position;
		tvShare.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibPrev:
				currentPageNo--;
				mViewPager.setCurrentItem(currentPageNo);
				checkFooterStatus();
			break;
		case R.id.ibNext:
				currentPageNo++;
				mViewPager.setCurrentItem(currentPageNo);
				checkFooterStatus();
			break;
			
		default:
			break;
		}
	}

	protected void showActionBarForGallery(String titletobeset) 
	{
		getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActivity().getActionBar().setDisplayShowHomeEnabled(false);
		getActivity().getActionBar().setDisplayShowCustomEnabled(true);
		getActivity().getActionBar().setDisplayShowTitleEnabled(false);
		View view = View.inflate(getActivity(),R.layout.custom_actionbar_gallery, null);
		getActivity().getActionBar().setCustomView(view);
		TextView title = (TextView) view.findViewById(R.id.tvScreenTitle);
		title.setText(titletobeset);
		TextView tvBack = (TextView) view.findViewById(R.id.tvBack);
		TextView tvSeeAll = (TextView) view.findViewById(R.id.tvSeeAll);
		tvShare = (TextView) view.findViewById(R.id.tvShare);
		tvShare.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(getActivity(),IconValue.fa_share_alt,30),null, null, null);
		tvSeeAll.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(mViewPager.getVisibility()==View.VISIBLE){
	        		mViewPager.setVisibility(View.GONE);
	        		gvImages.setVisibility(View.VISIBLE);
	        	//	item.setTitle(getString(R.string.close));
	        		trFooterButton.setVisibility(View.GONE);
					tvShare.setVisibility(View.GONE);

	        		
	        	}else{											// if single large image visible	
	        		mViewPager.setVisibility(View.VISIBLE);
	        		gvImages.setVisibility(View.GONE);
	      //  		item.setTitle(getString(R.string.see_all));
	        		if(mList!=null)
	        			showActionBarForGallery((index+1)+" of "+ mList.size());
	        		else
	        			showActionBarForGallery((index+1)+" of "+ urlList.size());
	        		trFooterButton.setVisibility(View.VISIBLE);
					tvShare.setVisibility(View.VISIBLE);

	        	}
			}
		});
		tvShare.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				manageShare();
			}
		});
		Rect bounds = new Rect();
		Paint textPaint = tvBack.getPaint();
		textPaint.getTextBounds(tvBack.getText().toString(), 0, tvBack.getText().toString().length(), bounds);
		if (title.getText().toString().trim().length() > 30)
			title.setPadding((int) (bounds.width() * 1.5), 0, 0, 0);
		tvBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				hideKeyboard();
				if (getFragmentManager().getBackStackEntryCount() != 0) {
					getFragmentManager().popBackStack();
				} else {
					getActivity().finish();
				}
				getActivity().getActionBar().setDisplayShowCustomEnabled(false);
			}
		});
	}
	
	private void manageShare(){
		
		String pathToShare="";
    	if(mList!=null){
    		if(mList.get(currentPageNo).isLocal()){
    			pathToShare=mList.get(currentPageNo).getPath();
    			shareLocalImage(getActivity(),pathToShare);
    		}else{
    			if(mList.get(currentPageNo) instanceof MyImage){
    				MyImage tempImage= (MyImage) mList.get(currentPageNo);
        			if(!prevDownloadedUrl.equals(tempImage.getFull())){
        				prevDownloadedUrl=tempImage.getFull();
        				new ImageDownloadTask((BuyActivity) getActivity()).execute(tempImage.getFull());
        			}else
        				shareLocalImage(getActivity(),prevSharedPath);
    			}else{
    				if(!prevDownloadedUrl.equals(mList.get(currentPageNo).getLink())){
    					prevDownloadedUrl=mList.get(currentPageNo).getLink();
    					new ImageDownloadTask((BuyActivity) getActivity()).execute(mList.get(currentPageNo).getLink());
    				}else
        				shareLocalImage(getActivity(),prevSharedPath);
    			}
    		}
    	}else if(urlList!=null){
        		if(!prevDownloadedUrl.equals(urlList.get(currentPageNo))){
        			prevDownloadedUrl=urlList.get(currentPageNo);
        			new ImageDownloadTask((BuyActivity) getActivity()).execute(urlList.get(currentPageNo));
        		}else
        			shareLocalImage(getActivity(),prevSharedPath);
    	}
	}
	
	private static void shareLocalImage(Context c,String imagePath) {
	    if(imagePath!=null){
			Intent share = new Intent(Intent.ACTION_SEND);
		    share.setType("image/*");
		    File imageFileToShare = new File(imagePath);
		    Uri uri = Uri.fromFile(imageFileToShare);
		    share.putExtra(Intent.EXTRA_STREAM, uri);
		    c.startActivity(Intent.createChooser(share, "Share Image"));
	    }
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(Helper.isTablet(getActivity())){
			linearLayout.setPadding(Helper.dpToPx(getActivity(), 16),
					Helper.dpToPx(getActivity(), 16), 
					Helper.dpToPx(getActivity(), 16), 
					Helper.dpToPx(getActivity(), 16));
		}else{
			linearLayout.setPadding(Helper.dpToPx(getActivity(), 10),
					Helper.dpToPx(getActivity(), 10), 
					Helper.dpToPx(getActivity(), 10), 
					Helper.dpToPx(getActivity(), 10));
		}
	}
	
	public static class ImageDownloadTask extends AsyncTask<String, Void, String>{
		Dialog proDialog;
		WeakReference<BuyActivity> mContext;
		public ImageDownloadTask(BuyActivity activity) {
			mContext=new WeakReference<BuyActivity>(activity);
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			proDialog=CustomDialogManager.showProgressDialog(mContext.get());
		}
		@Override
		protected String doInBackground(String... params) {
			return Helper.downloadImage(params[0]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			proDialog.dismiss();
			shareLocalImage(mContext.get(),result);
		}
	}
}
