package com.nw.fragments;

import android.os.Bundle;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.nw.adapters.MemberListSettingAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SettingsUser;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class TradeMembersFragment extends BaseFragement implements OnClickListener
{
	ArrayList<SettingsUser> settingsUsersList;
	ListView lvListMembers;
	MemberListSettingAdapter memberListSettingAdapter;
	Button btnAddMember;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_members, container, false);
		setHasOptionsMenu(true);

		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		lvListMembers = (ListView) view.findViewById(R.id.lvListMembers);
		btnAddMember = (Button) view.findViewById(R.id.btnAddMember);
		btnAddMember.setOnClickListener(this);

		lvListMembers.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				AddEditTradeMembersFragment addEditTradeMembersFragment = new AddEditTradeMembersFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("Memberid", settingsUsersList.get(position).getMemberID());
				bundle.putParcelable("member_details", settingsUsersList.get(position));
				showActionBar("Edit Trade Member");
				addEditTradeMembersFragment.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Container, addEditTradeMembersFragment).addToBackStack(null).commit();
			}
		});

		tradeMembersList();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Authorised Members");
		//getActivity().getActionBar().setSubtitle(null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getActivity().finish();
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
			case R.id.btnAddMember:
				
				AddEditTradeMembersFragment addEditTradeMembersFragment = new AddEditTradeMembersFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("Memberid", 0);
				addEditTradeMembersFragment.setArguments(bundle);
				getActivity().getActionBar().setTitle("Add Trade Member");
				getFragmentManager().beginTransaction().replace(R.id.Container, addEditTradeMembersFragment).addToBackStack(null).commit();
				break;

			default:
				break;
		}
	}

	private void tradeMembersList()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("ListTradeMembers");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ListTradeMembers");
			inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			// Network call
			new WebServiceTask(getActivity(), inObj, true, new TaskListener()
			{

				// Network callback
				@Override
				public void onTaskComplete(Object result)
				{
					try
					{
						Helper.Log("Response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("TradeMemberArray");
						SettingsUser settingsUser;
						settingsUsersList = new ArrayList<SettingsUser>();
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								
								settingsUser = new SettingsUser();
								settingsUser.setTradeBuy(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("TradeBuy")));
								settingsUser.setMemberID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("MemberID")));
								settingsUser.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("ID")));
								settingsUser.setTradeSell(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("TradeSell")));
								settingsUser.setTenderAccept(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("TenderAccept")));
								settingsUser.setTenderDecline(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("TenderDecline")));
								settingsUser.setTenderManager(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("TenderManager")));
								settingsUser.setTenderAuditor(Boolean.parseBoolean(vehicleObj.getPropertySafelyAsString("TenderAuditor")));
								settingsUser.setMemberName(vehicleObj.getPropertySafelyAsString("MemberName"));
								settingsUsersList.add(settingsUser);
							}
						}
						memberListSettingAdapter = new MemberListSettingAdapter(getActivity(), 0, settingsUsersList);
						lvListMembers.setAdapter(memberListSettingAdapter);
					} catch (Exception e)
					{
						e.printStackTrace();
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
