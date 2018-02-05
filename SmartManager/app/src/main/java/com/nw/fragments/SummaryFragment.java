package com.nw.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.greysonparrelli.permiso.Permiso;
import com.luminous.pick.CustomGalleryActivity;
import com.nw.adapters.AYearOlderAdapter;
import com.nw.adapters.NewVariantAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.BaseImage;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.ScanVIN;
import com.nw.model.SmartObject;
import com.nw.model.Variant;
import com.nw.model.VehicleDetails;
import com.nw.model.YearYounger;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import net.photopay.barcode.BarcodeDetailedData;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import mobi.pdf417.Pdf417MobiScanData;
import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;

import static com.utils.AppSession.TAG;

public class SummaryFragment extends BaseFragement implements OnClickListener
{
    ImageLoader imageLoader;
    TextView tvTitleCarName, tvCardetails, tvYoungerCount, tvSameYearCount, tvOlderCount, tvReviewCount, tvDemandSummary,
            tvAvailibility, tvAvgDays, tvLeadPool, tvWarrantyServices, tvVIN, llDoAppraisal, tvVInStatus;
    RelativeLayout rlExtrainfo, rlChangeVehicle, rlYoungerVehicle, rlSameYear, rlOlder, rlManualSection;
    LinearLayout llayoutExtraInfo, llayoutModel, llReviews, llVINHistory, llNewPricePlotter, llayout_Average_days, llayoutDemand, llayouSimilar, llayoutSalesHistory, llayoutLeadPool,
            llayout_verify_vin, llOEM, llAvailibility, llPricing, llAvailibilityLine, llayoutManualSection;
    StaticListView lvYounger, lvsameYear, lvOlder;
    ImageView ivArrowIconExtraInfo, ivArrowIcon, ivYoungerArrow, ivSameYearArrow, ivOlderArrow, ivManualSectionArrow;
    NetworkImageView ivVehicleImage;
    EditText edMinYear, edMake, edModel, edVariant;
    ArrayList<SmartObject> makeList;
    ArrayList<SmartObject> modelList;
    ArrayList<Variant> variantList;
    int selectedMakeId, selectedModelId, selectedVariantId, selectedPageNumber = 0;
    Button btnClear, btnChangeVehicle, btnScanVIn;
    Variant variant;
    DoAppraisalFragment doAppraisalFragment;
    VehicleDetails summeryDetails;
    ArrayList<BaseImage> vehicleImage;
    TextView tvTUAPriceTrade, tvTUAPriceRetail, tvTUAPriceFetch, tvTUAPriceDate, tvTUAPriceUpdate;
    TextView tvOnlinePriceNow, tvSimplePriceTrade, tvSimplePriceRetail, tvIXTrader, tvPrivateadvert;
    AYearOlderAdapter summaryAYearOlderAdapter;
    ArrayList<YearYounger> al_summaryAYearOlder;
    ArrayList<YearYounger> al_summaryAYearYounger;
    ArrayList<YearYounger> al_summaryASameYear;

    private static final int MY_REQUEST_CODE_DO_APPRAISAL = 1337;
    private static final int MY_REQUEST_CODE_VINHISTORY = 1338;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            summeryDetails = getArguments().getParcelable("summaryObejct");
            selectedVariantId = summeryDetails.getVariantID();
        }
        imageLoader = VolleySingleton.getInstance().getImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        setHasOptionsMenu(true);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initialise(view);
        if (summeryDetails != null)
        {
            putValues();
        }
        return view;
    }

    private void initialise(View view)
    {
        // TextView
        tvTitleCarName = (TextView) view.findViewById(R.id.tvTitleCarName);
        tvCardetails = (TextView) view.findViewById(R.id.tvCardetails);
        tvVIN = (TextView) view.findViewById(R.id.tvVIN);
        tvVInStatus = (TextView) view.findViewById(R.id.tvVInStatus);
        tvYoungerCount = (TextView) view.findViewById(R.id.tvYoungerCount);
        tvSameYearCount = (TextView) view.findViewById(R.id.tvSameYearCount);
        tvOlderCount = (TextView) view.findViewById(R.id.tvOlderCount);
        tvDemandSummary = (TextView) view.findViewById(R.id.tvDemandSummary);
        tvAvailibility = (TextView) view.findViewById(R.id.tvAvailibility);
        tvAvgDays = (TextView) view.findViewById(R.id.tvAvgDays);
        tvLeadPool = (TextView) view.findViewById(R.id.tvLeadPool);
        tvWarrantyServices = (TextView) view.findViewById(R.id.tvWarrantyServices);
        tvReviewCount = (TextView) view.findViewById(R.id.tvReviewCount);
        tvOnlinePriceNow = (TextView) view.findViewById(R.id.tvOnlinePriceNow);
        tvSimplePriceTrade = (TextView) view.findViewById(R.id.tvSimplePriceTrade);
        tvSimplePriceRetail = (TextView) view.findViewById(R.id.tvSimplePriceRetail);
        tvIXTrader = (TextView) view.findViewById(R.id.tvIXTrader);
        tvPrivateadvert = (TextView) view.findViewById(R.id.tvPrivateadvert);
        llDoAppraisal = (TextView) view.findViewById(R.id.llDoAppraisal);
        llDoAppraisal.setOnClickListener(this);

       /* if (summeryDetails.getStr_Seller().equalsIgnoreCase("1"))
        {
            tvVInStatus.setText(Html.fromHtml("<font color=" + getActivity().getResources().getColor(R.color.green) + ">\u2713</font>"));
        } else
        {*/
        tvVInStatus.setText(Html.fromHtml("<font color=" + getActivity().getResources().getColor(R.color.red) + ">\u2718</font>"));
        //}

        // ListView
        lvYounger = (StaticListView) view.findViewById(R.id.lvYounger);
        lvsameYear = (StaticListView) view.findViewById(R.id.lvsameYear);
        lvOlder = (StaticListView) view.findViewById(R.id.lvOlder);

        // ImageView
        ivArrowIconExtraInfo = (ImageView) view.findViewById(R.id.ivArrowIconExtraInfo);
        ivYoungerArrow = (ImageView) view.findViewById(R.id.ivYoungerArrow);
        ivSameYearArrow = (ImageView) view.findViewById(R.id.ivSameYearArrow);
        ivOlderArrow = (ImageView) view.findViewById(R.id.ivOlderArrow);
        ivManualSectionArrow = (ImageView) view.findViewById(R.id.ivManualSectionArrow);
        ivArrowIcon = (ImageView) view.findViewById(R.id.ivArrowIcon);
        ivVehicleImage = (NetworkImageView) view.findViewById(R.id.ivVehicleImage);

        // Layout
        rlExtrainfo = (RelativeLayout) view.findViewById(R.id.rlExtrainfo);
        rlChangeVehicle = (RelativeLayout) view.findViewById(R.id.rlChangeVehicle);
        rlYoungerVehicle = (RelativeLayout) view.findViewById(R.id.rlYoungerVehicle);
        rlSameYear = (RelativeLayout) view.findViewById(R.id.rlSameYear);
        rlOlder = (RelativeLayout) view.findViewById(R.id.rlOlder);
        rlManualSection = (RelativeLayout) view.findViewById(R.id.rlManualSection);
        llayoutExtraInfo = (LinearLayout) view.findViewById(R.id.llayoutExtraInfo);
        llayoutModel = (LinearLayout) view.findViewById(R.id.llayoutModel);
        llNewPricePlotter = (LinearLayout) view.findViewById(R.id.llNewPricePlotter);

        llVINHistory = (LinearLayout) view.findViewById(R.id.llVINHistory);
        llayout_Average_days = (LinearLayout) view.findViewById(R.id.llayout_Average_days);
        llayoutDemand = (LinearLayout) view.findViewById(R.id.llayoutDemand);
        llayouSimilar = (LinearLayout) view.findViewById(R.id.llayouSimilar);
        llOEM = (LinearLayout) view.findViewById(R.id.llOEM);
        llAvailibility = (LinearLayout) view.findViewById(R.id.llAvailibility);
        llAvailibilityLine = (LinearLayout) view.findViewById(R.id.llAvailibilityLine);
        llPricing = (LinearLayout) view.findViewById(R.id.llPricing);
        llayoutSalesHistory = (LinearLayout) view.findViewById(R.id.llayoutSalesHistory);
        llayoutLeadPool = (LinearLayout) view.findViewById(R.id.llayoutLeadPool);
        llayout_verify_vin = (LinearLayout) view.findViewById(R.id.llayout_verify_vin);
        llayoutManualSection = (LinearLayout) view.findViewById(R.id.llayoutManualSection);

        llayoutModel.setVisibility(View.GONE);
        llReviews = (LinearLayout) view.findViewById(R.id.llReviews);
        ivVehicleImage.setOnClickListener(this);
        llOEM.setOnClickListener(this);
        llAvailibilityLine.setOnClickListener(this);
        llAvailibility.setOnClickListener(this);
        llPricing.setOnClickListener(this);

        rlExtrainfo.setOnClickListener(this);
        rlChangeVehicle.setOnClickListener(this);
        rlYoungerVehicle.setOnClickListener(this);
        rlSameYear.setOnClickListener(this);
        rlOlder.setOnClickListener(this);
        rlManualSection.setOnClickListener(this);
        llReviews.setOnClickListener(this);
        llVINHistory.setOnClickListener(this);
        llayout_Average_days.setOnClickListener(this);
        llayoutDemand.setOnClickListener(this);
        llayouSimilar.setOnClickListener(this);
        llayoutSalesHistory.setOnClickListener(this);
        llayoutLeadPool.setOnClickListener(this);
        llNewPricePlotter.setOnClickListener(this);
        llayout_verify_vin.setOnClickListener(this);
        edMinYear = (EditText) view.findViewById(R.id.edYear);
        edMake = (EditText) view.findViewById(R.id.edMake);
        edModel = (EditText) view.findViewById(R.id.edModel);
        edVariant = (EditText) view.findViewById(R.id.edVariant);
        btnClear = (Button) view.findViewById(R.id.btnClear);
        btnChangeVehicle = (Button) view.findViewById(R.id.btnChangeVehicle);
        btnScanVIn = (Button) view.findViewById(R.id.btnScanVIn);
        btnScanVIn.setOnClickListener(this);

        edMake.setOnClickListener(this);
        edModel.setOnClickListener(this);
        edVariant.setOnClickListener(this);
        edMinYear.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnChangeVehicle.setOnClickListener(this);

        // show the default year here
        edMinYear.setText("" + Calendar.getInstance().get(Calendar.YEAR));
        tvTUAPriceTrade = (TextView) view.findViewById(R.id.tvTUAPriceTrade);
        tvTUAPriceRetail = (TextView) view.findViewById(R.id.tvTUAPriceRetail);
        tvTUAPriceFetch = (TextView) view.findViewById(R.id.tvTUAPriceFetch);
        tvTUAPriceDate = (TextView) view.findViewById(R.id.tvTUAPriceDate);
        tvTUAPriceUpdate = (TextView) view.findViewById(R.id.tvTUAPriceUpdate);
        tvTUAPriceTrade.setVisibility(View.GONE);
        tvTUAPriceRetail.setVisibility(View.GONE);
        tvTUAPriceDate.setVisibility(View.GONE);
        tvTUAPriceUpdate.setVisibility(View.GONE);
        tvTUAPriceDate.setVisibility(View.GONE);
        tvTUAPriceFetch.setOnClickListener(this);
        tvTUAPriceUpdate.setOnClickListener(this);
        getChangeVehicleDataList();
        lvYounger.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (al_summaryAYearYounger.size() > 0)
                {
                    if (summeryDetails != null)
                    {
                        selectedVariantId = Integer.parseInt(al_summaryAYearYounger.get(position).getVariantID());
                        GetSynopsisXml(summeryDetails.getMakeId(), summeryDetails.getModelId(), Integer.parseInt(al_summaryAYearYounger.get(position).getVariantID()), Integer.parseInt(al_summaryAYearYounger.get(position).getYear()));

                    }
                }
            }
        });

        lvsameYear.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (al_summaryASameYear.size() > 0)
                {
                    selectedVariantId = Integer.parseInt(al_summaryASameYear.get(position).getVariantID());
                    GetSynopsisXml(summeryDetails.getMakeId(), summeryDetails.getModelId(), Integer.parseInt(al_summaryASameYear.get(position).getVariantID()), Integer.parseInt(al_summaryASameYear.get(position).getYear()));
                }
            }
        });

        lvOlder.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (al_summaryAYearOlder.size() > 0)
                {
                    selectedVariantId = Integer.parseInt(al_summaryAYearOlder.get(position).getVariantID());
                    GetSynopsisXml(summeryDetails.getMakeId(), summeryDetails.getModelId(), Integer.parseInt(al_summaryAYearOlder.get(position).getVariantID()), Integer.parseInt(al_summaryAYearOlder.get(position).getYear()));
                }
            }
        });

        ivArrowIconExtraInfo.setRotation(90);
    }

    private void putValues()
    {
        tvTitleCarName.setText(Html.fromHtml("<font color=#ffffff>" + summeryDetails.getYear() + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">"
                + summeryDetails.getFriendlyName() + "</font>"));

      /*  if (summeryDetails.getVin() != null && (summeryDetails.getVin().equals("No VIN loaded")))
            btnScanVIn.setVisibility(View.VISIBLE);
        else
            btnScanVIn.setVisibility(View.GONE);*/

        tvVIN.setText(summeryDetails.getVin());
        StringBuilder detailsBuilder = new StringBuilder();
        if (summeryDetails.getEngine_CC() != 0)
            detailsBuilder.append(summeryDetails.getEngine_CC() + "cc");
        if (summeryDetails.getPower_KW() != 0)
            detailsBuilder.append(", " + summeryDetails.getPower_KW() + "kW");
        if (summeryDetails.getTorque_NM() != 0)
            detailsBuilder.append(", " + summeryDetails.getTorque_NM() + "Nm");
        if (summeryDetails.getFuel_Type() != null && !summeryDetails.getFuel_Type().isEmpty())
            detailsBuilder.append(", " + summeryDetails.getFuel_Type() + "");
        if (summeryDetails.getTransmission_Type() != null && !summeryDetails.getTransmission_Type().isEmpty())
            detailsBuilder.append(", " + summeryDetails.getTransmission_Type());
        if (summeryDetails.getGearbox() != null)
            if (summeryDetails.getGearbox() != null && !summeryDetails.getGearbox().isEmpty() && !summeryDetails.getGearbox().equalsIgnoreCase("Gears?"))
                detailsBuilder.append(", " + summeryDetails.getGearbox());
        if (summeryDetails.getGears() != null)
            if (summeryDetails.getGears() != null && !summeryDetails.getGears().isEmpty() && !summeryDetails.getGears().equalsIgnoreCase("Gears?"))
                detailsBuilder.append(", " + summeryDetails.getGears() + " gears");
        if (summeryDetails.getOfferStart() != null && !summeryDetails.getOfferStart().isEmpty())
            if (TextUtils.isEmpty(summeryDetails.getOfferEnd()) || summeryDetails.getOfferStart() == null)
            {
                if (TextUtils.isEmpty(detailsBuilder))
                {
                    detailsBuilder.append("Avail. as new from " + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferStart()));
                } else
                {
                    detailsBuilder.append(". Avail. as new from " + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferStart()));
                }
            } else
            {
                if (TextUtils.isEmpty(detailsBuilder))
                {
                    detailsBuilder.append("Avail. as new from " + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferStart()) + " to "
                            + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferEnd()));
                } else
                {
                    detailsBuilder.append(". Avail. as new from " + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferStart()) + " to "
                            + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferEnd()));
                }
            }
        tvOnlinePriceNow.setText(summeryDetails.getOnline_price());
        tvSimplePriceTrade.setText(summeryDetails.getSimple_logic_trade());
        tvSimplePriceRetail.setText(summeryDetails.getSimple_logic_retail());
        tvIXTrader.setText(summeryDetails.getIx_trade());
        tvPrivateadvert.setText(summeryDetails.getPrivate_advert());
        tvTUAPriceRetail.setText(summeryDetails.getTUARetailPrice());
        tvTUAPriceTrade.setText(summeryDetails.getTUATradePrice());
        tvTUAPriceDate.setText("TUA price check : " + summeryDetails.getTUADate());
        tvCardetails.setText(detailsBuilder.toString());
        StringBuilder htmlBuilder = new StringBuilder();
        for (int i = 0; i < summeryDetails.getDemandSummaryList().size(); i++)
        {
            htmlBuilder.append("<b>" + summeryDetails.getDemandSummaryList().get(i).getName() + " : </b>" + summeryDetails.getDemandSummaryList().get(i).getId() + " | ");
        }
        if (summeryDetails.getDemandSummaryList().size() != 0)
        {
            String string = StringUtils.substringBeforeLast(htmlBuilder.toString(), "|");
            tvDemandSummary.setText(Html.fromHtml(string));
        } else
        {
            tvDemandSummary.setText("N/A");
        }
        StringBuilder availibilityBuilder = new StringBuilder();
        for (int i = 0; i < summeryDetails.getAverageAvailableSummaryList().size(); i++)
        {
            availibilityBuilder.append("<b>" + summeryDetails.getAverageAvailableSummaryList().get(i).getName() + " : </b>" + summeryDetails.getAverageAvailableSummaryList().get(i).getId() + " | ");
        }
        if (summeryDetails.getAverageAvailableSummaryList().size() != 0)
        {
            String string = StringUtils.substringBeforeLast(availibilityBuilder.toString(), "|");
            tvAvailibility.setText(Html.fromHtml(string));
        } else
        {
            tvAvailibility.setText("N/A");
        }
        StringBuilder daysBuilder = new StringBuilder();
        for (int i = 0; i < summeryDetails.getAverageDaysInStockSummaryList().size(); i++)
        {
            daysBuilder.append("<b>" + summeryDetails.getAverageDaysInStockSummaryList().get(i).getName() + " : </b>" + summeryDetails.getAverageDaysInStockSummaryList().get(i).getId() + " | ");
        }
        if (summeryDetails.getAverageDaysInStockSummaryList().size() != 0)
        {
            String string = StringUtils.substringBeforeLast(daysBuilder.toString(), "|");
            tvAvgDays.setText(Html.fromHtml(string));
        } else
        {
            tvAvgDays.setText("N/A");
        }
        StringBuilder leadBuilder = new StringBuilder();
        for (int i = 0; i < summeryDetails.getLeadPoolSummaryList().size(); i++)
        {
            leadBuilder.append("<b>" + summeryDetails.getLeadPoolSummaryList().get(i).getName() + " : </b>" + summeryDetails.getLeadPoolSummaryList().get(i).getId() + " | ");
        }
        if (summeryDetails.getLeadPoolSummaryList().size() != 0)
        {
            String string = StringUtils.substringBeforeLast(leadBuilder.toString(), "|");
            tvLeadPool.setText(Html.fromHtml(string));
        } else
        {
            tvLeadPool.setText("N/A");
        }

        StringBuilder warrantyBuilder = new StringBuilder();
        for (int i = 0; i < summeryDetails.getWarrantySummaryList().size(); i++)
        {
            warrantyBuilder.append("<b>" + summeryDetails.getWarrantySummaryList().get(i).getName().trim() + " : </b>" + summeryDetails.getWarrantySummaryList().get(i).getType() + "<br/>");
        }
        if (summeryDetails.getWarrantySummaryList().size() != 0)
        {
            String string = StringUtils.substringBeforeLast(warrantyBuilder.toString(), "<");
            tvWarrantyServices.setText(Html.fromHtml(string));
        } else
        {
            tvWarrantyServices.setText("N/A");
        }
        ivVehicleImage.setImageUrl(summeryDetails.getImageURL(), imageLoader);
        ivVehicleImage.setErrorImageResId(R.drawable.noimage);
        ivVehicleImage.setDefaultImageResId(R.drawable.noimage);
        tvReviewCount.setText("" + summeryDetails.getReviewCounts());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Permiso.getInstance().setActivity(getActivity());
        showActionBar("Summary");
    }

    @Override
    public void onClick(final View v)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("vehicleDetails", summeryDetails);
        switch (v.getId())
        {
            case R.id.tvTUAPriceFetch:
                tvTUAPriceFetch.setVisibility(View.GONE);
                tvTUAPriceTrade.setVisibility(View.VISIBLE);
                tvTUAPriceRetail.setVisibility(View.VISIBLE);
                tvTUAPriceDate.setVisibility(View.VISIBLE);
                tvTUAPriceUpdate.setVisibility(View.VISIBLE);
                break;

            case R.id.tvTUAPriceUpdate:
                CustomDialogManager.showOkCancelDialog(getActivity(), "Are you sure? You will be charged again.", "Yes", "No", new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        getUpdatedPrice();
                    }
                });
                break;

            case R.id.ivVehicleImage:
                if (vehicleImage == null)
                {
                    vehicleImage = new ArrayList<BaseImage>();
                    BaseImage baseImage = new BaseImage();
                    baseImage.setPath(summeryDetails.getImageURL());
                    baseImage.setLink(summeryDetails.getImageURL());
                    baseImage.setThumbPath(summeryDetails.getImageURL());
                    vehicleImage.add(baseImage);
                }
                navigateToLargeImage(0, vehicleImage);
                break;

            case R.id.llVINHistory:
                if (summeryDetails != null)
                {
                    if (summeryDetails.getVin() != null && (summeryDetails.getVin().equals("No VIN loaded")))
                    {
                        showDialogVIN(MY_REQUEST_CODE_VINHISTORY);
                    } else
                    {
                        VinHistoryFragment vinHistoryFragment = new VinHistoryFragment();
                        vinHistoryFragment.setArguments(bundle);
                        ft.replace(R.id.Container, vinHistoryFragment).addToBackStack(null);
                        ft.commit();
                    }
                }
                break;

            case R.id.llayout_Average_days:
                AverageDaysFragment averageDaysFragment = new AverageDaysFragment();
                averageDaysFragment.setArguments(bundle);
                ft.replace(R.id.Container, averageDaysFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.llayoutDemand:
                DemandFragment demandFragment = new DemandFragment();
                demandFragment.setArguments(bundle);
                ft.replace(R.id.Container, demandFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.llayoutSalesHistory:
                SalesHistoryFragment salesHistoryFragment = new SalesHistoryFragment();
                salesHistoryFragment.setArguments(bundle);
                ft.replace(R.id.Container, salesHistoryFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.llayoutLeadPool:
                LeadPoolFragment leadPoolFragment = new LeadPoolFragment();
                leadPoolFragment.setArguments(bundle);
                ft.replace(R.id.Container, leadPoolFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.llayout_verify_vin:
                VerifyVINFragment verifyVINFragment = new VerifyVINFragment();
                bundle.putBoolean("fromSum", true);
                VehicleDetails vehicleDetails = new VehicleDetails();
                vehicleDetails.setYear(summeryDetails.getYear());
                vehicleDetails.setFriendlyName(summeryDetails.getFriendlyName());
                vehicleDetails.setVin(tvVIN.getText().toString().trim());
                vehicleDetails.setRegistration("");
                vehicleDetails.setCondition(summeryDetails.getCondition() == null ? "Good" : summeryDetails.getCondition());
                vehicleDetails.setMileage(summeryDetails.getMileage());
                bundle.putParcelable("summaryObejct", vehicleDetails);
                verifyVINFragment.setArguments(bundle);
                ft.replace(R.id.Container, verifyVINFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.llDoAppraisal:
                if (summeryDetails != null)
                {
                    if (summeryDetails.getVin() != null && (summeryDetails.getVin().equals("No VIN loaded")))
                    {
                        showDialogVIN(MY_REQUEST_CODE_DO_APPRAISAL);
                    } else
                    {
                        doAppraisalFragment = new DoAppraisalFragment();
                        Bundle bundleDoAppraisal = new Bundle();
                        bundleDoAppraisal.putParcelable("summaryObejct", summeryDetails);
                        doAppraisalFragment.setArguments(bundleDoAppraisal);
                        ft.replace(R.id.Container, doAppraisalFragment).addToBackStack(null);
                        ft.commit();
                    }
                }
                break;

            case R.id.btnScanVIn:

                break;
            case R.id.llReviews:
                ReviewsFragment reviewsFragment = new ReviewsFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                reviewsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.Container, reviewsFragment).addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.llOEM:
                OemSpecsFragment oemSpecsFragment = new OemSpecsFragment();
                oemSpecsFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, oemSpecsFragment).addToBackStack(null).commit();
                break;

            case R.id.llAvailibility:
            case R.id.llAvailibilityLine:
                AvailabilityFragment availabilityFragment = new AvailabilityFragment();
                availabilityFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, availabilityFragment).addToBackStack(null).commit();
                break;

            case R.id.llPricing:
                PricingFragment pricingFragment = new PricingFragment();
                bundle.putInt("Type", 1);
                pricingFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, pricingFragment).addToBackStack(null).commit();
                break;

            case R.id.llNewPricePlotter:
                PricePlotterFragment pricepPlotterFragment = new PricePlotterFragment();
                pricepPlotterFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, pricepPlotterFragment).addToBackStack(null).commit();
                break;

            case R.id.rlExtrainfo:

                if (llayoutExtraInfo.getVisibility() != View.GONE)
                {
                    llayoutExtraInfo.setVisibility(View.GONE);
                    ivArrowIconExtraInfo.setRotation(0);
                } else
                {
                    llayoutExtraInfo.setVisibility(View.VISIBLE);
                    ivArrowIconExtraInfo.setRotation(90);
                }
                break;

            case R.id.rlChangeVehicle:
                if (llayoutModel.getVisibility() != View.GONE)
                {
                    llayoutModel.setVisibility(View.GONE);
                    ivArrowIcon.setRotation(0);
                } else
                {
                    llayoutModel.setVisibility(View.VISIBLE);
                    ivArrowIcon.setRotation(90);
                }
                break;

            case R.id.rlYoungerVehicle:
                if (lvYounger.getVisibility() != View.GONE)
                {
                    lvYounger.setVisibility(View.GONE);
                    ivYoungerArrow.setRotation(0);
                } else
                {
                    lvYounger.setVisibility(View.VISIBLE);
                    ivYoungerArrow.setRotation(90);
                    SetYoungerData();
                }
                break;

            case R.id.rlSameYear:
                if (lvsameYear.getVisibility() != View.GONE)
                {
                    lvsameYear.setVisibility(View.GONE);
                    ivSameYearArrow.setRotation(0);
                } else
                {
                    lvsameYear.setVisibility(View.VISIBLE);
                    ivSameYearArrow.setRotation(90);
                    SetSameYear();
                }
                break;

            case R.id.rlOlder:
                if (lvOlder.getVisibility() != View.GONE)
                {
                    lvOlder.setVisibility(View.GONE);
                    ivOlderArrow.setRotation(0);
                } else
                {
                    lvOlder.setVisibility(View.VISIBLE);
                    ivOlderArrow.setRotation(90);
                    SetYearOlder();
                }
                break;

            case R.id.rlManualSection:
                if (llayoutManualSection.getVisibility() != View.GONE)
                {
                    llayoutManualSection.setVisibility(View.GONE);
                    ivManualSectionArrow.setRotation(0);
                } else
                {
                    llayoutManualSection.setVisibility(View.VISIBLE);
                    ivManualSectionArrow.setRotation(90);
                }
                break;

            // Edittext of minimum year
            case R.id.edYear:
                showToPopUp(v);
                break;

            // Edittext of maximum year
            case R.id.maxYear:
                showToPopUp(v);
                break;

            // Edittext of make
            case R.id.edMake:
                if (makeList == null)
                {
                    if (HelperHttp.isNetworkAvailable(getActivity()))
                        getMakeList(true);
                    else
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                } else
                {
                    if (!makeList.isEmpty())
                        showListPopUp(v, makeList);
                }

                break;

            // Edittext of model
            case R.id.edModel:
                if (selectedMakeId == 0)
                {
                    return;
                }

                if (modelList != null)
                {
                    if (!modelList.isEmpty())
                        showListPopUp(v, modelList);
                } else
                {
                    if (HelperHttp.isNetworkAvailable(getActivity()))
                        getModelList();
                    else
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                }
                break;

            // Edittext of variant
            case R.id.edVariant:
                if (selectedMakeId == 0)
                {
                    return;
                }
                if (selectedModelId == 0)
                {
                    return;
                }
                if (variantList != null)
                {
                    if (!variantList.isEmpty())
                    {
                        NewVariantAdapter variantAdapter = new NewVariantAdapter(getActivity(), R.layout.list_item_variant, variantList);
                        Helper.showDropDown(v, variantAdapter, new OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                                edVariant.setText(variantList.get(position).getVariantName() + "");
                                selectedVariantId = variantList.get(position).getVariantId();
                                if (selectedVariantId != 0)
                                    variant = variantList.get(position);
                            }
                        });
                    }
                } else
                {
                    if (HelperHttp.isNetworkAvailable(getActivity()))
                        getVariantList();
                    else
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                }
                break;

            // Search button
            case R.id.btnChangeVehicle:
                selectedPageNumber = 0;
                if (TextUtils.isEmpty(edMake.getText().toString()) || edMake.getText().toString().equals("Select Make*"))
                {
                    Helper.showToast("Select make", getActivity());
                    return;
                }
                if (edModel.getText().toString().equals("") || edModel.getText().toString().equals("Select Model"))
                {
                    Helper.showToast(getString(R.string.select_model1), getActivity());
                    return;
                }
                if (edVariant.getText().toString().equals("") || edVariant.getText().toString().equals("Select Variant"))
                {
                    Helper.showToast(getString(R.string.select_varient1), getActivity());
                    return;
                }
                selectedPageNumber = 0;
                if (HelperHttp.isNetworkAvailable(getActivity()))
                    GetSynopsisXml(selectedMakeId, selectedModelId, selectedVariantId, Integer.parseInt(edMinYear.getText().toString().trim()));
                else
                    CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                break;

            case R.id.btnClear:
                makeList = null;
                modelList = null;
                variantList = null;
                // set default text
                edMinYear.setText("" + Calendar.getInstance().get(Calendar.YEAR));
                edMake.setText(R.string.select_make);
                edModel.setText(R.string.select_model);
                edVariant.setText(R.string.select_varient);
                selectedVariantId = 0;
                selectedModelId = 0;
                selectedMakeId = 0;
                variant = null;
                break;
        }
    }

    private void showDialogVIN(final int RequiestCode)
    {
        CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.scan_vin_note_summary), "Scan VIN", "Cancel", new DialogListener()
        {
            @Override
            public void onButtonClicked(int type)
            {
                switch (type)
                {
                    case Dialog.BUTTON_NEGATIVE:
                        // activity.this.finish();
                        break;
                    case Dialog.BUTTON_POSITIVE:
                        checkVINRequiest(RequiestCode);
                        break;
                }
            }
        });
    }

    private void checkVINRequiest(final int RequiestCode)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, 100);
                return;
            }
        }
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult()
        {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet)
            {
                if (resultSet.isPermissionGranted(Manifest.permission.CAMERA))
                {
                    scanVIN(RequiestCode);
                } else
                {
                    Helper.showToast("Please accept permission for scanning VIN", getActivity());
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions)
            {

            }
        }, Manifest.permission.CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (doAppraisalFragment != null)
        {
            doAppraisalFragment.onActivityResult(requestCode, resultCode, data);
        }

        if (resultCode == Pdf417ScanActivity.RESULT_OK)
        {
            // obtain scan results
            ArrayList<Pdf417MobiScanData> scanDataList = data.getParcelableArrayListExtra(Pdf417ScanActivity.EXTRAS_RESULT_LIST);
            // NOTE: if you are interested in only single scan result, you can obtain the first element of the array list
            //       or you can use the old key EXTRAS_RESULT
            // If you have set allowing of multiple scan results on single image to false (Pdf417MobiSettings.setAllowMultipleScanResultsOnSingleImage method)
            // scanDataList will contain at most one element.
            //Pdf417MobiScanData scanData = data.getParcelableExtra(Pdf417ScanActivity.EXTRAS_RESULT);
            String barcodeData = null;
            @SuppressWarnings("unused")
            String barcodeType = null;
            @SuppressWarnings("unused")
            BarcodeDetailedData rawData = null;
            for (Pdf417MobiScanData scanData : scanDataList)
            {
                // read scanned barcode type (PDF417 or QR code)
                barcodeType = scanData.getBarcodeType();
                // read the data contained in barcode
                barcodeData = scanData.getBarcodeData();
                // read raw barcode data
                rawData = scanData.getBarcodeRawData();
                // determine if returned scan data is certain
            }

            String[] dataIn = barcodeData.split("%");
            Helper.Log("orignal:", barcodeData);
            Helper.Log("data Length", "Size:" + dataIn.length);
            for (int i = 0; i < dataIn.length; i++)
            {
                Helper.Log("data" + i, dataIn[i]);
            }

            if (dataIn.length >= 14)
            {
                ScanVIN scanVIN = new ScanVIN();
                scanVIN.setId(0);
                scanVIN.setDate(dataIn[14]);
                scanVIN.setVIN(dataIn[12]);
                scanVIN.setRegistration(dataIn[6]);
                scanVIN.setShape(dataIn[8]);
                scanVIN.setMake(dataIn[9]);
                scanVIN.setModel(dataIn[10]);
                scanVIN.setColour(dataIn[11]);
                scanVIN.setEngineNumber(dataIn[13]);
                //    checkScannedVINJson(scanVIN);
                if (requestCode == MY_REQUEST_CODE_DO_APPRAISAL)
                {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    summeryDetails.setVin(dataIn[12]);
                    doAppraisalFragment = new DoAppraisalFragment();
                    Bundle bundleDoAppraisal = new Bundle();
                    bundleDoAppraisal.putParcelable("summaryObejct", summeryDetails);
                    doAppraisalFragment.setArguments(bundleDoAppraisal);
                    ft.replace(R.id.Container, doAppraisalFragment).addToBackStack(null);
                    ft.commit();
                } else if (requestCode == MY_REQUEST_CODE_VINHISTORY)
                {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    summeryDetails.setVin(dataIn[12]);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("vehicleDetails", summeryDetails);
                    VinHistoryFragment vinHistoryFragment = new VinHistoryFragment();
                    vinHistoryFragment.setArguments(bundle);
                    ft.replace(R.id.Container, vinHistoryFragment).addToBackStack(null);
                    ft.commit();
                }
            }
        }
    }

    private void navigateToLargeImage(int position, ArrayList<BaseImage> images)
    {
        try
        {
            Intent iToBuyActivity = new Intent(getActivity(), BuyActivity.class);
            iToBuyActivity.putParcelableArrayListExtra("imagelist", images);
            iToBuyActivity.putExtra("index", position);
            iToBuyActivity.putExtra("vehicleName", tvTitleCarName.getText().toString());
            startActivity(iToBuyActivity);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
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

        Helper.showDropDownYear(v, new ArrayAdapter<String>(getActivity(), R.layout.list_item_text3, R.id.tvText, years), new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ed.setText(years.get(position) + "");
                if (!lastYear.equals(years.get(position) + ""))
                {
                    edVariant.setText(R.string.select_varient); // set
                    // defaulttext
                    edModel.setText(R.string.select_model);
                    edMake.setText(R.string.select_make);
                    selectedVariantId = 0;
                    selectedModelId = 0;
                    selectedMakeId = 0;
                    makeList = null;
                    // getMakeList(false);
                }

            }
        });
    }

    /**
     * Displays list popup for make, model and variant Function gets general
     * arraylist as parameter, it can be makeList, modelList or variantList
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void showListPopUp(final View mView, final ArrayList list)
    {
        try
        {
            final EditText ed = (EditText) mView;
            final String edtData = ed.getText().toString().trim();

            boolean showSearch = false;
            if (mView.getId() == R.id.edMake)
                showSearch = true;
            else
                showSearch = false;

            Helper.showDropDownSearch(showSearch, mView, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, list), new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    SmartObject smartObject = (SmartObject) parent.getItemAtPosition(position);
                    ed.setText(smartObject.getName() + "");
                    if (edtData.equals(ed.getText().toString().trim()))
                        return;
                    if (mView.getId() == R.id.edMake)
                    {
                        // if make is clicked second
                        // time
                        if (modelList != null) // remove modelList and variantL
                            modelList = null;
                        if (variantList != null)
                            variantList = null;
                        edVariant.setText(R.string.select_varient);

                        edModel.setText(R.string.select_model);
                        selectedVariantId = 0;
                        selectedModelId = 0;
                        variant = null;
                        // set default text
                        selectedMakeId = smartObject.getId();

                    } else if (mView.getId() == R.id.edModel)
                    { // if model is clicked remove variant list items
                        if (variantList != null)
                            variantList = null;
                        edVariant.setText(R.string.select_varient);
                        selectedVariantId = 0;
                        variant = null;

                        selectedModelId = smartObject.getId();
                        /*
                         * if (HelperHttp.isNetworkAvailable(getActivity()))
						 * getVariantList(position); else
						 * CustomDialogManager.showOkDialog(getActivity(),
						 * getString(R.string.no_internet_connection));
						 */

                    } else if (mView.getId() == R.id.edVariant)
                    {
                        // if variant is
                        selectedVariantId = variantList.get(position).getVariantId();
                        if (selectedVariantId != 0)
                            variant = variantList.get(position);
                    }
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getUpdatedPrice()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            if (summeryDetails.getVin() != null && (!summeryDetails.getVin().equals("No VIN loaded")))
            {
                parameterList.add(new Parameter("vinNumber", summeryDetails.getVin(), String.class));
            } else
            {
                parameterList.add(new Parameter("vinNumber", "", String.class));
            }
            parameterList.add(new Parameter("manufactureYear", summeryDetails.getYear(), String.class));
            parameterList.add(new Parameter("registrationNumber", "", String.class));
            parameterList.add(new Parameter("MMCode", summeryDetails.getMmcode(), String.class));
            parameterList.add(new Parameter("mileage", "", String.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadTransUnionPricing");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadTransUnionPricing");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    hideProgressDialog();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject resultObj = (SoapObject) outer.getPropertySafely("Result");
                        SoapObject inner = (SoapObject) resultObj.getPropertySafely("TUAPricing");
                        summeryDetails.setTUARetailPrice(Helper.formatPrice(inner.getPropertySafelyAsString("TUARetailPrice", "0")));
                        summeryDetails.setTUATradePrice(Helper.formatPrice(inner.getPropertySafelyAsString("TUATradePrice", "0")));
                        summeryDetails.setTUADate(Helper.convertUTCDateToNormal(inner.getPropertySafelyAsString("SearchDateTime", "Date?")));
                        tvTUAPriceRetail.setText(Helper.formatPrice(inner.getPropertySafelyAsString("TUARetailPrice", "0")));
                        tvTUAPriceTrade.setText(Helper.formatPrice(inner.getPropertySafelyAsString("TUATradePrice", "0")));
                        tvTUAPriceDate.setText("TUA price check : " + Helper.convertUTCDateToNormal(resultObj.getPropertySafelyAsString("SearchDateTime", "0")));
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

    private void GetSynopsisXml(int makeId, int modelId, int VariantId, int year)
    {
        selectedPageNumber = 0;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", year, Integer.class));
            parameterList.add(new Parameter("makeId", makeId, Integer.class));
            parameterList.add(new Parameter("modelId", modelId, Integer.class));
            parameterList.add(new Parameter("variantId", VariantId, Integer.class));
            parameterList.add(new Parameter("VIN", "", String.class));
            parameterList.add(new Parameter("kilometers", "", String.class));
            parameterList.add(new Parameter("extras", "", String.class));
            parameterList.add(new Parameter("condition", "", String.class));
            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("GetSynopsisXml");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/GetSynopsisXml");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    summeryDetails = ParserManager.parsesSynopsisForVehicle(result);
                    hideProgressDialog();
                    if (summeryDetails != null)
                    {
                        putValues();
                        getChangeVehicleDataList();
                        collapsAllChange();
                    } else
                    {
                        CustomDialogManager.showOkDialog(getActivity(), "Error while loading data. Please try again later");
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getModelList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", Integer.parseInt(edMinYear.getText().toString().trim()), Integer.class));
            parameterList.add(new Parameter("makeID", selectedMakeId, Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListModelsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListModelsXML");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    hideProgressDialog();
                    modelList = new ArrayList<SmartObject>();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Models");
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject modelObj = (SoapObject) inner.getProperty(i);
                            String modelid = modelObj.getPropertySafelyAsString("modelID", "0");
                            String modelname = modelObj.getPropertySafelyAsString("modelName", "-");
                            modelList.add(i, new SmartObject(Integer.parseInt(modelid), modelname));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        if (!modelList.isEmpty())
                            showListPopUp(edModel, modelList);
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getVariantList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", Integer.parseInt(edMinYear.getText().toString().trim()), Integer.class));
            parameterList.add(new Parameter("modelID", selectedModelId, Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListVariantsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListVariantsXML");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    hideProgressDialog();
                    variantList = new ArrayList<Variant>();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Variants");
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject variantObj = (SoapObject) inner.getProperty(i);

                            variantList.add(new Variant(Integer.parseInt(variantObj.getPropertySafelyAsString("variantID", "0")), variantObj.getPropertySafelyAsString("meadCode", "0"), variantObj
                                    .getPropertySafelyAsString("friendlyName", "-"), variantObj.getPropertySafelyAsString("variantName", "-"), variantObj.getPropertySafelyAsString("MinDate", "-"),
                                    variantObj.getPropertySafelyAsString("MaxDate", "-")));

                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        if (!variantList.isEmpty())
                        {
                            NewVariantAdapter variantAdapter = new NewVariantAdapter(getActivity(), R.layout.list_item_variant, variantList);
                            Helper.showDropDown(edVariant, variantAdapter, new OnItemClickListener()
                            {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                                {
                                    edVariant.setText(variantList.get(position).getVariantName() + "");
                                    selectedVariantId = variantList.get(position).getVariantId();
                                    if (selectedVariantId != 0)
                                        variant = variantList.get(position);
                                }
                            });
                        }
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getMakeList(final boolean show)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", Integer.parseInt(edMinYear.getText().toString()), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListMakesXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListMakesXML");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    hideProgressDialog();
                    makeList = new ArrayList<SmartObject>();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Makes");
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject makeObj = (SoapObject) inner.getProperty(i);
                            String makeid = makeObj.getPropertySafelyAsString("makeID", "0");
                            String makename = makeObj.getPropertySafelyAsString("makeName", "-");

                            makeList.add(i, new SmartObject(Integer.parseInt(makeid), makename));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        if (show)
                        {
                            if (!makeList.isEmpty())
                                showListPopUp(edMake, makeList);
                            else
                                Helper.showToast(getString(R.string.no_make), getActivity());
                        }
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getChangeVehicleDataList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("variantID", selectedVariantId, Integer.class));
            parameterList.add(new Parameter("year", summeryDetails.getYear(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadSimilarVehiclesByID");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadSimilarVehiclesByID");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    //  hideProgressDialog();
                    al_summaryAYearYounger = new ArrayList<>();
                    al_summaryASameYear = new ArrayList<>();
                    al_summaryAYearOlder = new ArrayList<>();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("SimilarVehicles");
                        SoapObject YearYoungerVehicles = (SoapObject) inner.getPropertySafely("YearYoungerVehicles");
                        SoapObject SameYearVehicles = (SoapObject) inner.getPropertySafely("SameYearVehicles");
                        SoapObject YearOlderVehicles = (SoapObject) inner.getPropertySafely("YearOlderVehicles");
                        for (int i = 0; i < YearYoungerVehicles.getPropertyCount(); i++)
                        {
                            SoapObject makeObj = (SoapObject) YearYoungerVehicles.getProperty(i);
                            String VariantID = makeObj.getPropertySafelyAsString("VariantID", "");
                            String Year = makeObj.getPropertySafelyAsString("Year", "");
                            String VariantName = makeObj.getPropertySafelyAsString("VariantName", "");
                            String Transmission = makeObj.getPropertySafelyAsString("Transmission", "");
                            String FuelType = makeObj.getPropertySafelyAsString("FuelType", "");
                            al_summaryAYearYounger.add(new YearYounger(VariantID, Year, VariantName, Transmission, FuelType));
                        }
                        for (int i = 0; i < SameYearVehicles.getPropertyCount(); i++)
                        {
                            SoapObject makeObj = (SoapObject) SameYearVehicles.getProperty(i);
                            String VariantID = makeObj.getPropertySafelyAsString("VariantID", "");
                            String Year = makeObj.getPropertySafelyAsString("Year", "");
                            String VariantName = makeObj.getPropertySafelyAsString("VariantName", "");
                            String Transmission = makeObj.getPropertySafelyAsString("Transmission", "");
                            String FuelType = makeObj.getPropertySafelyAsString("FuelType", "");
                            al_summaryASameYear.add(new YearYounger(VariantID, Year, VariantName, Transmission, FuelType));
                        }
                        for (int i = 0; i < YearOlderVehicles.getPropertyCount(); i++)
                        {
                            SoapObject makeObj = (SoapObject) YearOlderVehicles.getProperty(i);
                            String VariantID = makeObj.getPropertySafelyAsString("VariantID", "");
                            String Year = makeObj.getPropertySafelyAsString("Year", "");
                            String VariantName = makeObj.getPropertySafelyAsString("VariantName", "");
                            String Transmission = makeObj.getPropertySafelyAsString("Transmission", "");
                            String FuelType = makeObj.getPropertySafelyAsString("FuelType", "");
                            al_summaryAYearOlder.add(new YearYounger(VariantID, Year, VariantName, Transmission, FuelType));
                        }

                        tvYoungerCount.setText("" + al_summaryAYearYounger.size());
                        tvSameYearCount.setText("" + al_summaryASameYear.size());
                        tvOlderCount.setText("" + al_summaryAYearOlder.size());

                        if (summeryDetails.getVin() != null && (summeryDetails.getVin().equals("No VIN loaded")))
                            getVinVerification();
                        else
                            hideProgressDialog();

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

    private void getVinVerification()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("vinNumber", summeryDetails.getVin(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("IsVinVerified");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/IsVinVerified");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            //  showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    hideProgressDialog();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Result");
                        String Verified = inner.getPropertySafelyAsString("Verified", "");
                        if (Verified.contains("false"))
                        {
                            tvVInStatus.setText(Html.fromHtml("<font color=" + getActivity().getResources().getColor(R.color.red) + ">\u2718</font>"));
                        } else
                        {
                            tvVInStatus.setText(Html.fromHtml("<font color=" + getActivity().getResources().getColor(R.color.green) + ">\u2713</font>"));
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

    private void SetYoungerData()
    {
        summaryAYearOlderAdapter = new AYearOlderAdapter(getActivity(), R.layout.list_item_a_year_older, al_summaryAYearYounger);
        lvYounger.setAdapter(summaryAYearOlderAdapter);
    }

    private void SetSameYear()
    {
        summaryAYearOlderAdapter = new AYearOlderAdapter(getActivity(), R.layout.list_item_a_year_older, al_summaryASameYear);
        lvsameYear.setAdapter(summaryAYearOlderAdapter);
    }

    private void SetYearOlder()
    {
        summaryAYearOlderAdapter = new AYearOlderAdapter(getActivity(), R.layout.list_item_a_year_older, al_summaryAYearOlder);
        lvOlder.setAdapter(summaryAYearOlderAdapter);
    }

    private void collapsAllChange()
    {
        if (llayoutExtraInfo.getVisibility() != View.GONE)
        {
            llayoutExtraInfo.setVisibility(View.GONE);
            ivArrowIconExtraInfo.setRotation(0);
        }

        if (llayoutModel.getVisibility() != View.GONE)
        {
            llayoutModel.setVisibility(View.GONE);
            ivArrowIcon.setRotation(0);
        }

        if (lvYounger.getVisibility() != View.GONE)
        {
            lvYounger.setVisibility(View.GONE);
            ivYoungerArrow.setRotation(0);
        }

        if (lvsameYear.getVisibility() != View.GONE)
        {
            lvsameYear.setVisibility(View.GONE);
            ivSameYearArrow.setRotation(0);
        }

        if (lvOlder.getVisibility() != View.GONE)
        {
            lvOlder.setVisibility(View.GONE);
            ivOlderArrow.setRotation(0);
        }

        if (llayoutManualSection.getVisibility() != View.GONE)
        {
            llayoutManualSection.setVisibility(View.GONE);
            ivManualSectionArrow.setRotation(0);
        }
    }

    public void scanVIN(int REQUIESTCODE)
    {
        Helper.Log(TAG, "scan will be performed");
        // Intent for ScanActivity
        Intent intent = new Intent(getActivity(), Pdf417ScanActivity.class);

        // If you want sound to be played after the scanning process ends,
        // put here the resource ID of your sound file. (optional)
        intent.putExtra(Pdf417ScanActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);

        // set EXTRAS_ALWAYS_USE_HIGH_RES to true if you want to always use highest
        // possible camera resolution (enabled by default for all devices that support
        // at least 720p camera preview frame size)
        //		intent.putExtra(Pdf417ScanActivity.EXTRAS_ALWAYS_USE_HIGH_RES, true);

        // set the license key (for commercial versions only) - obtain your key at
        // http://pdf417.mobi
        intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, Constants.SCAN_LICENSE_KEY); // demo license key for package mobi.pdf417.demo

        intent.putExtra(Pdf417ScanActivity.EXTRAS_ALWAYS_USE_HIGH_RES, true);

        //intent.putExtra(Pdf417ScanActivity.EXTRAS_OPTIMIZE_CAMERA_FOR_NEAR_SCANNING, true);

        // If you want to open front facing camera, uncomment the following line.
        // Note that front facing cameras do not have autofocus support, so it will not
        // be possible to scan denser and smaller codes.
        //		intent.putExtra(Pdf417ScanActivity.EXTRAS_CAMERA_TYPE, (Parcelable)CameraType.CAMERA_FRONTFACE);

        // You can use Pdf417MobiSettings object to tweak additional scanning parameters.
        // This is entirely optional. If you don't send this object via intent, default
        // scanning parameters will be used - this means both QR and PDF417 codes will
        // be scanned and default camera overlay will be shown.

        Pdf417MobiSettings sett = new Pdf417MobiSettings();

        // set this to true to enable PDF417 scanning
        sett.setPdf417Enabled(true);
        // Set this to true to scan even barcode not compliant with standards
        // For example, malformed PDF417 barcodes which were incorrectly encoded
        // Use only if necessary because it slows down the recognition process
        //		sett.setUncertainScanning(true);
        // Set this to true to scan barcodes which don't have quiet zone (white area) around it
        // Use only if necessary because it drastically slows down the recognition process
        sett.setNullQuietZoneAllowed(true);
        // Set this to true to enable parsing of data from US Driver's License barcodes
        // This feature is available only if license key permits it.
        sett.setDecodeUSDriverLicenseData(true);
        // set this to true to enable QR code scanning
        sett.setQrCodeEnabled(true);
        // set this to true to prevent showing dialog after successful scan
        sett.setDontShowDialog(true);
        // if license permits this, remove Pdf417.mobi logo overlay on scan activity
        // if license forbids this, this option has no effect
        sett.setRemoveOverlayEnabled(true);
        // set this to false if you want to receive at most one scan result
//	        sett.setAllowMultipleScanResultsOnSingleImage(false);
        // put settings as intent extra
        intent.putExtra(Pdf417ScanActivity.EXTRAS_SETTINGS, sett);
        // Start Activity
        startActivityForResult(intent, REQUIESTCODE);
    }
}
