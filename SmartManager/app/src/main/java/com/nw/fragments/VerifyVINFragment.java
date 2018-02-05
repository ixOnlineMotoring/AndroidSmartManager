package com.nw.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.FragmentTransaction;
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

import com.nw.adapters.FinanceHistoryAdapter;
import com.nw.adapters.MileageRegNoHistoryAdapter;
import com.nw.adapters.NewVariantAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.FullVerification;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.Variant;
import com.nw.model.Vehicle;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class VerifyVINFragment extends BaseFragement implements OnClickListener {
	TextView tvTitleCarName,tvPRManufacturer,tvPRModelVariant,tvPRVINChasis,tvPREngineNo,tvPRRegNo,tvPRColour,tvPRYear;
	EditText edVerifyVin,edRegistration;
	StaticListView slvFullVerification;
	RelativeLayout  rlVIN_Verification, rlFull_Verification;
	LinearLayout llayoutVin_verification,llayout_full_verification;
	ImageView ivArrowIcon, ivArrowIcon_VIN_Verification,ivArrowIcon_Full_Verification;
	Button btnVerifyVIN, btnFullVerification,btnContinue;
	Variant variant;
	SummaryFragment summaryFragment;
	VehicleDetails details;
    HashMap<String,ArrayList<FullVerification>> verificationData = new HashMap<String,ArrayList<FullVerification>>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            details = getArguments().getParcelable("summaryObejct");
        }
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_verify_vin, container,false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}

	private void initialise(View view) {
		btnFullVerification = (Button) view.findViewById(R.id.btnFullVerification);
		btnVerifyVIN = (Button) view.findViewById(R.id.btnVerifyVIN);
        btnContinue = (Button) view.findViewById(R.id.btnContinue);
	//	btnFullVerification.setOnClickListener(this);
		btnVerifyVIN.setOnClickListener(this);
		tvTitleCarName = (TextView) view.findViewById(R.id.tvTitleCarName);
        tvPRManufacturer= (TextView) view.findViewById(R.id.tvPRManufacturer);
        tvPRModelVariant= (TextView) view.findViewById(R.id.tvPRModelVariant);
        tvPRVINChasis= (TextView) view.findViewById(R.id.tvPRVINChasis);
        tvPREngineNo= (TextView) view.findViewById(R.id.tvPREngineNo);
        tvPRRegNo= (TextView) view.findViewById(R.id.tvPRRegNo);
        tvPRColour= (TextView) view.findViewById(R.id.tvPRColour);
        tvPRYear= (TextView) view.findViewById(R.id.tvPRYear);
		edVerifyVin = (EditText) view.findViewById(R.id.ed_verify_Vin);
		edRegistration = (EditText) view.findViewById(R.id.edRegistration);
		ivArrowIcon = (ImageView) view.findViewById(R.id.ivArrowIcon);
		ivArrowIcon_VIN_Verification = (ImageView) view.findViewById(R.id.ivArrowIcon_VIN_Verification);
		ivArrowIcon_Full_Verification = (ImageView) view.findViewById(R.id.ivArrowIcon_Full_Verification);
        slvFullVerification = (StaticListView) view.findViewById(R.id.lv_finance_history);

		llayoutVin_verification = (LinearLayout) view.findViewById(R.id.llayoutVin_verification);
		llayout_full_verification = (LinearLayout) view.findViewById(R.id.llayout_full_verification);
        rlVIN_Verification = (RelativeLayout) view.findViewById(R.id.rlVIN_Verification);
        rlFull_Verification = (RelativeLayout) view.findViewById(R.id.rlFull_Verification);
		// Finance History
		rlVIN_Verification.setOnClickListener(this);
		rlFull_Verification.setOnClickListener(this);
		btnContinue.setOnClickListener(this);
		putValues();

//        rlVIN_Verification.setVisibility(View.VISIBLE);
//        rlFull_Verification.setVisibility(View.VISIBLE);
	}

    private void putValues() {
        if(getArguments()!=null&&getArguments().containsKey("fromSum")&&getArguments().getBoolean("fromSum"))
        {
            btnContinue.setVisibility(View.GONE);
        }
        if(details.getVin()!=null)
        {
            edVerifyVin.setText(details.getVin());
        }
        if (details.getRegistration()!=null){
            edRegistration.setText(details.getRegistration());
        }
       if (details.getFriendlyName()!=null && details.getYear()!=0){
		   tvTitleCarName.setText(Html.fromHtml("<font color=#ffffff>" + details.getYear() + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">"
				   + details.getFriendlyName() + "</font>"));
	   }else {
		   tvTitleCarName.setText(Html.fromHtml("<font color=#ffffff>Year?</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">Make?Model?</font>"));
	   }
    }

    @Override
	public void onResume() {
		super.onResume();
		showActionBar("Verify VIN");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnFullVerification:
                if (edVerifyVin.getText().toString().equals(""))
                {
                    Helper.showToast(getString(R.string.vin_warning), getActivity());
                    return;
                }
                CustomDialogManager.showOkCancelDialog(getActivity(), "Are you sure? you will be charged again.", "Yes", "No", new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        switch (type){
                            case Dialog.BUTTON_POSITIVE:
                                fullVINVerfication();
                                break;
                        }
                    }
                });
				break;

			case R.id.btnVerifyVIN:
                if (edVerifyVin.getText().toString().equals(""))
                {
                    Helper.showToast(getString(R.string.vin_warning), getActivity());
                    return;
                }
                CustomDialogManager.showOkCancelDialog(getActivity(), "Are you sure? you will be charged again.", "Yes", "No", new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        switch (type){
                            case Dialog.BUTTON_POSITIVE:
                                verifyVIN();
                                break;
                        }
                    }
                });
				break;

			case R.id.rlVIN_Verification:
				
				if (llayoutVin_verification.getVisibility() != View.GONE)
				{
					llayoutVin_verification.setVisibility(View.GONE);
					ivArrowIcon_VIN_Verification.setRotation(0);
				}
				else
				{
					llayoutVin_verification.setVisibility(View.VISIBLE);
					ivArrowIcon_VIN_Verification.setRotation(90);
				}
				break;

			case R.id.rlFull_Verification:
				
				if (llayout_full_verification.getVisibility() != View.GONE)
				{
					llayout_full_verification.setVisibility(View.GONE);
					ivArrowIcon_Full_Verification.setRotation(0);
				}
				else
				{
					llayout_full_verification.setVisibility(View.VISIBLE);
					ivArrowIcon_Full_Verification.setRotation(90);
				}
				break;

			case R.id.btnContinue:
				GetSynopsisData();
				break;
		}
	}

	private void GetSynopsisData()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			if(getArguments()==null)
			{
				CustomDialogManager.showOkDialog(getActivity(), "No record(s) found");
				return;
			}
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", Integer.parseInt(getArguments().getString("year")), Integer.class));
			if(getArguments().getBoolean("fromVehicleCode"))
			{
				parameterList.add(new Parameter("mmcode",getArguments().getString("mmcode"), Integer.class));
			}else
			{
				parameterList.add(new Parameter("makeId", getArguments().getString("make"), Integer.class));
				parameterList.add(new Parameter("modelId", getArguments().getString("model"), Integer.class));
				parameterList.add(new Parameter("variantId", getArguments().getString("variant"), Integer.class));
			}
			parameterList.add(new Parameter("VIN", getArguments().getString("vin"), String.class));
			parameterList.add(new Parameter("kilometers",getArguments().getString("kilometer"), Integer.class));
			parameterList.add(new Parameter("extras",getArguments().getString("extras"), String.class)); 
			parameterList.add(new Parameter("condition",getArguments().getString("condition"), String.class));
			DataInObject inObj = new DataInObject();
			
			// create web service inputs
			if(getArguments().getBoolean("fromVehicleCode"))
			{
				inObj.setMethodname("GetSynopsisByMMCodeXml");
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
				inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/GetSynopsisByMMCodeXml");
			}
			else
			{
				inObj.setMethodname("GetSynopsisXml");
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
				inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/GetSynopsisXml");
			}
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
					VehicleDetails details = ParserManager.parsesSynopsisForVehicle(result);
					hideProgressDialog();
					if (details!=null)
					{
						summaryFragment = new SummaryFragment();
						Bundle bundle = new Bundle();
						bundle.putParcelable("summaryObejct", details);
						summaryFragment.setArguments(bundle);
						getFragmentManager().beginTransaction().replace(R.id.Container, summaryFragment).addToBackStack(null).commit();
					}else {
						CustomDialogManager.showOkDialog(getActivity(), "Error while loading data");
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

    private void verifyVIN(){
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("vinNumber", (details.getVin()== null?edVerifyVin.getText().toString():details.getVin()), String.class));
            parameterList.add(new Parameter("manufactureYear", details.getYear(), Integer.class));
            parameterList.add(new Parameter("registrationNumber",edRegistration.getText().toString().trim(), String.class));
            parameterList.add(new Parameter("MMCode",details.getMmcode(), Integer.class));
            parameterList.add(new Parameter("mileage",details.getMileage(), Integer.class));

            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadTransUnionVINVerification");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadTransUnionVINVerification");
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
                    try{
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("VINVerification");
                        SoapObject vehicleConfirmationObj = (SoapObject) inner .getPropertySafely("VehicleConfirmationModel");
                        if (vehicleConfirmationObj.getPrimitivePropertySafelyAsString("ResultCode").equals("MSG020")){
                            CustomDialogManager.showOkDialog(getActivity(), vehicleConfirmationObj.getPrimitivePropertySafelyAsString("ResultCodeDescription"), new DialogListener() {
                                @Override
                                public void onButtonClicked(int type) {
                                    getFragmentManager().popBackStack();
                                }
                            });
                            return;
                        }
                        tvPRColour.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchColour"));
                        tvPREngineNo.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchEngineNumber"));
                        tvPRManufacturer.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchManufacturer"));
                        tvPRModelVariant.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchModel"));
                        tvPRRegNo.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchVehicleRegistration"));
                        tvPRVINChasis.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchVinorChassis"));
                        tvPRYear.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchYear"));
						if(rlVIN_Verification.getVisibility()==View.GONE)
						{
							rlVIN_Verification.setVisibility(View.VISIBLE);
						}
                    }catch (Exception e){
                        e.printStackTrace();
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), "Error while loading data. Please try again later");
                    }
                }
            }).execute();
        }
        else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

	private void fullVINVerfication()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("vinNumber", (details.getVin()== null?edVerifyVin.getText().toString():details.getVin()), String.class));
			parameterList.add(new Parameter("manufactureYear", details.getYear(), Integer.class));
			parameterList.add(new Parameter("registrationNumber",edRegistration.getText().toString().trim(), String.class));
			parameterList.add(new Parameter("MMCode",details.getMmcode(), Integer.class));
			parameterList.add(new Parameter("mileage",details.getMileage(), Integer.class));

			DataInObject inObj = new DataInObject();
			inObj.setMethodname("LoadTransUnionFullVerification");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadTransUnionFullVerification");
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
					try{
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("FullVerification");
						SoapObject accidentHistoryObj = (SoapObject) inner .getPropertySafely("AccidentHistory");
                        ArrayList<FullVerification>  dataItems= new ArrayList<FullVerification>();
                        verificationData.put("AccidentHistory",dataItems);
                        for (int i = 0; i < accidentHistoryObj.getPropertyCount(); i++) {
                            SoapObject soapObject = (SoapObject) accidentHistoryObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setResultCodeDescription(soapObject.getPropertySafelyAsString("ResultCodeDescription"));
                            verificationData.get("AccidentHistory").add(fullVerification);
                        }
                        ArrayList<FullVerification>  alerts= new ArrayList<FullVerification>();
                        verificationData.put("Alert",alerts);
                        SoapObject alertObj = (SoapObject) inner .getPropertySafely("Alert");
                        for (int i = 0; i < alertObj.getPropertyCount(); i++) {
                            SoapObject soapObject = (SoapObject) alertObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setResultCodeDescription(soapObject.getPropertySafelyAsString("ResultCodeDescription"));
                            verificationData.get("Alert").add(fullVerification);
                        }
                        ArrayList<FullVerification>  EnquiriesHistories= new ArrayList<FullVerification>();
                        verificationData.put("EnquiriesHistory",EnquiriesHistories);
                        SoapObject enquiriesHistoryObj = (SoapObject) inner .getPropertySafely("EnquiriesHistory");
                        for (int i = 0; i < enquiriesHistoryObj.getPropertyCount(); i++) {
                            SoapObject soapObject = (SoapObject) enquiriesHistoryObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setSource(soapObject.getPropertySafelyAsString("Source"));
                            fullVerification.setTransactionDate(soapObject.getPropertySafelyAsString("TransactionDate"));
                            verificationData.get("EnquiriesHistory").add(fullVerification);
                        }
                        ArrayList<FullVerification>  FactoryFittedExtras= new ArrayList<FullVerification>();
                        verificationData.put("FactoryFittedExtra",FactoryFittedExtras);
                        SoapObject factoryFittedExtraObj = (SoapObject) inner .getPropertySafely("FactoryFittedExtra");
                        for (int i = 0; i < factoryFittedExtraObj.getPropertyCount(); i++) {
                            SoapObject soapObject = (SoapObject) factoryFittedExtraObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setResultCodeDescription(soapObject.getPropertySafelyAsString("ResultCodeDescription"));
                            verificationData.get("FactoryFittedExtra").add(fullVerification);
                        }
                        ArrayList<FullVerification>  FinanceHistories= new ArrayList<FullVerification>();
                        verificationData.put("FinanceHistory",FinanceHistories);
                        SoapObject financeHistoryObj = (SoapObject) inner .getPropertySafely("FinanceHistory");
                        for (int i = 0; i < financeHistoryObj.getPropertyCount(); i++) {
                            SoapObject soapObject = (SoapObject) financeHistoryObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setAgreementOrAccountNumber(soapObject.getPropertySafelyAsString("AgreementOrAccountNumber"));
                            fullVerification.setAgreementType(soapObject.getPropertySafelyAsString("AgreementType"));
                            fullVerification.setEndDate(soapObject.getPropertySafelyAsString("EndDate"));
                            fullVerification.setFinanceHouse(soapObject.getPropertySafelyAsString("FinanceHouse"));
                            fullVerification.setStartDate(soapObject.getPropertySafelyAsString("StartDate"));
                            verificationData.get("FinanceHistory").add(fullVerification);
                        }
                        ArrayList<FullVerification>  finances= new ArrayList<FullVerification>();
                        verificationData.put("Finance",finances);
                        SoapObject financeObj = (SoapObject) inner .getPropertySafely("Finance");
                        for (int i = 0; i < financeObj.getPropertyCount(); i++)
                        {
                            SoapObject soapObject = (SoapObject) financeObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setAgreementOrAccountNumber(soapObject.getPropertySafelyAsString("AgreementOrAccountNumber"));
                            fullVerification.setAgreementType(soapObject.getPropertySafelyAsString("AgreementType"));
                            fullVerification.setFinanceBranch(soapObject.getPropertySafelyAsString("FinanceBranch"));
                            fullVerification.setFinanceProvider(soapObject.getPropertySafelyAsString("FinanceProvider"));
                            fullVerification.setTelephoneNumber(soapObject.getPropertySafelyAsString("TelNumber"));
                            verificationData.get("Finance").add(fullVerification);
                        }
                        ArrayList<FullVerification>  IVIDHistories= new ArrayList<FullVerification>();
                        verificationData.put("IVIDHistory",IVIDHistories);
                        SoapObject IVIDHistoryObj = (SoapObject) inner .getPropertySafely("IVIDHistory");
                        for (int i = 0; i < IVIDHistoryObj.getPropertyCount(); i++) {
                            SoapObject soapObject = (SoapObject) IVIDHistoryObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setResultCodeDescription(soapObject.getPropertySafelyAsString("ResultCodeDescription"));
                            verificationData.get("IVIDHistory").add(fullVerification);
                        }
                        ArrayList<FullVerification>  Microdots= new ArrayList<FullVerification>();
                        verificationData.put("Microdot",Microdots);
                        SoapObject MicrodotObj = (SoapObject) inner .getPropertySafely("Microdot");
                        for (int i = 0; i < MicrodotObj.getPropertyCount(); i++) {
                            SoapObject soapObject = (SoapObject) MicrodotObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setCompany(soapObject.getPropertySafelyAsString("Company"));
                            fullVerification.setContactNumber(soapObject.getPropertySafelyAsString("ContactNumber"));
                            fullVerification.setDateApplied(soapObject.getPropertySafelyAsString("DateApplied"));
                            fullVerification.setReferenceNumber(soapObject.getPropertySafelyAsString("ReferenceNumber"));
                            verificationData.get("Microdot").add(fullVerification);
                        }
                        ArrayList<FullVerification>  MileageHistories= new ArrayList<FullVerification>();
                        verificationData.put("MileageHistory",MileageHistories);
                        SoapObject MileageHistoryObj = (SoapObject) inner .getPropertySafely("MileageHistory");
                        for (int i = 0; i < MileageHistoryObj.getPropertyCount(); i++) {
                            SoapObject soapObject = (SoapObject) MileageHistoryObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setResultCodeDescription(soapObject.getPropertySafelyAsString("ResultCodeDescription"));
                            verificationData.get("MileageHistory").add(fullVerification);
                        }
                        ArrayList<FullVerification>  RegistrationHistories= new ArrayList<FullVerification>();
                        verificationData.put("RegistrationHistory",RegistrationHistories);
                        SoapObject RegistrationHistoryObj = (SoapObject) inner .getPropertySafely("RegistrationHistory");
                        for (int i = 0; i < RegistrationHistoryObj.getPropertyCount(); i++) {
                            SoapObject soapObject = (SoapObject) RegistrationHistoryObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setResultCodeDescription(soapObject.getPropertySafelyAsString("ResultCodeDescription"));
                            verificationData.get("RegistrationHistory").add(fullVerification);
                        }
                        ArrayList<FullVerification>  Stolens= new ArrayList<FullVerification>();
                        verificationData.put("Stolen",Stolens);
                        SoapObject StolenObj = (SoapObject) inner .getPropertySafely("Stolen");
                        for (int i = 0; i < StolenObj.getPropertyCount(); i++) {
                            SoapObject soapObject = (SoapObject) StolenObj.getProperty(i);
                            FullVerification fullVerification = new FullVerification();
                            fullVerification.setResultCodeDescription(soapObject.getPropertySafelyAsString("ResultCodeDescription"));
                            verificationData.get("Stolen").add(fullVerification);
                        }
                        if(rlFull_Verification.getVisibility()==View.GONE)
                        {
                            rlFull_Verification.setVisibility(View.VISIBLE);
                        }

                        SoapObject vehicleConfirmationObj = (SoapObject) inner .getPropertySafely("VehicleConfirmationModel");
                        if (vehicleConfirmationObj.getPrimitivePropertySafelyAsString("ResultCode").equals("MSG020")){
                            CustomDialogManager.showOkDialog(getActivity(),vehicleConfirmationObj.getPrimitivePropertySafelyAsString("ResultCodeDescription") );
                            return;
                        }
						tvPRColour.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchColour"));
						tvPREngineNo.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchEngineNumber"));
						tvPRManufacturer.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchManufacturer"));
						tvPRModelVariant.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchModel"));
						tvPRRegNo.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchVehicleRegistration"));
						tvPRVINChasis.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchVinorChassis"));
						tvPRYear.setText(vehicleConfirmationObj.getPrimitivePropertySafelyAsString("MatchYear"));
						if(rlVIN_Verification.getVisibility()==View.GONE)
						{
							rlVIN_Verification.setVisibility(View.VISIBLE);
						}
					}catch (Exception e){
						e.printStackTrace();
						hideProgressDialog();
						CustomDialogManager.showOkDialog(getActivity(), "Error while loading data. Please try again later");
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(summaryFragment != null){
            summaryFragment.onActivityResult(requestCode, resultCode, data);
        }
    }


}