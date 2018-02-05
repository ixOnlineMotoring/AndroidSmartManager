package com.nw.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.greysonparrelli.permiso.Permiso;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.nw.adapters.HorizontalListVideoAdapter;
import com.nw.broadcast.NetworkUtil;
import com.nw.database.SMDatabase;
import com.nw.fragments.MakeModelFragment.VariatEditListener;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.model.BaseImage;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
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
import com.nw.widget.CustomDialogManager;
import com.nw.widget.DragableGridView;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.activity.LoginActivity;
import com.smartmanager.activity.VehicleActivity;
import com.smartmanager.android.R;
import com.utils.AppSession;
import com.utils.Connectivity;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;
import com.utils.ImageHelper;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class ListDetailsFragment extends BaseFragement implements OnClickListener
{
    EditText edType, edtColor, edtMilage, edtStock, edtPriceRetail,
            edtPriceTrade, edtProgramName, edtExtras, edtVIN, edtEngineNo,
            edtRegNo, edtOEM, edtLocation, edtTrim, edtCost, edtStand,
            edtInternalNote, edtComments, edYear, edtCondition, edAddTender;
    CheckBox cbProgramVehicle, cbDontImport, cbIgnoreSetting, cbErrorInfo, cbRemoveVehicle, cbRetail, cbTrade;
    LinearLayout botttomLayout;
    TextView tvtVariant, tvtMAndM, tvName, tvAdditionalInfo;
    Button btnSave, btnSaveClose;
    DragableGridView imageDragableGridView;
    String department = "Used";
    ScrollView scView;
    ArrayList<SmartObject> tenderList, typeList;
    ListPopupWindow window;
    VehicleDetails details;
    ImageView ivRightArrow;
    NetworkImageView ivVehicleImage;
    int imageUploadIndex = 0;
    int deleteVideoCount = 0;
    int noOfImagesInGrid = 0;
    int uploadImageCount = 0;
    int deleteImageCount = 0;
    int uploadVideoCount = 0;
    int priorityImageCount = 0;
    Button btnEdit;
    String message = "", errorMessage = "";
    ArrayList<BaseImage> updatedImages;
    ImageLoader imageLoader;
    ImageView ivAddVideos;
    boolean isVideoSelected = false;
    String videoPath = null, result;
    VideoInfoFragment infoFragment;
    VideoPreviewFragment previewFragment;
    HorizontalListView hlvphotosExtraCarVideos;
    ArrayList<String> videoCodes = new ArrayList<>();
    ArrayList<YouTubeVideo> youTubeVideos, deletedVideos, uploaded_videos;
    HorizontalListVideoAdapter horizontalListVideoAdapter;
    ProgressBar progressBarDetails;

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
        View view = inflater.inflate(R.layout.fragment_variant_details_new, container, false);
        setHasOptionsMenu(true);
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CanUploadVideo();
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
                DataManager.getInstance().getimageArray().clear();
            }
        }
        if (deletedVideos == null)
        {
            deletedVideos = new ArrayList<>();
        }
        if (uploaded_videos == null)
        {
            uploaded_videos = new ArrayList<>();
        }
        if (youTubeVideos == null)
        {
            youTubeVideos = new ArrayList<>();
        }
        hideKeyboard(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getVehicleDetailsSoap();
        getTypeList();
    }

    private void initialise(View view)
    {
        progressBarDetails = (ProgressBar) view.findViewById(R.id.progressBardetails);
        scView = (ScrollView) view.findViewById(R.id.svVINDetails);
        edtMilage = (EditText) view.findViewById(R.id.edtMilage);
        edtStock = (EditText) view.findViewById(R.id.edtStock);
        edtPriceRetail = (EditText) view.findViewById(R.id.edtPriceRetail);
        edtPriceTrade = (EditText) view.findViewById(R.id.edtPriceTrade);
        edtProgramName = (EditText) view.findViewById(R.id.edtProgramName);
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
        edtCondition = (EditText) view.findViewById(R.id.edtCondition);
        ivRightArrow = (ImageView) view.findViewById(R.id.ivRightArrow);
        ivRightArrow.setRotation(-90);
        cbProgramVehicle = (CheckBox) view.findViewById(R.id.cbProgramVehicle);
        cbRetail = (CheckBox) view.findViewById(R.id.cbRetail);
        cbTrade = (CheckBox) view.findViewById(R.id.cbTrade);
        ivVehicleImage = (NetworkImageView) view.findViewById(R.id.ivVehicleImage);
        ivVehicleImage.setOnClickListener(this);
        cbDontImport = (CheckBox) view.findViewById(R.id.cbDontImport);
        cbIgnoreSetting = (CheckBox) view.findViewById(R.id.cbIgnoreSetting);
        cbErrorInfo = (CheckBox) view.findViewById(R.id.cbErrorInfo);
        cbRemoveVehicle = (CheckBox) view.findViewById(R.id.cbRemoveVehicle);
        ivAddVideos = (ImageView) view.findViewById(R.id.ivAddVideos);
        ivAddVideos.setOnClickListener(this);
        edType = (EditText) view.findViewById(R.id.edType);
        edType.setOnClickListener(this);
        edYear = (EditText) view.findViewById(R.id.edYear);
        edYear.setOnClickListener(this);
        edtColor = (EditText) view.findViewById(R.id.edtColor);
        tvtVariant = (TextView) view.findViewById(R.id.tvtVariant);
        tvtMAndM = (TextView) view.findViewById(R.id.tvtMAndM);
        tvName = (TextView) view.findViewById(R.id.tvName);
        view.findViewById(R.id.btnSave).setOnClickListener(this);
        botttomLayout = (LinearLayout) view.findViewById(R.id.botttomLayout);
        tvAdditionalInfo = (TextView) view.findViewById(R.id.tvAdditionalInfo);
        tvAdditionalInfo.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (botttomLayout.getVisibility() == View.GONE)
                {
                    botttomLayout.setVisibility(View.VISIBLE);
                    ivRightArrow.setRotation(0);
                    scView.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            scView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                } else
                {
                    ivRightArrow.setRotation(-90);
                    botttomLayout.setVisibility(View.GONE);
                }
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

        edAddTender = (EditText) view.findViewById(R.id.tvAddTender);
        edAddTender.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //this code will be executed on devices running Marshmellow or later
            imageDragableGridView = new DragableGridView();
        } else
        {
            imageDragableGridView = new DragableGridView(getActivity());
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
        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.setEnabled(false);
                MakeModelFragment fragment = new MakeModelFragment();
                fragment.setVariatEditListener(new VariatEditListener()
                {
                    @Override
                    public void onVariantEdited(String year, String makeID,
                                                String make, String modelId, String model,
                                                String variantId, String variant, String detail,
                                                String meadcode)
                    {
                        //	tvName.setText(detail != null ? detail: getString(R.string.no_information));
                        details.setMmcode(meadcode);

                        tvtVariant.setText(year + " " + variant);
                        details.setYear(Integer.parseInt(year));
                        if (TextUtils.isEmpty("" + details.getYear()))
                        {
                            edYear.setText("Year?");
                        } else
                        {
                            edYear.setText("" + details.getYear());
                        }
                        if (!TextUtils.isEmpty(details.getMmcode()))
                            tvtMAndM.setText("M&M : " + Helper.checkEmpty(details.getMmcode()));
                        else
                            tvtMAndM.setText("M&M?");
                    }
                });
                fragment.show(getActivity().getFragmentManager(), "");
                v.setEnabled(true);
            }
        });
        btnEdit.setVisibility(View.GONE);
        btnSaveClose = (Button) view.findViewById(R.id.btnSaveClose);
        btnSaveClose.setOnClickListener(this);
        cbTrade.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                if (arg1)
                {
                    edAddTender.setText("");
                    edAddTender.setTag("");
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
                    edAddTender.setText("");
                    edAddTender.setTag("");
                }
            }
        });
        errorMessage = getString(R.string.stock_details_not_saved);
        message = getString(R.string.stock_saved_successfully);
    }

    @SuppressWarnings("unchecked")
    private void putValues()
    {
        edtColor.setText(Helper.checkEmpty(details.getColour()));
        edtMilage.setText(Helper.checkEmpty("" + details.getMileage()));
        edtStock.setText(Helper.checkEmpty(details.getStockCode()));
        edtPriceRetail.setText(Helper.doubleToInt(details.getPrice() + ""));
        edtPriceTrade.setText(Helper.doubleToInt(details.getTradeprice() + ""));
        edtProgramName.setText(Helper.checkEmpty(details.getProgramname()));
        edtComments.setText(Helper.checkEmpty(details.getComments()));
        edtExtras.setText(Helper.checkEmpty(details.getExtras()));
        edYear.setText(Helper.checkEmpty("" + details.getYear()));
        edtVIN.setText(Helper.checkEmpty("" + details.getVin()));
        edtEngineNo.setText(Helper.checkEmpty("" + details.getEngine()));
        edtRegNo.setText(Helper.checkEmpty(details.getRegistration()));
        edtOEM.setText(Helper.checkEmpty(details.getOem()));
        edtLocation.setText(Helper.checkEmpty("" + details.getLocation()));
        edtTrim.setText(Helper.checkEmpty(details.getTrim()));
        edtCost.setText(Helper.doubleToInt(details.getCost() + ""));
        edtStand.setText(Helper.checkEmpty(details.getStandin() + ""));
        edtInternalNote.setText(Helper.checkEmpty(details.getInternalnote()));
        edtComments.setText(Helper.checkEmpty(details.getComments()));
        edYear.setText(Helper.checkEmpty(details.getYear() + ""));
        edtCondition.setText(Helper.checkEmpty(details.getCondition()));
        cbProgramVehicle.setChecked(details.isIsprogram());
        cbDontImport.setChecked(details.isIgnoreonimport());
        cbIgnoreSetting.setChecked(details.isOverride());
        cbErrorInfo.setChecked(details.isCpaerror());
        cbRetail.setChecked(details.isIsretail());
        cbTrade.setChecked(details.isIstrade());
        if (getArguments().getString("vehicleName") != null)
            tvtVariant.setText(Html.fromHtml("<font color=#ffffff>"
                    + details.getYear() + " " + "</font>"
                    + getArguments().getString("vehicleName")));
        else
            tvtVariant.setText(Html.fromHtml("<font color=#ffffff>"
                    + details.getYear() + " " + "</font>"
                    + details.getFriendlyName()));
        if (!TextUtils.isEmpty(details.getMmcode()))
            tvtMAndM.setText("M&M : " + Helper.checkEmpty(details.getMmcode()));
        else
            tvtMAndM.setText("");

        ArrayList<? extends BaseImage> imageList = details.getImageList();
        if (isVideoSelected)
        {
            imageDragableGridView.setImageList(DataManager.getInstance().getimageArray());
        } else
        {
            imageDragableGridView.setImageList((ArrayList<BaseImage>) imageList);
        }

        if (details.getVariantID() > 0)
            getVatientDetails(details.getVariantID());
        else
        {
            //	tvName.setText(getString(R.string.no_information));
            hideProgressDialog();
        }

        if (details.isIstender())
        {
            cbTrade.setChecked(false);
            cbRetail.setChecked(false);
        }

        if (details.getVariantID() > 0)
            btnEdit.setVisibility(View.GONE);
        else
            btnEdit.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(Helper.checkEmpty(department)))
        {
            edType.setText(Helper.checkEmpty(department));
            edType.setTag("" + getTypeIdByTypeName(department));
        }

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
                CustomDialogManager.showOkCancelDialog(getActivity(),
                        getActivity().getString(R.string.are_you_sure_you_want_to_delete),
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
     * Navigate to video info screen fragment As Fragment to called belongs to
	 * other activity we call activity and pass parameters through intent
	 * Parameter- position of image clicked
	 */

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

    private void navigateToLargeImage(int position)
    {
        try
        {
            Intent iToBuyActivity = new Intent(getActivity(), BuyActivity.class);
            iToBuyActivity.putParcelableArrayListExtra("imagelist",
                    imageDragableGridView.getUpdatedImageListWithoutPlus());
            iToBuyActivity.putExtra("index", position);
            iToBuyActivity.putExtra("vehicleName", "Smart Manager");
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
            case R.id.tvAddTender:
                if (tenderList == null || tenderList.isEmpty())
                    getTenderList();
                else
                    showTenderPopup(v);
                break;

            case R.id.edType:
                if (typeList == null || typeList.isEmpty())
                {
                    getTypeList();
                } else
                {
                    showTypePopup(v);
                }
                break;

            case R.id.edYear:
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
                Helper.showDropDown(v, new ArrayAdapter(getActivity(),
                                R.layout.list_item_text2, R.id.tvText, years),
                        new OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id)
                            {
                                edYear.setText(years.get(position) + "");
                                details.setYear(Integer.parseInt(years
                                        .get(position)));
                            }
                        });
                break;

            case R.id.btnSave:
                saveVehicle(false);
                break;

            case R.id.btnSaveClose:
                saveVehicle(true);
                break;

            case R.id.ivVehicleImage:
                if (details.getImageList() != null)
                {
                    navigateToLargeImage(0);
                }

                break;

            case R.id.ivAddVideos:
                int total_local_video = 0;
                for (int j = 0; j < DataManager.getInstance().getYouTubeVideos().size(); j++)
                {
                    if (DataManager.getInstance().getYouTubeVideos().get(j).isLocal())
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

    private void saveVehicle(final boolean close)
    {
        if (TextUtils.isEmpty(edType.getText().toString().trim()))
        {
            scView.scrollTo(0, edType.getTop() + 20);
            Helper.showToast(getString(R.string.select_type1), getActivity());
            edType.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edYear.getText().toString().trim()))
        {
            scView.scrollTo(0, edYear.getTop() + 20);
            Helper.showToast(getString(R.string.select_year1), getActivity());
            edYear.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtColor.getText().toString().trim()))
        {
            scView.scrollTo(0, edtColor.getTop() + 20);
            Helper.showToast(getString(R.string.no_color), getActivity());
            edtColor.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edtMilage.getText().toString().trim()))
        {
            scView.scrollTo(0, edtMilage.getTop() + 20);
            Helper.showToast(getString(R.string.no_milage), getActivity());
            edtMilage.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtStock.getText().toString().trim()))
        {
            scView.scrollTo(0, edtStock.getTop() + 20);
            Helper.showToast(getString(R.string.no_stock_number), getActivity());
            edtStock.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edtPriceRetail.getText().toString().trim()))
        {
            scView.scrollTo(0, edtPriceRetail.getTop() + 20);
            Helper.showToast(getString(R.string.no_retail_price), getActivity());
            edtPriceRetail.requestFocus();
            return;
        }

        noOfImagesInGrid = 0;
        uploadImageCount = 0;
        deleteImageCount = 0;
        priorityImageCount = 0;

        if (HelperHttp.isNetworkAvailable(getActivity()))
        {

            if (imageDragableGridView.getLocalImageListWithoutPlus().size() > 0)
            {
                // check for slower connection
                if (!Connectivity.isConnectedFast(getActivity()))
                {
                    CustomDialogManager
                            .showOkCancelDialog(
                                    getActivity(),
                                    "Please be advised you are currently on a slow data network do you wish to proceed?",
                                    "Yes", "No", new DialogListener()
                                    {
                                        @Override
                                        public void onButtonClicked(int type)
                                        {
                                            if (type == Dialog.BUTTON_NEGATIVE)
                                                return;
                                            else
                                                updateVehicleXML(close);
                                        }
                                    });
                } else
                    updateVehicleXML(close);
            } else
                updateVehicleXML(close);

        } else
            HelperHttp.showNoInternetDialog(getActivity());
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
                    Helper.showToast(getString(R.string.error_getting_data),
                            getActivity());
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

            VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IStockService/CanUploadVideo", listener);
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
            CustomDialogManager.showOkCancelDialog(getActivity(), "You have not uploaded selected video  do you want to continue?", new DialogListener()
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
            if (getArguments().containsKey("fromActiveBidsFragment"))
                getActivity().finish();
            else
                getActivity().getFragmentManager().popBackStack();
        }
    }

    private void uploadWarning(final boolean close)
    {
        long total_video_size = 0;
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mWifi.isConnected())
        {
            new videoUpload(close, false).execute();
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
                CustomDialogManager.showOkCancelDialog(
                        getActivity(), "It is recommended that you connect to "
                                + "a WiFi network to upload video files of size "
                                + (total_video_size / 1024)
                                + "MB, to avoid excessive data use."
                                + " Do you want to:", getString(R.string.Upload_Now), getString(R.string.upload_with_wifi), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                if (type == Dialog.BUTTON_POSITIVE)
                                {
                                    new videoUpload(close, false).execute();
                                } else
                                {
                                    new videoUpload(close, true).execute();
                                }
                            }
                        });
            } else
            {
                new videoUpload(close, true).execute();
            }

        }
    }

    private class videoUpload extends AsyncTask<Void, Void, String>
    {
        String VideoName;
        boolean close = false;
        SMDatabase myDatabase = new SMDatabase(getContext());
        boolean saveInDataBase = false;

        public videoUpload(boolean close, boolean saveInDataBase)
        {
            this.close = close;
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
                                "" + details.getUsedVehicleStockID(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_title(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Description(),
                                DataManager.getInstance().getYouTubeVideos().get(i).getVideo_Tags(),
                                "" + DataManager.getInstance().getYouTubeVideos().get(i).isSearchable());
                        VideoName = "Success";
                    } else
                    {
                        VideoName = HelperHttp.uploadVideoFile(new File(DataManager.getInstance().getYouTubeVideos().get(i).getVideoFullPath()),
                                details.getUsedVehicleStockID(),
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
                    hideProgressDialog();
                    CustomDialogManager.showOkDialog(getActivity(),
                            getString(R.string.vehicles_updated_successfully), new DialogListener()
                    {
                        @Override
                        public void onButtonClicked(int type)
                        {
                            if (type == Dialog.BUTTON_POSITIVE)
                            {
                                hideKeyboard();
                                if (close)
                                {
                                    if (getArguments().containsKey("fromActiveBidsFragment"))
                                        getActivity().finish();
                                    else
                                        getActivity().getFragmentManager().popBackStack();
                                } else
                                {
                                    reLoadAllData();
                                }
                            }
                        }
                    });
                } else
                {
                    uploadVideoCount++;
                    //  DataManager.getInstance().getYouTubeVideos().remove(uploadImageCount);
                    new videoUpload(close, saveInDataBase).execute();
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
                                    hideKeyboard();
                                    hideProgressDialog();
                                    getActivity().getFragmentManager().popBackStack();
                                }
                            });
                }
            }
        }
    }

    public SoapObject createSoapObjectFromSoapObjectString(String soapObjectString)
    {
        // Get response
        SoapObject so = null;
        try
        {
            // Create a SoapSerializationEnvelope with some config
            SoapSerializationEnvelope env = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            env.dotNet = true;

            // Set your string as output
            env.setOutputSoapObject(soapObjectString);
            so = (SoapObject) env.getResponse();
        } catch (SoapFault soapFault)
        {
            soapFault.printStackTrace();
        }
        return so;
    }

    public void getVehicleDetailsSoap1()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {

            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<LoadVehicleDetailsXML xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<userHash>" + Helper.getCDATAString(DataManager.getInstance().user.getUserHash()) + "</userHash>");
            soapMessage.append("<usedVehicleStockID>" + Helper.getCDATAString("" + getArguments().getInt("stockID")) + "</usedVehicleStockID>");
            soapMessage.append("</LoadVehicleDetailsXML>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError volleyError)
                {
                    System.out.println("Request volleyError " + volleyError);
                }

                @Override
                public void onResponse(String response)
                {
                    Object result = null;
                    try
                    {
                        if (response != null)
                        {

                            Helper.Log("response", "" + response.toString());
                            new LoadWebServiceResponse(response).execute();
                          /*
                             * else {
							 * ivVehicleImage.setImageResource(R.drawable.
							 * no_media); }
							 */
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        ivVehicleImage.setBackgroundResource(R.drawable.no_media);
                        //   ivVehicleImage.setErrorImageResId(R.drawable.no_media);
                        //  ivVehicleImage.setDefaultImageResId(R.drawable.no_media);
                    }
                    /*finally
                    {
                        if (details != null)
                            putValues();
                        else
                        {
                            hideProgressDialog();
                            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
                        }
                    }*/
                }
            };
            try
            {
                VollyCustomRequest vollyCustomRequest = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL,
                        soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IStockService/LoadVehicleDetailsXML", vollyResponseListener);
                vollyCustomRequest.init("validateUser");
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }

        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    private class LoadWebServiceResponse extends AsyncTask<Void,Void,Void>
    {
        String response = "";

        public LoadWebServiceResponse(String response)
        {
            this.response = response;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            details = ParserManager.parseVehicleDetailsRespose(response);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (details != null)
                putValues();
            else
            {
                hideProgressDialog();
                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
            }
        }
    }

    private void getVehicleDetailsSoap()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("usedVehicleStockID", getArguments().getInt("stockID"), Integer.class));

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
                            SoapObject inner = (SoapObject) outer
                                    .getPropertySafely("Details");
                            details.setUsedVehicleStockID(Integer.parseInt(inner.getPropertySafelyAsString("usedVehicleStockID", "0")));
                            department = inner.getPropertySafelyAsString("department", "");
                            details.setStockCode(Helper.checkEmpty(inner.getPropertySafelyAsString("stockCode", "")));

                            //Added by Asmita
                            //29th Nov 2017
                            String age = inner.getPropertySafelyAsString("age", "0.00").replace(",",".");
                            details.setAge((int) Float.parseFloat(age));

                            //details.setAge((int) Float.parseFloat(inner.getPropertySafelyAsString("age", "0")));
                            details.setVariantID(Integer.parseInt(inner.getPropertySafelyAsString("variantID", "0")));
                            details.setFriendlyName(Helper.checkEmpty(inner.getPropertySafelyAsString("friendlyName", "")));
                            details.setMmcode(Helper.checkEmpty(inner.getPropertySafelyAsString("mmcode", "")));
                            details.setYear(Integer.parseInt(inner.getPropertySafelyAsString("year", "0")));
                            details.setRegistration(Helper.checkEmpty(inner.getPropertySafelyAsString("registration", "")));
                            details.setPrice(Double.parseDouble(inner.getPropertySafelyAsString("price", "0.0")));
                            details.setTradeprice(Double.parseDouble(inner.getPropertySafelyAsString("tradeprice", "0.0")));
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


                            //Added by Asmita
                            //29th Nov 2017
                            String cost = inner.getPropertySafelyAsString("cost", "0.00").replace(",",".");
                            details.setCost((int) Float.parseFloat(cost));

                           //details.setCost(Float.parseFloat(inner.getPropertySafelyAsString("cost", "0.0")));

                            //Added by Asmita
                            //29th Nov 2017
                            String standin = inner.getPropertySafelyAsString("standin", "0.00").replace(",",".");
                            details.setStandin((int) Float.parseFloat(standin));

                            //details.setStandin(Float.parseFloat(inner.getPropertySafelyAsString("standin", "0.0")));

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
                                    if (imgObj.getPropertySafelyAsString("imageDPI", "").equalsIgnoreCase("anyType{}"))
                                    {
                                        image.setImagedpi(96);
                                    } else
                                    {
                                        image.setImagedpi((int) Float.parseFloat(imgObj.getPropertySafelyAsString("imageDPI", "0")));
                                    }
                                    imageList.add(image);
                                }
                                details.setImageList(imageList);
                            }/*
                             * else {
							 * ivVehicleImage.setImageResource(R.drawable.
							 * no_media); }
							 */
                            videoloading(DataManager.getInstance().getYouTubeVideos());
                            hideProgressDialog();
                            if (details.getImageList() != null)
                            {
                                if (details.getImageList().isEmpty())
                                {
                                    progressBarDetails.setVisibility(View.GONE);
                                    ivVehicleImage.setBackgroundResource(R.drawable.no_media);
                                    // ivVehicleImage.setErrorImageResId(R.drawable.no_media);
                                } else
                                {
                                    progressBarDetails.setVisibility(View.GONE);
                                    ivVehicleImage.setImageUrl(URLDecoder.decode(details.getImageList().get(0).getLink(), "utf-8"), imageLoader);
                                }
                            } else
                            {
                                progressBarDetails.setVisibility(View.GONE);
                                ivVehicleImage.setBackgroundResource(R.drawable.no_media);
                                //  ivVehicleImage.setErrorImageResId(R.drawable.no_media);
                            }

                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        ivVehicleImage.setBackgroundResource(R.drawable.no_media);
                        //   ivVehicleImage.setErrorImageResId(R.drawable.no_media);
                        //  ivVehicleImage.setDefaultImageResId(R.drawable.no_media);
                    } finally
                    {
                        if (details != null)
                            putValues();
                        else
                        {
                            hideProgressDialog();
                            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
                        }
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    public void getVatientDetails(int variantId)
    {
        ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
        parameterList.add(new Parameter("variantID", variantId, Integer.class));

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
                hideProgressDialog();
                if (response != null)
                {
                    Log.d(getTag(), response.toString());
                    if (response instanceof SoapFault
                            || response instanceof SoapObject)
                    {
                        //tvName.setText(getString(R.string.no_information));
                    } else
                    {
                        try
                        {
                            String string = (String) ((SoapPrimitive) response)
                                    .getValue();
                            /*
                             * String [] data = string.split("|");
							 * if(data.length>=3)
							 */
                            String data = StringUtils.substringAfter(string, "|");
                            tvName.setText(StringUtils.substringBetween(data,
                                    "|", "|")
                                    + " | "
                                    + StringUtils.substringAfterLast(data, "|")
                                    + " | "
                                    + StringUtils.substringBefore(data, "|"));
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                } else
                {
                    //tvName.setText(getString(R.string.no_information));
                }
            }
        }).execute();
    }

    private void getTenderList()
    {
        // Add parameters to request in arraylist
        ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("userHash",
                DataManager.getInstance().user.getUserHash(), String.class));

        // create web service inputs
        DataInObject inObj = new DataInObject();
        inObj.setMethodname("ListTenderXML");
        inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
        inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE
                + "IStockService/ListTenderXML");
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
                        SoapObject obj = (SoapObject) result;
                        tenderList = new ArrayList<SmartObject>();
                        SoapObject inner = (SoapObject) obj
                                .getPropertySafely("Tenders");
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject tenderObj = (SoapObject) inner
                                    .getProperty(i);
                            SmartObject smartObject = new SmartObject();
                            smartObject.setId(Integer.parseInt(tenderObj
                                    .getPropertySafelyAsString("tenderID", "0")));
                            smartObject.setName(tenderObj
                                    .getPropertySafelyAsString("tenderName", ""));
                            tenderList.add(smartObject);
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                } finally
                {
                    showTenderPopup(edAddTender);
                }

            }
        }).execute();
    }

    private void getTypeList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListVehicletypeXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListVehicletypeXML");
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
                            SoapObject obj = (SoapObject) result;
                            typeList = new ArrayList<SmartObject>();
                            SoapObject inner = (SoapObject) obj
                                    .getPropertySafely("VehicleTypes");
                            for (int i = 0; i < inner.getPropertyCount(); i++)
                            {
                                SoapObject tenderObj = (SoapObject) inner.getProperty(i);
                                SmartObject smartObject = new SmartObject();
                                smartObject.setId(Integer.parseInt(tenderObj.getPropertySafelyAsString("vtID", "0")));
                                smartObject.setName(tenderObj.getPropertySafelyAsString("vtName", ""));

                                typeList.add(smartObject);
                            }
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

    private String getString(EditText edtInpuText)
    {
        return edtInpuText.getText().toString().trim();
    }

    private void updateVehicleXML(final boolean close)
    {

        showProgressDialog();
        try
        {
            final StringBuilder soapBuilder = new StringBuilder();
            soapBuilder.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "<s:Body>");
            soapBuilder.append("<UpdateVehicleViaObj xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\"><userHash>"
                    + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapBuilder.append("<vehicleObject xmlns:d4p1=\"http://schemas.datacontract.org/2004/07/StockServiceNS\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">");
            soapBuilder.append("<d4p1:Colour>" + Helper.getCDATAString(getString(edtColor)) + "</d4p1:Colour>");
            soapBuilder.append("<d4p1:Comments>" + Helper.getCDATAString(getString(edtComments)) + "</d4p1:Comments>");
            soapBuilder.append("<d4p1:Condition>" + Helper.getCDATAString(getString(edtCondition)) + " </d4p1:Condition>");
            soapBuilder.append("<d4p1:DeleteReason>" + "" + "</d4p1:DeleteReason>");
            soapBuilder.append("<d4p1:DepartmentID>" + Integer.parseInt(edType.getTag() == null ? "2" : edType.getTag().toString()) + "</d4p1:DepartmentID>");
            soapBuilder.append("<d4p1:EngineNo>" + Helper.getCDATAString(getString(edtEngineNo)) + "</d4p1:EngineNo>");
            soapBuilder.append("<d4p1:Extras>" + Helper.getCDATAString(getString(edtExtras)) + "</d4p1:Extras>");
            soapBuilder.append("<d4p1:FullServiceHistory>0</d4p1:FullServiceHistory>");
            soapBuilder.append("<d4p1:IgnoreImport>" + (cbDontImport.isChecked() ? 1 : 0) + "</d4p1:IgnoreImport>");
            soapBuilder.append("<d4p1:InternalNote>" + Helper.getCDATAString(getString(edtInternalNote)) + "</d4p1:InternalNote>");
            soapBuilder.append("<d4p1:IsDeleted>" + (cbRemoveVehicle.isChecked() ? 1 : 0) + "</d4p1:IsDeleted>");
            soapBuilder.append("<d4p1:IsProgram>" + (cbProgramVehicle.isChecked() ? 1 : 0) + "</d4p1:IsProgram>");
            soapBuilder.append("<d4p1:IsRetail>" + (cbRetail.isChecked() ? 1 : 0) + "</d4p1:IsRetail>");
            soapBuilder.append("<d4p1:IsTender>" + (TextUtils.isEmpty(edAddTender.getText().toString()) ? 0 : 1) + "</d4p1:IsTender>");
            soapBuilder.append("<d4p1:IsTrade>" + (cbTrade.isChecked() ? 1 : 0) + "</d4p1:IsTrade>");
            soapBuilder.append("<d4p1:Location>" + Helper.getCDATAString(getString(edtLocation)) + "</d4p1:Location>");
            soapBuilder.append("<d4p1:MMCode>" + details.getMmcode() + "</d4p1:MMCode>");
            soapBuilder.append("<d4p1:ManufacturerModelCode>" + Helper.getCDATAString(edtOEM) + "</d4p1:ManufacturerModelCode>");
            soapBuilder.append("<d4p1:Mileage>" + getString(edtMilage) + "</d4p1:Mileage>");
            soapBuilder.append("<d4p1:OriginalCost>" + Helper.getDoubleValue(edtCost) + "</d4p1:OriginalCost>");
            soapBuilder.append("<d4p1:Override>" + (cbIgnoreSetting.isChecked() ? 1 : 0) + "</d4p1:Override>");
            soapBuilder.append("<d4p1:OverrideReason >" + "" + "</d4p1:OverrideReason>");
            soapBuilder.append("<d4p1:PlusAccessories>0.0</d4p1:PlusAccessories>");
            soapBuilder.append("<d4p1:PlusAdmin>0.0</d4p1:PlusAdmin>");
            soapBuilder.append("<d4p1:PlusMileage>0.0</d4p1:PlusMileage>");
            soapBuilder.append("<d4p1:PlusRecon>0.0</d4p1:PlusRecon>");
            soapBuilder.append("<d4p1:Price>" + Helper.getDoubleValue(edtPriceRetail) + "</d4p1:Price>");
            soapBuilder.append("<d4p1:ProgramName>" + Helper.getCDATAString(getString(edtProgramName)) + "</d4p1:ProgramName>");
            soapBuilder.append("<d4p1:Registration>" + Helper.getCDATAString(edtRegNo) + "</d4p1:Registration>");
            soapBuilder.append("<d4p1:ShowErrorDisclaimer>" + (cbErrorInfo.isChecked() ? 1 : 0) + "</d4p1:ShowErrorDisclaimer>");
            soapBuilder.append("<d4p1:Standin>" + Helper.getDoubleValue(edtStand) + "</d4p1:Standin>");
            soapBuilder.append("<d4p1:StockCode>" + Helper.getCDATAString(edtStock) + "</d4p1:StockCode>");
            soapBuilder.append("<d4p1:TouchMethod>android App</d4p1:TouchMethod>");
            soapBuilder.append("<d4p1:TradePrice>" + Helper.getDoubleValue(edtPriceTrade) + "</d4p1:TradePrice>");
            soapBuilder.append("<d4p1:Trim>" + Helper.getCDATAString(edtTrim) + "</d4p1:Trim>");
            soapBuilder.append("<d4p1:UsedVehicleStockID>" + details.getUsedVehicleStockID() + "</d4p1:UsedVehicleStockID>");
            soapBuilder.append("<d4p1:UsedYear>" + details.getYear() + "</d4p1:UsedYear>");
            soapBuilder.append("<d4p1:VIN>" + Helper.getCDATAString(edtVIN) + "</d4p1:VIN>");
            soapBuilder.append("</vehicleObject>");
            soapBuilder.append("</UpdateVehicleViaObj></s:Body></s:Envelope>");
            Helper.Log("UpdateVehicleViaObj request", soapBuilder.toString());

            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Helper.Log("UpdateVehicleViaObj Error: ",
                            "" + error.getMessage());
                    hideProgressDialog();
                    showErrorDialog(errorMessage);
                }

                @Override
                public void onResponse(String response)
                {
                    handleUpdateResponse(response, close);
                }
            };
            VollyCustomRequest request = new VollyCustomRequest(
                    Constants.STOCK_WEBSERVICE_URL, soapBuilder.toString(),
                    Constants.TEMP_URI_NAMESPACE
                            + "IStockService/UpdateVehicleViaObj",
                    vollyResponseListener);
            try
            {
                request.init();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }

    }

    private void handleUpdateResponse(String response, boolean close)
    {
        try
        {
            if (!TextUtils.isEmpty(response))
            {
                Helper.Log("response", response.toString());
                String result[] = new String[2];
                result = ParserManager.parseUpdateVehicleResponse(response)
                        .split(":");
                if (result[0].contains("OK"))
                {
                    if (!TextUtils.isEmpty(result[1]))
                    {
                        Helper.Log("response", result.toString());

                        updatedImages = imageDragableGridView.getUpdatedImageListWithoutPlus();
                        if (updatedImages.isEmpty())
                        {
                            if (imageDragableGridView.getDeletedImages().isEmpty())
                                showSuccessDialog(close);
                            else
                            {
                                noOfImagesInGrid = updatedImages.size();
                                checkDelete(close);
                            }
                        } else
                        {
                            noOfImagesInGrid = updatedImages.size();
                            addImageToVehicleBase64(
                                    details.getUsedVehicleStockID(), close);
                        }

                    }
                } else
                {
                    Helper.Log("response", "" + response);
                    hideProgressDialog();
                    if (result[0].contains("ERROR"))
                    {
                        if (!TextUtils.isEmpty(result[1]))
                            CustomDialogManager.showOkDialog(getActivity(), ""
                                    + result[1]);

                        else
                            CustomDialogManager.showOkDialog(getActivity(),
                                    errorMessage);
                    } else
                        CustomDialogManager.showOkDialog(getActivity(),
                                errorMessage);
                }
            } else
            {
                Helper.Log("response", "" + response);
                hideProgressDialog();
                CustomDialogManager.showOkDialog(getActivity(), errorMessage);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateImageId(String response)
    {
        if (response != null)
        {
            String AddImageToVehicleResponse = ParserManager
                    .parseAddImageToVehicleResponse(response);
            String result[] = new String[2];
            result = AddImageToVehicleResponse.toString().split(":");
            if (result[0].contains("OK"))
            {
                // ((BaseImage)imageDragableGridView.getAdapter().getItem(imageUploadIndex)).setLocal(false);
                ((BaseImage) imageDragableGridView.getAdapter().getItem(imageUploadIndex)).setId(Integer.parseInt(result[1]));
            }
        }
    }

    public void addImageToVehicleBase64(final int vehicleId, final boolean close)
    {
        showVideoAleart = 3;

        // Check is on wifi or mobile network
        if (NetworkUtil.getConnectivityStatusString(getActivity()) == ConnectivityManager.TYPE_WIFI)
        {
            sendImagesToServerOrDataBase(false, vehicleId, close);
        } else
        {
            boolean showImageAleart = false;

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
                                sendImagesToServerOrDataBase(false, vehicleId, close);
                                break;
                            // Upload With WIFI
                            case Dialog.BUTTON_NEGATIVE:
                                showVideoAleart = 2;
                                sendImagesToServerOrDataBase(true, vehicleId, close);
                                break;
                        }
                    }
                });
            } else
            {
                sendImagesToServerOrDataBase(false, vehicleId, close);
            }

        }
    }

    /**
     * This Method is used to send selected images to server.
     *
     * @param isSaveDataBase = if true (Save to local database) if false (send directly to server on any network)
     * @param vehicleId      =
     * @param close          = if true (Save to local database) if false (send directly to server on any network)
     */
    private void sendImagesToServerOrDataBase(final boolean isSaveDataBase, final int vehicleId, final boolean close)
    {
        if (!updatedImages.get(uploadImageCount).isLocal())
        {
            uploadImageCount++;
            if (noOfImagesInGrid == uploadImageCount)
                checkImagePriority(close);
            else
                sendImagesToServerOrDataBase(isSaveDataBase, vehicleId, close);
        } else
        {
            String base64String = Helper.convertBitmapToBase64(updatedImages.get(uploadImageCount).getPath());

            if (TextUtils.isEmpty(base64String))
            {
                uploadImageCount++;
                if (noOfImagesInGrid == uploadImageCount)
                    checkImagePriority(close);
                else
                    sendImagesToServerOrDataBase(isSaveDataBase, vehicleId, close);
            }

            final StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<AddImageToVehicleBase64 xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");

            if (isSaveDataBase)
                soapMessage.append("<userHash>" + Constants.CHANGE_THIS_USER_HASH + "</userHash>");
            else
                soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");

            soapMessage.append("<usedVehicleStockID>" + vehicleId + "</usedVehicleStockID>");
            soapMessage.append("<imageBase64>" + base64String + "</imageBase64>");
            soapMessage.append("<imageName>" + Helper.getNameWithExtenstion(updatedImages.get(uploadImageCount).getPath()) + "</imageName>");
            soapMessage.append("<imageTitle>" + Helper.getName(updatedImages.get(uploadImageCount).getPath()) + "</imageTitle>");
            soapMessage.append("<imageSource>phone app</imageSource>");
            soapMessage.append("<imagePriority>" + (uploadImageCount + 1) + "</imagePriority>");
            soapMessage.append("<imageIsEtched>0</imageIsEtched>");
            soapMessage.append("<imageIsBranded>0</imageIsBranded>");
            soapMessage.append("<imageAngle></imageAngle>");
            soapMessage.append("</AddImageToVehicleBase64></Body></Envelope>");

            Helper.Log("soapMessage " + uploadImageCount, "" + soapMessage);

            // Check do we need to save in local database or send to server.
            if (isSaveDataBase)
            {
                // Save in local Database
                SMDatabase myDatabase = new SMDatabase(getActivity());
                myDatabase.insertRecords(soapMessage.toString(), Constants.SavePhotosAndExtrasImage);

                try
                {
                    updatedImages.get(uploadImageCount).setLocal(false);
                    uploadImageCount++;
                    if (uploadImageCount == noOfImagesInGrid)
                    {
                        checkImagePriority(close);
                    } else
                    {
                        sendImagesToServerOrDataBase(isSaveDataBase, vehicleId, close);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            } else
            {
                VollyResponseListener vollyResponseListener = new VollyResponseListener()
                {

                    @Override
                    public void onErrorResponse(VolleyError arg0)
                    {
                        try
                        {
                            updatedImages.get(uploadImageCount).setLocal(false);
                            uploadImageCount++;
                            if (uploadImageCount == noOfImagesInGrid)
                            {
                                checkImagePriority(close);
                            } else
                            {
                                sendImagesToServerOrDataBase(isSaveDataBase, vehicleId, close);
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onResponse(String response)
                    {
                        updateImageId(response);
                        // updatedImages.get(uploadImageCount).setLocal(false);
                        try
                        {
                            Helper.Log("response", response + "");
                            uploadImageCount++;
                            if (uploadImageCount >= noOfImagesInGrid)
                            {
                                checkImagePriority(close);
                            } else
                            {
                                sendImagesToServerOrDataBase(isSaveDataBase, vehicleId, close);
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                };

                VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL,
                        soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IStockService/AddImageToVehicleBase64", vollyResponseListener);
                try
                {
                    request.init();
                } catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void checkImagePriority(boolean close)
    {
        if (imageDragableGridView.isPriorityChanged())
        {
            changeImagePriority(close);
        } else
        {
            checkDelete(close);
        }
    }

    private void changeImagePriority(final boolean close)
    {

        if (noOfImagesInGrid == priorityImageCount)
        {
            checkDelete(close);
        } else
        {
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager
                    .getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("usedVehicleStockID", details
                    .getUsedVehicleStockID(), Integer.class));
            parameterList.add(new Parameter("imageID", updatedImages.get(
                    priorityImageCount).getId(), Integer.class));
            parameterList.add(new Parameter("newPriorityID",
                    priorityImageCount + 1, Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ChangeImagePriorityForVehicle");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE
                    + "IStockService/ChangeImagePriorityForVehicle");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("response", "" + result);
                        priorityImageCount++;
                        if (priorityImageCount == noOfImagesInGrid)
                        {
                            checkDelete(close);
                        } else
                        {
                            changeImagePriority(close);
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        }
    }

    private void checkDelete(boolean close)
    {
        if (imageDragableGridView.getDeletedImages().isEmpty())
        {
            showSuccessDialog(close);
        } else
            deleteImage(close);
    }

    private void deleteImage(final boolean close)
    {
        if (deleteImageCount == imageDragableGridView.getDeletedImages().size())
        {
            showSuccessDialog(close);
        } else
        {
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager
                    .getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("usedVehicleStockID", details
                    .getUsedVehicleStockID(), Integer.class));
            parameterList.add(new Parameter("imageID", imageDragableGridView
                    .getDeletedImages().get(deleteImageCount).getId(),
                    Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("RemoveImageFromVehicle");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE
                    + "IStockService/RemoveImageFromVehicle");
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
                        Helper.Log("response", "" + result);
                        deleteImageCount++;
                        if (deleteImageCount == imageDragableGridView
                                .getDeletedImages().size())
                        {
                            showSuccessDialog(close);
                        } else
                        {
                            deleteImage(close);
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        }
    }

    private void RemoveVideoLinkFromVehicle(final boolean close, final ArrayList<YouTubeVideo> videos)
    {
        showProgressDialog();
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage
                    .append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<UnLinkVideo xmlns=\""
                    + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<userHash>"
                    + DataManager.getInstance().user.getUserHash()
                    + "</userHash>");
            soapMessage.append("<clientID>"
                    + DataManager.getInstance().user.getDefaultClient().getId()
                    + "</clientID>");
            soapMessage.append("<linkID>"
                    + videos.get(deleteVideoCount).getVideoLinkID()
                    + "</linkID>");
            soapMessage.append("</UnLinkVideo>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            VollyResponseListener listener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Helper.showToast(getString(R.string.error_getting_data),
                            getActivity());
                    VolleyLog.e(" UnLinkVideo Error: ", error.toString());
                }

                @Override
                public void onResponse(String response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    // hideProgressDialog();
                    Helper.Log("UnLinkVideo", "" + response);
                    if (response.contains("Link removed"))
                    {
                        if (videos.size() > 0)
                        {
                            videos.remove(deleteVideoCount);
                            // deleteVideoCount++;
                            if (videos.size() != 0)
                            {
                                RemoveVideoLinkFromVehicle(close, deletedVideos);
                            } else
                            {
                                deletedVideos.clear();
                                showSuccessDialog(close);
                            }
                        } else
                        {
                            deletedVideos.clear();
                            showSuccessDialog(close);
                        }
                    }
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(
                    Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/UnLinkVideo",
                    listener);
            try
            {
                request.init();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(),
                    getString(R.string.no_internet_connection));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void showTenderPopup(View v)
    {
        if (getActivity() != null)
        {
            if (tenderList != null)
            {
                Helper.showDropDown(v, new ArrayAdapter(getActivity(),
                                R.layout.list_item_text2, R.id.tvText, tenderList),
                        new OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id)
                            {
                                cbTrade.setChecked(false);
                                cbRetail.setChecked(false);
                                edAddTender.setText(tenderList.get(position)
                                        + "");
                            }
                        });
            } else
            {
                Helper.showToast(getString(R.string.no_tender), getActivity());
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void showTypePopup(View v)
    {
        if (getActivity() != null)
        {
            if (typeList != null)
            {
                Helper.showDropDown(v, new ArrayAdapter(getActivity(),
                                R.layout.list_item_text2, R.id.tvText, typeList),
                        new OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id)
                            {
                                edType.setText(typeList.get(position) + "");
                            }
                        });
            } else
            {
                Helper.showToast(getString(R.string.no_type), getActivity());
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Edit Stock");
    }

    @SuppressWarnings("static-access")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK)
        {
            // infoFragment =null;
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
                    videoPath = ImageHelper.getVideoPathFromGalleryAboveKitkat(
                            getActivity(), selectedVideo);
                } else
                {
                    videoPath = ImageHelper.getVideoRealPathFromURI(
                            getActivity(), selectedVideo);
                }

                if (TextUtils.isEmpty(videoPath))
                {
                    videoPath = ImageHelper.getPath(getActivity(),
                            selectedVideo);
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
                            Helper.showToast("Video is corrupted,can't upload",
                                    getActivity());
                            return;
                        } else if ((length / 1000) > 50)
                        {
                            // Show Your Messages
                            Helper.showToast("Video size is more than 50 MB.",
                                    getActivity());
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
        /*
         * if (videoDragableGridView != null) { if
		 * (videoDragableGridView.isOptionSelected())
		 * videoDragableGridView.onActivityResult(requestCode, resultCode,data);
		 * }
		 */
    }

    private int getTypeIdByTypeName(String dept)
    {
        int tag = 2;
        if (typeList != null)
        {
            for (int i = 0; i < typeList.size(); i++)
            {
                if (typeList.get(i).getName().equals(dept))
                {
                    tag = typeList.get(i).getId();
                }
            }
        }
        return tag;
    }

    private void showSuccessDialog(final boolean close)
    {
        if (deletedVideos.size() != 0)
        {
            RemoveVideoLinkFromVehicle(close, deletedVideos);
        } else
        {
            if (isVideoSelected)
            {
                if (showVideoAleart == 3)
                    showVideoAleart = 1;
                uploadWarning(close);
            } else
            {
                hideProgressDialog();
                VehicleActivity.isVehicleUpdated = true;
                CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        if (type == Dialog.BUTTON_POSITIVE)
                        {
                            if (close)
                            {
                                if (getArguments().containsKey("fromActiveBidsFragment"))
                                    getActivity().finish();
                                else
                                    getActivity().getFragmentManager().popBackStack();
                            } else
                            {
                                reLoadAllData();
                            }
                        }
                    }
                });
            }
        }
    }

    private void reLoadAllData()
    {
        /*DataManager.getInstance().getYouTubeVideos().clear();
        DataManager.getInstance().getimageArray().clear();

        deletedVideos = new ArrayList<YouTubeVideo>();
        uploaded_videos = new ArrayList<YouTubeVideo>();
        youTubeVideos = new ArrayList<YouTubeVideo>();

        getVehicleDetailsSoap();
        getTypeList();*/
    }

	/*
     * private void displayShowCaseView() { if
	 * (!ShowcaseSessions.isSessionAvailable(getActivity(),
	 * ListDetailsFragment.class.getSimpleName())) { ArrayList<TargetView>
	 * viewList = new ArrayList<TargetView>(); viewList.add(new TargetView(
	 * scView, ShowCaseType.SwipeUpDown,
	 * getString(R.string.submit_vehicle_information_by_scrolling_up_and_down
	 * ))); ShowcaseLayout showcaseView = new ShowcaseLayout(getActivity());
	 * showcaseView.setShowcaseView(viewList);
	 *
	 * ((ViewGroup) getActivity().getWindow().getDecorView())
	 * .addView(showcaseView); ShowcaseSessions.saveSession(getActivity(),
	 * ListDetailsFragment.class.getSimpleName()); } }
	 */
}
