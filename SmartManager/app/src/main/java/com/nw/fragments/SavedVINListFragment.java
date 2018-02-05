package com.nw.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Model;
import com.nw.model.Parameter;
import com.nw.model.ScanVIN;
import com.nw.model.SmartObject;
import com.nw.model.Variant;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.ArrayList;

public class SavedVINListFragment extends BaseFragement implements OnItemClickListener
{
	ArrayList<ScanVIN>scanVINs;
	ArrayAdapter<ScanVIN> scanVINAdapter;
	ListView listView1;
	ScanVINDetailsSynopsis scanVINDetailsSynopsis;
	VINLookupFragment vinLookupFragment;
	boolean fromVehicleModule =false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		fromVehicleModule = getArguments().getBoolean("fromVehicleModule");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View view=inflater.inflate(R.layout.fragment_saved_vin, container,false);
		setHasOptionsMenu(true);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		listView1=(ListView) view.findViewById(R.id.listView1);
		listView1.setOnItemClickListener(this);
		getSavedVINeList();
		return view;
	}
	
	public void onResume() 
	{
		super.onResume();
		showActionBar("Saved VIN Scans");
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
	{
		checkScannedVINJson(scanVINAdapter.getItem(position), fromVehicleModule);
	}
	
	private void checkScannedVINJson(final ScanVIN scanVIN,final boolean fromVehicleModule)
	{
		if(HelperHttp.isNetworkAvailable(getActivity()))
 		{
			ArrayList<Parameter>parameters=new ArrayList<Parameter>();
			parameters.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameters.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameters.add(new Parameter("vin", scanVIN.getVIN(), String.class));
			parameters.add(new Parameter("registration", scanVIN.getRegistration(), String.class));
			parameters.add(new Parameter("make",scanVIN.getMake(), String.class));
			parameters.add(new Parameter("model", scanVIN.getModel(), String.class));
			
			DataInObject dataInObject=new DataInObject();
			dataInObject.setMethodname("CheckScannedVinJSON");
			dataInObject.setNamespace(Constants.TEMP_URI_NAMESPACE);
			dataInObject.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/CheckScannedVinJSON");
			dataInObject.setUrl(Constants.STOCK_WEBSERVICE_URL);
			dataInObject.setParameterList(parameters);
			new WebServiceTask(getActivity(), dataInObject, true, new TaskListener() 
			{
				@Override
				public void onTaskComplete(Object response) 
				{
					if(response!=null)
					{
						Helper.Log("checkScannedVINJson onTaskComplete ", ""+response.toString());
							if(response instanceof SoapFault)
							{
								// error
								showErrorDialog();
							}
							else
							{
								// not fault
								try 
								{
									String value=(String) ((SoapPrimitive)response).getValue();
									if(!TextUtils.isEmpty(value))
									{
										JSONObject jsonObject=new JSONObject(value);
										if(jsonObject.optString("status", "").equals("ok"))
										{
											if(jsonObject.optString("existing", "no").equals("yes"))
												scanVIN.setExisting(true);
											if(jsonObject.has("stock"))
											{
												JSONArray stockArray=jsonObject.getJSONArray("stock");
												for(int index=0;index<stockArray.length();index++)
												{
													JSONObject stockObject=stockArray.getJSONObject(index);
													SmartObject smartObject=new SmartObject(stockObject.optInt("id"),
													stockObject.optString("code"));
													scanVIN.getStocks().add(smartObject);
												}
											}
											scanVIN.setHasModel(jsonObject.optBoolean("hasModel", false));
											scanVIN.setModelId(jsonObject.optInt("ModelID", 0));
											scanVIN.setMakeId(jsonObject.optInt("makeID", 0));
											scanVIN.setMaxYear(jsonObject.optInt("maxYear", 0));
											scanVIN.setMinYear(jsonObject.optInt("minYear", 0));
											
											if(jsonObject.has("models"))
											{
												JSONArray modelArray=jsonObject.getJSONArray("models");
												for(int index=0;index<modelArray.length();index++)
												{
													JSONObject modelObject=modelArray.getJSONObject(index);
													Model smartObject=new Model(modelObject.optInt("modelID"),modelObject.optString("modelName"),modelObject.optInt("minYear"),modelObject.optInt("maxYear"));
													scanVIN.getModels().add(smartObject);
												}
											}
											if(jsonObject.has("variants"))
											{
												JSONArray variantsArray=jsonObject.optJSONArray("variants");
												for(int index=0;index<variantsArray.length();index++)
												{
													Variant variant=new Variant();
													JSONObject variantObject=variantsArray.getJSONObject(index);
													variant.setVariantId(variantObject.optInt("id"));
													variant.setVariantName(variantObject.optString("name"));
													variant.setFriendlyName(variantObject.optString("friendly"));
													variant.setMeadCode(variantObject.optString("code"));
													variant.setMinYear(variantObject.optString("minYear"));
													variant.setMaxYear(variantObject.optString("maxYear"));
													scanVIN.getVariants().add(variant);
												}
											}
											// got to next screen
											if (fromVehicleModule)
											{
												vinLookupFragment = new VINLookupFragment();
												Bundle bundle = new Bundle();
												bundle.putParcelable("data",scanVIN);
												bundle.putBoolean("fromScan",false);
												vinLookupFragment.setArguments(bundle);
												getFragmentManager().beginTransaction().replace(R.id.Container,vinLookupFragment).addToBackStack(null).commit();
											} else
											{
												scanVINDetailsSynopsis=new ScanVINDetailsSynopsis();
												Bundle bundle=new Bundle();
												bundle.putParcelable("scannedVIN", scanVIN);
												bundle.putBoolean("fromScan", true);
												scanVINDetailsSynopsis.setArguments(bundle);
												getFragmentManager().beginTransaction().replace(R.id.Container,scanVINDetailsSynopsis).addToBackStack(null).commit();
											}
										}
									}
								}
								catch(Exception e)
								{
									e.printStackTrace();
									showErrorDialog();
								}
							}
					}
				else
					{
						showErrorDialog();
					}
				}
			}).execute();
 		}
		else
			HelperHttp.showNoInternetDialog(getActivity());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (vinLookupFragment != null)
			vinLookupFragment.onActivityResult(requestCode, resultCode, data);
		if (scanVINDetailsSynopsis != null)
			scanVINDetailsSynopsis.onActivityResult(requestCode, resultCode, data);
	}

	private void getSavedVINeList()
	{
		if(scanVINs==null)
			scanVINs=new ArrayList<ScanVIN>();
		else
			scanVINs.clear();
		if(HelperHttp.isNetworkAvailable(getActivity()))
 		{
		ArrayList<Parameter>parameters=new ArrayList<Parameter>();
		parameters.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
		parameters.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
		DataInObject dataInObject=new DataInObject();
		dataInObject.setMethodname("ListSavedVINScansXML");
		dataInObject.setNamespace(Constants.TEMP_URI_NAMESPACE);
		dataInObject.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/ListSavedVINScansXML");
		dataInObject.setUrl(Constants.STOCK_WEBSERVICE_URL);
		dataInObject.setParameterList(parameters);
		new WebServiceTask(getActivity(), dataInObject, true, new TaskListener() 
		{
			@Override
			public void onTaskComplete(Object response) 
			{
				if(response!=null)
				{
					Helper.Log("getSavedVINeList onTaskComplete ", ""+response.toString());
					
						if(response instanceof SoapFault)
						{
							// error
							showErrorDialog();
						}
						else
						{
							// not fault
							try 
							{
								if(((SoapObject)response).getPropertyCount()>0)
								{
									SoapObject SoapvehicleTypes=(SoapObject) ((SoapObject)response).getProperty(0);
									for(int index=0;index<SoapvehicleTypes.getPropertyCount();index++)
									{
										SoapObject SoapVehicle=(SoapObject) SoapvehicleTypes.getProperty(index);
										ScanVIN scanVIN=new ScanVIN();
										scanVIN.setId(Integer.parseInt(SoapVehicle.getPropertySafelyAsString("savedScanID", 0)));
										scanVIN.setDate(SoapVehicle.getPropertySafelyAsString("date", ""));
										scanVIN.setVIN(SoapVehicle.getPropertySafelyAsString("VIN", ""));
										scanVIN.setRegistration(SoapVehicle.getPropertySafelyAsString("registration", ""));
										scanVIN.setShape(SoapVehicle.getPropertySafelyAsString("shape", ""));
										scanVIN.setMake(SoapVehicle.getPropertySafelyAsString("make", ""));
										scanVIN.setModel(SoapVehicle.getPropertySafelyAsString("model", ""));
										scanVIN.setColour(SoapVehicle.getPropertySafelyAsString("colour", ""));
										scanVIN.setEngineNumber(SoapVehicle.getPropertySafelyAsString("engineNo", ""));
										scanVIN.setMilage(SoapVehicle.getPropertySafelyAsString("kilometers", ""));
										scanVIN.setExtras(SoapVehicle.getPropertySafelyAsString("extras", ""));
										scanVIN.setYear(SoapVehicle.getPropertySafelyAsString("Year", ""));
										scanVIN.setCondition(SoapVehicle.getPropertySafelyAsString("condition", ""));
										scanVIN.setVariantstr(SoapVehicle.getPropertySafelyAsString("variant", ""));
										scanVIN.setVariantID(Integer.parseInt(SoapVehicle.getPropertySafelyAsString("variantID", "0")));
										scanVIN.setExpiry_Date(SoapVehicle.getPropertySafelyAsString("LicenseExpiry", ""));
										scanVINs.add(scanVIN);
									}
									if (scanVINs != null && !scanVINs.isEmpty()) 
									{
										scanVINAdapter = new ArrayAdapter<ScanVIN>(	getActivity(), R.layout.list_item_scan_vin_new,scanVINs)
											{
												public View getView(int position, View convertView, ViewGroup parent) 
												{
													if(convertView==null)
														convertView=getActivity().getLayoutInflater().inflate(R.layout.list_item_scan_vin_new, parent,false);
														
														TextView tvTitle1=(TextView) convertView.findViewById(R.id.textView1);
														//check for null values
														//--------------------------------------------------
														if(getItem(position).getMake()!=null)
															tvTitle1.setText(""+getItem(position).getMake());
														else
															tvTitle1.setText("Make?");
														if(getItem(position).getModel()!=null)
															tvTitle1.append(" "+getItem(position).getModel());
														else
															tvTitle1.append(" Model?");
														if(getItem(position).getColour()!=null)
															tvTitle1.append(" | "+getItem(position).getColour());
														else
															tvTitle1.append(" | Color? | ");
														if(getItem(position).getYear()!=null)
															tvTitle1.append(" | "+getItem(position).getYear());
														else
															tvTitle1.append(" | "+"Year?");
														if(getItem(position).getVIN()!=null)
															tvTitle1.append(" | VIN:"+getItem(position).getVIN());
														else
															tvTitle1.append(" | VIN? | ");
														if(getItem(position).getDate()!=null)
															tvTitle1.append(" | "+getItem(position).getDate());
														else
															tvTitle1.append(" | Date?");

														return convertView;
													};
												};
										showActionBar("Saved VIN Scans");
										listView1.setAdapter(scanVINAdapter);
										
									}else
									{
										noRecordDialog();
									}

								}else
								{
									noRecordDialog();
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
								noRecordDialog();
							}
						}
				}
			else
				{ 
					noRecordDialog();
				}
			}
		}).execute();
 		}
		else
			HelperHttp.showNoInternetDialog(getActivity());
	}
	
	private void noRecordDialog(){
		CustomDialogManager.showOkDialog(getActivity(),"No record(s) found", new DialogListener()
		{
			@Override
			public void onButtonClicked(int type)
			{
				getFragmentManager().popBackStack();
			}
		});
	}
	
}
