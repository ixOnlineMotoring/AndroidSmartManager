package com.nw.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nw.adapters.SimilarAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SettingsUser;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.HelperHttp;

import java.util.ArrayList;

public class SimilarFragment extends BaseFragement implements OnClickListener
{
	RelativeLayout rlAYearYounger, rlmodelOfSameYear, rlAYearOlder;
	LinearLayout llAYearYounger, llModelOfSameYear, llAYearOlder;
	ImageView ivArrowIconAYearYounger, ivArrowIconModelOfSameYear, ivArrowIconAYearOlder;
	StaticListView lvAYearYounger, lvModelOfSameYear, lvAYearOlder;
	TextView tvTitleCarName;
	VehicleDetails vehicleDetails;

	ArrayList<SettingsUser> arraylistAYearYounger, arraylistmodelOfSameYear, arraylistAYearOlder;
	SimilarAdapter similarAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_similar_vehicles, container, false);
		setHasOptionsMenu(true);
		if(getArguments()!=null)
		{
			vehicleDetails = getArguments().getParcelable("vehicleDetails");
		}
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		// RelativeLayout
		rlAYearYounger = (RelativeLayout) view.findViewById(R.id.rlAYearYounger);
		rlmodelOfSameYear = (RelativeLayout) view.findViewById(R.id.rlmodelOfSameYear);
		rlAYearOlder = (RelativeLayout) view.findViewById(R.id.rlAYearOlder);

		// LinearLayout
		llAYearYounger = (LinearLayout) view.findViewById(R.id.llAYearYounger);
		llModelOfSameYear = (LinearLayout) view.findViewById(R.id.llModelOfSameYear);
		llAYearOlder = (LinearLayout) view.findViewById(R.id.llAYearOlder);

		// ImageView
		ivArrowIconAYearYounger = (ImageView) view.findViewById(R.id.ivArrowIconAYearYounger);
		ivArrowIconModelOfSameYear = (ImageView) view.findViewById(R.id.ivArrowIconModelOfSameYear);
		ivArrowIconAYearOlder = (ImageView) view.findViewById(R.id.ivArrowIconAYearOlder);

		// ListView
		lvAYearYounger = (StaticListView) view.findViewById(R.id.lvAYearYounger);
		lvModelOfSameYear = (StaticListView) view.findViewById(R.id.lvModelOfSameYear);
		lvAYearOlder = (StaticListView) view.findViewById(R.id.lvAYearOlder);

		// Onclick
		rlAYearYounger.setOnClickListener(this);
		rlmodelOfSameYear.setOnClickListener(this);
		rlAYearOlder.setOnClickListener(this);

		tvTitleCarName = (TextView) view.findViewById(R.id.tvTitleCarName);
		tvTitleCarName.setText(Html.fromHtml("<font color=#ffffff>" + "2010" + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">"
				+ "Volkswagen Polo Hatch 1.4 Comfortline" + "</font>"));
		getSimilarVehicles();
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
	public void onResume()
	{
		super.onResume();
		showActionBar("Similar Vehicles");
		//getActivity().getActionBar().setSubtitle(null);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rlAYearYounger:
				if (llAYearYounger.getVisibility() == View.GONE)
				{
					ivArrowIconAYearYounger.setRotation(90);
					llAYearYounger.setVisibility(View.VISIBLE);
					lvAYearYounger.setAdapter(similarAdapter);
				}
				else
				{
					llAYearYounger.setVisibility(View.GONE);
					ivArrowIconAYearYounger.setRotation(0);
				}
				break;
			case R.id.rlmodelOfSameYear:
				if (llModelOfSameYear.getVisibility() == View.GONE)
				{
					ivArrowIconModelOfSameYear.setRotation(90);
					llModelOfSameYear.setVisibility(View.VISIBLE);
					lvModelOfSameYear.setAdapter(similarAdapter);
				}
				else
				{
					llModelOfSameYear.setVisibility(View.GONE);
					ivArrowIconModelOfSameYear.setRotation(0);
				}
				break;
			case R.id.rlAYearOlder:
				if (llAYearOlder.getVisibility() == View.GONE)
				{
					ivArrowIconAYearOlder.setRotation(90);
					llAYearOlder.setVisibility(View.VISIBLE);
					lvAYearOlder.setAdapter(similarAdapter);
				}
				else
				{
					llAYearOlder.setVisibility(View.GONE);
					ivArrowIconAYearOlder.setRotation(0);
				}
				break;

			default:
				break;
		}

	}
	
	private void getSimilarVehicles()
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("variantID", vehicleDetails.getVariantID(),Integer.class));
			parameterList.add(new Parameter("year", vehicleDetails.getYear(),Integer.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("LoadSimilarVehiclesByID");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/LoadSimilarVehiclesByID");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			showProgressDialog();
			new WebServiceTask(getActivity(),inObj, false,new TaskListener() {
			
			@Override
			public void onTaskComplete(Object result) 
			{
				hideProgressDialog();
				try{
					arraylistAYearYounger = new ArrayList<SettingsUser>();
					for (int i = 0; i < 6; i++)
					{
						arraylistAYearYounger.add(new SettingsUser());
					}
					similarAdapter = new SimilarAdapter(getActivity(), R.layout.list_item_oem_space, arraylistAYearYounger);
					/*SoapObject outer= (SoapObject) result;
					SoapObject inner= (SoapObject) outer.getPropertySafely("Models");
					for(int i=0;i<inner.getPropertyCount();i++){
						SoapObject modelObj= (SoapObject) inner.getProperty(i);
						String modelid=modelObj.getPropertySafelyAsString("modelID", "0");
						String modelname= modelObj.getPropertySafelyAsString("modelName","-");
						
					//	modelList.add(i, new SmartObject(Integer.parseInt(modelid),modelname));
					}*/
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	


}
