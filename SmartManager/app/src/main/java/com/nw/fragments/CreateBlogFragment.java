package com.nw.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.ScrollView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.greysonparrelli.permiso.Permiso;
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
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.util.ArrayList;

public class CreateBlogFragment extends BaseFragement implements OnClickListener, AddPhotoListener, onSaveClickListener
{
    EditText edtTitle, edtDetails, edtAuthor, edtActive, edtEndDate;
    Button btnPreview, btnSave;
    ArrayList<BlogType> blogTypes;
    EditText tvBlogType;
    ArrayAdapter<BlogType> blogadapter;
    ScrollView scrollView;
    BlogType blogType;
    CheckBox active;
    Blog blog;
    int responseSize;
    boolean isPreview = false;
    String blogMessage = "";
    int deleteImageCount = 0;
    int imageDeleteIndex = 0;
    int imageCount;
    int imagePriotiyIndex = 0;
    int imageUploadIndex = 0;

    DragableGridView imageDragableGridView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Permiso.getInstance().setActivity(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_blog, container, false);
        setHasOptionsMenu(true);
        initialise(view);
        hideKeyboard(view);
        return view;
    }

    private void initialise(View view)
    {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //this code will be executed on devices running Marshmellow or later
            imageDragableGridView = new DragableGridView(getActivity());
        } else
        {
            imageDragableGridView = new DragableGridView();
        }
        imageDragableGridView.init(view, new ImageClickListener()
        {
            @Override
            public void onImageClick(int position)
            {
                navigateToLargeImage(position);
            }

            @Override
            public void onImageDeleted(int position)
            {
            }
        });
        if (blog != null)
        {
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
            blogMessage = getString(R.string.blog_updated_successfully);
        else
            blogMessage = getString(R.string.blog_saved_successfully);
    }

    /*
     * Navigate to large screen fragment
     * As Fragment to called belongs to other activity we call activity and pass parameters through intent
     * Parameter- position of image clicked
     * */
    private void navigateToLargeImage(int position)
    {
        try
        {
            Intent iToBuyActivity = new Intent(getActivity(), BuyActivity.class);
            iToBuyActivity.putParcelableArrayListExtra("imagelist", imageDragableGridView.getUpdatedImageListWithoutPlus());
            iToBuyActivity.putExtra("index", position);
            iToBuyActivity.putExtra("vehicleName", "Create Blog");
            startActivity(iToBuyActivity);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.tvBlogType:
                if (blogTypes == null)
                {
                    if (HelperHttp.isNetworkAvailable(getActivity()))
                        getBlogType(true);
                    else
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                } else
                {
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
                }

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

                if (blog == null)
                    blog = new Blog();
                blog.setTitle(edtTitle.getText().toString().trim());
                blog.setCreatedDate(edtActive.getText().toString().trim());
                blog.setEndDate(edtEndDate.getText().toString().trim());
                blog.setName(edtAuthor.getText().toString().trim());
                blog.setAuthor(edtAuthor.getText().toString().trim());
                blog.setDetails(edtDetails.getText().toString().trim());
                blog.setGridImages(imageDragableGridView.getUpdatedImageListWithoutPlus());
                isPreview = false;

                if (HelperHttp.isNetworkAvailable(getActivity()))
                {
                    if (!active.isChecked())
                    {
                        CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.this_blog_is_not_active), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                if (type == Dialog.BUTTON_POSITIVE)
                                {
                                    saveBlogPost();
                                }
                            }
                        });
                    } else
                    {
                        saveBlogPost();
                    }
                } else
                {
                    HelperHttp.showNoInternetDialog(getActivity());
                }
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Permiso.getInstance().setActivity(getActivity());
        if (getArguments() == null)
            showActionBar(getActivity().getResources().getString(R.string.create_blog));
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
            /*if (blogTypes == null)
            {
				if(HelperHttp.isNetworkAvailable(getActivity()))
					getBlogType(false);
				else
					CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
			}*/
            if (blog == null)
            {
                showProgressDialog();
                if (HelperHttp.isNetworkAvailable(getActivity()))
                    getBlog();
                else
                    CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
            }
        } else
        {
            btnSave.setText("Save");
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

        StringBuilder soapMessage = new StringBuilder();
        soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapMessage.append("<Body>");
        soapMessage.append("<GetBlog xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
        soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
        soapMessage.append("<ID>" + getArguments().getInt("data") + "</ID>");
        soapMessage.append("</GetBlog>");
        soapMessage.append("</Body>");
        soapMessage.append("</Envelope>");

        VollyResponseListener vollyResponseListener = new VollyResponseListener()
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
                hideProgressDialog();
                if (response == null)
                    return;
                Helper.Log("TAG", "" + response);
                blog = ParserManager.parseGetBlog(response);
                if (blog != null)
                {
                    edtTitle.setText(Html.fromHtml(blog.getTitle()));
                    edtAuthor.setText(Html.fromHtml(blog.getAuthor()));
                    edtDetails.setText(Html.fromHtml(blog.getDetails()));
                    edtActive.setText(blog.getPublishDate());

                    edtEndDate.setText(blog.getEndDate().equals("31 Dec 1969") || (blog.getEndDate().equals("01 Jan 1970")) ? "" : blog.getEndDate());
                    active.setChecked(blog.isActive());

                    if (blog.getGridImages().size() > 0)
                        imageDragableGridView.setImageList(blog.getGridImages());

                    if (blogTypes != null)
                    {
                        blogType = getBlogPostType();
                        if (blogType != null)
                            tvBlogType.setText(blogType.getName());
                    } else
                    {
                        getBlogType(false);
                    }
                }

            }
        };

        VollyCustomRequest request = new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IBlogService/GetBlog", vollyResponseListener);

        try
        {
            request.init();
        } catch (Exception e1)
        {
            e1.printStackTrace();
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

        StringBuilder soapMessage = new StringBuilder();
        soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapMessage.append("<Body>");
        soapMessage.append("<GetBlogTypes xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
        soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
        soapMessage.append("</GetBlogTypes>");
        soapMessage.append("</Body>");
        soapMessage.append("</Envelope>");


        VollyResponseListener vollyResponseListener = new VollyResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError arg0)
            {
                if (showDialog)
                    hideProgressDialog();
            }

            @Override
            public void onResponse(String response)
            {
                hideProgressDialog();
                if (response == null)
                {
                    return;
                }
                ArrayList<BlogType> tempBlogTypes = ParserManager.parseBlogType(response);
                if (tempBlogTypes != null)
                {
                    if (!tempBlogTypes.isEmpty())
                    {
                        blogTypes.addAll(tempBlogTypes);
                    }
                }

                if (showDialog)
                {
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
                } else
                {
                    blogType = getBlogPostType();
                    if (blogType != null)
                        tvBlogType.setText(blogType.getName());
                }
            }
        };

        VollyCustomRequest request = new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IBlogService/GetBlogTypes", vollyResponseListener);
        try
        {
            request.init("getBlogType");
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    public void saveBlogPost()
    {
        SearchBlogFragment.isEdited = true;
        showProgressDialog();
        save();
    }

    private void save()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
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
                    try
                    {
                        if (result != null)
                        {
                            int blogPostId = 0;
                            if (TextUtils.isEmpty(result.toString()))
                            {
                                blogPostId = 0;
                            } else
                            {
                                blogPostId = Integer.parseInt(result.toString());
                            }

                            if (blogPostId > 0)
                            {
                                // check for image size
                                if (!blog.getGridImages().isEmpty())
                                {
                                    imageCount = blog.getGridImages().size();
                                    responseSize = imageCount;
                                    imageUploadIndex = 0;
                                    Helper.Log("Image count before saveBlogImage", "" + imageCount);
                                    saveBlogImage(blogPostId);
                                } else
                                {
                                    if (imageDragableGridView.isImageDeleted())
                                    {
                                        deleteImageCount = imageDragableGridView.getDeletedImages().size();
                                        imageDeleteIndex = 0;
                                        deleteImage();
                                    } else
                                    {
                                        hideProgressDialog();
                                        CustomDialogManager.showOkDialog(getActivity(), blogMessage, new DialogListener()
                                        {
                                            @Override
                                            public void onButtonClicked(int type)
                                            {
                                                if (isPreview == false)
                                                    getActivity().getFragmentManager().popBackStack();
                                                else
                                                {
                                                    getActivity().getFragmentManager().popBackStack();
                                                    getActivity().getFragmentManager().popBackStack();
                                                }
                                            }
                                        });
                                    }
                                }
                            } else
                            {
                                hideProgressDialog();
                                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.blog_not_saved_please_try));
                            }
                        } else
                        {
                            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.blog_not_saved_please_try));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
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
            } else
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
        if (blog.getGridImages().get(imageUploadIndex).isLocal())
        {
            base64String = Helper.convertBitmapToBase64(blog.getGridImages().get(imageUploadIndex).getPath());

            if (TextUtils.isEmpty(base64String))
            {
                imageUploadIndex++;
                responseSize--;
                if (imageUploadIndex == imageCount)
                    checkResponse();
                else
                    sendImagesToServerOrDataBase(blogPostId, isSaveDataBase);
            }

            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<SaveBlogImage xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");

            if (isSaveDataBase)
                soapMessage.append("<UserHash>" + Constants.CHANGE_THIS_USER_HASH + "</UserHash>");
            else
                soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");

            soapMessage.append("<BlogPostID>" + blogPostId + "</BlogPostID>");
            soapMessage.append("<cmUserID>" + DataManager.getInstance().user.getIdenttity() + "</cmUserID>");
            soapMessage.append("<ClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</ClientID>");
            soapMessage.append("<Priority>" + (imageUploadIndex + 1) + "</Priority>");
            soapMessage.append("<OriginalFileName>" + Helper.getFileName(blog.getGridImages().get(imageUploadIndex).getPath()) + "</OriginalFileName>");
            soapMessage.append("<Base64EncodedImage>" + base64String + "</Base64EncodedImage>");
            soapMessage.append("</SaveBlogImage>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            Log.d("SaveBlogImage request ", soapMessage.toString());

            // Check do we need to save in local database or send to server.
            if (isSaveDataBase)
            {
                SMDatabase myDatabase = new SMDatabase(getContext());
                myDatabase.insertRecords(soapMessage.toString(), Constants.SaveBlogImage);

                imageUploadIndex++;
                responseSize--;
                if (imageUploadIndex == imageCount)
                    checkResponse();
                else
                    sendImagesToServerOrDataBase(blogPostId, isSaveDataBase);

            } else
            {
                VollyResponseListener vollyResponseListener = new VollyResponseListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Helper.Log("Error: ", "" + error);
                        imageUploadIndex++;
                        responseSize--;
                        if (imageUploadIndex == imageCount)
                            checkResponse();
                        else
                            sendImagesToServerOrDataBase(blogPostId, isSaveDataBase);

                    }

                    @Override
                    public void onResponse(String response)
                    {
                        imageUploadIndex++;
                        responseSize--;
                        Helper.Log("TAG", "" + response);
                        if (imageUploadIndex == imageCount)
                            checkResponse();
                        else
                            sendImagesToServerOrDataBase(blogPostId, isSaveDataBase);
                    }
                };

                VollyCustomRequest request = new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IBlogService/SaveBlogImage", vollyResponseListener);
                try
                {
                    request.init("" + imageUploadIndex);
                } catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        } else
        {
            // no need to update image
            responseSize--;
            imageUploadIndex++;
            if (imageUploadIndex == imageCount)
                checkResponse();
            else
            {
                sendImagesToServerOrDataBase(blogPostId, isSaveDataBase);
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
                    imagePriotiyIndex = 0;
                    updateImagePriority();
                } else
                {
                    // check any image deleted
                    if (imageDragableGridView.isImageDeleted())
                    {
                        deleteImageCount = imageDragableGridView.getDeletedImages().size();
                        imageDeleteIndex = 0;
                        deleteImage();
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), blogMessage, new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                if (isPreview == false)
                                    getActivity().getFragmentManager().popBackStack();
                                else
                                {
                                    getActivity().getFragmentManager().popBackStack();
                                    getActivity().getFragmentManager().popBackStack();
                                }
                            }
                        });
                    }
                }
            } else
            {
                hideProgressDialog();
                CustomDialogManager.showOkDialog(getActivity(), blogMessage, new DialogListener()
                {

                    @Override
                    public void onButtonClicked(int type)
                    {
                        if (isPreview == false)
                            getActivity().getFragmentManager().popBackStack();
                        else
                        {
                            getActivity().getFragmentManager().popBackStack();
                            getActivity().getFragmentManager().popBackStack();
                        }
                    }
                });
            }
        }
    }

    private void updateImagePriority()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<UpdateBlogImagePriority xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<blogImageID>" + ((BaseImage) imageDragableGridView.getUpdatedImageListWithoutPlus().get(imagePriotiyIndex)).getId() + "</blogImageID>");
            soapMessage.append("<priority>" + (imagePriotiyIndex + 1) + "</priority>");
            soapMessage.append("</UpdateBlogImagePriority>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            Helper.Log("updateImagePriority " + imagePriotiyIndex, "" + soapMessage);

            VollyResponseListener listener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Helper.Log("VolleyError", "" + error.getMessage());
                    imagePriotiyIndex++;
                    if (imagePriotiyIndex == imageCount)
                        checkImagePriorityResponse();
                    else
                        updateImagePriority();

                }

                @Override
                public void onResponse(String response)
                {
                    Helper.Log("updateImagePriority response " + imagePriotiyIndex, "" + response);
                    imagePriotiyIndex++;
                    if (imagePriotiyIndex == imageCount)
                        checkImagePriorityResponse();
                    else
                        updateImagePriority();

                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IBlogService/UpdateBlogImagePriority", listener);
            try
            {
                request.init("updateImagePriority" + imagePriotiyIndex);
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } else
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
        } else
        {
            hideProgressDialog();
            CustomDialogManager.showOkDialog(getActivity(), blogMessage, new DialogListener()
            {
                @Override
                public void onButtonClicked(int type)
                {
                    if (isPreview == false)
                        getActivity().getFragmentManager().popBackStack();
                    else
                    {
                        getActivity().getFragmentManager().popBackStack();
                        getActivity().getFragmentManager().popBackStack();
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
                    getActivity().getFragmentManager().popBackStack();
                else
                {
                    getActivity().getFragmentManager().popBackStack();
                    getActivity().getFragmentManager().popBackStack();
                }
            }
        });
    }

    private void deleteImage()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<DeleteBlogImage xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<blogImageID>" + imageDragableGridView.getDeletedImages().get(imageDeleteIndex).getId() + "</blogImageID>");
            soapMessage.append("</DeleteBlogImage>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            VollyResponseListener listener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Helper.Log("VolleyError", "" + error.toString());
                    imageDeleteIndex++;
                    if (imageDeleteIndex == deleteImageCount)
                        checkImageDeleteResponse();
                    else
                        deleteImage();

                }

                @Override
                public void onResponse(String response)
                {
                    Helper.Log("TAG", "" + response);
                    imageDeleteIndex++;
                    if (imageDeleteIndex == deleteImageCount)
                        checkImageDeleteResponse();
                    else
                        deleteImage();

                }
            };


            VollyCustomRequest request = new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IBlogService/DeleteBlogImage", listener);
            try
            {
                request.init("DeleteBlogImage" + imageDeleteIndex);
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onAddOptionSelected()
    {
    }

    @Override
    public void onRemoveOptionSelected(int position)
    {
    }

    @Override
    public void onSaveClicked()
    {
        isPreview = true;
        saveBlogPost();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageDragableGridView != null)
        {
            if (imageDragableGridView.isOptionSelected())
                imageDragableGridView.onActivityResult(requestCode, resultCode, data);
        }
    }
}
