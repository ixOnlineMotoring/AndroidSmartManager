package com.nw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.AverageDaysAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.AverageDays;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class AverageDaysFragment extends BaseFragement
{
	AverageDaysAdapter averageDaysAdapter;
	ArrayList<AverageDays> arrayList;
	VehicleDetails vehicleDetails;
	ListView lv_averagedays;
	TextView tvAvgUs,tvAvgCity,tvAvgNation,tvSampleNation,tvSampleCity,tvSampleUs,tvCityName,tv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_averagedays, container, false);
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
		tvAvgUs=(TextView)view.findViewById(R.id.tvAvgUs);
		tvAvgCity=(TextView)view.findViewById(R.id.tvAvgCity);
		tvAvgNation=(TextView)view.findViewById(R.id.tvAvgNation);
		tvSampleUs=(TextView)view.findViewById(R.id.tvSampleUs);
		tvSampleCity=(TextView)view.findViewById(R.id.tvSampleCity);
		tvSampleNation=(TextView)view.findViewById(R.id.tvSampleNation);
        tvCityName = (TextView)view.findViewById(R.id.tvCityName);
		lv_averagedays = (ListView) view.findViewById(R.id.lv_averagedays);
		arrayList = new ArrayList<AverageDays>();
		averageDaysAdapter = new AverageDaysAdapter(getActivity(), R.layout.list_item_average_days, arrayList);
		lv_averagedays.setAdapter(averageDaysAdapter);
		getAverageDaysData();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Average days");
	}

	private void getAverageDaysData(){
		if(HelperHttp.isNetworkAvailable(getActivity())){
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
		//	parameterList.add(new Parameter("modelID", 496,Integer.class));
			parameterList.add(new Parameter("modelID", vehicleDetails.getModelId(),Integer.class));
			//parameterList.add(new Parameter("clientID", 103,Integer.class));
			parameterList.add(new Parameter("clientID",  DataManager.getInstance().user.getDefaultClient().getId(),Integer.class));
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("LoadAverageDaysInStock");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/LoadAverageDaysInStock");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			showProgressDialog();
			new WebServiceTask(getActivity(),inObj, false,new TaskListener() {
			
			@Override
			public void onTaskComplete(Object result) 
			{

				try{
					SoapObject outer= (SoapObject) result;
					SoapObject resultObj= (SoapObject) outer.getPropertySafely("Result");
					SoapObject inner= (SoapObject) resultObj.getPropertySafely("AverageDaysInStock");
					int us=0,city=0,nation=0,sampleUs=0,sampleCity=0,sampleNation=0;;
					for(int i=0;i<inner.getPropertyCount();i++){
						SoapObject variantObj= (SoapObject) inner.getProperty(i);
						AverageDays averageDays=new AverageDays();
						averageDays.setVariantName(variantObj.getPropertySafelyAsString("VariantName"));
						averageDays.setCityAverageDays(Integer.parseInt(variantObj.getPropertySafelyAsString("CityAverageDays")));
						us=us+Integer.parseInt(variantObj.getPropertySafelyAsString("ClientAverageDays"));
						averageDays.setClientAverageDays(Integer.parseInt(variantObj.getPropertySafelyAsString("ClientAverageDays")));
						city=city+Integer.parseInt(variantObj.getPropertySafelyAsString("CityAverageDays"));
						averageDays.setNationalAverageDays(Integer.parseInt(variantObj.getPropertySafelyAsString("NationalAverageDays")));
						nation=nation+Integer.parseInt(variantObj.getPropertySafelyAsString("NationalAverageDays"));
						averageDays.setCityTotalStockMovements(Integer.parseInt(variantObj.getPropertySafelyAsString("CityTotalStockMovements")));
						averageDays.setNationalTotalStockMovements(Integer.parseInt(variantObj.getPropertySafelyAsString("NationalTotalStockMovements")));
						averageDays.setClientTotalStockMovements((Integer.parseInt(variantObj.getPropertySafelyAsString("ClientTotalStockMovements"))));
						sampleUs=sampleUs+(Integer.parseInt(variantObj.getPropertySafelyAsString("ClientTotalStockMovements")));
						sampleCity=sampleCity+(Integer.parseInt(variantObj.getPropertySafelyAsString("CityTotalStockMovements")));
						sampleNation=sampleNation+(Integer.parseInt(variantObj.getPropertySafelyAsString("NationalTotalStockMovements")));
						arrayList.add(averageDays);
					//	modelList.add(i, new SmartObject(Integer.parseInt(modelid),modelname));
					}
					if(inner.getPropertyCount()==0)
					{
						CustomDialogManager.showOkDialog(getActivity(), "No record(s) found.",new DialogListener() {

							@Override
							public void onButtonClicked(int type) {
								getFragmentManager().popBackStack();
							}
						});
                        return;
					}
                    SoapObject clientObj= (SoapObject) resultObj.getPropertySafely("ClientDetails");
                 //   tvGroupName.setText(clientObj.getPropertySafelyAsString("GroupName"));
                    tvCityName.setText(clientObj.getPropertySafelyAsString("CityName"));
					if(arrayList.size()>0)
					{
						tvAvgUs.setText(us/arrayList.size()+"");
						tvAvgCity.setText(city/arrayList.size()+"");
						tvAvgNation.setText(nation/arrayList.size()+"");
						tvSampleUs.setText(sampleUs+"");
						tvSampleCity.setText(sampleCity+"");
						tvSampleNation.setText(sampleNation+"");
					}
					averageDaysAdapter.notifyDataSetChanged();
                    hideProgressDialog();
				}catch(Exception e){
					e.printStackTrace();
                    hideProgressDialog();
                    CustomDialogManager.showOkDialog(getActivity(), "No record(s) found.",new DialogListener() {

                        @Override
                        public void onButtonClicked(int type) {
                            getFragmentManager().popBackStack();
                        }
                    });
				}
				
			}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
}