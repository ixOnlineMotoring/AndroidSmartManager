package com.nw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentTransaction;
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

import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class VehicleCodeFragment extends BaseFragement implements OnClickListener
{
	EditText edYear,edMMCode,edIXCode,edVIN,edKilometers,edtCondition,edExtras;
	CustomButton btnSynopsisSummary;
	String[] conditionType = { "Excellent", "Very Good", "Good","Poor","Very Poor"};
	SummaryFragment summaryFragment;
	
	Button btnVINVerify;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_code, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		btnSynopsisSummary = (CustomButton) view.findViewById(R.id.btnSynopsisSummary);
		btnSynopsisSummary.setOnClickListener(this);
		btnSynopsisSummary.setTransformationMethod(null);
		edYear = (EditText) view.findViewById(R.id.edYear);
		edMMCode = (EditText) view.findViewById(R.id.edMMCode);
		edKilometers = (EditText) view.findViewById(R.id.edKilometers);
		edVIN = (EditText) view.findViewById(R.id.edVIN);
		edExtras = (EditText) view.findViewById(R.id.edExtras);
		edIXCode = (EditText) view.findViewById(R.id.edIXCode);
		edtCondition = (EditText) view.findViewById(R.id.edtCondition);
		edYear.setOnClickListener(this);
		edtCondition.setOnClickListener(this);
		edYear.setText("" + Calendar.getInstance().get(Calendar.YEAR));
		btnVINVerify=(Button) view.findViewById(R.id.btnVINVerify);
		btnVINVerify.setOnClickListener(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Vehicle Code");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btnVINVerify:
			if(TextUtils.isEmpty(edMMCode.getText().toString()))
			{
				Helper.showToast("Please enter M&M code", getActivity());
				return;
			}
			if (edVIN.getText().toString().equals(""))
			{
				Helper.showToast(getString(R.string.vin_warning), getActivity());
				return;
			}
            if (edKilometers.getText().toString().equals(""))
            {
                Helper.showToast(getString(R.string.kilometers_warning), getActivity());
                return;
            }
			if(!TextUtils.isEmpty(edMMCode.getText().toString()))
			{
				VerifyVINFragment verifyVINFragment=new VerifyVINFragment();
				Bundle bundle=new Bundle();
				bundle.putBoolean("fromSum", false);
                VehicleDetails vehicleDetails = new VehicleDetails();
                vehicleDetails.setYear(Integer.parseInt(edYear.getText().toString().trim()));
				vehicleDetails.setMmcode(edMMCode.getText().toString().trim());
				vehicleDetails.setVin(edVIN.getText().toString().trim());
				vehicleDetails.setExtras(edExtras.getText().toString().trim());
				vehicleDetails.setCondition(edtCondition.getText().toString().trim());
				vehicleDetails.setMileage(TextUtils.isEmpty(edKilometers.getText().toString().trim())==true?0:Integer.parseInt(edKilometers.getText().toString().trim()));
				bundle.putParcelable("summaryObejct",vehicleDetails);
				verifyVINFragment.setArguments(bundle);
				 getFragmentManager().beginTransaction().replace(R.id.Container, verifyVINFragment).addToBackStack(null).commit();
			}
			break;

		case R.id.btnSynopsisSummary:
			
			if(TextUtils.isEmpty(edMMCode.getText().toString()))
			{
				Helper.showToast("Please enter M&M code", getActivity());
				return;
			}
			if (edKilometers.getText().toString().equals(""))
			{
				Helper.showToast(getString(R.string.kilometers_warning), getActivity());
				return;
			}
			
				if(!TextUtils.isEmpty(edMMCode.getText().toString()))
			{
				GetSynopsisByMMCodeXml();
			}
			break;
			
		case R.id.edYear:
			showToPopUp(v);
			break;
			
		case R.id.edtCondition:
				Helper.showDropDown(edtCondition, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, conditionType), new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						edtCondition.setText(conditionType[position]);
					}
				});
			break;	
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
		Helper.showDropDownYear(v, new ArrayAdapter<String>(getActivity(),R.layout.list_item_text3, R.id.tvText, years), new AdapterView.OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				ed.setText(years.get(position) + "");
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(summaryFragment != null){
			summaryFragment.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	private void GetSynopsisByMMCodeXml() 
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", Integer.parseInt(edYear.getText().toString()), Integer.class));
			parameterList.add(new Parameter("mmcode", edMMCode.getText().toString().trim(), Integer.class));
			parameterList.add(new Parameter("VIN", edVIN.getText().toString().trim(), String.class));
			parameterList.add(new Parameter("kilometers",edKilometers.getText().toString().trim(), Integer.class));
			parameterList.add(new Parameter("extras",edExtras.getText().toString().trim(), String.class)); 
			parameterList.add(new Parameter("condition",edtCondition.getText().toString().trim(), String.class));
			
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetSynopsisByMMCodeXml");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/GetSynopsisByMMCodeXml");
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
	
	private void GetSynopsisByIxCodeXml() 
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("year", Integer.parseInt(edYear.getText().toString()), Integer.class));
			parameterList.add(new Parameter("ixCode", edIXCode.getText().toString(), Integer.class));
			parameterList.add(new Parameter("VIN", "", String.class));
			parameterList.add(new Parameter("kilometers","", Integer.class));
			parameterList.add(new Parameter("extras","", String.class)); 
			parameterList.add(new Parameter("condition","", String.class));
			
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetSynopsisByIxCode");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/GetSynopsisByIxCode");
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

}
