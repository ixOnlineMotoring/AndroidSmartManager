package com.nw.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nw.model.Blog;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;

public class PreviewFragment extends BaseFragement
{
	TextView tvTitle, tvSubTitle, tvDesciption;
	LinearLayout horizontalLayout;
	StringBuilder stringBuilder = new StringBuilder();
	ImageLoader imageLoader;
	LinearLayout linearLayout;
	Blog blog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_preview, container, false);
		setHasOptionsMenu(true);

		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvSubTitle = (TextView) view.findViewById(R.id.tvSubTitle);
		tvDesciption = (TextView) view.findViewById(R.id.tvDescription);
		horizontalLayout = (LinearLayout) view.findViewById(R.id.llHorizontal);
		blog = (Blog) getArguments().getParcelable("data");
		tvTitle.setText(blog.getTitle());
		
		linearLayout=(LinearLayout) getActivity().findViewById(R.id.Container);
		linearLayout.setPadding(0, 0, 0, 0);

		// check author name is exist
		if (!TextUtils.isEmpty(blog.getName().trim()))
			stringBuilder.append("By <font color='#448AF4'>" + blog.getName() + "</font> | ");

		// check created date is exist
		if (!TextUtils.isEmpty(blog.getCreatedDate()))
			stringBuilder.append("Posted On <font color='#448AF4'>" + blog.getCreatedDate() + "</font> | ");

		// check end date
		if (!TextUtils.isEmpty(blog.getEndDate()))
			stringBuilder.append("Expires On <font color='#448AF4'>" + blog.getEndDate() + "</font> ");
		else
			stringBuilder.append("Expires On <font color='#448AF4'>Never Ends</font> ");

		imageLoader = ImageLoader.getInstance();

		if (blog.getGridImages().size() != 0)
		{
			DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
			int width = displayMetrics.widthPixels;
			for (int i = 0; i < blog.getGridImages().size(); i++)
			{
				ImageView imageView = (ImageView) inflater.inflate(R.layout.grid_item_blog_preview, null);
				if (blog.getGridImages().get(i).isLocal())
					imageView.setImageBitmap(BitmapFactory.decodeFile(blog.getGridImages().get(i).getPath()));
				else
					imageLoader.displayImage(Constants.IMAGE_BASE_URL + blog.getGridImages().get(i).getLink(), imageView);
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				LayoutParams lpp = new LayoutParams(width, 500);
				lpp.weight = 1;
				lpp.bottomMargin = 10;
				lpp.topMargin = 10;
				lpp.leftMargin = 10;
				lpp.rightMargin = 10;
				imageView.setLayoutParams(lpp);
				horizontalLayout.addView(imageView);
			}
		}
		else
		{
			horizontalLayout.setVisibility(View.GONE);
		}

		tvSubTitle.setText(Html.fromHtml(stringBuilder.toString()));
		tvDesciption.setText(blog.getDetails());
		return view;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getActivity().getFragmentManager().popBackStack();
				return true;
			case 123:
				if (onsaveClickListener != null)
					onsaveClickListener.onSaveClicked();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		showActionBar(getActivity().getResources().getString(R.string.preview_blog));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		if (getArguments().getBoolean("update"))
			menu.add(0, 123, 0, "Update").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		else
			menu.add(0, 123, 0, "Save").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	public interface onSaveClickListener
	{
		void onSaveClicked();
	}

	private onSaveClickListener onsaveClickListener;

	public onSaveClickListener getOnsaveClickListener()
	{
		return onsaveClickListener;
	}

	public void setOnsaveClickListener(onSaveClickListener onsaveClickListener)
	{
		this.onsaveClickListener = onsaveClickListener;
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
}
