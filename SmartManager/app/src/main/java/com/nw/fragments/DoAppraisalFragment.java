package com.nw.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nw.interfaces.DialogListener;
import com.nw.model.BaseImage;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.PurchaseDetail;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class DoAppraisalFragment extends BaseFragement implements OnClickListener
{
    TextView tvSellerStatus, tvPurchaseDetailsStatus, tvTitleCarName, tvCardetails, tvConditionStatus, tvVehicleExtrasStatus, tvInteriorStatus, tvEngineStatus, tvExteriorStatus, tvValuationStatus,
            tvAppraisalDate;
    Resources resources;
    RelativeLayout rlSeller, rlPurchaseDetails, rlConditions, rlInterior, rlEngine, rlExterior, rlSendOffer, rlVehicleExtras, rlValuation, rlForwardAppraisal;
    NetworkImageView ivVehicleImage;

    Exterior_ReconditioningSelectedItemsFragment exterior_ReconditioningSelectedItemsFragment;
    ImageLoader imageLoader;
    SellerFragment sellerFragment;

    VehicleDetails summeryDetails;

    ArrayList<BaseImage> vehicleImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_doappraisal, container, false);
        setHasOptionsMenu(true);
        if (resources == null)
        {
            resources = getActivity().getResources();
        }
        init(view);
        return view;
    }

    private void init(View view)
    {
        imageLoader = VolleySingleton.getInstance().getImageLoader();

        // TextView
        tvTitleCarName = (TextView) view.findViewById(R.id.tvTitleCarName);
        tvCardetails = (TextView) view.findViewById(R.id.tvCardetails);
        tvConditionStatus = (TextView) view.findViewById(R.id.tvConditionStatus);
        tvSellerStatus = (TextView) view.findViewById(R.id.tvSellerStatus);
        tvPurchaseDetailsStatus = (TextView) view.findViewById(R.id.tvPurchaseDetailsStatus);
        tvVehicleExtrasStatus = (TextView) view.findViewById(R.id.tvVehicleExtrasStatus);
        tvInteriorStatus = (TextView) view.findViewById(R.id.tvInteriorStatus);
        tvEngineStatus = (TextView) view.findViewById(R.id.tvEngineStatus);
        tvExteriorStatus = (TextView) view.findViewById(R.id.tvExteriorStatus);
        tvValuationStatus = (TextView) view.findViewById(R.id.tvValuationStatus);
        tvAppraisalDate = (TextView) view.findViewById(R.id.tvAppraisalDate);


        rlSeller = (RelativeLayout) view.findViewById(R.id.rlSeller);
        rlSeller.setOnClickListener(this);

        rlPurchaseDetails = (RelativeLayout) view.findViewById(R.id.rlPurchaseDetails);
        rlPurchaseDetails.setOnClickListener(this);

        rlExterior = (RelativeLayout) view.findViewById(R.id.rlExterior);
        rlExterior.setOnClickListener(this);

        rlConditions = (RelativeLayout) view.findViewById(R.id.rlConditions);
        rlConditions.setOnClickListener(this);

        rlInterior = (RelativeLayout) view.findViewById(R.id.rlInterior);
        rlInterior.setOnClickListener(this);

        rlEngine = (RelativeLayout) view.findViewById(R.id.rlEngine);
        rlEngine.setOnClickListener(this);

        rlSendOffer = (RelativeLayout) view.findViewById(R.id.rlSendOffer);
        rlSendOffer.setOnClickListener(this);

        rlVehicleExtras = (RelativeLayout) view.findViewById(R.id.rlVehicleExtras);
        rlVehicleExtras.setOnClickListener(this);

        rlValuation = (RelativeLayout) view.findViewById(R.id.rlValuation);
        rlValuation.setOnClickListener(this);

        rlForwardAppraisal = (RelativeLayout) view.findViewById(R.id.rlForwardAppraisal);
        rlForwardAppraisal.setOnClickListener(this);

        ivVehicleImage = (NetworkImageView) view.findViewById(R.id.ivVehicleImage);
        ivVehicleImage.setOnClickListener(this);

        setPreviousVehicledata();
    }

    private void setPreviousVehicledata()
    {
        if (getArguments() != null)
        {
            summeryDetails = getArguments().getParcelable("summaryObejct");

            ivVehicleImage.setImageUrl(summeryDetails.getImageURL(), imageLoader);
            ivVehicleImage.setErrorImageResId(R.drawable.noimage);
            ivVehicleImage.setDefaultImageResId(R.drawable.noimage);

            tvTitleCarName.setText(Html.fromHtml("<font color=#ffffff>" + summeryDetails.getYear() + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">"
                    + summeryDetails.getFriendlyName() + "</font>"));

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

            tvCardetails.setText(detailsBuilder.toString());
            getDoAppraisalData();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Do Appraisal");
        //getActivity().getActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                hideKeyboard();
                getActivity().getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable("vehicleDetails", summeryDetails);
        switch (v.getId())
        {

            case R.id.rlSeller:
                sellerFragment = new SellerFragment();
                sellerFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, sellerFragment).addToBackStack(null).commit();
                break;

            case R.id.rlPurchaseDetails:
                PurchaseDetailsFragment purchaseDetailsFragment = new PurchaseDetailsFragment();
                purchaseDetailsFragment.setArguments(bundle);
                ft.replace(R.id.Container, purchaseDetailsFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.rlConditions:
                ConditionsFragment conditionsFragment = new ConditionsFragment();
                conditionsFragment.setArguments(bundle);
                ft.replace(R.id.Container, conditionsFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.rlExterior:
                exterior_ReconditioningSelectedItemsFragment = new Exterior_ReconditioningSelectedItemsFragment();
                exterior_ReconditioningSelectedItemsFragment.setArguments(bundle);
                ft.replace(R.id.Container, exterior_ReconditioningSelectedItemsFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.rlInterior:
                InteriorReconditioningFragment reconditioningFragment = new InteriorReconditioningFragment();
                reconditioningFragment.setArguments(bundle);
                ft.replace(R.id.Container, reconditioningFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.rlEngine:
                EngineDriveTrainFragment engineDriveTrainFragment = new EngineDriveTrainFragment();
                engineDriveTrainFragment.setArguments(bundle);
                ft.replace(R.id.Container, engineDriveTrainFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.rlValuation:
                PricingFragment pricingFragment = new PricingFragment();
                bundle.putInt("Type", 2);
                pricingFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, pricingFragment).addToBackStack(null).commit();
                break;

            case R.id.rlSendOffer:
                SendOfferFragment sendOfferFragment = new SendOfferFragment();
                sendOfferFragment.setArguments(bundle);
                ft.replace(R.id.Container, sendOfferFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.rlVehicleExtras:
                VehicleExtrasFragment vehicleExtrasFragment = new VehicleExtrasFragment();
                vehicleExtrasFragment.setArguments(bundle);
                ft.replace(R.id.Container, vehicleExtrasFragment).addToBackStack(null);
                ft.commit();
                break;

            case R.id.rlForwardAppraisal:
                ForwardAppraisalFragment forwardAppraisalFragment = new ForwardAppraisalFragment();
                forwardAppraisalFragment.setArguments(bundle);
                ft.replace(R.id.Container, forwardAppraisalFragment).addToBackStack(null);
                ft.commit();
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (exterior_ReconditioningSelectedItemsFragment != null)
            exterior_ReconditioningSelectedItemsFragment.onActivityResult(requestCode, resultCode, data);
        if (sellerFragment != null)
            sellerFragment.onActivityResult(requestCode, resultCode, data);
    }

    private void getDoAppraisalData()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("UserHash", DataManager.getInstance().user.getUserHash(), String.class));
            if (!summeryDetails.getVin().equals("No VIN loaded"))
            {
                parameterList.add(new Parameter("VinNumber", summeryDetails.getVin(), String.class));
            } else
            {
                parameterList.add(new Parameter("VinNumber", "", String.class));
            }
            //   parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("GetAppraisalInfo");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/GetAppraisalInfo");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("soap response", result.toString());
                        hideProgressDialog();
                        Helper.Log("Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject response = (SoapObject) outer.getPropertySafely("Appraisal");
                        if (summeryDetails == null)
                            summeryDetails = new VehicleDetails();

                        summeryDetails.setStr_AppraisalId(response.getPropertySafelyAsString("AppraisalId", "0"));
                        summeryDetails.setStr_Seller(response.getPropertySafelyAsString("PhotosSaved", ""));
                        summeryDetails.setStr_purchaseDetails(response.getPropertySafelyAsString("PurchaseDetailsSaved", ""));
                        summeryDetails.setStr_Condition(response.getPropertySafelyAsString("OverallconditionName", ""));
                        summeryDetails.setStr_VahicleExtra(Helper.formatPrice(response.getPropertySafelyAsString("VehicleExtraTotalPrice", "0")));
                        summeryDetails.setStr_InteriorReconditioning(Helper.formatPrice(response.getPropertySafelyAsString("InteriorReconditioningTotalPrice", "0")));
                        summeryDetails.setStr_EngineDrivetrain(Helper.formatPrice(response.getPropertySafelyAsString("EngineDrivetrainTotalPrice", "0")));
                        summeryDetails.setStr_ExteriorReconditioning(Helper.formatPrice(response.getPropertySafelyAsString("VehicleExtraTotalPrice", "0")));
                        summeryDetails.setStr_CreateDate(response.getPropertySafelyAsString("CreateDate", "0"));

                        SoapObject responseValuation = (SoapObject) response.getPropertySafely("Valuation");
                        summeryDetails.setStr_ValuationStartRang(Helper.formatPrice(responseValuation.getPropertySafelyAsString("ValuationStartPrice", "0")));
                        summeryDetails.setGetStr_ValuationEndRang(Helper.formatPrice(responseValuation.getPropertySafelyAsString("ValuationEndPrice", "0")));

                        setDoAppraisalWebseviceData();

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                //getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                    }
                }

            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void setDoAppraisalWebseviceData()
    {
        if (summeryDetails.getStr_Seller().equalsIgnoreCase("1"))
        {
            tvSellerStatus.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
        } else
        {
            tvSellerStatus.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
        }

        if (summeryDetails.getStr_purchaseDetails().equalsIgnoreCase("1"))
        {
            tvPurchaseDetailsStatus.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.green) + ">\u2713</font>"));
        } else
        {
            tvPurchaseDetailsStatus.setText(Html.fromHtml("<font color=" + resources.getColor(R.color.red) + ">\u2718</font>"));
        }

        tvConditionStatus.setText(summeryDetails.getStr_Condition());
        tvVehicleExtrasStatus.setText(summeryDetails.getStr_VahicleExtra());
        tvInteriorStatus.setText(summeryDetails.getStr_InteriorReconditioning());
        tvEngineStatus.setText(summeryDetails.getStr_EngineDrivetrain());
        tvExteriorStatus.setText(summeryDetails.getStr_ExteriorReconditioning());
        tvValuationStatus.setText(summeryDetails.getStr_ValuationStartRang()+" & "+summeryDetails.getGetStr_ValuationEndRang());
        tvAppraisalDate.setText("Date: "+Helper.showDateWithDay(summeryDetails.getStr_CreateDate()));

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

}
