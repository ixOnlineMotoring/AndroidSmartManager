package com.nw.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.adapters.GridImageAdapter.AddPhotoListener;
import com.nw.broadcast.NetworkUtil;
import com.nw.database.SMDatabase;
import com.nw.fragments.PreviewFragment.onSaveClickListener;
import com.nw.interfaces.DateListener;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.model.BaseImage;
import com.nw.model.Blog;
import com.nw.model.BlogType;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.DragableGridView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.util.ArrayList;

public class CustomerDeliveryFragment extends BaseFragement implements
		OnClickListener, AddPhotoListener, onSaveClickListener {
	EditText edtTitle, edtDetails, edtAuthor, edtActive, edtEndDate;
	ImageView ivAdd;
	Button btnPreview, btnSave;
	ArrayList<BlogType> blogTypes;
	EditText tvBlogType;
	ArrayAdapter<BlogType> blogadapter;
	ScrollView scrollView;
	BlogType blogType;
	CheckBox active;
	Blog blog;
	int count;
	int responseSize;
	boolean isPreview = false;
	String blogMessage = "";
	/**
	 * Deleted image count
	 */
	int deleteImageCount = 0;
	int k = 0;
	/**
	 * Total Number of images for blog.
	 */
	int imageCount;
	int j = 0;
	int i = 0;

	DragableGridView imageDragableGridView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_customer_delivery, container, false);
		setHasOptionsMenu(true);
		scrollView = (ScrollView) view.findViewById(R.id.search_form);
		edtTitle = (EditText) view.findViewById(R.id.edtTitle);
		edtDetails = (EditText) view.findViewById(R.id.edtDetails);
		edtAuthor = (EditText) view.findViewById(R.id.edtAuthor);
		edtActive = (EditText) view.findViewById(R.id.edtActive);
		edtEndDate = (EditText) view.findViewById(R.id.edtEnd);
		tvBlogType = (EditText) view.findViewById(R.id.tvBlogType);
		active = (CheckBox) view.findViewById(R.id.cbActive);

		btnPreview = (Button) view.findViewById(R.id.btnPreview);
		btnSave = (Button) view.findViewById(R.id.btnSave);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			//this code will be executed on devices running Marshmellow or later
			imageDragableGridView = new DragableGridView();
		}else {
			imageDragableGridView = new DragableGridView(getActivity());
		}
		imageDragableGridView.init(view, new ImageClickListener()
		{
			@Override
			public void onImageClick(int position){}

			@Override
			public void onImageDeleted(int position){}
		});
		if(blog!=null){
			imageDragableGridView.setImageList(blog.getGridImages());
		}
		edtDetails.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				edtDetails.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		tvBlogType.setOnClickListener(this);
		edtActive.setOnClickListener(this);
		edtEndDate.setOnClickListener(this);
		btnPreview.setOnClickListener(this);
		btnSave.setOnClickListener(this);

		if (getArguments() != null)
			blogMessage =getString(R.string.blog_updated_successfully);
		else
			blogMessage = getString(R.string.blog_saved_successfully);
		
		hideKeyboard(view);
		return view;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{

			case R.id.tvBlogType:
				blogadapter = new ArrayAdapter<BlogType>(getActivity(), R.layout.list_item_text2, blogTypes);
				Helper.showDropDown(tvBlogType, blogadapter, new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
					{
						blogType = blogadapter.getItem(itemPosition);
						tvBlogType.setText(blogType.getName());
					}
				});
				break;
			case R.id.edtActive:
				Bundle args = new Bundle();
				args.putBoolean("isEndDate", false);
				DatePickerFragment startDate = new DatePickerFragment();
				startDate.setArguments(args);
				startDate.setDateListener(new DateListener()
				{
					@Override
					public void onDateSet(int year, int monthOfYear, int dayOfMonth)
					{
						edtActive.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
					}
				});
				startDate.show(getActivity().getFragmentManager(), "datePicker");

				break;
			case R.id.edtEnd:
				Bundle bundle = new Bundle();
				bundle.putBoolean("isEndDate", true);
				DatePickerFragment endDate = new DatePickerFragment();
				endDate.setArguments(bundle);
				endDate.setDateListener(new DateListener()
				{
					@Override
					public void onDateSet(int year, int monthOfYear, int dayOfMonth)
					{
						edtEndDate.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
					}
				});
				endDate.show(getActivity().getFragmentManager(), "datePicker");

				break;
			case R.id.btnPreview:
				previewBlog();

				break;
			case R.id.btnSave:
				// save blog
				if (TextUtils.isEmpty(tvBlogType.getText().toString().trim()))
				{
					Helper.showToast(getString(R.string.please_select_blog_type), getActivity());
					scrollView.scrollTo(0, tvBlogType.getTop());
					return;
				}

				if (TextUtils.isEmpty(edtTitle.getText().toString().trim()))
				{
					Helper.showToast(getString(R.string.please_enter_title), getActivity());
					scrollView.scrollTo(0, edtTitle.getTop());
					return;
				}

				if (TextUtils.isEmpty(edtDetails.getText().toString().trim()))
				{
					Helper.showToast(getString(R.string.please_enter_blog_details), getActivity());
					scrollView.scrollTo(0, edtDetails.getTop());
					return;
				}

				if (TextUtils.isEmpty(edtActive.getText().toString().trim()))
				{
					Helper.showToast(getString(R.string.please_select_active_date), getActivity());
					scrollView.scrollTo(0, edtActive.getTop());
					return;
				}

				if(!active.isChecked())
				{
					CustomDialogManager.showOkCancelDialog(getActivity(), "This Post is not active. Are you sure you want to save?", "Yes", "No", new DialogListener() {
						
						@Override
						public void onButtonClicked(int type) {
							if(Dialog.BUTTON_NEGATIVE==type)
							{
								return;
							}else
							{
								if (blog == null)
									blog = new Blog();
								blog.setTitle(edtTitle.getText().toString().trim());
								blog.setCreatedDate(edtActive.getText().toString().trim());
								blog.setEndDate(edtEndDate.getText().toString().trim());
								blog.setName(edtAuthor.getText().toString().trim());
								blog.setDetails(edtDetails.getText().toString().trim());
								blog.setGridImages(imageDragableGridView.getUpdatedImageListWithoutPlus());
								isPreview = false;
								saveBlogPost();
							}
							
						}
					});
				}else
				{
					if (blog == null)
						blog = new Blog();
					blog.setTitle(edtTitle.getText().toString().trim());
					blog.setCreatedDate(edtActive.getText().toString().trim());
					blog.setEndDate(edtEndDate.getText().toString().trim());
					blog.setName(edtAuthor.getText().toString().trim());
					blog.setDetails(edtDetails.getText().toString().trim());
					blog.setGridImages(imageDragableGridView.getUpdatedImageListWithoutPlus());
					isPreview = false;
					saveBlogPost();
				}
				
				break;
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (getArguments() == null)
			showActionBar(getActivity().getResources().getString(R.string.delivery));
		else
			showActionBar(getArguments().getString("title"));
	
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null)
		{
			// edit fragment
			btnSave.setText("Update");
			if (blogTypes == null)
				getBlogType(false);

			if (blog == null)
			{
				showProgressDialog();
				getBlog();
			}
		}
		else
		{
			btnSave.setText("Save");
			blogType=new BlogType();
			blogType.setActive(true);
			blogType.setBlogPostTypeID(3);
			blogType.setName("Customer Delivery");
			blogType.setOrder(0);
			
			if (blogTypes == null)
				blogTypes=new ArrayList<BlogType>();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				Helper.hidekeybord(edtTitle);
				getActivity().finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void previewBlog()
	{
		if (TextUtils.isEmpty(tvBlogType.getText().toString().trim()))
		{
			Helper.showToast(getString(R.string.please_select_blog_type), getActivity());
			scrollView.scrollTo(0, tvBlogType.getTop());
			return;
		}

		if (TextUtils.isEmpty(edtTitle.getText().toString().trim()))
		{
			Helper.showToast(getString(R.string.please_enter_title), getActivity());
			scrollView.scrollTo(0, edtTitle.getTop());
			return;
		}

		if (TextUtils.isEmpty(edtDetails.getText().toString().trim()))
		{
			Helper.showToast(getString(R.string.please_enter_blog_details), getActivity());
			scrollView.scrollTo(0, edtDetails.getTop());
			return;
		}

		if (TextUtils.isEmpty(edtActive.getText().toString().trim()))
		{
			Helper.showToast(getString(R.string.please_select_active_date), getActivity());
			scrollView.scrollTo(0, edtActive.getTop());
			return;
		}
		if (blog == null)
			blog = new Blog();
		blog.setTitle(edtTitle.getText().toString().trim());
		blog.setCreatedDate(edtActive.getText().toString().trim());
		blog.setEndDate(edtEndDate.getText().toString().trim());
		blog.setName(edtAuthor.getText().toString().trim());
		blog.setDetails(edtDetails.getText().toString().trim());
		blog.setGridImages(imageDragableGridView.getUpdatedImageListWithoutPlus());
		Bundle bundle = new Bundle();
		bundle.putBoolean("update", getArguments() == null ? false : true);
		bundle.putParcelable("data", blog);
		PreviewFragment previewFragment = new PreviewFragment();
		previewFragment.setOnsaveClickListener(this);
		previewFragment.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.Container, previewFragment).addToBackStack(null).commit();
	}

	private void getBlog()
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			/*final String soapMessage = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
					"<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
					"<Body>" +
					"<GetBlog xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">" +
					"<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>" +
					"<ID>" + getArguments().getInt("data") + "</ID>" +
					"</GetBlog>" +
					"</Body>" +
					"</Envelope>";*/
			
			StringBuilder soapMessage=new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<GetBlog xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
			soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
			soapMessage.append("<ID>" + getArguments().getInt("data") + "</ID>");
			soapMessage.append("</GetBlog>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");
			
			
			VollyResponseListener listener=new VollyResponseListener()
			{
				
				@Override
				public void onErrorResponse(VolleyError error)
				{
					hideProgressDialog();
					Helper.showToast(getString(R.string.error_getting_data), getActivity());
					VolleyLog.e("Error: ", error.toString());
					
				}
				
				@Override
				public void onResponse(String response)
				{
					if(response==null)
					{
						hideProgressDialog();
						return;
					}
					Helper.Log("TAG", "" + response);
					blog = ParserManager.parseGetBlog(response);
					if (blog != null)
					{
						edtTitle.setText(Html.fromHtml(blog.getTitle()));
						edtAuthor.setText(Html.fromHtml(blog.getAuthor()));
						edtDetails.setText(Html.fromHtml(blog.getDetails()));
						edtActive.setText(blog.getCreatedDate());

						edtEndDate.setText(blog.getEndDate().equals("31 Dec 1969") || (blog.getEndDate().equals("01 Jan 1970")) ? "" : blog.getEndDate());
						blogType = getBlogPostType();
						active.setChecked(blog.isActive());
						if (blogType != null)
							tvBlogType.setText(blogType.getName());
						if (blog.getGridImages().size() > 0)
						{

							imageDragableGridView.setImageList(blog.getGridImages());
							
						}
					}
					hideProgressDialog();
					
				}
			};
			
			VollyCustomRequest request=new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL,soapMessage.toString(),Constants.TEMP_URI_NAMESPACE + "IBlogService/GetBlog",listener);
			try
			{
				request.init();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	/**
	 * Get blog type from server
	 */
	public void getBlogType(final boolean showDialog)
	{
		blogTypes = new ArrayList<BlogType>();
		if (showDialog)
			showProgressDialog();
		StringBuilder soapMessage=new StringBuilder();
		soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		soapMessage.append("<Body>");
		soapMessage.append("<GetBlogTypes xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
		soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>" );
		soapMessage.append("</GetBlogTypes>");
		soapMessage.append("</Body>");
		soapMessage.append("</Envelope>");
		
		VollyResponseListener listener=new VollyResponseListener()
		{
			
			@Override
			public void onErrorResponse(VolleyError error)
			{
				if (showDialog)
					hideProgressDialog();
				VolleyLog.e("Error: ", error.toString());
			}
			
			@Override
			public void onResponse(String response)
			{
				ArrayList<BlogType> tempBlogTypes = ParserManager.parseBlogType(response);
				if(response==null){
					if (showDialog)
						hideProgressDialog();
					return;
				}
				if (tempBlogTypes != null)
				{
					if (!tempBlogTypes.isEmpty())
					{
						blogTypes.addAll(tempBlogTypes);
					}
				}
				if (showDialog)
					hideProgressDialog();
				
			}
		};
		
		
		VollyCustomRequest request=new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL,soapMessage.toString(),Constants.TEMP_URI_NAMESPACE + "IBlogService/GetBlogTypes",listener);
		try
		{
			request.init("getBlogType");
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	public void saveBlogPost()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			SearchBlogFragment.isEdited = true;
			showProgressDialog();		
			save();
		}
		else
			HelperHttp.showNoInternetDialog(getActivity());
	}

	private void save()
	{
		// Add parameters to request in arraylist
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		parameterList.add(new Parameter("UserHash", DataManager.getInstance().user.getUserHash(), String.class));
		parameterList.add(new Parameter("BlogPostTypeVal", blogType.getBlogPostTypeID(), Integer.class));
		parameterList.add(new Parameter("Title", blog.getTitle(), String.class));
		parameterList.add(new Parameter("CKEditor1", blog.getDetails(), String.class));
		parameterList.add(new Parameter("DateFrom", blog.getCreatedDate(), String.class));
		parameterList.add(new Parameter("EndDate", (blog.getEndDate() == null ? "" : blog.getEndDate()), String.class));
		parameterList.add(new Parameter("Author", (blog.getAuthor() == null ? "" : blog.getAuthor()), String.class));
		parameterList.add(new Parameter("Active", active.isChecked(), Boolean.class));
		parameterList.add(new Parameter("nUserID", DataManager.getInstance().user.getIdenttity(), Integer.class));
		parameterList.add(new Parameter("nCLientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
		parameterList.add(new Parameter("BlogPostID", getArguments() != null ? blog.getBlogPostID() : 0, Integer.class));
		// create web service inputs
		DataInObject inObj = new DataInObject();
		inObj.setMethodname("SaveBlogPost");
		inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
		inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IBlogService/SaveBlogPost");
		inObj.setUrl(Constants.BLOG_WEBSERVICE_URL);
		inObj.setParameterList(parameterList);

		// Network call
		new WebServiceTask(getActivity(), inObj, true, new TaskListener()
		{

			@Override
			public void onTaskComplete(Object result)
			{
				if (result != null)
				{
					int blogPostId = 0;
					if (TextUtils.isEmpty(result.toString()))
					{
						blogPostId = 0;
					}
					else
					{
						blogPostId = Integer.parseInt(result.toString());// ParserManager.parseSaveBlog(response);
					}

					if (blogPostId > 0)
					{
						// check for image size
						if (!blog.getGridImages().isEmpty())
						{
							count = blog.getGridImages().size();
							responseSize = count;
							i = 0;
							Helper.Log("Image count before saveBlogImage", "" + count);
							saveBlogImage(blogPostId);
						}
						else
						{
							hideProgressDialog();
							CustomDialogManager.showOkDialog(getActivity(),  blogMessage, new DialogListener()
							{
								@Override
								public void onButtonClicked(int type)
								{
									if (isPreview == false)
										getActivity().finish();
									else
									{
										getActivity().finish();
									}
								}
							});
						}
					}
					else
					{
						hideProgressDialog();
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.blog_not_saved_please_try));
					}
				}
				else
				{
					hideProgressDialog();
					CustomDialogManager.showOkDialog(getActivity(),  getString(R.string.blog_not_saved_please_try));
				}
			}
		}).execute();
	}

	public void saveBlogImage(final int blogPostId)
	{
		// Check is on wifi or mobile network
		if (NetworkUtil.getConnectivityStatusString(getActivity()) == ConnectivityManager.TYPE_WIFI)
		{
			sendImagesToServerOrDataBase(blogPostId, false);
		} else
		{
			boolean showImageAleart = false;

			if (imageDragableGridView.getLocalImageListWithoutPlus().size() > 0)
			{
				showImageAleart = true;
			}

			if (showImageAleart)
			{
				CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.network_title), getString(R.string.Upload_Now), getString(R.string.upload_with_wifi), new DialogListener()
				{
					@Override
					public void onButtonClicked(int type)
					{
						switch (type)
						{
							// Upload Now
							case Dialog.BUTTON_POSITIVE:
								sendImagesToServerOrDataBase(blogPostId, false);
								break;
							// Upload With WIFI
							case Dialog.BUTTON_NEGATIVE:
								sendImagesToServerOrDataBase(blogPostId, true);
								break;
						}
					}
				});
			}else
			{
				sendImagesToServerOrDataBase(blogPostId, false);
			}
		}
	}

	/**
	 * This Method is used to send selected images to server.
	 *
	 * @param blogPostId     = blogPostId
	 * @param isSaveDataBase = if true (Save to local database) if false (send directly to server on any network)
	 */
	private void sendImagesToServerOrDataBase(final int blogPostId, final boolean isSaveDataBase)
	{

		String base64String = null;
		if (blog.getGridImages().get(i).isLocal())
		{
			base64String=Helper.convertBitmapToBase64(blog.getGridImages().get(i).getPath());
			if(TextUtils.isEmpty(base64String))
			{
				i++;
				responseSize--;
				if (i == count)
					checkResponse();
				else
					sendImagesToServerOrDataBase(blogPostId,isSaveDataBase);
			}
			
			if(HelperHttp.isNetworkAvailable(getActivity()))
			{
				StringBuilder soapMessage=new StringBuilder();
				soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>" );
				soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
				soapMessage.append("<Body>");
				soapMessage.append("<SaveBlogImage xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");

				if(isSaveDataBase)
					soapMessage.append("<UserHash>" + Constants.CHANGE_THIS_USER_HASH + "</UserHash>");
				else
					soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");

				soapMessage.append("<BlogPostID>" + blogPostId + "</BlogPostID>");
				soapMessage.append("<cmUserID>" + DataManager.getInstance().user.getIdenttity() + "</cmUserID>");
				soapMessage.append("<ClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</ClientID>");
				soapMessage.append("<Priority>" + (i + 1) + "</Priority>" );
				soapMessage.append("<OriginalFileName>" + Helper.getFileName(blog.getGridImages().get(i).getPath()) + "</OriginalFileName>");
				soapMessage.append("<Base64EncodedImage>" + base64String + "</Base64EncodedImage>");
				soapMessage.append("</SaveBlogImage>");
				soapMessage.append("</Body>");			
				soapMessage.append("</Envelope>");		
	
				Helper.Log("soapMessage " + i, "" + soapMessage.toString());

				// Check do we need to save in local database or send to server.
				if (isSaveDataBase)
				{
					// Save in local Database
					SMDatabase myDatabase = new SMDatabase(getContext());
					myDatabase.insertRecords(soapMessage.toString(), Constants.SaveDeliveryImage);

					i++;
					responseSize--;
					if (i == count)
						checkResponse();
					else
					{
						sendImagesToServerOrDataBase(blogPostId,isSaveDataBase);
					}
				}else
				{
					VollyResponseListener listener=new VollyResponseListener()
					{
						@Override
						public void onErrorResponse(VolleyError error)
						{
							VolleyLog.e("Error: ", ""+error);
							i++;
							responseSize--;
							if (i == count)
								checkResponse();
							else
							{
								sendImagesToServerOrDataBase(blogPostId,isSaveDataBase);
							}
						}

						@Override
						public void onResponse(String response)
						{
							i++;
							responseSize--;
							Helper.Log("TAG", ""+response);
							if (i == count)
								checkResponse();
							else
							{
								sendImagesToServerOrDataBase(blogPostId,isSaveDataBase);
							}
						}
					};

					VollyCustomRequest request=new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL,soapMessage.toString(),Constants.TEMP_URI_NAMESPACE + "IBlogService/SaveBlogImage",listener);
					try
					{
						request.init(""+ i);
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
			}
			else
			{
				CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
			}
		}
		else
		{
			// no need to update image
			responseSize--;
			i++;
			if (i == count)
				checkResponse();
			else
			{
				sendImagesToServerOrDataBase(blogPostId,isSaveDataBase);
			}
		}
	}

	private BlogType getBlogPostType()
	{
		BlogType blogTypeResult = null;
		for (BlogType blogType : blogTypes)
		{
			if (blog.getBlogPostTypeId() == blogType.getBlogPostTypeID())
			{
				blogTypeResult = blogType;
				break;
			}
		}

		return blogTypeResult;
	}

	void checkResponse()
	{
		if (responseSize == 0)
		{
			if (getArguments() != null)
			{
				// image priority changed
				if (imageDragableGridView.isPriorityChanged())
				{
					imageCount = imageDragableGridView.getUpdatedImageListWithoutPlus().size();// adapter.getCount()-1;
					j = 0;
					updateImagePriority();
				}
				else
				{
					// check any image deleted
					if (imageDragableGridView.isImageDeleted())
					{
						deleteImageCount = imageDragableGridView.getDeletedImages().size();
						k = 0;
						deleteImage();
					}
					else
					{
						hideProgressDialog();
						CustomDialogManager.showOkDialog(getActivity(),  blogMessage, new DialogListener()
						{
							@Override
							public void onButtonClicked(int type)
							{
								if (isPreview == false)
									getActivity().finish();
								else
								{
									getActivity().finish();
								}
							}
						});

					}
				}
			}
			else
			{
				hideProgressDialog();
				CustomDialogManager.showOkDialog(getActivity(), blogMessage, new DialogListener()
				{
					@Override
					public void onButtonClicked(int type)
					{
						if (isPreview == false)
							getActivity().finish();
						else
						{
							getActivity().finish();
						}
					}
				});
			}

		}
	}

	private void updateImagePriority()
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			
			StringBuilder soapMessage=new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<UpdateBlogImagePriority xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
			soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
			soapMessage.append("<blogImageID>" + ((BaseImage) imageDragableGridView.getUpdatedImageListWithoutPlus().get(j)).getId() + "</blogImageID>");
			soapMessage.append("<priority>" + (j + 1) + "</priority>");
			soapMessage.append("</UpdateBlogImagePriority>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");	
	
			Helper.Log("updateImagePriority " + j, "" + soapMessage);
			
			VollyResponseListener listener=new VollyResponseListener()
			{
				
				@Override
				public void onErrorResponse(VolleyError error)
				{
					Helper.Log("VolleyError", "" + error.toString());
					j++;
					if (j == imageCount)
						checkImagePriorityResponse();
					else
						updateImagePriority();
					
				}
				
				@Override
				public void onResponse(String response)
				{
					Helper.Log("updateImagePriority response " + j, "" + response);
					j++;
					if (j == imageCount)
						checkImagePriorityResponse();
					else
						updateImagePriority();
					
				}
			};
			
			VollyCustomRequest request=new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL,soapMessage.toString(),Constants.TEMP_URI_NAMESPACE + "IBlogService/UpdateBlogImagePriority",listener);
			try
			{
				request.init("updateImagePriority"+j);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	void checkImagePriorityResponse()
	{
		if (imageDragableGridView.isImageDeleted())
		{
			deleteImageCount = imageDragableGridView.getDeletedImages().size();
			deleteImage();
		}
		else
		{
			hideProgressDialog();
			CustomDialogManager.showOkDialog(getActivity(), blogMessage, new DialogListener()
			{

				@Override
				public void onButtonClicked(int type)
				{
					if (isPreview == false)
						getActivity().finish();
					else
					{
						getActivity().finish();
					}
				}
			});
		}
	}

	void checkImageDeleteResponse()
	{

		// image priority changed
		hideProgressDialog();
		CustomDialogManager.showOkDialog(getActivity(), blogMessage, new DialogListener()
		{

			@Override
			public void onButtonClicked(int type)
			{
				if (isPreview == false)
					getActivity().finish();
				else
				{
					getActivity().finish();
				}
			}
		});
	}

	private void deleteImage()
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			StringBuilder soapMessage=new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Body>");
			soapMessage.append("<DeleteBlogImage xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
			soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
			soapMessage.append("<blogImageID>" + imageDragableGridView.getDeletedImages().get(k).getId() + "</blogImageID>");
			soapMessage.append("</DeleteBlogImage>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");
			
			VollyResponseListener listener=new VollyResponseListener()
			{
				
				@Override
				public void onErrorResponse(VolleyError error)
				{
					Helper.Log("VolleyError", "" + error.toString());

					k++;
					if (k == deleteImageCount)
						checkImageDeleteResponse();
					else
						deleteImage();
				}
				
				@Override
				public void onResponse(String response)
				{
					Helper.Log("TAG", "" + response);
					k++;
					if (k == deleteImageCount)
						checkImageDeleteResponse();
					else
						deleteImage();
					
				}
			};
			
			VollyCustomRequest request=new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL,soapMessage.toString(),Constants.TEMP_URI_NAMESPACE + "IBlogService/DeleteBlogImage",listener);
			try
			{
				request.init("DeleteBlogImage"+k);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	@Override
	public void onAddOptionSelected(){}

	@Override
	public void onRemoveOptionSelected(int position){}

	@Override
	public void onSaveClicked()
	{
		isPreview = true;
		saveBlogPost();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (imageDragableGridView != null)
		{
			if (imageDragableGridView.isOptionSelected())
				imageDragableGridView.onActivityResult(requestCode, resultCode, data);
		}
	}

}
