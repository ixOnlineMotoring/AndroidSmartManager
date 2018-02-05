package com.nw.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nw.interfaces.BlogItemEditClickListener;
import com.nw.interfaces.LoadMoreListener;
import com.nw.model.Blog;
import com.nw.webservice.VolleySingleton;
import com.smartmanager.android.R;
import com.utils.Constants;

import java.util.ArrayList;

public class SearchBlogAdapter extends PagerAdapter {
	ArrayList<Blog> blogs;
	Context context;
	LoadMoreListener loadMoreListener;
	boolean loadMoreEnabled = true;
	ImageLoader imageLoader;
	LinearLayout llSearchBlog;
	BlogItemEditClickListener blogItemEditClickListener;

	public SearchBlogAdapter(Context context, ArrayList<Blog> blogs,BlogItemEditClickListener editClickListener) {
		this.context = context;
		this.blogs = new ArrayList<Blog>();
		this.blogs.addAll(blogs);
		this.blogItemEditClickListener = editClickListener;
		imageLoader = VolleySingleton.getInstance().getImageLoader();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.pager_item_search, null);
		llSearchBlog =(LinearLayout) view.findViewById(R.id.llSearchBlog);
		NetworkImageView ivImage = (NetworkImageView) view.findViewById(R.id.ivImage);
		TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvTitle.setEllipsize(TruncateAt.END);
		TextView tvDescption = (TextView) view.findViewById(R.id.tvDescption);
		TextView tvType = (TextView) view.findViewById(R.id.tvType);
		TextView tvImageCount = (TextView) view.findViewById(R.id.tvImageCount);
		TextView tvDaysRemaining = (TextView) view.findViewById(R.id.tvDaysRemaining);
		TextView tvActive = (TextView) view.findViewById(R.id.tvActive);
		TextView tvCreated = (TextView) view.findViewById(R.id.tvCreated);
		container.addView(view);
		Blog blog = blogs.get(position);
		ivImage.setContentDescription(null);
		tvTitle.setText("" + blog.getTitle());
		tvDescption.setText(Html.fromHtml("" + blog.getDetails()));
		tvType.setText(Html.fromHtml("<b>" + tvType.getText() + "</b> "+ blog.getBlogType()));
		tvImageCount.setText(Html.fromHtml("<b>" + tvImageCount.getText()+ "</b> " + blog.getImageCount()));
		String path = Constants.IMAGE_BASE_URL + blog.getImagePath();
		path = path.replaceAll(" ", "%20");
		if (TextUtils.isEmpty(path))
		{
			ivImage.setImageResource(R.drawable.noimage);
		} else
		{
			ivImage.setImageUrl(path,imageLoader);
		}

		if (blog != null) 
		{
			if (blog.getEndStatus() != null) 
			{
				tvDaysRemaining.setVisibility(View.VISIBLE);
				if (blog.getEndStatus().contains("day(s)")) 
				{
					tvDaysRemaining.setText("Days Remaining: "+ blog.getEndStatus());
				} 
				else {
					tvDaysRemaining.setText("Time Remaining: "+ blog.getEndStatus());
				}
			} else
				tvDaysRemaining.setVisibility(View.GONE);

		}
		llSearchBlog.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				blogItemEditClickListener.onBlogItemClicked(position);
			}
		});

		tvActive.setText(Html.fromHtml("<b>"
				+ tvActive.getText()
				+ "</b> "
				+ blog.getPublishDate()
				+ " to "
				+ (blog.getEndDate().equals("31 Dec 1969")||(blog.getEndDate().equals("01 Jan 1970"))||(blog.getEndDate().equals("01 Jan 1900")) ? "Ends Never" : blog.getEndDate())));
		tvCreated.setText(Html.fromHtml("<b>" + tvCreated.getText() + "</b> "+ blog.getCreatedDate()+" by "+blog.getName()));

		// check for load more option
		if (loadMoreEnabled) 
		{
			if(blogs.size()>1)
			{
				if (position == (blogs.size()-1)) 
				{
					if (loadMoreListener != null)
						loadMoreListener.onLoadMore();
				}
			}			
		}
		return view;
	}

	
	public void clear() {
		blogs.clear();
	}

	@Override
	public int getCount() {
		return this.blogs.size();
	}

	public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
		this.loadMoreListener = loadMoreListener;
	}

	public void setLoadMoreEnabled(boolean loadMoreEnabled) {
		this.loadMoreEnabled = loadMoreEnabled;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((LinearLayout) object);
	}

}
