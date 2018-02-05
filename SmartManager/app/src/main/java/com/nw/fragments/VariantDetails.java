package com.nw.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.greysonparrelli.permiso.Permiso;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nw.adapters.HorizontalListVideoAdapter;
import com.nw.broadcast.NetworkUtil;
import com.nw.database.SMDatabase;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.model.BaseImage;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.ScanVIN;
import com.nw.model.SmartObject;
import com.nw.model.Variant;
import com.nw.model.VehicleImage;
import com.nw.model.VehicleObject;
import com.nw.model.YouTubeVideo;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.DragableGridView;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Connectivity;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;
import com.utils.ImageHelper;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class VariantDetails extends BaseFragement implements OnClickListener
{
    EditText edType, edtColor, edtMilage, edtStock, edtPriceRetail, edtPriceTrade, edtProgramName, edtCondition, edtExtras, edtVIN,
            edtEngineNo, edtRegNo, edtOEM, edtLocation, edtTrim, edtCost, edtStand, edtInternalNote, edtComments, edYear;
    CheckBox cbProgramVehicle, cbDontImport, cbIgnoreSetting, cbErrorInfo, cbRemoveVehicle, cbRetail, cbTrade;
    RelativeLayout rlImage, rlVideo;
    LinearLayout botttomLayout;
    TextView tvtVariant, tvtMAndM, tvName, tvAdditionalInfo, tvAddTender;
    Button btnSave, btnUpdate, btnSaveClose;
    boolean isShowcaseShown = false, flagForClose = false;
    ArrayList<SmartObject> vehicleTypeList;
    ArrayList<SmartObject> tenderList;
    ArrayAdapter<SmartObject> vehicleAdapter, tenderAdapter;
    DragableGridView imageDragableGridView;
    NetworkImageView ivVehicleImage;
    /*DragableVideoGridView videoDragableGridView;*/
    ScrollView svVINDetails;
    FrameLayout flImages;
    Variant variant;
    ScanVIN scanVIN;
    boolean isUpdate;
    String message = "", errorMessage = "", department = "Used";
    int usedVehicleStockID = 0;
    int imageUploadIndex = 0;
    int imageProrityIndex = 0;
    int imageDeletedIndex = 0;
    int uploadVideoCount = 0;
    int deleteVideoCount = 0;
    int totalImageUploadSize = 0;
    int totalImagePriorityChangedSize = 0;
    int totalImageDeletedSize = 0;
    ListPopupWindow window;
    Button btnEdit;
    //Add image
    ArrayList<BaseImage> updatedImages;
    ImageView ivAddVideos, ivRightArrow;
    boolean isFirstImage = true, isVideoSelected = false;
    String videoPath = null, result, fromFragment = null;
    Bitmap fisrtBitmap = null, secondBitmap = null;
    VideoInfoFragment infoFragment;
    VideoPreviewFragment previewFragment;
    ArrayList<String> videoCodes = new ArrayList<>();
    ArrayList<YouTubeVideo> youTubeVideos, deletedVideos, uploaded_videos;
    DisplayImageOptions options;
    ImageLoader imageLoader;
    TableRow tableRow2;

    HorizontalListView hlvphotosExtraCarVideos;
    HorizontalListVideoAdapter horizontalListVideoAdapter;

    // 1 = Upload Now , 2 = Upload with wifi , 3 = default
    int showVideoAleart = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Permiso.getInstance().setActivity(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_variant_details_new, container, false);
        setHasOptionsMenu(true);
        CanUploadVideo();
        initView(view);
        hideKeyboard(view);
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        if (isVideoSelected)
        {
            imageDragableGridView.setImageList(DataManager.getInstance().getimageArray());
            videoloading(DataManager.getInstance().getYouTubeVideos());
        } else
        {
            if (infoFragment != null || previewFragment != null)
            {
                videoloading(DataManager.getInstance().getYouTubeVideos());
            } else
            {
                DataManager.getInstance().getYouTubeVideos().clear();
                DataManager.getInstance().getimageArray().clear();
            }
        }
        if (youTubeVideos == null)
        {
            youTubeVideos = new ArrayList<YouTubeVideo>();
        }
        if (uploaded_videos == null)
        {
            uploaded_videos = new ArrayList<YouTubeVideo>();
        }
        if (deletedVideos == null)
        {
            deletedVideos = new ArrayList<>();
        }
        return view;
    }

    private void initView(View view)
    {
        svVINDetails = (ScrollView) view.findViewById(R.id.svVINDetails);
        edType = (EditText) view.findViewById(R.id.edType);
        edYear = (EditText) view.findViewById(R.id.edYear);
        edYear.setOnClickListener(this);
        edtColor = (EditText) view.findViewById(R.id.edtColor);
        flImages = (FrameLayout) view.findViewById(R.id.flImages);
        if (getArguments().containsKey("fromLoadVehicle") || getArguments().getString("fromFragment").equalsIgnoreCase("add to stock")
                || getArguments().getString("fromFragment").equalsIgnoreCase("VINLookup"))
        {
            flImages.setVisibility(View.GONE);
        } else
        {
            flImages.setVisibility(View.VISIBLE);
        }
        edtMilage = (EditText) view.findViewById(R.id.edtMilage);
        edtStock = (EditText) view.findViewById(R.id.edtStock);
        edtPriceRetail = (EditText) view.findViewById(R.id.edtPriceRetail);
        edtPriceTrade = (EditText) view.findViewById(R.id.edtPriceTrade);
        edtProgramName = (EditText) view.findViewById(R.id.edtProgramName);
        edtCondition = (EditText) view.findViewById(R.id.edtCondition);
        edtExtras = (EditText) view.findViewById(R.id.edtExtras);
        edtVIN = (EditText) view.findViewById(R.id.edtVIN);
        edtEngineNo = (EditText) view.findViewById(R.id.edtEngineNo);
        edtRegNo = (EditText) view.findViewById(R.id.edtRegNo);
        edtOEM = (EditText) view.findViewById(R.id.edtOEM);
        edtTrim = (EditText) view.findViewById(R.id.edtTrim);
        edtCost = (EditText) view.findViewById(R.id.edtCost);
        edtStand = (EditText) view.findViewById(R.id.edtStand);
        edtInternalNote = (EditText) view.findViewById(R.id.edtInternalNote);
        edtComments = (EditText) view.findViewById(R.id.edtComments);
        edtLocation = (EditText) view.findViewById(R.id.edtLocation);
        ivVehicleImage = (NetworkImageView) view.findViewById(R.id.ivVehicleImage);
        cbProgramVehicle = (CheckBox) view.findViewById(R.id.cbProgramVehicle);
        cbRetail = (CheckBox) view.findViewById(R.id.cbRetail);
        cbTrade = (CheckBox) view.findViewById(R.id.cbTrade);
        tableRow2 = (TableRow) view.findViewById(R.id.tableRow2);
        tableRow2.setOnClickListener(this);
        cbDontImport = (CheckBox) view.findViewById(R.id.cbDontImport);
        cbIgnoreSetting = (CheckBox) view.findViewById(R.id.cbIgnoreSetting);
        cbErrorInfo = (CheckBox) view.findViewById(R.id.cbErrorInfo);
        cbRemoveVehicle = (CheckBox) view.findViewById(R.id.cbRemoveVehicle);
        rlImage = (RelativeLayout) view.findViewById(R.id.rlImage);
        rlVideo = (RelativeLayout) view.findViewById(R.id.rlVideo);
        botttomLayout = (LinearLayout) view.findViewById(R.id.botttomLayout);
        tvtVariant = (TextView) view.findViewById(R.id.tvtVariant);
        tvtMAndM = (TextView) view.findViewById(R.id.tvtMAndM);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvAdditionalInfo = (TextView) view.findViewById(R.id.tvAdditionalInfo);
        tvAddTender = (TextView) view.findViewById(R.id.tvAddTender);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);
        btnSaveClose = (Button) view.findViewById(R.id.btnSaveClose);
        ivRightArrow = (ImageView) view.findViewById(R.id.ivRightArrow);
        btnSave.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnSaveClose.setOnClickListener(this);
        ivVehicleImage.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (imageDragableGridView.getUpdatedImageListWithoutPlus() != null)
                {
                    navigateToLargeImage(0);
                }
            }
        });
        if (getArguments() != null)
        {
            fromFragment = getArguments().getString("fromFragment");
            scanVIN = getArguments().getParcelable("data");
            if (scanVIN != null)
                variant = scanVIN.getVariant();
            if (getArguments().containsKey("update"))
                isUpdate = getArguments().getBoolean("update");
            edYear.setText(scanVIN.getYear());
            if (variant != null)
            {

                tvtMAndM.setText("M&M : " + variant.getMeadCode());
                if (!TextUtils.isEmpty(variant.getDetails()))
                {
                    tvName.setVisibility(View.VISIBLE);
                    String string = variant.getDetails();
                    String data = StringUtils.substringAfter(string, "|");
                    tvName.setText(StringUtils.substringBetween(data, "|", "|")
                            + " | " + StringUtils.substringAfterLast(data, "|")
                            + " | " + StringUtils.substringBefore(data, "|"));
                } else
                {
                    tvName.setVisibility(View.GONE);
                }
                if (!isUpdate) // not update
                    tvtVariant.setText(Html.fromHtml("<font color=#ffffff>" + variant.getYear() + " " + "</font>" + variant.getFriendlyName()));
                else
                    tvtVariant.setText(Html.fromHtml("<font color=#ffffff>" + variant.getYear() + " " + "</font>" + variant.getFriendlyName()));
            }
            if (scanVIN != null)
            {
                edtColor.setText(Helper.checkEmpty(scanVIN.getColour()));
                edtVIN.setText(Helper.checkEmpty(scanVIN.getVIN()));
                edtEngineNo.setText(Helper.checkEmpty(scanVIN.getEngineNumber()));
                edtRegNo.setText(Helper.checkEmpty(scanVIN.getRegistration()));
            }
            if (!isUpdate)
            {
                btnSave.setVisibility(View.VISIBLE);

                message = getString(R.string.stock_saved_successfully);
                errorMessage = getString(R.string.stock_details_not_saved);

                cbRetail.setChecked(true);
                cbTrade.setChecked(false);
            } else
            {
                message = getString(R.string.stock_updated_successfully);
                errorMessage = getString(R.string.stock_details_not_saved);
                edtStock.setText(scanVIN.getStock().getName());
            }
        }
        cbTrade.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                if (arg1)
                {
                    tvAddTender.setText("");
                    tvAddTender.setTag("");
                }
            }
        });
        cbRetail.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                if (arg1)
                {
                    tvAddTender.setText("");
                    tvAddTender.setTag("");
                }
            }
        });
        edType.setOnClickListener(this);
        tvAddTender.setOnClickListener(this);
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

        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        btnEdit.setVisibility(View.GONE);
        ivAddVideos = (ImageView) view.findViewById(R.id.ivAddVideos);
        ivAddVideos.setOnClickListener(this);
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
                                   // horizontalListVideoAdapter.notifyDataSetChanged();
                                    videoloading(DataManager.getInstance().getYouTubeVideos());
                                }
                            }
                        });
            }
        });
    }

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


	/*
     * Navigate to video info screen fragment
	 * As Fragment to called belongs to other activity we call activity and pass parameters through intent
	 * Parameter- position of image clicked
	 * */

    private void videoInfoScreen(String videoPath)
    {
        saveImagesToDataManager();

        infoFragment = new VideoInfoFragment();
        Bundle args = new Bundle();
        args.putString("videoPath", videoPath);
        args.putString("default_name", tvtVariant.getText().toString() + "-" + edtStock.getText().toString());
        infoFragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.Container, infoFragment).addToBackStack("").commit();
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
            iToBuyActivity.putExtra("vehicleName", scanVIN.getVariant().getFriendlyName());
            startActivity(iToBuyActivity);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if(isVideoSelected == false)
        {
            if (variant != null)
            {
                //updated
                if (isUpdate)
                    getVehicleDetailsSoap();

                getVatientDetails();
            } else
                getVehicleTypeList();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.edType:
                if (vehicleAdapter != null)
                {
                    Helper.showDropDown(edType, vehicleAdapter, new OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
                        {
                            edType.setText(vehicleAdapter.getItem(itemPosition).toString().trim());
                            edType.setTag("" + vehicleAdapter.getItem(itemPosition).getId());
                        }
                    });
                } else
                    Helper.showToast(getString(R.string.no_type), getActivity());
                break;

            case R.id.tvAddTender:

                if (tenderAdapter != null)
                {
                    Helper.showDropDown(tvAddTender, tenderAdapter, new OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
                        {
                            // retail and trade will false
                            cbTrade.setChecked(false);
                            cbRetail.setChecked(false);
                            tvAddTender.setText(tenderAdapter.getItem(itemPosition).toString());
                            tvAddTender.setTag("" + tenderAdapter.getItem(itemPosition).getId());
                        }
                    });
                } else
                    Helper.showToast(getString(R.string.no_tender), getActivity());

                break;

            case R.id.tableRow2:
                if (botttomLayout.getVisibility() == View.VISIBLE)
                {
                    botttomLayout.setVisibility(View.GONE);
                    ivRightArrow.setRotation(270);
                } else
                {
                    botttomLayout.setVisibility(View.VISIBLE);
                    ivRightArrow.setRotation(0);
                }
                break;
            case R.id.btnSave:
                if (!isUpdate)
                    addVehicleXML(false);
                else
                    updateVehicleXML(false);
                break;

            case R.id.btnSaveClose:
                if (!isUpdate)
                    addVehicleXML(true);
                else
                    updateVehicleXML(true);
                break;

            case R.id.btnUpdate:
                break;

            case R.id.edYear:
                showToPopUp(v);
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
        /*if (videoDragableGridView != null)
        {
			if (videoDragableGridView.isOptionSelected())
				videoDragableGridView.onActivityResult(requestCode, resultCode,	data);
		}*/
    }

    private void showToPopUp(View v)
    {
        int defaultYear = 1990;
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        cal.set(Calendar.YEAR, defaultYear);

        int subtraction = nowYear - defaultYear;
        final List<String> years = new ArrayList<String>();
        int i = 0;
        cal.set(Calendar.YEAR, defaultYear);
        while (i <= subtraction)
        {
            years.add(cal.get(Calendar.YEAR) + i + "");
            i++;
        }
        Collections.reverse(years);
        final EditText ed = (EditText) v;
        final String lastYear = ed.getText().toString().trim();

        Helper.showDropDownYear(v, new ArrayAdapter<String>(getActivity(), R.layout.list_item_text3, R.id.tvText, years), new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ed.setText(years.get(position) + "");
                if (scanVIN != null)
                    scanVIN.setYear(ed.getText().toString());

            }
        });
        /*if (window.isShowing())
        {
			if (v.getId() == R.id.maxYear)
				window.getListView().setSelection(years.size() - 1);
		}*/
    }

    private void getVehicleTypeList()
    {
        if (vehicleTypeList == null)
            vehicleTypeList = new ArrayList<SmartObject>();
        else
            vehicleTypeList.clear();

        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            DataInObject dataInObject = new DataInObject();
            dataInObject.setMethodname("ListVehicletypeXML");
            dataInObject.setNamespace(Constants.TEMP_URI_NAMESPACE);
            dataInObject.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListVehicletypeXML");
            dataInObject.setUrl(Constants.STOCK_WEBSERVICE_URL);
            dataInObject.setParameterList(parameters);
            new WebServiceTask(getActivity(), dataInObject, false,
                    new TaskListener()
                    {
                        @Override
                        public void onTaskComplete(Object response)
                        {
                            if (response != null)
                            {
                                if (response instanceof SoapFault)
                                {

                                } else
                                {
                                    // not fault
                                    try
                                    {
                                        if (((SoapObject) response).getPropertyCount() > 0)
                                        {
                                            SoapObject SoapvehicleTypes = (SoapObject) ((SoapObject) response).getProperty(0);
                                            for (int index = 0; index < SoapvehicleTypes.getPropertyCount(); index++)
                                            {
                                                SoapObject SoapVehicle = (SoapObject) SoapvehicleTypes.getProperty(index);
                                                vehicleTypeList.add(new SmartObject(Integer.parseInt(SoapVehicle.getPropertySafelyAsString("vtID", 0)),
                                                        SoapVehicle.getPropertySafelyAsString("vtName", "")));
                                            }

                                            if (vehicleTypeList != null && !vehicleTypeList.isEmpty())
                                            {
                                                vehicleAdapter = new ArrayAdapter<SmartObject>(getActivity(), R.layout.list_item_text2, vehicleTypeList);
                                                for (SmartObject smartObject : vehicleTypeList)
                                                {
                                                    // set used type as default
                                                    // type
                                                    if (smartObject.getName().equalsIgnoreCase(department))
                                                    {
                                                        edType.setText(smartObject.toString());
                                                        edType.setTag("" + smartObject.getId());
                                                        break;
                                                    }

                                                }
                                            } else
                                            {
                                                if (!TextUtils.isEmpty(Helper.checkEmpty(department)))
                                                {
                                                    edType.setText(Helper.checkEmpty(department));
                                                    edType.setTag("" + 2);
                                                }
                                            }

                                        }
                                    } catch (Exception e)
                                    {
                                        hideProgressDialog();
                                        e.printStackTrace();
                                    }
                                }
                            } else
                            {
                                hideProgressDialog();
                            }

                            getTenderList();
                        }
                    }).execute();
        } else
        {
            hideProgressDialog();
            HelperHttp.showNoInternetDialog(getActivity());
        }

    }

    private void getTenderList()
    {
        if (tenderList == null)
            tenderList = new ArrayList<SmartObject>();
        else
            tenderList.clear();

        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            DataInObject dataInObject = new DataInObject();
            dataInObject.setMethodname("ListTenderXML");
            dataInObject.setNamespace(Constants.TEMP_URI_NAMESPACE);
            dataInObject.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListTenderXML");
            dataInObject.setUrl(Constants.STOCK_WEBSERVICE_URL);
            dataInObject.setParameterList(parameters);
            new WebServiceTask(getActivity(), dataInObject, false, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object response)
                {
                    if (response != null)
                    {
                        if (response instanceof SoapFault)
                        {
                        } else
                        {
                            try
                            {
                                if (((SoapObject) response).getPropertyCount() > 0)
                                {
                                    SoapObject SoapvehicleTypes = (SoapObject) ((SoapObject) response).getProperty(0);
                                    for (int index = 0; index < SoapvehicleTypes.getPropertyCount(); index++)
                                    {
                                        SoapObject SoapVehicle = (SoapObject) SoapvehicleTypes.getProperty(index);
                                        tenderList.add(new SmartObject(Integer.parseInt(SoapVehicle.getPropertySafelyAsString("tenderID", 0)),
                                                SoapVehicle.getPropertySafelyAsString("tenderName", "")));
                                    }
                                    if (tenderList != null && !tenderList.isEmpty())
                                    {
                                        tenderAdapter = new ArrayAdapter<SmartObject>(getActivity(), R.layout.list_item_text2, tenderList);
                                    }
                                }
                            } catch (Exception e)
                            {
                                hideProgressDialog();
                                e.printStackTrace();
                            } finally
                            {
                                hideProgressDialog();
                            }
                        }
                        hideProgressDialog();
                    } else
                    {
                        hideProgressDialog();
                    }
                    if (!isShowcaseShown)
                    {
                        isShowcaseShown = true;
                    }
                }
            }).execute();
        } else
        {
            hideProgressDialog();
            HelperHttp.showNoInternetDialog(getActivity());
        }

    }

    private void addVehicleXML(final boolean flag)
    {
        if (TextUtils.isEmpty(edYear.getText().toString().trim()))
        {
            svVINDetails.scrollTo(0, edYear.getTop() + 20);
            Helper.showToast(getString(R.string.select_year1), getActivity());
            edYear.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edtColor.getText().toString().trim()))
        {
            svVINDetails.scrollTo(0, edtColor.getTop() + 20);
            Helper.showToast(getString(R.string.no_color), getActivity());
            edtColor.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edtMilage.getText().toString().trim()))
        {
            svVINDetails.scrollTo(0, edtMilage.getTop() + 20);
            Helper.showToast(getString(R.string.no_milage), getActivity());
            edtMilage.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edtStock.getText().toString().trim()))
        {
            svVINDetails.scrollTo(0, edtStock.getTop() + 20);
            Helper.showToast(getString(R.string.no_stock_number), getActivity());
            edtStock.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edtPriceRetail.getText().toString().trim()))
        {
            svVINDetails.scrollTo(0, edtPriceRetail.getTop() + 20);
            Helper.showToast(getString(R.string.no_retail_price), getActivity());
            edtPriceRetail.requestFocus();
            return;
        }

        if (HelperHttp.isNetworkAvailable(getActivity()))
        {

            if (imageDragableGridView.getLocalImageListWithoutPlus().size() > 0)
            {
                // check for slower connection
                if (!Connectivity.isConnectedFast(getActivity()))
                {
                    CustomDialogManager.showOkCancelDialog(getActivity(), "Please be advised you are currently on a slow data network do you wish to proceed?", "Yes", "No", new DialogListener()
                    {
                        @Override
                        public void onButtonClicked(int type)
                        {
                            if (type == Dialog.BUTTON_NEGATIVE)
                                return;
                            else
                                addVehicle2(flag);
                        }
                    });
                } else
                    addVehicle2(flag);
            } else
                addVehicle2(flag);

        } else
            HelperHttp.showNoInternetDialog(getActivity());
    }

    private void addVehicle2(final boolean flag)
    {
        try
        {
            showProgressDialog();
            final StringBuilder soapBuilder = new StringBuilder();
            soapBuilder.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body>");
            soapBuilder.append("<AddVehicleViaObj xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\"><userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapBuilder.append("<ClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</ClientID>");
            soapBuilder.append("<vehicleObject xmlns:d4p1=\"http://schemas.datacontract.org/2004/07/StockServiceNS\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">");
            soapBuilder.append("<d4p1:Colour>" + Helper.getCDATAString(edtColor) + "</d4p1:Colour>");
            soapBuilder.append("<d4p1:Comments>" + Helper.getCDATAString(edtComments) + "</d4p1:Comments>");
            soapBuilder.append("<d4p1:Condition>" + Helper.getCDATAString(edtCondition) + " </d4p1:Condition>");
            soapBuilder.append("<d4p1:DeleteReason>" + "" + "</d4p1:DeleteReason>");
            soapBuilder.append("<d4p1:DepartmentID>" + Integer.parseInt(edType.getTag() == null ? "2" : edType.getTag().toString()) + "</d4p1:DepartmentID>");
            soapBuilder.append("<d4p1:EngineNo>" + Helper.getCDATAString(edtEngineNo) + "</d4p1:EngineNo>");
            soapBuilder.append("<d4p1:Extras>" + Helper.getCDATAString(edtExtras) + "</d4p1:Extras>");
            soapBuilder.append("<d4p1:FullServiceHistory>0</d4p1:FullServiceHistory>");
            soapBuilder.append("<d4p1:IgnoreImport>" + (cbDontImport.isChecked() ? 1 : 0) + "</d4p1:IgnoreImport>");
            soapBuilder.append("<d4p1:InternalNote>" + Helper.getCDATAString(edtInternalNote) + "</d4p1:InternalNote>");
            soapBuilder.append("<d4p1:IsDeleted>0</d4p1:IsDeleted>");
            soapBuilder.append("<d4p1:IsProgram>" + (cbProgramVehicle.isChecked() ? 1 : 0) + "</d4p1:IsProgram>");
            soapBuilder.append("<d4p1:IsRetail>" + (cbRetail.isChecked() ? 1 : 0) + "</d4p1:IsRetail>");
            soapBuilder.append("<d4p1:IsTender>" + (TextUtils.isEmpty(tvAddTender.getText().toString()) ? 0 : 1) + "</d4p1:IsTender>");
            soapBuilder.append("<d4p1:IsTrade>" + (cbTrade.isChecked() ? 1 : 0) + "</d4p1:IsTrade>");
            soapBuilder.append("<d4p1:Location>" + Helper.getCDATAString(edtLocation) + "</d4p1:Location>");
            soapBuilder.append("<d4p1:MMCode>" + variant.getMeadCode() + "</d4p1:MMCode>");
            soapBuilder.append("<d4p1:ManufacturerModelCode>" + Helper.getCDATAString(edtOEM) + "</d4p1:ManufacturerModelCode>");
            soapBuilder.append("<d4p1:Mileage>" + Helper.getCDATAString(edtMilage) + "</d4p1:Mileage>");
            soapBuilder.append("<d4p1:OriginalCost>" + Helper.getDoubleValue(edtCost) + "</d4p1:OriginalCost>");
            soapBuilder.append("<d4p1:Override>" + (cbIgnoreSetting.isChecked() ? 1 : 0) + "</d4p1:Override>");
            soapBuilder.append("<d4p1:OverrideReason >" + "" + "</d4p1:OverrideReason>");
            soapBuilder.append("<d4p1:PlusAccessories>0.0</d4p1:PlusAccessories>");
            soapBuilder.append("<d4p1:PlusAdmin>0.0</d4p1:PlusAdmin>");
            soapBuilder.append("<d4p1:PlusMileage>0.0</d4p1:PlusMileage>");
            soapBuilder.append("<d4p1:PlusRecon>0.0</d4p1:PlusRecon>");
            soapBuilder.append("<d4p1:Price>" + Helper.getDoubleValue(edtPriceRetail) + "</d4p1:Price>");
            soapBuilder.append("<d4p1:ProgramName>" + Helper.getCDATAString(edtProgramName) + "</d4p1:ProgramName>");
            soapBuilder.append("<d4p1:Registration>" + Helper.getCDATAString(edtRegNo) + "</d4p1:Registration>");
            soapBuilder.append("<d4p1:ShowErrorDisclaimer>" + (cbErrorInfo.isChecked() ? 1 : 0) + "</d4p1:ShowErrorDisclaimer>");
            soapBuilder.append("<d4p1:Standin>" + Helper.getDoubleValue(edtStand) + "</d4p1:Standin>");
            soapBuilder.append("<d4p1:StockCode>" + Helper.getCDATAString(edtStock) + "</d4p1:StockCode>");
            soapBuilder.append("<d4p1:TouchMethod>android App</d4p1:TouchMethod>");
            soapBuilder.append("<d4p1:TradePrice>" + Helper.getDoubleValue(edtPriceTrade) + "</d4p1:TradePrice>");
            soapBuilder.append("<d4p1:Trim>" + Helper.getCDATAString(edtTrim) + "</d4p1:Trim>");
            soapBuilder.append("<d4p1:UsedVehicleStockID>0</d4p1:UsedVehicleStockID>");
            soapBuilder.append("<d4p1:UsedYear>" + scanVIN.getYear() + "</d4p1:UsedYear>");
            soapBuilder.append("<d4p1:VIN>" + Helper.getCDATAString(edtVIN) + "</d4p1:VIN>");
            soapBuilder.append("</vehicleObject>");
            soapBuilder.append("</AddVehicleViaObj></s:Body></s:Envelope>");
            Helper.Log("AddVehicleViaObj request", soapBuilder.toString());


            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    VolleyLog.e("AddVehicleViaObj Error: ", error.getMessage());
                    CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                }

                @Override
                public void onResponse(String response)
                {
                    if (response == null)
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                        return;
                    }
                    if (!TextUtils.isEmpty(response.toString()))
                    {
                        Helper.Log("AddVehicleViaObj Response:", "" + response);
                        String result[] = new String[2];
                        String parseResult = ParserManager.parseAddVehicleResponse(response);
                        result = parseResult.split(":");
                        if (result[0].contains("OK"))
                        {
                            if (!TextUtils.isEmpty(result[1]))
                            {
                                usedVehicleStockID = Integer.parseInt(result[1]);
                                updatedImages = imageDragableGridView.getUpdatedImageListWithoutPlus();
                                totalImageUploadSize = updatedImages.size();
                                imageUploadIndex = 0;
                                if (totalImageUploadSize > 0)
                                {
                                    addImageToVehicleBase64(usedVehicleStockID, flag);
                                } else
                                {
                                    flagForClose = true;
                                    if (isVideoSelected)
                                    {
                                        uploadWarning(flag);
                                    } else
                                    {
                                        showSuccessDialog(flag);
                                    }
                                }
                            }
                        } else
                        {
                            hideProgressDialog();
                            if (result[0].contains("ERROR"))
                            {
                                if (!TextUtils.isEmpty(result[1]))
                                {
                                    //CustomDialogManager.showOkDialog(getActivity(), ""+result[1]);
                                    CustomDialogManager.showOkDialog(getActivity(), getActivity().getResources().getString(R.string.error_msg));
                                } else
                                    CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                            } else
                                CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                    }
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapBuilder.toString(), Constants.TEMP_URI_NAMESPACE + "IStockService/AddVehicleViaObj", vollyResponseListener);

            try
            {
                request.init();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }

        } catch (NumberFormatException e1)
        {
            e1.printStackTrace();
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private void addVehicle1(final boolean flag)
    {
        try
        {
            if (updatedImages == null)
                updatedImages = new ArrayList<BaseImage>();
            else
                updatedImages.clear();

            showProgressDialog();
            SoapObject vehicleObject = new SoapObject("", "vehicleObject");
            vehicleObject.addAttribute("xmlns:d4p1", "http://schemas.datacontract.org/2004/07/StockServiceNS");

            vehicleObject.addProperty("d4p1:Colour", Helper.getString(edtColor));
            vehicleObject.addProperty("d4p1:Comments", Helper.getString(edtComments));
            vehicleObject.addProperty("d4p1:Condition", Helper.getString(edtCondition));
            vehicleObject.addProperty("d4p1:DeleteReason", "");

            vehicleObject.addProperty("d4p1:DepartmentID", Integer.parseInt(edType.getTag() == null ? "2" : edType.getTag().toString()));
            vehicleObject.addProperty("d4p1:EngineNo", Helper.getString(edtEngineNo));
            vehicleObject.addProperty("d4p1:Extras", Helper.getString(edtExtras));
            vehicleObject.addProperty("d4p1:FullServiceHistory", 0);

            vehicleObject.addProperty("d4p1:IgnoreImport", (cbDontImport.isChecked() ? 1 : 0));
            vehicleObject.addProperty("d4p1:InternalNote", Helper.getString(edtInternalNote));
            vehicleObject.addProperty("d4p1:IsDeleted", 0);
            vehicleObject.addProperty("d4p1:IsProgram", (cbProgramVehicle.isChecked() ? 1 : 0));

            vehicleObject.addProperty("d4p1:IsRetail", (cbRetail.isChecked() ? 1 : 0));
            vehicleObject.addProperty("d4p1:IsTender", (TextUtils.isEmpty(tvAddTender.getText().toString()) ? 0 : 1));
            vehicleObject.addProperty("d4p1:IsTrade", (cbTrade.isChecked() ? 1 : 0));
            vehicleObject.addProperty("d4p1:Location", Helper.getString(edtLocation));

            vehicleObject.addProperty("d4p1:MMCode", variant.getMeadCode());
            vehicleObject.addProperty("d4p1:ManufacturerModelCode", Helper.getString(edtOEM));
            vehicleObject.addProperty("d4p1:Mileage", Helper.getString(edtMilage));
            vehicleObject.addProperty("d4p1:OriginalCost", Helper.getDoubleValue(edtCost));

            vehicleObject.addProperty("d4p1:Override", (cbIgnoreSetting.isChecked() ? 1 : 0));
            vehicleObject.addProperty("d4p1:OverrideReason", "");
            vehicleObject.addProperty("d4p1:PlusAccessories", 0.0);
            vehicleObject.addProperty("d4p1:PlusAdmin", 0.0);

            vehicleObject.addProperty("d4p1:PlusMileage", 0.0);
            vehicleObject.addProperty("d4p1:PlusRecon", 0.0);
            vehicleObject.addProperty("d4p1:Price", Helper.getDoubleValue(edtPriceRetail));
            vehicleObject.addProperty("d4p1:ProgramName", Helper.getString(edtProgramName));

            vehicleObject.addProperty("d4p1:Registration", Helper.getString(edtRegNo));
            vehicleObject.addProperty("d4p1:ShowErrorDisclaimer", (cbErrorInfo.isChecked() ? 1 : 0));
            vehicleObject.addProperty("d4p1:Standin", Helper.getDoubleValue(edtStand));
            vehicleObject.addProperty("d4p1:StockCode", Helper.getString(edtStock));

            vehicleObject.addProperty("d4p1:TouchMethod", "android App");
            vehicleObject.addProperty("d4p1:TradePrice", Helper.getDoubleValue(edtPriceTrade));
            vehicleObject.addProperty("d4p1:Trim", Helper.getString(edtTrim));
            vehicleObject.addProperty("d4p1:UsedVehicleStockID", 0);

            vehicleObject.addProperty("d4p1:UsedYear", scanVIN.getYear());
            vehicleObject.addProperty("d4p1:VIN", Helper.getString(edtVIN));

            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("vehicleObject", vehicleObject, VehicleObject.class, ""));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("AddVehicleViaObj");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/AddVehicleViaObj");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object response)
                {
                    if (response == null)
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                        return;
                    }
                    if (!TextUtils.isEmpty(response.toString()))
                    {
                        Helper.Log("AddVehicleViaObj Response:", "" + response);
                        String result[] = new String[2];
                        result = response.toString().split(":");
                        if (result[0].contains("OK"))
                        {
                            if (!TextUtils.isEmpty(result[1]))
                            {
                                usedVehicleStockID = Integer.parseInt(result[1]);
                                updatedImages = imageDragableGridView.getUpdatedImageListWithoutPlus();
                                totalImageUploadSize = updatedImages.size();
                                imageUploadIndex = 0;
                                if (totalImageUploadSize > 0)
                                {
                                    addImageToVehicleBase64(usedVehicleStockID, flag);
                                } else
                                {
                                    showSuccessDialog(flag);

                                }
                            }
                        } else
                        {

                            hideProgressDialog();
                            if (result[0].contains("ERROR"))
                            {
                                if (!TextUtils.isEmpty(result[1]))
                                    CustomDialogManager.showOkDialog(getActivity(), "" + result[1]);
                                else
                                    CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                            } else
                                CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                    }


                }

            }).execute();

        } catch (NumberFormatException e1)
        {
            e1.printStackTrace();
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    private void updateVehicleXML(final boolean flag)
    {

        if (TextUtils.isEmpty(edType.getText().toString().trim()))
        {
            svVINDetails.scrollTo(0, edType.getTop() + 20);
            Helper.showToast(getString(R.string.select_type1), getActivity());
            edType.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtColor.getText().toString().trim()))
        {
            svVINDetails.scrollTo(0, edtColor.getTop() + 20);
            Helper.showToast(getString(R.string.no_color), getActivity());
            edtColor.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edtMilage.getText().toString().trim()))
        {
            svVINDetails.scrollTo(0, edtMilage.getTop() + 20);
            Helper.showToast(getString(R.string.no_milage), getActivity());
            edtMilage.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtStock.getText().toString().trim()))
        {
            svVINDetails.scrollTo(0, edtStock.getTop() + 20);
            Helper.showToast(getString(R.string.no_stock_number), getActivity());
            edtStock.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtPriceRetail.getText().toString().trim()))
        {
            svVINDetails.scrollTo(0, edtPriceRetail.getTop() + 20);
            Helper.showToast(getString(R.string.no_retail_price), getActivity());
            edtPriceRetail.requestFocus();
            return;
        }

        if (HelperHttp.isNetworkAvailable(getActivity()))
        {

            if (imageDragableGridView.getLocalImageListWithoutPlus().size() > 0)
            {
                // check for slower connection
                if (!Connectivity.isConnectedFast(getActivity()))
                {
                    CustomDialogManager.showOkCancelDialog(getActivity(), "Please be advised you are currently on a slow data network do you wish to proceed?", "Yes", "No", new DialogListener()
                    {
                        @Override
                        public void onButtonClicked(int type)
                        {
                            if (type == Dialog.BUTTON_NEGATIVE)
                                return;
                            else
                                updateVehicle(flag);
                        }
                    });
                } else
                    updateVehicle(flag);
            } else
                updateVehicle(flag);

        } else
            HelperHttp.showNoInternetDialog(getActivity());
    }

    private void updateVehicle(final boolean flag)
    {
        showProgressDialog();
        try
        {
            if (updatedImages == null)
                updatedImages = new ArrayList<BaseImage>();
            else
                updatedImages.clear();
            final StringBuilder soapBuilder = new StringBuilder();
            soapBuilder.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "<s:Body>");
            soapBuilder.append("<UpdateVehicleViaObj xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\"><userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapBuilder.append("<vehicleObject xmlns:d4p1=\"http://schemas.datacontract.org/2004/07/StockServiceNS\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">");
            soapBuilder.append("<d4p1:Colour>" + Helper.getCDATAString(edtColor) + "</d4p1:Colour>");
            soapBuilder.append("<d4p1:Comments>" + Helper.getCDATAString(edtComments) + "</d4p1:Comments>");
            soapBuilder.append("<d4p1:Condition>" + Helper.getCDATAString(edtCondition) + " </d4p1:Condition>");
            soapBuilder.append("<d4p1:DeleteReason>" + "" + "</d4p1:DeleteReason>");
            soapBuilder.append("<d4p1:DepartmentID>" + Integer.parseInt(edType.getTag() == null ? "2" : edType.getTag().toString()) + "</d4p1:DepartmentID>");
            soapBuilder.append("<d4p1:EngineNo>" + Helper.getCDATAString(edtEngineNo) + "</d4p1:EngineNo>");
            soapBuilder.append("<d4p1:Extras>" + Helper.getCDATAString(edtExtras) + "</d4p1:Extras>");
            soapBuilder.append("<d4p1:FullServiceHistory>0</d4p1:FullServiceHistory>");
            soapBuilder.append("<d4p1:IgnoreImport>" + (cbDontImport.isChecked() ? 1 : 0) + "</d4p1:IgnoreImport>");
            soapBuilder.append("<d4p1:InternalNote>" + Helper.getCDATAString(edtInternalNote) + "</d4p1:InternalNote>");
            soapBuilder.append("<d4p1:IsDeleted>" + (cbRemoveVehicle.isChecked() ? 1 : 0) + "</d4p1:IsDeleted>");
            soapBuilder.append("<d4p1:IsProgram>" + (cbProgramVehicle.isChecked() ? 1 : 0) + "</d4p1:IsProgram>");
            soapBuilder.append("<d4p1:IsRetail>" + (cbRetail.isChecked() ? 1 : 0) + "</d4p1:IsRetail>");
            soapBuilder.append("<d4p1:IsTender>" + (TextUtils.isEmpty(tvAddTender.getText().toString()) ? 0 : 1) + "</d4p1:IsTender>");
            soapBuilder.append("<d4p1:IsTrade>" + (cbTrade.isChecked() ? 1 : 0) + "</d4p1:IsTrade>");
            soapBuilder.append("<d4p1:Location>" + Helper.getCDATAString(edtLocation) + "</d4p1:Location>");
            soapBuilder.append("<d4p1:MMCode>" + variant.getMeadCode() + "</d4p1:MMCode>");
            soapBuilder.append("<d4p1:ManufacturerModelCode>" + Helper.getCDATAString(edtOEM) + "</d4p1:ManufacturerModelCode>");
            soapBuilder.append("<d4p1:Mileage>" + Helper.getCDATAString(edtMilage) + "</d4p1:Mileage>");
            soapBuilder.append("<d4p1:OriginalCost>" + Helper.getDoubleValue(edtCost) + "</d4p1:OriginalCost>");
            soapBuilder.append("<d4p1:Override>" + (cbIgnoreSetting.isChecked() ? 1 : 0) + "</d4p1:Override>");
            soapBuilder.append("<d4p1:OverrideReason >" + "" + "</d4p1:OverrideReason>");
            soapBuilder.append("<d4p1:PlusAccessories>0.0</d4p1:PlusAccessories>");
            soapBuilder.append("<d4p1:PlusAdmin>0.0</d4p1:PlusAdmin>");
            soapBuilder.append("<d4p1:PlusMileage>0.0</d4p1:PlusMileage>");
            soapBuilder.append("<d4p1:PlusRecon>0.0</d4p1:PlusRecon>");
            soapBuilder.append("<d4p1:Price>" + Helper.getDoubleValue(edtPriceRetail) + "</d4p1:Price>");
            soapBuilder.append("<d4p1:ProgramName>" + Helper.getCDATAString(edtProgramName) + "</d4p1:ProgramName>");
            soapBuilder.append("<d4p1:Registration>" + Helper.getCDATAString(edtRegNo) + "</d4p1:Registration>");
            soapBuilder.append("<d4p1:ShowErrorDisclaimer>" + (cbErrorInfo.isChecked() ? 1 : 0) + "</d4p1:ShowErrorDisclaimer>");
            soapBuilder.append("<d4p1:Standin>" + Helper.getDoubleValue(edtStand) + "</d4p1:Standin>");
            soapBuilder.append("<d4p1:StockCode>" + Helper.getCDATAString(edtStock) + "</d4p1:StockCode>"); //previous(20-03-2015) scanVIN.getStock().getName()
            soapBuilder.append("<d4p1:TouchMethod>android App</d4p1:TouchMethod>");
            soapBuilder.append("<d4p1:TradePrice>" + Helper.getDoubleValue(edtPriceTrade) + "</d4p1:TradePrice>");
            soapBuilder.append("<d4p1:Trim>" + Helper.getCDATAString(edtTrim) + "</d4p1:Trim>");
            soapBuilder.append("<d4p1:UsedVehicleStockID>" + scanVIN.getStock().getId() + "</d4p1:UsedVehicleStockID>");
            soapBuilder.append("<d4p1:UsedYear>" + scanVIN.getYear() + "</d4p1:UsedYear>");
            soapBuilder.append("<d4p1:VIN>" + Helper.getCDATAString(edtVIN) + "</d4p1:VIN>");
            soapBuilder.append("</vehicleObject>");
            soapBuilder.append("</UpdateVehicleViaObj></s:Body></s:Envelope>");
            Helper.Log("UpdateVehicleViaObj request", soapBuilder.toString());
            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Helper.Log("UpdateVehicleViaObj Error: ", error.getMessage());
                    showErrorDialog(errorMessage);
                }

                @Override
                public void onResponse(String response)
                {
                    if (!TextUtils.isEmpty(response))
                    {
                        Helper.Log("UpdateVehicleViaObj Response:", "" + response.toString());
                        String result[] = new String[2];
                        result = ParserManager.parseUpdateVehicleResponse(response).split(":");

                        if (result[0].contains("OK"))
                        {
                            if (!TextUtils.isEmpty(result[1]))
                            {
                                usedVehicleStockID = Integer.parseInt(result[1]);
                                updatedImages = imageDragableGridView.getUpdatedImageListWithoutPlus();
                                totalImageUploadSize = updatedImages.size();
                                imageUploadIndex = 0;
                                if (totalImageUploadSize > 0)
                                    addImageToVehicleBase64(usedVehicleStockID, flag);
                                else
                                {
                                    uploadWarning(flag);
                                }
                            } else
                            {
                                hideProgressDialog();
                            }
                        } else
                        {
                            hideProgressDialog();
                            if (result[0].contains("ERROR"))
                            {
                                if (!TextUtils.isEmpty(result[1]))
                                    CustomDialogManager.showOkDialog(getActivity(), "" + result[1]);
                                else
                                    CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                            } else
                                CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), errorMessage);
                    }
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapBuilder.toString(), Constants.TEMP_URI_NAMESPACE + "IStockService/UpdateVehicleViaObj", vollyResponseListener);

            try
            {
                request.init();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } catch (NumberFormatException e1)
        {
            e1.printStackTrace();
        } catch (Exception e2)
        {
            e2.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Permiso.getInstance().setActivity(getActivity());
        if(fromFragment.equalsIgnoreCase("VINLookup"))
        {
            showActionBar("Add To Stock");
        }else
        {
            showActionBar(fromFragment);
        }
    }

    public void getVatientDetails()
    {
        showLoadingProgressDialog();
        ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
        parameterList.add(new Parameter("variantID", variant.getVariantId(), Integer.class));
        // create web service inputs
        DataInObject inObj = new DataInObject();
        inObj.setMethodname("VariantDetails");
        inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
        inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/VariantDetails");
        inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
        inObj.setParameterList(parameterList);
        // Network call
        new WebServiceTask(getActivity(), inObj, false, new TaskListener()
        {

            @Override
            public void onTaskComplete(Object response)
            {
                try
                {
                    //	tvName.setText(getString(R.string.no_information));
                    tvName.setVisibility(View.GONE);
                    if (response != null)
                    {
                        Log.d(getTag(), response.toString());
                        if (response instanceof SoapFault || response instanceof SoapObject)
                            //tvName.setText(getString(R.string.no_information));
                            tvName.setVisibility(View.GONE);
                        else
                        {
                            try
                            {
                                String string = (String) ((SoapPrimitive) response).getValue();
                                String data = StringUtils.substringAfter(string, "|");
                                tvName.setVisibility(View.VISIBLE);
                                tvName.setText(StringUtils.substringBetween(data, "|", "|") +
                                        " | " + StringUtils.substringAfterLast(data, "|") + " | " + StringUtils.substringBefore(data, "|"));
                                /*StringTokenizer st1 = new StringTokenizer(string, "|");
                                        while(st1.hasMoreTokens())
								        {
								            System.out.println(st1.nextToken());
								        }*/
								/*if(data.length>=3)
								tvName.setText(data[2]+" | "+data[3]+" | "+data[1]);*/
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                } finally
                {
                    getVehicleTypeList();
                }
            }
        }).execute();
    }

    private void updateImageId(String response)
    {
        if (response != null)
        {
            String AddImageToVehicleResponse = ParserManager.parseAddImageToVehicleResponse(response);
            String result[] = new String[2];
            result = AddImageToVehicleResponse.toString().split(":");
            if (result[0].contains("OK"))
            {
                //((BaseImage)imageDragableGridView.getAdapter().getItem(imageUploadIndex)).setLocal(false);
                ((BaseImage) imageDragableGridView.getAdapter().getItem(imageUploadIndex)).setId(Integer.parseInt(result[1]));
                if (updatedImages != null)
                    updatedImages.get(imageUploadIndex).setId(Integer.parseInt(result[1]));
            }
        }
    }

    /**
     * This Method is used to send selected images to server.
     *
     * @param isSaveDataBase     = if true (Save to local database) if false (send directly to server on any network)
     * @param usedVehicleStockID = vehicleId
     * @param flag               = if true Close the screen and move back
     */
    private void sendImagesToServerOrDataBase(final boolean isSaveDataBase, final int usedVehicleStockID, final boolean flag)
    {
        if (updatedImages.get(imageUploadIndex).getId() == 0)
        {
            String base64String = Helper.convertBitmapToBase64(updatedImages.get(imageUploadIndex).getPath());
            if (TextUtils.isEmpty(base64String))
            {
                imageUploadIndex++;
                if (imageUploadIndex == totalImageUploadSize)
                    checkImageUploadResponse(flag);
                else
                    sendImagesToServerOrDataBase(isSaveDataBase, usedVehicleStockID, flag);
            }

            final StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<AddImageToVehicleBase64 xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");

            if (isSaveDataBase)
                soapMessage.append("<userHash>" + Constants.CHANGE_THIS_USER_HASH + "</userHash>");
            else
                soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");

            soapMessage.append("<usedVehicleStockID>" + usedVehicleStockID + "</usedVehicleStockID>");
            soapMessage.append("<imageBase64>" + base64String + "</imageBase64>");
            soapMessage.append("<imageName>" + Helper.getNameWithExtenstion(updatedImages.get(imageUploadIndex).getPath()) + "</imageName>");
            soapMessage.append("<imageTitle>" + Helper.getName(updatedImages.get(imageUploadIndex).getPath()) + "</imageTitle>");
            soapMessage.append("<imageSource>phone app</imageSource>");
            soapMessage.append("<imagePriority>" + (imageUploadIndex + 1) + "</imagePriority>");
            soapMessage.append("<imageIsEtched>0</imageIsEtched>");
            soapMessage.append("<imageIsBranded>0</imageIsBranded>");
            soapMessage.append("<imageAngle></imageAngle>");
            soapMessage.append("</AddImageToVehicleBase64></Body></Envelope>");

            Helper.Log("soapMessage " + imageUploadIndex, "" + soapMessage);

            // Check do we need to save in local database or send to server.
            if (isSaveDataBase)
            {
                // Save in local Database
                SMDatabase myDatabase = new SMDatabase(getContext());
                myDatabase.insertRecords(soapMessage.toString(), Constants.SavePhotosAndExtrasImage);

                imageUploadIndex++;
                if (imageUploadIndex == totalImageUploadSize)
                    checkImageUploadResponse(flag);
                else
                    sendImagesToServerOrDataBase(isSaveDataBase, usedVehicleStockID, flag);

            } else
            {
                VollyResponseListener listener = new VollyResponseListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        imageUploadIndex++;
                        VolleyLog.e("Error: ", "" + error);
                        if (imageUploadIndex == totalImageUploadSize)
                            checkImageUploadResponse(flag);
                        else
                            sendImagesToServerOrDataBase(isSaveDataBase, usedVehicleStockID, flag);
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        VolleyLog.d("onResponse: ", "" + response);

                        updateImageId(response);
                        imageUploadIndex++;
                        if (imageUploadIndex == totalImageUploadSize)
                            checkImageUploadResponse(flag);
                        else
                            sendImagesToServerOrDataBase(isSaveDataBase, usedVehicleStockID, flag);
                    }
                };

                VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IStockService/AddImageToVehicleBase64", listener);
                try
                {
                    request.init("addImageToVehicleBase64" + imageUploadIndex);
                } catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        } else
        {
            // no need to update image
            imageUploadIndex++;
            if (imageUploadIndex == totalImageUploadSize)
                checkImageUploadResponse(flag);
            else
                sendImagesToServerOrDataBase(isSaveDataBase, usedVehicleStockID, flag);
        }

    }

    public void addImageToVehicleBase64(final int usedVehicleStockID, final boolean flag)
    {
        showVideoAleart = 3;

        // Check is on wifi or mobile network
        if (NetworkUtil.getConnectivityStatusString(getActivity()) == ConnectivityManager.TYPE_WIFI)
        {
            sendImagesToServerOrDataBase(false, usedVehicleStockID, flag);
        } else
        {
            boolean showImageAleart = false;

            if (imageDragableGridView.getLocalImageListWithoutPlus().size() > 0)
            {
                showImageAleart = true;
            }

           /* // Check is local video available .
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
                                sendImagesToServerOrDataBase(false, usedVehicleStockID, flag);
                                break;
                            // Upload With WIFI
                            case Dialog.BUTTON_NEGATIVE:
                                showVideoAleart = 2;
                                sendImagesToServerOrDataBase(true, usedVehicleStockID, flag);
                                break;
                        }
                    }
                });
            } else
            {
                sendImagesToServerOrDataBase(false, usedVehicleStockID, flag);
            }

        }
    }

    public void changeImagePriorityForVehicle(final int usedVehicleStockID, final boolean flag)
    {
        final StringBuilder soapMessage = new StringBuilder();
        soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapMessage.append("<Body>");
        soapMessage.append("<ChangeImagePriorityForVehicle xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
        soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
        soapMessage.append("<usedVehicleStockID>" + usedVehicleStockID + "</usedVehicleStockID>");
        soapMessage.append("<imageID>" + ((BaseImage) updatedImages.get(imageProrityIndex)).getId() + "</imageID>");
        soapMessage.append("<newPriorityID>" + (imageProrityIndex + 1) + "</newPriorityID>");
        soapMessage.append("</ChangeImagePriorityForVehicle></Body></Envelope>");

        Helper.Log("soapMessage " + imageProrityIndex, "" + soapMessage);


        VollyResponseListener listener = new VollyResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Helper.Log("Error: ", "" + error);
                imageProrityIndex++;
                if (imageProrityIndex == totalImagePriorityChangedSize)
                    checkImagePriorityResponse(flag);
                else
                    changeImagePriorityForVehicle(usedVehicleStockID, flag);
            }

            @Override
            public void onResponse(String response)
            {
                Helper.Log("onResponse: ", "" + response);
                imageProrityIndex++;
                if (imageProrityIndex == totalImagePriorityChangedSize)
                    checkImagePriorityResponse(flag);
                else
                    changeImagePriorityForVehicle(usedVehicleStockID, flag);
            }
        };


        VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IStockService/ChangeImagePriorityForVehicle", listener);

        try
        {
            request.init("ChangeImagePriorityForVehicle" + imageProrityIndex);
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    public void removeImageFromVehicle(final int usedVehicleStockID, final boolean flag)
    {
        final StringBuilder soapMessage = new StringBuilder();
        soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapMessage.append("<Body>");
        soapMessage.append("<RemoveImageFromVehicle xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
        soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
        soapMessage.append("<usedVehicleStockID>" + usedVehicleStockID + "</usedVehicleStockID>");
        soapMessage.append("<imageID>" + ((BaseImage) imageDragableGridView.getDeletedImages().get(imageDeletedIndex)).getId() + "</imageID>");
        soapMessage.append("</RemoveImageFromVehicle></Body></Envelope>");
        Helper.Log("soapMessage " + imageDeletedIndex, "" + soapMessage);

        VollyResponseListener listener = new VollyResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Helper.Log("Error: ", error.toString());
                imageDeletedIndex++;
                if (imageDeletedIndex == totalImageDeletedSize)
                    checkImageDeletedResponse(flag);
                else
                    removeImageFromVehicle(usedVehicleStockID, flag);

            }

            @Override
            public void onResponse(String response)
            {
                Helper.Log("onResponse: ", "" + response);
                imageDeletedIndex++;
                if (imageDeletedIndex == totalImageDeletedSize)
                    checkImageDeletedResponse(flag);
                else
                    removeImageFromVehicle(usedVehicleStockID, flag);

            }
        };

        VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IStockService/RemoveImageFromVehicle", listener);

        try
        {
            request.init("RemoveImageFromVehicle" + imageDeletedIndex);
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }

    }

    private void checkImageUploadResponse(final boolean flag)
    {
        if (imageDragableGridView.isPriorityChanged())
        {
            // image priority changed
            totalImagePriorityChangedSize = updatedImages.size();
            imageProrityIndex = 0;
            changeImagePriorityForVehicle(usedVehicleStockID, flag);
        } else
        {
            // check any image deleted
            if (imageDragableGridView.isImageDeleted())
            {
                totalImageDeletedSize = imageDragableGridView.getDeletedImages().size();
                imageDeletedIndex = 0;
                removeImageFromVehicle(usedVehicleStockID, flag);
            } else
            {
                if (isVideoSelected)
                {
                    uploadWarning(flag);

                } else
                {


                    showSuccessDialog(flag);
                }
            }
        }
    }

    private void checkImagePriorityResponse(final boolean flag)
    {
        // check any image deleted
        if (imageDragableGridView.isImageDeleted())
        {
            totalImageDeletedSize = imageDragableGridView.getDeletedImages().size();
            imageDeletedIndex = 0;
            removeImageFromVehicle(usedVehicleStockID, flag);
        } else
        {
            if (isVideoSelected)
            {
                uploadWarning(flag);
            } else
            {
                showSuccessDialog(flag);
            }
        }
    }

    private void checkImageDeletedResponse(final boolean flag)
    {
        flagForClose = flag;
        if (deletedVideos.size() != 0)
        {
            RemoveVideoLinkFromVehicle(deletedVideos);
        }
        if (isVideoSelected)
        {
            uploadWarning(flag);
        } else
        {
            showSuccessDialog(flag);
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
                                showSuccessDialog(flagForClose);
                            }
                        } else
                        {
                            showSuccessDialog(flagForClose);
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

	/*private void displayShowCaseView(){

		if(!ShowcaseSessions.isSessionAvailable(getActivity(), VariantDetails.class.getSimpleName())){
			ArrayList<TargetView> viewList= new ArrayList<TargetView>();
			viewList.add(new TargetView(svVINDetails,ShowCaseType.SwipeUpDown, getString(R.string.submit_vehicle_information_by_scrolling_up_and_down)));
			 ShowcaseLayout showcaseView= new ShowcaseLayout(getActivity());
			showcaseView.setShowcaseView(viewList);

			((ViewGroup)getActivity().getWindow().getDecorView()).addView(showcaseView);
			ShowcaseSessions.saveSession(getActivity(), VariantDetails.class.getSimpleName());
		}
	}*/

    protected void showSuccessDialog(final boolean flag)
    {

        if (deletedVideos.size() != 0)
        {
            RemoveVideoLinkFromVehicle(deletedVideos);
        } else
        {
            hideProgressDialog();
            CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
            {
                @Override
                public void onButtonClicked(int type)
                {
                    if (flag)
                    {
                        if (getArguments().containsKey("fromLoadVehicle"))
                        {
                            //go back to load vehicle screen
                            getFragmentManager().popBackStack();
                        } else
                        {
                            getFragmentManager().popBackStack();
                            getFragmentManager().popBackStack();
                            getFragmentManager().popBackStack();
                        }
                    }

                /*if (flag)
                    getFragmentManager().popBackStack();*/
                }
            });
        }

    }

    @SuppressWarnings("unchecked")
    private void getVehicleDetailsSoap()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("usedVehicleStockID", scanVIN.getStock().getId(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadVehicleDetailsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadVehicleDetailsXML");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {
                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    if (result == null)
                    {
                        return;
                    }
                    try
                    {
                        Helper.Log("response", result.toString());
                        Vector<SoapObject> vectorResult = (Vector<SoapObject>) result;
                        SoapObject outer = vectorResult.get(0);
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Details");
                        department = inner.getPropertySafelyAsString("department", "");
                        scanVIN.setAge((int) Float.parseFloat(inner.getPropertySafelyAsString("age", "0")));
                        scanVIN.setPrice(inner.getPropertySafelyAsString("price", ""));
                        scanVIN.setTradePrice(inner.getPropertySafelyAsString("tradeprice", ""));
                        scanVIN.setColour(inner.getPropertySafelyAsString("colour", ""));
                        scanVIN.setMilage(inner.getPropertySafelyAsString("mileage", ""));
                        scanVIN.setComments(inner.getPropertySafelyAsString("comments", ""));
                        scanVIN.setLocation(inner.getPropertySafelyAsString("location", ""));
                        scanVIN.setExtras(inner.getPropertySafelyAsString("extras", ""));
                        scanVIN.setTrim(inner.getPropertySafelyAsString("trim", ""));
                        scanVIN.setCondition(inner.getPropertySafelyAsString("condition", ""));
                        scanVIN.setOem(inner.getPropertySafelyAsString("oem", ""));
                        scanVIN.setCost(inner.getPropertySafelyAsString("cost", ""));
                        scanVIN.setStandIn(inner.getPropertySafelyAsString("standin", ""));
                        scanVIN.setCpError(Boolean.parseBoolean(inner.getPropertySafelyAsString("cpaerror", "false")));
                        scanVIN.setInternalNote(inner.getPropertySafelyAsString("internalnote", ""));
                        scanVIN.setProgramname(inner.getPropertySafelyAsString("programname", ""));
                        scanVIN.setTender(Boolean.parseBoolean(inner.getPropertySafelyAsString("istender", "false")));
                        scanVIN.setTrade(Boolean.parseBoolean(inner.getPropertySafelyAsString("istrade", "false")));
                        scanVIN.setRetail(Boolean.parseBoolean(inner.getPropertySafelyAsString("isretail", "false")));
                        scanVIN.setProgram(Boolean.parseBoolean(inner.getPropertySafelyAsString("isprogram", "false")));
                        scanVIN.setExcluded(Boolean.parseBoolean(inner.getPropertySafelyAsString("isexcluded", "false")));
                        scanVIN.setInvalid(Boolean.parseBoolean(inner.getPropertySafelyAsString("isinvalid", "false")));
                        scanVIN.setOverride(Boolean.parseBoolean(inner.getPropertySafelyAsString("override", "false")));
                        scanVIN.setIgnorimport(Boolean.parseBoolean(inner.getPropertySafelyAsString("ignoreonimport", "false")));
                        scanVIN.setEditable(Boolean.parseBoolean(inner.getPropertySafelyAsString("editable", "false")));

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
                                if (!youTubeVideos.contains(tubeVideo.getVideo_title()))
                                {
                                    Helper.Log("No duplicate", "-----------0");
                                }
                                youTubeVideos.add(tubeVideo);
                            }
                            DataManager.getInstance().getYouTubeVideos().addAll(youTubeVideos);
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
                                image.setImagedpi((int) Float.parseFloat(imgObj.getPropertySafelyAsString("imageDPI", "0")));
                                imageList.add(image);
                            }
                            scanVIN.setImageList(imageList);
                            Helper.Log("ImagesCount", imageList.size() + "");
                            if(imageList.size() > 0)
                            {
                                flImages.setVisibility(View.VISIBLE);
                                ivVehicleImage.setVisibility(View.VISIBLE);
                            }else
                            {
                                flImages.setVisibility(View.GONE);
                                ivVehicleImage.setVisibility(View.GONE);
                            }
                            ivVehicleImage.setImageUrl(imageList.get(0).getLink(), imageLoader);
                        }
                        videoloading(youTubeVideos);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        setData();
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    private void uploadWarning(final boolean flag)
    {
        long total_video_size = 0;
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mWifi.isConnected())
        {
            new videoUpload(flag, false).execute();
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

            if(isVideoSelected)
            {
                if(showVideoAleart == 3)
                    showVideoAleart = 1;
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
                            new videoUpload(flag, false).execute();
                        } else
                        {
                            new videoUpload(flag, true).execute();

                           /* hideProgressDialog();
                            CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    if (flag)
                                    {
                                        if (getArguments().containsKey("fromLoadVehicle"))
                                        {
                                            //go back to load vehicle screen
                                            getFragmentManager().popBackStack();
                                        } else
                                        {
                                            getFragmentManager().popBackStack();
                                            getFragmentManager().popBackStack();
                                            getFragmentManager().popBackStack();
                                        }
                                    }
                                }
                            });*/
                        }
                    }
                });
            } else
            {
                new videoUpload(flag, false).execute();
            }


        }

    }

    private class videoUpload extends AsyncTask<Void, Void, String>
    {
        String VideoName;
        boolean saveInDataBase;
        SMDatabase myDatabase = new SMDatabase(getContext());

        public videoUpload(boolean flag, boolean saveInDataBase)
        {
            super();
            flagForClose = flag;
            this.saveInDataBase = saveInDataBase;
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
            for (int i = uploadVideoCount; i < DataManager.getInstance().getYouTubeVideos().size(); i++)
            {
                if (DataManager.getInstance().getYouTubeVideos().get(i).isLocal())
                {
                    if (saveInDataBase)
                    {
                        // Save in database
                        myDatabase.insertVideoUploadingRecords(DataManager.getInstance().getYouTubeVideos().get(i).getVideoFullPath(),
                                "" + usedVehicleStockID,
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_title(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Description(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Tags(),
                                "" + DataManager.getInstance().getYouTubeVideos().get(i).isSearchable());
                        VideoName = "Success";
                    } else
                    {
                        VideoName = HelperHttp.uploadVideoFile(new File(DataManager.getInstance().getYouTubeVideos().get(i).getVideoFullPath()),
                                usedVehicleStockID,
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

                    ArrayList<YouTubeVideo> youTubeVideosTemp = new ArrayList<>();
                    for (int i = 0; i < DataManager.getInstance().getYouTubeVideos().size(); i++)
                    {
                        YouTubeVideo youTubeVideo = DataManager.getInstance().getYouTubeVideos().get(i);
                        youTubeVideo.setLocal(false);
                        youTubeVideosTemp.add(youTubeVideo);
                    }
                    DataManager.getInstance().getYouTubeVideos().clear();
                    DataManager.getInstance().getYouTubeVideos().addAll(youTubeVideosTemp);

                    uploaded_videos.clear();
                    showSuccessDialog(flagForClose);

                } else
                {
                    uploadVideoCount++;
                    DataManager.getInstance().getYouTubeVideos().remove(uploadVideoCount);
                    new videoUpload(flagForClose, saveInDataBase).execute();
                }
            } else
            {
                if (isVideoSelected)
                {
                    CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.stock_updated_successfully_video_failed), new DialogListener()
                    {
                        @Override
                        public void onButtonClicked(int type)
                        {
                            hideKeyboard();
                            hideProgressDialog();
                            getActivity().getFragmentManager().popBackStack();
                        }
                    });
                } else
                {
                    showSuccessDialog(flagForClose);
                }
            }
        }

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

    public void onBackPressed()
    {
        if (isVideoSelected)
        {
            CustomDialogManager.showOkCancelDialog(getActivity(),
                    "You have not uploaded selected video  do you want to continue?", new DialogListener()
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

    @SuppressWarnings("unchecked")
    private void setData()
    {
        edtPriceRetail.setText(Helper.doubleToInt(scanVIN.getPrice()));
        edtPriceTrade.setText(Helper.doubleToInt(scanVIN.getTradePrice()));
        edtColor.setText(Helper.checkEmpty(scanVIN.getColour()));
        edtMilage.setText(Helper.checkEmpty(scanVIN.getMilage()));

        edtComments.setText(Helper.checkEmpty(scanVIN.getComments()));
        edtLocation.setText(Helper.checkEmpty(scanVIN.getLocation()));
        edtExtras.setText(Helper.checkEmpty(scanVIN.getExtras()));
        edtTrim.setText(Helper.checkEmpty(scanVIN.getTrim()));

        edtCondition.setText(Helper.checkEmpty(scanVIN.getCondition()));
        edtOEM.setText(Helper.checkEmpty(scanVIN.getOem()));
        edtCost.setText(Helper.doubleToInt(scanVIN.getCost()));
        edtStand.setText(Helper.checkEmpty(scanVIN.getStandIn()));

        edtInternalNote.setText(Helper.checkEmpty(scanVIN.getInternalNote()));
        edtProgramName.setText(Helper.checkEmpty(scanVIN.getProgramname()));

        cbProgramVehicle.setChecked(scanVIN.isProgram());
        cbRetail.setChecked(scanVIN.isRetail());
        cbTrade.setChecked(scanVIN.isTrade());

        cbDontImport.setChecked(scanVIN.isIgnorimport());
        cbIgnoreSetting.setChecked(scanVIN.isOverride());
        cbErrorInfo.setChecked(scanVIN.isCpError());

        ArrayList<? extends BaseImage> imageList = scanVIN.getImageList();
        imageDragableGridView.setImageList((ArrayList<BaseImage>) imageList);
    }
}
