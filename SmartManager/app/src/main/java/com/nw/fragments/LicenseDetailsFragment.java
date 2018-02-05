package com.nw.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.nw.interfaces.DialogListener;
import com.nw.model.Person;
import com.nw.model.VehicleClass;
import com.nw.webservice.DataManager;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomTextViewLight;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

	public class LicenseDetailsFragment extends BaseFragement 
{
	TextView tvName, tvLicenseId, tvDob, tvAge, tvGender, tvRestrictions, tvCertificateId;
	EditText edPhone, edEmail;
	Button btnSave;
	ImageView ivScanLicense;
	ImageLoader loader;
	Person person_details;
	LinearLayout llClassess;
	int type=0;
	int ON_ITEM_CLICKED =1,ON_SCAN_BUTTON_CLICK=2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view=inflater.inflate(R.layout.fragment_license_details, container,false);
		if (getArguments()!= null) 
		{
			type = getArguments().getInt("FromFragment");
			person_details = getArguments().getParcelable("person_details");
		}
		setHasOptionsMenu(true);
		initialise(view);
		if (person_details != null)
		{
			putValues();
		}else {
			getActivity().getFragmentManager().popBackStack();
		}
		
		return view;
	}
	
	private void initialise(View view)
	{
		tvName= (TextView) view.findViewById(R.id.tvName);
		tvLicenseId= (TextView) view.findViewById(R.id.tvLicenseId);
		tvDob= (TextView) view.findViewById(R.id.tvDob);
		tvAge= (TextView) view.findViewById(R.id.tvAge);
		llClassess = (LinearLayout) view.findViewById(R.id.llClassess);
		tvGender= (TextView) view.findViewById(R.id.tvGender);
		tvRestrictions= (TextView) view.findViewById(R.id.tvRestrictions);
		tvCertificateId= (TextView) view.findViewById(R.id.tvCertificateId);
		edPhone= (EditText) view.findViewById(R.id.edPhone);
		edEmail= (EditText) view.findViewById(R.id.edEmail);
		btnSave= (Button) view.findViewById(R.id.btnSave);
		if (type==1)
		{
			btnSave.setText("Update");
		} else
		{
			btnSave.setText("Save");
		}
		ivScanLicense= (ImageView) view.findViewById(R.id.ivUserPhoto);
		btnSave.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if (edPhone.getText().toString().length()<9)
				{
					Helper.showToast("Please enter valid phone number", getActivity());
					return;
				}
				if (!Helper.validMail(edEmail.getText().toString().trim()))
				{
					Helper.showToast("Please enter valid email address", getActivity());
					return;
				}
				saveLicenseDetails();
			}
		});
		
		loader= VolleySingleton.getInstance().getImageLoader();
		///ivScanLicense.setImageUrl("http://mezzoblue.com/i/articles/16may06-raw.jpg", loader);
	}
	
	private void putValues()
	{
		tvName.setText(person_details.getInitials()+" "+ person_details.getSurname());
		tvLicenseId.setText(person_details.getIdentity_Number());
		tvAge.setText(Helper.getAge(person_details.getDateOfBirth()));
		tvGender.setText(person_details.getGender());
		tvDob.setText(person_details.getDateOfBirth());
		tvCertificateId.setText(person_details.getCertificateNumber());
		if (!person_details.getVehicleClass1().getCode().equals(""))
		{
			addLicenseClass(person_details.getVehicleClass1());
		}
		if (!person_details.getVehicleClass2().getCode().equals(""))
		{
			addLicenseClass(person_details.getVehicleClass2());
		}
		if (!person_details.getVehicleClass3().getCode().equals(""))
		{
			addLicenseClass(person_details.getVehicleClass3());
		}
		if (!person_details.getVehicleClass4().getCode().equals(""))
		{
			addLicenseClass(person_details.getVehicleClass4());
		}
		if (person_details.getDriverRestriction1().equals("0") && person_details.getDriverRestriction2().equals("0") )
		{
			tvRestrictions.setText("None");
		}else if (person_details.getDriverRestriction1().equals("1") && person_details.getDriverRestriction2().equals("1") ) {
			tvRestrictions.setText("Glasses / Contact Lens");
		}else if (person_details.getDriverRestriction1().equals("1") && person_details.getDriverRestriction2().equals("2") ) {
			tvRestrictions.setText("Glasses / Contact Lens,Artificial Limb");
		}else if (person_details.getDriverRestriction1().equals("2") && person_details.getDriverRestriction2().equals("1") ) {
			tvRestrictions.setText("Artificial Limb, Glasses / Contact Lens");
		}else if (person_details.getDriverRestriction1().equals("2") && person_details.getDriverRestriction2().equals("2") ) {
			tvRestrictions.setText("Artificial Limb");
		}else if (person_details.getDriverRestriction1().equals("0") && person_details.getDriverRestriction2().equals("2") ) {
			tvRestrictions.setText("Artificial Limb");
		}else if (person_details.getDriverRestriction1().equals("0") && person_details.getDriverRestriction2().equals("1") ) {
			tvRestrictions.setText("Glasses / Contact Lens");
		}else if (person_details.getDriverRestriction1().equals("0") && person_details.getDriverRestriction2().equals("2") ) {
			tvRestrictions.setText("Artificial Limb");
		}else if (person_details.getDriverRestriction1().equals("0") && person_details.getDriverRestriction2().equals("1") ) {
			tvRestrictions.setText("Glasses / Contact Lens");
		}else if (person_details.getDriverRestriction1().equals("2") && person_details.getDriverRestriction2().equals("0") ) {
			tvRestrictions.setText("Artificial Limb");
		}else if (person_details.getDriverRestriction1().equals("1") && person_details.getDriverRestriction2().equals("0") ) {
			tvRestrictions.setText("Glasses / Contact Lens");
		}
		String base64 = (person_details.getPhoto()).replace("\\.", "");
		byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ivScanLicense.setImageBitmap(decodedByte);
        if (type==ON_ITEM_CLICKED)
		{
			edEmail.setText(person_details.getEmail_id());
			edPhone.setText(person_details.getTelephone());
		}
	}
	
	private void addLicenseClass(VehicleClass vehicleClass)
	{
		View vehicleClassView = View.inflate(getActivity(), R.layout.vehicle_class_layout, null);
		CustomTextViewLight tvClassId = (CustomTextViewLight) vehicleClassView.findViewById(R.id.tvClassId);
		CustomTextViewLight tvClassName = (CustomTextViewLight) vehicleClassView.findViewById(R.id.tvClassName);
		CustomTextViewLight tvClassRestrictions = (CustomTextViewLight) vehicleClassView.findViewById(R.id.tvClassRestrictions);
		CustomTextViewLight tvClassIssueDate = (CustomTextViewLight) vehicleClassView.findViewById(R.id.tvClassIssueDate);
		ImageView ivClassImage = (ImageView)  vehicleClassView.findViewById(R.id.ivClassImage);
		tvClassId.setText(vehicleClass.getCode());
		switch (vehicleClass.getCode())
		{
		case "A":
			tvClassName.setText("Motorcycle");
			ivClassImage.setImageResource(R.drawable.class_a);
			break;

		case "A1":
			tvClassName.setText("Motorcycle");
			ivClassImage.setImageResource(R.drawable.class_a1);	
			break;
		
		case "B":
			tvClassName.setText("Vehicle & Trailer");
			ivClassImage.setImageResource(R.drawable.class_b);	
			break;
		
		case "C1":
			tvClassName.setText("Minibus / Light Truck");
			ivClassImage.setImageResource(R.drawable.class_c1);	
			break;
		
		case "C":
			tvClassName.setText("Bus / Truck");
			ivClassImage.setImageResource(R.drawable.class_c);	
			break;
		case "EB":
			tvClassName.setText("Vehicle & Trailer");
			ivClassImage.setImageResource(R.drawable.class_eb);	
			break;
		case "EC1":
			tvClassName.setText("Bus / Truck & Trailer");
			ivClassImage.setImageResource(R.drawable.class_ec1);	
			break;
		case "EC":
			tvClassName.setText("Bus / Truck & Trailer");
			ivClassImage.setImageResource(R.drawable.class_ec);	
			break;
		}
		if (vehicleClass.getVehicleRestriction().equals("0")){
			tvClassRestrictions.setText("None");
		}else if (person_details.getDriverRestriction1().equals("1") ) {
			tvClassRestrictions.setText("Automatic transmission");
		}else if (person_details.getDriverRestriction1().equals("2")) {
			tvClassRestrictions.setText("Electrically powered");
		}else if (person_details.getDriverRestriction1().equals("3")) {
			tvClassRestrictions.setText("Physically disabled");
		}else if (person_details.getDriverRestriction1().equals("4")) {
			tvClassRestrictions.setText("Bus > 16000 kg (GVM) permitted");
		}
		tvClassIssueDate.setText(vehicleClass.getFirstIssueDate());
		llClassess.addView(vehicleClassView);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				hideKeyboard();
		//		getFragmentManager().popBackStack();
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected void saveLicenseDetails()
	{
			if(HelperHttp.isNetworkAvailable(getActivity()))
			{
				showProgressDialog();
				StringBuilder soapMessage=new StringBuilder();
			//	soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
				soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
				soapMessage.append("<Body>");
				soapMessage.append("<UpdateLicense xmlns=\""+Constants.TEMP_URI_NAMESPACE+"\">");
				soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash()+ "</userHash>");
				soapMessage.append("<clientID>" + DataManager.getInstance().user.getDefaultClient().getId()+ "</clientID>");
				soapMessage.append("<ScanID>" + person_details.getScanID()+"</ScanID>");
				soapMessage.append("<EmailAddress>" +edEmail.getText().toString().trim()+ "</EmailAddress>");
				soapMessage.append("<Telephone>" +edPhone.getText().toString().trim()+ "</Telephone>");
				soapMessage.append("</UpdateLicense>");
				soapMessage.append("</Body>");
				soapMessage.append("</Envelope>");
				
				
				VollyResponseListener listener=new VollyResponseListener()
				{
					@Override
					public void onErrorResponse(VolleyError error)
					{
						hideProgressDialog();
						Helper.showToast(getString(R.string.error_getting_data),getActivity());
						VolleyLog.e("Error: ", error.toString());
					}
					
					@Override
					public void onResponse(String response)
					{
						hideProgressDialog();
						if(response==null)
						{
							return;
						}
						String message;
						Helper.Log("UpdateLicense", "" + response);
						if (response.contains("Success"))
						{
							message = "License information saved";
							
						}else {
							message = "Please try again later";
						}
						CustomDialogManager.showOkDialog(getActivity(),message , new DialogListener()
						{
							
							@Override
							public void onButtonClicked(int type)
							{
								getFragmentManager().popBackStack();
							}
						});
						
					}
				};
				
				VollyCustomRequest request=new VollyCustomRequest(Constants.LICENSE_WEBSERVICE_URL,soapMessage.toString(),
								Constants.TEMP_URI_NAMESPACE+"ILicense/UpdateLicense",listener);
				try
				{
					request.init();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
			else
			{
				CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
			}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Scan Info");
		//getActivity().getActionBar().setSubtitle(null);
	}

	public void onBackPressed()
	{
		if (type==ON_SCAN_BUTTON_CLICK)
		{
			CustomDialogManager.showOkCancelDialog(getActivity(),
					"You have not added any contact information do you want to continue?",new DialogListener()
							{

								@Override
								public void onButtonClicked(int type)
								{

									if (type == Dialog.BUTTON_POSITIVE)
									{
										getFragmentManager().popBackStack();
									}
								}
							});
		}else {
			getFragmentManager().popBackStack();
		}
	}
}
