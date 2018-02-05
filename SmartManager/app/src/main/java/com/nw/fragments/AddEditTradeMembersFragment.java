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
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.nw.interfaces.DialogListener;
import com.nw.model.Member;
import com.nw.model.SettingsUser;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomCheckBox;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.util.ArrayList;

public class AddEditTradeMembersFragment extends BaseFragement implements OnClickListener
{
	int memberid;
	private EditText edMembersList;
	ArrayList<Member> members;
	ArrayAdapter<Member> memmberAdapter;
	int selectedMemberId;
	CustomButton bnSave, bnRemove;
	CustomCheckBox chkBuy, chkSell, chkAccept, chkDecline, chkManager, chkAuditor;
	SettingsUser member_details = null;
	private Context context;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_add_edit_trade_members, container, false);
		setHasOptionsMenu(true);
		context = getActivity();
		initialise(view);

		if (getArguments() != null)
		{
			memberid = getArguments().getInt("Memberid");
			member_details = getArguments().getParcelable("member_details");
			if (member_details != null && memberid != 0)
			{
				putvalues();
			}
			if (memberid == 0)
			{
				getListMembersForClients();
				bnRemove.setVisibility(View.GONE);
			}
			else
			{
				bnRemove.setVisibility(View.VISIBLE);
			}
		}

		return view;
	}

	private void initialise(View view)
	{
		edMembersList = (EditText) view.findViewById(R.id.edMembersList);
		bnSave = (CustomButton) view.findViewById(R.id.bnSave);
		bnRemove = (CustomButton) view.findViewById(R.id.bnRemove);
		chkBuy = (CustomCheckBox) view.findViewById(R.id.chkBuy);
		chkSell = (CustomCheckBox) view.findViewById(R.id.chkSell);
		chkAccept = (CustomCheckBox) view.findViewById(R.id.chkAccept);
		chkDecline = (CustomCheckBox) view.findViewById(R.id.chkDecline);
		chkManager = (CustomCheckBox) view.findViewById(R.id.chkManager);
		chkAuditor = (CustomCheckBox) view.findViewById(R.id.chkAuditor);
		bnSave.setOnClickListener(this);
		bnRemove.setOnClickListener(this);
		edMembersList.setOnClickListener(this);

	}

	private void putvalues()
	{
		edMembersList.setText(member_details.getMemberName());
		chkAccept.setChecked(member_details.isTenderAccept());
		chkAuditor.setChecked(member_details.isTenderAuditor());
		chkBuy.setChecked(member_details.isTradeBuy());
		chkSell.setChecked(member_details.isTradeSell());
		chkManager.setChecked(member_details.isTenderManager());
		chkDecline.setChecked(member_details.isTenderDecline());
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (memberid == 0)
		{
			showActionBar("Add Trade Member");
		}
		else
		{
			showActionBar("Edit Trade Member");
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
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.edMembersList:
				if (memberid == 0)
				{
					showMemberPopup(v);
				}

				break;

			case R.id.bnSave:
				if (checkValidation() == true)
				{
					if (memberid == 0)
					{
						saveTradeMember(false);
					}
					else
					{
						saveTradeMember(true);
					}
				}

				break;
			case R.id.bnRemove:
				removeTradeMember();
				break;
		}
	}

	private void removeTradeMember()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			StringBuilder soapMessage = new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<RemoveTradeMember xmlns=\"" + Constants.TRADER_NAMESPACE + "\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
			soapMessage.append("<ClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</ClientID>");
			soapMessage.append("<tradeMemberID>" + member_details.getID() + "</tradeMemberID>");
			soapMessage.append("<memberID>" + member_details.getMemberID() + "</memberID>");
			soapMessage.append("</RemoveTradeMember>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");

			Helper.Log("removeTradeMember request:", soapMessage.toString());

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
					Helper.Log("RemoveTradeMember Response:", response);
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

			VollyCustomRequest request = new VollyCustomRequest(Constants.TRADER_WEBSERVICE_URL, soapMessage.toString(), Constants.TRADER_NAMESPACE + "/ITradeService/RemoveTradeMember", listener);

			try
			{
				request.init("getListMembersForClients");
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

	private void saveTradeMember(boolean save)
	{
		showProgressDialog();
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			StringBuilder soapMessage = new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<SaveTradeMember xmlns=\"" + Constants.TRADER_NAMESPACE + "\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
			soapMessage.append("<ClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</ClientID>");
			if (save)
			{
				soapMessage.append("<tradeMemberID>" + member_details.getID() + "</tradeMemberID>");
				soapMessage.append("<memberID>" + member_details.getMemberID() + "</memberID>");
			}
			else
			{
				soapMessage.append("<tradeMemberID>" + 0 + "</tradeMemberID>");
				soapMessage.append("<memberID>" + selectedMemberId + "</memberID>");
			}
			soapMessage.append("<memberName>" + edMembersList.getText().toString().trim() + "</memberName>");
			if (chkBuy.isChecked())
			{
				soapMessage.append("<canBuy>" + 1 + "</canBuy>");
			}
			else
			{
				soapMessage.append("<canBuy>" + 0 + "</canBuy>");
			}
			if (chkSell.isChecked())
			{
				soapMessage.append("<canSell>" + 1 + "</canSell>");
			}
			else
			{
				soapMessage.append("<canSell>" + 0 + "</canSell>");
			}
			if (chkAccept.isChecked())
			{
				soapMessage.append("<canAccept>" + 1 + "</canAccept>");
			}
			else
			{
				soapMessage.append("<canAccept>" + 0 + "</canAccept>");
			}
			if (chkDecline.isChecked())
			{
				soapMessage.append("<canDecline>" + 1 + "</canDecline>");
			}
			else
			{
				soapMessage.append("<canDecline>" + 0 + "</canDecline>");
			}
			if (chkManager.isChecked())
			{
				soapMessage.append("<isManager>" + 1 + "</isManager>");
			}
			else
			{
				soapMessage.append("<isManager>" + 0 + "</isManager>");
			}
			if (chkAuditor.isChecked())
			{
				soapMessage.append("<isAuditor>" + 1 + "</isAuditor>");
			}
			else
			{
				soapMessage.append("<isAuditor>" + 0 + "</isAuditor>");
			}
			soapMessage.append("</SaveTradeMember>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");

			Helper.Log("SaveTradeMember request:", soapMessage.toString());

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

			VollyCustomRequest request = new VollyCustomRequest(Constants.TRADER_WEBSERVICE_URL, soapMessage.toString(), Constants.TRADER_NAMESPACE + "/ITradeService/SaveTradeMember", listener);

			try
			{
				request.init("getListMembersForClients");
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

	private void getListMembersForClients()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			StringBuilder soapMessage = new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<ListMembersForClientXML xmlns=\"" + Constants.TRADER_NAMESPACE + "\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
			soapMessage.append("<clientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</clientID>");
			soapMessage.append("</ListMembersForClientXML>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");

			Helper.Log("getListMembersForClients request:", soapMessage.toString());

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
					members = ParserManager.parseListMembersForClientXMLRespone(response);
					if (members != null)
					{
						memmberAdapter = new ArrayAdapter<Member>(getActivity(), R.layout.list_item_text2, members);
					}
					// hideProgressDialog();
					// showMemberPopup(edMembersList);
				}
			};

			VollyCustomRequest request = new VollyCustomRequest(Constants.TRADER_WEBSERVICE_URL, soapMessage.toString(), Constants.TRADER_NAMESPACE + "/ITradeService/ListMembersForClientXML",
					listener);

			try
			{
				request.init("getListMembersForClients");
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

	// don't use Helper.showDropDown() method
	private void showMemberPopup(View v)
	{
		if (memmberAdapter != null)
		{
			Helper.showDropDown(v, memmberAdapter, new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
				{
					edMembersList.setText(memmberAdapter.getItem(itemPosition).getName());
					selectedMemberId = memmberAdapter.getItem(itemPosition).getId();
					edMembersList.setTag("" + memmberAdapter.getItem(itemPosition).getId());
				}
			});
		}
		else
			Helper.showToast(getString(R.string.no_item_to_select), getActivity());
	}

	private boolean checkValidation()
	{
		if (edMembersList.getText().toString().trim().equalsIgnoreCase("") || edMembersList.getText().toString().trim().equalsIgnoreCase("Select"))
		{
			Helper.showToast("Please select members", context);
			return false;
		}

		return true;

	}
}
