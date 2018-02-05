package com.nw.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nw.adapters.HorizontalListVideoAdapter;
import com.nw.adapters.HorizontalListViewAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.model.BaseImage;
import com.nw.model.DataInObject;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.model.Variant;
import com.nw.model.Vehicle;
import com.nw.model.VehicleDetails;
import com.nw.model.VehicleImage;
import com.nw.model.YouTubeVideo;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomCheckBox;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.nw.widget.DragableGridView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;
import com.utils.ImageHelper;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Vector;

public class StocksDetailFragment extends BaseFragement implements OnClickListener, OnCheckedChangeListener
{
    NetworkImageView ivItemStockList;
    TextView tvTitleItemStockList, tvDistanceItemStockList, tvColorItemStockList, tvStockCodeItem, tvStockRegistration,
            tvMyBidItemStockList, tvTimeLeftItemStockList;
    TextView tvRetailPriceStockList, tvTradePriceStockList, tvInternalMemo, tvVehicleSpecs, tvCharacterCount;
    HorizontalListView hlvStockCarImages, hlvStockCarVideos;
    TextView tvStockExtras, tvStockComment, tvStockType;
    FrameLayout flItemStockBig;
    HorizontalListViewAdapter horizontalListViewAdapter;
    ArrayList<MyImage> images = new ArrayList<MyImage>(), tempList = new ArrayList<MyImage>();
    ImageLoader imageLoader;
    Vehicle vehicle;
    Variant variant;
    CustomEditText etEmailAddress, etFirstName, etLastname, etMobileNumber, etCommentStock;
    CustomButton btnSend;
    String message = "", personalizedImageListToken = "", department = "Used";
    LinearLayout llEmailList, llSeeMore, llayout_SeeMoreInfo, llayout_Varant_Details, llayout_eBroucher_Details;
    ImageView ivArrow;
    CustomCheckBox cbSendPhotos, cbSendVideos, cbSendPhotoPersonlised, cbSendVideoPersonlised;
    RelativeLayout rlImage, rlImage1, rlImage2;
    DragableGridView imageDragableGridView;
    //Video component for device videos and server videos
    boolean isVideoSelected = false, isImagesSending = false, isVideoSending = false, isVideoUploadFail = false;
    LinearLayout llImage1, llImage2, llSendPhotos, llSendVideos, llSpecs, llInternalMemo;
    ImageView ivAddImage, ivPicture, ivPicture1, ivClose, ivClose1;
    RelativeLayout rlVideo;
    String videoPath = null, result;
    Bitmap fisrtBitmap = null, secondBitmap = null;
    VideoInfoFragment infoFragment;
    VideoPreviewFragment previewFragment;
    ArrayList<YouTubeVideo> uploadedVideos, uploaded_video;
    DisplayImageOptions options;
    VollyCustomRequest request;
    VehicleDetails vehicleDetails;
    int noOfImagesInGrid = 0;
    int uploadImageCount = 0;
    int uploadVideoCount = 0;

    ArrayList<String> videoCodes = new ArrayList<String>();
    boolean isFromVariantList = false;

    TextView tvTitle, tvSubTitle, tvRetailPrice;
    String operationType ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_stock_details, container, false);
        setHasOptionsMenu(true);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        initView(view);
        CanUploadVideo();
        if (uploadedVideos == null)
        {
            uploadedVideos = new ArrayList<YouTubeVideo>();
        }
        try
        {
            if (getArguments() != null)
            {
                if (getArguments().containsKey("selectedVehicle"))
                {
                    isFromVariantList = false;
                    vehicle = getArguments().getParcelable("selectedVehicle");
                } else if (getArguments().containsKey("varientDetails"))
                {
                    isFromVariantList = true;
                    variant = getArguments().getParcelable("varientDetails");
                }
                if(getArguments().containsKey("operationType"))
                {
                    operationType = getArguments().getString("operationType");
                }

            }

            if (vehicle != null)
            {
                putValues();
            }

            if (variant != null)
            {
                putValues();
            }

            if (isVideoSelected)
            {
                videoloading(DataManager.getInstance().getYouTubeVideos());
                imageDragableGridView.setImageList(DataManager.getInstance().getimageArray());

            } else
            {
                if (infoFragment != null)
                {
                    videoloading(DataManager.getInstance().getYouTubeVideos());
                    imageDragableGridView.setImageList(DataManager.getInstance().getimageArray());
                } else
                {
                    /*DataManager.getInstance().getYouTubeVideos().clear();
                    DataManager.getInstance().getimageArray().clear();*/
                }
            }
            if (uploaded_video == null)
            {
                uploaded_video = new ArrayList<YouTubeVideo>();
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
                    getFragmentManager().popBackStack();
                }
            });
        }
        return view;
    }

    private void initView(View view)
    {
        etEmailAddress = (CustomEditText) view.findViewById(R.id.etEmailIdStock);
        etFirstName = (CustomEditText) view.findViewById(R.id.etFirstName);
        etLastname = (CustomEditText) view.findViewById(R.id.etLastName);
        etMobileNumber = (CustomEditText) view.findViewById(R.id.etMobileNumber);
        etCommentStock = (CustomEditText) view.findViewById(R.id.etMessage);
        tvCharacterCount = (TextView) view.findViewById(R.id.tvCharacterCount);
        tvCharacterCount.setText("Character Remaining : 160");
        etCommentStock.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                tvCharacterCount.setText("Character Remaining : " + (160 - s.toString().length()));
            }
        });
        btnSend = (CustomButton) view.findViewById(R.id.btnSend);
        tvTitleItemStockList = (TextView) view.findViewById(R.id.tvTitleItemStockList);
        tvStockRegistration = (TextView) view.findViewById(R.id.tvStockRegistration);
        tvDistanceItemStockList = (TextView) view.findViewById(R.id.tvDistanceItemStockList);
        tvColorItemStockList = (TextView) view.findViewById(R.id.tvColorItemStockList);
        tvStockCodeItem = (TextView) view.findViewById(R.id.tvStockCodeItem);
        tvRetailPriceStockList = (TextView) view.findViewById(R.id.tvRetailPriceStockList);
        tvTradePriceStockList = (TextView) view.findViewById(R.id.tvTradePriceStockList);
        tvMyBidItemStockList = (TextView) view.findViewById(R.id.tvMyBidItemBuyList);
        tvInternalMemo = (TextView) view.findViewById(R.id.tvInternalMemo);
        tvVehicleSpecs = (TextView) view.findViewById(R.id.tvVehicleSpecs);

        llSendVideos = (LinearLayout) view.findViewById(R.id.llSendVideos);
        llSendPhotos = (LinearLayout) view.findViewById(R.id.llSendPhotos);
        llSpecs = (LinearLayout) view.findViewById(R.id.llSpecs);
        llInternalMemo = (LinearLayout) view.findViewById(R.id.llInternalMemo);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvSubTitle = (TextView) view.findViewById(R.id.tvSubTitle);
        tvRetailPrice = (TextView) view.findViewById(R.id.tvRetailPrice);

        btnSend.setOnClickListener(this);
        //Components for video to be loaded from device
        rlImage = (RelativeLayout) view.findViewById(R.id.rlImage);
        rlImage1 = (RelativeLayout) view.findViewById(R.id.rlImage1);
        rlImage2 = (RelativeLayout) view.findViewById(R.id.rlImage2);
        rlImage1.setOnClickListener(this);
        rlImage2.setOnClickListener(this);
        rlVideo = (RelativeLayout) view.findViewById(R.id.rlVideo);
        ivAddImage = (ImageView) view.findViewById(R.id.ivAddImage);
        ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
        ivPicture1 = (ImageView) view.findViewById(R.id.ivPicture1);
        ivClose = (ImageView) view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);
        ivClose1 = (ImageView) view.findViewById(R.id.ivClose1);
        ivClose1.setOnClickListener(this);
        llImage1 = (LinearLayout) view.findViewById(R.id.llImage1);
        llImage1.setVisibility(View.GONE);
        llImage2 = (LinearLayout) view.findViewById(R.id.llImage2);
        llImage2.setVisibility(View.GONE);
        ivAddImage.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //this code will be executed on devices running Marshmellow or later
            imageDragableGridView = new DragableGridView(getActivity());
        } else
        {
            imageDragableGridView = new DragableGridView();
        }    //image grid view implemented in imageDragableGridView
        imageDragableGridView.init(view, new ImageClickListener()
        {
            @Override
            public void onImageClick(int position)
            {

            }

            @Override
            public void onImageDeleted(int position)
            {
            }
        });
        cbSendPhotos = (CustomCheckBox) view.findViewById(R.id.cbSendPhotos);
        cbSendPhotoPersonlised = (CustomCheckBox) view.findViewById(R.id.cbSendPhotoPersonlised);
        cbSendVideos = (CustomCheckBox) view.findViewById(R.id.cbSendVideos);
        cbSendVideoPersonlised = (CustomCheckBox) view.findViewById(R.id.cbSendVideoPersonlised);
        cbSendPhotoPersonlised.setOnCheckedChangeListener(this);
        cbSendVideoPersonlised.setOnCheckedChangeListener(this);
        cbSendPhotos.setOnCheckedChangeListener(this);
        llEmailList = (LinearLayout) view.findViewById(R.id.llEmailList);
        ivArrow = (ImageView) view.findViewById(R.id.ivArrow);
        ivArrow.setOnClickListener(this);
        llEmailList.setOnClickListener(this);
        llSeeMore = (LinearLayout) view.findViewById(R.id.llSeeMore);
        llSeeMore.setVisibility(View.GONE);

        llayout_SeeMoreInfo = (LinearLayout) view.findViewById(R.id.llayout_SeeMoreInfo);
        llayout_Varant_Details = (LinearLayout) view.findViewById(R.id.llayout_Varant_Details);
        llayout_eBroucher_Details = (LinearLayout) view.findViewById(R.id.llayout_eBroucher_Details);

        tvStockExtras = (TextView) view.findViewById(R.id.tvStockExtras);
        tvStockComment = (TextView) view.findViewById(R.id.tvStockComment);
        tvStockType = (TextView) view.findViewById(R.id.tvStockType);
        tvTimeLeftItemStockList = (TextView) view.findViewById(R.id.tvTimeLeftItemStockList);
        hlvStockCarImages = (HorizontalListView) view.findViewById(R.id.hlvStockCarImages);
        hlvStockCarVideos = (HorizontalListView) view.findViewById(R.id.hlvStockCarVideos);
        ivItemStockList = (NetworkImageView) view.findViewById(R.id.ivItemStockList);
        ivItemStockList.setDefaultImageResId(R.drawable.noimage);
        ivItemStockList.setOnClickListener(this);
        flItemStockBig = (FrameLayout) view.findViewById(R.id.flItemStockBig);
        hlvStockCarImages.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                navigateToLargeImage(position, images);
            }
        });

        hlvStockCarVideos.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                saveImagesToDataManager();
                videoPreview(DataManager.getInstance().getYouTubeVideos().get(position), position);
            }
        });
    }

    private void putValues()
    {
        if (isFromVariantList)
        {
            //  llayout_SeeMoreInfo.setVisibility(View.GONE);
            llayout_Varant_Details.setVisibility(View.VISIBLE);
            llayout_eBroucher_Details.setVisibility(View.GONE);

            tvTitle.setText(Html.fromHtml("" + variant.getFriendlyName() + ""));

            tvSubTitle.setText("New | " + variant.getMeadCode() + " | ");

            if (Helper.formatPrice(new BigDecimal(variant.getPrice()) + "").equals("R0"))
            {
                tvRetailPrice.setText("R?");
            } else
            {
                tvRetailPrice.setText(Helper.formatPrice(new BigDecimal(variant.getPrice()) + ""));
            }
            getVariantDetailsSoap();
        } else
        {
            //  llayout_SeeMoreInfo.setVisibility(View.VISIBLE);
            llayout_Varant_Details.setVisibility(View.GONE);
            llayout_eBroucher_Details.setVisibility(View.VISIBLE);

            tvColorItemStockList.setText(vehicle.getColour());

            tvRetailPriceStockList.setText(Helper.formatPrice(new BigDecimal(vehicle.getRetailPrice()) + ""));
            tvTradePriceStockList.setText(Helper.formatPrice(new BigDecimal(vehicle.getTradePrice()) + ""));
            if (vehicle.getMileage() == 0)
            {
                tvDistanceItemStockList.setText("Mileage?");
            } else
            {
                tvDistanceItemStockList.setText(Helper.getFormattedDistance(vehicle.getMileage() + "") + " Km");
            }
            tvTimeLeftItemStockList.setText(" " + vehicle.getExpires() + " Days");
            if(operationType.equalsIgnoreCase("new")){
                tvStockRegistration.setText("New");
                tvTitleItemStockList.setText(Html.fromHtml(vehicle.getFriendlyName()));
            }else {
                tvTitleItemStockList.setText(Html.fromHtml("<font color=#ffffff>" + vehicle.getYear() + "</font> " + " " + vehicle.getFriendlyName()));
                tvStockRegistration.setText(vehicle.getRegNumber());
            }

            tvStockCodeItem.setText(vehicle.getStockNumber());
            if (vehicle.getNumOfVideos() == 0)
            {
                llSendVideos.setVisibility(View.GONE);
            } else
            {
                llSendVideos.setVisibility(View.VISIBLE);
            }
            if (vehicle.getNumOfPhotos() == 0)
            {
                llSendPhotos.setVisibility(View.GONE);
            } else
            {
                llSendPhotos.setVisibility(View.VISIBLE);
            }
            getVehicleDetailsSoap();
        }

        //	fetchCommentsAndExtras();
    }

    private void serverVideoLoading(ArrayList<YouTubeVideo> videos)
    {
        ArrayList<YouTubeVideo> videos_temp = new ArrayList<>();
        for (int i = 0; i < videos.size(); i++)
        {
            YouTubeVideo youTubeVideo = videos.get(i);
            if (!youTubeVideo.isLocal())
            {
                videos_temp.add(youTubeVideo);
            }
        }

        HorizontalListVideoAdapter horizontalListVideoAdapter = new HorizontalListVideoAdapter(getActivity(), R.layout.grid_item_video, videos_temp, false);
        hlvStockCarVideos.setAdapter(horizontalListVideoAdapter);

        if (videos_temp.size() > 0)
        {
            llSendVideos.setVisibility(View.VISIBLE);
            hlvStockCarVideos.setVisibility(View.VISIBLE);
        } else
        {
            llSendVideos.setVisibility(View.GONE);
            hlvStockCarVideos.setVisibility(View.GONE);
        }
    }

    private void videoloading(ArrayList<YouTubeVideo> videosTest)
    {
        ArrayList<YouTubeVideo> videos = new ArrayList<>();
        for (int i = 0; i < videosTest.size(); i++)
        {
            YouTubeVideo youTubeVideo = videosTest.get(i);
            if (youTubeVideo.isLocal())
            {
                videos.add(youTubeVideo);
            }
        }

        if (videos.size() == 0)
        {
            llImage1.setVisibility(View.GONE);
            llImage2.setVisibility(View.GONE);
            ivAddImage.setOnClickListener(this);
            isVideoSelected = false;
            fisrtBitmap = null;
            ivClose1.setVisibility(View.VISIBLE);
            ivClose1.setVisibility(View.VISIBLE);
            secondBitmap = null;
        }
        if (videos.size() == 1)
        {
            llImage1.setVisibility(View.VISIBLE);
            llImage2.setVisibility(View.GONE);
            ivAddImage.setOnClickListener(this);
            if (videos.get(0).isLocal())
            {
                ivClose.setVisibility(View.VISIBLE);
                isVideoSelected = true;
                fisrtBitmap = ThumbnailUtils.createVideoThumbnail(videos.get(0).getVideoFullPath(), Thumbnails.MINI_KIND);
                ivPicture.setImageBitmap(fisrtBitmap);
            }
        }
        if (videos.size() == 2)
        {
            if (videos.get(0).isLocal())
            {
                llImage1.setVisibility(View.VISIBLE);
                ivClose.setVisibility(View.VISIBLE);
                fisrtBitmap = ThumbnailUtils.createVideoThumbnail(videos.get(0).getVideoFullPath(), Thumbnails.MINI_KIND);
                ivPicture.setImageBitmap(fisrtBitmap);
                isVideoSelected = true;
            }
            if (videos.get(1).isLocal())
            {
                llImage2.setVisibility(View.VISIBLE);
                ivClose1.setVisibility(View.VISIBLE);
                isVideoSelected = true;
                fisrtBitmap = ThumbnailUtils.createVideoThumbnail(videos.get(0).getVideoFullPath(), Thumbnails.MINI_KIND);
                ivPicture.setImageBitmap(fisrtBitmap);
                secondBitmap = ThumbnailUtils.createVideoThumbnail(videos.get(1).getVideoFullPath(), Thumbnails.MINI_KIND);
                ivPicture1.setImageBitmap(secondBitmap);
                ivAddImage.setOnClickListener(null);
            }
        }

    }

    private void watchYoutubeVideo(String id)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.ivItemStockList:
                navigateToLargeImage(0, images);
                break;

            case R.id.btnSend:

                //CustomDialogManager.showErrorDialogEmail(getActivity(),
                //		"Coming Soon! Please contact support@ix.co.za about this functionality. ");
                //	sendBrochure();
                if (TextUtils.isEmpty(etFirstName.getText().toString().trim()))
                {
                    Helper.showToast("Please enter name", getActivity());
                    return;
                }
                if (TextUtils.isEmpty(etMobileNumber.getText().toString().trim()))
                {
                    Helper.showToast("Please enter mobile number", getActivity());
                    return;
                }
                if (etMobileNumber.getText().toString().trim().length() < 10)
                {
                    Helper.showToast("Please enter valid mobile number", getActivity());
                    return;
                }
                if (!Helper.validMail(etEmailAddress.getText().toString().trim()))
                {
                    Helper.showToast("Please enter valid email address", getActivity());
                    return;
                }
                if (TextUtils.isEmpty(etCommentStock.getText().toString().trim()))
                {
                    Helper.showToast("Please add a comment", getActivity());
                    return;
                }
                if (imageDragableGridView.getLocalImageListWithoutPlus().size() != 0 && cbSendPhotoPersonlised.isChecked())
                {
                    beginPersonalizedImageList();
                    //sendBrochure(true, cbSendPhotos.isChecked(), cbSendVideos.isChecked());
                } else if (cbSendVideoPersonlised.isChecked())
                {
                    uploadWarning();
                } else
                {
                    sendBrochure(false, cbSendPhotos.isChecked(), cbSendVideos.isChecked());
                }

                break;
            case R.id.ivArrow:
            case R.id.llEmailList:

                if (llSeeMore.getVisibility() == View.GONE)
                {
                    ivArrow.setRotation(0);
                    llSeeMore.setVisibility(View.VISIBLE);
                } else
                {
                    ivArrow.setRotation(-90);
                    llSeeMore.setVisibility(View.GONE);
                }
                break;

            case R.id.ivAddImage:
                if (DataManager.getInstance().isClientSetUptoUploadVideo)
                {
                    Helper.getVideoFromGalleryOrCamera(getActivity());
                } else
                {
                    CustomDialogManager.showErrorDialogEmail(getActivity(), "Sorry, you have not been activated for this service yet.");
                }
                break;
            case R.id.ivClose:
                if (llImage2.getVisibility() == View.VISIBLE)
                {
                    if (DataManager.getInstance().getYouTubeVideos().get(DataManager.getInstance().getYouTubeVideos().size() - 2).isLocal())
                    {
                        isVideoSelected = false;
                        DataManager.getInstance().getYouTubeVideos().remove(DataManager.getInstance().getYouTubeVideos().size() - 2);
                        llImage1.setVisibility(View.GONE);
                        fisrtBitmap = null;
                        ivAddImage.setOnClickListener(this);
                    } else
                    {
                        isVideoSelected = false;
                        llImage1.setVisibility(View.GONE);
                        ivAddImage.setOnClickListener(this);
                        DataManager.getInstance().getYouTubeVideos().remove(DataManager.getInstance().getYouTubeVideos().size() - 2);
                    }
                } else
                {
                    if (DataManager.getInstance().getYouTubeVideos().get(DataManager.getInstance().getYouTubeVideos().size() - 1).isLocal())
                    {
                        isVideoSelected = false;
                        DataManager.getInstance().getYouTubeVideos().remove(DataManager.getInstance().getYouTubeVideos().size() - 1);
                        llImage1.setVisibility(View.GONE);
                        fisrtBitmap = null;
                        ivAddImage.setOnClickListener(this);
                    } else
                    {
                        isVideoSelected = false;
                        llImage1.setVisibility(View.GONE);
                        ivAddImage.setOnClickListener(this);
                        DataManager.getInstance().getYouTubeVideos().remove(DataManager.getInstance().getYouTubeVideos().size() - 1);
                    }
                }
                break;

            case R.id.ivClose1:
                if (DataManager.getInstance().getYouTubeVideos().get(DataManager.getInstance().getYouTubeVideos().size() - 1).isLocal())
                {
                    DataManager.getInstance().getYouTubeVideos().remove(DataManager.getInstance().getYouTubeVideos().size() - 1);
                    secondBitmap = null;
                    llImage2.setVisibility(View.GONE);
                    ivAddImage.setOnClickListener(this);
                } else
                {
                    llImage2.setVisibility(View.GONE);
                    ivAddImage.setOnClickListener(this);
                    DataManager.getInstance().getYouTubeVideos().remove(DataManager.getInstance().getYouTubeVideos().size() - 1);
                }
                break;

            case R.id.rlImage1:

                if (llImage2.getVisibility() == View.VISIBLE)
                {
                    videoPreview(DataManager.getInstance().getYouTubeVideos().get(DataManager.getInstance().getYouTubeVideos().size() - 2), DataManager.getInstance().getYouTubeVideos().size() - 2);

                } else
                {
                    videoPreview(DataManager.getInstance().getYouTubeVideos().get(DataManager.getInstance().getYouTubeVideos().size() - 1), DataManager.getInstance().getYouTubeVideos().size() - 1);

                }
                break;

            case R.id.rlImage2:
                videoPreview(DataManager.getInstance().getYouTubeVideos().get(DataManager.getInstance().getYouTubeVideos().size() - 1), DataManager.getInstance().getYouTubeVideos().size() - 1);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {

        switch (buttonView.getId())
        {
            case R.id.cbSendPhotoPersonlised:

                if (isChecked)
                {
                    rlImage.setVisibility(View.VISIBLE);
                    isImagesSending = true;
                } else
                {
                    rlImage.setVisibility(View.GONE);
                    isImagesSending = false;
                }
                break;

            case R.id.cbSendVideoPersonlised:
                if (isChecked)
                {
                    rlVideo.setVisibility(View.VISIBLE);
                    isVideoSending = true;
                } else
                {
                    rlVideo.setVisibility(View.GONE);
                    isVideoSending = false;
                }
                break;
        }

    }

    @SuppressWarnings("static-access")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK)
        {
            if (requestCode == Constants.VIDEO_RECORDING_CUSTOM)
            {
                result = data.getStringExtra("result");
                if (result != null)
                    isVideoSelected = true;
                videoInfoScreen(result);
            } else if (requestCode == Constants.VIDEO_GALLERY)
            {
                Uri selectedVideo = data.getData();
                Helper.Log("sELECT GAALLAET : ", "videoUri= " + selectedVideo);
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
                        /* MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(videoPath));
                        int duration = mp.getDuration(); FOR CHECKING DURATION FOR VIDEO
	                    mp.release();*/
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
        if (imageDragableGridView != null)
        {
            if (imageDragableGridView.isOptionSelected())
                imageDragableGridView.onActivityResult(requestCode, resultCode, data);
        }
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
     getFragmentManager().beginTransaction().replace(R.id.Container, previewFragment).addToBackStack("").commit();
    }

    private void saveImagesToDataManager()
    {
        // To save the images in data manager.
        if (imageDragableGridView != null)
            DataManager.getInstance().setimageArray(imageDragableGridView.getUpdatedImageListWithoutPlus());
    }


    private void videoInfoScreen(String videoPath)
    {
        saveImagesToDataManager();

        infoFragment = new VideoInfoFragment();
        Bundle args = new Bundle();
        args.putString("videoPath", videoPath);
        if(isFromVariantList)
        {
            args.putString("default_name", variant.getFriendlyName());
        }else
        {
            args.putString("default_name", tvTitleItemStockList.getText().toString() + "-" + tvStockCodeItem.getText().toString());
        }
        args.putString("from_ebrochure", "true");
        infoFragment.setArguments(args);
       getFragmentManager().beginTransaction().replace(R.id.Container, infoFragment).addToBackStack("").commit();
    }

    protected void navigateToLargeImage(int position, ArrayList<MyImage> images)
    {
        GallaryFragment imageDetailFragment = new GallaryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("imagelist", images);
        args.putInt("index", position);
        args.putString("vehicleName", "Stock Audit");
        args.putString("from", "image");
        imageDetailFragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.Container, imageDetailFragment).addToBackStack(null).commit();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("eBrochure");
        //	getActivity().getActionBar().setTitle("eBrochure");
        //	getActivity().getActionBar().setSubtitle(null);

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
                    hideProgressDialog();
                    Helper.Log("Error: ", error.toString());
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

    VehicleDetails details;

    private void getVehicleDetailsSoap()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("usedVehicleStockID", vehicle.getID(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadVehicleDetailsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadVehicleDetailsXML");
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
                        if (result != null)
                        {
                            Helper.Log("response", "" + result.toString());
                            details = new VehicleDetails();
                            @SuppressWarnings("unchecked")
                            Vector<SoapObject> vectorResult = (Vector<SoapObject>) result;

                            SoapObject outer = vectorResult.get(0);
                            SoapObject inner = (SoapObject) outer.getPropertySafely("Details");
                            details.setUsedVehicleStockID(Integer.parseInt(inner.getPropertySafelyAsString("usedVehicleStockID", "0")));
                            department = inner.getPropertySafelyAsString("department", "");
                            details.setStockCode(Helper.checkEmpty(inner.getPropertySafelyAsString("stockCode", "")));
                            details.setAge((int) Float.parseFloat(inner.getPropertySafelyAsString("age", "0")));
                            details.setVariantID(Integer.parseInt(inner.getPropertySafelyAsString("variantID", "0")));
                            details.setFriendlyName(Helper.checkEmpty(inner.getPropertySafelyAsString("friendlyName", "")));
                            details.setMmcode(Helper.checkEmpty(inner.getPropertySafelyAsString("mmcode", "")));
                            details.setYear(Integer.parseInt(inner.getPropertySafelyAsString("year", "0")));
                            details.setRegistration(Helper.checkEmpty(inner.getPropertySafelyAsString("registration", "")));
                            String price = inner.getPropertySafelyAsString("price", "0.0").replace(",",".");
                            details.setPrice(Double.parseDouble(price));
                            String tradePrice = inner.getPropertySafelyAsString("tradeprice", "0.0").replace(",",".");
                            details.setTradeprice(Double.parseDouble(tradePrice));
                            details.setColour(Helper.checkEmpty(inner.getPropertySafelyAsString("colour", "")));
                            details.setMileage(Integer.parseInt(inner.getPropertySafelyAsString("mileage", "0")));
                            details.setComments(Helper.checkEmpty(inner.getPropertySafelyAsString("comments", "")));
                            details.setLocation(Helper.checkEmpty(inner.getPropertySafelyAsString("location", "")));
                            details.setExtras(Helper.checkEmpty(inner.getPropertySafelyAsString("extras", "")));
                            details.setTrim(inner.getPropertySafelyAsString("trim", ""));
                            details.setCondition(Helper.checkEmpty(inner.getPropertySafelyAsString("condition", "")));
                            details.setVin(Helper.checkEmpty(Helper.checkEmpty(inner.getPropertySafelyAsString("vin", ""))));
                            details.setEngine(Helper.checkEmpty(inner.getPropertySafelyAsString("engine", "")));

                            details.setOem(Helper.checkEmpty(inner.getPropertySafelyAsString("oem", "")));
                            String cost = inner.getPropertySafelyAsString("cost", "0.0").replace(",",".");
                            details.setCost(Float.parseFloat(cost));
                            String standin = inner.getPropertySafelyAsString("standin", "0.0").replace(",",".");
                            details.setStandin(Float.parseFloat(standin));
                            details.setCpaerror(Boolean.parseBoolean(inner.getPropertySafelyAsString("cpaerror", "false")));
                            details.setInternalnote(Helper.checkEmpty(inner.getPropertySafelyAsString("internalnote", "")));
                            details.setProgramname(Helper.checkEmpty(inner.getPropertySafelyAsString("programname", "")));
                            details.setIstender(Boolean.parseBoolean(inner.getPropertySafelyAsString("istender", "false")));
                            details.setIstrade(Boolean.parseBoolean(inner.getPropertySafelyAsString("istrade", "false")));
                            details.setIsretail(Boolean.parseBoolean(inner.getPropertySafelyAsString("isretail", "false")));
                            details.setIsprogram(Boolean.parseBoolean(inner.getPropertySafelyAsString("isprogram", "false")));
                            details.setIsexcluded(Boolean.parseBoolean(inner.getPropertySafelyAsString("isexcluded", "false")));
                            details.setIsinvalid(Boolean.parseBoolean(inner.getPropertySafelyAsString("isinvalid", "false")));
                            details.setOverride(Boolean.parseBoolean(inner.getPropertySafelyAsString("override", "false")));
                            details.setIgnoreonimport(Boolean.parseBoolean(inner.getPropertySafelyAsString("ignoreonimport", "false")));
                            details.setEditable(Boolean.parseBoolean(inner.getPropertySafelyAsString("editable", "false")));

                            if (inner.hasProperty("Videos") && infoFragment == null && previewFragment == null)
                            {
                                SoapObject videoObj = (SoapObject) inner.getPropertySafely("Videos");
                                YouTubeVideo tubeVideo = null;
                                for (int i = 0; i < videoObj.getPropertyCount(); i++)
                                {
                                    tubeVideo = new YouTubeVideo();
                                    SoapObject videoListObj = (SoapObject) videoObj.getProperty(i);
                                    tubeVideo.setVideo_ID(videoListObj.getPropertySafelyAsString("youtubeVideoID", ""));
                                    tubeVideo.setLocal(false);
                                    tubeVideo.setVideo_Description(videoListObj.getPropertySafelyAsString("description", ""));
                                    tubeVideo.setSearchable(Boolean.parseBoolean(inner.getPropertySafelyAsString("Searchable", "false")));
                                    tubeVideo.setVideo_Tags(videoListObj.getPropertySafelyAsString("Keywords", ""));
                                    tubeVideo.setVideo_title(videoListObj.getPropertySafelyAsString("title", ""));
                                    tubeVideo.setVideoLinkID(Integer.parseInt(videoListObj.getPropertySafelyAsString("VideoLinkID", "")));
                                    tubeVideo.setVideoCode(videoListObj.getPropertySafelyAsString("youtubeID", ""));
                                    tubeVideo.setVideoFullPath(videoListObj.getPropertySafelyAsString("videoURL", ""));
                                    tubeVideo.setVideoThumbUrl("http://img.youtube.com/vi/" + tubeVideo.getVideoCode() + "/0.jpg");
                                    uploadedVideos.add(tubeVideo);
                                }
                                DataManager.getInstance().setYouTubeVideos(uploadedVideos);
                            }
                            if (inner.hasProperty("SpecDetails"))
                            {
                                SoapObject specListObj = (SoapObject) inner.getPropertySafely("SpecDetails");
                                Helper.Log("specobject line", specListObj.toString());
                                vehicleDetails = new VehicleDetails();
                                for (int i = 0; i < specListObj.getPropertyCount(); i++)
                                {
                                    String name = ((SoapPrimitive) specListObj.getProperty(i)).getAttribute(1).toString();
                                    //spec.setId(Integer.parseInt((String) specObj.getAttributeSafelyAsString("name")));
                                    String value = ((SoapPrimitive) specListObj.getProperty(i)).getValue().toString();
                                    parseResponse(name, value);
                                }
                                loadspec(vehicleDetails);
                            } else
                            {
                                llSpecs.setVisibility(View.GONE);
                            }

                            if (inner.hasProperty("images"))
                            {
                                SoapObject imgListObj = (SoapObject) inner.getPropertySafely("images");
                                ArrayList<VehicleImage> imageList = new ArrayList<VehicleImage>();
                                VehicleImage image = null;
                                for (int i = 0; i < imgListObj.getPropertyCount(); i++)
                                {
                                    image = new VehicleImage();
                                    SoapObject imgObj = (SoapObject) imgListObj.getProperty(i);
                                    image.setUciid(Integer.parseInt(imgObj.getPropertySafelyAsString("uciID", "0")));
                                    image.setId(Integer.parseInt(imgObj.getPropertySafelyAsString("imageID", "0")));
                                    image.setImageTitle(imgObj.getPropertySafelyAsString("imageTitle2", ""));
                                    image.setPriority(Integer.parseInt(imgObj.getPropertySafelyAsString("imagePriority", "0")));
                                    image.setImageTypeName(imgObj.getPropertySafelyAsString("imageTypeName", ""));
                                    image.setImageSource(imgObj.getPropertySafelyAsString("imageSource", ""));
                                    image.setPath(imgObj.getPropertySafelyAsString("imagePath", ""));
                                    image.setLink(imgObj.getPropertySafelyAsString("imageLink", ""));
                                    image.setImageSize(Integer.parseInt(imgObj.getPropertySafelyAsString("imageSize", "0")));
                                    image.setImageRes(imgObj.getPropertySafelyAsString("imageRes", ""));
                                    image.setType(Integer.parseInt(imgObj.getPropertySafelyAsString("imageType", "0")));
                                    //image.setImagedpi((int) Float.parseFloat(imgObj.getPropertySafelyAsString("imageDPI", "0")));

                                    imageList.add(image);
                                }
                                details.setImageList(imageList);
                                images = new ArrayList<MyImage>();
                                for (int i = 0; i < details.getImageList().size(); i++)
                                {
                                    BaseImage baseImage = details.getImageList().get(i);
                                    MyImage image1 = new MyImage();
                                    image1.setThumb(baseImage.getLink());
                                    image1.setFull(baseImage.getLink());
                                    images.add(image1);
                                }
                            }

                            if (details.getImageList() != null && details.getImageList().size() != 0)
                            {
                                flItemStockBig.setVisibility(View.VISIBLE);
                                ivItemStockList.setImageUrl(images.get(0).getThumb(), imageLoader);
                                if (images.size() == 1)
                                {
                                    hlvStockCarImages.setVisibility(View.GONE);
                                } else
                                {
                                    hlvStockCarImages.setVisibility(View.VISIBLE);
                                }
                            } else
                            {
                                flItemStockBig.setVisibility(View.GONE);
                                hlvStockCarImages.setVisibility(View.GONE);
                                ivItemStockList.setErrorImageResId(R.drawable.no_media);
                            }
                            if (!TextUtils.isEmpty(details.getInternalnote()))
                            {
                                tvInternalMemo.setText(details.getInternalnote());
                            } else
                            {
                                llInternalMemo.setVisibility(View.GONE);
                            }
                            if (!TextUtils.isEmpty(details.getComments()))
                            {
                                tvStockComment.setText(details.getComments());
                            } else
                            {
                                tvStockComment.setText("None loaded");
                            }
                            if (!TextUtils.isEmpty(details.getExtras()))
                            {
                                tvStockExtras.setText(details.getExtras());
                            } else
                            {
                                tvStockExtras.setText("None loaded");
                            }
                            tvStockType.setText(department);
                            tempList = new ArrayList<MyImage>();
                            tempList.addAll(images);
                            if (!tempList.isEmpty())
                                tempList.remove(0);
                            horizontalListViewAdapter = new HorizontalListViewAdapter(getActivity(), tempList);
                            hlvStockCarImages.setAdapter(horizontalListViewAdapter);
                            serverVideoLoading(uploadedVideos);
                            hideProgressDialog();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                        ivItemStockList.setErrorImageResId(R.drawable.no_media);
                        ivItemStockList.setDefaultImageResId(R.drawable.no_media);
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    protected void parseResponse(String name, String value) throws ParseException
    {

        if (name.equals("Engine CC"))
        {
            String enginecc = StringUtils.substringBefore(value, "c");
            vehicleDetails.setEngine_CC(NumberFormat.getInstance().parse(enginecc).intValue());
        } else if (name.equals("Power KW"))
        {
            String power = StringUtils.substringBefore(value, "k");
            vehicleDetails.setPower_KW(NumberFormat.getInstance().parse(power).intValue());
        } else if (name.equals("Torque NM"))
        {
            String identity = StringUtils.substringBefore(value, "N");
            if (TextUtils.isEmpty(identity))
                vehicleDetails.setTorque_NM(0);
            else
                vehicleDetails.setTorque_NM(NumberFormat.getInstance().parse(identity).intValue());
        } else if (name.equals("Gearbox"))
            vehicleDetails.setGearbox(value);
        else if (name.equals("Fuel Type"))
            vehicleDetails.setFuel_Type(value);
        else if (name.equals("Gears"))
        {
            String identity = value;
            if (TextUtils.isEmpty(identity))
                vehicleDetails.setGears(0 + "");
            else
                vehicleDetails.setGears(identity + "");
        } else if (name.equals("Accel 0-100"))
        {
            String identity = value;
            if (TextUtils.isEmpty(identity))
                vehicleDetails.setAcceleration0_100("");
            else
                vehicleDetails.setAcceleration0_100(identity);
        } else if (name.equals("Max Speed"))
        {
            String identity = value;
            if (TextUtils.isEmpty(identity))
                vehicleDetails.setMax_Speed("");
            else
                vehicleDetails.setMax_Speed(identity);
        } else if (name.equals("Fuel per 100km - average"))
        {
            String identity = value;
            if (TextUtils.isEmpty(identity))
                vehicleDetails.setVehicle_mileage_100km("");
            else
                vehicleDetails.setVehicle_mileage_100km(identity);
        } else if (name.equals("Warranty Period (RSA)"))
        {
            String identity = value;
            if (TextUtils.isEmpty(identity))
                vehicleDetails.setWarranty_Period_RSA("");
            else
                /*if (Integer.parseInt(identity)<12)
                {*/
                vehicleDetails.setWarranty_Period_RSA(identity);
                /*}else {
                    vehicleDetails.setWarranty_Period_RSA(Integer.parseInt(identity)/12+" years");
				}*/
        } else if (name.equals("Warranty (RSA)"))
        {
            String identity = value;
            if (TextUtils.isEmpty(identity))
                vehicleDetails.setWarranty("");
            else
                vehicleDetails.setWarranty(identity);
        } else if (name.equals("Maintenance Plan Period"))
        {
            String identity = value;
            if (TextUtils.isEmpty(identity))
                vehicleDetails.setMaintenance_Plan_Period("");
            else
                /*if (Integer.parseInt(identity)<12)
                {*/
                vehicleDetails.setMaintenance_Plan_Period(identity);
                /*}else {
                    vehicleDetails.setMaintenance_Plan_Period(Integer.parseInt(identity)/12+" years");
				}*/
        } else if (name.equals("Maintenance Plan"))
        {
            String identity = value;
            if (TextUtils.isEmpty(identity))
                vehicleDetails.setMaintenance_Plan("");
            else
                vehicleDetails.setMaintenance_Plan(identity);
        }

    }

    protected void loadspec(VehicleDetails summeryDetails)
    {
        StringBuilder detailsBuilder = new StringBuilder();
        if (summeryDetails.getEngine_CC() != 0)
            detailsBuilder.append(summeryDetails.getEngine_CC() + "cc; ");
        if (summeryDetails.getPower_KW() != 0)
            detailsBuilder.append(summeryDetails.getPower_KW() + "Kw; ");
        if (summeryDetails.getTorque_NM() != 0)
            detailsBuilder.append(summeryDetails.getTorque_NM() + "Nm; ");
        if (!TextUtils.isEmpty(summeryDetails.getAcceleration0_100()))
            detailsBuilder.append("0-100kph in " + summeryDetails.getAcceleration0_100() + "; ");
        if (!TextUtils.isEmpty(summeryDetails.getMax_Speed()))
            detailsBuilder.append("Top Speed " + summeryDetails.getMax_Speed() + "; ");
        if (summeryDetails.getGearbox() != null && !summeryDetails.getGearbox().isEmpty())
            detailsBuilder.append(summeryDetails.getGearbox().trim() + "; ");
        if (summeryDetails.getGears() != null)
            detailsBuilder.append(summeryDetails.getGears() + " ; ");
        if (summeryDetails.getFuel_Type() != null && !summeryDetails.getFuel_Type().isEmpty())
            detailsBuilder.append(summeryDetails.getFuel_Type().trim() + "; ");
        if (!TextUtils.isEmpty(summeryDetails.getVehicle_mileage_100km()))
            detailsBuilder.append("Cons. " + summeryDetails.getVehicle_mileage_100km() + "/100Km; ");
        if (!TextUtils.isEmpty(summeryDetails.getWarranty()) && !TextUtils.isEmpty(summeryDetails.getWarranty_Period_RSA()))
            detailsBuilder.append("Warranty " + summeryDetails.getWarranty() + "/" + summeryDetails.getWarranty_Period_RSA() + "; ");
        if (!TextUtils.isEmpty(summeryDetails.getMaintenance_Plan()) && !TextUtils.isEmpty(summeryDetails.getMaintenance_Plan_Period()))
            detailsBuilder.append("Maintenance Plan " + summeryDetails.getMaintenance_Plan() + "/" + summeryDetails.getMaintenance_Plan_Period());

        tvVehicleSpecs.setText(detailsBuilder);
    }

    // web service call to get token to upload images
    private void beginPersonalizedImageList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<BeginPersonalizedImageList xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("</BeginPersonalizedImageList>");
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
                        hideProgressDialog();
                        return;
                    }
                    Helper.Log("BeginPersonalizedImageList", "" + response);
                    personalizedImageListToken = ParserManager.parsetokenChecker(response, "BeginPersonalizedImageListResult");
                    addImageToList();
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.EBROCHURE_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/BeginPersonalizedImageList", listener);
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
        noOfImagesInGrid = imageDragableGridView.getUpdatedImageListWithoutPlus().size();
        if (noOfImagesInGrid == uploadImageCount)
        {
            endPersonalizedImageList();
        }
        if (imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).isLocal())
        {
            String base64String = Helper.convertBitmapToBase64(imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).getPath());
            if (HelperHttp.isNetworkAvailable(getActivity()))
            {
                ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
                parameterList.add(new Parameter("personalizedImageListToken", personalizedImageListToken, String.class));

                if (isFromVariantList)
                    parameterList.add(new Parameter("variantID", variant.getVariantId(), Integer.class));
                else
                    parameterList.add(new Parameter("usedVehicleStockID", vehicle.getID(), Integer.class));

                parameterList.add(new Parameter("base64EncodedString", base64String, String.class));

                // create web service inputs
                DataInObject inObj = new DataInObject();
                if (isFromVariantList)
                {
                    inObj.setMethodname("UploadNewPersonalizedImageAsPng");
                    inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
                    inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/UploadNewPersonalizedImageAsPng");
                } else
                {
                    inObj.setMethodname("UploadPersonalizedImageAsPng");
                    inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
                    inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/UploadPersonalizedImageAsPng");
                }

                inObj.setUrl(Constants.EBROCHURE_WEBSERVICE_URL);
                inObj.setParameterList(parameterList);

                // Network call
                new WebServiceTask(getActivity(), inObj, false, new TaskListener()
                {
                    @Override
                    public void onTaskComplete(Object result)
                    {
                        try
                        {
                            Helper.Log("Image upload Response", result.toString());
                            SoapObject outer = (SoapObject) result;
                            boolean isSuccess = Boolean.parseBoolean(outer.getPropertySafelyAsString("IsSuccess", "false"));
                            if (isSuccess)
                            {
                                if (uploadImageCount != noOfImagesInGrid)
                                {
                                    uploadImageCount++;
                                    addImageToList();
                                } else
                                {
                                    endPersonalizedImageList();
                                }
                            } else
                            {
                                hideProgressDialog();
                                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_sendiing_data), new DialogListener()
                                {
                                    @Override
                                    public void onButtonClicked(int type)
                                    {
                                        getFragmentManager().popBackStack();
                                    }
                                });
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
    }

    private void endPersonalizedImageList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<EndPersonalizedImageList xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<personalizedImageListToken>" + personalizedImageListToken + "</personalizedImageListToken>");
            soapMessage.append("</EndPersonalizedImageList>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            VollyResponseListener listener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Helper.showToast(getString(R.string.error_getting_data), getActivity());
                    hideProgressDialog();
                    VolleyLog.e("Error: ", error.toString());
                }

                @Override
                public void onResponse(String response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    Helper.Log("EndPersonalizedImageList", "" + response);

                    boolean isSuccess = Boolean.parseBoolean(ParserManager.parsetokenChecker(response, "a:IsSuccess"));
                    if (isSuccess)
                    {
                        if (DataManager.getInstance().getYouTubeVideos().size() != 0 && cbSendVideoPersonlised.isChecked())
                        {
                            uploadWarning();
                        } else
                        {
                            sendBrochure(true, cbSendPhotos.isChecked(), cbSendVideos.isChecked());
                        }
                    } else
                    {
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_occured_try_later));

                    }
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.EBROCHURE_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/EndPersonalizedImageList", listener);
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

    private class videoUpload extends AsyncTask<Void, Void, String>
    {
        String VideoName;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            showProgressDialog();
            if (videoCodes.size() == uploaded_video.size())
            {
                return;
            }

        }

        @Override
        protected String doInBackground(Void... params)
        {
            for (int i = uploadVideoCount; i < DataManager.getInstance().getYouTubeVideos().size(); i++)
            {
                if (DataManager.getInstance().getYouTubeVideos().get(i).isLocal())
                {
                    if (isFromVariantList)
                    {
                        VideoName = HelperHttp.uploadVideoVariantFile(new File(DataManager.getInstance().getYouTubeVideos().get(i).getVideoFullPath()),
                                variant.getVariantId(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_title(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Description(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Tags(),
                                DataManager.getInstance().getYouTubeVideos().get(i).isSearchable());
                    } else
                    {
                        VideoName = HelperHttp.uploadVideoFile(new File(DataManager.getInstance().getYouTubeVideos().get(i).getVideoFullPath()),
                                vehicle.getID(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_title(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Description(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Tags(),
                                DataManager.getInstance().getYouTubeVideos().get(i).isSearchable());
                    }

                    if (VideoName != null)
                    {
                        videoCodes.add(VideoName);
                        uploaded_video.add(DataManager.getInstance().getYouTubeVideos().get(uploadVideoCount));
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
                hideProgressDialog();
                if (videoCodes.size() == uploaded_video.size())
                {
                    isVideoSelected = false;
                    DataManager.getInstance().getYouTubeVideos().clear();
                    if (!TextUtils.isEmpty(personalizedImageListToken))
                    {
                        sendBrochure(true, cbSendPhotos.isChecked(), cbSendVideos.isChecked());
                    } else
                    {

                        sendBrochure(false, cbSendPhotos.isChecked(), cbSendVideos.isChecked());
                    }

                } else
                {
                    uploadVideoCount++;
                    DataManager.getInstance().getYouTubeVideos().remove(uploadImageCount);
                    new videoUpload().execute();
                }
            } else
            {
                hideProgressDialog();
                isVideoUploadFail = true;
                //	message= "Brochure has been sent successfully but could not attach the video/s.";
                if (!TextUtils.isEmpty(personalizedImageListToken))
                {
                    sendBrochure(true, cbSendPhotos.isChecked(), cbSendVideos.isChecked());
                } else
                {

                    sendBrochure(false, cbSendPhotos.isChecked(), cbSendVideos.isChecked());
                }
            }
        }

    }

    private void uploadWarning()
    {
        long total_video_size = 0;
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mWifi.isConnected())
        {
            new videoUpload().execute();
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

            CustomDialogManager.showOkCancelDialog(getActivity(), "It is recommended that you connect to " +
                    "a WiFi network to upload video files of size " + (total_video_size / 1024) +
                    "MB, to avoid excessive data use." +
                    " Do you want to upload over your Mobile Network connection ?", "Yes", "No", new DialogListener()
            {
                @Override
                public void onButtonClicked(int type)
                {
                    if (type == Dialog.BUTTON_POSITIVE)
                    {
                        new videoUpload().execute();
                    } else
                    {
                        sendBrochure(false, cbSendPhotos.isChecked(), cbSendVideos.isChecked());
                    }
                }
            });
        }
    }

    // web service call to send brochure with all comment and email id
    private void sendBrochure(boolean isWithImages, boolean sendPhotos, boolean sendVideos)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            if (isWithImages)
            {
                if (isFromVariantList)
                {
                    soapMessage.append("<SendNewBrochureToAddressWithImages xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
                } else
                {
                    soapMessage.append("<SendBrochureToAddressWithImages xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
                }
            } else
            {
                if (isFromVariantList)
                {
                    soapMessage.append("<SendNewBrochureToAddressWithoutImages xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
                } else
                {
                    soapMessage.append("<SendBrochureToAddressWithoutImages xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
                }
            }
            soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");

            //
            if (isFromVariantList)
                soapMessage.append("<variantID>" + variant.getVariantId() + "</variantID>");
            else
                soapMessage.append("<usedVehicleStockId>" + vehicle.getID() + "</usedVehicleStockId>");

            soapMessage.append("<clientId>" + DataManager.getInstance().user.getDefaultClient().getId() + "</clientId>");
            soapMessage.append("<recipient>");
            soapMessage.append("<EmailAddress xmlns=\"" + Constants.e_BROCHURE_NAMESPACE + "\">" + etEmailAddress.getText().toString().trim() + "</EmailAddress>");
            soapMessage.append("<FirstName xmlns=\"" + Constants.e_BROCHURE_NAMESPACE + "\">" + etFirstName.getText().toString().trim() + "</FirstName>");
            soapMessage.append("<MobileNumber xmlns=\"" + Constants.e_BROCHURE_NAMESPACE + "\">" + etMobileNumber.getText().toString().trim() + "</MobileNumber>");
            soapMessage.append("<Surname xmlns=\"" + Constants.e_BROCHURE_NAMESPACE + "\">" + etLastname.getText().toString().trim() + "</Surname>");
            soapMessage.append("</recipient>");
            soapMessage.append("<messageBoxComments>" + etCommentStock.getText().toString().trim() + "</messageBoxComments>");
            soapMessage.append("<options>");
            if (sendPhotos)
            {
                soapMessage.append("<ElectronicBrochureOption xmlns=\"" + Constants.e_BROCHURE_NAMESPACE + "\">");
                soapMessage.append("<OptionName>UsedImages</OptionName>");
                soapMessage.append("<OptionValue>true</OptionValue>");
                soapMessage.append("</ElectronicBrochureOption>");
            } else
            {
                soapMessage.append("<ElectronicBrochureOption xmlns=\"" + Constants.e_BROCHURE_NAMESPACE + "\">");
                soapMessage.append("<OptionName>UsedImages</OptionName>");
                soapMessage.append("<OptionValue>false</OptionValue>");
                soapMessage.append("</ElectronicBrochureOption>");
            }
            if (sendVideos)
            {
                soapMessage.append("<ElectronicBrochureOption xmlns=\"" + Constants.e_BROCHURE_NAMESPACE + "\">");
                soapMessage.append("<OptionName>UsedVideos</OptionName>");
                soapMessage.append("<OptionValue>true</OptionValue>");
                soapMessage.append("</ElectronicBrochureOption>");
            } else
            {
                soapMessage.append("<ElectronicBrochureOption xmlns=\"" + Constants.e_BROCHURE_NAMESPACE + "\">");
                soapMessage.append("<OptionName>UsedVideos</OptionName>");
                soapMessage.append("<OptionValue>false</OptionValue>");
                soapMessage.append("</ElectronicBrochureOption>");
            }
            soapMessage.append("</options>");
            if (videoCodes.size() != 0)
            {
                soapMessage.append("<personalizedVideoLinkList>");
                for (int i = 0; i < videoCodes.size(); i++)
                {
                    soapMessage.append("<ElectronicBrochureVideoLink xmlns=\"" + Constants.e_BROCHURE_NAMESPACE + "\">");
                    soapMessage.append("<Address>" + videoCodes.get(i).replaceAll("\"", "") + "</Address>");
                    if (uploaded_video.get(i).isSearchable())
                    {
                        soapMessage.append("<IsPrivate>0</IsPrivate>");
                    } else
                    {
                        soapMessage.append("<IsPrivate>1</IsPrivate>");
                    }
                    soapMessage.append("</ElectronicBrochureVideoLink>");
                }
                soapMessage.append("</personalizedVideoLinkList>");
            }
            if (isWithImages)
            {
                soapMessage.append("<personalizedImageListToken>" + personalizedImageListToken + "</personalizedImageListToken>");
                if (isFromVariantList)
                {
                    soapMessage.append("</SendNewBrochureToAddressWithImages>");
                } else
                {
                    soapMessage.append("</SendBrochureToAddressWithImages>");
                }

            } else
            {
                if (isFromVariantList)
                {
                    soapMessage.append("</SendNewBrochureToAddressWithoutImages>");
                } else
                {
                    soapMessage.append("</SendBrochureToAddressWithoutImages>");
                }
            }
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            Helper.Log("SendBrochure request", "" + soapMessage);

            VollyResponseListener listener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    VolleyLog.e("Error: ", error.toString());

                }

                @Override
                public void onResponse(String response)
                {
                    hideProgressDialog();
                    if (response == null)
                    {
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_sendiing_data), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                getFragmentManager().popBackStack();
                            }
                        });
                        return;
                    }
                    Helper.Log("SendBrochure", "" + response);
                    boolean isSuccess = Boolean.parseBoolean(ParserManager.parsetokenChecker(response, "a:IsSuccess"));
                    if (isSuccess)
                    {
                        if (isVideoUploadFail)
                        {
                            message = "Brochure has been sent successfully but could not attach the video/s.";
                        } else
                        {
                            message = "Brochure sent successfully";
                        }
                        uploaded_video.clear();
                        personalizedImageListToken = null;
                    } else
                    {
                        message = "Error while sending brochure please try later.";
                    }
                    hideProgressDialog();
                    CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
                    {
                        @Override
                        public void onButtonClicked(int type)
                        {
                            getFragmentManager().popBackStack();
                        }
                    });
                }
            };

            if (isWithImages)
            {
                if (isFromVariantList)
                {
                    request = new VollyCustomRequest(Constants.EBROCHURE_WEBSERVICE_URL, soapMessage.toString()
                            , Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/SendNewBrochureToAddressWithImages", listener);
                } else
                {
                    request = new VollyCustomRequest(Constants.EBROCHURE_WEBSERVICE_URL, soapMessage.toString()
                            , Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/SendBrochureToAddressWithImages", listener);
                }

            } else
            {
                if (isFromVariantList)
                {
                    request = new VollyCustomRequest(Constants.EBROCHURE_WEBSERVICE_URL, soapMessage.toString()
                            , Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/SendNewBrochureToAddressWithoutImages", listener);
                } else
                {
                    request = new VollyCustomRequest(Constants.EBROCHURE_WEBSERVICE_URL, soapMessage.toString()
                            , Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/SendBrochureToAddressWithoutImages", listener);
                }
            }
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

    private void getVariantDetailsSoap()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("variantID", variant.getVariantId(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("VariantDetailsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/VariantDetailsXML");
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
                        if (result != null)
                        {
                            Helper.Log("response", "" + result.toString());
                            details = new VehicleDetails();

                            SoapObject outer = (SoapObject) result;
                            SoapObject inner = (SoapObject) outer.getPropertySafely("Details");

                            if (inner.hasProperty("Videos") && infoFragment == null && previewFragment == null)
                            {
                                SoapObject videoObj = (SoapObject) inner.getPropertySafely("Videos");
                                YouTubeVideo tubeVideo = null;
                                for (int i = 0; i < videoObj.getPropertyCount(); i++)
                                {
                                    tubeVideo = new YouTubeVideo();
                                    SoapObject videoListObj = (SoapObject) videoObj.getProperty(i);
                                    tubeVideo.setVideo_ID(videoListObj.getPropertySafelyAsString("youtubeVideoID", ""));
                                    tubeVideo.setLocal(false);
                                    tubeVideo.setVideo_Description(videoListObj.getPropertySafelyAsString("description", ""));
                                    tubeVideo.setSearchable(Boolean.parseBoolean(inner.getPropertySafelyAsString("Searchable", "false")));
                                    tubeVideo.setVideo_Tags(videoListObj.getPropertySafelyAsString("Keywords", ""));
                                    tubeVideo.setVideo_title(videoListObj.getPropertySafelyAsString("title", ""));
                                    tubeVideo.setVideoLinkID(Integer.parseInt(videoListObj.getPropertySafelyAsString("VideoLinkID", "")));
                                    tubeVideo.setVideoCode(videoListObj.getPropertySafelyAsString("youtubeID", ""));
                                    tubeVideo.setVideoFullPath(videoListObj.getPropertySafelyAsString("videoURL", ""));
                                    tubeVideo.setVideoThumbUrl("http://img.youtube.com/vi/" + tubeVideo.getVideoCode() + "/0.jpg");
                                    uploadedVideos.add(tubeVideo);
                                }
                                DataManager.getInstance().setYouTubeVideos(uploadedVideos);
                            }
                            if (inner.hasProperty("SpecDetails"))
                            {
                                SoapObject specListObj = (SoapObject) inner.getPropertySafely("SpecDetails");
                                Helper.Log("specobject line", specListObj.toString());
                                vehicleDetails = new VehicleDetails();
                                for (int i = 0; i < specListObj.getPropertyCount(); i++)
                                {
                                    String name = ((SoapPrimitive) specListObj.getProperty(i)).getAttribute(1).toString();
                                    //spec.setId(Integer.parseInt((String) specObj.getAttributeSafelyAsString("name")));
                                    String value = ((SoapPrimitive) specListObj.getProperty(i)).getValue().toString();
                                    parseResponse(name, value);
                                }
                                loadspec(vehicleDetails);
                            } else
                            {
                                llSpecs.setVisibility(View.GONE);
                            }

                            if (inner.hasProperty("images"))
                            {
                                SoapObject imgListObj = (SoapObject) inner.getPropertySafely("images");
                                ArrayList<VehicleImage> imageList = new ArrayList<VehicleImage>();
                                VehicleImage image = null;
                                for (int i = 0; i < imgListObj.getPropertyCount(); i++)
                                {
                                    image = new VehicleImage();
                                   /* SoapObject imgObj = (SoapObject) imgListObj.getProperty(i);
                                    image.setUciid(Integer.parseInt(imgObj.getPropertySafelyAsString("uciID", "0")));
                                    image.setId(Integer.parseInt(imgObj.getPropertySafelyAsString("imageID", "0")));
                                    image.setImageTitle(imgObj.getPropertySafelyAsString("imageTitle2", ""));
                                    image.setPriority(Integer.parseInt(imgObj.getPropertySafelyAsString("imagePriority", "0")));
                                    image.setImageTypeName(imgObj.getPropertySafelyAsString("imageTypeName", ""));
                                    image.setImageSource(imgObj.getPropertySafelyAsString("imageSource", ""));
                                    image.setPath(imgObj.getPropertySafelyAsString("imagePath", ""));
                                    image.setLink(imgObj.getPropertySafelyAsString("imageLink", ""));
                                    image.setImageSize(Integer.parseInt(imgObj.getPropertySafelyAsString("imageSize", "0")));
                                    image.setImageRes(imgObj.getPropertySafelyAsString("imageRes", ""));
                                    image.setType(Integer.parseInt(imgObj.getPropertySafelyAsString("imageType", "0")));*/
                                    //image.setImagedpi((int) Float.parseFloat(imgObj.getPropertySafelyAsString("imageDPI", "0")));

                                    image.setLink(imgListObj.getProperty(i).toString());
                                    imageList.add(image);
                                }
                                details.setImageList(imageList);
                                images = new ArrayList<MyImage>();
                                for (int i = 0; i < details.getImageList().size(); i++)
                                {
                                    BaseImage baseImage = details.getImageList().get(i);
                                    MyImage image1 = new MyImage();
                                    image1.setThumb(baseImage.getLink());
                                    image1.setFull(baseImage.getLink());
                                    images.add(image1);
                                }
                            }

                            if (details.getImageList() != null && details.getImageList().size() != 0)
                            {
                                flItemStockBig.setVisibility(View.VISIBLE);
                                ivItemStockList.setImageUrl(images.get(0).getThumb(), imageLoader);
                                if (images.size() == 1)
                                {
                                    hlvStockCarImages.setVisibility(View.GONE);
                                } else
                                {
                                    hlvStockCarImages.setVisibility(View.VISIBLE);
                                }
                            } else
                            {
                                flItemStockBig.setVisibility(View.GONE);
                                hlvStockCarImages.setVisibility(View.GONE);
                                ivItemStockList.setErrorImageResId(R.drawable.no_media);
                            }
                            if (!TextUtils.isEmpty(details.getInternalnote()))
                            {
                                tvInternalMemo.setText(details.getInternalnote());
                            } else
                            {
                                llInternalMemo.setVisibility(View.GONE);
                            }
                            if (!TextUtils.isEmpty(details.getComments()))
                            {
                                tvStockComment.setText(details.getComments());
                            } else
                            {
                                tvStockComment.setText("None loaded");
                            }
                            if (!TextUtils.isEmpty(details.getExtras()))
                            {
                                tvStockExtras.setText(details.getExtras());
                            } else
                            {
                                tvStockExtras.setText("None loaded");
                            }
                            tvStockType.setText(department);
                            tempList = new ArrayList<MyImage>();
                            tempList.addAll(images);
                            if (!tempList.isEmpty())
                                tempList.remove(0);
                            horizontalListViewAdapter = new HorizontalListViewAdapter(getActivity(), tempList);
                            hlvStockCarImages.setAdapter(horizontalListViewAdapter);
                            serverVideoLoading(uploadedVideos);
                            hideProgressDialog();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                        ivItemStockList.setErrorImageResId(R.drawable.no_media);
                        ivItemStockList.setDefaultImageResId(R.drawable.no_media);
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

}
