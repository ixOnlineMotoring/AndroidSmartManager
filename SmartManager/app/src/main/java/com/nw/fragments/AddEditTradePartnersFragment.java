package com.nw.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.adapters.ClientAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.Client;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SettingsUser;
import com.nw.model.SmartObject;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomCheckBox;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class AddEditTradePartnersFragment extends BaseFragement implements OnClickListener
{
	CustomEditText etPartnerType, etPartner, etTradePeriod;
	CustomButton bnSave, bnRemove;
	String[] partnertype = { "Dealer", "Group", "Make" };
	ArrayAdapter<String> partnerType;
	ArrayList<SmartObject> makeList, groupList, fromdaysList;
	int selectedMemberId, selectedMakeId, selectedGroupId, trader_id, selectedTradePeriod = -1;
	SettingsUser traderDetail = null;
	CustomCheckBox chkTrade, chkTender;
	Context context;
	ArrayList<Client> clients;
	ClientAdapter clientAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_add_edit_trade_partner, container, false);
		setHasOptionsMenu(true);
		context = getActivity();
		initialise(view);
		if (getArguments() != null)
		{
			trader_id = getArguments().getInt("trader_id", 0);
			traderDetail = getArguments().getParcelable("TraderDetail");
			if (traderDetail != null && trader_id != 0)
			{
				bnRemove.setVisibility(View.VISIBLE);
				putvalues();
			}
			else
			{
				bnRemove.setVisibility(View.GONE);
			}
		}
		if (makeList == null)
		{
			makeList = new ArrayList<SmartObject>();
		}
		if (groupList == null)
		{
			groupList = new ArrayList<SmartObject>();
		}
		if (fromdaysList == null)
		{
			fromdaysList = new ArrayList<SmartObject>();
		}
		return view;
	}

	private void initialise(View view)
	{
		chkTrade = (CustomCheckBox) view.findViewById(R.id.chkTrade);
		chkTender = (CustomCheckBox) view.findViewById(R.id.chkTender);
		etPartnerType = (CustomEditText) view.findViewById(R.id.etPartnerType);
		bnSave = (CustomButton) view.findViewById(R.id.bnSave);
		bnRemove = (CustomButton) view.findViewById(R.id.bnRemove);
		bnSave.setOnClickListener(this);
		bnRemove.setOnClickListener(this);
		etPartnerType.setOnClickListener(this);
		etPartner = (CustomEditText) view.findViewById(R.id.etPartner);
		etPartner.setOnClickListener(this);
		etTradePeriod = (CustomEditText) view.findViewById(R.id.etTradePeriod);
		etTradePeriod.setOnClickListener(this);
		partnerType = new ArrayAdapter<String>(getActivity(), R.layout.list_item_text2, partnertype);
	}

	private void putvalues()
	{
		etPartnerType.setText(traderDetail.getType());
		etPartner.setText(traderDetail.getMemberName());
		chkTender.setChecked(traderDetail.isTenderAccess());
		chkTrade.setChecked(traderDetail.isAuctionAccess());

		if (traderDetail.getTraderPeriod() == -1)
		{
			etTradePeriod.setText("No Access");
			selectedTradePeriod = traderDetail.getTraderPeriod();
		}
		else if (traderDetail.getTraderPeriod() == 0)
		{
			etTradePeriod.setText("Nil days");
			selectedTradePeriod = traderDetail.getTraderPeriod();
		}
		else if (traderDetail.getTraderPeriod() == 1)
		{
			etTradePeriod.setText(traderDetail.getTraderPeriod() + " day");
			selectedTradePeriod = traderDetail.getTraderPeriod();
		}
		else
		{
			etTradePeriod.setText("+" + traderDetail.getTraderPeriod() + " day");
			selectedTradePeriod = traderDetail.getTraderPeriod();
		}

		if (trader_id == 0)
		{
			bnRemove.setVisibility(View.GONE);
		}
		else
		{
			bnRemove.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{
			case R.id.etPartnerType:
				etPartner.setText("");
				showPartnertypePopup(v);

				break;

			case R.id.etPartner:
				if(etPartnerType.getText().toString().trim().equalsIgnoreCase(""))
				{
					Helper.showToast("Please select type", getActivity());
					return;
				}
				if (etPartnerType.getText().toString().equals(partnertype[0]))
				{
					if (clients != null)
					{
						showMemberPopup(v);
					}
					else
					{
						Helper.showToast("No client to select", getActivity());
					}
				}
				else if (etPartnerType.getText().toString().equals(partnertype[1]))
				{
					if (groupList.isEmpty())
					{
						getGroupList();
					}
					else
					{
						showGroupDropdown();
					}
				}
				else if (etPartnerType.getText().toString().equals(partnertype[2]))
				{
					if (makeList.isEmpty())
					{
						getMakeList();
					}
					else
					{
						showMakeDropdown();
					}
				}
				break;
			case R.id.etTradePeriod:
				if (fromdaysList.isEmpty())
				{
					// fromdays();
					getFromDaysOptions();
				}
				else
				{
					showFromDaysDropdown();
				}
				break;
			case R.id.bnSave:
				if (checkValidations() == true)
				{
					if (trader_id == 0)
					{
						SaveTradePartner(false);
					}
					else
					{
						SaveTradePartner(true);
					}
				}

				break;
			case R.id.bnRemove:
				removeTradePartner();
				break;
		}

	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (trader_id == 0)
		{
			showActionBar("Add Trade Partner");
		}
		else
		{
			showActionBar("Edit Trade Partner");
		}
		//getActivity().getActionBar().setSubtitle(null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getActivity().getFragmentManager().popBackStack();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		getClients();
	}

	private void showPartnertypePopup(View v)
	{
		Helper.showDropDown(v, partnerType, new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
			{
				etPartnerType.setText(partnerType.getItem(itemPosition));
				etPartnerType.setTag("" + partnerType.getItem(itemPosition));
			}
		});
	}

	private void showMemberPopup(View v)
	{
		if (clientAdapter != null)
		{

			Helper.showDropDown(v, clientAdapter, new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
				{
					etPartner.setText(clientAdapter.getItem(itemPosition).getName());
					selectedMemberId = clientAdapter.getItem(itemPosition).getId();
					if (traderDetail != null)
					{
						traderDetail.setID(selectedMemberId);
						traderDetail.setTypeID(1);
					}
					etPartner.setTag("" + clientAdapter.getItem(itemPosition).getId());
				}
			});
		}
		else
			Helper.showToast(getString(R.string.no_item_to_select), getActivity());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void showMakeDropdown()
	{
		Helper.showDropDown(etPartner, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, makeList), new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				etPartner.setText(makeList.get(position).toString());
				selectedMakeId = makeList.get(position).getId();
				if (traderDetail != null)
				{
					traderDetail.setID(selectedMakeId);
					traderDetail.setTypeID(2);
				}
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void showGroupDropdown()
	{
		Helper.showDropDown(etPartner, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, groupList), new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				etPartner.setText(groupList.get(position).toString());
				selectedGroupId = groupList.get(position).getId();
				if (traderDetail != null)
				{
					traderDetail.setID(selectedGroupId);
					traderDetail.setTypeID(3);
				}
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void showFromDaysDropdown()
	{
		Helper.showDropDown(etTradePeriod, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, fromdaysList), new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				etTradePeriod.setText(fromdaysList.get(position).toString());
				selectedTradePeriod = fromdaysList.get(position).getId();
				if (selectedTradePeriod == -1)
				{
					chkTrade.setChecked(false);
				}
				else
				{
					chkTrade.setChecked(true);
				}
				if (traderDetail != null)
				{
					traderDetail.setTraderPeriod(selectedTradePeriod);
				}
			}
		});
	}

	private void getMakeList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("fromYear", 0, Integer.class));
			parameterList.add(new Parameter("toYear", 0, Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListMakesXML");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ListMakesXML");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{

				// Network callback

				@Override
				public void onTaskComplete(Object result)
				{
					makeList.clear();
					try
					{
						Helper.Log("response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Makes");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject makeObj = (SoapObject) inner.getProperty(i);
							String makeid = makeObj.getPropertySafelyAsString("id", "0");
							String makename = makeObj.getPropertySafelyAsString("name", "-");
							makeList.add(i, new SmartObject(Integer.parseInt(makeid), makename));
						}

						if (!makeList.isEmpty())
						{
							showMakeDropdown();
						}

					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						hideProgressDialog();
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private void getGroupList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetClientCorprateGroups");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IAuthenticate/GetClientCorprateGroups");
			inObj.setUrl(Constants.WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, false, new TaskListener()
			{

				// Network callback

				@Override
				public void onTaskComplete(Object result)
				{
					groupList.clear();
					try
					{
						Helper.Log("GroupList response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Results");
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							SoapObject groupObj = (SoapObject) inner.getProperty(i);
							String groupid = groupObj.getPropertySafelyAsString("ID", "0");
							String groupname = groupObj.getPropertySafelyAsString("Name", "-");
							groupList.add(i, new SmartObject(Integer.parseInt(groupid), groupname));
						}

						if (!groupList.isEmpty())
						{
							showGroupDropdown();
						}

					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						hideProgressDialog();
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private void getClients()
	{
		if (clients != null)
			clients.clear();
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			final StringBuilder soapBuilder = new StringBuilder();
			soapBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\"><Body><ListAvailableClientsXML xmlns=\""
					+ Constants.TEMP_URI_NAMESPACE + "\">");
			soapBuilder.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
			soapBuilder.append("</ListAvailableClientsXML></Body></Envelope>");

			Helper.Log("getClients request:", soapBuilder.toString());

			VollyResponseListener listener = new VollyResponseListener()
			{

				@Override
				public void onErrorResponse(VolleyError error)
				{
					VolleyLog.e("Error: ", "" + error.getMessage());
					// getPlannerType();
				}

				@Override
				public void onResponse(String response)
				{
					if (response == null)
					{
						return;
					}
					Helper.Log("Response:", response);
					clients = ParserManager.parseClientList(response);
					if (getActivity() != null)
					{
						if (clients != null)
							clientAdapter = new ClientAdapter(getActivity(), R.layout.list_item_text2, clients);
						else
							clientAdapter = new ClientAdapter(getActivity(), R.layout.list_item_text2, new ArrayList<Client>());
					}

				}
			};

			VollyCustomRequest request = new VollyCustomRequest(Constants.PLANNER_WEBSERVICE_URL, soapBuilder.toString(), Constants.TEMP_URI_NAMESPACE + "IPlannerService/ListAvailableClientsXML",
					listener);
			try
			{
				request.init("getClients");
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	/*
	 * private void getListMembersForClients() { if
	 * (HelperHttp.isNetworkAvailable(getActivity())) { showProgressDialog();
	 * StringBuilder soapMessage = new StringBuilder();
	 * soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	 * soapMessage
	 * .append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">"
	 * ); soapMessage.append("<Body>");
	 * soapMessage.append("<ListMembersForClientXML xmlns=\"" +
	 * Constants.TEMP_URI_NAMESPACE + "\">"); soapMessage.append("<userHash>" +
	 * DataManager.getInstance().user.getUserHash() + "</userHash>");
	 * soapMessage.append("</ListMembersForClientXML>");
	 * soapMessage.append("</Body>"); soapMessage.append("</Envelope>");
	 * 
	 * Helper.Log("getListMembersForClients request:", soapMessage.toString());
	 * 
	 * VollyResponseListener listener = new VollyResponseListener() {
	 * 
	 * @Override public void onErrorResponse(VolleyError error) {
	 * hideProgressDialog(); Helper.Log("Error: ", error.getMessage()); }
	 * 
	 * @Override public void onResponse(String response) { hideProgressDialog();
	 * if (response == null) { return; } Helper.Log("Response:", response);
	 * members = ParserManager.parseListMembersForClientXMLRespone(response); if
	 * (members != null) { memmberAdapter = new
	 * ArrayAdapter<Member>(getActivity(), R.layout.list_item_text2, members); }
	 * hideProgressDialog(); showMemberPopup(etPartner); } };
	 * 
	 * VollyCustomRequest request = new
	 * VollyCustomRequest(Constants.PLANNER_WEBSERVICE_URL,
	 * soapMessage.toString(), Constants.TEMP_URI_NAMESPACE +
	 * "IPlannerService/ListMembersForClientXML", listener);
	 * 
	 * try { request.init("getListMembersForClients"); } catch (Exception e1) {
	 * e1.printStackTrace(); }
	 * 
	 * } else { CustomDialogManager.showOkDialog(getActivity(),
	 * getString(R.string.no_internet_connection)); } }
	 */

	private void getFromDaysOptions()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			StringBuilder soapMessage = new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<GetFromDays xmlns=\"" + Constants.TRADER_NAMESPACE + "\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
			soapMessage.append("</GetFromDays>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");

			Helper.Log("GetFromDays request:", soapMessage.toString());

			VollyResponseListener listener = new VollyResponseListener()
			{

				@Override
				public void onErrorResponse(VolleyError error)
				{
					hideProgressDialog();
					Helper.Log("GetFromDays Error: ", error.getMessage());
				}

				@Override
				public void onResponse(String response)
				{
					hideProgressDialog();
					if (response == null)
					{
						return;
					}
					Helper.Log("GetFromDays Response:", response);
					fromdaysList = ParserManager.parseFromdaysResponse(response);
					hideProgressDialog();
					showFromDaysDropdown();
				}
			};

			VollyCustomRequest request = new VollyCustomRequest(Constants.TRADER_WEBSERVICE_URL, soapMessage.toString(), Constants.TRADER_NAMESPACE + "/ITradeService/GetFromDays", listener);

			try
			{
				request.init("GetFromDays");
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private void SaveTradePartner(boolean save)
	{
		showProgressDialog();
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			StringBuilder soapMessage = new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<SaveTradePartner xmlns=\"" + Constants.TRADER_NAMESPACE + "\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
			soapMessage.append("<ClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</ClientID>");
			if (save)
			{
				soapMessage.append("<settingID>" + traderDetail.getSettingID() + "</settingID>");
				soapMessage.append("<traderPeriod>" + traderDetail.getTraderPeriod() + "</traderPeriod>");
			}
			else
			{
				soapMessage.append("<settingID>" + 0 + "</settingID>");
				soapMessage.append("<traderPeriod>" + selectedTradePeriod + "</traderPeriod>");
			}

			if (chkTrade.isChecked())
			{
				soapMessage.append("<auctionAccess>" + 1 + "</auctionAccess>");
			}
			else
			{
				soapMessage.append("<auctionAccess>" + 0 + "</auctionAccess>");
			}
			if (chkTender.isChecked())
			{
				soapMessage.append("<tenderAccess>" + 1 + "</tenderAccess>");
			}
			else
			{
				soapMessage.append("<tenderAccess>" + 0 + "</tenderAccess>");
			}
			if (save)
			{
				soapMessage.append("<id>" + traderDetail.getID() + "</id>");
				soapMessage.append("<name>" + etPartner.getText().toString().trim() + "</name>");
				soapMessage.append("<type>" + traderDetail.getType() + "</type>");
				soapMessage.append("<typeID>" + traderDetail.getTypeID() + "</typeID>");
			}
			else
			{
				if (etPartnerType.getText().toString().trim().equalsIgnoreCase(partnertype[0]))
				{
					soapMessage.append("<id>" + selectedMemberId + "</id>");
				}
				else if (etPartnerType.getText().toString().trim().equalsIgnoreCase(partnertype[1]))
				{
					soapMessage.append("<id>" + selectedGroupId + "</id>");
				}
				else if (etPartnerType.getText().toString().trim().equalsIgnoreCase(partnertype[2]))
				{
					soapMessage.append("<id>" + selectedMakeId + "</id>");
				}
				soapMessage.append("<name>" + etPartner.getText().toString().trim() + "</name>");
				soapMessage.append("<type>" + etPartnerType.getText().toString().trim() + "</type>");
				if (etPartnerType.getText().toString().trim().equalsIgnoreCase(partnertype[0]))
				{ 
					soapMessage.append("<typeID>" + 1 + "</typeID>");
				}
				else if (etPartnerType.getText().toString().trim().equalsIgnoreCase(partnertype[1]))
				{
					soapMessage.append("<typeID>" + 2 + "</typeID>");
				}
				else if (etPartnerType.getText().toString().trim().equalsIgnoreCase(partnertype[2]))
				{
					soapMessage.append("<typeID>" + 3 + "</typeID>");
				}
			}
			soapMessage.append("</SaveTradePartner>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");

			Helper.Log("SaveTradePartner request:", soapMessage.toString());

			VollyResponseListener listener = new VollyResponseListener()
			{

				@Override
				public void onErrorResponse(VolleyError error)
				{
					hideProgressDialog();
					Helper.Log("Error: ", error.getMessage());
				}

				@Override
				public void onResponse(String response)
				{
					hideProgressDialog();
					if (response == null)
					{
						return;
					}
					Helper.Log("Response:", response);
					hideProgressDialog();
					String msg = ParserManager.parseMessageChecker(response);
					CustomDialogManager.showOkDialog(getActivity(), msg, new DialogListener()
					{
						@Override
						public void onButtonClicked(int type)
						{
							getActivity().getFragmentManager().popBackStack();
						}
					});
				}
			};

			VollyCustomRequest request = new VollyCustomRequest(Constants.TRADER_WEBSERVICE_URL, soapMessage.toString(), Constants.TRADER_NAMESPACE + "/ITradeService/SaveTradePartner", listener);

			try
			{
				request.init("SaveTradePartner");
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private void removeTradePartner()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			StringBuilder soapMessage = new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<RemoveTradePartner xmlns=\"" + Constants.TRADER_NAMESPACE + "\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
			soapMessage.append("<ClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</ClientID>");
			soapMessage.append("<settingID>" + traderDetail.getSettingID() + "</settingID>");
			soapMessage.append("<traderPeriod>" + traderDetail.getTraderPeriod() + "</traderPeriod>");

			if (chkTrade.isChecked())
			{
				soapMessage.append("<auctionAccess>" + 1 + "</auctionAccess>");
			}
			else
			{
				soapMessage.append("<auctionAccess>" + 0 + "</auctionAccess>");
			}
			if (chkTender.isChecked())
			{
				soapMessage.append("<tenderAccess>" + 1 + "</tenderAccess>");
			}
			else
			{
				soapMessage.append("<tenderAccess>" + 0 + "</tenderAccess>");
			}
			soapMessage.append("<id>" + traderDetail.getID() + "</id>");
			soapMessage.append("<name>" + etPartner.getText().toString().trim() + "</name>");
			soapMessage.append("<type>" + traderDetail.getType() + "</type>");
			soapMessage.append("<typeID>" + traderDetail.getTypeID() + "</typeID>");
			soapMessage.append("</RemoveTradePartner>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");

			Helper.Log("RemoveTradePartner request:", soapMessage.toString());

			VollyResponseListener listener = new VollyResponseListener()
			{

				@Override
				public void onErrorResponse(VolleyError error)
				{
					hideProgressDialog();
					Helper.Log("Error: ", error.getMessage());
				}

				@Override
				public void onResponse(String response)
				{
					hideProgressDialog();
					if (response == null)
					{
						return;
					}
					Helper.Log("RemoveTradePartner Response:", response);
					String msg = ParserManager.parseMessageChecker(response);
					hideProgressDialog();
					CustomDialogManager.showOkDialog(getActivity(), msg, new DialogListener()
					{
						@Override
						public void onButtonClicked(int type)
						{
							getActivity().getFragmentManager().popBackStack();
						}
					});
				}
			};

			VollyCustomRequest request = new VollyCustomRequest(Constants.TRADER_WEBSERVICE_URL, soapMessage.toString(), Constants.TRADER_NAMESPACE + "/ITradeService/RemoveTradePartner", listener);

			try
			{
				request.init("RemoveTradePartner");
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	public boolean checkValidations()
	{
		if (etPartnerType.getText().toString().trim().equalsIgnoreCase("") || etPartnerType.getText().toString().trim().equalsIgnoreCase("Select"))
		{
			Helper.showToast("Please select type", context);
			return false;
		}

		if (etPartner.getText().toString().trim().equalsIgnoreCase("") || etPartner.getText().toString().trim().equalsIgnoreCase("Select"))
		{
			Helper.showToast("Please select partner", context);
			return false;
		}
		
		
		if (selectedTradePeriod != -1)
		{
			if (chkTrade.isChecked() == false)
			{
				Helper.showToast("Please mark Access to my trade vehicles check", context);
				return false;
			}
		}
		
		if (chkTrade.isChecked())
		{
			if (etTradePeriod.getText().toString().trim().equalsIgnoreCase("") || etTradePeriod.getText().toString().trim().equalsIgnoreCase("Select") || selectedTradePeriod == -1)
			{
				Helper.showToast("Please select from day", context);
				return false;
			}
		}
		else
		{
			selectedTradePeriod = -1;
		}

		return true;
	}

}
