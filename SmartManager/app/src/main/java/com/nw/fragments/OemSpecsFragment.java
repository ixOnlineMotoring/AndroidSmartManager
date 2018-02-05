package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nw.adapters.OmeSpecsAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.ArrayList;
import java.util.HashMap;

public class OemSpecsFragment extends BaseFragement implements OnClickListener
{
    RelativeLayout rlDisclaimer, rlEngineAndGearbox, rlDimensions, rlFeatures, rlSuspensions_and_Drivetrain,
            rlSafetyAndSecurity, rlPlansValid;
    LinearLayout llDisclaimer, llEngineAndGearbox, llDimensions, llFeatures, llSuspensions_and_Drivetrain,
            llSafetyAndSecurity, llPlansValid;
    StaticListView lvDisclaimer, lvEngineAndGearbox, lvDimensions, lvFeatures, lvSuspensions_and_Drivetrain,
            lvSafetyAndSecurity, lvPlansValid;
    ImageView ivArrowIconDisclaimer, ivArrowIconEngineAndGearbox, ivArrowIconDimensions, ivArrowIconFeatures,
            ivArrowIconSuspensions_and_Drivetrain, ivArrowIconSafetyAndSecurity, ivArrowIconPlansValid;
    ArrayList<SmartObject> specsList;
    OmeSpecsAdapter omeSpecsAdapter;
    SmartObject smartObject;
    TextView tvDisclaimer;
    HashMap<String, ArrayList<SmartObject>> specsdata;
    VehicleDetails vehicleDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_oem_specs, container, false);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            vehicleDetails = getArguments().getParcelable("vehicleDetails");
        }
        initialise(view);
        return view;
    }

    private void initialise(View view)
    {
        // RelativeLayout
        rlDisclaimer = (RelativeLayout) view.findViewById(R.id.rlDisclaimer);
        rlEngineAndGearbox = (RelativeLayout) view.findViewById(R.id.rlEngineAndGearbox);
        rlDimensions = (RelativeLayout) view.findViewById(R.id.rlDimensions);
        rlFeatures = (RelativeLayout) view.findViewById(R.id.rlFeatures);
        rlSuspensions_and_Drivetrain = (RelativeLayout) view.findViewById(R.id.rlSuspensions_and_Drivetrain);
        rlSafetyAndSecurity = (RelativeLayout) view.findViewById(R.id.rlSafetyAndSecurity);
        rlPlansValid = (RelativeLayout) view.findViewById(R.id.rlPlansValid);
        // LinearLayout
        llDisclaimer = (LinearLayout) view.findViewById(R.id.llDisclaimer);
        llEngineAndGearbox = (LinearLayout) view.findViewById(R.id.llEngineAndGearbox);
        llDimensions = (LinearLayout) view.findViewById(R.id.llDimensions);
        llFeatures = (LinearLayout) view.findViewById(R.id.llFeatures);
        llSuspensions_and_Drivetrain = (LinearLayout) view.findViewById(R.id.llSuspensions_and_Drivetrain);
        llSafetyAndSecurity = (LinearLayout) view.findViewById(R.id.llSafetyAndSecurity);
        llPlansValid = (LinearLayout) view.findViewById(R.id.llPlansValid);
        // ListView
        tvDisclaimer = (TextView) view.findViewById(R.id.tvDisclaimer);
        lvEngineAndGearbox = (StaticListView) view.findViewById(R.id.lvEngineAndGearbox);
        lvDimensions = (StaticListView) view.findViewById(R.id.lvDimensions);
        lvFeatures = (StaticListView) view.findViewById(R.id.lvFeatures);
        lvSuspensions_and_Drivetrain = (StaticListView) view.findViewById(R.id.lvSuspensions_and_Drivetrain);
        lvSafetyAndSecurity = (StaticListView) view.findViewById(R.id.lvSafetyAndSecurity);
        lvPlansValid = (StaticListView) view.findViewById(R.id.lvPlansValid);
        // ImageViews
        ivArrowIconDisclaimer = (ImageView) view.findViewById(R.id.ivArrowIconDisclaimer);
        ivArrowIconEngineAndGearbox = (ImageView) view.findViewById(R.id.ivArrowIconEngineAndGearbox);
        ivArrowIconDimensions = (ImageView) view.findViewById(R.id.ivArrowIconDimensions);
        ivArrowIconFeatures = (ImageView) view.findViewById(R.id.ivArrowIconFeatures);
        ivArrowIconSuspensions_and_Drivetrain = (ImageView) view.findViewById(R.id.ivArrowIconSuspensions_and_Drivetrain);
        ivArrowIconSafetyAndSecurity = (ImageView) view.findViewById(R.id.ivArrowIconSafetyAndSecurity);
        ivArrowIconPlansValid = (ImageView) view.findViewById(R.id.ivArrowIconPlansValid);
        rlDisclaimer.setOnClickListener(this);
        rlEngineAndGearbox.setOnClickListener(this);
        rlDimensions.setOnClickListener(this);
        rlFeatures.setOnClickListener(this);
        rlSuspensions_and_Drivetrain.setOnClickListener(this);
        rlSafetyAndSecurity.setOnClickListener(this);
        rlPlansValid.setOnClickListener(this);

        getDataForOEM();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("OEM Specs");
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.rlDisclaimer:
                if (llDisclaimer.getVisibility() == View.GONE)
                {
                    ivArrowIconDisclaimer.setRotation(90);
                    llDisclaimer.setVisibility(View.VISIBLE);
                } else
                {
                    llDisclaimer.setVisibility(View.GONE);
                    ivArrowIconDisclaimer.setRotation(0);
                }

                break;

            case R.id.rlEngineAndGearbox:
                if (llEngineAndGearbox.getVisibility() == View.GONE)
                {
                    ivArrowIconEngineAndGearbox.setRotation(90);
                    llEngineAndGearbox.setVisibility(View.VISIBLE);
                    omeSpecsAdapter = new OmeSpecsAdapter(getActivity(), R.layout.list_item_oem_space, specsdata.get("Engine & Gearbox"));
                    lvEngineAndGearbox.setAdapter(omeSpecsAdapter);
                } else
                {
                    llEngineAndGearbox.setVisibility(View.GONE);
                    ivArrowIconEngineAndGearbox.setRotation(0);
                }
                break;

            case R.id.rlDimensions:
                if (llDimensions.getVisibility() == View.GONE)
                {
                    ivArrowIconDimensions.setRotation(90);
                    llDimensions.setVisibility(View.VISIBLE);
                    omeSpecsAdapter = new OmeSpecsAdapter(getActivity(), R.layout.list_item_oem_space, specsdata.get("Dimensions"));
                    lvDimensions.setAdapter(omeSpecsAdapter);
                } else
                {
                    llDimensions.setVisibility(View.GONE);
                    ivArrowIconDimensions.setRotation(0);
                }
                break;

            case R.id.rlFeatures:
                if (llFeatures.getVisibility() == View.GONE)
                {
                    ivArrowIconFeatures.setRotation(90);
                    llFeatures.setVisibility(View.VISIBLE);
                    omeSpecsAdapter = new OmeSpecsAdapter(getActivity(), R.layout.list_item_oem_space, specsdata.get("Features"));
                    lvFeatures.setAdapter(omeSpecsAdapter);
                } else
                {
                    llFeatures.setVisibility(View.GONE);
                    ivArrowIconFeatures.setRotation(0);
                }
                break;

            case R.id.rlSuspensions_and_Drivetrain:
                if (llSuspensions_and_Drivetrain.getVisibility() == View.GONE)
                {
                    ivArrowIconSuspensions_and_Drivetrain.setRotation(90);
                    llSuspensions_and_Drivetrain.setVisibility(View.VISIBLE);
                    omeSpecsAdapter = new OmeSpecsAdapter(getActivity(), R.layout.list_item_oem_space, specsdata.get("Suspension & Drivetrain"));
                    lvSuspensions_and_Drivetrain.setAdapter(omeSpecsAdapter);
                } else
                {
                    llSuspensions_and_Drivetrain.setVisibility(View.GONE);
                    ivArrowIconSuspensions_and_Drivetrain.setRotation(0);
                }
                break;

            case R.id.rlSafetyAndSecurity:
                if (llSafetyAndSecurity.getVisibility() == View.GONE)
                {
                    ivArrowIconSafetyAndSecurity.setRotation(90);
                    llSafetyAndSecurity.setVisibility(View.VISIBLE);
                    omeSpecsAdapter = new OmeSpecsAdapter(getActivity(), R.layout.list_item_oem_space, specsdata.get("Safety & Security"));
                    lvSafetyAndSecurity.setAdapter(omeSpecsAdapter);
                } else
                {
                    llSafetyAndSecurity.setVisibility(View.GONE);
                    ivArrowIconSafetyAndSecurity.setRotation(0);
                }
                break;

            case R.id.rlPlansValid:
                if (llPlansValid.getVisibility() == View.GONE)
                {
                    ivArrowIconPlansValid.setRotation(90);
                    llPlansValid.setVisibility(View.VISIBLE);
                    omeSpecsAdapter = new OmeSpecsAdapter(getActivity(), R.layout.list_item_oem_space, specsdata.get("Plan from 1st Year of Registration"));
                    lvPlansValid.setAdapter(omeSpecsAdapter);
                } else
                {
                    llPlansValid.setVisibility(View.GONE);
                    ivArrowIconPlansValid.setRotation(0);
                }
                break;
        }
    }

    private void getDataForOEM()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("variantID", vehicleDetails.getVariantID(), Integer.class));
            parameterList.add(new Parameter("year", vehicleDetails.getYear(), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadOEMSpecsByID");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadOEMSpecsByID");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject specsObj = (SoapObject) outer.getPropertySafely("Specs");
                        String disclaimer = specsObj.getPropertySafelyAsString("Disclaimer");
                        tvDisclaimer.setText(disclaimer);
                        SoapObject inner = (SoapObject) specsObj.getPropertySafely("Details");
                        specsdata = new HashMap<>();
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            specsList = new ArrayList<SmartObject>();
                            SoapObject catObject = (SoapObject) inner.getProperty(i);
                            SmartObject smartObject;
                            for (int j = 0; j < catObject.getPropertyCount(); j++)
                            {
                                smartObject = new SmartObject();
                                smartObject.setName(((SoapPrimitive) catObject.getProperty(j)).getAttribute(0).toString());
                                smartObject.setType(((SoapPrimitive) catObject.getProperty(j)).getValue().toString());
                                specsList.add(smartObject);
                            }
                            specsdata.put(((SoapObject) inner.getProperty(i)).getAttribute(0).toString(), specsList);
                        }
                        hideProgressDialog();

                    } catch (Exception e)
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_getting_data));
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }
}
