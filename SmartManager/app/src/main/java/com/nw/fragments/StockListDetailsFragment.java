package com.nw.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nw.adapters.HorizontalListViewAdapter;
import com.nw.interfaces.DialogListener;
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
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomTextView;
import com.smartmanager.activity.PlayerActivity;
import com.smartmanager.android.R;
import com.smartmanager.android.live.SmartManagerApplication;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Vector;

public class StockListDetailsFragment extends BaseFragement implements
        OnClickListener
{
    TextView tvTitleItemStockList, tvDistanceItemStockList,
            tvColorItemStockList, tvStockCodeItem, tvStockRegistration,
            tvTypeItemStockList, tvTimeLeftItemStockList;
    TextView tvRetailPriceStockList;
    HorizontalListView hlvStockCarImages;
    HorizontalListViewAdapter horizontalListViewAdapter;
    TextView tvStockExtras, tvStockComment, tvStockType, tvInternalMemo, tvVehicleSpecs;
    ArrayList<MyImage> images = new ArrayList<MyImage>(), tempList;
    ImageLoader imageLoader;
    ImageView ivStockPic, ivPicture, ivPicture1;
    Vehicle vehicle;
    ArrayList<BaseImage> imageList;
    ArrayList<YouTubeVideo> videos;
    String department = "Used";
    CustomButton btnSendBrochure;
    RelativeLayout rlVideo;
    LinearLayout llImage1, llImage2, llSpecs, llInternalMemo;
    FrameLayout flItemStockBig;
    StocksDetailFragment stocksDetailFragment;
    DisplayImageOptions options;
    VehicleDetails vehicleDetails;

    boolean isFromVariantList = false;
    Variant variant;
    LinearLayout llayout_Varant_Details, llayout_eBroucher_Details;
    TextView tvTitle, tvSubTitle, tvRetailPrice;
    String operationType ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_stock_list_details, container, false);
        setHasOptionsMenu(true);
        showActionBar("Vehicle Info");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        if (videos == null)
        {
            videos = new ArrayList<YouTubeVideo>();
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
            initView(view);
            if (vehicle != null)
            {
                putValues();
            }

            if (variant != null)
            {
                putValues();
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

    private void putValues()
    {
        if (isFromVariantList)
        {
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
            tvColorItemStockList.setText(vehicle.getColour());

            tvRetailPriceStockList.setText(Helper.formatPrice(new BigDecimal(vehicle.getRetailPrice()) + ""));
            tvTimeLeftItemStockList.setText(" " + vehicle.getExpires() + " Days");
            if(operationType.equalsIgnoreCase("new")){
                tvStockRegistration.setText("New");
                tvTitleItemStockList.setText(vehicle.getFriendlyName());
            }else {
                tvStockRegistration.setText(vehicle.getRegNumber());
                tvTitleItemStockList.setText(vehicle.getYear() + " " + vehicle.getFriendlyName());
            }
            tvStockCodeItem.setText(vehicle.getStockNumber());
            if (vehicle.getMileage() == 0)
            {
                tvDistanceItemStockList.setText("Mileage?");
            } else
            {
                tvDistanceItemStockList.setText(Helper.getFormattedDistance(vehicle.getMileage() + "") + " Km");
            }
            getVehicleDetailsSoap();
        }


        //fetchCommentsAndExtras();
    }

    private void initView(View view)
    {
        btnSendBrochure = (CustomButton) view.findViewById(R.id.btnSendBrochure);
        flItemStockBig = (FrameLayout) view.findViewById(R.id.flItemStockBig);
        tvTitleItemStockList = (TextView) view.findViewById(R.id.tvTitleItemStockList);
        tvStockRegistration = (TextView) view.findViewById(R.id.tvStockRegistration);
        tvDistanceItemStockList = (TextView) view.findViewById(R.id.tvDistanceItemStockList);
        tvColorItemStockList = (TextView) view.findViewById(R.id.tvColorItemStockList);
        tvStockCodeItem = (TextView) view.findViewById(R.id.tvStockCodeItem);
        tvRetailPriceStockList = (CustomTextView) view.findViewById(R.id.tvRetailPriceStockList);
        tvStockExtras = (TextView) view.findViewById(R.id.tvStockExtras);
        tvStockComment = (TextView) view.findViewById(R.id.tvStockComment);
        tvStockType = (TextView) view.findViewById(R.id.tvStockType);
        tvTimeLeftItemStockList = (TextView) view.findViewById(R.id.tvTimeLeftItemStockList);
        tvInternalMemo = (TextView) view.findViewById(R.id.tvInternalMemo);
        tvVehicleSpecs = (TextView) view.findViewById(R.id.tvVehicleSpecs);
        ivStockPic = (ImageView) view.findViewById(R.id.ivItemStockList);
        ivStockPic.setOnClickListener(this);
        ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
        rlVideo = (RelativeLayout) view.findViewById(R.id.rlVideo);
        ivPicture1 = (ImageView) view.findViewById(R.id.ivPicture1);
        llImage1 = (LinearLayout) view.findViewById(R.id.llImage1);
        llImage2 = (LinearLayout) view.findViewById(R.id.llImage2);
        llSpecs = (LinearLayout) view.findViewById(R.id.llSpecs);
        llInternalMemo = (LinearLayout) view.findViewById(R.id.llInternalMemo);

        llImage1.setOnClickListener(this);
        llImage2.setOnClickListener(this);
        rlVideo.setVisibility(View.GONE);


        hlvStockCarImages = (HorizontalListView) view.findViewById(R.id.hlvStockCarImages);
        hlvStockCarImages.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3)
            {
                navigateToLargeImage(position, images);
            }
        });

        llayout_Varant_Details = (LinearLayout) view.findViewById(R.id.llayout_Varant_Details);
        llayout_eBroucher_Details = (LinearLayout) view.findViewById(R.id.llayout_eBroucher_Details);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvSubTitle = (TextView) view.findViewById(R.id.tvSubTitle);
        tvRetailPrice = (TextView) view.findViewById(R.id.tvRetailPrice);

        btnSendBrochure.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isFromVariantList)
                {
                    if(variant.isEBrochureFlag())
                    {
                        stocksDetailFragment = new StocksDetailFragment();
                        Bundle args = new Bundle();
                        args.putParcelable("varientDetails", variant);
                        args.putString("operationType",getArguments().getString("operationType"));
                        stocksDetailFragment.setArguments(args);
                        getFragmentManager().beginTransaction().replace(R.id.Container, stocksDetailFragment).addToBackStack(null).commit();
                    }else
                    {
                        CustomDialogManager.showErrorDialogEmail(getActivity(), "Coming Soon! Please contact support@ix.co.za about this functionality.");
                    }
                } else
                {
                    if (vehicle.isEBrochureFlag())
                    {
                        stocksDetailFragment = new StocksDetailFragment();
                        Bundle args = new Bundle();
                        args.putParcelable("selectedVehicle", vehicle);
                        args.putString("fromFragment", "E-Brochure");
                        args.putString("operationType",getArguments().getString("operationType"));
                        stocksDetailFragment.setArguments(args);
                        getFragmentManager().beginTransaction().replace(R.id.Container, stocksDetailFragment).addToBackStack(null).commit();
                    } else
                    {
                        CustomDialogManager.showErrorDialogEmail(getActivity(), "Coming Soon! Please contact support@ix.co.za about this functionality.");
                    }
                }

                /*
                 * stocksDetailFragment = new StocksDetailFragment(); Bundle
				 * args = new Bundle(); args.putParcelable("selectedVehicle",
				 * vehicle); stocksDetailFragment.setArguments(args);
				 * getFragmentManager
				 * ().beginTransaction().replace(R.id.Container,
				 * stocksDetailFragment).addToBackStack(null).commit();
				 */
            }
        });
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

    private void videoloading(ArrayList<YouTubeVideo> videos)
    {

        if (videos.size() == 0)
        {
            rlVideo.setVisibility(View.GONE);
        }
        if (videos.size() >= 1)
        {
            rlVideo.setVisibility(View.VISIBLE);
            llImage1.setVisibility(View.VISIBLE);
            llImage2.setVisibility(View.GONE);
            SmartManagerApplication.getImageLoader().displayImage(videos.get(0).getVideoThumbUrl(), ivPicture, options);

        }
        if (videos.size() >= 2)
        {
            rlVideo.setVisibility(View.VISIBLE);
            llImage2.setVisibility(View.VISIBLE);
            llImage1.setVisibility(View.VISIBLE);
            SmartManagerApplication.getImageLoader().displayImage(videos.get(0).getVideoThumbUrl(), ivPicture, options);
            SmartManagerApplication.getImageLoader().displayImage(videos.get(1).getVideoThumbUrl(), ivPicture1, options);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Vehicle Info");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (stocksDetailFragment != null)
            stocksDetailFragment.onActivityResult(requestCode, resultCode, data);

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

                            if (inner.hasProperty("Videos"))
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
                                    videos.add(tubeVideo);
                                }
                                DataManager.getInstance().getYouTubeVideos().addAll(videos);
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
                                    //		image.setImagedpi((int) Float.parseFloat(imgObj.getPropertySafelyAsString("imageDPI", "0")));

                                    imageList.add(image);
                                }
                                details.setImageList(imageList);

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
                                SmartManagerApplication.getImageLoader().displayImage(images.get(0).getThumb(), ivStockPic, options);
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
                                ivStockPic.setImageResource(R.drawable.no_media);
                            }
                            if (!TextUtils.isEmpty(details.getInternalnote()))
                            {
                                tvInternalMemo.setText(details.getInternalnote());
                            } else
                            {
                                llInternalMemo.setVisibility(View.GONE);
                                ;
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
                            videoloading(videos);
                            hideProgressDialog();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                        ivStockPic.setImageResource(R.drawable.no_media);
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
                vehicleDetails.setGears(identity+ "");
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
            detailsBuilder.append(summeryDetails.getGearbox() + "; ");
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.llImage1:
                if (!videos.get(0).isLocal())
                {
                    watchYoutubeVideo(videos.get(0).getVideoCode());
                } else
                {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra("video_url", videos.get(0).getVideoFullPath());
                    startActivity(intent);
                }
                break;

            case R.id.llImage2:
                if (!videos.get(1).isLocal())
                {
                    watchYoutubeVideo(videos.get(1).getVideoCode());
                } else
                {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra("video_url", videos.get(1).getVideoFullPath());
                    startActivity(intent);
                }
                break;

            case R.id.ivItemStockList:
                navigateToLargeImage(0, images);
                break;
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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
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

                            if (inner.hasProperty("Videos"))
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
                                    videos.add(tubeVideo);
                                }
                                DataManager.getInstance().getYouTubeVideos().addAll(videos);
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
                                    image.setType(Integer.parseInt(imgObj.getPropertySafelyAsString("imageType", "0")));
                                    //		image.setImagedpi((int) Float.parseFloat(imgObj.getPropertySafelyAsString("imageDPI", "0")));*/

                                    image.setLink(imgListObj.getProperty(i).toString());
                                    imageList.add(image);
                                }
                                details.setImageList(imageList);

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
                                SmartManagerApplication.getImageLoader().displayImage(images.get(0).getThumb(), ivStockPic, options);
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
                                ivStockPic.setImageResource(R.drawable.no_media);
                            }
                            if (!TextUtils.isEmpty(details.getInternalnote()))
                            {
                                tvInternalMemo.setText(details.getInternalnote());
                            } else
                            {
                                llInternalMemo.setVisibility(View.GONE);
                                ;
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
                            videoloading(videos);
                            hideProgressDialog();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                        ivStockPic.setImageResource(R.drawable.no_media);
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
