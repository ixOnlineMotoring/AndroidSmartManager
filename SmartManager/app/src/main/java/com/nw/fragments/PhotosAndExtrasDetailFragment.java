package com.nw.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.greysonparrelli.permiso.Permiso;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nw.adapters.GridImageAdapter.AddPhotoListener;
import com.nw.adapters.HorizontalListVideoAdapter;
import com.nw.broadcast.NetworkUtil;
import com.nw.database.SMDatabase;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.model.BaseImage;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.Vehicle;
import com.nw.model.YouTubeVideo;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.DragableGridView;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.activity.VehicleActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;
import com.utils.ImageHelper;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PhotosAndExtrasDetailFragment extends BaseFragement implements OnClickListener, AddPhotoListener
{

    Vehicle vehicle;
    TextView tvVehicleName;
    TextView tvRegNo, tvColor, tvDaysRemain;
    TextView tvStock, tvDepartment, tvMileage;
    TextView tvExtras, tvComments, tvPhotos, tvVideos, tvRetailPrice, tvTradePrice;
    Button btnSave, btnEdit;
    EditText edComments, edExtras;
    ArrayList<BaseImage> imageList;
    Uri fileUri, mImageCaptureUri;
    DragableGridView imageDragableGridView;
    HorizontalListView hlvphotosExtraCarVideos;
    RelativeLayout gridLayout;
    ImageView ivAddVideos;
    int noOfImagesInGrid = 0;
    int uploadImageCount = 0;
    int deleteImageCount = 0;
    int deleteVideoCount = 0;
    int uploadVideoCount = 0;
    int priorityImageCount = 0;
    boolean isFirstImage = true, isVideoSelected = false, isTextchanged = false;
    String videoPath = null, result;

    Bitmap fisrtBitmap = null, secondBitmap = null;
    VideoInfoFragment infoFragment;
    VideoPreviewFragment previewFragment;
    ListDetailsFragment listDetailsFragment;
    ArrayList<String> videoCodes = new ArrayList<>();
    ArrayList<YouTubeVideo> youTubeVideos, deletedVideos, uploaded_videos;
    DisplayImageOptions options;
    HorizontalListVideoAdapter horizontalListVideoAdapter;
    // 1 = Upload Now , 2 = Upload with wifi , 3 = default
    int showVideoAleart = 3;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Permiso.getInstance().setActivity(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.photosnextras_detail_fragment, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setHasOptionsMenu(true);
        CanUploadVideo();
        try
        {
            if (getArguments() != null)
            {
                if (getArguments().containsKey("selectedVehicle"))
                    vehicle = getArguments().getParcelable("selectedVehicle");
            }
            initialise(view);
            if (isVideoSelected)
            {
                videoloading(DataManager.getInstance().getYouTubeVideos());
            } else
            {
                if (infoFragment != null || previewFragment != null)
                {
                    videoloading(DataManager.getInstance().getYouTubeVideos());
                } else
                {
                    DataManager.getInstance().getYouTubeVideos().clear();
                }
            }
            if (deletedVideos == null)
            {
                deletedVideos = new ArrayList<YouTubeVideo>();
            }
            if (uploaded_videos == null)
            {
                uploaded_videos = new ArrayList<YouTubeVideo>();
            }
            if (vehicle != null)
            {
                putValues();
                if (isVideoSelected)
                {
                    imageDragableGridView.setImageList(DataManager.getInstance().getimageArray());
                    updateHeader();
                } else
                {
                    fetchCommentsAndExtras();
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            Helper.Log(getTag(), "" + e.getMessage());
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data), new DialogListener()
            {
                @Override
                public void onButtonClicked(int type)
                {
                    if (type == Dialog.BUTTON_POSITIVE)
                    {
                        hideKeyboard();
                        getActivity().getFragmentManager().popBackStack();
                    }
                }
            });
        }

        hideKeyboard(view);
        return view;
    }

    /*
     * Initialise views */
    private void initialise(View view)
    {
        tvVehicleName = (TextView) view.findViewById(R.id.tvVehicleName);
        tvRegNo = (TextView) view.findViewById(R.id.tvRegNumber);
        tvColor = (TextView) view.findViewById(R.id.tvColor);
        tvStock = (TextView) view.findViewById(R.id.tvStock);
        tvDepartment = (TextView) view.findViewById(R.id.tvDepartment);
        tvRetailPrice = (TextView) view.findViewById(R.id.tvRetailPrice);
        tvTradePrice = (TextView) view.findViewById(R.id.tvTradePrice);
        tvMileage = (TextView) view.findViewById(R.id.tvMileage);
        tvDaysRemain = (TextView) view.findViewById(R.id.tvRemainingDays);
        tvExtras = (TextView) view.findViewById(R.id.tvExtras);
        ivAddVideos = (ImageView) view.findViewById(R.id.ivAddVideos);
        tvComments = (TextView) view.findViewById(R.id.tvComments);

        hlvphotosExtraCarVideos = (HorizontalListView) view.findViewById(R.id.hlvphotosExtraCarVideos);

        hlvphotosExtraCarVideos.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                isVideoSelected = true;
                saveImagesToDataManager();
                videoPreview(DataManager.getInstance().getYouTubeVideos().get(position), position);
            }
        });

        tvPhotos = (TextView) view.findViewById(R.id.tvPhotos);
        tvVideos = (TextView) view.findViewById(R.id.tvVideos);
        edComments = (EditText) view.findViewById(R.id.edComments);
        edComments.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                isTextchanged = true;
            }
        });
        edExtras = (EditText) view.findViewById(R.id.edExtras);
        edExtras.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                isTextchanged = true;
            }
        });
        btnSave = (Button) view.findViewById(R.id.bSave);
        btnSave.setOnClickListener(this);
        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
        ivAddVideos.setOnClickListener(this);
        gridLayout = (RelativeLayout) view.findViewById(R.id.rlImage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //this code will be executed on devices running Marshmallow or later
            imageDragableGridView = new DragableGridView();
        } else
        {
            imageDragableGridView = new DragableGridView(getActivity());
        }    //image grid view implemented in imageDragableGridView
        imageDragableGridView.init(view, new ImageClickListener()
        {
            @Override
            public void onImageClick(int position)
            {
                try
                {
                    navigateToLargeImage(position);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onImageDeleted(int position)
            {
                updateHeader();
            }
        });

    }

    private void videoloading(ArrayList<YouTubeVideo> videos)
    {
        horizontalListVideoAdapter = new HorizontalListVideoAdapter(getActivity(), R.layout.grid_item_video, videos, true);
        hlvphotosExtraCarVideos.setAdapter(horizontalListVideoAdapter);

        horizontalListVideoAdapter.setAddPhotoListener(new HorizontalListVideoAdapter.AddPhotoListener()
        {
            @Override
            public void onAddOptionSelected()
            {
            }

            @Override
            public void onRemoveOptionSelected(final int postition)
            {
                CustomDialogManager.showOkCancelDialog(getContext(),
                        getContext().getString(R.string.are_you_sure_you_want_to_delete),
                        new DialogListener()
                        {

                            @Override
                            public void onButtonClicked(int type)
                            {
                                if (type == Dialog.BUTTON_POSITIVE)
                                {
                                    if (DataManager.getInstance().getYouTubeVideos().get(postition).isLocal())
                                    {
                                        isVideoSelected = false;
                                        DataManager.getInstance().getYouTubeVideos().remove(postition);
                                    } else
                                    {
                                        isVideoSelected = false;
                                        deletedVideos.add(DataManager.getInstance().getYouTubeVideos().get(postition));
                                        DataManager.getInstance().getYouTubeVideos().remove(postition);
                                    }
                                    horizontalListVideoAdapter.notifyDataSetChanged();
                                    updateHeader();
                                }
                            }
                        });
            }
        });
    }

    /*
     * put values to views with object from previous screen*/
    private void putValues()
    {
        tvVehicleName.setText(Html.fromHtml("<font color=#ffffff>" + vehicle.getYear() + "</font> " + vehicle.getFriendlyName()));
        tvRegNo.setText(vehicle.getRegNumber());
        tvRegNo.append(" | " + vehicle.getColour());
        tvRegNo.append(" | " + Helper.getSubStringBeforeString(vehicle.getStockNumber(), "<br/>"));
        tvDepartment.setText(vehicle.getDepartment());
        if (Helper.formatPrice(new BigDecimal(vehicle.getTradePrice()) + "").equals("R0"))
        {
            tvTradePrice.setText("R?");
        } else
        {
            tvTradePrice.setText(Helper.formatPrice(new BigDecimal(vehicle.getTradePrice()) + ""));
        }
        if (Helper.formatPrice(new BigDecimal(vehicle.getRetailPrice()) + "").equals("R0"))
        {
            tvRetailPrice.setText("R?");
        } else
        {
            tvRetailPrice.setText(Helper.formatPrice(new BigDecimal(vehicle.getRetailPrice()) + ""));
        }
        if (Helper.getFormattedDistance(vehicle.getMileage() + "").equals("0"))
        {
            tvDepartment.append(" | Mileage?");
        } else
        {
            tvDepartment.append(" | " + Helper.getFormattedDistance(vehicle.getMileage() + "") + " Km");
        }
        tvDepartment.append(Html.fromHtml("| <font color=#3476BE> " + vehicle.getExpires() + " Days</font>"));
        if (vehicle.getExtras().equals("True"))
        {
            tvExtras.setText(Html.fromHtml("<font color=#ffffff>Extras</font> <font color=" + getResources().getColor(R.color.green) + ">\u2713</font>"));
        } else
        {
            tvExtras.setText(Html.fromHtml("<font color=#ffffff>Extras</font> <font color=" + getResources().getColor(R.color.red) + ">\u2718</font>"));
        }
        if (vehicle.getComments().equals("True"))
            tvExtras.append(Html.fromHtml("<font color=#ffffff> &emsp; &emsp; | Comments</font> <font color=" + getResources().getColor(R.color.green) + ">\u2713</font>"));
        else
        {
            tvExtras.append(Html.fromHtml("<font color=#ffffff> &emsp; &emsp; | Comments</font> <font color=" + getResources().getColor(R.color.red) + ">\u2718</font>"));
        }
        tvExtras.append(Html.fromHtml("<font color=#ffffff> &emsp; &emsp; | Photos </font> <font color=" + getResources().getColor(R.color.dark_blue) + ">" + vehicle.getNumOfPhotos() + "</font>"));
        tvExtras.append(Html.fromHtml("<font color=#ffffff> &emsp; &emsp; | Videos </font> <font color=" + getResources().getColor(R.color.dark_blue) + ">" + vehicle.getNumOfVideos() + "</font>"));


    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnEdit:
                listDetailsFragment = new ListDetailsFragment();
                Bundle args = new Bundle();
                args.putString("vehicleName", vehicle.getFriendlyName());
                args.putInt("stockID", vehicle.getID());
                listDetailsFragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.Container, listDetailsFragment).addToBackStack(null).commit();
                break;

            case R.id.bSave:
                noOfImagesInGrid = 0;
                uploadImageCount = 0;
                deleteImageCount = 0;
                priorityImageCount = 0;
                deleteVideoCount = 0;
                // call web service to save comments and extras
                showProgressDialog();
                savePhotosAndExtras(vehicle.getID());
                break;

            case R.id.ivAddVideos:
                int total_local_video = 0;
                for (int i = 0; i < DataManager.getInstance().getYouTubeVideos().size(); i++)
                {
                    if (DataManager.getInstance().getYouTubeVideos().get(i).isLocal())
                    {
                        total_local_video++;
                    }
                }
                if (total_local_video < 2)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                && getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                                && getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 100);
                            return;
                        }
                    }
                    if (DataManager.getInstance().isClientSetUptoUploadVideo)
                    {
                        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult()
                        {
                            @Override
                            public void onPermissionResult(Permiso.ResultSet resultSet)
                            {
                                if (resultSet.areAllPermissionsGranted())
                                {
                                    Helper.getVideoFromGalleryOrCamera(getActivity());
                                } else
                                {
                                    CustomDialogManager.showOkCancelDialog(getActivity(), "Please accept all permissions" +
                                            " for proper functioning of app", "Ok", "Cancel", new DialogListener()
                                    {
                                        @Override
                                        public void onButtonClicked(int type)
                                        {
                                            switch (type)
                                            {
                                                case Dialog.BUTTON_POSITIVE:
                                                    ivAddVideos.performClick();
                                                    break;
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions)
                            {

                            }
                        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA);
                    } else
                    {
                        CustomDialogManager.showErrorDialogEmail(getActivity(), "You are not activated to upload videos.Please contact support@ix.co.za");
                    }
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK)
        {
            if (listDetailsFragment != null)
            {
                listDetailsFragment.onActivityResult(requestCode, resultCode, data);
            }
            if (imageDragableGridView != null)
            {
                if (imageDragableGridView.isOptionSelected())
                    imageDragableGridView.onActivityResult(requestCode, resultCode, data);
            }
            if (requestCode == Constants.VIDEO_RECORDING_CUSTOM)
            {
                result = data.getStringExtra("result");
                if (result != null)
                    isVideoSelected = true;
                videoInfoScreen(result);
            } else if (requestCode == Constants.VIDEO_GALLERY)
            {
                Uri selectedVideo = data.getData();
                Helper.Log("Select Gallery : ", "videoUri= " + selectedVideo);

                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT)
                {
                    videoPath = ImageHelper.getVideoPathFromGalleryAboveKitkat(getActivity(), selectedVideo);
                } else
                {
                    videoPath = ImageHelper.getVideoRealPathFromURI(getActivity(), selectedVideo);
                }


                if (TextUtils.isEmpty(videoPath))
                {
                    videoPath = ImageHelper.getPath(getActivity(), selectedVideo);
                }
                try
                {
                    if (videoPath != null)
                    {
                        File file = new File(videoPath);
                        long length = file.length();
                        length = length / 1024;
                        System.out.println("File Path : " + file.getPath() + " Size:" + length / 1000 + " MB");
                        if (length == 0)
                        {
                            // Show Your Messages
                            Helper.showToast("Video is corrupted,can't upload", getActivity());
                            return;
                        } else if ((length / 1000) > 50)
                        {
                            // Show Your Messages

                            Helper.showToast("Video size is more than 50 MB.", getActivity());
                            return;
                        } else
                        {

                            if (videoPath != null)
                                isVideoSelected = true;
                            videoInfoScreen(videoPath);
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
      /*  if (imageDragableGridView != null)
        {
            if (imageDragableGridView.isOptionSelected())
                imageDragableGridView.onActivityResult(requestCode, resultCode, data);
        }*/
        updateHeader();
    }

	/*
     * Navigate to video PREVIEW screen fragment
	 * As Fragment to called belongs to other activity we call activity and pass parameters through intent
	 * Parameter- position of image clicked
	 * */

    private void videoPreview(YouTubeVideo youTubeVideo, int position)
    {
        saveImagesToDataManager();

        previewFragment = new VideoPreviewFragment();
        Bundle args = new Bundle();
        args.putParcelable("videoToPreview", youTubeVideo);
        args.putInt("position", position);
        previewFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Container, previewFragment).addToBackStack("").commit();

    }

    private void saveImagesToDataManager()
    {
        if (listDetailsFragment == null)
        {
            // To save the images in data manager.
            if (imageDragableGridView != null)
                DataManager.getInstance().setimageArray(imageDragableGridView.getUpdatedImageListWithoutPlus());
        }
    }

	/*
     * Navigate to video info screen fragment
	 * As Fragment to called belongs to other activity we call activity and pass parameters through intent
	 * Parameter- position of image clicked
	 * */

    private void videoInfoScreen(String videoPath)
    {
        saveImagesToDataManager();

        if (listDetailsFragment == null)
        {
            infoFragment = new VideoInfoFragment();
            Bundle args = new Bundle();
            args.putString("videoPath", videoPath);
            args.putString("default_name", tvVehicleName.getText().toString() + "-" + tvStock.getText().toString());
            infoFragment.setArguments(args);
             getFragmentManager().beginTransaction().replace(R.id.Container, infoFragment).addToBackStack("").commit();
        }

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
            iToBuyActivity.putExtra("vehicleName", vehicle.getFriendlyName());
            startActivity(iToBuyActivity);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     * As soon as changes are saved we need to update header views
     * */
    private void updateHeader()
    {
        tvPhotos.setText(Html.fromHtml("<font color=#ffffff>Photos </font> <font color=" + getResources().getColor(R.color.dark_blue) + ">" + imageDragableGridView.getUpdatedImageListWithoutPlus().size() + "</font>"));
        tvVideos.setText(Html.fromHtml("<font color=#ffffff>Videos </font> <font color=" + getResources().getColor(R.color.dark_blue) + ">" + DataManager.getInstance().getYouTubeVideos().size() + "</font>"));
        if (edExtras.getText().toString().trim().equals(""))
            tvExtras.setText(Html.fromHtml("<font color=#ffffff>Extras</font> <font color=" + getResources().getColor(R.color.red) + ">\u2718</font>"));
        else
            tvExtras.setText(Html.fromHtml("<font color=#ffffff>Extras</font> <font color=" + getResources().getColor(R.color.green) + ">\u2713</font>"));
        if (edComments.getText().toString().trim().equals(""))
            tvComments.setText(Html.fromHtml("<font color=#ffffff>Comment</font> <font color=" + getResources().getColor(R.color.red) + ">\u2718</font>"));
        else
            tvComments.setText(Html.fromHtml("<font color=#ffffff>Comment</font> <font color=" + getResources().getColor(R.color.green) + ">\u2713</font>"));
    }

    private void CanUploadVideo()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<CanUploadVideo xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapMessage.append("<clientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</clientID>");
            soapMessage.append("</CanUploadVideo>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            VollyResponseListener listener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Helper.showToast(getString(R.string.error_getting_data), getActivity());
                    VolleyLog.e("Error: ", error.toString());
                }

                @Override
                public void onResponse(String response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    Helper.Log("CanUploadVideo", "" + response);
                    if (response.contains("true"))
                    {
                        DataManager.getInstance().isClientSetUptoUploadVideo = true;
                    } else
                    {
                        DataManager.getInstance().isClientSetUptoUploadVideo = false;
                    }
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/CanUploadVideo", listener);
            try
            {
                request.init();
            } catch (Exception e1)
            {
                DataManager.getInstance().isClientSetUptoUploadVideo = false;
                e1.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    /* Web service integration to save comments and extras
     * */
    private void savePhotosAndExtras(int vehicleId)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("usedVehicleStockID", vehicleId, Integer.class));
            parameterList.add(new Parameter("comments", edComments.getText().toString().trim(), String.class));
            parameterList.add(new Parameter("extras", edExtras.getText().toString().trim(), String.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("UpdateVehicleExtrasAndComments");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/UpdateVehicleExtrasAndComments");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);


            // Network call
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("Response", result.toString());
                        if (result.toString().equalsIgnoreCase("ok"))
                        {
                            isTextchanged = false;
                            Helper.Log("response", result.toString());
                            if (imageDragableGridView.getUpdatedImageListWithoutPlus().isEmpty())
                            {
                                if (imageDragableGridView.getDeletedImages().isEmpty())
                                {
                                    showSuccessDialog();
                                } else
                                {
                                    noOfImagesInGrid = imageDragableGridView.getUpdatedImageListWithoutPlus().size();
                                    checkDelete();
                                }
                            } else
                            {
                                noOfImagesInGrid = imageDragableGridView.getUpdatedImageListWithoutPlus().size();
                                addImageToList();
                            }
                        }
                    } catch (Exception e)
                    {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private class videoUpload extends AsyncTask<Void, Void, String>
    {
        String VideoName;
        SMDatabase myDatabase = new SMDatabase(getContext());
        boolean saveInDataBase = false;

        // save in database or not
        public videoUpload(boolean b)
        {
            saveInDataBase = b;
        }

        @Override
        protected void onPreExecute()
        {
            if (videoCodes.size() == uploaded_videos.size())
            {
                return;
            }
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            for (int i = uploadVideoCount; i < DataManager.getInstance().getYouTubeVideos().size();
                 i++)
            {
                if (DataManager.getInstance().getYouTubeVideos().get(i).isLocal())
                {
                    if (saveInDataBase)
                    {
                        // Save in database
                        myDatabase.insertVideoUploadingRecords(DataManager.getInstance().getYouTubeVideos().get(i).getVideoFullPath(),
                                "" + vehicle.getID(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_title(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Description(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Tags(),
                                "" + DataManager.getInstance().getYouTubeVideos().get(i).isSearchable());
                        VideoName = "Success";
                    } else
                    {
                        VideoName = HelperHttp.uploadVideoFile(new File(DataManager.getInstance().getYouTubeVideos().get(i).getVideoFullPath()),
                                vehicle.getID(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_title(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Description(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Tags(),
                                DataManager.getInstance().getYouTubeVideos().get(i).isSearchable());
                        if (VideoName != null)
                        {
                            videoCodes.add(VideoName);
                            uploaded_videos.add(DataManager.getInstance().getYouTubeVideos().get(uploadVideoCount));
                        }
                    }
                }
            }
            return VideoName;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if (result != null)
            {
                if (videoCodes.size() == uploaded_videos.size())
                {
                    isVideoSelected = false;
                    DataManager.getInstance().getYouTubeVideos().clear();
                    uploaded_videos.clear();
                    showSuccessDialog();
                } else
                {
                    uploadVideoCount++;
                    DataManager.getInstance().getYouTubeVideos().remove(uploadImageCount);
                    new videoUpload(saveInDataBase).execute();
                }
            } else
            {
                if (isVideoSelected)
                {
                    hideProgressDialog();
                    CustomDialogManager.showOkDialog(getActivity(), getString(R.string.vehicles_updated_successfully_video_failed),
                            new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    getActivity().getFragmentManager().popBackStack();
                                }
                            });
                }
            }
        }
    }

    // web service call to fetch comments and extras also image list
    private void fetchCommentsAndExtras()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showLoadingProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<LoadVehicleDetailsXML xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapMessage.append("<usedVehicleStockID>" + vehicle.getID() + "</usedVehicleStockID>");
            soapMessage.append("</LoadVehicleDetailsXML>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            VollyResponseListener listener = new VollyResponseListener()
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
                    {
                        return;
                    }
                    Helper.Log("LoadVehicleDetailsXML", "" + response);
                    edComments.setText(ParserManager.parseComments(response));
                    edExtras.setText(ParserManager.parseExtras(response));
                    if (imageList == null)
                    {
                        imageList = ParserManager.parseImageList(response);
                    }
                    imageDragableGridView.setImageList(imageList);
                    if (youTubeVideos == null)
                    {
                        youTubeVideos = ParserManager.parseVideoList(response);
                        videoloading(DataManager.getInstance().getYouTubeVideos());
                    } else
                    {
                        videoloading(DataManager.getInstance().getYouTubeVideos());
                    }
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/LoadVehicleDetailsXML", listener);
            try
            {
                request.init();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    // web service call to change priority of images
    private void changeImagePriority()
    {

        if (noOfImagesInGrid == priorityImageCount)
        {
            checkDelete();
        } else
        {
            if (HelperHttp.isNetworkAvailable(getActivity()))
            {
                // Add parameters to request in arraylist
                ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
                parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
                parameterList.add(new Parameter("usedVehicleStockID", vehicle.getID(), Integer.class));
                parameterList.add(new Parameter("imageID", imageDragableGridView.getUpdatedImageListWithoutPlus().get(priorityImageCount).getId(), Integer.class));
                parameterList.add(new Parameter("newPriorityID", priorityImageCount + 1, Integer.class));

                // create web service inputs
                DataInObject inObj = new DataInObject();
                inObj.setMethodname("ChangeImagePriorityForVehicle");
                inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ChangeImagePriorityForVehicle");
                inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
                inObj.setParameterList(parameterList);

                // Network call
                new WebServiceTask(getActivity(), inObj, false, new TaskListener()
                {

                    @Override
                    public void onTaskComplete(Object result)
                    {
                        try
                        {
                            Helper.Log("soap Response", result.toString());
                            priorityImageCount++;
                            if (priorityImageCount == noOfImagesInGrid)
                            {
                                checkDelete();
                            } else
                            {
                                changeImagePriority();
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).execute();
            } else
            {
                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
            }
        }
    }

    private void checkDelete()
    {
        if (imageDragableGridView.getDeletedImages().isEmpty())
        {
            showSuccessDialog();
        } else
        {
            removeImageFromList();
        }
    }

    // web service call to delete images.
    private void removeImageFromList()
    {
        if (deleteImageCount == imageDragableGridView.getDeletedImages().size())
        {
            showSuccessDialog();
        } else
        {
            if (HelperHttp.isNetworkAvailable(getActivity()))
            {
                // Add parameters to request in arraylist
                ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
                parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
                parameterList.add(new Parameter("usedVehicleStockID", vehicle.getID(), Integer.class));
                parameterList.add(new Parameter("imageID", imageDragableGridView.getDeletedImages().get(deleteImageCount).getId(), Integer.class));

                // create web service inputs
                DataInObject inObj = new DataInObject();
                inObj.setMethodname("RemoveImageFromVehicle");
                inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/RemoveImageFromVehicle");
                inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
                inObj.setParameterList(parameterList);

                // Network call
                new WebServiceTask(getActivity(), inObj, false, new TaskListener()
                {

                    @Override
                    public void onTaskComplete(Object result)
                    {
                        try
                        {
                            Helper.Log("soap Response", result.toString());
                            deleteImageCount++;
                            if (deleteImageCount == imageDragableGridView.getDeletedImages().size())
                            {
                                showSuccessDialog();
                            } else
                            {
                                removeImageFromList();
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).execute();
            } else
            {
                CustomDialogManager.showOkDialog(getActivity(),
                        getString(R.string.no_internet_connection));
            }
        }
    }

    private void RemoveVideoLinkFromVehicle(final ArrayList<YouTubeVideo> videos)
    {
        showProgressDialog();
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<UnLinkVideo xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapMessage.append("<clientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</clientID>");
            soapMessage.append("<linkID>" + videos.get(deleteVideoCount).getVideoLinkID() + "</linkID>");
            soapMessage.append("</UnLinkVideo>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            VollyResponseListener listener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Helper.showToast(getString(R.string.error_getting_data), getActivity());
                    VolleyLog.e(" UnLinkVideo Error: ", error.toString());
                }

                @Override
                public void onResponse(String response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    //hideProgressDialog();
                    Helper.Log("UnLinkVideo", "" + response);
                    if (response.contains("Link removed"))
                    {
                        if (videos.size() > 0)
                        {
                            videos.remove(deleteVideoCount);
                            //deleteVideoCount++;
                            if (videos.size() != 0)
                            {
                                RemoveVideoLinkFromVehicle(deletedVideos);
                            } else
                            {
                                deletedVideos.clear();
                                showSuccessDialog();
                            }
                        } else
                        {
                            deletedVideos.clear();
                            showSuccessDialog();
                        }
                    }
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/UnLinkVideo", listener);
            try
            {
                request.init();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    // web service call to upload images
    private void addImageToList()
    {

        showVideoAleart = 3;

        // Check is on wifi or mobile network
        if (NetworkUtil.getConnectivityStatusString(getActivity()) == ConnectivityManager.TYPE_WIFI)
        {
            sendImagesToServerOrDataBase(false);
        } else
        {
            boolean showImageAleart = false;

            // Check is local Image available .
            if (imageDragableGridView.getLocalImageListWithoutPlus().size() > 0)
            {
                showImageAleart = true;
            }

            /*// Check is local video available .
            for (int i = 0; i < DataManager.getInstance().getYouTubeVideos().size(); i++)
            {
                if (DataManager.getInstance().getYouTubeVideos().get(i).isLocal())
                {
                    showImageAleart = true;
                }
            }*/

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
                                showVideoAleart = 1;
                                sendImagesToServerOrDataBase(false);
                                break;
                            // Upload With WIFI
                            case Dialog.BUTTON_NEGATIVE:
                                showVideoAleart = 2;
                                sendImagesToServerOrDataBase(true);
                                break;
                        }
                    }
                });
            } else
            {
                sendImagesToServerOrDataBase(false);
            }
        }
    }

    /**
     * This Method is used to send selected images to server.
     *
     * @param isSaveDataBase = if true (Save to local database) if false (send directly to server on any network)
     */
    private void sendImagesToServerOrDataBase(final boolean isSaveDataBase)
    {
        if (!imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).isLocal())
        {
            uploadImageCount++;
            if (noOfImagesInGrid == uploadImageCount)
                checkImagePriority();
            else
                sendImagesToServerOrDataBase(isSaveDataBase);
        } else
        {
            String base64String = Helper.convertBitmapToBase64(imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).getPath());

            if (TextUtils.isEmpty(base64String))
            {
                uploadImageCount++;
                if (noOfImagesInGrid == uploadImageCount)
                    checkImagePriority();
                else
                    sendImagesToServerOrDataBase(isSaveDataBase);
            }
            if (HelperHttp.isNetworkAvailable(getActivity()))
            {
                if (isSaveDataBase)
                {
                    StringBuilder soapMessage = new StringBuilder();
                    soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                    soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
                    soapMessage.append("<Body>");
                    soapMessage.append("<AddImageToVehicleBase64 xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
                    soapMessage.append("<userHash>" + Constants.CHANGE_THIS_USER_HASH + "</userHash>");
                    soapMessage.append("<usedVehicleStockID>" + vehicle.getID() + "</usedVehicleStockID>");
                    soapMessage.append("<imageBase64>" + base64String + "</imageBase64>");
                    soapMessage.append("<imageName>" + Helper.getNameWithExtenstion(imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).getPath()) + "</imageName>");
                    soapMessage.append("<imageTitle>" + Helper.getName(imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).getPath()) + "</imageTitle>");
                    soapMessage.append("<imageSource>" + "phone app" + "</imageSource>");
                    soapMessage.append("<imagePriority>" + (uploadImageCount + 1) + "</imagePriority>");
                    soapMessage.append("<imageIsEtched>" + false + "</imageIsEtched>");
                    soapMessage.append("<imageIsBranded>" + false + "</imageIsBranded>");
                    soapMessage.append("<imageAngle>" + "" + "</imageAngle>");
                    soapMessage.append("</AddImageToVehicleBase64>");
                    soapMessage.append("</Body>");
                    soapMessage.append("</Envelope>");

                    // Save in local Database
                    SMDatabase myDatabase = new SMDatabase(getContext());
                    myDatabase.insertRecords(soapMessage.toString(), Constants.SavePhotosAndExtrasImage);
                    try
                    {
                        Helper.Log("soap Response", soapMessage.toString());
                        uploadImageCount++;
                        if (uploadImageCount == noOfImagesInGrid)
                        {
                            checkImagePriority();
                        } else
                        {
                            sendImagesToServerOrDataBase(isSaveDataBase);
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                } else
                {
                    ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
                    parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
                    parameterList.add(new Parameter("usedVehicleStockID", vehicle.getID(), Integer.class));
                    parameterList.add(new Parameter("imageBase64", base64String, String.class));
                    parameterList.add(new Parameter("imageName",
                            Helper.getNameWithExtenstion(imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).getPath()), String.class));
                    parameterList.add(new Parameter("imageTitle",
                            Helper.getName(imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).getPath()), String.class));
                    parameterList.add(new Parameter("imageSource", "phone app", String.class));
                    parameterList.add(new Parameter("imagePriority", (uploadImageCount + 1),
                            Integer.class));
                    parameterList.add(new Parameter("imageIsEtched", false, Boolean.class));
                    parameterList.add(new Parameter("imageIsBranded", false, Boolean.class));
                    parameterList.add(new Parameter("imageAngle", "", String.class));

                    // create web service inputs
                    DataInObject inObj = new DataInObject();
                    inObj.setMethodname("AddImageToVehicleBase64");
                    inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
                    inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/AddImageToVehicleBase64");
                    inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
                    inObj.setParameterList(parameterList);

                    // Network call
                    new WebServiceTask(getActivity(), inObj, false, new TaskListener()
                    {
                        @Override
                        public void onTaskComplete(Object result)
                        {
                            try
                            {
                                Helper.Log("soap Response", result.toString());
                                uploadImageCount++;
                                if (uploadImageCount == noOfImagesInGrid)
                                {
                                    checkImagePriority();
                                } else
                                {
                                    sendImagesToServerOrDataBase(isSaveDataBase);
                                }
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }).execute();
                }
            } else
            {
                HelperHttp.showNoInternetDialog(getActivity());
            }
        }
    }

    private void checkImagePriority()
    {
        if (imageDragableGridView.isPriorityChanged())
        {
            changeImagePriority();
        } else
        {
            checkDelete();
        }
    }

    private void showSuccessDialog()
    {
        updateHeader();

        if (isVideoSelected)
        {
            if (showVideoAleart == 3)
                showVideoAleart = 1;
            uploadWarning();
        } else
        {
            if (deletedVideos.size() != 0)
            {
                RemoveVideoLinkFromVehicle(deletedVideos);
            } else
            {
                hideProgressDialog();
                VehicleActivity.isVehicleUpdated = true;
                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.vehicles_updated_successfully), new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        if (type == Dialog.BUTTON_POSITIVE)
                        {
                            vehicle.setNumOfPhotos(imageDragableGridView.getUpdatedImageListWithoutPlus().size());
                            hideKeyboard();
                            getActivity().getFragmentManager().popBackStack();
                        }
                    }
                });
            }
        }
    }

    private void uploadWarning()
    {
        long total_video_size = 0;

        ConnectivityManager connManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mWifi.isConnected())
        {
            new videoUpload(false).execute();
        }

        if (mMobile.isConnected())
        {
            for (int i = 0; i < DataManager.getInstance().getYouTubeVideos().size(); i++)
            {
                long length = 0;
                if (DataManager.getInstance().getYouTubeVideos().get(i).isLocal())
                {
                    File file = new File(DataManager.getInstance().getYouTubeVideos().get(i).getVideoFullPath());
                    length = file.length();
                    length = length / 1024;
                }
                total_video_size = total_video_size + length;
            }

            if (showVideoAleart == 1)
            {
                CustomDialogManager.showOkCancelDialog(getActivity(), "It is recommended that you connect to " +
                        "a WiFi network to upload video files of size " + (total_video_size / 1024) +
                        "MB, to avoid excessive data use." +
                        " Do you want to:", getString(R.string.Upload_Now), getString(R.string.upload_with_wifi), new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        if (type == Dialog.BUTTON_POSITIVE)
                        {
                            new videoUpload(false).execute();
                        } else
                        {
                            VehicleActivity.isVehicleUpdated = true;
                            vehicle.setNumOfPhotos(imageDragableGridView.getUpdatedImageListWithoutPlus().size());
                            new videoUpload(true).execute();

                          /*  hideProgressDialog();
                            VehicleActivity.isVehicleUpdated = true;
                            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.vehicles_updated_successfully), new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    if (type == Dialog.BUTTON_POSITIVE)
                                    {
                                        vehicle.setNumOfPhotos(imageDragableGridView.getUpdatedImageListWithoutPlus().size());
                                        hideKeyboard();
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }
                                }
                            });*/
                        }
                    }
                });
            } else
            {
                VehicleActivity.isVehicleUpdated = true;
                vehicle.setNumOfPhotos(imageDragableGridView.getUpdatedImageListWithoutPlus().size());
                new videoUpload(true).execute();
            }

        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar(vehicle.getFriendlyName());
        Permiso.getInstance().setActivity(getActivity());
    }

    public void onBackPressed()
    {
        if (infoFragment != null && infoFragment.isVisible())
        {
            infoFragment.onBackPressed();
        }
        if (previewFragment != null && previewFragment.isVisible())
        {
            previewFragment.onBackPressed();
        }
        if (isVideoSelected)
        {
            CustomDialogManager.showOkCancelDialog(getActivity(),
                    "You have not saved the information.Are you sure want to continue?", new DialogListener()
                    {
                        @Override
                        public void onButtonClicked(int type)
                        {
                            if (type == Dialog.BUTTON_POSITIVE)
                            {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        }
                    });
        } else
        {
            getActivity().getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                hideKeyboard();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAddOptionSelected()
    {
    }

    @Override
    public void onRemoveOptionSelected(int postition)
    {
    }

    public interface UpdateVehicleList
    {
        public void updateList(Boolean flag);

    }

	/*private void displayShowcaseView(){
        if(!ShowcaseSessions.isSessionAvailable(getActivity(), PhotosAndExtrasDetailFragment.class.getSimpleName())){
			ArrayList<TargetView> viewList= new ArrayList<TargetView>();
			viewList.add(new TargetView(gridLayout, ShowCaseType.Left, getString(R.string.to_view_large_image_tap_on_the_image)));
			ShowcaseLayout showcaseView = new ShowcaseLayout(getActivity());
			showcaseView.setShowcaseView(viewList);
			
			((ViewGroup)getActivity().getWindow().getDecorView()).addView(showcaseView);
			ShowcaseSessions.saveSession(getActivity(), PhotosAndExtrasDetailFragment.class.getSimpleName());
		}
	}*/

}
